package org.kie.workbench.common.stunner.bpmn.backend.converters;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class Laneconverter {

    private final TypedFactoryManager typedFactoryManager;
    private final DefinitionResolver definitionResolver;

    public Laneconverter(TypedFactoryManager typedFactoryManager, DefinitionResolver definitionResolver) {

        this.typedFactoryManager = typedFactoryManager;
        this.definitionResolver = definitionResolver;
    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(org.eclipse.bpmn2.Lane lane) {
        Node<View<Lane>, Edge> node = typedFactoryManager.newNode(lane.getId(), Lane.class);
        Lane definition = node.getContent().getDefinition();
        return node;
    }


}
