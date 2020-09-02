/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.session;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.model.NamedElement;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandlerImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementsClearEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.processing.index.MutableIndex;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleManager;

@DMNEditor
@Dependent
public class DMNCanvasHandler<D extends Diagram, C extends AbstractCanvas> extends CanvasHandlerImpl<D, C> {

    private final DMNGraphUtils dmnGraphUtils;

    @Inject
    public DMNCanvasHandler(final ClientDefinitionManager clientDefinitionManager,
                            final CanvasCommandFactory<AbstractCanvasHandler> commandFactory,
                            final RuleManager ruleManager,
                            final GraphUtils graphUtils,
                            final GraphIndexBuilder<? extends MutableIndex<Node, Edge>> indexBuilder,
                            final ShapeManager shapeManager,
                            final TextPropertyProviderFactory textPropertyProviderFactory,
                            final Event<CanvasElementAddedEvent> canvasElementAddedEvent,
                            final Event<CanvasElementRemovedEvent> canvasElementRemovedEvent,
                            final Event<CanvasElementUpdatedEvent> canvasElementUpdatedEvent,
                            final Event<CanvasElementsClearEvent> canvasElementsClearEvent,
                            final DMNGraphUtils dmnGraphUtils) {
        super(clientDefinitionManager, commandFactory, ruleManager, graphUtils, indexBuilder, shapeManager, textPropertyProviderFactory, canvasElementAddedEvent, canvasElementRemovedEvent, canvasElementUpdatedEvent, canvasElementsClearEvent);
        this.dmnGraphUtils = dmnGraphUtils;
    }

    @Override
    protected void beforeElementUpdated(final Element element,
                                        final Shape shape) {
        super.beforeElementUpdated(element, shape);

        final String updatedNodeId = getId(element);
        final String newName = getName(element);

        getNodeStream().forEach(node -> {
            if (Objects.equals(updatedNodeId, getId(node))) {
                final String oldName = getName(node);
                if (!Objects.equals(oldName, newName)) {
                    setName(node, newName);
                }
            }
        });
    }

    private void setName(final Node node,
                         final String newText) {
        getCurrentSession().ifPresent(session -> setNodeText(node, newText, session));
    }

    private String getId(final Object node) {
        return getNamedElement(node)
                .map(dmnElement -> dmnElement.getId().getValue())
                .orElse("");
    }

    private String getName(final Object node) {
        return getNamedElement(node)
                .map(namedElement -> namedElement.getName().getValue())
                .orElse("");
    }

    private Optional<NamedElement> getNamedElement(final Object node) {
        return Optional
                .ofNullable(node)
                .filter(obj -> obj instanceof NodeImpl)
                .map(obj -> (NodeImpl) obj)
                .map(NodeImpl::getContent)
                .filter(content -> content instanceof ViewImpl)
                .map(content -> (ViewImpl) content)
                .map(ViewImpl::getDefinition)
                .filter(content -> content instanceof NamedElement)
                .map(content -> (NamedElement) content);
    }

    @SuppressWarnings("unchecked")
    private void setNodeText(final Node node,
                             final String newText,
                             final DMNEditorSession session) {
        final TextPropertyProvider textPropertyProvider = getTextPropertyProviderFactory().getProvider(node);

        getCanvasHandler(session).ifPresent(canvasHandler -> {
            textPropertyProvider.setText(canvasHandler, session.getCommandManager(), node, newText);
        });
    }

    private Optional<AbstractCanvasHandler> getCanvasHandler(final DMNEditorSession session) {
        return Optional.ofNullable(session.getCanvasHandler());
    }

    private Stream<Node> getNodeStream() {
        return dmnGraphUtils.getNodeStream();
    }

    private Optional<DMNEditorSession> getCurrentSession() {
        return dmnGraphUtils.getCurrentSession().map(s -> (DMNEditorSession) s);
    }
}

