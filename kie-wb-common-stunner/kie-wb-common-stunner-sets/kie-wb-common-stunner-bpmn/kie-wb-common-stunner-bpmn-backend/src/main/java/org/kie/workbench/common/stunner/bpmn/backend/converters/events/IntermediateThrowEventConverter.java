package org.kie.workbench.common.stunner.bpmn.backend.converters.events;

import java.util.List;

import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.IntermediateThrowEvent;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.kie.workbench.common.stunner.bpmn.backend.converters.BPMNGeneralSets;
import org.kie.workbench.common.stunner.bpmn.backend.converters.AssignmentsInfos;
import org.kie.workbench.common.stunner.bpmn.backend.converters.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Match;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseThrowingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.Executable;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRefExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.ScopedSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class IntermediateThrowEventConverter {

    private final TypedFactoryManager factoryManager;
    private final DefinitionResolver definitionResolver;

    public IntermediateThrowEventConverter(TypedFactoryManager factoryManager, DefinitionResolver definitionResolver) {
        this.factoryManager = factoryManager;
        this.definitionResolver = definitionResolver;
    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(IntermediateThrowEvent throwEvent) {
        List<EventDefinition> eventDefinitions = throwEvent.getEventDefinitions();
        Node<? extends View<? extends BaseThrowingIntermediateEvent>, ?> convertedThrowEvent = convertThrowEvent(throwEvent, eventDefinitions);
        BPMNGeneralSets.setProperties(throwEvent, convertedThrowEvent.getContent().getDefinition().getGeneral());

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
                            Node<View<IntermediateSignalEventThrowing>, Edge> node = factoryManager.newNode(nodeId, IntermediateSignalEventThrowing.class);

                            IntermediateSignalEventThrowing definition = node.getContent().getDefinition();
                            definition.getDataIOSet().getAssignmentsinfo().setValue(Properties.getAssignmentsInfo(throwEvent));

                            ScopedSignalEventExecutionSet executionSet = definition.getExecutionSet();
                            SignalRef signalRef = executionSet.getSignalRef();
                            definitionResolver.resolveSignal(e.getSignalRef())
                                    .ifPresent(signal -> signalRef.setValue(signal.getName()));
                            executionSet.getSignalScope().setValue(Properties.findMetaValue(throwEvent, "customScope"));

                            return node;
                        })
                        .when(MessageEventDefinition.class, e -> {
                            Node<View<IntermediateMessageEventThrowing>, Edge> node = factoryManager.newNode(nodeId, IntermediateMessageEventThrowing.class);

                            IntermediateMessageEventThrowing definition = node.getContent().getDefinition();
                            definition.getDataIOSet().getAssignmentsinfo().setValue(Properties.getAssignmentsInfo(throwEvent));

                            MessageEventExecutionSet executionSet = definition.getExecutionSet();
                            executionSet.getMessageRef().setValue(e.getMessageRef().getName());

                            return node;
                        })
                        .missing(ErrorEventDefinition.class)
                        .missing(EscalationEventDefinition.class)
                        .missing(CompensateEventDefinition.class)
                        .missing(ConditionalEventDefinition.class)
                        .apply(eventDefinitions.get(0)).value();
            default:
                throw new UnsupportedOperationException("Multiple definitions not supported for intermediate throw event");
        }
    }
}
