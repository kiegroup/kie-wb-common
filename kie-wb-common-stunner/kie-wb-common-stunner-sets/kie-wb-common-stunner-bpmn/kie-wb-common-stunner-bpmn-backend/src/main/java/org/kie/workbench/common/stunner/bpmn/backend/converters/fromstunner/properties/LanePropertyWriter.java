package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Lane;

public class LanePropertyWriter extends BasePropertyWriter {

    private final Lane lane;

    public LanePropertyWriter(Lane lane, VariableScope variableScope) {
        super(lane, variableScope);
        this.lane = lane;
    }

    @Override
    public Lane getElement() {
        return (Lane) super.getElement();
    }

    @Override
    public void addChild(BasePropertyWriter child) {
        lane.getFlowNodeRefs().add((FlowNode) child.getElement());
    }
}
