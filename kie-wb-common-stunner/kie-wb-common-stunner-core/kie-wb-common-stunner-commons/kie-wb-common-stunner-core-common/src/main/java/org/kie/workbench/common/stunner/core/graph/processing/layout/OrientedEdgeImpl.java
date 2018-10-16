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

import java.util.Objects;

import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.OrientedEdge;

public class OrientedEdgeImpl implements OrientedEdge {

    private String from;
    private String to;
    private boolean isReversed;

    public OrientedEdgeImpl(final String from,
                            final String to) {
        this(from, to, false);
    }

    public OrientedEdgeImpl(final String from,
                            final String to,
                            final boolean isReversed) {
        this.from = from;
        this.to = to;
        this.isReversed = isReversed;
    }

    public String getFrom() {
        return this.from;
    }

    public String getTo() {
        return this.to;
    }

    /**
     * Checks if this edge is connected to the specified vertex.
     * @param vertexId The id of the vertex.
     * @return True if it is connect, false if it is not.
     */
    public boolean isLinkedWith(final String vertexId) {
        return this.getFrom().equals(vertexId) || this.getTo().equals(vertexId);
    }

    public boolean isReversed() {
        return isReversed;
    }

    public void reverse() {
        final String oldTo = this.to;
        this.to = this.from;
        this.from = oldTo;
        this.isReversed = !this.isReversed;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof OrientedEdgeImpl) {
            final OrientedEdgeImpl that = (OrientedEdgeImpl) obj;
            return Objects.equals(getTo(), that.getTo())
                    && Objects.equals(getFrom(), that.getFrom());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, isReversed);
    }
}