package org.kie.workbench.common.stunner.bpmn.backend.converters.tasks;

import org.kie.workbench.common.stunner.bpmn.backend.converters.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.UserTaskExecutionSet;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties.findInputBooleans;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties.findInputValue;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties.findMetaBoolean;

public class UserTaskConverter {

    private final TypedFactoryManager factoryManager;
    private final DefinitionResolver resolver;

    public UserTaskConverter(TypedFactoryManager factoryManager, DefinitionResolver resolver) {
        this.factoryManager = factoryManager;
        this.resolver = resolver;
    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(org.eclipse.bpmn2.UserTask task) {
        Node<View<UserTask>, Edge> node = factoryManager.newNode(task.getId(), UserTask.class);

        UserTaskExecutionSet executionSet = node.getContent().getDefinition().getExecutionSet();
        AssignmentsInfoStringBuilder.setAssignmentsInfo(
                task, executionSet.getAssignmentsinfo());

        executionSet.getTaskName().setValue(task.getName());
        executionSet.getIsAsync().setValue(findMetaBoolean(task, "customAsync"));
        executionSet.getAdHocAutostart().setValue(findMetaBoolean(task, "customAutoStart"));

        executionSet.getSubject().setValue(findInputValue(task, "Comment"));
        executionSet.getTaskName().setValue(findInputValue(task, "TaskName"));
        executionSet.getSkippable().setValue(findInputBooleans(task, "Skippable"));
        executionSet.getDescription().setValue(findInputValue(task, "Description"));
        executionSet.getPriority().setValue(findInputValue(task, "Priority"));
        executionSet.getCreatedBy().setValue(findInputValue(task, "CreatedBy"));

        executionSet.getActors().setValue(Properties.actors(task));
        executionSet.getGroupid().setValue(findInputValue(task, "GroupId"));

        Scripts.setProperties(task, executionSet);
        resolver.resolveSimulationParameters(task.getId())
                .ifPresent(params -> Simulations.setProperties(params, node.getContent().getDefinition().getSimulationSet()));
        // Simulations.setProperties(elementParameters, node.getContent().getDefinition().getSimulationSet());
        return node;
    }
}
