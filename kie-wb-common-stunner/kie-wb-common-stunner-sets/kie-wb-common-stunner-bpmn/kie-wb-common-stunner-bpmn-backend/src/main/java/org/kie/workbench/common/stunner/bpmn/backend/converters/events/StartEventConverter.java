package org.kie.workbench.common.stunner.bpmn.backend.converters.events;

import java.util.List;

import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.kie.workbench.common.stunner.bpmn.backend.converters.BPMNGeneralSets;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tasks.AssignmentsInfoStringBuilder;
import org.kie.workbench.common.stunner.bpmn.backend.converters.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Match;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseStartEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.IsInterrupting;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.InterruptingTimerEventExecutionSet;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class StartEventConverter {

    private final TypedFactoryManager factoryManager;
    private final DefinitionResolver definitionResolver;
    private final MessageEventDefinitionConverter messageEventDefinitionConverter;
    private final ErrorEventDefinitionConverter errorEventDefinitionConverter;
    private final SignalEventDefinitionConverter signalEventDefinitionConverter;
    private final TimerEventDefinitionConverter timerEventDefinitionConverter;

    public StartEventConverter(TypedFactoryManager factoryManager, DefinitionResolver definitionResolver) {
        this.factoryManager = factoryManager;
        this.definitionResolver = definitionResolver;
        this.messageEventDefinitionConverter = new MessageEventDefinitionConverter(factoryManager);
        this.errorEventDefinitionConverter = new ErrorEventDefinitionConverter(factoryManager);
        this.signalEventDefinitionConverter = new SignalEventDefinitionConverter(factoryManager, definitionResolver);
        this.timerEventDefinitionConverter = new TimerEventDefinitionConverter(factoryManager);
    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(StartEvent startEvent) {
        List<EventDefinition> eventDefinitions = startEvent.getEventDefinitions();
        Node<? extends View<? extends BaseStartEvent>, ?> convertedStartEvent = convertStartEvent(startEvent, eventDefinitions);
        BPMNGeneralSets.setProperties(startEvent, convertedStartEvent.getContent().getDefinition().getGeneral());

        return convertedStartEvent;
    }

    private Node<? extends View<? extends BaseStartEvent>, ?> convertStartEvent(StartEvent startEvent, List<EventDefinition> eventDefinitions) {
        String nodeId = startEvent.getId();
        switch (eventDefinitions.size()) {
            case 0:
                return factoryManager.newNode(nodeId, StartNoneEvent.class);
            case 1:
                return Match.ofNode(EventDefinition.class, BaseStartEvent.class)
                        .when(SignalEventDefinition.class, e -> signalEventDefinitionConverter.convert(e, nodeId, StartSignalEvent.class))
                        .when(MessageEventDefinition.class, e -> {
                            Node<View<StartMessageEvent>, Edge> node = messageEventDefinitionConverter.convert(e, nodeId, StartMessageEvent.class);
                            AssignmentsInfoStringBuilder.setAssignmentsInfo(
                                    startEvent, node.getContent().getDefinition().getDataIOSet().getAssignmentsinfo());
                            return node;
                        })
                        .when(TimerEventDefinition.class, e -> {
                            Node<View<StartTimerEvent>, Edge> node = timerEventDefinitionConverter.convert(e, nodeId, StartTimerEvent.class);
                            InterruptingTimerEventExecutionSet executionSet = node.getContent().getDefinition().getExecutionSet();
                            executionSet.getIsInterrupting().setValue(startEvent.isIsInterrupting());
                            return node;
                        })
                        .when(ErrorEventDefinition.class, e -> {
                            Node<View<StartErrorEvent>, Edge> node = errorEventDefinitionConverter.convert(e, nodeId, StartErrorEvent.class);
                            AssignmentsInfoStringBuilder.setAssignmentsInfo(
                                    startEvent, node.getContent().getDefinition().getDataIOSet().getAssignmentsinfo());
                            return node;
                        })
                        .missing(ConditionalEventDefinition.class)
                        .missing(EscalationEventDefinition.class)
                        .missing(CompensateEventDefinition.class)
                        .apply(eventDefinitions.get(0)).value();
            default:
                throw new UnsupportedOperationException("Multiple event definitions not supported for start event");
        }
    }
}
