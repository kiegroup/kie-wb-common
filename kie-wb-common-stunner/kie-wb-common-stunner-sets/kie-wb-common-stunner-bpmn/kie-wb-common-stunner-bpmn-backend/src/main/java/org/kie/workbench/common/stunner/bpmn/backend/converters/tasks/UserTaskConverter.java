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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tasks;

import org.kie.workbench.common.stunner.bpmn.backend.converters.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.assignee.Actors;
import org.kie.workbench.common.stunner.bpmn.definition.property.assignee.Groupid;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.Priority;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocAutostart;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.CreatedBy;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Description;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Skippable;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Subject;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskName;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.UserTaskExecutionSet;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties.findInputBooleans;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties.findInputValue;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties.findMetaBoolean;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tasks.Scripts.onEntry;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tasks.Scripts.onExit;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tasks.Scripts.scriptLanguage;

public class UserTaskConverter {

    private final TypedFactoryManager factoryManager;
    private final DefinitionResolver resolver;

    public UserTaskConverter(TypedFactoryManager factoryManager, DefinitionResolver resolver) {
        this.factoryManager = factoryManager;
        this.resolver = resolver;
    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(org.eclipse.bpmn2.UserTask task) {
        Node<View<UserTask>, Edge> node = factoryManager.newNode(task.getId(), UserTask.class);

        UserTask definition = node.getContent().getDefinition();

        definition.setGeneral(new TaskGeneralSet(
                new Name(task.getName()),
                Properties.documentation(task.getDocumentation())
        ));

        definition.setSimulationSet(
                resolver.extractSimulationSet(task)
        );

        definition.setExecutionSet(new UserTaskExecutionSet(
                //new TaskName(task.getName()), ??? why is this overridden ???
                new TaskName(findInputValue(task, "TaskName")),
                new Actors(Properties.actors(task)),
                new Groupid(findInputValue(task, "GroupId")),
                new AssignmentsInfo(Properties.getAssignmentsInfo(task)),
                new IsAsync(findMetaBoolean(task, "customAsync")),
                new Skippable(findInputBooleans(task, "Skippable")),
                new Priority(findInputValue(task, "Priority")),
                new Subject(findInputValue(task, "Comment")),
                new Description(findInputValue(task, "Description")),
                new CreatedBy(findInputValue(task, "CreatedBy")),
                new AdHocAutostart(findMetaBoolean(task, "customAutoStart")),
                onEntry(task),
                onExit(task),
                scriptLanguage(task)
        ));

        return node;
    }
}
