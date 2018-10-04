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

package org.kie.workbench.common.stunner.core.backend.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.HasBounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Layer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.ReorderedGraph;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Vertex;
import org.kie.workbench.common.stunner.core.graph.processing.layout.step01.CycleBreaker;
import org.kie.workbench.common.stunner.core.graph.processing.layout.step02.LongestPathVertexLayerer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.step03.VertexOrdering;
import org.kie.workbench.common.stunner.core.graph.processing.layout.step04.VertexPositioning;

public class AutomaticLayoutService {

    public static void applyLayout(final Graph<DefinitionSet, ?> graph) {

        if (hasLayoutInformation(graph)) {
            return;
        }

        HashMap<String, Node> indexByUuid = new HashMap<>();
        ReorderedGraph reorderedGraph = new ReorderedGraph();

        for (Node n :
                graph.nodes()) {

            if (!(n.getContent() instanceof HasBounds)) {
                continue;
            }

            indexByUuid.put(n.getUUID(), n);

            for (Object e :
                    n.getInEdges()) {

                Edge edge = (Edge) e;

                String from = edge.getSourceNode().getUUID();
                String to = n.getUUID();
                reorderedGraph.addEdge(from, to);
            }

            for (Object e :
                    n.getOutEdges()) {

                Edge edge = (Edge) e;

                String to = edge.getTargetNode().getUUID();
                String from = n.getUUID();
                reorderedGraph.addEdge(from, to);
            }
        }

        CycleBreaker cycleBreaker = new CycleBreaker(reorderedGraph);
        ReorderedGraph acyclic = cycleBreaker.breakCycle();
        LongestPathVertexLayerer layerer = new LongestPathVertexLayerer(acyclic);
        ArrayList<Layer> layers = layerer.execute();
        VertexOrdering vertexOrdering = new VertexOrdering(acyclic, layers);
        VertexOrdering.Ordered ordered = vertexOrdering.process();

        VertexPositioning vertexPositioning = new VertexPositioning();
        vertexPositioning.execute(ordered.getLayers(),
                                  ordered.getEdges(),
                                  VertexPositioning.LayerArrangement.BottomUp);

        ArrayList<Layer> layers1 = ordered.getLayers();
        for (int i = layers1.size() - 1; i >= 0; i--) {
            Layer layer = layers1.get(i);
            for (Vertex v :
                    layer.getVertices()) {
                Node n = indexByUuid.get(v.getId());

                int x = v.getX();
                int y = v.getY();

                Bounds currentBounds = ((HasBounds) n.getContent()).getBounds();
                Bounds.Bound lowerRight = currentBounds.getLowerRight();
                int x2;
                if (isCloseToZero(lowerRight.getX())) {
                    x2 = x + VertexPositioning.DefaultVertexWidth;
                } else {
                    x2 = (int) (x + lowerRight.getX());
                }

                int y2;
                if (isCloseToZero(lowerRight.getY())) {
                    y2 = y + VertexPositioning.DefaultVertexHeight;
                } else {
                    y2 = (int) (y + lowerRight.getY());
                }

                ((HasBounds) n.getContent()).setBounds(BoundsImpl.build(
                        x, y, x2, y2
                ));
            }
        }
    }

    private static boolean hasLayoutInformation(final Graph<DefinitionSet, ?> graph) {

        boolean hasLayoutInformation = false;

        for (Node n :
                graph.nodes()) {

            Object content = n.getContent();
            if (content instanceof HasBounds) {
                HasBounds hasBounds = (HasBounds) content;
                if (!isNullOrEmpty(hasBounds.getBounds())) {
                    hasLayoutInformation = true;
                }
            }

            if (hasLayoutInformation) {
                break;
            }
        }

        return hasLayoutInformation;
    }

    private static boolean isNullOrEmpty(final Bounds bounds) {

        if (bounds == null) {
            return true;
        }

        Bounds.Bound upperLeft = bounds.getUpperLeft();

        /* We're ignoring bottomRight because it used to define the size of the elements,
         * not the position on the diagram.*/
        return isCloseToZero(upperLeft.getX())
                && isCloseToZero(upperLeft.getY());
    }

    private static boolean isCloseToZero(final double value) {
        double tolerance = 0.1;
        return Math.abs(value - 0) < tolerance;
    }
}