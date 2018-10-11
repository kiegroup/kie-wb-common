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
import java.util.Collections;

import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.graph.processing.layout.Edge;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Layer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Vertex;

/**
 * Transpose vertices inside a layer.
 */
public class VerticesTransposer {

    private final LayerCrossingCount crossingCount;

    @Inject
    public VerticesTransposer(final LayerCrossingCount crossingCount) {
        this.crossingCount = crossingCount;
    }

    /**
     * Transpose neighbouring vertices inside layers trying to reduce crossing.
     * @param layers The layers with vertices.
     * @param edges The edges connecting vertices.
     */
    void transpose(final ArrayList<Layer> layers,
                   final ArrayList<Edge> edges,
                   final int currentIteration) {

        boolean improved = true;
        final boolean bottomUp = (currentIteration % 2) == 0;

        while (improved) {
            improved = false;

            if (bottomUp) {
                for (int index = layers.size() - 1; index > 0; index--) {
                    final Layer current;
                    final Layer previous;
                    current = layers.get(index - 1);
                    previous = layers.get(index);
                    improved = doTranspose(current, previous, edges);
                }
            } else {
                for (int index = 1; index < layers.size(); index++) {
                    final Layer current;
                    final Layer previous;
                    current = layers.get(index);
                    previous = layers.get(index - 1);
                    improved = doTranspose(current, previous, edges);
                }
            }
        }
    }

    private boolean doTranspose(final Layer current,
                                final Layer previous,
                                final ArrayList<Edge> edges) {

        final ArrayList<Vertex> vertices = current.getVertices();
        boolean improved = false;
        for (int i = 1; i < vertices.size(); i++) {

            final int currentCrossing = this.crossingCount.crossing(edges, previous, current);

            Collections.swap(vertices, i, i - 1);

            final int newCrossing = this.crossingCount.crossing(edges, previous, current);
            if (newCrossing >= currentCrossing) {
                Collections.swap(vertices, i - 1, i);
            } else {
                improved = true;
            }
        }
        return improved;
    }
}
