package org.kie.workbench.common.stunner.bpmn.backend.converters.events;

import java.util.List;

import org.eclipse.bpmn2.CancelEventDefinition;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.Signal;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.TerminateEventDefinition;
import org.kie.workbench.common.stunner.bpmn.backend.converters.BPMNGeneralSets;
import org.kie.workbench.common.stunner.bpmn.backend.converters.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Match;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.AssignmentsInfos;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseEndEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.Executable;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRefExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.ScopedSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalScope;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class EndEventConverter {

    private final TypedFactoryManager factoryManager;
    private final DefinitionResolver definitionResolver;

    public EndEventConverter(TypedFactoryManager factoryManager, DefinitionResolver definitionResolver) {
        this.factoryManager = factoryManager;
        this.definitionResolver = definitionResolver;
    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(EndEvent endEvent) {
        List<EventDefinition> eventDefinitions = endEvent.getEventDefinitions();
        // properties.put(ISINTERRUPTING,
        //         startEvent.isIsInterrupting());

        Node<? extends View<? extends BaseEndEvent>, ?> convertedEndEvent = convertEndEvent(endEvent, eventDefinitions);
        BPMNGeneralSets.setProperties(endEvent, convertedEndEvent.getContent().getDefinition().getGeneral());

        return convertedEndEvent;
    }

    public Node<? extends View<? extends BaseEndEvent>, ?> convertEndEvent(EndEvent endEvent, List<EventDefinition> eventDefinitions) {
        String nodeId = endEvent.getId();
        switch (eventDefinitions.size()) {
            case 0:
                return factoryManager.newNode(nodeId, EndNoneEvent.class);
            case 1:
                return Match.ofNode(EventDefinition.class, BaseEndEvent.class)
                        .when(TerminateEventDefinition.class, e ->
                                factoryManager.newNode(nodeId, EndTerminateEvent.class))
                        .when(SignalEventDefinition.class, e -> {
                            Node<View<EndSignalEvent>, Edge> node = factoryManager.newNode(nodeId, EndSignalEvent.class);

                            EndSignalEvent definition = node.getContent().getDefinition();
                            definition.getDataIOSet().getAssignmentsinfo().setValue(Properties.getAssignmentsInfo(endEvent));

                            definition.setExecutionSet(new ScopedSignalEventExecutionSet(
                                    new SignalRef(definitionResolver.resolveSignalName(e.getSignalRef())),
                                    new SignalScope(Properties.findMetaValue(endEvent.getExtensionValues(), "customScope"))
                            ));

                            return node;
                        })
                        .when(MessageEventDefinition.class, e -> {
                            Node<View<EndMessageEvent>, Edge> node = factoryManager.newNode(nodeId, EndMessageEvent.class);

                            EndMessageEvent definition = node.getContent().getDefinition();
                            definition.getDataIOSet().getAssignmentsinfo().setValue(Properties.getAssignmentsInfo(endEvent));

                            definition.setExecutionSet(new MessageEventExecutionSet(
                                    new MessageRef(e.getMessageRef().getName())
                            ));
                            return node;
                        })
                        .when(ErrorEventDefinition.class, e -> {
                            Node<View<EndErrorEvent>, Edge> node = factoryManager.newNode(nodeId, EndErrorEvent.class);

                            EndErrorEvent definition = node.getContent().getDefinition();
                            definition.getDataIOSet().getAssignmentsinfo().setValue(Properties.getAssignmentsInfo(endEvent));

                            definition.setExecutionSet(new ErrorEventExecutionSet(
                                    new ErrorRef(e.getErrorRef().getErrorCode())
                            ));

                            return node;
                        })
                        .missing(EscalationEventDefinition.class)
                        .missing(CompensateEventDefinition.class)
                        .missing(CancelEventDefinition.class)
                        .apply(eventDefinitions.get(0)).value();
            default:
                throw new UnsupportedOperationException("Multiple event definitions not supported for end event");
        }
    }
}
