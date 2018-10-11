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

package org.kie.workbench.common.stunner.core.graph.processing.layout.step04;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.inject.Default;

import org.kie.workbench.common.stunner.core.graph.processing.layout.Edge;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Layer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.ReorderedGraph;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Vertex;

/**
 * Calculate position for each vertex in a graph using the simplest approach.
 * 1. Vertices are horizontal distributed inside its layer, using the same space between each one
 * 2. All layers are vertical centered
 * 3. The space between layers is the same
 */
@Default
public final class DefaultVertexPositioning implements VertexPositioning {

    public static final int DEFAULT_VERTEX_WIDTH = 100;
    public static final int DEFAULT_VERTEX_HEIGHT = 50;
    private static final int DEFAULT_VERTEX_SPACE = 75;
    private static final int DEFAULT_LAYER_SPACE = 125;
    private static final int DEFAULT_LAYER_HORIZONTAL_PADDING = 50;
    private static final int DEFAULT_LAYER_VERTICAL_PADDING = 50;

    /*
     * Pre:
     * 1. De-reverse reversed layers
     * 2. Remove dummy vertices and reconnect each side
     */
    @Override
    public void calculateVerticesPositions(final ReorderedGraph graph,
                                           final LayerArrangement arrangement) {
        for (final Edge edge : graph.getEdges()) {
            if (edge.isReversed()) {
                edge.reverse();
            }
        }

        final Set<Vertex> vertices = graph.getLayers().stream()
                .flatMap(l -> l.getVertices().stream()).collect(Collectors.toSet());

        removeVirtualVertices(graph.getEdges(), vertices);
        removeVirtualVerticesFromLayers(graph.getLayers(), vertices);
        calculateVerticesPosition(graph.getLayers(), arrangement);
    }

    private void calculateVerticesPosition(final ArrayList<Layer> layers,
                                           final LayerArrangement arrangement) {

        final HashMap<Integer, Integer> layersWidth = new HashMap<>();

        int largestWidth = 0;
        for (int i = 0; i < layers.size(); i++) {
            final Layer layer = layers.get(i);
            int currentWidth = layer.getVertices().size() * DEFAULT_VERTEX_WIDTH;
            currentWidth += (layer.getVertices().size() - 1) * DEFAULT_VERTEX_SPACE;
            layersWidth.put(i, currentWidth);
            largestWidth = Math.max(largestWidth, currentWidth);
        }

        // center everything based on largest width
        final HashMap<Integer, Integer> layersStartX = new HashMap<>();
        for (int i = 0; i < layers.size(); i++) {
            final int middle = largestWidth / 2;
            final int layerWidth = layersWidth.get(i);
            final int firstHalf = layerWidth / 2;
            int startPoint = middle - firstHalf;
            startPoint += DEFAULT_LAYER_HORIZONTAL_PADDING;
            layersStartX.put(i, startPoint);
        }

        int y = DEFAULT_LAYER_VERTICAL_PADDING;
        switch (arrangement) {
            case TopDown:
                for (int i = 0; i < layers.size(); i++) {
                    y = distributeVertices(layers, layersStartX, y, i);
                }
                break;

            case BottomUp:
                for (int i = layers.size() - 1; i >= 0; i--) {
                    y = distributeVertices(layers, layersStartX, y, i);
                }
                break;
        }
    }

    private int distributeVertices(final ArrayList<Layer> layers,
                                   final HashMap<Integer, Integer> layersStartX,
                                   final int y,
                                   final int i) {

        final Layer layer = layers.get(i);
        int x = layersStartX.get(i);

        for (final Vertex v : layer.getVertices()) {

            v.setX(x);
            v.setY(y);

            x += DEFAULT_VERTEX_SPACE;
            x += DEFAULT_VERTEX_WIDTH;
        }

        return y + DEFAULT_LAYER_SPACE;
    }

    private void removeVirtualVerticesFromLayers(final ArrayList<Layer> layers,
                                                 final Set<Vertex> vertices) {
        final Set<String> ids = vertices.stream().map(Vertex::getId).collect(Collectors.toSet());
        for (final Layer layer : layers) {
            for (int i = 0; i < layer.getVertices().size(); i++) {
                final Vertex existingVertex = layer.getVertices().get(i);
                if (!ids.contains(existingVertex.getId())) {
                    layer.getVertices().remove(existingVertex);
                    i--;
                }
            }
        }
    }

    boolean removeVirtualVertex(final Edge edge,
                                final ArrayList<Edge> edges,
                                final Set<Vertex> vertices) {

        final Optional<Vertex> toVirtual = vertices.stream()
                .filter(v -> v.isVirtual() && edge.getTo().equals(v.getId())).findFirst();

        Edge newEdge = null;
        if (toVirtual.isPresent()) {
            final String virtualVertex = edge.getTo();
            // gets other side
            final Optional<Edge> otherSide = edges.stream()
                    .filter(e -> e.getFrom().equals(virtualVertex))
                    .findFirst();

            if (otherSide.isPresent()) {
                // this_vertex->virtual
                final String realToVertex = otherSide.get().getTo();
                newEdge = new Edge(edge.getFrom(), realToVertex);
                edges.remove(edge);
                vertices.remove(toVirtual.get());

                final Optional<Edge> oldEdge = edges.stream()
                        .filter(e -> e.getFrom().equals(toVirtual.get().getId()) && e.getTo().equals(realToVertex))
                        .findFirst();

                oldEdge.ifPresent(edges::remove);
            }
        }

        final Optional<Vertex> fromVirtual = vertices.stream()
                .filter(v -> v.isVirtual() && edge.getFrom().equals(v.getId())).findFirst();

        if (fromVirtual.isPresent()) {
            // virtual->this_vertex
            final String virtualVertex = edge.getFrom();

            final Optional<Edge> otherSide = edges.stream()
                    .filter(e -> (e.getTo().equals(virtualVertex)))
                    .findFirst();

            if (otherSide.isPresent()) {
                final String realFromVertex = otherSide.get().getFrom();
                if (newEdge == null) {
                    newEdge = new Edge(realFromVertex, edge.getTo());
                } else {
                    newEdge = new Edge(realFromVertex, newEdge.getTo());
                }

                edges.remove(edge);
                vertices.remove(fromVirtual.get());

                final Optional<Edge> oldEdge = edges.stream()
                        .filter(e -> e.getTo().equals(fromVirtual.get().getId()))
                        .findFirst();

                oldEdge.ifPresent(edges::remove);
            }
        }

        if (newEdge != null) {
            edges.add(newEdge);
            return true;
        }

        return false;
    }

    void removeVirtualVertices(final ArrayList<Edge> edges,
                               final Set<Vertex> vertices) {
        while (vertices.stream().anyMatch(Vertex::isVirtual)) {
            for (int i = 0; i < edges.size(); i++) {
                if (removeVirtualVertex(edges.get(i), edges, vertices)) {
                    i--;
                }
            }
        }
    }

    /**
     * How the layers should be draw.
     */
    public enum LayerArrangement {
        TopDown,
        BottomUp
    }
}