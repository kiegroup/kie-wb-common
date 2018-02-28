package org.kie.workbench.common.stunner.bpmn.backend.fromstunner.properties;

import org.eclipse.bpmn2.CallActivity;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Attribute;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.CustomElement;

public class CallActivityPropertyWriter extends ActivityPropertyWriter {

    private final CallActivity activity;

    public CallActivityPropertyWriter(CallActivity activity, VariableScope variableScope) {
        super(activity, variableScope);
        this.activity = activity;
    }

    public void setIndependent(Boolean independent) {
        Attribute.independent.of(activity).set(independent);
    }

    public void setWaitForCompletion(Boolean waitForCompletion) {
        Attribute.waitForCompletion.of(activity).set(waitForCompletion);
    }

    public void setAsync(Boolean async) {
        CustomElement.async.of(activity).set(async);
    }

    public void setCalledElement(String value) {
        activity.setCalledElement(value);
    }
}
