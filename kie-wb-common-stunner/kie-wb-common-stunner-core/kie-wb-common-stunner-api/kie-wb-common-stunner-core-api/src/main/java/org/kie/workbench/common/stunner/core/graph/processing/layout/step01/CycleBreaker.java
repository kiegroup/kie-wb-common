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

package org.kie.workbench.common.stunner.core.graph.processing.layout.step01;

import java.util.HashSet;

import org.kie.workbench.common.stunner.core.graph.processing.layout.Edge;
import org.kie.workbench.common.stunner.core.graph.processing.layout.ReorderedGraph;

public final class CycleBreaker {

    private final HashSet<String> vertices;
    private final HashSet<Edge> edges;
    private final HashSet<String> visitedVertices;
    private final HashSet<Edge> reversed;

    public CycleBreaker(final ReorderedGraph graph) {

        this.reversed = new HashSet<>();
        this.vertices = new HashSet<>();
        this.edges = new HashSet<>();
        this.visitedVertices = new HashSet<>();

        for (Edge edge :
                graph.getEdges()) {
            this.edges.add(edge);
        }

        for (String vertex :
                graph.getVertices()) {
            this.vertices.add(vertex);
        }
    }

    public ReorderedGraph breakCycle() {

        for (String vertex :
                this.vertices) {
            visit(vertex);
        }

        ReorderedGraph g = new ReorderedGraph();
        for (Edge e :
                edges) {
            g.addEdge(e);
        }

        return g;
    }

    private boolean visit(final String vertex) {
        if (visitedVertices.contains(vertex)) {
            // Found a cycle.
            return false;
        }
        visitedVertices.add(vertex);

        String[] verticesFromThis = getVerticesFrom(vertex);
        for (String nextVertex :
                verticesFromThis) {
            if (!visit(nextVertex)) {
                // break
                Edge toReverse = edges.stream().filter(edge -> edge.getFrom().equals(vertex)
                        && edge.getTo().equals(nextVertex))
                        .findFirst()
                        .orElse(null);

                if (toReverse != null) {
                    reversed.add(toReverse);
                    edges.remove(toReverse);
                    Edge reversed = new Edge(toReverse.getTo(), toReverse.getFrom());
                    edges.add(reversed);
                }
            }
        }

        visitedVertices.remove(vertex);
        return true;
    }

    public String[] getVerticesFrom(final String vertex) {
        HashSet<String> verticesFrom = new HashSet<>();
        for (Edge edge :
                this.edges) {
            if (edge.getFrom().equals(vertex)) {
                verticesFrom.add(edge.getTo());
            }
        }
        return verticesFrom.toArray(new String[0]);
    }
}