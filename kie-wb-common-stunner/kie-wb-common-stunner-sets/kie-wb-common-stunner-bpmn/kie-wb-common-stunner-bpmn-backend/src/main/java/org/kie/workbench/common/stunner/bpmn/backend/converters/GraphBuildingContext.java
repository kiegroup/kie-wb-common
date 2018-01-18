package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.impl.AbstractGraphCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.AddNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.command.impl.SetConnectionSourceNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.SetConnectionTargetNodeCommand;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class GraphBuildingContext {



    private final GraphCommandExecutionContext executionContext;
    private final GraphCommandFactory commandFactory;
    private final GraphCommandManager commandManager;
    private final List<AbstractGraphCommand> commands = new ArrayList<>();

    public GraphBuildingContext(GraphCommandExecutionContext executionContext, GraphCommandFactory commandFactory, GraphCommandManager commandManager) {
        this.executionContext = executionContext;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    public void addNode(Node node) {
        AddNodeCommand addNodeCommand = commandFactory.addNode(node);
        commandManager.execute(executionContext, addNodeCommand);
    }

    public void addEdge(Edge<? extends View<?>, Node> edge, Node<? extends View<?>, Edge> source, Node<? extends View<?>, Edge> target) {
        SetConnectionSourceNodeCommand setSourceNode = commandFactory.setSourceNode(source, edge, null);
        SetConnectionTargetNodeCommand setTargetNode = commandFactory.setTargetNode(target, edge, null);
        commandManager.execute(executionContext, setSourceNode);
        commandManager.execute(executionContext, setTargetNode);
    }

    public GraphCommandExecutionContext executionContext() {
        return executionContext;
    }

}
