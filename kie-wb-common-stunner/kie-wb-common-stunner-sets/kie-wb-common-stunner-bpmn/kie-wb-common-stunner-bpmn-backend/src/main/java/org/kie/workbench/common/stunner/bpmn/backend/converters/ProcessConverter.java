package org.kie.workbench.common.stunner.bpmn.backend.converters;

import org.eclipse.bpmn2.*;
import org.eclipse.bpmn2.Process;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class ProcessConverter {
    private static final Logger _logger = LoggerFactory.getLogger(ProcessConverter.class);
    private final FlowElementConverter flowElementConverter;

    private TypedFactoryManager factoryManager;

    public ProcessConverter(TypedFactoryManager factoryManager) {
        this.factoryManager = factoryManager;
        this.flowElementConverter = new FlowElementConverter(factoryManager);
    }

    public Graph<DefinitionSet, Node> convert(Process process) {

        Graph<DefinitionSet, Node> graph =
                factoryManager.newGraph(process.getId(), BPMNDefinitionSet.class);

        Element<View<BPMNDiagramImpl>> diagramView = convertDiagram(process);
        graph.addNode(diagramView.asNode());

        List<Element<View<BPMNViewDefinition>>> elements = convertFlowElements(process);
        for (Element<View<BPMNViewDefinition>> element : elements) {
            Node<View<BPMNViewDefinition>, Edge> node = element.asNode();
            if (node != null) graph.addNode(node);
        }

        return graph;
    }

    public Node<View<BPMNDiagramImpl>, ?> convertDiagram(Process process) {
        Node<View<BPMNDiagramImpl>, Edge> diagramNode = factoryManager.newNode(UUID.uuid(), BPMNDiagramImpl.class);
        View<BPMNDiagramImpl> diagram = diagramNode.getContent();
        BPMNDiagramImpl definition = diagram.getDefinition();
        DiagramSet diagramSet = definition.getDiagramSet();
        diagramSet.setName(new Name(process.getName()));
        return diagramNode;
    }

    public List<Element<View<BPMNViewDefinition>>> convertFlowElements(Process process) {
        return process.getFlowElements()
                        .stream()
                        // we are ignoring SequenceFlows
                        .filter(x -> ! (x instanceof SequenceFlow))
                        .map(flowElementConverter::convertNode)
                        .collect(toList());
    }
}
