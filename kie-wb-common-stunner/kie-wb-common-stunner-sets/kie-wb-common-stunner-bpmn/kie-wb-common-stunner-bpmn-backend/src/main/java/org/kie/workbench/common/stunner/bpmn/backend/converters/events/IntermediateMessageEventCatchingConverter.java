package org.kie.workbench.common.stunner.bpmn.backend.converters.events;

import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.CancelActivity;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.CancellingMessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class IntermediateMessageEventCatchingConverter {

    private final TypedFactoryManager factoryManager;

    public IntermediateMessageEventCatchingConverter(TypedFactoryManager factoryManager) {
        this.factoryManager = factoryManager;
    }

    public Node<View<IntermediateMessageEventCatching>, Edge> convert(CatchEvent event, MessageEventDefinition e) {
        String nodeId = event.getId();
        Node<View<IntermediateMessageEventCatching>, Edge> node = factoryManager.newNode(nodeId, IntermediateMessageEventCatching.class);

        IntermediateMessageEventCatching definition = node.getContent().getDefinition();

        definition.setGeneral(new BPMNGeneralSet(
                new Name(event.getName()),
                Properties.documentation(event.getDocumentation())
        ));

        definition.getDataIOSet().getAssignmentsinfo().setValue(Properties.getAssignmentsInfo(event));

        CancellingMessageEventExecutionSet executionSet = definition.getExecutionSet();
        executionSet.getCancelActivity().setValue(Properties.findAnyAttributeBoolean(event, "boundaryca"));
        executionSet.getMessageRef().setValue(e.getMessageRef().getName());

        definition.setExecutionSet(new CancellingMessageEventExecutionSet(
                new CancelActivity(Properties.findAnyAttributeBoolean(event, "boundaryca")),
                new MessageRef(e.getMessageRef().getName())
        ));

        return node;
    }
}
