/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.graph.command.impl;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * A Command to set the magnet for a node
 */
@Portable
public final class SetConnectionTargetMagnetCommand extends AbstractGraphCommand {

    private final String nodeUUID;
    private final String edgeUUID;
    private final Integer magnetIndex;
    private final Double magnetX;
    private final Double magnetY;

    private Integer lastMagnetIndex;
    private Double lastMagnetX;
    private Double lastMagnetY;
    private transient Edge<? extends View, Node> edge;
    private transient Node<? extends View<?>, Edge> node;

    @SuppressWarnings("unchecked")
    public SetConnectionTargetMagnetCommand(final @MapsTo("nodeUUID") String nodeUUID,
                                            final @MapsTo("edgeUUID") String edgeUUID,
                                            final @MapsTo("magnetIndex") Integer magnetIndex,
                                            final @MapsTo("magnetX") Double magnetX,
                                            final @MapsTo("magnetY") Double magnetY) {
        this.edgeUUID = PortablePreconditions.checkNotNull("edgeUUID",
                                                           edgeUUID);
        this.nodeUUID = nodeUUID;
        this.magnetIndex = magnetIndex;
        this.magnetX = magnetX;
        this.magnetY = magnetY;
        this.lastMagnetIndex = null;
        this.lastMagnetX = null;
        this.lastMagnetY = null;
    }

    @SuppressWarnings("unchecked")
    public SetConnectionTargetMagnetCommand(final Node<? extends View<?>, Edge> node,
                                            final Edge<? extends View, Node> edge,
                                            final Integer magnetIndex,
                                            final Double magnetX,
                                            final Double magnetY) {
        this(null != node ? node.getUUID() : null,
             edge.getUUID(),
             magnetIndex,
             magnetX,
             magnetY);
        this.node = node;
        this.edge = edge;
    }

    @SuppressWarnings("unchecked")
    public SetConnectionTargetMagnetCommand(final Node<? extends View<?>, Edge> node,
                                            final Edge<? extends View, Node> edge) {
        this(node,
             edge,
             null,
             null,
             null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        final CommandResult<RuleViolation> results = allow(context);
        if (!results.getType().equals(CommandResult.Type.ERROR)) {
            ViewConnector connectionContent = (ViewConnector) edge.getContent();
            lastMagnetIndex = connectionContent.getTargetMagnetIndex();
            lastMagnetX = connectionContent.getTargetMagnetX();
            lastMagnetY = connectionContent.getTargetMagnetY();
            connectionContent.setTargetMagnet(magnetIndex,
                                              magnetX,
                                              magnetY);
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
        return new GraphCommandResultBuilder().build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        final SetConnectionTargetMagnetCommand undoCommand = new SetConnectionTargetMagnetCommand((Node<? extends View<?>, Edge>) getNode(context,
                                                                                                                                          nodeUUID),
                                                                                                  getEdge(context),
                                                                                                  lastMagnetIndex,
                                                                                                  lastMagnetX,
                                                                                                  lastMagnetY);
        return undoCommand.execute(context);
    }

    @SuppressWarnings("unchecked")
    public Node<? extends View<?>, Edge> getNode(final GraphCommandExecutionContext context) {
        if (null == node) {
            node = (Node<? extends View<?>, Edge>) getNode(context,
                                                           nodeUUID);
        }
        return node;
    }

    public Edge<? extends View, Node> getEdge(final GraphCommandExecutionContext context) {
        if (null == this.edge) {
            this.edge = getViewEdge(context,
                                    edgeUUID);
        }
        return this.edge;
    }

    public Node<? extends View<?>, Edge> getNode() {
        return node;
    }

    public Edge<? extends View, Node> getEdge() {
        return edge;
    }

    public Integer getMagnetIndex() {
        return magnetIndex;
    }

    public Double getMagnetX() {
        return magnetX;
    }

    public Double getMagnetY() {
        return magnetY;
    }

    @Override
    public String toString() {
        return "SetConnectionTargetMagnetCommand [edge=" + edgeUUID
                + ", candidate=" + (null != nodeUUID ? nodeUUID : "null")
                + ", magnet=" + magnetIndex + "]";
    }
}
