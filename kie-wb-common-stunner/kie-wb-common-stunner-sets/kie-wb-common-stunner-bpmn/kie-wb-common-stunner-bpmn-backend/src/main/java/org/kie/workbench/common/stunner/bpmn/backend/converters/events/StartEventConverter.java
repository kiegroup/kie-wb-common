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
import org.kie.workbench.common.stunner.bpmn.backend.converters.AssignmentsInfos;
import org.kie.workbench.common.stunner.bpmn.backend.converters.BPMNGeneralSets;
import org.kie.workbench.common.stunner.bpmn.backend.converters.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Match;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseStartEvent;
import org.kie.workbench.common.stunner.bpmn.definition.Executable;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.InterruptingExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.InterruptingErrorEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.InterruptingMessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRefExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.InterruptingTimerEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettings;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class StartEventConverter {

    private final TypedFactoryManager factoryManager;
    private final DefinitionResolver definitionResolver;

    public StartEventConverter(TypedFactoryManager factoryManager, DefinitionResolver definitionResolver) {
        this.factoryManager = factoryManager;
        this.definitionResolver = definitionResolver;
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
                        .when(SignalEventDefinition.class, e -> {

                            Node<View<StartSignalEvent>, Edge> node = factoryManager.newNode(nodeId, StartSignalEvent.class);

                            SignalExecutionSet executionSet = node.getContent().getDefinition().getExecutionSet();
                            SignalRef signalRef = executionSet.getSignalRef();
                            definitionResolver.resolveSignal(e.getSignalRef())
                                    .ifPresent(signal -> signalRef.setValue(signal.getName()));

                            return node;
                        })
                        .when(MessageEventDefinition.class, e -> {
                            Node<View<StartMessageEvent>, Edge> node = factoryManager.newNode(nodeId, StartMessageEvent.class);

                            StartMessageEvent definition = node.getContent().getDefinition();
                            definition.getDataIOSet().getAssignmentsinfo().setValue(Properties.getAssignmentsInfo(startEvent));

                            InterruptingMessageEventExecutionSet executionSet = definition.getExecutionSet();
                            executionSet.getMessageRef().setValue(e.getMessageRef().getName());

                            return node;
                        })
                        .when(TimerEventDefinition.class, e -> {
                            Node<View<StartTimerEvent>, Edge> node = factoryManager.newNode(nodeId, StartTimerEvent.class);

                            StartTimerEvent definition = node.getContent().getDefinition();

                            InterruptingTimerEventExecutionSet executionSet = definition.getExecutionSet();
                            executionSet.setTimerSettings(TimerEventDefinitionConverter.convertTimerEventDefinition(e));
                            executionSet.getIsInterrupting().setValue(startEvent.isIsInterrupting());

                            return node;
                        })
                        .when(ErrorEventDefinition.class, e -> {
                            Node<View<StartErrorEvent>, Edge> node = factoryManager.newNode(nodeId, StartErrorEvent.class);


                            StartErrorEvent definition = node.getContent().getDefinition();
                            definition.getDataIOSet().getAssignmentsinfo().setValue(Properties.getAssignmentsInfo(startEvent));

                            InterruptingErrorEventExecutionSet executionSet = definition.getExecutionSet();
                            executionSet.getErrorRef().setValue(e.getErrorRef().getErrorCode());

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
