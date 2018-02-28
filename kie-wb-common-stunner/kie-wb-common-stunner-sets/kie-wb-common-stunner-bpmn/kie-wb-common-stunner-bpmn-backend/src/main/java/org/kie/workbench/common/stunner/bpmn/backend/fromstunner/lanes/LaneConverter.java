package org.kie.workbench.common.stunner.bpmn.backend.fromstunner.lanes;

import org.kie.workbench.common.stunner.bpmn.backend.converters.NodeMatch;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Result;
import org.kie.workbench.common.stunner.bpmn.backend.fromstunner.properties.LanePropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.fromstunner.properties.ProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.bpmn.backend.fromstunner.Factories.bpmn2;

public class LaneConverter {

    final PropertyWriterFactory propertyWriterFactory;

    public LaneConverter(PropertyWriterFactory propertyWriterFactory) {
        this.propertyWriterFactory = propertyWriterFactory;
    }

    public Result<LanePropertyWriter> toElement(Node<View<? extends BPMNViewDefinition>, ?> node) {
        return NodeMatch.fromNode(BPMNViewDefinition.class, LanePropertyWriter.class)
                .when(Lane.class, n -> {
                    org.eclipse.bpmn2.Lane lane = bpmn2.createLane();
                    lane.setId(n.getUUID());

                    LanePropertyWriter p = propertyWriterFactory.of(lane);

                    Lane definition = n.getContent().getDefinition();
                    BPMNGeneralSet general = definition.getGeneral();
                    lane.setName(general.getName().getValue());
                    p.setDocumentation(general.getName().getValue());

                    p.setBounds(n.getContent().getBounds());

                    return p;
                })
                .ignore(Object.class)
                .apply(node);
    }
}
