package org.kie.workbench.common.stunner.bpmn.backend.converters;

import org.kie.workbench.common.stunner.bpmn.definition.*;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class TaskConverter {
    private TypedFactoryManager factoryManager;

    public TaskConverter(TypedFactoryManager factoryManager) {
        this.factoryManager = factoryManager;
    }
    public Node<View<BPMNViewDefinition>, ?> convert(org.eclipse.bpmn2.Task task) {
        return Match.ofNode(org.eclipse.bpmn2.Task.class, BPMNViewDefinition.class)
            .when(org.eclipse.bpmn2.BusinessRuleTask.class, t ->
                    factoryManager.newNode(t.getId(), BusinessRuleTask.class))
            .when(org.eclipse.bpmn2.ScriptTask.class,       t ->
                    factoryManager.newNode(t.getId(), ScriptTask.class)
            )
            //.when(org.eclipse.bpmn2.ServiceTask.class,      t -> null)
            //.when(org.eclipse.bpmn2.ManualTask.class,       t -> null)
            .when(org.eclipse.bpmn2.UserTask.class,         t ->
                    factoryManager.newNode(t.getId(), UserTask.class)
            )
            .orElse(t ->
                    factoryManager.newNode(t.getId(), NoneTask.class)
            )
            .apply(task)
            .get();
    }

}
