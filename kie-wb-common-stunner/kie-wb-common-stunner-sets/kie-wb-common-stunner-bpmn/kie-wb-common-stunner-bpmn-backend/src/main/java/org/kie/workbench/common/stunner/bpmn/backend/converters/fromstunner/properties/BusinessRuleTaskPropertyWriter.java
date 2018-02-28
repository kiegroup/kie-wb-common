package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import org.eclipse.bpmn2.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.Scripts;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.RuleFlowGroup;

public class BusinessRuleTaskPropertyWriter extends ActivityPropertyWriter {

    public BusinessRuleTaskPropertyWriter(BusinessRuleTask task, VariableScope variableScope) {
        super(task, variableScope);
    }

    public void setAsync(Boolean value) {
        CustomElement.async.of(baseElement).set(value);
    }

    public void setRuleFlowGroup(RuleFlowGroup ruleFlowGroup) {
        CustomAttribute.ruleFlowGroup.of(baseElement).set(ruleFlowGroup.getValue());
    }

    public void setAdHocAutostart(Boolean value) {
        CustomElement.autoStart.of(baseElement).set(value);
    }

    public void setOnEntryAction(OnEntryAction onEntryAction) {
        Scripts.setOnEntryAction(flowElement, onEntryAction);
    }

    public void setOnExitAction(OnExitAction onExitAction) {
        Scripts.setOnExitAction(flowElement, onExitAction);
    }
}
