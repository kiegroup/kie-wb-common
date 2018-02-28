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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.processes;

import java.util.Map;

import org.eclipse.bpmn2.SubProcess;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.SubProcessPropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class SubProcessConverter {

    protected final TypedFactoryManager factoryManager;
    protected final PropertyReaderFactory propertyReaderFactory;

    private final ProcessConverterFactory processConverterFactory;

    public SubProcessConverter(
            TypedFactoryManager typedFactoryManager,
            PropertyReaderFactory propertyReaderFactory,
            ProcessConverterFactory processConverterFactory) {

        this.factoryManager = typedFactoryManager;
        this.propertyReaderFactory = propertyReaderFactory;
        this.processConverterFactory = processConverterFactory;
    }

    public BpmnNode convertSubProcess(SubProcess subProcess) {
        BpmnNode subProcessRoot =
                subProcess.isTriggeredByEvent() ?
                        convertEventSubprocessNode(subProcess)
                        : convertEmbeddedSubprocessNode(subProcess);

        Map<String, BpmnNode> nodes =
                processConverterFactory.convertChildNodes(
                        subProcessRoot,
                        subProcess.getFlowElements(),
                        subProcess.getLaneSets());

        processConverterFactory.convertEdges(
                subProcessRoot,
                subProcess.getFlowElements(),
                nodes);

        return subProcessRoot;
    }

    private BpmnNode convertEmbeddedSubprocessNode(SubProcess subProcess) {
        Node<View<EmbeddedSubprocess>, Edge> node =
                factoryManager.newNode(subProcess.getId(), EmbeddedSubprocess.class);

        EmbeddedSubprocess definition = node.getContent().getDefinition();
        SubProcessPropertyReader p = propertyReaderFactory.of(subProcess);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(subProcess.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.getOnEntryAction().setValue(p.getOnEntryAction());
        definition.getOnExitAction().setValue(p.getOnExitAction());
        definition.getIsAsync().setValue(p.isAsync());

        definition.setProcessData(new ProcessData(
                new ProcessVariables(p.getProcessVariables())));

        definition.setSimulationSet(p.getSimulationSet());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        node.getContent().setBounds(p.getBounds());
        return BpmnNode.of(node);
    }

    private BpmnNode convertEventSubprocessNode(SubProcess subProcess) {
        Node<View<EventSubprocess>, Edge> node =
                factoryManager.newNode(subProcess.getId(), EventSubprocess.class);

        EventSubprocess definition = node.getContent().getDefinition();
        SubProcessPropertyReader p = propertyReaderFactory.of(subProcess);

        definition.setGeneral(new BPMNGeneralSet(
                new Name(subProcess.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.getIsAsync().setValue(p.isAsync());

        definition.setProcessData(new ProcessData(
                new ProcessVariables(p.getProcessVariables())));

        definition.setSimulationSet(p.getSimulationSet());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        node.getContent().setBounds(p.getBounds());

        return BpmnNode.of(node);
    }
}