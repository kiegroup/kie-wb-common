package org.kie.workbench.common.stunner.bpmn.backend.converters;

import org.eclipse.bpmn2.*;
import org.kie.workbench.common.stunner.bpmn.definition.*;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import java.util.List;

public class EndEventConverter {
    private final TypedFactoryManager factoryManager;

    public EndEventConverter(TypedFactoryManager factoryManager) {
        this.factoryManager = factoryManager;
    }

    public Node<View<BPMNViewDefinition>, ?> convert(EndEvent endEvent) {
        List<EventDefinition> eventDefinitions = endEvent.getEventDefinitions();
        switch (eventDefinitions.size()) {
            case 0:
                return factoryManager.newNode(endEvent.getId(), EndNoneEvent.class);
            case 1:
                return Match.ofNode(EventDefinition.class, BPMNViewDefinition.class)
                        .when(TerminateEventDefinition.class,  e -> factoryManager.newNode(e.getId(), EndTerminateEvent.class))
                        .when(SignalEventDefinition.class,     e -> factoryManager.newNode(e.getId(), EndSignalEvent.class))
                        .when(MessageEventDefinition.class,    e -> factoryManager.newNode(e.getId(), EndMessageEvent.class))
                        .when(ErrorEventDefinition.class,      e -> factoryManager.newNode(e.getId(), EndErrorEvent.class))
                        //.when(EscalationEventDefinition.class, e -> factoryManager.newNode(e.getId(), EndEscalationEvent.class))
                        //.when(CompensateEventDefinition.class, e -> factoryManager.newNode(e.getId(), EndCompensationEvent.class))
                        //.when(CancelEventDefinition.class,     e -> factoryManager.newNode(e.getId(), EndCancelEvent.class))
                        .apply(eventDefinitions.get(0)).get();
            default:
                throw new UnsupportedOperationException("Multiple event definitions not supported for end event");
        }
    }
}
