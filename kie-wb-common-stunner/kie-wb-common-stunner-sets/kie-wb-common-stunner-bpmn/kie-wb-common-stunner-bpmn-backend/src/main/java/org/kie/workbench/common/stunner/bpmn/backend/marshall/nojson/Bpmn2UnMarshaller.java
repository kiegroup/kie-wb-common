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

package org.kie.workbench.common.stunner.bpmn.backend.marshall.nojson;

import bpsim.impl.BpsimPackageImpl;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.util.Bpmn2ResourceFactoryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.impl.DroolsPackageImpl;
import org.jboss.drools.util.DroolsResourceFactoryImpl;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.profile.IDiagramProfile;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder.GraphObjectBuilderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.OryxManager;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.command.CommandManager;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

import java.io.IOException;

public class Bpmn2UnMarshaller {

    final static ResourceSet resourceSet = new ResourceSetImpl();
    final Bpmn2JsonMarshaller marshaller = new Bpmn2JsonMarshaller();

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

    BPMNGraphGenerator bpmnGraphGenerator;

    public Bpmn2UnMarshaller(final GraphObjectBuilderFactory elementBuilderFactory,
                             final DefinitionManager definitionManager,
                             final FactoryManager factoryManager,
                             final RuleManager ruleManager,
                             final OryxManager oryxManager,
                             final CommandManager<GraphCommandExecutionContext, RuleViolation> commandManager,
                             final GraphCommandFactory commandFactory,
                             final GraphIndexBuilder<?> indexBuilder,
                             final Class<?> diagramDefinitionSetClass,
                             final Class<? extends BPMNDiagram> diagramDefinitionClass) {
        this.bpmnGraphGenerator = new BPMNGraphGenerator(elementBuilderFactory,
                                                         definitionManager,
                                                         factoryManager,
                                                         ruleManager,
                                                         oryxManager,
                                                         commandManager,
                                                         commandFactory,
                                                         indexBuilder,
                                                         diagramDefinitionSetClass,
                                                         diagramDefinitionClass);
    }

    public void setProfile(IDiagramProfile profile) {
        marshaller.setProfile(profile);
    }

    public Graph unmarshall(final Definitions def) throws IOException {
        DroolsPackageImpl.init();
        BpsimPackageImpl.init();
        marshaller.marshall(bpmnGraphGenerator,
                       def,
                       null);
        bpmnGraphGenerator.close();
        return bpmnGraphGenerator.getGraph();
    }
}
