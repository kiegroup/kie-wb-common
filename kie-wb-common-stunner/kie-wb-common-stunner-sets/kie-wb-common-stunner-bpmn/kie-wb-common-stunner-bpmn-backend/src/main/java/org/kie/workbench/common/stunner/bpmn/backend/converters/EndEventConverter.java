package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.Collections;
import java.util.List;

import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.Signal;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.TerminateEventDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseEndEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.ScopedSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class EndEventConverter {

    private final TypedFactoryManager factoryManager;
    private final MessageEventDefinitionConverter messageEventDefinitionConverter;
    private final ErrorEventDefinitionConverter errorEventDefinitionConverter;
    private final DefinitionResolver definitionResolver;

    public EndEventConverter(TypedFactoryManager factoryManager, DefinitionResolver definitionResolver) {
        this.factoryManager = factoryManager;
        this.messageEventDefinitionConverter = new MessageEventDefinitionConverter(factoryManager);
        this.errorEventDefinitionConverter = new ErrorEventDefinitionConverter(factoryManager);
        this.definitionResolver = definitionResolver;
    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(EndEvent endEvent) {
        List<EventDefinition> eventDefinitions = endEvent.getEventDefinitions();
        // properties.put(ISINTERRUPTING,
        //         startEvent.isIsInterrupting());

        Node<? extends View<? extends BaseEndEvent>, ?> convertedEndEvent = convertEndEvent(endEvent, eventDefinitions);
        copyGeneralInfo(endEvent, convertedEndEvent);

        return convertedEndEvent;
    }

    private void copyGeneralInfo(EndEvent startEvent, Node<? extends View<? extends BaseEndEvent>, ?> convertedEndEvent) {
        BaseEndEvent definition = convertedEndEvent.getContent().getDefinition();
        BPMNGeneralSet generalInfo = definition.getGeneral();
        generalInfo.setName(new Name(startEvent.getName()));
        List<org.eclipse.bpmn2.Documentation> documentation = startEvent.getDocumentation();
        if (!documentation.isEmpty()) {
            generalInfo.setDocumentation(new org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation(documentation.get(0).getText()));
        }
    }

    public Node<? extends View<? extends BaseEndEvent>, ?> convertEndEvent(EndEvent endEvent, List<EventDefinition> eventDefinitions) {
        String nodeId = endEvent.getId();
        switch (eventDefinitions.size()) {
            case 0:
                return factoryManager.newNode(nodeId, EndNoneEvent.class);
            case 1:
                return Match.ofNode(EventDefinition.class, BaseEndEvent.class)
                        .when(TerminateEventDefinition.class, e -> {
                            Node<View<EndTerminateEvent>, Edge> node = factoryManager.newNode(nodeId, EndTerminateEvent.class);
//                            InterruptingSignalEventExecutionSet executionSet = node.getContent().getDefinition().get();
//                            executionSet.setIsInterrupting(new IsInterrupting(endEvent.is()));
//                            executionSet.setSignalRef(new SignalRef(e.getSignalRef()));
                            return node;
                        })
                        .when(SignalEventDefinition.class, e -> {
                            Node<View<EndSignalEvent>, Edge> node = factoryManager.newNode(nodeId, EndSignalEvent.class);
                            AssignmentsInfoStringBuilder.setAssignmentsInfo(
                                    endEvent, node.getContent().getDefinition().getDataIOSet().getAssignmentsinfo());
                            ScopedSignalEventExecutionSet executionSet = node.getContent().getDefinition().getExecutionSet();
                            executionSet.getSignalScope().setValue(Properties.findMetaValue(endEvent.getExtensionValues(), "customScope"));
                            executionSet.getSignalRef().setValue(definitionResolver.resolveSignal(e.getSignalRef()).map(Signal::getName).orElse(""));

                            return node;
                        })
                        .when(MessageEventDefinition.class, e -> {
                            Node<View<EndMessageEvent>, Edge> node = messageEventDefinitionConverter.convert(e, nodeId, EndMessageEvent.class);
                            AssignmentsInfoStringBuilder.setAssignmentsInfo(
                                    endEvent, node.getContent().getDefinition().getDataIOSet().getAssignmentsinfo());
                            return node;
                        })
                        .when(ErrorEventDefinition.class, e -> errorEventDefinitionConverter.convert(e, nodeId, EndErrorEvent.class))
                        //.when(EscalationEventDefinition.class, e -> factoryManager.newNode(nodeId, EndEscalationEvent.class))
                        //.when(CompensateEventDefinition.class, e -> factoryManager.newNode(nodeId, EndCompensationEvent.class))
                        //.when(CancelEventDefinition.class,     e -> factoryManager.newNode(nodeId, EndCancelEvent.class))
                        .apply(eventDefinitions.get(0)).value();
            default:
                throw new UnsupportedOperationException("Multiple event definitions not supported for end event");
        }
    }
}
