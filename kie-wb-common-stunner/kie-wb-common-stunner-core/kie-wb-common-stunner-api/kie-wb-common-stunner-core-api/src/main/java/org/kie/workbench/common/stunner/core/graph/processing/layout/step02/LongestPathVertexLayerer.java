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

package org.kie.workbench.common.stunner.core.graph.processing.layout.step02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

import org.kie.workbench.common.stunner.core.graph.processing.layout.Layer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.ReorderedGraph;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Vertex;

/**
 * Assign each vertex in a graph to a layers, using the longest path algorithm.
 */
public final class LongestPathVertexLayerer {

    private final Vertex[] vertices;
    private final HashMap<String, Integer> vertexHeight;
    private final ReorderedGraph graph;
    private final ArrayList<Layer> layers;

    public LongestPathVertexLayerer(final ReorderedGraph graph) {

        String[] graphVertices = graph.getVertices();

        this.layers = new ArrayList<>();
        this.graph = graph;
        this.vertices = new Vertex[graphVertices.length];
        this.vertexHeight = new HashMap<>();

        for (int i = 0; i < graphVertices.length; i++) {
            String v = graphVertices[i];
            this.vertices[i] = new Vertex(v);
            this.vertexHeight.put(v, -1);
        }
    }

    public ArrayList<Layer> execute() {

        for (Vertex vertex :
                this.vertices) {
            visit(vertex);
        }

        return this.layers;
    }

    private int visit(final Vertex vertex) {
        int height = this.vertexHeight.get(vertex.getId());
        if (height >= 0) {
            return height;
        }

        int maxHeight = 1;

        String[] verticesFromHere = graph.getVerticesFrom(vertex.getId());
        for (String nextVertex :
                verticesFromHere) {
            if(!nextVertex.equals(vertex.getId())){
                Optional<Vertex> next = Arrays.stream(this.vertices)
                        .filter(f -> f.getId().equals(nextVertex))
                        .findFirst();
                int targetHeight = visit(next.get());
                maxHeight = Math.max(maxHeight, targetHeight+1);
            }
        }

        addToLayer(vertex, maxHeight);
        return maxHeight;
    }

    private void addToLayer(final Vertex vertex, final int height) {
        for(int i = this.layers.size(); i <height; i++){
            layers.add(0, new Layer());
        }

        int level = layers.size() - height;
        Layer layer = layers.get(level);
        layer.setLevel(height);
        layer.addVertex(vertex);
        vertexHeight.put(vertex.getId(), height);
    }
}