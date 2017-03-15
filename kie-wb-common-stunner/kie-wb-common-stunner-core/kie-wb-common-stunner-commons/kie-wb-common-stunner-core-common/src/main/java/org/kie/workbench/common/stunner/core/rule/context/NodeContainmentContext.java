/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.rule.context;

import java.util.Optional;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.rule.handler.impl.NodeContainmentEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.impl.CanContain;

/**
 * This rule evaluation context provides the runtime information
 * that allows the evaluation for parent-child related operations
 * in a graph structure.
 * @See {@link CanContain}
 * @See {@link NodeContainmentEvaluationHandler}
 */
public interface NodeContainmentContext extends GraphEvaluationContext {

    /**
     * The candidate's parent. If not candidate parent elements
     * the parent's graph instance is used as parent argument.
     */
    Optional<Element<? extends Definition<?>>> getParent();

    /**
     * The candidate instance to be set as a child for the parent one.
     */
    Node<? extends Definition<?>, ? extends Edge> getCandidate();
}
