package org.kie.workbench.common.stunner.bpmn.backend.converters;

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

    public IntermediateCatchEventConverter(TypedFactoryManager factoryManager, DefinitionResolver definitionResolver) {
        this.factoryManager = factoryManager;
        this.definitionResolver = definitionResolver;
    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(IntermediateCatchEvent catchEvent) {
        List<EventDefinition> eventDefinitions = catchEvent.getEventDefinitions();
        Node<? extends View<? extends BaseCatchingIntermediateEvent>, ?> convertedEndEvent = convertCatchEvent(catchEvent, eventDefinitions);
        copyGeneralInfo(catchEvent, convertedEndEvent);

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
                            CancellingTimerEventExecutionSet executionSet = node.getContent().getDefinition().getExecutionSet();
                            TimerSettings timerSettingsValue = convertTimerEventDefinition(e);
                            executionSet.setTimerSettings(timerSettingsValue);
                            return node;
                        })
                        .when(SignalEventDefinition.class, e -> {
                            Node<View<IntermediateSignalEventCatching>, Edge> node = factoryManager.newNode(nodeId, IntermediateSignalEventCatching.class);
                            SignalRef signalRef = node.getContent().getDefinition().getExecutionSet().getSignalRef();
                            definitionResolver.resolveSignal(e.getSignalRef())
                                    .ifPresent(signal -> signalRef.setValue(signal.getName()));
                            return node;
                        })
                        .when(MessageEventDefinition.class, e -> factoryManager.newNode(nodeId, IntermediateMessageEventCatching.class))
                        .when(ErrorEventDefinition.class, e -> factoryManager.newNode(nodeId, IntermediateErrorEventCatching.class))
                        //.when(EscalationEventDefinition.class, e -> factoryManager.newNode(nodeId, EndEscalationEvent.class))
                        //.when(CompensateEventDefinition.class, e -> factoryManager.newNode(nodeId, EndCompensationEvent.class))
                        //.when(ConditionalEventDefinition.class,     e -> factoryManager.newNode(nodeId, EndCancelEvent.class))
                        .apply(eventDefinitions.get(0)).value();
            default:
                throw new UnsupportedOperationException("Multiple definitions not supported for intermediate catch event");
        }
    }

    private TimerSettings convertTimerEventDefinition(TimerEventDefinition e) {
        TimerSettingsValue timerSettingsValue = new TimerSettings().getValue();
        FormalExpression timeCycle = (FormalExpression) e.getTimeCycle();
        timerSettingsValue.setTimeCycle(timeCycle.getMixed().getValue(0).toString());
        timerSettingsValue.setTimeCycleLanguage(timeCycle.getLanguage());

        FormalExpression timeDate = (FormalExpression) e.getTimeDate();
        timerSettingsValue.setTimeDate(timeDate.getMixed().getValue(0).toString());

        FormalExpression timeDateDuration = (FormalExpression) e.getTimeDuration();
        timerSettingsValue.setTimeDuration(timeDateDuration.getMixed().getValue(0).toString());
        return new TimerSettings(timerSettingsValue);
    }

    private void copyGeneralInfo(IntermediateCatchEvent startEvent, Node<? extends View<? extends BaseCatchingIntermediateEvent>, ?> convertedEndEvent) {
        BaseCatchingIntermediateEvent definition = convertedEndEvent.getContent().getDefinition();
        BPMNGeneralSet generalInfo = definition.getGeneral();
        generalInfo.setName(new Name(startEvent.getName()));
        List<org.eclipse.bpmn2.Documentation> documentation = startEvent.getDocumentation();
        if (!documentation.isEmpty()) {
            generalInfo.setDocumentation(new org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation(documentation.get(0).getText()));
        }
    }
}
