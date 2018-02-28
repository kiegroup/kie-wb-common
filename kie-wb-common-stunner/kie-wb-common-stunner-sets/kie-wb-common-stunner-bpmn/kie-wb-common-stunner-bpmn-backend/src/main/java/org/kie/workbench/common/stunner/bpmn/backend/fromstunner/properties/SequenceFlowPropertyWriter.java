package org.kie.workbench.common.stunner.bpmn.backend.fromstunner.properties;

import java.util.Optional;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.DiscreteConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

import static org.kie.workbench.common.stunner.bpmn.backend.fromstunner.Factories.dc;
import static org.kie.workbench.common.stunner.bpmn.backend.fromstunner.Factories.di;

public class SequenceFlowPropertyWriter extends PropertyWriter {

    private final SequenceFlow sequenceFlow;
    private BasePropertyWriter source;
    private BasePropertyWriter target;
    private BPMNEdge bpmnEdge;

    public SequenceFlowPropertyWriter(SequenceFlow sequenceFlow, VariableScope variableScope) {
        super(sequenceFlow, variableScope);
        this.sequenceFlow = sequenceFlow;
    }

    public void setAutoConnectionSource(Connection connection) {
        DiscreteConnection c = (DiscreteConnection) connection;
        CustomElement.autoConnectionSource.of(sequenceFlow).set(c.isAuto());
    }

    public void setAutoConnectionTarget(Connection connection) {
        DiscreteConnection c = (DiscreteConnection) connection;
        CustomElement.autoConnectionTarget.of(sequenceFlow).set(c.isAuto());
    }

    public void setConnection(ViewConnector<? extends BPMNViewDefinition> content) {
        Optional<Connection> sourceConnection = content.getSourceConnection();
        setAutoConnectionSource(sourceConnection.get());

        Optional<Connection> targetConnection = content.getTargetConnection();
        setAutoConnectionTarget(targetConnection.get());

        setWaypoints(content);
    }

    public void setSource(BasePropertyWriter pSrc) {
        this.source = pSrc;
        sequenceFlow.setSourceRef((FlowNode) pSrc.getElement());
        pSrc.setTarget(this);
    }

    public void setTarget(BasePropertyWriter pTgt) {
        this.target = pTgt;
        sequenceFlow.setTargetRef((FlowNode) pTgt.getElement());
        pTgt.setSource(this);
    }

    private void setWaypoints(ViewConnector<? extends BPMNViewDefinition> connector) {
        BPMNEdge bpmnEdge = di.createBPMNEdge();
        bpmnEdge.setBpmnElement(sequenceFlow);

        Point2D sourcePt = connector.getSourceConnection().get().getLocation();
        Point2D targetPt = connector.getTargetConnection().get().getLocation();

        org.eclipse.dd.dc.Point sourcePoint = dc.createPoint();
        sourcePoint.setX(
                source.getShape().getBounds().getX() + (float) sourcePt.getX());
        sourcePoint.setY(
                source.getShape().getBounds().getY() + (float) sourcePt.getY());

        org.eclipse.dd.dc.Point targetPoint = dc.createPoint();
        targetPoint.setX(
                target.getShape().getBounds().getX() + (float) targetPt.getX());
        targetPoint.setY(
                target.getShape().getBounds().getY() + (float) targetPt.getY());

        bpmnEdge.getWaypoint().add(sourcePoint);
        bpmnEdge.getWaypoint().add(targetPoint);

        this.bpmnEdge = bpmnEdge;
    }

    public BPMNEdge getEdge() {
        return bpmnEdge;
    }
}
