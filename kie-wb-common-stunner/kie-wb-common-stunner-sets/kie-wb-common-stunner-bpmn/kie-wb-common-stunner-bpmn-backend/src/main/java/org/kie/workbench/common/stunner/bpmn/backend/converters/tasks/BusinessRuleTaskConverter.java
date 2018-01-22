package org.kie.workbench.common.stunner.bpmn.backend.converters.tasks;

import org.kie.workbench.common.stunner.bpmn.backend.converters.BPMNGeneralSets;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocAutostart;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BusinessRuleTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties.findMetaBoolean;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tasks.Scripts.onEntry;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tasks.Scripts.onExit;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tasks.Scripts.ruleFlowGroup;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tasks.Scripts.scriptLanguage;

public class BusinessRuleTaskConverter {

    private final TypedFactoryManager factoryManager;

    public BusinessRuleTaskConverter(TypedFactoryManager factoryManager) {
        this.factoryManager = factoryManager;
    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(org.eclipse.bpmn2.BusinessRuleTask task) {
        Node<View<BusinessRuleTask>, Edge> node = factoryManager.newNode(task.getId(), BusinessRuleTask.class);

        BusinessRuleTask definition = node.getContent().getDefinition();
        BPMNGeneralSets.setProperties(task, definition.getGeneral());
        definition.getDataIOSet().getAssignmentsinfo().setValue(Properties.getAssignmentsInfo(task));

        definition.setExecutionSet(new BusinessRuleTaskExecutionSet(
                ruleFlowGroup(task),
                onEntry(task),
                onExit(task),
                scriptLanguage(task),
                new IsAsync(findMetaBoolean(task, "customAsync")),
                new AdHocAutostart(findMetaBoolean(task, "customAutoStart"))
        ));

        return node;
    }
}
