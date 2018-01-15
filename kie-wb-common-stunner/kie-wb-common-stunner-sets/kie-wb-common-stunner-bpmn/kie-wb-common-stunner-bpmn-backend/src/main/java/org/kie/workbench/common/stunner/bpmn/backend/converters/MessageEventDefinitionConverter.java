package org.kie.workbench.common.stunner.bpmn.backend.converters;

import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.Executable;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRefExecutionSet;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class MessageEventDefinitionConverter {

    private final TypedFactoryManager factoryManager;

    public MessageEventDefinitionConverter(TypedFactoryManager factoryManager) {
        this.factoryManager = factoryManager;
    }

    public <T extends Executable<? extends MessageRefExecutionSet>> Node<View<T>, Edge> convert(MessageEventDefinition e, String nodeId, Class<T> type) {
        return MessageEventDefinitionConverter.convertNode(e, factoryManager.newNode(nodeId, type));
    }

    public static <T extends Node<? extends View<? extends Executable<? extends MessageRefExecutionSet>>, Edge>> T convertNode(MessageEventDefinition e, T node) {
        Executable<? extends MessageRefExecutionSet> definition = node.getContent().getDefinition();
        convertMessageRef(e.getMessageRef(), definition.getExecutionSet().getMessageRef());
        return node;
    }
    
    public static void convertMessageRef(Message e, MessageRef ref) {
        ref.setValue(e.getName());
    }
}
