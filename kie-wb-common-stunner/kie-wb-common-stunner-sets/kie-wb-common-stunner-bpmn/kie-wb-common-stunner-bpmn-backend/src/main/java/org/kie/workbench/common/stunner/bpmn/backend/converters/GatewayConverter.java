package org.kie.workbench.common.stunner.bpmn.backend.converters;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BaseGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveDatabasedGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class GatewayConverter {

    private final TypedFactoryManager factoryManager;

    public GatewayConverter(TypedFactoryManager factoryManager) {
        this.factoryManager = factoryManager;
    }

    public Node<? extends View<? extends BaseGateway>, ?> convert(org.eclipse.bpmn2.Gateway gateway) {
        return Match.ofNode(org.eclipse.bpmn2.Gateway.class, BaseGateway.class)
                .when(org.eclipse.bpmn2.ParallelGateway.class, e -> factoryManager.newNode(gateway.getId(), ParallelGateway.class))
                .when(org.eclipse.bpmn2.ExclusiveGateway.class, e -> factoryManager.newNode(gateway.getId(), ExclusiveDatabasedGateway.class))
                .apply(gateway).value();
    }
}
