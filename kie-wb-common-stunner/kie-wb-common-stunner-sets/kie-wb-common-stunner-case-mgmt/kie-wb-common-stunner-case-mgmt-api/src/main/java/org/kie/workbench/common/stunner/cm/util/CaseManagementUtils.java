/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.cm.util;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.UserTask;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

/**
 * Shared utils class for BPMN stuff.
 */
public class CaseManagementUtils {

    public static final double GAP = 50.0;
    public static final double STAGE_GAP = 11.0;
    public static final double ORIGIN_X = 50.0;
    public static final double ORIGIN_Y = 50.0;
    public static final double EVENT_WIDTH = 55.0;
    public static final double EVENT_HEIGHT = 55.0;
    public static final double STAGE_WIDTH = 175.0;
    public static final double STAGE_HEIGHT = 50.0;
    public static final double CHILD_WIDTH = 153.0;
    public static final double CHILD_HEIGHT = 103.0;

    /**
     * Finds the first Diagram in the graph structure.
     * @param graph The graph structure.
     */
    @SuppressWarnings("unchecked")
    public static Node<Definition<CaseManagementDiagram>, ?> getFirstDiagramNode(final Graph<?, Node> graph) {
        return GraphUtils.getFirstNode(graph,
                                       CaseManagementDiagram.class);
    }

    @SuppressWarnings("unchecked")
    public static int getGraphIndex(final Node parent,
                                    final Node child) {
        if (parent != null && child != null) {
            List<Edge> outEdges = parent.getOutEdges();
            if (null != outEdges && !outEdges.isEmpty()) {
                for (int i = 0, n = outEdges.size(); i < n; i++) {
                    if (child.equals(outEdges.get(i).getTargetNode())) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    public static int getCanvasChildIndex(final Node<View<?>, Edge> parent,
                                          final Node<View<?>, Edge> child) {
        if (parent != null && child != null) {
            List<Edge> outEdges = parent.getOutEdges();
            if (null != outEdges && !outEdges.isEmpty()) {
                Predicate<Node> predicate = (Objects.isNull(parent.getContent())
                        || !(parent.getContent().getDefinition() instanceof CaseManagementDiagram))
                        ? CaseManagementUtils::isSubStageNode : CaseManagementUtils::isStageNode;

                for (int i = 0, c = 0, n = outEdges.size(); i < n; i++) {
                    final Node cNode = outEdges.get(i).getTargetNode();
                    if (child.equals(cNode)) {
                        return c;
                    }

                    if (predicate.test(cNode)) {
                        c++;
                    }
                }
            }
        }
        return -1;
    }

    public static boolean isStage(final Node<View<?>, Edge> parent, final Node<View<?>, Edge> child) {
        return !Objects.isNull(parent.getContent())
                && parent.getContent().getDefinition() instanceof CaseManagementDiagram
                && isStageNode(child);
    }

    public static boolean isStageNode(final Node<View<?>, Edge> node) {
        if (node.getContent().getDefinition() instanceof AdHocSubprocess) {
            final List<Node> childNodes = node.getOutEdges().stream()
                    .filter(childPredicate())
                    .map(Edge::getTargetNode).collect(Collectors.toList());

            return childNodes.isEmpty()
                    || childNodes.stream().allMatch(CaseManagementUtils::isSubStageNode);
        }

        return false;
    }

    public static boolean isSubStageNode(final Node<View<?>, Edge> node) {
        return node.getContent().getDefinition() instanceof UserTask
                || node.getContent().getDefinition() instanceof ReusableSubprocess;
    }

    public static Predicate<Edge> childPredicate() {
        return edge -> edge.getContent() instanceof Child;
    }

    public static Predicate<Edge> sequencePredicate() {
        return edge -> edge.getContent() instanceof ViewConnector
                && ((ViewConnector) edge.getContent()).getDefinition() instanceof SequenceFlow;
    }

    @SuppressWarnings("unchecked")
    public static int getCanvasNewChildIndex(final Node<View<?>, Edge> parent) {
        Predicate<Node> predicate = Objects.isNull(parent.getContent())
                || !(parent.getContent().getDefinition() instanceof CaseManagementDiagram)
                ? CaseManagementUtils::isSubStageNode : CaseManagementUtils::isStageNode;

        return (int) parent.getOutEdges().stream()
                .filter(childPredicate())
                .filter(edge -> predicate.test(edge.getTargetNode())).count();
    }

    @SuppressWarnings("unchecked")
    public static int getGraphNewChildIndex(final Node<View<?>, Edge> parent) {
        if (Objects.isNull(parent.getContent()) ||
                !(parent.getContent().getDefinition() instanceof CaseManagementDiagram)) {
            return parent.getOutEdges().size();
        }

        List<Node<View<?>, Edge>> childNodes = parent.getOutEdges().stream()
                .map(edge -> (Node<View<?>, Edge>) edge.getTargetNode())
                .collect(Collectors.toList());

        for (int n = childNodes.size(), i = n - 1; i >= 0; i--) {
            Node<View<?>, Edge> node = childNodes.get(i);
            if (isStageNode(node)
                    || node.getContent().getDefinition() instanceof StartNoneEvent) {
                return i + 1;
            }
        }

        return 0;
    }
}
