package org.kie.workbench.common.stunner.bpmn.backend.converters;

import org.eclipse.bpmn2.CallActivity;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
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
