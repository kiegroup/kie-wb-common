package org.kie.workbench.common.stunner.bpmn.backend.converters;

import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class TypedFactoryManager {
    private final FactoryManager factoryManager;
    public TypedFactoryManager(FactoryManager factoryManager) {
        this.factoryManager = factoryManager;
    }

    public <R, U extends R> Node<View<R>, Edge> newNode(String s, Class<U> aClass) {
        return (Node<View<R>, Edge>) factoryManager.newElement(s, aClass);
    }

    public <R, U extends R> Edge<View<R>, Node> newEdge(String s, Class<U> aClass) {
        return (Edge<View<R>, Node>) factoryManager.newElement(s, aClass);
    }

    public Graph<DefinitionSet, Node> newGraph(String s, Class<?> aClass) {
        return (Graph<DefinitionSet, Node>) factoryManager.newElement(s, aClass);
    }


}
