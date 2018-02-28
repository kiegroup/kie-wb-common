package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.activities;

import org.eclipse.bpmn2.CallActivity;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.CallActivityPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class ReusableSubprocessConverter {

    private final PropertyWriterFactory propertyWriterFactory;

    public ReusableSubprocessConverter(PropertyWriterFactory propertyWriterFactory) {

        this.propertyWriterFactory = propertyWriterFactory;
    }

    public PropertyWriter toFlowElement(Node<View<ReusableSubprocess>, ?> n) {
        CallActivity activity = bpmn2.createCallActivity();
        activity.setId(n.getUUID());

        CallActivityPropertyWriter p = propertyWriterFactory.of(activity);

        ReusableSubprocess definition = n.getContent().getDefinition();

        BPMNGeneralSet general = definition.getGeneral();

        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());

        ReusableSubprocessTaskExecutionSet executionSet = definition.getExecutionSet();
        p.setCalledElement(executionSet.getCalledElement().getValue());
        p.setAsync(executionSet.getIsAsync().getValue());
        p.setIndependent(executionSet.getIndependent().getValue());
        p.setWaitForCompletion(executionSet.getIndependent().getValue());

        p.setAssignmentsInfo(definition.getDataIOSet().getAssignmentsinfo());

        p.setSimulationSet(definition.getSimulationSet());

        p.setBounds(n.getContent().getBounds());
        return p;
    }
}
