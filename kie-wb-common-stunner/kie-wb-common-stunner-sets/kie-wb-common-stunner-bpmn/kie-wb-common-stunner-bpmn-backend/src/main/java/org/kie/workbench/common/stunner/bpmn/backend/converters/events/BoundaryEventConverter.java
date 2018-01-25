package org.kie.workbench.common.stunner.bpmn.backend.converters.events;

import java.util.List;

import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.kie.workbench.common.stunner.bpmn.backend.converters.BPMNGeneralSets;
import org.kie.workbench.common.stunner.bpmn.backend.converters.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.GraphBuildingContext;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Match;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseCatchingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.CancellingSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class BoundaryEventConverter {

    private final TypedFactoryManager factoryManager;
    private final DefinitionResolver definitionResolver;
    private final IntermediateSignalEventCatchingConverter intermediateSignalEventCatchingConverter;
    private final IntermediateTimerEventConverter intermediateTimerEventConverter;
    private IntermediateMessageEventCatchingConverter intermediateMessageEventCatchingConverter;

    public BoundaryEventConverter(TypedFactoryManager factoryManager, DefinitionResolver definitionResolver) {

        this.factoryManager = factoryManager;
        this.definitionResolver = definitionResolver;

        this.intermediateSignalEventCatchingConverter = new IntermediateSignalEventCatchingConverter(factoryManager, definitionResolver);
        this.intermediateTimerEventConverter = new IntermediateTimerEventConverter(factoryManager);
        this.intermediateMessageEventCatchingConverter = new IntermediateMessageEventCatchingConverter(factoryManager);

    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(BoundaryEvent event) {
        List<EventDefinition> eventDefinitions = event.getEventDefinitions();
        switch (eventDefinitions.size()) {
            case 0:
                throw new UnsupportedOperationException("A boundary event should contain exactly one definition");
            case 1:

                return Match.ofNode(EventDefinition.class, BaseCatchingIntermediateEvent.class)
                        .when(SignalEventDefinition.class, e -> intermediateSignalEventCatchingConverter.convert(event, e))
                        .when(TimerEventDefinition.class, e -> intermediateTimerEventConverter.convert(event, e))
                        .when(MessageEventDefinition.class, e -> intermediateMessageEventCatchingConverter.convert(event, e))
                        .missing(EscalationEventDefinition.class)
                        .missing(ErrorEventDefinition.class)
                        .missing(CompensateEventDefinition.class)
                        .missing(ConditionalEventDefinition.class)
                        .apply(eventDefinitions.get(0)).asSuccess().value();
            default:
                throw new UnsupportedOperationException("Multiple definitions not supported for boundary event");

        }
    }

    public void convertEdge(BoundaryEvent e, GraphBuildingContext context) {
        context.addDockedNode(e.getId(), e.getAttachedToRef().getId());
    }
}
