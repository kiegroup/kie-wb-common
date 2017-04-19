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

package org.kie.workbench.common.stunner.core.graph.processing.traverse.tree;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import javax.enterprise.context.Dependent;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.uberfire.mvp.Command;

@Dependent
public final class TreeWalkTraverseProcessorImpl implements TreeWalkTraverseProcessor {

    private Graph graph;
    private EdgeVisitorPolicy edgeVisitorPolicy;
    private TreeTraverseCallback<Graph, Node, Edge> callback;
    private final Set<String> processesEdges = new HashSet<String>();
    private final Set<String> processesNodes = new HashSet<String>();
    private Predicate<Node<?, Edge>> startNodePredicate;

    public TreeWalkTraverseProcessorImpl() {
        this.edgeVisitorPolicy = EdgeVisitorPolicy.VISIT_EDGE_BEFORE_TARGET_NODE;
        this.startNodePredicate = n -> n.getInEdges().isEmpty();
    }

    @Override
    public TreeWalkTraverseProcessorImpl useEdgeVisitorPolicy(final EdgeVisitorPolicy policy) {
        this.edgeVisitorPolicy = policy;
        return this;
    }

    @Override
    public TreeWalkTraverseProcessorImpl useStartNodePredicate(final Predicate<Node<?, Edge>> predicate) {
        this.startNodePredicate = predicate;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void traverse(final Graph graph,
                         final Node node,
                         final TreeTraverseCallback<Graph, Node, Edge> callback) {
        this.doTraverse(graph,
                        Optional.ofNullable(node),
                        callback);
    }

    @Override
    public void traverse(final Graph graph,
                         final TreeTraverseCallback<Graph, Node, Edge> callback) {
        this.doTraverse(graph,
                        Optional.empty(),
                        callback);
    }

    private void doTraverse(final Graph graph,
                            final Optional<Node<?, Edge>> node,
                            final TreeTraverseCallback<Graph, Node, Edge> callback) {
        this.graph = graph;
        this.callback = callback;
        processesNodes.clear();
        processesEdges.clear();
        startTraverse(node);
    }

    private void startTraverse(final Optional<Node<?, Edge>> startNode) {
        startGraphTraversal(startNode);
        endGraphTraversal();
    }

    private void endGraphTraversal() {
        callback.endGraphTraversal();
        this.graph = null;
        this.callback = null;
        this.processesEdges.clear();
        this.processesNodes.clear();
    }

    @SuppressWarnings("unchecked")
    private void startGraphTraversal(final Optional<Node<?, Edge>> startNode) {
        assert graph != null && callback != null;
        callback.startGraphTraversal(graph);
        if (!startNode.isPresent()) {
            final List<Node<?, Edge>> orderedGraphNodes = getStartingNodes();
            for (final Node<?, Edge> node : orderedGraphNodes) {
                ifNotProcessed(node,
                               () -> startNodeTraversal(node));
            }
        } else {
            startNodeTraversal(startNode.get());
        }
    }

    @SuppressWarnings("unchecked")
    private void startNodeTraversal(final Node<?, Edge> node) {
        this.processesNodes.add(node.getUUID());
        if (callback.startNodeTraversal(node)) {
            node.getOutEdges().forEach(this::startEdgeTraversal);
            callback.endNodeTraversal(node);
        }
    }

    @SuppressWarnings("unchecked")
    private void startEdgeTraversal(final Edge edge) {
        final String uuid = edge.getUUID();
        if (!this.processesEdges.contains(uuid)) {
            processesEdges.add(uuid);
            boolean isTraverNode = true;
            if (isVisitBefore()) {
                isTraverNode = callback.startEdgeTraversal(edge);
            }
            if (isTraverNode) {
                ifNotProcessed(edge.getTargetNode(),
                               () -> startNodeTraversal(edge.getTargetNode()));
            }
            if (isVisitAfter()) {
                callback.startEdgeTraversal(edge);
            }
            callback.endEdgeTraversal(edge);
        }
    }

    private boolean isVisitBefore() {
        return EdgeVisitorPolicy.VISIT_EDGE_BEFORE_TARGET_NODE.equals(edgeVisitorPolicy);
    }

    private boolean isVisitAfter() {
        return !isVisitBefore();
    }

    private void ifNotProcessed(final Node node,
                                final Command action) {
        if (null != node && !processesNodes.contains(node.getUUID())) {
            action.execute();
        }
    }

    @SuppressWarnings("unchecked")
    private List<Node<?, Edge>> getStartingNodes() {
        final Iterable<Node> nodes = graph.nodes();
        final List<Node<?, Edge>> result = new LinkedList<>();
        nodes.forEach(n -> {
            if (isStartingNode(n)) {
                result.add(n);
            }
        });
        return result;
    }

    @SuppressWarnings("unchecked")
    private boolean isStartingNode(final Node node) {
        return null == node.getInEdges() || startNodePredicate.test(node);
    }
}
