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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.core.graph.processing.layout.Edge;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Layer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.ReorderedGraph;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Vertex;

public class VertexOrdering {

    private final ReorderedGraph graph;
    private ArrayList<Layer> inputLayers;

    /**
     * Maximum number of iterations to perform.
     * 24 is the optimal number (Gansner et al 1993).
     */
    private final int MaxIterations = 24;

    public VertexOrdering(ReorderedGraph graph, ArrayList<Layer> layers) {
        this.graph = graph;
        this.inputLayers = layers;
    }

    public Ordered process() {

        ArrayList<Edge> edges = new ArrayList<>(Arrays.asList(this.graph.getEdges()));
        ArrayList<Layer> virtualized = createVirtual(edges);
        ArrayList<Layer> best = clone(virtualized);

        Object[][] nestedBestRanks = new Object[virtualized.size()][];
        // Starts with the current order
        for (int i = 0; i < nestedBestRanks.length; i++) {
            Layer layer = best.get(i);
            nestedBestRanks[i] = new Object[layer.getVertices().size()];
            for (int j = 0; j < layer.getVertices().size(); j++) {
                nestedBestRanks[i][j] = layer.getVertices().get(j);
            }
        }

        for (int i = 0; i < MaxIterations; i++) {
            median(virtualized, edges, i);
            transpose(virtualized, edges);
            if (crossing(best, edges) > crossing(virtualized, edges)) {
                best = clone(virtualized);
            } else {
                break;
            }
        }

        return new Ordered(best, edges);
    }

    /**
     * Transpose neighbouring vertices inside layers trying to reduce crossing.
     * @param layers The layers with vertices.
     * @param edges The edges connecting vertices.
     */
    private void transpose(ArrayList<Layer> layers,
                           ArrayList<Edge> edges) {

        boolean improved = true;

        while (improved) {
            improved = false;
            Layer current;
            Layer previous;
            for (int r = 1; r < layers.size(); r++) {
                current = layers.get(r);
                previous = layers.get(r - 1);
                ArrayList<Vertex> vertices = current.getVertices();

                for (int i = 1; i < vertices.size(); i++) {

                    int currentCrossing = crossing(edges, previous, current);

                    Collections.swap(vertices, i, i - 1);

                    int newCrossing = crossing(edges, previous, current);
                    if (newCrossing >= currentCrossing) {
                        Collections.swap(vertices, i - 1, i);
                    } else {
                        improved = true;
                    }
                }
            }
        }
    }

    public static Object[] flat(ArrayList<Edge> edges,
                                Layer north,
                                Layer south) {

        ArrayList<String> southPos = new ArrayList<>(south.getVertices().size());
        for (int i = 0; i < south.getVertices().size(); i++) {
            southPos.add(south.getVertices().get(i).getId());
        }

        Object[] southEntries = north.getVertices().stream().flatMap(v -> {
            List<Edge> connectedEdges = edges.stream()
                    .filter(e -> (e.getTo().equals(v.getId()) || e.getFrom().equals(v.getId())))
                    .collect(Collectors.toList());

            return connectedEdges.stream().map(e -> {
                if (southPos.contains(e.getTo())) {
                    return southPos.indexOf(e.getTo());
                }
                return southPos.indexOf(e.getFrom());
            }).sorted();
        }).toArray();

        return southEntries;
    }

    public static int crossing(ArrayList<Edge> edges,
                               Layer north,
                               Layer south) {

        final int DefaultVertexWeight = 1;

        Object[] southEntries = flat(edges, north, south);

        int firstIndex = 1;
        while (firstIndex < south.getVertices().size()) {
            firstIndex <<= 1;
        }
        int treeSize = 2 * firstIndex - 1;
        firstIndex -= 1;
        int[] tree = new int[treeSize];

        int crossings = 0;

        for (Object entry :
                southEntries) {
            int index = ((Integer) entry) + firstIndex;
            if (index < 0) {
                continue;
            }
            tree[index] += DefaultVertexWeight;
            int weightSum = 0;
            while (index > 0) {
                if (index % 2 != 0) {
                    weightSum += tree[index + 1];
                }
                index = (index - 1) >> 1;
                tree[index] += DefaultVertexWeight;
                ;
            }
            crossings += DefaultVertexWeight * weightSum;
        }

        return crossings;
    }

    public static int crossing(ArrayList<Layer> layers,
                               ArrayList<Edge> edges) {
        int crossingCount = 0;
        for (int i = 1; i < layers.size(); i++) {
            crossingCount += crossing(edges, layers.get(i - 1), layers.get(i));
        }
        return crossingCount;
    }

    public static void median(ArrayList<Layer> layers,
                              ArrayList<Edge> edges,
                              int i) {

        if ((i % 2 == 0)) {
            for (int j = layers.size() - 1; j >= 1; j--) {
                Layer currentLayer = layers.get(j);
                for (Vertex vertex :
                        currentLayer.getVertices()) {
                    //median value of vertices in rank r-1 connected to v
                    double median = calculateMedianOfVerticesConnectedTo(vertex.getId(), layers.get(j - 1), edges);
                    vertex.setMedian(median);
                }

                // sort the vertices inside layer based on the new order
                currentLayer.getVertices().sort(Vertex::compareTo);
            }
        } else {
            for (int j = 0; j < layers.size() - 1; j++) {
                Layer currentLayer = layers.get(j);

                for (Vertex vertex :
                        layers.get(j).getVertices()) {
                    double median = calculateMedianOfVerticesConnectedTo(vertex.getId(), layers.get(j + 1), edges);
                    vertex.setMedian(median);
                }

                currentLayer.getVertices().sort(Vertex::compareTo);
            }
        }
    }

    public static double calculateMedianOfVerticesConnectedTo(String vertex,
                                                              Layer layer,
                                                              ArrayList<Edge> edges) {
        ArrayList<Integer> connectedVerticesIndex = new ArrayList<>();
        ArrayList<Vertex> vertices = layer.getVertices();
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
            Optional<Vertex> first = layer.getVertices().stream().filter(v -> v.getId().equals(vertex)).findFirst();
            return first.map(vertex1 -> layer.getVertices().indexOf(vertex1)).orElse(-1);
        }

        if (size == 1) {
            return connectedVerticesIndex.get(0);
        }

        if (size % 2 == 0) {
            median = ((double) connectedVerticesIndex.get(size / 2) + (double) connectedVerticesIndex.get(size / 2 - 1)) / 2;
        } else {
            median = (double) connectedVerticesIndex.get(size / 2); // ??????
        }

        return median;
    }

    private ArrayList<Layer> clone(ArrayList<Layer> input) {
        ArrayList<Layer> clone = new ArrayList<>(input.size());
        for (Layer value :
                input) {
            clone.add(value.clone());
        }
        return clone;
    }

    public ArrayList<Layer> createVirtual(ArrayList<Edge> edges) {
        int virtualIndex = 0;
        ArrayList<Layer> virtualized = clone(inputLayers);

        for (int i = 0; i < virtualized.size() - 1; i++) {
            Layer currentLayer = virtualized.get(i);
            Layer nextLayer = virtualized.get(i + 1);
            for (Vertex vertex :
                    currentLayer.getVertices()) {

                List<Edge> outgoing = edges.stream()
                        .filter(e -> e.getFrom().equals(vertex.getId()))
                        .filter(e -> Math.abs(getLayerNumber(e.getTo(), virtualized) - getLayerNumber(vertex.getId(), virtualized)) > 1)
                        .collect(Collectors.toList());

                List<Edge> incoming = edges.stream()
                        .filter(e -> e.getTo().equals(vertex.getId()))
                        .filter(e -> Math.abs(getLayerNumber(e.getFrom(), virtualized) - getLayerNumber(vertex.getId(), virtualized)) > 1)
                        .collect(Collectors.toList());

                for (Edge edge :
                        outgoing) {
                    Vertex virtualVertex = new Vertex("V" + virtualIndex++, true);
                    nextLayer.getVertices().add(virtualVertex);
                    edges.remove(edge);
                    Edge v1 = new Edge(edge.getFrom(), virtualVertex.getId());
                    Edge v2 = new Edge(virtualVertex.getId(), edge.getTo());
                    edges.add(v1);
                    edges.add(v2);
                }

                for (Edge edge :
                        incoming) {
                    Vertex virtualVertex = new Vertex("V" + virtualIndex++, true);
                    nextLayer.getVertices().add(virtualVertex);
                    edges.remove(edge);
                    Edge v1 = new Edge(virtualVertex.getId(), edge.getTo());
                    Edge v2 = new Edge(edge.getFrom(), virtualVertex.getId());
                    edges.add(v1);
                    edges.add(v2);
                }
            }
        }

        return virtualized;
    }

    private int getLayerNumber(String vertex, ArrayList<Layer> layers) {
        Optional<Layer> layer = layers
                .stream()
                .filter(l -> l.getVertices().stream().anyMatch(v -> v.getId().equals(vertex)))
                .findFirst();

        return layer.get().getLevel();
    }

    public static final class Ordered {

        private final ArrayList<Layer> layers;
        private final ArrayList<Edge> edges;

        public Ordered(ArrayList<Layer> layers,
                       ArrayList<Edge> edges) {
            this.layers = layers;
            this.edges = edges;
        }

        public ArrayList<Layer> getLayers() {
            return layers;
        }

        public ArrayList<Edge> getEdges() {
            return edges;
        }
    }
}