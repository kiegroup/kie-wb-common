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
import java.util.HashSet;

public final class ReorderedGraph {

    private final ArrayList<String> vertices;
    private final ArrayList<Edge> edges;
    private final ArrayList<Layer> layers;

    public ReorderedGraph() {
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.layers = new ArrayList<>();
    }

    public ReorderedGraph(final String[][] edgesMatrix) {
        this();
        for (int i = 0; i < edgesMatrix.length; i++) {
            addEdge(edgesMatrix[i][0], edgesMatrix[i][1]);
        }
    }

    public void addEdge(final String from,
                        final String to) {
        addEdge(new Edge(from, to));
    }

    public void addEdge(final Edge edge) {
        if (!this.edges.contains(edge)) {
            this.edges.add(edge);
        }

        if (!this.vertices.contains(edge.getFrom())) {
            this.vertices.add(edge.getFrom());
        }

        if (!this.vertices.contains(edge.getTo())) {
            this.vertices.add(edge.getTo());
        }
    }

    public ArrayList<Layer> getLayers() {
        return this.layers;
    }

    public ArrayList<String> getVertices() {
        return this.vertices;
    }

    public ArrayList<Edge> getEdges() {
        return this.edges;
    }

    public boolean isAcyclic() {
        final HashSet<String> visited = new HashSet<>();
        for (String vertex : this.vertices) {
            if (leadsToACycle(vertex, visited)) {
                return false;
            }
        }
        return true;
    }

    private boolean leadsToACycle(final String vertex,
                                  final HashSet<String> visited) {
        if (visited.contains(vertex)) {
            return true;
        }

        visited.add(vertex);

        final String[] verticesFromThis = getVerticesFrom(vertex);
        for (String nextVertex : verticesFromThis) {
            if (leadsToACycle(nextVertex, visited)) {
                return true;
            }
        }

        visited.remove(vertex);
        return false;
    }

    public String[] getVerticesFrom(final String vertex) {
        final HashSet<String> verticesFrom = new HashSet<>();
        for (Edge edge : this.edges) {
            if (edge.getFrom().equals(vertex)) {
                verticesFrom.add(edge.getTo());
            }
        }
        return verticesFrom.toArray(new String[0]);
    }
}