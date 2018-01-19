package org.kie.workbench.common.stunner.bpmn.backend.converters.events;

import org.eclipse.bpmn2.SignalEventDefinition;
import org.kie.workbench.common.stunner.bpmn.backend.converters.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.definition.Executable;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class SignalEventDefinitionConverter {

    private final TypedFactoryManager factoryManager;
    private final DefinitionResolver definitionResolver;

    public SignalEventDefinitionConverter(TypedFactoryManager factoryManager, DefinitionResolver definitionResolver) {
        this.factoryManager = factoryManager;
        this.definitionResolver = definitionResolver;
    }

    public <T extends Executable<? extends SignalExecutionSet>> Node<View<T>, Edge> convert(SignalEventDefinition e, String nodeId, Class<T> type) {
        return convertNode(e, factoryManager.newNode(nodeId, type));
    }

    private <T extends Node<? extends View<? extends Executable<? extends SignalExecutionSet>>, Edge>> T convertNode(SignalEventDefinition e, T node) {
        SignalRef signalRef = node.getContent().getDefinition().getExecutionSet().getSignalRef();
        definitionResolver.resolveSignal(e.getSignalRef())
                .ifPresent(signal -> signalRef.setValue(signal.getName()));
        return node;
    }
}
