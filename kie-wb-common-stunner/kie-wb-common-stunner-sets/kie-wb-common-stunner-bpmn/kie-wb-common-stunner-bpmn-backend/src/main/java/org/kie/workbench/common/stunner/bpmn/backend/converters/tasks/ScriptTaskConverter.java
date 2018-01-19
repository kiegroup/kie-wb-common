package org.kie.workbench.common.stunner.bpmn.backend.converters.tasks;

import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.BPMNGeneralSets;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTaskExecutionSet;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties.findMetaBoolean;

public class ScriptTaskConverter {

    private final TypedFactoryManager factoryManager;

    public ScriptTaskConverter(TypedFactoryManager factoryManager) {
        this.factoryManager = factoryManager;
    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(org.eclipse.bpmn2.ScriptTask task) {
        Node<View<ScriptTask>, Edge> node = factoryManager.newNode(task.getId(), ScriptTask.class);
        BPMNGeneralSets.setProperties(task, node.getContent().getDefinition().getGeneral());
        ScriptTaskExecutionSet executionSet = node.getContent().getDefinition().getExecutionSet();
        Scripts.setProperties(task, executionSet);
        executionSet.getIsAsync().setValue(findMetaBoolean(task, "customAsync"));
        return node;
    }
}
