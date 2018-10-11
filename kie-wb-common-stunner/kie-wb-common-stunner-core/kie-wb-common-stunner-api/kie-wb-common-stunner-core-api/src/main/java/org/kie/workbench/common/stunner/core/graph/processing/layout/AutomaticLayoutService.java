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

package org.kie.workbench.common.stunner.core.graph.processing.layout;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.HasBounds;
import org.kie.workbench.common.stunner.core.graph.processing.layout.step01.CycleBreaker;
import org.kie.workbench.common.stunner.core.graph.processing.layout.step02.VertexLayerer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.step03.VertexOrdering;
import org.kie.workbench.common.stunner.core.graph.processing.layout.step04.DefaultVertexPositioning;
import org.kie.workbench.common.stunner.core.graph.processing.layout.step04.VertexPositioning;

public final class AutomaticLayoutService {

    private static final double closeToZeroTolerance = 0.1;

    private final CycleBreaker cycleBreaker;
    private final VertexLayerer vertexLayerer;
    private final VertexOrdering vertexOrdering;
    private final VertexPositioning vertexPositioning;

    /**
     * Default constructor.
     * @param cycleBreaker The strategy used to break cycles in cycle graphs.
     * @param vertexLayerer The strategy used to choose the layer for each vertex.
     * @param vertexOrdering The strategy used to order vertices inside each layer.
     * @param vertexPositioning The strategy used to position vertices on screen (x,y coordinates).
     */
    @Inject
    public AutomaticLayoutService(final CycleBreaker cycleBreaker,
                                  final VertexLayerer vertexLayerer,
                                  final VertexOrdering vertexOrdering,
                                  final VertexPositioning vertexPositioning) {
        this.cycleBreaker = cycleBreaker;
        this.vertexLayerer = vertexLayerer;
        this.vertexOrdering = vertexOrdering;
        this.vertexPositioning = vertexPositioning;
    }

    @SuppressWarnings("unused")
    public Layout createLayout(final Graph<?, ?> graph) {

        final HashMap<String, Node> indexByUuid = new HashMap<>();
        final ReorderedGraph reorderedGraph = new ReorderedGraph();

        for (final Node n : graph.nodes()) {

            if (!(n.getContent() instanceof HasBounds)) {
                continue;
            }

            indexByUuid.put(n.getUUID(), n);

            for (final Object e : n.getInEdges()) {

                final Edge edge = (Edge) e;

                final String from = edge.getSourceNode().getUUID();
                final String to = n.getUUID();
                reorderedGraph.addEdge(from, to);
            }

            for (final Object e : n.getOutEdges()) {

                final Edge edge = (Edge) e;

                final String to = edge.getTargetNode().getUUID();
                final String from = n.getUUID();
                reorderedGraph.addEdge(from, to);
            }
        }

        this.cycleBreaker.breakCycle(reorderedGraph);
        this.vertexLayerer.createLayers(reorderedGraph);
        this.vertexOrdering.orderVertices(reorderedGraph);
        this.vertexPositioning.calculateVerticesPositions(reorderedGraph,
                                                          DefaultVertexPositioning.LayerArrangement.BottomUp);

        final ArrayList<Layer> orderedLayers = reorderedGraph.getLayers();
        return buildLayout(indexByUuid, orderedLayers);
    }

    private Layout buildLayout(final HashMap<String, Node> indexByUuid,
                               final ArrayList<Layer> layers) {

        final Layout layout = new Layout();

        for (int i = layers.size() - 1; i >= 0; i--) {
            final Layer layer = layers.get(i);
            for (final Vertex v : layer.getVertices()) {
                final Node n = indexByUuid.get(v.getId());

                final int x = v.getX();
                final int y = v.getY();

                final Bounds currentBounds = ((HasBounds) n.getContent()).getBounds();
                final Bounds.Bound lowerRight = currentBounds.getLowerRight();
                final int x2;
                if (isCloseToZero(lowerRight.getX())) {
                    x2 = x + DefaultVertexPositioning.DEFAULT_VERTEX_WIDTH;
                } else {
                    x2 = (int) (x + lowerRight.getX());
                }

                final int y2;
                if (isCloseToZero(lowerRight.getY())) {
                    y2 = y + DefaultVertexPositioning.DEFAULT_VERTEX_HEIGHT;
                } else {
                    y2 = (int) (y + lowerRight.getY());
                }

                final NodePosition position = new NodePosition(v.getId(),
                                                               new NodePosition.Point(x, y),
                                                               new NodePosition.Point(x2, y2));

                layout.getNodePositions().add(position);
            }
        }

        return layout;
    }

    public boolean hasLayoutInformation(final Graph<?, ?> graph) {

        boolean hasLayoutInformation = false;

        for (final Node n : graph.nodes()) {

            final Object content = n.getContent();
            if (content instanceof HasBounds) {
                if (!isNullOrCloseToZero(((HasBounds) content).getBounds())) {
                    hasLayoutInformation = true;
                }
            }

            if (hasLayoutInformation) {
                break;
            }
        }

        return hasLayoutInformation;
    }

    private static boolean isNullOrCloseToZero(final Bounds bounds) {

        if (bounds == null) {
            return true;
        }

        final Bounds.Bound upperLeft = bounds.getUpperLeft();

        /* We're ignoring bottomRight because it used to define the size of the elements,
         * not the position on the diagram.*/
        return isCloseToZero(upperLeft.getX())
                && isCloseToZero(upperLeft.getY());
    }

    private static boolean isCloseToZero(final double value) {
        return Math.abs(value - 0) < closeToZeroTolerance;
    }

    public final class Layout {

        private final ArrayList<NodePosition> nodePositions;

        public Layout() {
            this.nodePositions = new ArrayList<>();
        }

        public ArrayList<NodePosition> getNodePositions() {
            return nodePositions;
        }
    }

    public static final class NodePosition {

        private final String nodeId;
        private final Point upperLeft;
        private final Point bottomRight;

        NodePosition(final String nodeId,
                     final Point upperLeft,
                     final Point bottomRight) {
            this.nodeId = nodeId;
            this.upperLeft = upperLeft;
            this.bottomRight = bottomRight;
        }

        public String getNodeId() {
            return this.nodeId;
        }

        public Point getUpperLeft() {
            return this.upperLeft;
        }

        public Point getBottomRight() {
            return this.bottomRight;
        }

        public static final class Point {

            private final int x;
            private final int y;

            private Point(final int x,
                          final int y) {
                this.x = x;
                this.y = y;
            }

            public int getX() {
                return x;
            }

            public int getY() {
                return y;
            }
        }
    }
}