package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.bpmn2.UserTask;

public class PropertyWriterFactory {

    private final VariableScope variableScope = new FlatVariableScope();

    public UserTaskPropertyWriter of(UserTask e) {
        return new UserTaskPropertyWriter(e, variableScope);
    }

    public ThrowEventPropertyWriter of(ThrowEvent e) {
        return new ThrowEventPropertyWriter(e, variableScope);
    }

    public PropertyWriter of(FlowElement e) {
        return new PropertyWriter(e, variableScope);
    }

    public CallActivityPropertyWriter of(CallActivity e) {
        return new CallActivityPropertyWriter(e, variableScope);
    }

    public BoundaryEventPropertyWriter of(BoundaryEvent e) {
        return new BoundaryEventPropertyWriter(e, variableScope);
    }

    public CatchEventPropertyWriter of(CatchEvent e) {
        return new CatchEventPropertyWriter(e, variableScope);
    }

    public BusinessRuleTaskPropertyWriter of(BusinessRuleTask e) {
        return new BusinessRuleTaskPropertyWriter(e, variableScope);
    }

    public DefinitionsPropertyWriter of(Definitions e) {
        return new DefinitionsPropertyWriter(e);
    }

    public SubProcessPropertyWriter of(SubProcess e) {
        return new SubProcessPropertyWriter(e, variableScope);
    }

    public ProcessPropertyWriter of(Process e) {
        return new ProcessPropertyWriter(e, variableScope);
    }

    public SequenceFlowPropertyWriter of(SequenceFlow e) {
        return new SequenceFlowPropertyWriter(e, variableScope);
    }

    public GatewayPropertyWriter of(Gateway e) {
        return new GatewayPropertyWriter(e, variableScope);
    }

    public LanePropertyWriter of(Lane e) {
        return new LanePropertyWriter(e, variableScope);
    }

    public ActivityPropertyWriter of(Activity e) {
        return new ActivityPropertyWriter(e, variableScope);
    }

    public ScriptTaskPropertyWriter of(ScriptTask e) {
        return new ScriptTaskPropertyWriter(e, variableScope);
    }
}
