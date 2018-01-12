package org.kie.workbench.common.stunner.bpmn.backend.converters;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class ParallelGatewayConverter {

    private final TypedFactoryManager factoryManager;

    public ParallelGatewayConverter(TypedFactoryManager factoryManager) {
        this.factoryManager = factoryManager;
    }

    public Node<View<BPMNViewDefinition>, ?> convert(org.eclipse.bpmn2.ParallelGateway parallelGateway) {
        return factoryManager.newNode(parallelGateway.getId(), ParallelGateway.class);
    }
}
