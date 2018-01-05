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
package org.kie.workbench.common.stunner.bpmn.backend.converters;

import bpsim.BPSimDataType;
import bpsim.BpsimPackage;
import bpsim.Scenario;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.Relationship;
import org.eclipse.bpmn2.util.Bpmn2ResourceFactoryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.util.DroolsResourceFactoryImpl;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.profile.IDiagramProfile;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder.GraphObjectBuilder;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder.GraphObjectBuilderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder.NodeObjectBuilder;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.OryxManager;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.factory.BPMNGraphFactory;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.command.CommandManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Antoine Toulme
 * @author Surdilovic
 *         <p>
 *         a classLoader to transform BPMN 2.0 elements into JSON format.
 */
public class Bpmn2DataModelConverter {

    private static final List<String> defaultTypesList = Arrays.asList("Object",
                                                                       "Boolean",
                                                                       "Float",
                                                                       "Integer",
                                                                       "List",
                                                                       "String");
    private static final Logger _logger = LoggerFactory.getLogger(Bpmn2DataModelConverter.class);

    private final static ResourceSet resourceSet = new ResourceSetImpl();

    static {
        resourceSet.getPackageRegistry().put(DroolsPackage.eNS_URI,
                DroolsPackage.eINSTANCE);
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION,
                new DroolsResourceFactoryImpl());
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION,
                new Bpmn2ResourceFactoryImpl());
        resourceSet.getPackageRegistry().put("http://www.omg.org/spec/BPMN/20100524/MODEL",
                Bpmn2Package.eINSTANCE);
    }


    private final IDiagramProfile profile;
    private final BPMNGraphGenerator generator;
    private final FactoryManager factoryManager;
    private final GraphIndexBuilder<?> indexBuilder;
    private DefinitionManager definitionManager;
    private final RuleManager ruleManager;
    private final OryxManager oryxManager;
    private final CommandManager<GraphCommandExecutionContext, RuleViolation> commandManager;
    private final GraphCommandFactory commandFactory;

    public Bpmn2DataModelConverter(final GraphObjectBuilderFactory elementBuilderFactory,
                                   final DefinitionManager definitionManager,
                                   final FactoryManager factoryManager,
                                   final RuleManager ruleManager,
                                   final OryxManager oryxManager,
                                   final CommandManager<GraphCommandExecutionContext, RuleViolation> commandManager,
                                   final GraphCommandFactory commandFactory,
                                   final GraphIndexBuilder<?> indexBuilder,
                                   final IDiagramProfile profile) {
        this.definitionManager = definitionManager;
        this.ruleManager = ruleManager;
        this.oryxManager = oryxManager;
        this.commandManager = commandManager;
        this.commandFactory = commandFactory;
        this.generator = new BPMNGraphGenerator(
                elementBuilderFactory,
                indexBuilder);
        this.indexBuilder = indexBuilder;
        this.factoryManager = factoryManager;
        this.profile = profile;
    }

    /**
     * NOTE:
     * This method has been added for Stunner support. Stunner bpmn parser provides a custom BPMNGraphGenerator that
     * is used instead of the one used in jbpm-designer-backend.
     */
    public Graph<DefinitionSet, Node> convert(Definitions def) throws IOException {
//        DefinitionsConverter definitionsConverter =
//                new DefinitionsConverter(factoryManager, profile);
//        definitionsConverter.convert(def);
//
//        Graph<DefinitionSet, Node> graph =
//                (Graph<DefinitionSet, Node>) factoryManager.newElement(UUID.uuid(), BPMNDefinitionSet.class);
//        Element<View<BPMNDiagram>> diagramNode = definitionsConverter.convert(def);
//
//        graph.addNode(diagramNode.asNode());
        return null;

    }

    public Graph<DefinitionSet, Node> createGraph(Collection<GraphObjectBuilder<?, ?>> builders) {

        Graph<DefinitionSet, Node> graph =
                (Graph<DefinitionSet, Node>) factoryManager.newElement(UUID.uuid(), BPMNDefinitionSet.class);
        // TODO: Where are the BPMN diagram bounds in the Oryx json structure? Exist?
        graph.getContent().setBounds(new BoundsImpl(
                new BoundImpl(0d,
                        0d),
                new BoundImpl(BPMNGraphFactory.GRAPH_DEFAULT_WIDTH,
                        BPMNGraphFactory.GRAPH_DEFAULT_HEIGHT)
        ));

        BuilderContext builderContext = new BuilderContext(
                definitionManager,
                factoryManager,
                ruleManager,
                oryxManager,
                commandManager,
                commandFactory,
                indexBuilder,
                builders,
                graph);

        NodeObjectBuilder<BPMNDiagram, Node<View<BPMNDiagram>, Edge>> diagramBuilder =
                builderContext.findBuilder(BPMNDiagram.class);
        Node<View<BPMNDiagram>, Edge> diagramNode = diagramBuilder.build(builderContext);
        graph.addNode(diagramNode);

        return graph;
    }

    private boolean willTranslateCoordinates(Definitions def, Scenario simulationScenario) {
        // this is a temp way to determine if
        // coordinate system changes are necessary
        String bpmn2Exporter = def.getExporter();
        String bpmn2ExporterVersion = def.getExporterVersion();
        boolean haveExporter = bpmn2Exporter != null && bpmn2ExporterVersion != null;
        return simulationScenario == null || haveExporter;
    }

    private Scenario getScenario(List<Relationship> relationships) {
        Scenario simulationScenario = null;
        if (!relationships.isEmpty()) {
            // current support for single relationship
            Relationship relationship = relationships.get(0);
            for (ExtensionAttributeValue extattrval : relationship.getExtensionValues()) {
                FeatureMap extensionElements = extattrval.getValue();
                @SuppressWarnings("unchecked")
                List<BPSimDataType> bpsimExtensions =
                        (List<BPSimDataType>) extensionElements
                                .get(BpsimPackage.Literals.DOCUMENT_ROOT__BP_SIM_DATA, true);
                if (!bpsimExtensions.isEmpty()) {
                    BPSimDataType processAnalysis = bpsimExtensions.get(0);
                    List<Scenario> scenario = processAnalysis.getScenario();
                    if (scenario.size() > 0) {
                        simulationScenario = scenario.get(0);
                    }
                }
            }
        }
        return simulationScenario;
    }

}