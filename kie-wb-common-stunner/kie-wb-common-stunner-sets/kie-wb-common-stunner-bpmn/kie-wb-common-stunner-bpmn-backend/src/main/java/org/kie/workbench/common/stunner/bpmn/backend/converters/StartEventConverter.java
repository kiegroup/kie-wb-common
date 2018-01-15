package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.List;

import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseStartEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.IsInterrupting;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.InterruptingErrorEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.InterruptingSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.InterruptingTimerEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
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
        copyGeneralInfo(startEvent, convertedStartEvent);

        return convertedStartEvent;
    }

    private void copyGeneralInfo(StartEvent startEvent, Node<? extends View<? extends BaseStartEvent>, ?> convertedStartEvent) {
        BaseStartEvent definition = convertedStartEvent.getContent().getDefinition();
        BPMNGeneralSet generalInfo = definition.getGeneral();
        generalInfo.setName(new Name(startEvent.getName()));
        List<org.eclipse.bpmn2.Documentation> documentation = startEvent.getDocumentation();
        if (!documentation.isEmpty()) {
            generalInfo.setDocumentation(new Documentation(documentation.get(0).getText()));
        }
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
                            InterruptingSignalEventExecutionSet executionSet = node.getContent().getDefinition().getExecutionSet();
                            executionSet.setIsInterrupting(new IsInterrupting(startEvent.isIsInterrupting()));
                            executionSet.setSignalRef(new SignalRef(e.getSignalRef()));
                            return node;
                        })
                        .when(MessageEventDefinition.class, e -> factoryManager.newNode(nodeId, StartMessageEvent.class))
                        .when(TimerEventDefinition.class, e -> {
                            Node<View<StartTimerEvent>, Edge> node = factoryManager.newNode(nodeId, StartTimerEvent.class);
                            InterruptingTimerEventExecutionSet executionSet = node.getContent().getDefinition().getExecutionSet();
                            executionSet.setIsInterrupting(new IsInterrupting(startEvent.isIsInterrupting()));
                            return node;
                        })
                        .when(ErrorEventDefinition.class, e -> {
                            Node<View<StartErrorEvent>, Edge> node = factoryManager.newNode(nodeId, StartErrorEvent.class);
                            InterruptingErrorEventExecutionSet executionSet = node.getContent().getDefinition().getExecutionSet();
                            definitionResolver.resolveError(
                                    e.getErrorRef().getId()).ifPresent(
                                            err -> executionSet.getErrorRef().setValue(err.getErrorCode()));
                            return node;
                        })
                        // .when(ConditionalEventDefinition.class, e -> factoryManager.newNode(nodeId, StartConditionalEvent.class))
                        // .when(EscalationEventDefinition.class,  e -> factoryManager.newNode(nodeId, StartEscalationEvent.class))
                        // .when(CompensateEventDefinition.class,  e -> factoryManager.newNode(nodeId, StartCompensationEvent.class))
                        .apply(eventDefinitions.get(0)).value();
            default:
                throw new UnsupportedOperationException("Multiple event definitions not supported for start event");
        }
    }
}
