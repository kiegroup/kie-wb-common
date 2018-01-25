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

    public Layout(BPMNPlane plane) {
        this.plane = plane;
    }

    public void updateNode(Node<? extends View<? extends BPMNViewDefinition>, ?> node) {
        getBPMNShapeForElement(node.getUUID()).ifPresent(shape -> {
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
        });
    }

    public void updateEdge(Edge<?, ?> edge) {
        String sourceId = edge.getSourceNode().getUUID();
        String targetId = edge.getTargetNode().getUUID();

        Bounds sourceBounds = getBPMNShapeForElement(sourceId).get().getBounds();
        Bounds targetBounds = getBPMNShapeForElement(targetId).get().getBounds();

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
        });
    }

    private Optional<BPMNEdge> getBPMNEdgeForElement(String elementId) {
        return plane.getPlaneElement().stream()
                .filter(dia -> dia instanceof BPMNEdge)
                .map(edge -> (BPMNEdge) edge)
                .filter(edge -> edge.getBpmnElement().getId().equals(elementId))
                .findFirst();
    }

    private Optional<BPMNShape> getBPMNShapeForElement(String elementId) {
        return plane.getPlaneElement().stream()
                .filter(dia -> dia instanceof BPMNShape)
                .map(shape -> (BPMNShape) shape)
                .filter(shape -> shape.getBpmnElement().getId().equals(elementId))
                .findFirst();
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
