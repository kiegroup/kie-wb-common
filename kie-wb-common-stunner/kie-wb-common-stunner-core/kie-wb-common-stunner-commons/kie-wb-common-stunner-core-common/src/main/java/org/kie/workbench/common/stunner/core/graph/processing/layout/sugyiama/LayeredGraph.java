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

package org.kie.workbench.common.stunner.core.graph.processing.layout.sugyiama;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.kie.workbench.common.stunner.core.graph.processing.layout.LayoutService;
import org.kie.workbench.common.stunner.core.graph.processing.layout.OrientedEdgeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.layout.ReorderedGraph;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.GraphLayer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.OrientedEdge;

/**
 * A graph that can be reordered in order to reduce edge crossing.
 * @see LayoutService
 */
public final class LayeredGraph implements ReorderedGraph {

    private final List<String> vertices;
    private final List<OrientedEdge> edges;
    private final List<GraphLayer> layers;

    /**
     * Default constructor.
     */
    public LayeredGraph() {
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.layers = new ArrayList<>();
    }

    /**
     * Constructor.
     * @param edgesMatrix Each row is an edge, each column is a vertex.
     */
    public LayeredGraph(final String[][] edgesMatrix) {
        this();
        for (final String[] edge : edgesMatrix) {
            addEdge(edge[0], edge[1]);
        }
    }

    public void addEdge(final String from,
                        final String to) {
        addEdge(new OrientedEdgeImpl(from, to));
    }

    public void addEdge(final OrientedEdgeImpl edge) {
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

    public List<GraphLayer> getLayers() {
        return this.layers;
    }

    public List<String> getVertices() {
        return this.vertices;
    }

    public List<OrientedEdge> getEdges() {
        return this.edges;
    }

    public boolean isAcyclic() {
        final HashSet<String> visited = new HashSet<>();
        for (final String vertex : this.vertices) {
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
        for (final String nextVertex : verticesFromThis) {
            if (leadsToACycle(nextVertex, visited)) {
                return true;
            }
        }

        visited.remove(vertex);
        return false;
    }

    public String[] getVerticesFrom(final String vertex) {
        final HashSet<String> verticesFrom = new HashSet<>();
        for (final OrientedEdge edge : this.edges) {
            if (Objects.equals(edge.getFrom(), vertex)) {
                verticesFrom.add(edge.getTo());
            }
        }
        return verticesFrom.toArray(new String[0]);
    }
}