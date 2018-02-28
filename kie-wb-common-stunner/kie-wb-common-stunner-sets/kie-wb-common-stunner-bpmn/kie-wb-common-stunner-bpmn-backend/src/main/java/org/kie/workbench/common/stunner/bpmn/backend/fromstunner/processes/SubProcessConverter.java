/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.fromstunner.processes;

import org.eclipse.bpmn2.SubProcess;
import org.kie.workbench.common.stunner.bpmn.backend.converters.NodeMatch;
import org.kie.workbench.common.stunner.bpmn.backend.fromstunner.DefinitionsBuildingContext;
import org.kie.workbench.common.stunner.bpmn.backend.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.fromstunner.properties.SubProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.bpmn.backend.fromstunner.Factories.bpmn2;

public class SubProcessConverter {

    private final DefinitionsBuildingContext context;
    private final PropertyWriterFactory propertyWriterFactory;
    private final ProcessConverterFactory processConverterFactory;

    public SubProcessConverter(DefinitionsBuildingContext context, PropertyWriterFactory propertyWriterFactory, ProcessConverterFactory processConverterFactory) {
        this.context = context;
        this.propertyWriterFactory = propertyWriterFactory;
        this.processConverterFactory = processConverterFactory;
    }

    public PropertyWriter convertSubProcess(Node<View<BaseSubprocess>, ?> node) {
        SubProcessPropertyWriter processRoot =
                NodeMatch.fromNode(BaseSubprocess.class, SubProcessPropertyWriter.class)
                        .when(EmbeddedSubprocess.class, this::convertEmbeddedSubprocessNode)
                        .when(EventSubprocess.class, this::convertEventSubprocessNode)
                        .apply(node).value();

        DefinitionsBuildingContext subContext = context.withRootNode(node);

        processConverterFactory.convertChildNodes(processRoot, subContext.nodes(), subContext.lanes());
        processConverterFactory.convertEdges(processRoot, subContext);

        return processRoot;
    }

    private SubProcessPropertyWriter convertEventSubprocessNode(Node<View<EventSubprocess>, ?> n) {
        SubProcess process = bpmn2.createSubProcess();
        process.setId(n.getUUID());

        SubProcessPropertyWriter p = propertyWriterFactory.of(process);

        EventSubprocess definition = n.getContent().getDefinition();
        process.setTriggeredByEvent(true);

        BPMNGeneralSet general = definition.getGeneral();

        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());

        ProcessData processData = definition.getProcessData();
        p.setProcessVariables(processData.getProcessVariables());

        p.setSimulationSet(definition.getSimulationSet());

        p.setBounds(n.getContent().getBounds());

        return p;
    }

    private SubProcessPropertyWriter convertEmbeddedSubprocessNode(Node<View<EmbeddedSubprocess>, ?> n) {
        SubProcess process = bpmn2.createSubProcess();
        process.setId(n.getUUID());

        SubProcessPropertyWriter p = propertyWriterFactory.of(process);

        EmbeddedSubprocess definition = n.getContent().getDefinition();

        BPMNGeneralSet general = definition.getGeneral();

        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());

        ProcessData processData = definition.getProcessData();
        p.setProcessVariables(processData.getProcessVariables());

        p.setSimulationSet(definition.getSimulationSet());
        p.setBounds(n.getContent().getBounds());
        return p;
    }
}