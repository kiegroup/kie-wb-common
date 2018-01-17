package org.kie.workbench.common.stunner.bpmn.backend.converters;

import org.eclipse.bpmn2.*;
import org.eclipse.bpmn2.Process;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.DroolsPackage;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.util.Utils;
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
import static org.kie.workbench.common.stunner.bpmn.backend.legacy.Bpmn2JsonPropertyIds.ADHOCPROCESS;
import static org.kie.workbench.common.stunner.bpmn.backend.legacy.Bpmn2JsonPropertyIds.PACKAGE;
import static org.kie.workbench.common.stunner.bpmn.backend.legacy.Bpmn2JsonPropertyIds.VERSION;

public class ProcessConverter {
    private static final Logger _logger = LoggerFactory.getLogger(ProcessConverter.class);

    private final TypedFactoryManager factoryManager;
    private final DefinitionResolver definitionResolver;

    private final FlowElementConverter flowElementConverter;

    public ProcessConverter(TypedFactoryManager factoryManager, DefinitionResolver definitionResolver) {
        this.factoryManager = factoryManager;
        this.definitionResolver = definitionResolver;
        this.flowElementConverter = new FlowElementConverter(factoryManager, definitionResolver);
    }

    public Graph<DefinitionSet, Node> convert(Process process) {

        Graph<DefinitionSet, Node> graph =
                factoryManager.newGraph(process.getId(), BPMNDefinitionSet.class);

        Element<View<BPMNDiagram>> diagramView = convertDiagram(process);
        graph.addNode(diagramView.asNode());

        List<Element<? extends View<? extends BPMNViewDefinition>>> elements = convertFlowElements(process);
        for (Element<? extends View<? extends BPMNViewDefinition>> element : elements) {
            Node<? extends View<? extends BPMNViewDefinition>, Edge> node = element.asNode();
            if (node != null) graph.addNode(node);
        }

        return graph;
    }

    public Node<View<BPMNDiagram>, ?> convertDiagram(Process process) {
        Node<View<BPMNDiagram>, Edge> diagramNode = factoryManager.newNode(UUID.uuid(), BPMNDiagramImpl.class);
        View<BPMNDiagram> diagram = diagramNode.getContent();
        BPMNDiagram definition = diagram.getDefinition();
        putMetadata(process, definition);
        return diagramNode;
    }

    public void putMetadata(Process process, BPMNDiagram definition) {
        DiagramSet diagramSet = definition.getDiagramSet();
        diagramSet.getName().setValue(process.getName());
        diagramSet.getId().setValue(process.getId());

        for (FeatureMap.Entry entry : process.getAnyAttribute()) {
            if (entry.getEStructuralFeature().getName().equals("packageName")) {
                diagramSet.getPackageProperty().setValue(entry.getValue().toString());
            }
            if (entry.getEStructuralFeature().getName().equals("version")) {
                diagramSet.getVersion().setValue(entry.getValue().toString());
            }
            if (entry.getEStructuralFeature().getName().equals("adHoc")) {
                diagramSet.getAdHoc().setValue(Boolean.parseBoolean(entry.getValue().toString()));
            }
        }

        List<Documentation> documentation = process.getDocumentation();
        if (!documentation.isEmpty())
            diagramSet.getDocumentation().setValue(documentation.get(0).getText());
        diagramSet.getProcessInstanceDescription().setValue(Utils.getMetaDataValue(process.getExtensionValues(), "customDescription"));
    }

    public List<Element<? extends View<? extends BPMNViewDefinition>>> convertFlowElements(Process process) {
        return process.getFlowElements()
                        .stream()
                        // we are ignoring SequenceFlows
                        .filter(x -> ! (x instanceof SequenceFlow))
                        .map(flowElementConverter::convertNode)
                        .collect(toList());
    }
}
