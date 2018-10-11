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
import java.util.stream.Collectors;

public final class Layer {

    private int level;
    private final ArrayList<Vertex> vertices;

    public Layer(final int level) {
        this();
        this.level = level;
    }

    public Layer() {
        this.vertices = new ArrayList<>();
    }

    public void addVertex(final Vertex vertex) {
        this.vertices.add(vertex);
    }

    public ArrayList<Vertex> getVertices() {
        return this.vertices;
    }

    public void setLevel(final int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void addNewVertex(final String vertexId) {
        this.vertices.add(new Vertex(vertexId));
    }

    public Layer clone() {
        final Layer clone = new Layer(this.level);
        final ArrayList<Vertex> cloneVertices = clone.getVertices();
        for (final Vertex v : this.vertices) {
            cloneVertices.add(v.clone());
        }
        return clone;
    }

    @Override
    public String toString() {
        return vertices.stream()
                .map(Vertex::getId)
                .collect(Collectors.joining(", ", "LAYER " + this.level + " [", "]"));
    }
}