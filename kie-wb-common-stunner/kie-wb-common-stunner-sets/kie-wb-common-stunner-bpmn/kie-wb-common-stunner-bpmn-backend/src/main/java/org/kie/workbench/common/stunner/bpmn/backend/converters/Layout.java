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

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.Point;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
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

        float offsetX = parentBounds.getX();
        float offsetY = parentBounds.getY();

        float relativeX = childBounds.getX() - offsetX;
        float relativeY = childBounds.getY() - offsetY;

        context.updatePosition(child, Point2D.create(relativeX, relativeY));

        logger.info(child.getContent().getDefinition().toString() + child.getContent().getBounds().toString());
    }

    public void updateNode(Node<? extends View<? extends BPMNViewDefinition>, ?> node) {
        BPMNShape shape = getBPMNShapeForElement(node.getUUID());
        Bounds bounds = shape.getBounds();

        double x, y;

        BaseElement bpmnElement = shape.getBpmnElement();
        if (bpmnElement instanceof BoundaryEvent) {
            // then we must check the overrides
            Point2D docker = Properties.docker((BoundaryEvent) bpmnElement);
            x = docker.getX();
            y = docker.getY();
        } else {
            x = bounds.getX();
            y = bounds.getY();
        }

        BoundsImpl convertedBounds = BoundsImpl.build(
                x,
                y,
                x + bounds.getWidth(),
                y + bounds.getHeight());
        node.getContent().setBounds(convertedBounds);

        context.updatePosition(node, Point2D.create(x, y));

        logger.info(node.getContent().getDefinition().toString() + node.getContent().getBounds().toString());
    }

    public void updateEdge(Edge<?, ?> edge) {
        Node sourceNode = edge.getSourceNode();
        String sourceId = sourceNode.getUUID();
        Node targetNode = edge.getTargetNode();
        String targetId = targetNode.getUUID();

        Bounds sourceBounds = getBPMNShapeForElement(sourceId).getBounds();
        Bounds targetBounds = getBPMNShapeForElement(targetId).getBounds();

        getBPMNEdgeForElement(edge.getUUID()).ifPresent(bpmnEdge -> {
            List<Point2D> pts = points(sourceBounds,
                                       targetBounds,
                                       bpmnEdge.getWaypoint());

            Optional<Connection> sourceConnection = ((ViewConnector) edge.getContent()).getSourceConnection();
            sourceConnection.get().getLocation().setX(pts.get(0).getX());
            sourceConnection.get().getLocation().setY(pts.get(0).getY());

            Optional<Connection> targetConnection = ((ViewConnector) edge.getContent()).getTargetConnection();
            targetConnection.get().getLocation().setX(pts.get(1).getX());
            targetConnection.get().getLocation().setY(pts.get(1).getY());
//
//            context.updatePosition(sourceNode, pts.get(0));
//            context.updatePosition(targetNode, pts.get(1));
//
//            logger.info(sourceNode.getContent().getDefinition().toString()+sourceNode.getContent().getBounds().toString());
//            logger.info(targetNode.getContent().getDefinition().toString()+targetNode.getContent().getBounds().toString());

        });
    }

    private Optional<BPMNEdge> getBPMNEdgeForElement(String elementId) {
        return plane.getPlaneElement().stream()
                .filter(dia -> dia instanceof BPMNEdge)
                .map(edge -> (BPMNEdge) edge)
                .filter(edge -> edge.getBpmnElement().getId().equals(elementId))
                .findFirst();
    }

    private BPMNShape getBPMNShapeForElement(String elementId) {
        return plane.getPlaneElement().stream()
                .filter(dia -> dia instanceof BPMNShape)
                .map(shape -> (BPMNShape) shape)
                .filter(shape -> shape.getBpmnElement().getId().equals(elementId))
                .findFirst().get();
    }

    private List<Point2D> points(Bounds sourceBounds, Bounds targetBounds, List<Point> waypoints) {
        List<Point2D> points = new ArrayList<>();

        if (waypoints.isEmpty()) {
            points.add(Point2D.create(sourceBounds.getWidth() / 2,
                                      sourceBounds.getHeight() / 2));

            points.add(Point2D.create(targetBounds.getWidth() / 2,
                                      targetBounds.getHeight() / 2));
        } else {
            if (waypoints.size() != 2) {
                logger.warn("Waypoints should be either 0 or 2. Unexpected size: " + waypoints.size());
            }
            Point firstWaypoint = waypoints.get(0);
            points.add(Point2D.create(
                    firstWaypoint.getX() - sourceBounds.getX(),
                    firstWaypoint.getY() - sourceBounds.getY()));

            // will skip these for now...
            // for (int i = 1; i < waypoints.size() - 1; i++) {
            //     Point midWaypoints = waypoints.get(i);
            //     points.add(Point2D.create(midWaypoints.getX(),
            //                               midWaypoints.getY()));
            // }

            Point lastWaypoint = waypoints.get(waypoints.size() - 1);
            points.add(Point2D.create(lastWaypoint.getX() - targetBounds.getX(),
                                      lastWaypoint.getY() - targetBounds.getY()));
        }

        return points;
    }
}
