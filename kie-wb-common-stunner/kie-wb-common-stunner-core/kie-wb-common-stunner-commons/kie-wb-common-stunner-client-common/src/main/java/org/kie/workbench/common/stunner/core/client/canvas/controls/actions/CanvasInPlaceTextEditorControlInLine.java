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
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.graph.Element;
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
        double scale = canvasHandler.getCanvas().getTransform().getScale().getX();

        HasTitle hasTitle = ((HasTitle)shapeView);

        getTextEditorBox().setFontSize(defaultFontSize * scale);
        getTextEditorBox().setFontX(hasTitle.getTitleFontX());
        getTextEditorBox().setFontY(hasTitle.getTitleFontY());
        getTextEditorBox().setOrientation(hasTitle.getOrientation());
        getTextEditorBox().setFontPosition(hasTitle.getFontPosition());
        getTextEditorBox().setFontAlignment(hasTitle.getFontAlignment());
        getTextEditorBox().setFontSize(defaultFontSize * scale);
        getTextEditorBox().setWidth(shapeView.getBoundingBox().getWidth() * scale);
        getTextEditorBox().setHeight(shapeView.getBoundingBox().getHeight() * scale);

        final double offsetX = getTextEditorBox().getDisplayOffsetX();
        final double offsetY = getTextEditorBox().getDisplayOffsetY();

        getFloatingView()
                .setX((hasTitle.getFontPosition().equals("OUTSIDE") ? hasTitle.getTitleFontX() : shapeView.getShapeX()) * scale)
                .setY((hasTitle.getFontPosition().equals("OUTSIDE") ? hasTitle.getTitleFontY() : shapeView.getShapeY()) * scale)
                .setOffsetX(((offset.left + offsetX) * scale) + translateX)
                .setOffsetY(((offset.top + offsetY) * scale) + translateY)
                .show();

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
