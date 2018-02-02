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

package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.Point;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseEndEvent;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Radius;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Layout {

    private static final Logger logger = LoggerFactory.getLogger(Layout.class);

    org.eclipse.bpmn2.di.BPMNPlane plane;
    private final GraphBuildingContext context;

    public Layout(BPMNPlane plane, GraphBuildingContext context) {
        this.plane = plane;
        this.context = context;
    }

    public void updateChildNode(
            Node<? extends View<? extends BPMNViewDefinition>, ?> parent,
            Node<? extends View<? extends BPMNViewDefinition>, ?> child) {

        BPMNShape parentShape = getBPMNShapeForElement(parent.getUUID());
        BPMNShape childShape = getBPMNShapeForElement(child.getUUID());
        Bounds parentBounds = parentShape.getBounds();
        Bounds childBounds = childShape.getBounds();

        float relativeX = childBounds.getX() - parentBounds.getX();
        float relativeY = childBounds.getY() - parentBounds.getY();

        BoundsImpl convertedBounds = BoundsImpl.build(
                relativeX,
                relativeY,
                relativeX + childBounds.getWidth(),
                relativeY + childBounds.getHeight());
        child.getContent().setBounds(convertedBounds);

        if (child.getContent() instanceof BaseTask) {
            BaseTask content = (BaseTask) child.getContent();
            content.setDimensionsSet(new RectangleDimensionsSet(
                    (double) childBounds.getWidth(),
                    (double) childBounds.getHeight()
            ));
        }

        if (child.getContent() instanceof BaseEndEvent) {
            BaseEndEvent content = (BaseEndEvent) child.getContent();
            content.setDimensionsSet(new CircleDimensionSet(new Radius(
                    childBounds.getHeight() / 2d
            )));
        }

        context.updatePosition(child, Point2D.create(relativeX, relativeY));

        logger.info(child.getContent().getDefinition().toString() + child.getContent().getBounds().toString());
    }



    private BPMNShape getBPMNShapeForElement(String elementId) {
        return plane.getPlaneElement().stream()
                .filter(dia -> dia instanceof BPMNShape)
                .map(shape -> (BPMNShape) shape)
                .filter(shape -> shape.getBpmnElement().getId().equals(elementId))
                .findFirst().get();
    }
}
