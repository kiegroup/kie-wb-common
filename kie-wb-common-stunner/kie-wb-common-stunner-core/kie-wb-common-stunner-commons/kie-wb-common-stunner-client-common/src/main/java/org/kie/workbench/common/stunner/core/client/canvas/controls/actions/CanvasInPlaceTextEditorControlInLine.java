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

package org.kie.workbench.common.stunner.core.client.canvas.controls.actions;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.components.views.FloatingView;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextDoubleClickEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextDoubleClickHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextEnterEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextEnterHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextExitEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextExitHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.graph.Element;

@Default
@Dependent
@InLineTextEditorBox
public class CanvasInPlaceTextEditorControlInLine extends AbstractCanvasInPlaceTextEditorControl {

    private final double defaultFontSize = 16;

    private final FloatingView<IsWidget> floatingView;
    private final TextEditorBox<AbstractCanvasHandler, Element> textEditorBox;
    private final Event<CanvasSelectionEvent> canvasSelectionEvent;

    @Inject
    public CanvasInPlaceTextEditorControlInLine(final FloatingView<IsWidget> floatingView,
                                                final @InLineTextEditorBox TextEditorBox<AbstractCanvasHandler, Element> textEditorBox,
                                                final Event<CanvasSelectionEvent> canvasSelectionEvent) {
        this.floatingView = floatingView;
        this.textEditorBox = textEditorBox;
        this.canvasSelectionEvent = canvasSelectionEvent;
    }

    @Override
    protected void doInit() {
        super.doInit();
        getTextEditorBox().initialize(canvasHandler,
                                      () -> {
                                          final String idToSelect = CanvasInPlaceTextEditorControlInLine.this.uuid;
                                          CanvasInPlaceTextEditorControlInLine.this.hide();
                                          getCanvasSelectionEvent().fire(new CanvasSelectionEvent(canvasHandler,
                                                                                                  idToSelect));
                                      });

        getFloatingView()
                .hide()
                .setHideCallback(hideFloatingViewOnTimeoutCommand)
                .setTimeOut(FLOATING_VIEW_TIMEOUT)
                .add(wrapTextEditorBoxElement(getTextEditorBox().getElement()));
    }

    @Override
    public void register(final Element element) {
        if (checkNotRegistered(element)) {
            final Shape<?> shape = getShape(element.getUUID());
            if (null != shape) {
                final ShapeView shapeView = shape.getShapeView();
                if (shapeView instanceof HasEventHandlers) {
                    final HasEventHandlers hasEventHandlers = (HasEventHandlers) shapeView;
                    if (hasEventHandlers.supports(ViewEventType.TEXT_DBL_CLICK)) {
                        final TextDoubleClickHandler clickHandler = new TextDoubleClickHandler() {
                            @Override
                            public void handle(final TextDoubleClickEvent event) {
                                CanvasInPlaceTextEditorControlInLine.this.show(element,
                                                                               shapeView);
                            }
                        };
                        hasEventHandlers.addHandler(ViewEventType.TEXT_DBL_CLICK,
                                                    clickHandler);
                        registerHandler(shape.getUUID(),
                                        clickHandler);
                    }

                    // Change mouse cursor, if shape supports it.
                    if (hasEventHandlers.supports(ViewEventType.TEXT_ENTER)) {
                        final TextEnterHandler enterHandler = new TextEnterHandler() {
                            @Override
                            public void handle(TextEnterEvent event) {
                                canvasHandler.getAbstractCanvas().getView().setCursor(AbstractCanvas.Cursors.TEXT);
                            }
                        };
                        hasEventHandlers.addHandler(ViewEventType.TEXT_ENTER,
                                                    enterHandler);
                        registerHandler(shape.getUUID(),
                                        enterHandler);
                    }
                    if (hasEventHandlers.supports(ViewEventType.TEXT_EXIT)) {
                        final TextExitHandler exitHandler = new TextExitHandler() {
                            @Override
                            public void handle(TextExitEvent event) {
                                canvasHandler.getAbstractCanvas().getView().setCursor(AbstractCanvas.Cursors.DEFAULT);
                            }
                        };
                        hasEventHandlers.addHandler(ViewEventType.TEXT_EXIT,
                                                    exitHandler);
                        registerHandler(shape.getUUID(),
                                        exitHandler);
                    }
                }
            }
        }
    }

    public CanvasInPlaceTextEditorControl<AbstractCanvasHandler, EditorSession, Element> show(final Element item,
                                                                                              ShapeView shapeView) {
        if (getTextEditorBox().isVisible()) {
            flush();
        }

        this.uuid = item.getUUID();

        enableShapeEdit();
        getTextEditorBox().show(item);
        final double offsetX = getTextEditorBox().getDisplayOffsetX();
        final double offsetY = getTextEditorBox().getDisplayOffsetY();

        double scale = canvasHandler.getCanvas().getTransform().getScale().getX();

        getFloatingView()
                .setX(shapeView.getShapeX() * scale)
                .setY(shapeView.getShapeY() * scale)
                .setOffsetX(-offsetX)
                .setOffsetY(-offsetY)
                .show();

        getTextEditorBox().setWidth(shapeView.getBoundingBox().getWidth() * scale);
        getTextEditorBox().setHeight(shapeView.getBoundingBox().getHeight() * scale);
        getTextEditorBox().setFontSize(defaultFontSize * scale);
        return this;
    }

    @Override
    protected FloatingView<IsWidget> getFloatingView() {
        return floatingView;
    }

    @Override
    protected TextEditorBox<AbstractCanvasHandler, Element> getTextEditorBox() {
        return textEditorBox;
    }

    @Override
    protected Event<CanvasSelectionEvent> getCanvasSelectionEvent() {
        return canvasSelectionEvent;
    }
}
