package org.kie.workbench.common.stunner.bpmn.backend.converters.tasks;

import org.eclipse.bpmn2.Task;
import org.kie.workbench.common.stunner.bpmn.backend.converters.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Match;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class TaskConverter {

    private final TypedFactoryManager factoryManager;
    private final BusinessRuleTaskConverter businessRuleTaskConverter;
    private final UserTaskConverter userTaskConverter;
    private final ScriptTaskConverter scriptTaskConverter;

    public TaskConverter(TypedFactoryManager factoryManager, DefinitionResolver definitionResolver) {
        this.factoryManager = factoryManager;
        this.businessRuleTaskConverter = new BusinessRuleTaskConverter(factoryManager);
        this.userTaskConverter = new UserTaskConverter(factoryManager, definitionResolver);
        this.scriptTaskConverter = new ScriptTaskConverter(factoryManager);
    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(org.eclipse.bpmn2.Task task) {
        return Match.ofNode(Task.class, BPMNViewDefinition.class)
                .when(org.eclipse.bpmn2.BusinessRuleTask.class, businessRuleTaskConverter::convert)
                .when(org.eclipse.bpmn2.ScriptTask.class, scriptTaskConverter::convert)
                .when(org.eclipse.bpmn2.UserTask.class, userTaskConverter::convert)
                .missing(org.eclipse.bpmn2.ServiceTask.class)
                .missing(org.eclipse.bpmn2.ManualTask.class)
                .orElse(t ->
                                factoryManager.newNode(t.getId(), NoneTask.class)
                )
                .apply(task)
                .asSuccess().value();
    }


}
