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
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.components.views.FloatingView;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.uberfire.client.views.pfly.selectpicker.JQueryElementOffset;

import static org.uberfire.client.views.pfly.selectpicker.JQuery.$;

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
    public CanvasInPlaceTextEditorControl<AbstractCanvasHandler, EditorSession, Element> show(final Element item,
                                                                                              final double x,
                                                                                              final double y) {
        if (getTextEditorBox().isVisible()) {
            flush();
        }
        setUuid(item.getUUID());

        final Shape<?> shape = getShape(item.getUUID());
        final ShapeView shapeView = shape.getShapeView();

        JQueryElementOffset offset = $(".canvas-panel").offset();

        enableShapeEdit();
        getTextEditorBox().show(item);

        double translateX = canvasHandler.getCanvas().getTransform().getTranslate().getX();
        double translateY = canvasHandler.getCanvas().getTransform().getTranslate().getY();
        double scale =      canvasHandler.getCanvas().getTransform().getScale().getX();

        org.kie.workbench.common.stunner.core.client.shape.view.HasTitle hasTitle
                = ((org.kie.workbench.common.stunner.core.client.shape.view.HasTitle) shapeView);

        ((HasTitle) getTextEditorBox()).setFontSize(defaultFontSize * scale);
        ((HasTitle) getTextEditorBox()).setTitleOrientation(hasTitle.getOrientation());
        ((HasTitle) getTextEditorBox()).setTitlePosition(hasTitle.getFontPosition());
        ((HasTitle) getTextEditorBox()).setTitleAlignment(hasTitle.getFontAlignment());
        ((HasTitle) getTextEditorBox()).setFontSize(defaultFontSize * scale);
        ((HasSize)  getTextEditorBox()).setWidth(shapeView.getBoundingBox().getWidth() * scale);
        ((HasSize)  getTextEditorBox()).setHeight(shapeView.getBoundingBox().getHeight() * scale);

        final double offsetX = getTextEditorBox().getDisplayOffsetX();
        final double offsetY = getTextEditorBox().getDisplayOffsetY();

        setXandY(shape, scale);

        getFloatingView()
                .setOffsetX(offset.left + offsetX + (!isOutSide(shape) ? translateX : 0))
                .setOffsetY(offset.top + offsetY + (!isOutSide(shape) ? translateY : 0))
                .show();
        return this;
    }

    private boolean ifSubNode(Node<View<?>, Edge> parent) {
        return !GraphUtils.isRootNode(parent, canvasHandler.getGraphIndex()
                .getGraph());
    }

    private void setXandY(Shape<?> shape, double scale) {
        org.kie.workbench.common.stunner.core.client.shape.view.HasTitle hasTitle
                = ((org.kie.workbench.common.stunner.core.client.shape.view.HasTitle) shape.getShapeView());
        Node<View<?>, Edge> parent = canvasHandler.getGraphIndex()
                .getNode(GraphUtils.getParent(canvasHandler
                                                      .getGraphIndex()
                                                      .getNode(shape.getUUID())).getUUID());
        if (isOutSide(shape)) {
            getFloatingView().setX(hasTitle.getTitleFontX());
            getFloatingView().setY(hasTitle.getTitleFontY());
        } else {
            if (ifSubNode(parent)) {
                final Shape<?> parentShape = getShape(parent.getUUID());
                getFloatingView().setX((parentShape.getShapeView().getShapeX() + shape.getShapeView().getShapeX()) * scale);
                getFloatingView().setY((parentShape.getShapeView().getShapeY() + shape.getShapeView().getShapeY()) * scale);
            } else {
                getFloatingView().setX(shape.getShapeView().getShapeX() * scale);
                getFloatingView().setY(shape.getShapeView().getShapeY() * scale);
            }
        }
    }

    protected boolean isOutSide(Shape<?> shape) {
        org.kie.workbench.common.stunner.core.client.shape.view.HasTitle hasTitle
                = ((org.kie.workbench.common.stunner.core.client.shape.view.HasTitle) shape.getShapeView());
        return hasTitle.getFontPosition().equals("OUTSIDE");
    }

    @Override
    public CanvasInPlaceTextEditorControl<AbstractCanvasHandler, EditorSession, Element> hide() {
        if (uuid != null) {
            getShape(uuid).applyState(ShapeState.NONE);
        }
        super.hide();
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
