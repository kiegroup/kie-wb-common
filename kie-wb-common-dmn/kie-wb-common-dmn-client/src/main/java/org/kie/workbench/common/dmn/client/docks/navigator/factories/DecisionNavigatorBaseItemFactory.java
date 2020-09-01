/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.docks.navigator.factories;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItemBuilder;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasFocusedShapeEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.uberfire.mvp.Command;

import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DecisionNavigatorBaseItemFactory_NoName;

@Dependent
public class DecisionNavigatorBaseItemFactory {

    private final DecisionNavigatorNestedItemFactory nestedItemFactory;

    private final DMNGraphUtils dmnGraphUtils;

    private final TextPropertyProviderFactory textPropertyProviderFactory;

    private final Event<CanvasFocusedShapeEvent> canvasFocusedSelectionEvent;

    private final Event<CanvasSelectionEvent> canvasSelectionEvent;

    private final DefinitionUtils definitionUtils;

    private final TranslationService translationService;

    @Inject
    public DecisionNavigatorBaseItemFactory(final DecisionNavigatorNestedItemFactory nestedItemFactory,
                                            final DMNGraphUtils dmnGraphUtils,
                                            final TextPropertyProviderFactory textPropertyProviderFactory,
                                            final Event<CanvasFocusedShapeEvent> canvasFocusedSelectionEvent,
                                            final Event<CanvasSelectionEvent> canvasSelectionEvent,
                                            final DefinitionUtils definitionUtils,
                                            final TranslationService translationService) {
        this.nestedItemFactory = nestedItemFactory;
        this.dmnGraphUtils = dmnGraphUtils;
        this.textPropertyProviderFactory = textPropertyProviderFactory;
        this.canvasFocusedSelectionEvent = canvasFocusedSelectionEvent;
        this.canvasSelectionEvent = canvasSelectionEvent;
        this.definitionUtils = definitionUtils;
        this.translationService = translationService;
    }

    public DecisionNavigatorItem makeItem(final Node<View, Edge> node,
                                          final DecisionNavigatorItem.Type type) {

        final String uuid = node.getUUID();
        final String label = getLabel(node);
        final Command onClick = makeOnClickCommand(node);
        final List<DecisionNavigatorItem> nestedItems = makeNestedItems(node);
        final DecisionNavigatorItem item = navigatorItemBuilder()
                .withUUID(uuid)
                .withLabel(label)
                .withType(type)
                .withOnClick(onClick)
                .build();

        nestedItems.forEach(item::addChild);

        return item;
    }

    Command makeOnClickCommand(final Node<View, Edge> node) {

        return () -> {

            final CanvasHandler canvasHandler = dmnGraphUtils.getCanvasHandler();
            final String uuid = node.getUUID();

            canvasSelectionEvent.fire(makeCanvasSelectionEvent(canvasHandler, uuid));
            canvasFocusedSelectionEvent.fire(makeCanvasFocusedShapeEvent(canvasHandler, uuid));
            if (canvasHandler != null && canvasHandler.getCanvas() != null) {
                canvasHandler.getCanvas().focus();
            }
        };
    }

    String getLabel(final Element<View> element) {

        final String name = getName(element);
        final String title = getTitle(element);

        if (isNil(name) && !isNil(title)) {
            return title;
        }

        return (name != null ? name : getDefaultName());
    }

    String getName(final Element<? extends Definition> element) {
        final TextPropertyProvider provider = textPropertyProviderFactory.getProvider(element);
        return provider.getText(element);
    }

    String getTitle(final Element<View> element) {

        final AdapterManager adapters = definitionUtils.getDefinitionManager().adapters();
        final DefinitionAdapter<Object> objectDefinitionAdapter = adapters.forDefinition();

        return objectDefinitionAdapter.getTitle(DefinitionUtils.getElementDefinition(element));
    }

    List<DecisionNavigatorItem> makeNestedItems(final Node<View, Edge> node) {
        final List<DecisionNavigatorItem> nestedItems = new ArrayList<>();
        if (hasNestedElement(node)) {
            nestedItems.add(nestedItemFactory.makeItem(node));
        }
        return nestedItems;
    }

    CanvasSelectionEvent makeCanvasSelectionEvent(final CanvasHandler canvas,
                                                  final String uuid) {
        return new CanvasSelectionEvent(canvas, uuid);
    }

    CanvasFocusedShapeEvent makeCanvasFocusedShapeEvent(final CanvasHandler canvas,
                                                        final String uuid) {
        return new CanvasFocusedShapeEvent(canvas, uuid);
    }

    private String getDefaultName() {
        return translationService.format(DecisionNavigatorBaseItemFactory_NoName);
    }

    private boolean hasNestedElement(final Node<View, Edge> node) {
        return nestedItemFactory.hasNestedElement(node);
    }

    private boolean isNil(final String s) {
        return s == null || s.trim().isEmpty();
    }

    private DecisionNavigatorItemBuilder navigatorItemBuilder() {
        return new DecisionNavigatorItemBuilder();
    }
}
