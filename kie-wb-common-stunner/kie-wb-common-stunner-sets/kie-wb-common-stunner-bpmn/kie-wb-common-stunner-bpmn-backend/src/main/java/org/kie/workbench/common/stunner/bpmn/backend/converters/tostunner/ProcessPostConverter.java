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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Height;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Width;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

public class ProcessPostConverter {

    private static double PRECISION = 0.5;

    public void postConvert(BpmnNode rootNode) {
        if (hasCollapsedChildren(rootNode)) {
            List<LaneInfo> laneInfos = new ArrayList<>();
            new ArrayList<>(rootNode.getChildren()).stream()
                    .filter(ProcessPostConverter::isLane)
                    .filter(BpmnNode::hasChildren)
                    .forEach(lane -> {
                        LaneInfo laneInfo = new LaneInfo(lane, Padding.of(lane), new ArrayList<>(lane.getChildren()));
                        laneInfos.add(laneInfo);
                        laneInfo.getChildren().forEach(child -> child.setParent(rootNode));
                        rootNode.removeChild(lane);
                    });

            rootNode.getChildren().stream()
                    .filter(ProcessPostConverter::isSubProcess)
                    .forEach(this::postConvertSubProcess);

            List<BpmnNode> resizedChildren = getResizedChildren(rootNode);
            resizedChildren.forEach(resizedChild -> applyNodeResize(rootNode, resizedChild));

            laneInfos.forEach(laneInfo -> {
                laneInfo.getLane().setParent(rootNode);
                laneInfo.getChildren().forEach(node -> node.setParent(laneInfo.getLane()));
                resizeLane(laneInfo.getLane(), laneInfo.getPadding());
            });
        }
    }

    private static boolean hasCollapsedChildren(BpmnNode rootNode) {
        boolean result = false;
        for (BpmnNode child : rootNode.getChildren()) {
            if (isLane(child)) {
                result = hasCollapsedChildren(child);
            } else if (isSubProcess(child)) {
                result = child.isCollapsed() || hasCollapsedChildren(child);
            }
            if (result) {
                break;
            }
        }
        return result;
    }

    private void postConvertSubProcess(BpmnNode subProcess) {
        subProcess.getChildren().stream()
                .filter(ProcessPostConverter::isSubProcess)
                .forEach(this::postConvertSubProcess);

        List<BpmnNode> resizedChildren = getResizedChildren(subProcess);
        resizedChildren.forEach(resizedChild -> applyNodeResize(subProcess, resizedChild));
        if ((subProcess.isCollapsed() && subProcess.hasChildren()) || !resizedChildren.isEmpty()) {
            resizeSubProcess(subProcess);
        }
        if (subProcess.isCollapsed()) {
            Bound subProcessUl = subProcess.value().getContent().getBounds().getUpperLeft();
            //docked nodes are relative to the holding element, translation is not applied.
            subProcess.getChildren().stream()
                    .filter(child -> !child.isDocked())
                    .forEach(child -> translate(child, subProcessUl.getX(), subProcessUl.getY()));
            translate(subProcess.getEdges(), subProcessUl.getX(), subProcessUl.getY());
            subProcess.setCollapsed(false);
        }
    }

    private static List<BpmnNode> getResizedChildren(BpmnNode node) {
        return node.getChildren().stream()
                .filter(BpmnNode::isResized)
                .collect(Collectors.toList());
    }

    private static void resizeSubProcess(BpmnNode subProcess) {
        if (subProcess.hasChildren()) {
            ViewPort viewPort = ViewPort.of(subProcess, true);
            double leftPadding = viewPort.getUpperLeftX();
            double topPadding = viewPort.getUpperLeftY();
            double width = viewPort.getLowerRightX() + leftPadding;
            double height = viewPort.getLowerRightY() + topPadding;

            Bounds subProcessBounds = subProcess.value().getContent().getBounds();
            double originalWidth = subProcessBounds.getWidth();
            double originalHeight = subProcessBounds.getHeight();
            Bound subProcessUl = subProcessBounds.getUpperLeft();
            Bound subProcessLr = subProcessBounds.getLowerRight();
            subProcessLr.setX(subProcessUl.getX() + width);
            subProcessLr.setY(subProcessUl.getY() + height);

            RectangleDimensionsSet subProcessRectangle = ((BaseSubprocess) subProcess.value().getContent().getDefinition()).getDimensionsSet();
            subProcessRectangle.setWidth(new Width(width));
            subProcessRectangle.setHeight(new Height(height));
            subProcess.setResized(true);

            double widthFactor = width / originalWidth;
            double heightFactor = height / originalHeight;
            inEdges(subProcess.getParent(), subProcess).forEach(edge -> scale(edge.getTargetConnection().getLocation(), widthFactor, heightFactor));
            outEdges(subProcess.getParent(), subProcess).forEach(edge -> scale(edge.getSourceConnection().getLocation(), widthFactor, heightFactor));
        }
    }

    private static void resizeLane(BpmnNode lane, Padding padding) {
        if (lane.hasChildren()) {
            ViewPort viewPort = ViewPort.of(lane, false);
            Bounds laneBounds = lane.value().getContent().getBounds();
            Bound laneUl = laneBounds.getUpperLeft();
            Bound laneLr = laneBounds.getLowerRight();
            laneUl.setX(viewPort.getUpperLeftX() - padding.getLeft());
            laneUl.setY(viewPort.getUpperLeftY() - padding.getTop());
            laneLr.setX(viewPort.getLowerRightX() + padding.getRight());
            laneLr.setY(viewPort.getLowerRightY() + padding.getBottom());

            RectangleDimensionsSet laneRectangle = ((Lane) lane.value().getContent().getDefinition()).getDimensionsSet();
            laneRectangle.setWidth(new Width(laneBounds.getWidth()));
            laneRectangle.setHeight(new Height(laneBounds.getHeight()));
        }
    }

    private static void adjustEdgeConnection(BpmnEdge.Simple edge, boolean targetConnection) {
        Point2D siblingPoint = null;
        Point2D connnectionPoint;
        BpmnNode connectionPointNode;
        List<Point2D> controlPoints = edge.getControlPoints();
        if (targetConnection) {
            connnectionPoint = edge.getTargetConnection().getLocation();
            connectionPointNode = edge.getTarget();
            if (controlPoints.size() >= 1) {
                siblingPoint = controlPoints.get(controlPoints.size() - 1);
            }
        } else {
            connnectionPoint = edge.getSourceConnection().getLocation();
            connectionPointNode = edge.getSource();
            if (controlPoints.size() >= 1) {
                siblingPoint = controlPoints.get(0);
            }
        }
        if (siblingPoint != null) {
            Bounds bounds = connectionPointNode.value().getContent().getBounds();
            Bound nodeUl = bounds.getUpperLeft();
            if (equals(connnectionPoint.getY(), 0, PRECISION) || equals(connnectionPoint.getY(), bounds.getHeight(), PRECISION)) {
                //scaled point is on top or bottom
                if (siblingPoint.getY() != (connnectionPoint.getY() + nodeUl.getY())) {
                    siblingPoint.setX(nodeUl.getX() + (bounds.getWidth() / 2));
                }
            } else {
                //scaled point left or right
                if (siblingPoint.getX() != (connnectionPoint.getX() + nodeUl.getX())) {
                    siblingPoint.setY(nodeUl.getY() + (bounds.getHeight() / 2));
                }
            }
        }
    }

    private static void applyNodeResize(BpmnNode container, BpmnNode resizedChild) {
        Bounds originalBounds = resizedChild.getPropertyReader().getBounds();
        Bounds currentBounds = resizedChild.value().getContent().getBounds();
        double deltaX = currentBounds.getWidth() - originalBounds.getWidth();
        double deltaY = currentBounds.getHeight() - originalBounds.getHeight();
        container.getChildren().stream()
                .filter(child -> child != resizedChild)
                .forEach(child -> applyTranslationIfRequired(currentBounds.getX(), currentBounds.getY(), deltaX, deltaY, child));

        toSimpleEdgesStream(container.getEdges()).forEach(edge -> applyTranslationIfRequired(currentBounds.getX(), currentBounds.getY(), deltaX, deltaY, edge));

        toSimpleEdgesStream(container.getEdges()).forEach(edge -> adjustEdgeConnection(edge, true));
        toSimpleEdgesStream(container.getEdges()).forEach(edge -> adjustEdgeConnection(edge, false));
    }

    private static void translate(BpmnNode node, double deltaX, double deltaY) {
        //TODO WM, ver los nodos q son circulos...
        Bounds childBounds = node.value().getContent().getBounds();
        translate(childBounds.getUpperLeft(), deltaX, deltaY);
        translate(childBounds.getLowerRight(), deltaX, deltaY);
        if (!node.isCollapsed()) {
            node.getChildren().forEach(child -> translate(child, deltaX, deltaY));
            translate(node.getEdges(), deltaX, deltaY);
        }
    }

    private static void translate(List<BpmnEdge> edges, double deltaX, double deltaY) {
        toSimpleEdgesStream(edges).forEach(edge -> translate(edge, deltaX, deltaY));
    }

    private static void translate(BpmnEdge.Simple edge, double deltaX, double deltaY) {
        //source and target connections points are relative to the respective source and target node, no translation is required for them,
        //only the control points are translated.
        edge.getControlPoints().forEach(controlPoint -> translate(controlPoint, deltaX, deltaY));
    }

    private static void translate(Point2D point, double deltaX, double deltaY) {
        point.setX(point.getX() + deltaX);
        point.setY(point.getY() + deltaY);
    }

    private static void translate(Bound bound, double deltaX, double deltaY) {
        bound.setX(bound.getX() + deltaX);
        bound.setY(bound.getY() + deltaY);
    }

    private static void scale(Point2D point2D, double widthFactor, double heightFactor) {
        point2D.setX(point2D.getX() * widthFactor);
        point2D.setY(point2D.getY() * heightFactor);
    }

    private static void applyTranslationIfRequired(double x, double y, double deltaX, double deltaY, BpmnNode node) {
        Bounds bounds = node.value().getContent().getBounds();
        Bound ul = bounds.getUpperLeft();
        if (ul.getX() >= x && ul.getY() >= y) {
            translate(node, deltaX, deltaY);
        } else if (ul.getX() >= x && ul.getY() < y) {
            translate(node, deltaX, 0);
        } else if (ul.getX() < x && ul.getY() >= y) {
            translate(node, 0, deltaY);
        }
    }

    private static void applyTranslationIfRequired(double x, double y, double deltaX, double deltaY, BpmnEdge.Simple edge) {
        edge.getControlPoints().forEach(point -> applyTranslationIfRequired(x, y, deltaX, deltaY, point));
    }

    private static void applyTranslationIfRequired(double x, double y, double deltaX, double deltaY, Point2D point) {
        if (point.getX() >= x && point.getY() >= y) {
            translate(point, deltaX, deltaY);
        } else if (point.getX() >= x && point.getY() < y) {
            translate(point, deltaX, 0);
        } else if (point.getX() < x && point.getY() >= y) {
            translate(point, 0, deltaY);
        }
    }

    private static boolean isSubProcess(BpmnNode node) {
        return node.value().getContent().getDefinition() instanceof EmbeddedSubprocess ||
                node.value().getContent().getDefinition() instanceof EventSubprocess ||
                node.value().getContent().getDefinition() instanceof AdHocSubprocess;
    }

    private static boolean isLane(BpmnNode node) {
        return node.value().getContent().getDefinition() instanceof Lane;
    }

    private static List<BpmnEdge.Simple> inEdges(BpmnNode container, BpmnNode targetNode) {
        return toSimpleEdgesStream(container.getEdges())
                .filter(edge -> edge.getTarget() == targetNode)
                .collect(Collectors.toList());
    }

    private static List<BpmnEdge.Simple> outEdges(BpmnNode container, BpmnNode sourceNode) {
        return toSimpleEdgesStream(container.getEdges())
                .filter(edge -> edge.getSource() == sourceNode)
                .collect(Collectors.toList());
    }

    private static Stream<BpmnEdge.Simple> toSimpleEdgesStream(List<BpmnEdge> edges) {
        return edges.stream()
                .filter(edge -> edge instanceof BpmnEdge.Simple)
                .map(edge -> (BpmnEdge.Simple) edge);
    }

    private static <X, T extends Object & Comparable<? super T>> T min(List<X> values, Function<X, T> mapper) {
        return Collections.min(values.stream().map(mapper).collect(Collectors.toList()));
    }

    private static <X, T extends Object & Comparable<? super T>> T max(List<X> values, Function<X, T> mapper) {
        return Collections.max(values.stream().map(mapper).collect(Collectors.toList()));
    }

    private static boolean equals(double a, double b, double delta) {
        if (Double.compare(a, b) == 0) {
            return true;
        } else {
            return Math.abs(a - b) < delta;
        }
    }

    private static class LaneInfo {

        private BpmnNode lane;
        private Padding padding;
        private List<BpmnNode> children;

        public LaneInfo(BpmnNode lane, Padding padding, List<BpmnNode> children) {
            this.lane = lane;
            this.padding = padding;
            this.children = children;
        }

        public BpmnNode getLane() {
            return lane;
        }

        public Padding getPadding() {
            return padding;
        }

        public List<BpmnNode> getChildren() {
            return children;
        }
    }

    private static class Padding {

        private double top;
        private double right;
        private double bottom;
        private double left;

        public Padding() {
        }

        public Padding(double top, double right, double bottom, double left) {
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            this.left = left;
        }

        public double getTop() {
            return top;
        }

        public double getRight() {
            return right;
        }

        public double getBottom() {
            return bottom;
        }

        public double getLeft() {
            return left;
        }

        public static Padding of(BpmnNode node) {
            if (!node.hasChildren()) {
                return new Padding();
            }
            ViewPort viewPort = ViewPort.of(node, false);
            Bounds bounds = node.value().getContent().getBounds();
            double topPadding = Math.abs(viewPort.getUpperLeftY() - bounds.getUpperLeft().getY());
            double rightPadding = Math.abs(viewPort.getLowerRightX() - bounds.getLowerRight().getX());
            double bottomPadding = Math.abs(viewPort.getLowerRightY() - bounds.getLowerRight().getY());
            double leftPadding = Math.abs(viewPort.getUpperLeftX() - bounds.getUpperLeft().getX());
            return new Padding(topPadding, rightPadding, bottomPadding, leftPadding);
        }
    }

    private static class ViewPort {

        private double ulx;
        private double uly;
        private double lrx;
        private double lry;

        public ViewPort(double ulx, double uly, double lrx, double lry) {
            this.ulx = ulx;
            this.uly = uly;
            this.lrx = lrx;
            this.lry = lry;
        }

        public double getUpperLeftX() {
            return ulx;
        }

        public double getUpperLeftY() {
            return uly;
        }

        public double getLowerRightX() {
            return lrx;
        }

        public double getLowerRightY() {
            return lry;
        }

        public static ViewPort of(BpmnNode node, boolean includeEdges) {
            final List<Bound> ulBounds = node.getChildren().stream()
                    .map(child -> child.value().getContent().getBounds().getUpperLeft())
                    .collect(Collectors.toList());
            final List<Bound> lrBounds = node.getChildren().stream()
                    .map(child -> child.value().getContent().getBounds().getLowerRight())
                    .collect(Collectors.toList());
            List<Point2D> controlPoints;
            if (includeEdges) {
                controlPoints = toSimpleEdgesStream(node.getEdges())
                        .map(BpmnEdge.Simple::getControlPoints)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
            } else {
                controlPoints = Collections.emptyList();
            }

            double ulx = min(ulBounds, Bound::getX);
            double uly = min(ulBounds, Bound::getY);
            double lrx = max(lrBounds, Bound::getX);
            double lry = max(lrBounds, Bound::getY);

            if (!controlPoints.isEmpty()) {
                ulx = Math.min(ulx, min(controlPoints, Point2D::getX));
                uly = Math.min(uly, min(controlPoints, Point2D::getY));
                lrx = Math.max(lrx, max(controlPoints, Point2D::getX));
                lry = Math.max(lry, max(controlPoints, Point2D::getY));
            }
            return new ViewPort(ulx, uly, lrx, lry);
        }
    }
}
