package org.kie.workbench.common.stunner.bpmn.backend.converters.tasks;

import org.kie.workbench.common.stunner.bpmn.backend.converters.Properties;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BusinessRuleTaskExecutionSet;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.Properties.findAnyAttribute;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.Properties.findMetaBoolean;

public class BusinessRuleTaskConverter {
    private final TypedFactoryManager factoryManager;

    public BusinessRuleTaskConverter(TypedFactoryManager factoryManager) {
        this.factoryManager = factoryManager;
    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(org.eclipse.bpmn2.BusinessRuleTask task) {
        Node<View<BusinessRuleTask>, Edge> node = factoryManager.newNode(task.getId(), BusinessRuleTask.class);
        BusinessRuleTask taskDef = node.getContent().getDefinition();
        AssignmentsInfoStringBuilder.setAssignmentsInfo(
                task, taskDef.getDataIOSet().getAssignmentsinfo());

        taskDef.getGeneral().getName().setValue(task.getName());
        BusinessRuleTaskExecutionSet executionSet = taskDef.getExecutionSet();
        executionSet.getIsAsync().setValue(findMetaBoolean(task, "customAsync"));

        executionSet.getRuleFlowGroup().setValue(findAnyAttribute(task, "ruleFlowGroup"));
        Properties.setScriptProperties(task, executionSet);
        return node;
    }
}
