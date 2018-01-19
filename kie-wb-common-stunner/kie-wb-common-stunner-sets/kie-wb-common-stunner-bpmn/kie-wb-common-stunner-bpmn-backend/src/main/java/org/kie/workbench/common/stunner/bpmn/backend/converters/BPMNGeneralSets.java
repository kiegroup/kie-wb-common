package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.List;

import org.eclipse.bpmn2.Documentation;
import org.eclipse.bpmn2.FlowElement;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;

public class BPMNGeneralSets {
    public static void setProperties(FlowElement flowElement, BPMNGeneralSet generalInfo) {
        generalInfo.setName(new Name(flowElement.getName()));
        List<Documentation> documentation = flowElement.getDocumentation();
        if (!documentation.isEmpty()) {
            generalInfo.getDocumentation().setValue(documentation.get(0).getText());
        }
    }
}
