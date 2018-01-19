package org.kie.workbench.common.stunner.bpmn.backend.converters;

import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.IntermediateCatchEvent;
import org.eclipse.bpmn2.IntermediateThrowEvent;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.Task;
import org.kie.workbench.common.stunner.bpmn.backend.converters.events.BoundaryEventConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.events.EndEventConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.events.IntermediateCatchEventConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.events.IntermediateThrowEventConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.events.StartEventConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.gateways.GatewayConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.sequenceflows.SequenceFlowConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tasks.TaskConverter;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class FlowElementConverter {

    private final TypedFactoryManager factoryManager;
    private final StartEventConverter startEventConverter;
    private final TaskConverter taskConverter;
    private final SequenceFlowConverter sequenceFlowConverter;
    private final GatewayConverter gatewayConverter;
    private final BoundaryEventConverter boundaryEventConverter;
    private DefinitionResolver definitionResolver;
    private final EndEventConverter endEventConverter;
    private final IntermediateThrowEventConverter intermediateThrowEventConverter;
    private final IntermediateCatchEventConverter intermediateCatchEventConverter;

    public FlowElementConverter(TypedFactoryManager factoryManager, DefinitionResolver definitionResolver) {
        this.factoryManager = factoryManager;
        this.startEventConverter = new StartEventConverter(factoryManager, definitionResolver);
        this.endEventConverter = new EndEventConverter(factoryManager, definitionResolver);
        this.intermediateThrowEventConverter = new IntermediateThrowEventConverter(factoryManager, definitionResolver);
        this.intermediateCatchEventConverter = new IntermediateCatchEventConverter(factoryManager, definitionResolver);
        this.taskConverter = new TaskConverter(factoryManager);
        this.sequenceFlowConverter = new SequenceFlowConverter(factoryManager);
        this.gatewayConverter = new GatewayConverter(factoryManager);
        this.boundaryEventConverter = new BoundaryEventConverter(factoryManager);
        this.definitionResolver = definitionResolver;
    }

    public void convertNode(FlowElement flowElement, GraphBuildingContext context) {
        Node<? extends View<? extends BPMNViewDefinition>, ?> result = Match.ofNode(FlowElement.class, BPMNViewDefinition.class)
                .when(StartEvent.class, startEventConverter::convert)
                .when(EndEvent.class, endEventConverter::convert)
                .when(BoundaryEvent.class, boundaryEventConverter::convert)
                .when(IntermediateCatchEvent.class, intermediateCatchEventConverter::convert)
                .when(IntermediateThrowEvent.class, intermediateThrowEventConverter::convert)
                .when(CallActivity.class, e -> {
                    throw new UnsupportedOperationException("call activity not yet implemented");
                })
                .when(Task.class, taskConverter::convert)
                .when(Gateway.class, gatewayConverter::convert)
                .apply(flowElement)
                .value();

        context.addNode(result);
    }

    public Edge<? extends View<? extends BPMNViewDefinition>, ?> convertEdge(FlowElement flowElement, GraphBuildingContext context) {
        return Match.ofEdge(FlowElement.class, BPMNViewDefinition.class)
                .when(SequenceFlow.class, seq -> sequenceFlowConverter.convert(seq, context))
                .apply(flowElement).value();
    }
}
