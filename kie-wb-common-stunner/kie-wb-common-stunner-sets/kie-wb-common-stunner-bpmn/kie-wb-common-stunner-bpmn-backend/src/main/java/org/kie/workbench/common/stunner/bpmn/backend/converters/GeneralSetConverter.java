package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.List;

import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.BaseCatchingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class GeneralSetConverter {
    static void copyGeneralInfo(CatchEvent startEvent, Node<? extends View<? extends BaseCatchingIntermediateEvent>, ?> convertedEndEvent) {
        BaseCatchingIntermediateEvent definition = convertedEndEvent.getContent().getDefinition();
        BPMNGeneralSet generalInfo = definition.getGeneral();
        generalInfo.setName(new Name(startEvent.getName()));
        List<Documentation> documentation = startEvent.getDocumentation();
        if (!documentation.isEmpty()) {
            generalInfo.setDocumentation(new org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation(documentation.get(0).getText()));
        }
    }
}
