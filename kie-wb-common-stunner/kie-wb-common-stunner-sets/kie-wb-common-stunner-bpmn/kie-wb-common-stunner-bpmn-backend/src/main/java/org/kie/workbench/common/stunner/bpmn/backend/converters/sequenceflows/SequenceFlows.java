package org.kie.workbench.common.stunner.bpmn.backend.converters.sequenceflows;

import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.SubProcess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SequenceFlows {
    public static void link(List<FlowElement> flowElements) {
        Map<String, FlowNode> nodes = new HashMap<String, FlowNode>();
        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof FlowNode) {
                nodes.put(flowElement.getId(),
                        (FlowNode) flowElement);
                if (flowElement instanceof SubProcess) {
                    link(((SubProcess) flowElement).getFlowElements());
                }
            }
        }
        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof SequenceFlow) {
                SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
                if (sequenceFlow.getSourceRef() == null && sequenceFlow.getTargetRef() == null) {
                    String id = sequenceFlow.getId();
                    try {
                        String[] subids = id.split("-_");
                        String id1 = subids[0];
                        String id2 = "_" + subids[1];
                        FlowNode source = nodes.get(id1);
                        if (source != null) {
                            sequenceFlow.setSourceRef(source);
                        }
                        FlowNode target = nodes.get(id2);
                        if (target != null) {
                            sequenceFlow.setTargetRef(target);
                        }
                    } catch (Throwable t) {
                        // Do nothing
                    }
                }
            }
        }
    }
}
