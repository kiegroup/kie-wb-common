package org.kie.workbench.common.stunner.bpmn.backend.converters;


import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder.GraphObjectBuilder;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder.NodeObjectBuilder;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.OryxManager;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandManager;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.EmptyRulesCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

import java.util.Collection;

public class BuilderContext implements GraphObjectBuilder.BuilderContext {
    private final DefinitionManager definitionManager;
    private final FactoryManager factoryManager;
    private final RuleManager ruleManager;
    private final OryxManager oryxManager;
    private final CommandManager<GraphCommandExecutionContext, RuleViolation> commandManager;
    private final GraphCommandFactory commandFactory;
    private Collection<GraphObjectBuilder<?, ?>> builders;
    Graph<DefinitionSet, Node> graph;
    Index<?, ?> index;

    public BuilderContext(
            final DefinitionManager definitionManager,
            final FactoryManager factoryManager,
            final RuleManager ruleManager,
            final OryxManager oryxManager,
            final CommandManager<GraphCommandExecutionContext, RuleViolation> commandManager,
            final GraphCommandFactory commandFactory,
            final GraphIndexBuilder<?> indexBuilder,
            final Collection<GraphObjectBuilder<?, ?>> builders,
            Graph<DefinitionSet, Node> graph) {

        this.definitionManager = definitionManager;
        this.factoryManager = factoryManager;
        this.ruleManager = ruleManager;
        this.oryxManager = oryxManager;
        this.commandManager = commandManager;
        this.commandFactory = commandFactory;
        this.builders = builders;
        this.graph = graph;

        this.graph = graph;
        this.index = indexBuilder.build(graph);

    }



    public <W, T extends Node<View<W>, Edge>> NodeObjectBuilder<W, T> findBuilder(Class<W> type) {
        return builders.stream()
                .filter(builder -> builder instanceof NodeObjectBuilder)
                .map(builder -> (NodeObjectBuilder<W, T>) builder)
                .filter(builder -> type.isAssignableFrom(builder.getDefinitionClass()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cannot find '" + type + "' !"));
    }


    @Override
    public GraphObjectBuilder.BuilderContext init(final Graph<DefinitionSet, Node> graph) {
        return this;
    }

    @Override
    public Index<?, ?> getIndex() {
        return index;
    }

    @Override
    public Collection<GraphObjectBuilder<?, ?>> getBuilders() {
        return builders;
    }

    @Override
    public DefinitionManager getDefinitionManager() {
        return definitionManager;
    }

    @Override
    public FactoryManager getFactoryManager() {
        return factoryManager;
    }

    @Override
    public OryxManager getOryxManager() {
        return oryxManager;
    }

    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> execute(final Command<GraphCommandExecutionContext, RuleViolation> command) {
        GraphCommandExecutionContext executionContext =
                new EmptyRulesCommandExecutionContext(definitionManager,
                        factoryManager,
                        ruleManager,
                        index);
        return commandManager.execute(executionContext,
                command);
    }

    public GraphCommandFactory getCommandFactory() {
        return commandFactory;
    }
};
