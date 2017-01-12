/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.graph.processing.index.labeled;

import java.util.Collection;
import java.util.List;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;

/**
 * <p>A graph index for labeled elements.</p>
 */
public interface LabeledIndex<N extends Node, E extends Edge> extends Index<N, E> {

    /**
     * Returns the nodes with the given labels.
     */
    Collection<N> findNodes( final List<String> labels );

    /**
     * Returns the edges with the given labels.
     */
    Collection<E> findEdges( final List<String> labels );
}
