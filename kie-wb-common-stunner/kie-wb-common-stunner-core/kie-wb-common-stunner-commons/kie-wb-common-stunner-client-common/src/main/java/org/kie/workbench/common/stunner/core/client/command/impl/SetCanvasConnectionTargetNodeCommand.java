/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.command.impl;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.AbstractCanvasGraphCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.util.ShapeUtils;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.SetConnectionTargetNodeCommand;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public final class SetCanvasConnectionTargetNodeCommand extends AbstractCanvasGraphCommand {

    private final Node<? extends View<?>, Edge> node;
    private final Edge<? extends View<?>, Node> edge;
    private final int magnetIndex;

    public SetCanvasConnectionTargetNodeCommand( final Node<? extends View<?>, Edge> node,
                                                 final Edge<? extends View<?>, Node> edge,
                                                 final int magnetIndex ) {
        this.node = node;
        this.edge = edge;
        this.magnetIndex = magnetIndex;
    }

    @Override
    protected Command<GraphCommandExecutionContext, RuleViolation> buildGraphCommand( final AbstractCanvasHandler context ) {
        return new SetConnectionTargetNodeCommand( node, edge, magnetIndex );
    }

    @Override
    public CommandResult<CanvasViolation> doCanvasExecute( final AbstractCanvasHandler context ) {
        ShapeUtils.applyConnections( edge, context, MutationContext.STATIC );
        return buildResult();
    }

    @Override
    protected AbstractCanvasCommand buildUndoCommand( final AbstractCanvasHandler context ) {
        return null;
    }

}
