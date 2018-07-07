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
package org.kie.workbench.common.stunner.core.client.canvas.command;

import java.util.List;
import java.util.Optional;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.EdgeShape;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.util.ShapeUtils;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Relationship;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

public class MorphCanvasNodeCommand extends AbstractCanvasCommand {

    private Node<? extends Definition<?>, Edge> candidate;
    private MorphDefinition morphDefinition;
    private String shapeSetId;

    public MorphCanvasNodeCommand(final Node<? extends Definition<?>, Edge> candidate,
                                  final MorphDefinition morphDefinition,
                                  final String shapeSetId) {
        this.candidate = candidate;
        this.morphDefinition = morphDefinition;
        this.shapeSetId = shapeSetId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> builder = new CompositeCommand.Builder<>();
        // Deregister the existing shape.
        Node parent = getParent();
        if (null != parent) {
            context.removeChild(parent,
                                candidate);
        }
        context.deregister(candidate);

        // Register the shape for the new morphed element.
        context.register(shapeSetId,
                         candidate);
        if (null != parent) {
            context.addChild(parent,
                             candidate);
        }
        context.applyElementMutation(candidate,
                                     MutationContext.STATIC);

        updateEdges(context, candidate);

        GraphUtils.getDockParent(candidate).ifPresent(dockParent-> {
            builder.addCommand(new CanvasDockNodeCommand(dockParent , candidate));
        });

        return builder.build().execute(context);
    }

    private void updateEdges(AbstractCanvasHandler context, Node<? extends Definition<?>, Edge> candidate) {
        // Update incoming edges for the new shape
        Optional.ofNullable(candidate.getInEdges())
                .ifPresent(edges -> edges.stream()
                        .filter(this::isViewEdge)
                        .forEach(edge -> updateConnections(context, edge, edge.getSourceNode(), candidate)));

        // Update outgoing edges for the new shape.
        Optional.ofNullable(candidate.getOutEdges())
                .ifPresent(edges -> edges.stream()
                        .forEach(edge -> {
                            if (isViewEdge(edge)) {
                                updateConnections(context, edge, candidate, edge.getTargetNode());
                            } else if (edge.getContent() instanceof Relationship) {
                                updateChild(context, edge.getSourceNode(), edge.getTargetNode());
                            }
                        })
                );
    }

    private void updateChild(AbstractCanvasHandler context, Node parent, Node child) {
        context.addChild(parent, child);
        ShapeUtils.moveViewConnectorsToTop(context, child);
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        return execute(context);
    }

    @SuppressWarnings("unchecked")
    private void updateConnections(final AbstractCanvasHandler context,
                                   final Edge edge,
                                   final Node sourceNode,
                                   final Node targetNode) {
        if (null != edge && null != sourceNode && null != targetNode) {
            final EdgeShape edgeShape = (EdgeShape) context.getCanvas().getShape(edge.getUUID());
            final Shape sourceNodeShape = context.getCanvas().getShape(sourceNode.getUUID());
            final Shape targetNodeShape = context.getCanvas().getShape(targetNode.getUUID());
            edgeShape.applyConnections(edge,
                                       sourceNodeShape.getShapeView(),
                                       targetNodeShape.getShapeView(),
                                       MutationContext.STATIC);
        }
    }

    private Node getParent() {
        List<Edge> inEdges = candidate.getInEdges();
        if (null != inEdges && !inEdges.isEmpty()) {
            for (final Edge edge : inEdges) {
                if (isChildEdge(edge) || isDockEdge(edge)) {
                    return edge.getSourceNode();
                }
            }
        }
        return null;
    }

    private boolean isChildEdge(final Edge edge) {
        return edge.getContent() instanceof Child;
    }

    private boolean isDockEdge(final Edge edge) {
        return edge.getContent() instanceof Dock;
    }

    private boolean isViewEdge(final Edge edge) {
        return edge.getContent() instanceof View;
    }

    @Override
    public String toString() {
        final Node parent = getParent();
        return getClass().getName() +
                " [parent=" + (null != parent ? parent.getUUID() : "null") + "," +
                " candidate=" + getUUID(candidate) + "," +
                " shapeSet=" + shapeSetId + "]";
    }
}
