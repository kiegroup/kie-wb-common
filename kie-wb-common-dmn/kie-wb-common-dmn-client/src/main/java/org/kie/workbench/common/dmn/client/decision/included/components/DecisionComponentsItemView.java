/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.decision.included.components;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.MouseDownEvent;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.HTMLImageElement;
import elemental2.dom.HTMLParagraphElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNElement;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.DMNShapeSet;
import org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.event.BuildCanvasShapeEvent;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxy;
import org.kie.workbench.common.stunner.core.client.components.drag.DragProxyCallback;
import org.kie.workbench.common.stunner.core.client.components.glyph.ShapeGlyphDragHandler;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

@Dependent
@Templated
public class DecisionComponentsItemView implements DecisionComponentsItem.View {

    @DataField("icon")
    private final HTMLImageElement icon;

    @DataField("name")
    private final HTMLHeadingElement name;

    @DataField("decision-component-item")
    private final HTMLDivElement decisionComponentItem;

    @DataField("file")
    private final HTMLParagraphElement file;

    private DecisionComponentsItem presenter;

    private final DMNShapeSet dmnShapeSet;
    private final SessionManager sessionManager;
    private final ShapeGlyphDragHandler shapeGlyphDragHandler;
    private final Event<BuildCanvasShapeEvent> buildCanvasShapeEvent;
    private final ClientFactoryService clientFactoryServices;
    private String className;
    private String objectId;

    @Inject
    public DecisionComponentsItemView(final HTMLImageElement icon,
                                      final @Named("h5") HTMLHeadingElement name,
                                      final HTMLParagraphElement file,
                                      final DMNShapeSet dmnShapeSet,
                                      final SessionManager sessionManager,
                                      final ShapeGlyphDragHandler shapeGlyphDragHandler,
                                      final Event<BuildCanvasShapeEvent> buildCanvasShapeEvent,
                                      final HTMLDivElement decisionComponentItem,
                                      final ClientFactoryService clientFactoryServices) {
        this.icon = icon;
        this.name = name;
        this.file = file;
        this.dmnShapeSet = dmnShapeSet;
        this.sessionManager = sessionManager;
        this.shapeGlyphDragHandler = shapeGlyphDragHandler;
        this.buildCanvasShapeEvent = buildCanvasShapeEvent;
        this.decisionComponentItem = decisionComponentItem;
        this.clientFactoryServices = clientFactoryServices;
    }

    @Override
    public void init(final DecisionComponentsItem presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setIcon(final String iconURI) {
        icon.src = iconURI;
    }

    @Override
    public void setName(final String name) {
        this.name.textContent = name;
    }

    @Override
    public void setFile(final String file) {
        this.file.textContent = file;
    }

    @Override
    public void setClass(final String className) {
        this.className = className;
    }

    @Override
    public void setObjectId(final String id) {
        this.objectId = id;
    }

    @EventHandler("decision-component-item")
    public void decisionComponentItemMouseDown(final MouseDownEvent mouseDownEvent) {
        showDragProxy(mouseDownEvent.getX(),
                      mouseDownEvent.getY());
    }

    void showDragProxy(int x, int y) {

        final ShapeFactory factory = dmnShapeSet.getShapeFactory();
        final Glyph glyph = factory.getGlyph(className);

        DragProxy itemDragProxy = shapeGlyphDragHandler.show(new ShapeGlyphDragHandler.Item() {
                                                                 @Override
                                                                 public Glyph getShape() {
                                                                     return glyph;
                                                                 }

                                                                 @Override

                                                                 public int getWidth() {
                                                                     return 16;
                                                                 }

                                                                 @Override
                                                                 public int getHeight() {
                                                                     return 16;
                                                                 }
                                                             },
                                                             x,
                                                             y,
                                                             new DragProxyCallback() {
                                                                 @Override
                                                                 public void onStart(int x,
                                                                                     int y) {
                                                                 }

                                                                 @Override
                                                                 public void onMove(int x,
                                                                                    int y) {

                                                                 }

                                                                 @Override
                                                                 public void onComplete(int x,
                                                                                        int y) {

                                                                     final Object definition = clientFactoryServices.getClientFactoryManager().newDefinition(className);
                                                                     final Name nameObject = getName();
                                                                     ((HasName) definition).setName(nameObject);
                                                                     ((DMNElement) definition).getId().setValue(objectId);
                                                                     if (definition instanceof DynamicReadOnly) {
                                                                         ((DynamicReadOnly) definition).setAllowOnlyVisualChange(true);
                                                                     }

                                                                     buildCanvasShapeEvent.fire(new BuildCanvasShapeEvent((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                                                                                                          definition,
                                                                                                                          factory,
                                                                                                                          x,
                                                                                                                          y));
                                                                 }
                                                             });
    }

    private Name getName() {
        final String prefix = objectId.split(":")[0];
        return new Name(prefix + "." + name.textContent);
    }
}
