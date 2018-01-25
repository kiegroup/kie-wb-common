package org.kie.workbench.common.stunner.bpmn.backend.converters.events;

import java.util.List;

import org.eclipse.bpmn2.CancelEventDefinition;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.TerminateEventDefinition;
import org.kie.workbench.common.stunner.bpmn.backend.converters.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Match;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseEndEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.ScopedSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalScope;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
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

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(EndEvent event) {
        List<EventDefinition> eventDefinitions = event.getEventDefinitions();
        String nodeId = event.getId();

        switch (eventDefinitions.size()) {
            case 0: {
                Node<View<EndNoneEvent>, Edge> node = factoryManager.newNode(nodeId, EndNoneEvent.class);
                EndNoneEvent definition = node.getContent().getDefinition();
                definition.setGeneral(new BPMNGeneralSet(
                        new Name(event.getName()),
                        Properties.documentation(event.getDocumentation())
                ));
                return node;
            }
            case 1:
                return Match.ofNode(EventDefinition.class, BaseEndEvent.class)
                        .when(TerminateEventDefinition.class, e -> {
                            Node<View<EndTerminateEvent>, Edge> node = factoryManager.newNode(nodeId, EndTerminateEvent.class);

                            EndTerminateEvent definition = node.getContent().getDefinition();

                            definition.setGeneral(new BPMNGeneralSet(
                                    new Name(event.getName()),
                                    Properties.documentation(event.getDocumentation())
                            ));

                            return node;
                        })
                        .when(SignalEventDefinition.class, e -> {
                            Node<View<EndSignalEvent>, Edge> node = factoryManager.newNode(nodeId, EndSignalEvent.class);

                            EndSignalEvent definition = node.getContent().getDefinition();

                            definition.setGeneral(new BPMNGeneralSet(
                                    new Name(event.getName()),
                                    Properties.documentation(event.getDocumentation())
                            ));

                            definition.getDataIOSet().getAssignmentsinfo().setValue(Properties.getAssignmentsInfo(event));

                            definition.setExecutionSet(new ScopedSignalEventExecutionSet(
                                    new SignalRef(definitionResolver.resolveSignalName(e.getSignalRef())),
                                    new SignalScope(Properties.findMetaValue(event.getExtensionValues(), "customScope"))
                            ));

                            return node;
                        })
                        .when(MessageEventDefinition.class, e -> {
                            Node<View<EndMessageEvent>, Edge> node = factoryManager.newNode(nodeId, EndMessageEvent.class);

                            EndMessageEvent definition = node.getContent().getDefinition();

                            definition.setGeneral(new BPMNGeneralSet(
                                    new Name(event.getName()),
                                    Properties.documentation(event.getDocumentation())
                            ));

                            definition.getDataIOSet().getAssignmentsinfo().setValue(Properties.getAssignmentsInfo(event));

                            definition.setExecutionSet(new MessageEventExecutionSet(
                                    new MessageRef(e.getMessageRef().getName())
                            ));
                            return node;
                        })
                        .when(ErrorEventDefinition.class, e -> {
                            Node<View<EndErrorEvent>, Edge> node = factoryManager.newNode(nodeId, EndErrorEvent.class);

                            EndErrorEvent definition = node.getContent().getDefinition();

                            definition.setGeneral(new BPMNGeneralSet(
                                    new Name(event.getName()),
                                    Properties.documentation(event.getDocumentation())
                            ));

                            definition.getDataIOSet().getAssignmentsinfo().setValue(Properties.getAssignmentsInfo(event));

                            definition.setExecutionSet(new ErrorEventExecutionSet(
                                    new ErrorRef(e.getErrorRef().getErrorCode())
                            ));

                            return node;
                        })
                        .missing(EscalationEventDefinition.class)
                        .missing(CompensateEventDefinition.class)
                        .missing(CancelEventDefinition.class)
                        .apply(eventDefinitions.get(0)).asSuccess().value();
            default:
                throw new UnsupportedOperationException("Multiple event definitions not supported for end event");
        }
    }
}
