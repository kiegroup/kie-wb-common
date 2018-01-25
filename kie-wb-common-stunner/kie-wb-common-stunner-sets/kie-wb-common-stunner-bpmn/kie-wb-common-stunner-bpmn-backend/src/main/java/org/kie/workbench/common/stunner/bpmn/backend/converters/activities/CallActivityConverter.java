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

import org.eclipse.bpmn2.CallActivity;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.CalledElement;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Independent;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.WaitForCompletion;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class CallActivityConverter {

    private final TypedFactoryManager factoryManager;

    public CallActivityConverter(TypedFactoryManager factoryManager) {
        this.factoryManager = factoryManager;
    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(CallActivity activity) {
        Node<View<ReusableSubprocess>, Edge> node = factoryManager.newNode(activity.getId(), ReusableSubprocess.class);

        ReusableSubprocess definition = node.getContent().getDefinition();

        definition.setGeneral(new BPMNGeneralSet(
                new Name(activity.getName()),
                Properties.documentation(activity.getDocumentation())
        ));

        definition.setExecutionSet(new ReusableSubprocessTaskExecutionSet(
                new CalledElement(activity.getCalledElement()),
                new Independent(Properties.findAnyAttributeBoolean(activity, "independent")),
                new WaitForCompletion(Properties.findAnyAttributeBoolean(activity, "waitForCompletion")),
                new IsAsync(Properties.findMetaBoolean(activity, "customAsync"))
        ));

        definition.getDataIOSet()
                .setAssignmentsinfo(new AssignmentsInfo(
                        Properties.getAssignmentsInfo(activity)));

        return node;
    }
}
