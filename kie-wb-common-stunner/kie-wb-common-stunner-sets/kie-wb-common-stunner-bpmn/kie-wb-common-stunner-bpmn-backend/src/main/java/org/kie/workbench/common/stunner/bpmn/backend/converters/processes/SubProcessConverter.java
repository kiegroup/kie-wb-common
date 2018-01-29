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

package org.kie.workbench.common.stunner.bpmn.backend.converters.processes;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.SubProcess;
import org.kie.workbench.common.stunner.bpmn.backend.converters.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.FlowElementConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.GraphBuildingContext;
import org.kie.workbench.common.stunner.bpmn.backend.converters.LaneConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Layout;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Result;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tasks.Scripts;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.tasks.Scripts.onEntry;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tasks.Scripts.onExit;

public class SubProcessConverter {

    private final TypedFactoryManager factoryManager;
    private final FlowElementConverter flowElementConverter;
    private final LaneConverter laneConverter;
    private final GraphBuildingContext context;
    private final Layout layout;

    public SubProcessConverter(
            TypedFactoryManager factoryManager,
            DefinitionResolver definitionResolver,
            FlowElementConverter flowElementConverter,
            GraphBuildingContext context, Layout layout) {

        this.factoryManager = factoryManager;
        this.context = context;

        this.flowElementConverter = flowElementConverter;
        this.laneConverter = new LaneConverter(factoryManager, definitionResolver);
        this.layout = layout;
    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(SubProcess subProcess) {
        Node<? extends View<? extends BPMNViewDefinition>, ?> subProcessNode = convertSubProcessNode(subProcess);

        subProcess.getFlowElements()
                .stream()
                .map(flowElementConverter::convertNode)
                .filter(Result::notIgnored)
                .map(Result::value)
                .forEach(n -> {
                    layout.updateNode(n);
                    context.addChildNode(subProcessNode, n);
                });

        subProcess.getLaneSets()
                .stream()
                .flatMap(laneSet -> laneSet.getLanes().stream())
                .map(laneConverter::convert)
                .forEach(n -> {
                    layout.updateNode(n);
                    context.addChildNode(subProcessNode, n);
                });

        subProcess.getFlowElements()
                .stream()
                .map(flowElementConverter::convertEdge)
                .filter(Result::isSuccess)
                .map(Result::value)
                .forEach(layout::updateEdge);

        subProcess.getFlowElements()
                .forEach(flowElementConverter::convertDockedNodes);

        return subProcessNode;
    }

    private Node<? extends View<? extends BPMNViewDefinition>, ?> convertSubProcessNode(SubProcess subProcess) {
        Node<View<EmbeddedSubprocess>, Edge> node = factoryManager.newNode(subProcess.getId(), EmbeddedSubprocess.class);

        EmbeddedSubprocess definition = node.getContent().getDefinition();

        definition.setGeneral(new BPMNGeneralSet(
                new Name(subProcess.getName()),
                Properties.documentation(subProcess.getDocumentation())
        ));

        List<ExtensionAttributeValue> extensionValues = subProcess.getExtensionValues();
        definition.getOnEntryAction().setValue(onEntry(extensionValues));
        definition.getOnExitAction().setValue(onExit(extensionValues));

        String joinedVariables = subProcess.getProperties()
                .stream()
                .map(p -> p.getId() + ":" + p.getItemSubjectRef().getStructureRef())
                .collect(Collectors.joining(","));
        definition.getProcessData().getProcessVariables().setValue(joinedVariables);

        definition.getScriptLanguage().setValue(Scripts.scriptLanguage(extensionValues));
        definition.setGeneral(new BPMNGeneralSet(
                new Name(subProcess.getName()),
                Properties.documentation(subProcess.getDocumentation())
        ));
        definition.getIsAsync().setValue(Properties.findMetaBoolean(subProcess, "customAsync"));

        return node;
    }
}
