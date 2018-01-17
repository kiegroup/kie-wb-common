package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.Collections;
import java.util.List;

import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.IntermediateThrowEvent;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.Signal;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.bpmn2.impl.OutputSetImpl;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseThrowingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.ScopedSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettings;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettingsValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class IntermediateThrowEventConverter {

    private final TypedFactoryManager factoryManager;
    private final DefinitionResolver definitionResolver;
    private final MessageEventDefinitionConverter messageEventDefinitionConverter;
    private final SignalEventDefinitionConverter signalEventDefinitionConverter;

    public IntermediateThrowEventConverter(TypedFactoryManager factoryManager, DefinitionResolver definitionResolver) {
        this.factoryManager = factoryManager;
        this.definitionResolver = definitionResolver;
        this.messageEventDefinitionConverter = new MessageEventDefinitionConverter(factoryManager);
        this.signalEventDefinitionConverter = new SignalEventDefinitionConverter(factoryManager, definitionResolver);
    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(IntermediateThrowEvent throwEvent) {
        List<EventDefinition> eventDefinitions = throwEvent.getEventDefinitions();
        Node<? extends View<? extends BaseThrowingIntermediateEvent>, ?> convertedThrowEvent = convertThrowEvent(throwEvent, eventDefinitions);
        copyGeneralInfo(throwEvent, convertedThrowEvent);

        return convertedThrowEvent;
    }

    private Node<? extends View<? extends BaseThrowingIntermediateEvent>, ?> convertThrowEvent(IntermediateThrowEvent throwEvent, List<EventDefinition> eventDefinitions) {
        String nodeId = throwEvent.getId();
        switch (eventDefinitions.size()) {
            case 0:
                throw new UnsupportedOperationException("An intermediate throw event should contain exactly one definition");
            case 1:
                return Match.ofNode(EventDefinition.class, BaseThrowingIntermediateEvent.class)
                        .when(SignalEventDefinition.class, e -> {
                            Node<View<IntermediateSignalEventThrowing>, Edge> node = signalEventDefinitionConverter.convert(e, nodeId, IntermediateSignalEventThrowing.class);

                            AssignmentsInfoStringBuilder.setAssignmentsInfo(
                                    throwEvent, node.getContent().getDefinition().getDataIOSet().getAssignmentsinfo());

                            ScopedSignalEventExecutionSet executionSet = node.getContent().getDefinition().getExecutionSet();

                            executionSet.getSignalScope().setValue(Properties.findMetaValue(throwEvent.getExtensionValues(), "customScope"));
                            executionSet.getSignalRef().setValue(definitionResolver.resolveSignal(e.getSignalRef()).map(Signal::getName).orElse(""));

                            return node;
                        })
                        .when(MessageEventDefinition.class, e -> {
                            Node<View<IntermediateMessageEventThrowing>, Edge> node = messageEventDefinitionConverter.convert(e, nodeId, IntermediateMessageEventThrowing.class);
                            AssignmentsInfoStringBuilder.setAssignmentsInfo(
                                    throwEvent, node.getContent().getDefinition().getDataIOSet().getAssignmentsinfo());
                            return node;
                        })
                        //.when(ErrorEventDefinition.class, e -> factoryManager.newNode(nodeId, IntermediateErrorEventT....class))
                        //.when(EscalationEventDefinition.class, e -> factoryManager.newNode(nodeId, EndEscalationEvent.class))
                        //.when(CompensateEventDefinition.class, e -> factoryManager.newNode(nodeId, EndCompensationEvent.class))
                        //.when(ConditionalEventDefinition.class,     e -> factoryManager.newNode(nodeId, EndCancelEvent.class))
                        .apply(eventDefinitions.get(0)).value();
            default:
                throw new UnsupportedOperationException("Multiple definitions not supported for intermediate throw event");
        }
    }

    private void copyGeneralInfo(IntermediateThrowEvent startEvent, Node<? extends View<? extends BaseThrowingIntermediateEvent>, ?> convertedEndEvent) {
        BaseThrowingIntermediateEvent definition = convertedEndEvent.getContent().getDefinition();
        BPMNGeneralSet generalInfo = definition.getGeneral();
        generalInfo.setName(new Name(startEvent.getName()));
        List<org.eclipse.bpmn2.Documentation> documentation = startEvent.getDocumentation();
        if (!documentation.isEmpty()) {
            generalInfo.setDocumentation(new org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation(documentation.get(0).getText()));
        }
    }
}
