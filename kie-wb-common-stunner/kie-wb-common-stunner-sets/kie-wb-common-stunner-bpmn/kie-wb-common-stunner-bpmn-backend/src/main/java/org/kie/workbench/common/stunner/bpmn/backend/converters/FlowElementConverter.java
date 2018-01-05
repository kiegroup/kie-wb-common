package org.kie.workbench.common.stunner.bpmn.backend.converters;

import org.eclipse.bpmn2.*;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class FlowElementConverter {
    private final TypedFactoryManager factoryManager;
    private final StartEventConverter startEventConverter;
    private final TaskConverter taskConverter;
    private final SequenceFlowConverter sequenceFlowConverter;
    private EndEventConverter endEventConverter;

    public FlowElementConverter(TypedFactoryManager factoryManager) {
        this.factoryManager = factoryManager;
        this.startEventConverter   = new StartEventConverter(factoryManager);
        this.endEventConverter     = new EndEventConverter(factoryManager);
        this.taskConverter         = new TaskConverter(factoryManager);
        this.sequenceFlowConverter = new SequenceFlowConverter(factoryManager);
    }

    public Node<View<BPMNViewDefinition>, ?> convertNode(FlowElement flowElement) {
        return Match.ofNode(FlowElement.class, BPMNViewDefinition.class)
                .when(StartEvent.class,    startEventConverter::convert)
                .when(EndEvent.class,      endEventConverter::convert)
                .when(Task.class,          taskConverter::convert)
                .apply(flowElement).get();
    }
    public Edge<View<BPMNViewDefinition>, ?> convertEdge(FlowElement flowElement) {
        return Match.ofEdge(FlowElement.class, BPMNViewDefinition.class)
                .when(SequenceFlow.class,  sequenceFlowConverter::convert)
                .apply(flowElement).get();
    }
}
