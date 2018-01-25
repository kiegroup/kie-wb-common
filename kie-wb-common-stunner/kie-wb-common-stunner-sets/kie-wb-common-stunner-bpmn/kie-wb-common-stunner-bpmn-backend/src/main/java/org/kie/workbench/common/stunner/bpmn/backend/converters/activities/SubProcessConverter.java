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

package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.SubProcess;
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

    public SubProcessConverter(TypedFactoryManager factoryManager) {
        this.factoryManager = factoryManager;
    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(SubProcess subProcess) {
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
