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

package org.kie.workbench.common.dmn.client.shape.view.decisionservice;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.AbstractControlHandle;
import com.ait.lienzo.client.core.shape.wires.IControlHandle;
import com.ait.lienzo.client.core.shape.wires.IControlHandleFactory;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.types.DragBounds;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DefaultDragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragContext;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.DragConstraint;
import com.ait.lienzo.shared.core.types.DragMode;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import org.kie.workbench.common.dmn.api.property.dimensions.GeneralRectangleDimensionsSet;
import org.kie.workbench.common.stunner.core.client.shape.view.event.DragHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitiveShape;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGShapeViewImpl;

public class DecisionServiceSVGShapeView extends SVGShapeViewImpl {

    private final DecisionServiceDividerLine divider;
    private final HandlerRegistrationManager registrationManager = new HandlerRegistrationManager();
    private final DecisionServiceControlHandleFactory decisionServiceControlHandleFactory;

    public DecisionServiceSVGShapeView(final String name,
                                       final SVGPrimitiveShape svgPrimitive,
                                       final double width,
                                       final double height,
                                       final boolean resizable) {
        super(name,
              svgPrimitive,
              width,
              height,
              resizable);

        final Shape<?> shape = getPath();
        this.divider = new DecisionServiceDividerLine(() -> shape.getBoundingBox().getWidth());
        this.decisionServiceControlHandleFactory = new DecisionServiceControlHandleFactory(divider,
                                                                                           shape.getControlHandleFactory(),
                                                                                           () -> shape.getBoundingBox().getWidth(),
                                                                                           () -> shape.getBoundingBox().getHeight());
        shape.setControlHandleFactory(decisionServiceControlHandleFactory);
        addChild(divider.asSVGPrimitiveShape());

        addWiresResizeStepHandler(event -> {
            decisionServiceControlHandleFactory
                    .getDividerResizeControlHandle()
                    .ifPresent(handle -> handle.getControl().setX(shape.getBoundingBox().getWidth() / 2));
        });
    }

    public DecisionServiceSVGShapeView addDividerDragHandler(final DragHandler dragHandler) {
        final HandlerManager handlerManager = getHandlerManager();
        final HandlerRegistration dragStartRegistration = handlerManager.addHandler(MoveDividerStartEvent.TYPE, event -> dragHandler.start(buildDragEvent(event)));
        final HandlerRegistration dragStepRegistration = handlerManager.addHandler(MoveDividerStepEvent.TYPE, event -> dragHandler.handle(buildDragEvent(event)));
        final HandlerRegistration dragEndRegistration = handlerManager.addHandler(MoveDividerEndEvent.TYPE, event -> dragHandler.end(buildDragEvent(event)));
        final HandlerRegistration[] registrations = new HandlerRegistration[]{dragStartRegistration, dragStepRegistration, dragEndRegistration};
        getEventHandlerManager().addHandlersRegistration(ViewEventType.DRAG, registrations);

        return this;
    }

    public double getDividerLineY() {
        return divider.getY();
    }

    public void setDividerLineY(final double y) {
        divider.setY(y);
        decisionServiceControlHandleFactory
                .getDividerResizeControlHandle()
                .ifPresent(handle -> handle.getControl().setY(y));
    }

    @Override
    public void destroy() {
        registrationManager.destroy();
        super.destroy();
    }

    private class DecisionServiceControlHandleFactory implements IControlHandleFactory {

        private static final double R0 = 5;

        private static final double R1 = 10;

        private static final double ANIMATION_DURATION = 150d;

        private final DecisionServiceDividerLine divider;
        private final IControlHandleFactory delegateControlHandleFactory;
        private final Supplier<Double> dragBoundsWidthSupplier;
        private final Supplier<Double> dragBoundsHeightSupplier;

        private Optional<ResizeControlHandle> dividerResizeControlHandle = Optional.empty();

        DecisionServiceControlHandleFactory(final DecisionServiceDividerLine divider,
                                            final IControlHandleFactory delegateControlHandleFactory,
                                            final Supplier<Double> dragBoundsWidthSupplier,
                                            final Supplier<Double> dragBoundsHeightSupplier) {
            this.divider = divider;
            this.delegateControlHandleFactory = delegateControlHandleFactory;
            this.dragBoundsWidthSupplier = dragBoundsWidthSupplier;
            this.dragBoundsHeightSupplier = dragBoundsHeightSupplier;
        }

        Optional<ResizeControlHandle> getDividerResizeControlHandle() {
            return dividerResizeControlHandle;
        }

        @Override
        public Map<IControlHandle.ControlHandleType, IControlHandleList> getControlHandles(final IControlHandle.ControlHandleType... types) {
            final Map<IControlHandle.ControlHandleType, IControlHandleList> controlHandles = delegateControlHandleFactory.getControlHandles(types);
            appendDividerResizeControlPoint(controlHandles);
            return controlHandles;
        }

        @Override
        public Map<IControlHandle.ControlHandleType, IControlHandleList> getControlHandles(final List<IControlHandle.ControlHandleType> types) {
            final Map<IControlHandle.ControlHandleType, IControlHandleList> controlHandles = delegateControlHandleFactory.getControlHandles(types);
            appendDividerResizeControlPoint(controlHandles);
            return controlHandles;
        }

        private void appendDividerResizeControlPoint(final Map<IControlHandle.ControlHandleType, IControlHandleList> controlHandles) {
            final IControlHandleList resizeControlHandles = controlHandles.get(IControlHandle.ControlHandleStandardType.RESIZE);
            if (!dividerResizeControlHandle.isPresent()) {
                dividerResizeControlHandle = Optional.of(getResizeControlHandle(resizeControlHandles,
                                                                                new Point2D(dragBoundsWidthSupplier.get() / 2, 0)));
                setupControlHandleEventHandlers();
            }
            dividerResizeControlHandle.ifPresent(resizeControlHandles::add);
        }

        private void setupControlHandleEventHandlers() {
            dividerResizeControlHandle.ifPresent(handle -> {
                final IPrimitive<?> control = handle.getControl();
                registrationManager.register(control.addNodeDragStartHandler(this::resizeStart));
                registrationManager.register(control.addNodeDragMoveHandler(this::resizeMove));
                registrationManager.register(control.addNodeDragEndHandler(this::resizeEnd));
            });
        }

        private void resizeStart(final NodeDragStartEvent event) {
            fireDragEvent(new MoveDividerStartEvent(DecisionServiceSVGShapeView.this, event));
        }

        private void resizeMove(final NodeDragMoveEvent event) {
            fireDragEvent(new MoveDividerStepEvent(DecisionServiceSVGShapeView.this, event));
        }

        private void resizeEnd(final NodeDragEndEvent event) {
            fireDragEvent(new MoveDividerEndEvent(DecisionServiceSVGShapeView.this, event));
        }

        private void fireDragEvent(final GwtEvent<?> event) {
            dividerResizeControlHandle.ifPresent(handle -> {
                divider.setY(handle.getControl().getY());
                getHandlerManager().fireEvent(event);
            });
        }

        private ResizeControlHandle getResizeControlHandle(final IControlHandleList resizeControlHandles,
                                                           final Point2D controlPointOffset) {
            final Circle controlShape = getControlPrimitive(R0, controlPointOffset);
            final ResizeControlHandle handle = new ResizeControlHandle(controlShape,
                                                                       resizeControlHandles,
                                                                       dragBoundsWidthSupplier,
                                                                       dragBoundsHeightSupplier);

            animate(handle, AnimationProperty.Properties.RADIUS(R1), AnimationProperty.Properties.RADIUS(R0));

            return handle;
        }

        private Circle getControlPrimitive(final double size,
                                           final Point2D controlPointOffset) {
            return new Circle(size)
                    .setX(divider.getX() + controlPointOffset.getX())
                    .setY(divider.getY() + controlPointOffset.getY())
                    .setFillColor(ColorName.DARKRED)
                    .setFillAlpha(0.8)
                    .setStrokeColor(ColorName.BLACK)
                    .setStrokeWidth(0.5)
                    .setDraggable(true)
                    .setDragConstraint(DragConstraint.VERTICAL)
                    .setDragMode(DragMode.SAME_LAYER);
        }

        private void animate(final AbstractControlHandle handle,
                             final AnimationProperty initialProperty,
                             final AnimationProperty endProperty) {
            final Node<?> node = (Node<?>) handle.getControl();

            handle.getHandlerRegistrationManager().register(node.addNodeMouseEnterHandler((event) -> animate(node, initialProperty)));
            handle.getHandlerRegistrationManager().register(node.addNodeMouseExitHandler((event) -> animate(node, endProperty)));
        }

        private void animate(final Node<?> node, final AnimationProperty property) {
            node.animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(property), ANIMATION_DURATION);
        }
    }

    private class ResizeControlHandle extends AbstractControlHandle {

        private final Circle controlShape;

        public ResizeControlHandle(final Circle controlShape,
                                   final IControlHandleList resizeControlHandles,
                                   final Supplier<Double> dragBoundsWidthSupplier,
                                   final Supplier<Double> dragBoundsHeightSupplier) {
            this.controlShape = controlShape;
            final ResizeHandleDragHandler handler = new ResizeHandleDragHandler(controlShape,
                                                                                resizeControlHandles,
                                                                                this,
                                                                                dragBoundsWidthSupplier,
                                                                                dragBoundsHeightSupplier);
            controlShape.setDragConstraints(handler);
            register(controlShape.addNodeDragEndHandler(handler));
        }

        @Override
        public IPrimitive<?> getControl() {
            return controlShape;
        }

        @Override
        public void destroy() {
            super.destroy();
        }

        @Override
        public ControlHandleType getType() {
            return ControlHandleStandardType.RESIZE;
        }
    }

    private class ResizeHandleDragHandler implements DragConstraintEnforcer,
                                                     NodeDragEndHandler {

        private final Circle controlShape;
        private final IControlHandleList resizeControlHandles;
        private final ResizeControlHandle resizeControlHandle;
        private final Supplier<Double> dragBoundsWidthSupplier;
        private final Supplier<Double> dragBoundsHeightSupplier;
        private final DragConstraintEnforcer delegateDragConstraintEnforcer = new DefaultDragConstraintEnforcer();

        public ResizeHandleDragHandler(final Circle controlShape,
                                       final IControlHandleList resizeControlHandles,
                                       final ResizeControlHandle resizeControlHandle,
                                       final Supplier<Double> dragBoundsWidthSupplier,
                                       final Supplier<Double> dragBoundsHeightSupplier) {
            this.controlShape = controlShape;
            this.resizeControlHandles = resizeControlHandles;
            this.resizeControlHandle = resizeControlHandle;
            this.dragBoundsWidthSupplier = dragBoundsWidthSupplier;
            this.dragBoundsHeightSupplier = dragBoundsHeightSupplier;
        }

        @Override
        public void startDrag(final DragContext dragContext) {
            dragContext.getNode().setDragBounds(makeDragBounds());
            delegateDragConstraintEnforcer.startDrag(dragContext);

            if ((resizeControlHandle.isActive()) && (resizeControlHandles.isActive())) {
                controlShape.setFillColor(ColorName.GREEN);
                controlShape.getLayer().draw();
            }
        }

        @Override
        public boolean adjust(final Point2D dxy) {
            return delegateDragConstraintEnforcer.adjust(dxy);
        }

        @Override
        public void onNodeDragEnd(final NodeDragEndEvent event) {
            if ((resizeControlHandle.isActive()) && (resizeControlHandles.isActive())) {
                controlShape.setFillColor(ColorName.DARKRED);
                controlShape.getLayer().draw();
            }
        }

        private DragBounds makeDragBounds() {
            final double width = dragBoundsWidthSupplier.get();
            final double height = dragBoundsHeightSupplier.get();
            return new DragBounds(0,
                                  GeneralRectangleDimensionsSet.DEFAULT_HEIGHT,
                                  width,
                                  height - GeneralRectangleDimensionsSet.DEFAULT_HEIGHT);
        }
    }
}
