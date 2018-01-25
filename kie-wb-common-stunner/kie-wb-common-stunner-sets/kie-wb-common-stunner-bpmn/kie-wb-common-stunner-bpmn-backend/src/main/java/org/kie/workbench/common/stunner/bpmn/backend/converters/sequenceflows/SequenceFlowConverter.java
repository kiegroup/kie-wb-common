package org.kie.workbench.common.stunner.bpmn.backend.converters.sequenceflows;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.dd.dc.Point;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.kie.workbench.common.stunner.bpmn.backend.converters.GraphBuildingContext;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.ScriptLanguages;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.SequenceFlowExecutionSet;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
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
        SequenceFlow definition = edge.getContent().getDefinition();
        definition.getGeneral().getName().setValue(seq.getName());
        FormalExpression conditionExpression = (FormalExpression) seq.getConditionExpression();
        SequenceFlowExecutionSet executionSet = definition.getExecutionSet();
        if (conditionExpression != null) {
            String body = conditionExpression.getBody();
            executionSet.getConditionExpression().setValue(body);
            String language = conditionExpression.getLanguage();
            executionSet.getConditionExpressionLanguage().setValue(ScriptLanguages.fromUri(language));
        }
        for (FeatureMap.Entry entry : seq.getAnyAttribute()) {
            if (entry.getEStructuralFeature().getName().equals("priority")) {
                executionSet.getPriority().setValue(entry.getValue().toString());
            }
        }

        String sourceId = seq.getSourceRef().getId();
        String targetId = seq.getTargetRef().getId();
        boolean isAutoConnectionSource = Properties.findMetaBoolean(seq, "isAutoConnection.source");
        boolean isAutoConnectionTarget = Properties.findMetaBoolean(seq, "isAutoConnection.target");
        context.addEdge(
                edge,
                sourceId,
                isAutoConnectionSource,
                targetId,
                isAutoConnectionTarget);

        return edge;
    }
}
