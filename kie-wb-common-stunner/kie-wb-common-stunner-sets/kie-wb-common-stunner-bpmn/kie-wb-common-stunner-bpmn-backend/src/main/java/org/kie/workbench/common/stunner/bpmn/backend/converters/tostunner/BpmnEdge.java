package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner;

import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public interface BpmnEdge {

    static BpmnEdge.Simple of(
            Edge<View<SequenceFlow>, Node> edge,
            BpmnNode source, Connection sourceConnection, BpmnNode target, Connection targetConnection) {
        return new BpmnEdge.Simple(edge, source, sourceConnection, target, targetConnection);
    }

    static BpmnEdge.Docked docked(BpmnNode source, BpmnNode target) {
        return new Docked(source, target);
    }


        BpmnNode getSource();
    BpmnNode getTarget();

    class Simple implements BpmnEdge {

        private final Edge<View<SequenceFlow>, Node> edge;
        private final BpmnNode source;
        private final Connection sourceConnection;
        private final BpmnNode target;
        private final Connection targetConnection;

        private Simple(Edge<View<SequenceFlow>, Node> edge, BpmnNode source, Connection sourceConnection, BpmnNode target, Connection targetConnection) {
            this.edge = edge;
            this.source = source;
            this.sourceConnection = sourceConnection;
            this.target = target;
            this.targetConnection = targetConnection;
        }

        public Edge<View<SequenceFlow>, Node> getEdge() {
            return edge;
        }

        public BpmnNode getSource() {
            return source;
        }

        public Connection getSourceConnection() {
            return sourceConnection;
        }

        public BpmnNode getTarget() {
            return target;
        }

        public Connection getTargetConnection() {
            return targetConnection;
        }
    }

    class Docked implements BpmnEdge {
        private final BpmnNode source;
        private final BpmnNode target;

        private Docked(BpmnNode source, BpmnNode target) {
            this.source = source;
            this.target = target;
        }

        @Override
        public BpmnNode getSource() {
            return source;
        }

        @Override
        public BpmnNode getTarget() {
            return target;
        }
    }
}
