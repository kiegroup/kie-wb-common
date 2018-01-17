package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.Collections;
import java.util.List;

import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.IntermediateCatchEvent;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseCatchingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.CancellingTimerEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettings;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettingsValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class IntermediateCatchEventConverter {

    private final TypedFactoryManager factoryManager;
    private final DefinitionResolver definitionResolver;
    private final MessageEventDefinitionConverter messageEventDefinitionConverter;
    private final SignalEventDefinitionConverter signalEventDefinitionConverter;
    private final ErrorEventDefinitionConverter errorEventDefinitionConverter;
    private final TimerEventDefinitionConverter timerEventDefinitionConverter;

    public IntermediateCatchEventConverter(TypedFactoryManager factoryManager, DefinitionResolver definitionResolver) {
        this.factoryManager = factoryManager;
        this.definitionResolver = definitionResolver;
        this.messageEventDefinitionConverter = new MessageEventDefinitionConverter(factoryManager);
        this.errorEventDefinitionConverter = new ErrorEventDefinitionConverter(factoryManager);
        this.signalEventDefinitionConverter = new SignalEventDefinitionConverter(factoryManager, definitionResolver);
        this.timerEventDefinitionConverter = new TimerEventDefinitionConverter(factoryManager);

    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(IntermediateCatchEvent catchEvent) {
        List<EventDefinition> eventDefinitions = catchEvent.getEventDefinitions();
        Node<? extends View<? extends BaseCatchingIntermediateEvent>, ?> convertedEndEvent = convertCatchEvent(catchEvent, eventDefinitions);
        GeneralSetConverter.copyGeneralInfo(catchEvent, convertedEndEvent);

        return convertedEndEvent;
    }

    private Node<? extends View<? extends BaseCatchingIntermediateEvent>, ?> convertCatchEvent(IntermediateCatchEvent catchEvent, List<EventDefinition> eventDefinitions) {
        String nodeId = catchEvent.getId();
        switch (eventDefinitions.size()) {
            case 0:
                throw new UnsupportedOperationException("An intermediate catch event should contain exactly one definition");
            case 1:
                return Match.ofNode(EventDefinition.class, BaseCatchingIntermediateEvent.class)
                        .when(TimerEventDefinition.class, e -> timerEventDefinitionConverter.convert(e, nodeId, IntermediateTimerEvent.class))
                        .when(SignalEventDefinition.class, e -> {
                            Node<View<IntermediateSignalEventCatching>, Edge> node = signalEventDefinitionConverter.convert(e, nodeId, IntermediateSignalEventCatching.class);
                            AssignmentsInfoStringBuilder.setAssignmentsInfo(
                                    catchEvent, node.getContent().getDefinition().getDataIOSet().getAssignmentsinfo());
                            return node;
                        })
                        .when(MessageEventDefinition.class, e -> {
                            Node<View<IntermediateMessageEventCatching>, Edge> node = messageEventDefinitionConverter.convert(e, nodeId, IntermediateMessageEventCatching.class);
                            AssignmentsInfoStringBuilder.setAssignmentsInfo(
                                    catchEvent, node.getContent().getDefinition().getDataIOSet().getAssignmentsinfo());
                            return node;
                        })
                        .when(ErrorEventDefinition.class, e -> {
                            Node<View<IntermediateErrorEventCatching>, Edge> node = errorEventDefinitionConverter.convert(e, nodeId, IntermediateErrorEventCatching.class);
                            AssignmentsInfoStringBuilder.setAssignmentsInfo(
                                    catchEvent, node.getContent().getDefinition().getDataIOSet().getAssignmentsinfo());
                            return node;
                        })
                        //.when(EscalationEventDefinition.class, e -> factoryManager.newNode(nodeId, EndEscalationEvent.class))
                        //.when(CompensateEventDefinition.class, e -> factoryManager.newNode(nodeId, EndCompensationEvent.class))
                        //.when(ConditionalEventDefinition.class,     e -> factoryManager.newNode(nodeId, EndCancelEvent.class))
                        .apply(eventDefinitions.get(0)).value();
            default:
                throw new UnsupportedOperationException("Multiple definitions not supported for intermediate catch event");
        }
    }
}
