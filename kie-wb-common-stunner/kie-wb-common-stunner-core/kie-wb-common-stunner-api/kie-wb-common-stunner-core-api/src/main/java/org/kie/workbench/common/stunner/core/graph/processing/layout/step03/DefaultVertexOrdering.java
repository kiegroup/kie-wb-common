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

package org.kie.workbench.common.stunner.core.graph.processing.layout.step03;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.graph.processing.layout.Edge;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Layer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.ReorderedGraph;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Vertex;

import static java.util.stream.Collectors.toList;

/**
 * Order vertices inside layers trying to reduce crossing between edges.
 */
@Default
public final class DefaultVertexOrdering implements VertexOrdering {

    private final VertexLayerPositioning vertexPositioning;
    private final LayerCrossingCount crossingCount;
    private final VerticesTransposer verticesTransposer;

    /**
     * Maximum number of iterations to perform.
     * 24 is the optimal number (Gansner et al 1993).
     */
    private static final int MAX_ITERATIONS = 24;

    /**
     * Default constructor.
     * @param vertexPositioning The strategy to find the position of the vertices inside a layer.
     * @param crossingCount The strategy to count the edges crossing.
     * @param verticesTransposer The strategy to transpose vertices in a layer.
     */
    @Inject
    public DefaultVertexOrdering(final VertexLayerPositioning vertexPositioning,
                                 final LayerCrossingCount crossingCount,
                                 final VerticesTransposer verticesTransposer) {
        this.vertexPositioning = vertexPositioning;
        this.crossingCount = crossingCount;
        this.verticesTransposer = verticesTransposer;
    }

    /**
     * Reorder the vertices to reduce edges crossing.
     * @param graph The graph.
     */
    @Override
    public void orderVertices(final ReorderedGraph graph) {
        final ArrayList<Edge> edges = graph.getEdges();
        final ArrayList<Layer> virtualized = createVirtual(edges, graph);
        ArrayList<Layer> best = clone(virtualized);

        final Object[][] nestedBestRanks = new Object[virtualized.size()][];
        // Starts with the current order
        for (int i = 0; i < nestedBestRanks.length; i++) {
            final Layer layer = best.get(i);
            nestedBestRanks[i] = new Object[layer.getVertices().size()];
            for (int j = 0; j < layer.getVertices().size(); j++) {
                nestedBestRanks[i][j] = layer.getVertices().get(j);
            }
        }

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            this.vertexPositioning.positionVertices(virtualized, edges, i);
            this.verticesTransposer.transpose(virtualized, edges, i);
            if (this.crossingCount.crossing(best, edges) > this.crossingCount.crossing(virtualized, edges)) {
                best = clone(virtualized);
            } else {
                break;
            }
        }

        graph.getLayers().clear();
        graph.getLayers().addAll(best);
    }

    private ArrayList<Layer> clone(final ArrayList<Layer> input) {
        final ArrayList<Layer> clone = new ArrayList<>(input.size());
        for (final Layer value : input) {
            clone.add(value.clone());
        }
        return clone;
    }

    /**
     * Creates virtual vertices in edges that crosses multiple layers.
     * @param edges The existing edges.
     * @param graph The graph.
     * @return The layers with virtual vertices.
     */
    private ArrayList<Layer> createVirtual(final ArrayList<Edge> edges,
                                           final ReorderedGraph graph) {
        int virtualIndex = 0;
        final ArrayList<Layer> virtualized = clone(graph.getLayers());

        for (int i = 0; i < virtualized.size() - 1; i++) {
            final Layer currentLayer = virtualized.get(i);
            final Layer nextLayer = virtualized.get(i + 1);
            for (final Vertex vertex : currentLayer.getVertices()) {

                final List<Edge> outgoing = edges.stream()
                        .filter(e -> e.getFrom().equals(vertex.getId()))
                        .filter(e -> Math.abs(getLayerNumber(e.getTo(), virtualized) - getLayerNumber(vertex.getId(), virtualized)) > 1)
                        .collect(toList());

                final List<Edge> incoming = edges.stream()
                        .filter(e -> e.getTo().equals(vertex.getId()))
                        .filter(e -> Math.abs(getLayerNumber(e.getFrom(), virtualized) - getLayerNumber(vertex.getId(), virtualized)) > 1)
                        .collect(toList());

                for (final Edge edge : outgoing) {
                    final Vertex virtualVertex = new Vertex("V" + virtualIndex++, true);
                    nextLayer.getVertices().add(virtualVertex);
                    edges.remove(edge);
                    final Edge v1 = new Edge(edge.getFrom(), virtualVertex.getId());
                    final Edge v2 = new Edge(virtualVertex.getId(), edge.getTo());
                    edges.add(v1);
                    edges.add(v2);
                }

                for (final Edge edge : incoming) {
                    final Vertex virtualVertex = new Vertex("V" + virtualIndex++, true);
                    nextLayer.getVertices().add(virtualVertex);
                    edges.remove(edge);
                    final Edge v1 = new Edge(virtualVertex.getId(), edge.getTo());
                    final Edge v2 = new Edge(edge.getFrom(), virtualVertex.getId());
                    edges.add(v1);
                    edges.add(v2);
                }
            }
        }

        return virtualized;
    }

    private int getLayerNumber(final String vertex,
                               final ArrayList<Layer> layers) {
        final Layer layer = layers
                .stream()
                .filter(l -> l.getVertices().stream().anyMatch(v -> v.getId().equals(vertex)))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Can not found the layer of the vertex."));

        return layer.getLevel();
    }
}