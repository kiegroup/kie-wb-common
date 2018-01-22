package org.kie.workbench.common.stunner.bpmn.backend.converters.events;

import java.util.List;

import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.IntermediateCatchEvent;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.kie.workbench.common.stunner.bpmn.backend.converters.BPMNGeneralSets;
import org.kie.workbench.common.stunner.bpmn.backend.converters.AssignmentsInfos;
import org.kie.workbench.common.stunner.bpmn.backend.converters.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Match;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseCatchingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.Executable;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.CancelActivity;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.CancellingErrorEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.CancellingMessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRefExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.CancellingSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.CancellingTimerEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettings;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class IntermediateCatchEventConverter {

    private final TypedFactoryManager factoryManager;
    private final DefinitionResolver definitionResolver;

    public IntermediateCatchEventConverter(TypedFactoryManager factoryManager, DefinitionResolver definitionResolver) {
        this.factoryManager = factoryManager;
        this.definitionResolver = definitionResolver;
    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(IntermediateCatchEvent catchEvent) {
        List<EventDefinition> eventDefinitions = catchEvent.getEventDefinitions();
        Node<? extends View<? extends BaseCatchingIntermediateEvent>, ?> convertedEndEvent = convertCatchEvent(catchEvent, eventDefinitions);
        BPMNGeneralSets.setProperties(catchEvent, convertedEndEvent.getContent().getDefinition().getGeneral());

        return convertedEndEvent;
    }

    private Node<? extends View<? extends BaseCatchingIntermediateEvent>, ?> convertCatchEvent(IntermediateCatchEvent catchEvent, List<EventDefinition> eventDefinitions) {
        String nodeId = catchEvent.getId();
        switch (eventDefinitions.size()) {
            case 0:
                throw new UnsupportedOperationException("An intermediate catch event should contain exactly one definition");
            case 1:
                return Match.ofNode(EventDefinition.class, BaseCatchingIntermediateEvent.class)
                        .when(TimerEventDefinition.class, e -> {
                            Node<View<IntermediateTimerEvent>, Edge> node = factoryManager.newNode(nodeId, IntermediateTimerEvent.class);

                            IntermediateTimerEvent definition = node.getContent().getDefinition();

                            definition.setExecutionSet(new CancellingTimerEventExecutionSet(
                                    new CancelActivity(Properties.findAnyAttributeBoolean(catchEvent, "boundaryca")),
                                    TimerEventDefinitionConverter.convertTimerEventDefinition(e)
                            ));

                            return node;
                        })
                        .when(SignalEventDefinition.class, e -> {
                            Node<View<IntermediateSignalEventCatching>, Edge> node = factoryManager.newNode(nodeId, IntermediateSignalEventCatching.class);

                            IntermediateSignalEventCatching definition = node.getContent().getDefinition();
                            definition.getDataIOSet().getAssignmentsinfo().setValue(Properties.getAssignmentsInfo(catchEvent));

                            definition.setExecutionSet(new CancellingSignalEventExecutionSet(
                                    new CancelActivity(Properties.findAnyAttributeBoolean(catchEvent, "boundaryca")),
                                    new SignalRef(definitionResolver.resolveSignalName(e.getSignalRef()))
                            ));

                            return node;
                        })
                        .when(MessageEventDefinition.class, e -> {
                            Node<View<IntermediateMessageEventCatching>, Edge> node = factoryManager.newNode(nodeId, IntermediateMessageEventCatching.class);

                            IntermediateMessageEventCatching definition = node.getContent().getDefinition();
                            definition.getDataIOSet().getAssignmentsinfo().setValue(Properties.getAssignmentsInfo(catchEvent));

                            CancellingMessageEventExecutionSet executionSet = definition.getExecutionSet();
                            executionSet.getCancelActivity().setValue(Properties.findAnyAttributeBoolean(catchEvent, "boundaryca"));
                            executionSet.getMessageRef().setValue(e.getMessageRef().getName());

                            definition.setExecutionSet(new CancellingMessageEventExecutionSet(
                                    new CancelActivity(Properties.findAnyAttributeBoolean(catchEvent, "boundaryca")),
                                    new MessageRef(e.getMessageRef().getName())
                            ));

                            return node;
                        })
                        .when(ErrorEventDefinition.class, e -> {
                            Node<View<IntermediateErrorEventCatching>, Edge> node = factoryManager.newNode(nodeId, IntermediateErrorEventCatching.class);

                            IntermediateErrorEventCatching definition = node.getContent().getDefinition();
                            definition.getDataIOSet().getAssignmentsinfo().setValue(Properties.getAssignmentsInfo(catchEvent));

                            definition.setExecutionSet(new CancellingErrorEventExecutionSet(
                                    new CancelActivity(true),
                                    new ErrorRef(e.getErrorRef().getErrorCode())
                            ));

                            return node;
                        })
                        .missing(EscalationEventDefinition.class)
                        .missing(CompensateEventDefinition.class)
                        .missing(ConditionalEventDefinition.class)
                        .apply(eventDefinitions.get(0)).value();
            default:
                throw new UnsupportedOperationException("Multiple definitions not supported for intermediate catch event");
        }
    }
}
