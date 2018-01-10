package org.kie.workbench.common.stunner.bpmn.backend.converters;

import org.eclipse.bpmn2.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SequenceFlowConverter {
    private static final Logger _logger = LoggerFactory.getLogger(SequenceFlowConverter.class);
    private TypedFactoryManager factoryManager;

    public SequenceFlowConverter(TypedFactoryManager factoryManager) {

        this.factoryManager = factoryManager;
    }
    public Edge<View<BPMNViewDefinition>, ?> convert(SequenceFlow seq) {
        return factoryManager.newEdge(seq.getId(), org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow.class);
    }
}
