package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.Objects;

import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.impl.AbstractGraphCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.AddDockedNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.AddNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.command.impl.SetConnectionSourceNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.SetConnectionTargetNodeCommand;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public class GraphBuildingContext {

    private final GraphCommandExecutionContext executionContext;
    private final GraphCommandFactory commandFactory;
    private final GraphCommandManager commandManager;

    public GraphBuildingContext(
            GraphCommandExecutionContext executionContext,
            GraphCommandFactory commandFactory,
            GraphCommandManager commandManager) {
        this.executionContext = executionContext;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    public void addDockedNode(String candidateId, String parentId) {
        Node parent = executionContext.getGraphIndex().getNode(parentId);
        Node candidate = executionContext.getGraphIndex().getNode(candidateId);

        AddDockedNodeCommand addNodeCommand = commandFactory.addDockedNode(parent, candidate);
        execute(addNodeCommand);
    }

    public void addDockedNode(Node node, Node parent) {
        AddDockedNodeCommand addNodeCommand = commandFactory.addDockedNode(parent, node);
        execute(addNodeCommand);
    }

    public void addNode(Node node) {
        AddNodeCommand addNodeCommand = commandFactory.addNode(node);
        execute(addNodeCommand);
    }

    public void addEdge(
            Edge<? extends View<?>, Node> edge,
            String sourceId,
            boolean isAutoConnectionSource,
            String targetId,
            boolean isAutoConnectionTarget) {

        Node source = executionContext.getGraphIndex().getNode(sourceId);
        Node target = executionContext.getGraphIndex().getNode(targetId);

        Objects.requireNonNull(source);
        Objects.requireNonNull(target);

        MagnetConnection sourceConnection = MagnetConnection.Builder.at(0,0 ).setAuto(isAutoConnectionSource);
        MagnetConnection targetConnection = MagnetConnection.Builder.at(0,0 ).setAuto(isAutoConnectionTarget);

        SetConnectionSourceNodeCommand setSourceNode = commandFactory.setSourceNode(source, edge, sourceConnection);
        SetConnectionTargetNodeCommand setTargetNode = commandFactory.setTargetNode(target, edge, targetConnection);

        execute(setSourceNode);
        execute(setTargetNode);
    }

    public void addEdge(
            Edge<? extends View<?>, Node> edge,
            Node<? extends View<?>, Edge> source,
            Node<? extends View<?>, Edge> target) {
        SetConnectionSourceNodeCommand setSourceNode = commandFactory.setSourceNode(source, edge, null);
        SetConnectionTargetNodeCommand setTargetNode = commandFactory.setTargetNode(target, edge, null);
        execute(setSourceNode);
        execute(setTargetNode);
    }

    public void setEdgeSourceConnection(String edgeId, Connection connection) {
        Edge edge = executionContext.getGraphIndex().getEdge(edgeId);
        Node sourceNode = edge.getSourceNode();
        SetConnectionSourceNodeCommand setSourceNode = commandFactory.setSourceNode(sourceNode, edge, connection);
        execute(setSourceNode);
    }

    public void setEdgeTargetConnection(String edgeId, Connection connection) {
        Edge edge = executionContext.getGraphIndex().getEdge(edgeId);
        Node targetNode = edge.getTargetNode();
        SetConnectionTargetNodeCommand setTargetNode = commandFactory.setTargetNode(targetNode, edge, connection);
        execute(setTargetNode);
    }

    public void setBounds(String elementId, int x1, int y1, int x2, int y2) {
        Element<? extends View<?>> element = executionContext.getGraphIndex().get(elementId);
        element.getContent().setBounds(BoundsImpl.build(x1, y1, x2, y2));
    }

    private CommandResult<RuleViolation> execute(Command<GraphCommandExecutionContext, RuleViolation> command) {
        return commandManager.execute(executionContext, command);
    }

    public GraphCommandExecutionContext executionContext() {
        return executionContext;
    }

    public CommandResult<RuleViolation> clearGraph() {
        return commandManager.execute(executionContext, commandFactory.clearGraph());
    }
}
