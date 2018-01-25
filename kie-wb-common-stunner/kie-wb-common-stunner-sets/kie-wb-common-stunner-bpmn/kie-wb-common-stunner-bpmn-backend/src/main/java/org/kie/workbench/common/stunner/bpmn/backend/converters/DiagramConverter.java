package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.stream.Collectors;

import org.eclipse.bpmn2.Process;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.AdHoc;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Executable;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Id;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Package;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.ProcessInstanceDescription;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Version;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiagramConverter {

    private static final Logger _logger = LoggerFactory.getLogger(DiagramConverter.class);

    private final TypedFactoryManager factoryManager;

    public DiagramConverter(TypedFactoryManager factoryManager) {
        this.factoryManager = factoryManager;
    }

    public Node<View<BPMNDiagramImpl>, ?> convert(String definitionId, Process process) {
        // FIXME why must we inherit the container's id ??
        Node<View<BPMNDiagramImpl>, Edge> diagramNode = factoryManager.newNode(definitionId, BPMNDiagramImpl.class);
        BPMNDiagramImpl definition = diagramNode.getContent().getDefinition();

        definition.setDiagramSet(new DiagramSet(
                new Name(process.getName()),
                Properties.documentation(process.getDocumentation()),
                new Id(process.getId()),
                new Package(Properties.findAnyAttribute(process, "packageName")),
                new Version(Properties.findAnyAttribute(process, "version")),
                new AdHoc(Properties.findAnyAttributeBoolean(process, "adHoc")),
                new ProcessInstanceDescription(Properties.findMetaValue(process, "customDescription")),
                new Executable()
        ));

        String joinedVariables = process.getProperties()
                .stream()
                .map(p -> p.getId() + ":" + p.getItemSubjectRef().getStructureRef())
                .collect(Collectors.joining(","));
        definition.getProcessData().getProcessVariables().setValue(joinedVariables);

        return diagramNode;
    }
}
