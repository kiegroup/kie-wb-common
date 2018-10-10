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
import java.util.Optional;

import javax.enterprise.inject.Default;

import org.kie.workbench.common.stunner.core.graph.processing.layout.Edge;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Layer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Vertex;

/**
 * Positions a vertex inside a layer using the median calc.
 */
@Default
public class MedianVertexLayerPositioning implements VertexLayerPositioning {

    /**
     * Position the vertices inside each layer using the median calc, putting each vertex V in layer L in the median
     * position based in vertices in layer L-1 connected to this vertex V.
     * @param layers The existing layers.
     * @param edges The existing edges.
     * @param currentIteration The current iteration of the execution. If is even goes up-down in layers, otherwise down-up.
     */
    public void positionVertices(final ArrayList<Layer> layers,
                                 final ArrayList<Edge> edges,
                                 final int currentIteration) {

        if ((currentIteration % 2 == 0)) {
            for (int j = layers.size() - 1; j >= 1; j--) {
                Layer currentLayer = layers.get(j);
                for (Vertex vertex : currentLayer.getVertices()) {
                    //positionVertices value of vertices in rank r-1 connected to v
                    double median = calculateMedianOfVerticesConnectedTo(vertex.getId(), layers.get(j - 1), edges);
                    vertex.setMedian(median);
                }

                // sort the vertices inside layer based on the new order
                currentLayer.getVertices().sort(Vertex::compareTo);
            }
        } else {
            for (int j = 0; j < layers.size() - 1; j++) {
                Layer currentLayer = layers.get(j);

                for (Vertex vertex : layers.get(j).getVertices()) {
                    double median = calculateMedianOfVerticesConnectedTo(vertex.getId(), layers.get(j + 1), edges);
                    vertex.setMedian(median);
                }

                currentLayer.getVertices().sort(Vertex::compareTo);
            }
        }
    }

    /**
     * Calculates the median position of a vertex connected to a neighborhood layer.
     * @param vertex The vertex.
     * @param layer The neighborhood layer.
     * @param edges The existing edges.
     * @return The median position. -1 (out of bounds) if there is no connection.
     */
    protected double calculateMedianOfVerticesConnectedTo(final String vertex,
                                                          final Layer layer,
                                                          final ArrayList<Edge> edges) {
        final ArrayList<Integer> connectedVerticesIndex = new ArrayList<>();
        final ArrayList<Vertex> vertices = layer.getVertices();
        for (int i = 0; i < vertices.size(); i++) {
            Vertex vertexInLayer = vertices.get(i);

            boolean hasConnection = edges.stream()
                    .anyMatch(e -> e.isLinkedWith(vertexInLayer.getId()) && e.isLinkedWith(vertex));

            if (hasConnection) {
                connectedVerticesIndex.add(i);
            }
        }

        double median;
        final int size = connectedVerticesIndex.size();

        if (size == 0) {
            final Optional<Vertex> first = layer.getVertices()
                    .stream()
                    .filter(v -> v.getId().equals(vertex))
                    .findFirst();
            return first.map(vertex1 -> layer.getVertices().indexOf(vertex1)).orElse(-1);
        }

        if (size == 1) {
            return connectedVerticesIndex.get(0);
        }

        if (size % 2 == 0) {
            median = ((double) connectedVerticesIndex.get(size / 2) + (double) connectedVerticesIndex.get(size / 2 - 1)) / 2;
        } else {
            median = (double) connectedVerticesIndex.get(size / 2);
        }

        return median;
    }
}
