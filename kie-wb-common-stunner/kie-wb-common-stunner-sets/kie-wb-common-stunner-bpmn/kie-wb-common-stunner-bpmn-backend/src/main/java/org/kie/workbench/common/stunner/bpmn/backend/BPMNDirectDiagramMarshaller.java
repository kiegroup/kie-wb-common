/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Layout;
import org.kie.workbench.common.stunner.bpmn.backend.converters.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.GraphBuildingContext;
import org.kie.workbench.common.stunner.bpmn.backend.converters.ProcessConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMarshaller;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.EmptyRulesCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.map.MapIndex;
import org.kie.workbench.common.stunner.core.graph.processing.index.map.MapIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Direct as in "skipping json encoding"
 *
 */
public class BPMNDirectDiagramMarshaller<D> implements DiagramMarshaller<Graph, Metadata, Diagram<Graph, Metadata>> {

    private static final Logger LOG = LoggerFactory.getLogger(BPMNDirectDiagramMarshaller.class);

    private final DefinitionManager definitionManager;
    private final RuleManager ruleManager;
    private final FactoryManager factoryManager;
    private final GraphCommandFactory commandFactory;
    private final GraphCommandManager commandManager;

    public BPMNDirectDiagramMarshaller(
            final DefinitionManager definitionManager,
            final RuleManager ruleManager,
            final FactoryManager factoryManager,
            final GraphCommandFactory commandFactory,
            final GraphCommandManager commandManager) {
        this.definitionManager = definitionManager;
        this.ruleManager = ruleManager;
        this.factoryManager = factoryManager;
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String marshall(final Diagram diagram) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Graph<DefinitionSet, Node> unmarshall(final Metadata metadata,
                                                 final InputStream inputStream) throws IOException {
        LOG.debug("Starting diagram unmarshalling...");

        final Definitions definitions = BPMN2Definitions.parse(inputStream);

        DefinitionResolver definitionResolver = new DefinitionResolver(definitions);

        TypedFactoryManager typedFactoryManager = new TypedFactoryManager(this.factoryManager);
        ProcessConverter processConverter = new ProcessConverter(typedFactoryManager, definitionResolver);

        Process processDiagram =
                (Process) definitions.getRootElements().stream()
                        .filter(el -> el instanceof Process)
                        .findFirst().get();

        Graph<DefinitionSet, Node> graph =
                typedFactoryManager.newGraph(processDiagram.getId(), BPMNDefinitionSet.class);

        MapIndexBuilder builder = new MapIndexBuilder();
        MapIndex index = builder.build(graph);

        final GraphCommandExecutionContext executionContext =
                new EmptyRulesCommandExecutionContext(definitionManager,
                                                      factoryManager,
                                                      ruleManager,
                                                      index);

        CommandResult<RuleViolation> result = commandManager.execute(executionContext, commandFactory.clearGraph());
        GraphBuildingContext context = new GraphBuildingContext(executionContext, commandFactory, commandManager);

        processConverter.processNodes(processDiagram, context);
        processConverter.processEdges(processDiagram, context);

        BPMNPlane plane = definitions.getDiagrams().get(0).getPlane();

        Node<View<BPMNDiagram>, ?> diagramView = processConverter.convertDiagram(definitions.getId(), processDiagram);
        graph.addNode(diagramView.asNode());
        graph.nodes().forEach(node -> Layout.updateNode(plane, node));


        // Update diagram's settings.
        Node<Definition<BPMNDiagramImpl>, ?> firstDiagramNode = GraphUtils.getFirstNode(graph, BPMNDiagramImpl.class);

        String uuid = firstDiagramNode.getUUID();
        metadata.setCanvasRootUUID(uuid);

        String title = firstDiagramNode.getContent().getDefinition().getDiagramSet().getName().getValue();
        metadata.setTitle(title);

        LOG.debug("Diagram unmarshalling finished successfully.");
        return graph;
    }

    @Override
    public DiagramMetadataMarshaller<Metadata> getMetadataMarshaller() {
        return null;
    }
}
