package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.Documentation;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessConverter {

    private static final Logger _logger = LoggerFactory.getLogger(ProcessConverter.class);

    private final TypedFactoryManager factoryManager;
    private final DefinitionResolver definitionResolver;

    private final FlowElementConverter flowElementConverter;

    public ProcessConverter(
            TypedFactoryManager factoryManager,
            DefinitionResolver definitionResolver) {
        this.factoryManager = factoryManager;
        this.definitionResolver = definitionResolver;
        this.flowElementConverter = new FlowElementConverter(factoryManager, definitionResolver);
    }

    public Node<View<BPMNDiagram>, ?> convertDiagram(String definitionId, Process process) {
        // FIXME why must we inherit the container's id ??
        Node<View<BPMNDiagram>, Edge> diagramNode = factoryManager.newNode(definitionId, BPMNDiagramImpl.class);
        View<BPMNDiagram> diagram = diagramNode.getContent();
        BPMNDiagram definition = diagram.getDefinition();
        putMetadata(process, definition);
        return diagramNode;
    }

    private void putMetadata(Process process, BPMNDiagram definition) {
        DiagramSet diagramSet = definition.getDiagramSet();
        diagramSet.getName().setValue(process.getName());
        diagramSet.getId().setValue(process.getId());

        String packageName = Properties.findAnyAttribute(process, "packageName");
        diagramSet.getPackageProperty().setValue(packageName);

        String version = Properties.findAnyAttribute(process, "version");
        diagramSet.getVersion().setValue(version);

        boolean adHoc = Properties.findAnyAttributeBoolean(process, "adHoc");
        diagramSet.getAdHoc().setValue(adHoc);

        List<Documentation> documentation = process.getDocumentation();
        if (!documentation.isEmpty()) {
            // fixme: how many nodes do we actually expect?
            diagramSet.getDocumentation().setValue(documentation.get(0).getText());
        }
        diagramSet.getProcessInstanceDescription()
                .setValue(Properties.findMetaValue(process, "customDescription"));

        String joinedVariables = process.getProperties()
                .stream()
                .map(p -> p.getId() + ":" + p.getItemSubjectRef().getStructureRef())
                .collect(Collectors.joining(","));
        definition.getProcessData().getProcessVariables().setValue(joinedVariables);
    }

    public void processNodes(Process process, GraphBuildingContext context) {
        process.getFlowElements()
                .stream()
                // we are excluding SequenceFlows
                .filter(x -> !(x instanceof SequenceFlow))
                .forEach(node -> flowElementConverter.convertNode(node, context));
    }

    public void processEdges(Process process, GraphBuildingContext context) {
        process.getFlowElements()
                .stream()
                // we are including only SequenceFlows
                .filter(x -> x instanceof SequenceFlow)
                .forEach(flow -> flowElementConverter.convertEdge(flow, context));
    }
}
