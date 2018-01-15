package org.kie.workbench.common.stunner.bpmn.backend.converters;

import org.eclipse.bpmn2.Error;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.Executable;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorRef;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class ErrorEventDefinitionConverter {

    private final TypedFactoryManager factoryManager;

    public ErrorEventDefinitionConverter(TypedFactoryManager factoryManager) {
        this.factoryManager = factoryManager;
    }

    public <T extends Executable<? extends ErrorExecutionSet>> Node<View<T>, Edge> convert(ErrorEventDefinition e, String nodeId, Class<T> type) {
        return ErrorEventDefinitionConverter.convertNode(e, factoryManager.newNode(nodeId, type));
    }

    public static <T extends Node<? extends View<? extends Executable<? extends ErrorExecutionSet>>, Edge>> T convertNode(ErrorEventDefinition e, T node) {
        Executable<? extends ErrorExecutionSet> definition = node.getContent().getDefinition();
        convertMessageRef(e.getErrorRef(), definition.getExecutionSet().getErrorRef());
        return node;
    }

    public static void convertMessageRef(Error e, ErrorRef ref) {
        ref.setValue(e.getErrorCode());
    }
}
