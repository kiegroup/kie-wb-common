package org.kie.workbench.common.stunner.bpmn.backend.converters;

import org.eclipse.bpmn2.*;
import org.kie.workbench.common.stunner.bpmn.definition.*;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import java.util.List;

public class StartEventConverter {
    private final TypedFactoryManager factoryManager;

    public StartEventConverter(TypedFactoryManager factoryManager) {
        this.factoryManager = factoryManager;
    }

    public Node<View<BPMNViewDefinition>, ?> convert(StartEvent startEvent) {
        List<EventDefinition> eventDefinitions = startEvent.getEventDefinitions();
        // properties.put(ISINTERRUPTING,
        //         startEvent.isIsInterrupting());

        switch (eventDefinitions.size()) {
            case 0:
                return factoryManager.newNode(startEvent.getId(), StartNoneEvent.class);
            case 1:
                return Match.ofNode(EventDefinition.class, BPMNViewDefinition.class)
                        .when(SignalEventDefinition.class, e -> factoryManager.newNode(e.getId(), StartSignalEvent.class))
                        .when(MessageEventDefinition.class, e -> factoryManager.newNode(e.getId(), StartMessageEvent.class))
                        .when(TimerEventDefinition.class, e -> factoryManager.newNode(e.getId(), StartTimerEvent.class))
                        .when(ErrorEventDefinition.class, e -> factoryManager.newNode(e.getId(), StartErrorEvent.class))
                        // .when(ConditionalEventDefinition.class, e -> factoryManager.newNode(e.getId(), StartConditionalEvent.class))
                        // .when(EscalationEventDefinition.class,  e -> factoryManager.newNode(e.getId(), StartEscalationEvent.class))
                        // .when(CompensateEventDefinition.class,  e -> factoryManager.newNode(e.getId(), StartCompensationEvent.class))
                        .apply(eventDefinitions.get(0)).get();
            default:
                throw new UnsupportedOperationException("Multiple event definitions not supported for start event");
        }
    }

}
