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

package org.kie.workbench.common.stunner.core.graph.command.util;

import java.util.Objects;

import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.violations.RuleViolationImpl;

public class ConnectionCommandHelper {

    /**
     * Helper method for checking that a node is not referring to it self while adding an connection.
     * This is a temporal fix until we can model this with the actual rule system or a more advanced fix is provided at
     * the canvas level. For more information see JBPM-7532.
     *
     * @param sourceNode the source node for the connection.
     * @param targetNode the target node for the connection.
     * @return a list with the rules evaluation result.
     */
    public static CommandResult<RuleViolation> checkSelfReferenceViolations(final Node<? extends View<?>, Edge> sourceNode,
                                                                            final Node<? extends View<?>, Edge> targetNode) {
        final String sourceId = sourceNode != null ? sourceNode.getUUID() : null;
        final String targetId = targetNode != null ? targetNode.getUUID() : null;
        final GraphCommandResultBuilder resultBuilder = new GraphCommandResultBuilder();
        if (Objects.equals(sourceId, targetId)) {
            resultBuilder.addViolation(new RuleViolationImpl("A node can not be connected to itself "));
        }
        return resultBuilder.build();
    }
}
