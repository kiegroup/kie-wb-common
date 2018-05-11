package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import java.util.List;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.Point;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.di;

class TestDefinitionsWriter {

    private final DefinitionResolver definitionResolver;

    TestDefinitionsWriter() {
        Definitions definitions = bpmn2.createDefinitions();
        definitions.getRootElements().add(bpmn2.createProcess());
        BPMNDiagram bpmnDiagram = di.createBPMNDiagram();
        bpmnDiagram.setPlane(di.createBPMNPlane());
        definitions.getDiagrams().add(bpmnDiagram);

        this.definitionResolver = new DefinitionResolver(definitions);
    }

    DefinitionResolver getDefinitionResolver() {
        return definitionResolver;
    }

    FlowNode mockNode(String id, Bounds bounds) {
        Task node = bpmn2.createTask();
        node.setId(id);

        BPMNShape shape = di.createBPMNShape();
        shape.setBounds(bounds);
        shape.setBpmnElement(node);
        definitionResolver.getPlane().getPlaneElement().add(shape);

        return node;
    }

    SequenceFlow sequenceFlowOf(String id, FlowNode source, FlowNode target, List<Point> waypoints) {
        SequenceFlow sequenceFlow = bpmn2.createSequenceFlow();
        sequenceFlow.setId(id);
        sequenceFlow.setSourceRef(source);
        sequenceFlow.setTargetRef(target);

        BPMNEdge edge = di.createBPMNEdge();
        edge.setBpmnElement(sequenceFlow);
        definitionResolver.getPlane().getPlaneElement().add(edge);
        edge.getWaypoint().addAll(waypoints);

        return sequenceFlow;
    }
}
