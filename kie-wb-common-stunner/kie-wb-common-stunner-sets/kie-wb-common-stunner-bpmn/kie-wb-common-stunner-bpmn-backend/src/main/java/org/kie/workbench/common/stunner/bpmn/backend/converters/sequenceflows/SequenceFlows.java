/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.converters.sequenceflows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.SubProcess;

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
