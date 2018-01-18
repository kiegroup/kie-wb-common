package org.kie.workbench.common.stunner.bpmn.backend.converters;

import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SequenceFlowConverter {
    private static final Logger _logger = LoggerFactory.getLogger(SequenceFlowConverter.class);
    private TypedFactoryManager factoryManager;

    public SequenceFlowConverter(TypedFactoryManager factoryManager) {

        this.factoryManager = factoryManager;
    }
    public Edge<? extends View<SequenceFlow>, ?> convert(org.eclipse.bpmn2.SequenceFlow seq, GraphBuildingContext context) {
        Edge<View<SequenceFlow>, Node> edge = factoryManager.newEdge(seq.getId(), SequenceFlow.class);
        Index<?, ?> graphIndex = context.executionContext().getGraphIndex();
        context.addEdge(edge, graphIndex.getNode(seq.getSourceRef().getId()), graphIndex.getNode(seq.getTargetRef().getId()));
        return edge;
    }
}
