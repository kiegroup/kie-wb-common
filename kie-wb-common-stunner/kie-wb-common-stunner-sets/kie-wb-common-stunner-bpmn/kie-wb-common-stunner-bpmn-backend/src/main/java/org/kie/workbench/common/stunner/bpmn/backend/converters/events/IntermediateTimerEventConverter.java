package org.kie.workbench.common.stunner.bpmn.backend.converters.events;

import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.CancelActivity;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.CancellingTimerEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class IntermediateTimerEventConverter {

    private final TypedFactoryManager factoryManager;

    public IntermediateTimerEventConverter(TypedFactoryManager factoryManager) {
        this.factoryManager = factoryManager;
    }

    public Node<View<IntermediateTimerEvent>, Edge> convert(CatchEvent event, TimerEventDefinition e) {
        String nodeId = event.getId();
        Node<View<IntermediateTimerEvent>, Edge> node = factoryManager.newNode(nodeId, IntermediateTimerEvent.class);

        IntermediateTimerEvent definition = node.getContent().getDefinition();

        definition.setGeneral(new BPMNGeneralSet(
                new Name(event.getName()),
                Properties.documentation(event.getDocumentation())
        ));

        definition.setExecutionSet(new CancellingTimerEventExecutionSet(
                new CancelActivity(Properties.findAnyAttributeBoolean(event, "boundaryca")),
                TimerEventDefinitionConverter.convertTimerEventDefinition(e)
        ));

        return node;
    }
}
