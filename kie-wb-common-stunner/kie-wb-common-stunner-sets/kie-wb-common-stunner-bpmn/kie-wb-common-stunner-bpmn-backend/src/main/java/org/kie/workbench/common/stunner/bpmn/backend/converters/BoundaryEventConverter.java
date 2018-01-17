package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.List;

import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseCatchingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class BoundaryEventConverter {

    private final TypedFactoryManager factoryManager;

    public BoundaryEventConverter(TypedFactoryManager factoryManager) {
        this.factoryManager = factoryManager;
    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(BoundaryEvent boundaryEvent) {
        List<EventDefinition> eventDefinitions = boundaryEvent.getEventDefinitions();
        Node<? extends View<? extends BaseCatchingIntermediateEvent>, ?> convertedEndEvent = convertBoundaryEvent(boundaryEvent, eventDefinitions);
        GeneralSetConverter.copyGeneralInfo(boundaryEvent, convertedEndEvent);
        return convertedEndEvent;
    }

    private Node<? extends View<? extends BaseCatchingIntermediateEvent>, ?> convertBoundaryEvent(BoundaryEvent boundaryEvent, List<EventDefinition> eventDefinitions) {
        String nodeId = boundaryEvent.getId();
        switch (eventDefinitions.size()) {
            case 0:
                throw new UnsupportedOperationException("A boundary event should contain exactly one definition");
            case 1:

                return Match.ofNode(EventDefinition.class, BaseCatchingIntermediateEvent.class)
                        .when(SignalEventDefinition.class,
                              e -> factoryManager.newNode(nodeId, IntermediateSignalEventCatching.class))

//                .when(EscalationEventDefinition.class,
//                      e -> factoryManager.newNode(nodeId, IntermediateEscalationEvent.class))
//
//                .when(ErrorEventDefinition.class,
//                      e -> factoryManager.newNode(nodeId, IntermediateErrorEvent.class))

                        .when(TimerEventDefinition.class,
                              e -> factoryManager.newNode(nodeId, IntermediateTimerEvent.class))

//                .when(CompensateEventDefinition.class,
//                      e -> factoryManager.newNode(nodeId, IntermediateCompensationEventCatching.class))
//
//                .when(ConditionalEventDefinition.class,
//                      e -> factoryManager.newNode(nodeId, IntermediateConditionalEvent.class))

                        .when(MessageEventDefinition.class,
                              e -> factoryManager.newNode(nodeId, IntermediateMessageEventCatching.class))
                        .apply(eventDefinitions.get(0)).value();
            default:
                throw new UnsupportedOperationException("Multiple definitions not supported for boundary event");

        }
    }
}
