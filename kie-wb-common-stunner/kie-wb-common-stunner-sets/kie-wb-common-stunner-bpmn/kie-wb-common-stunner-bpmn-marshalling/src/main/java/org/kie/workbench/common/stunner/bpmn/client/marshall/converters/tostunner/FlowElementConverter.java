/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner;

import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.IntermediateCatchEvent;
import org.eclipse.bpmn2.IntermediateThrowEvent;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.TextAnnotation;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.ConverterUtils;

public class FlowElementConverter extends AbstractConverter {

    private final BaseConverterFactory converterFactory;

    public FlowElementConverter(BaseConverterFactory converterFactory) {
        super(converterFactory.getDefinitionResolver().getMode());
        this.converterFactory = converterFactory;
    }

    public Result<BpmnNode> convertNode(FlowElement flowElement) {
        // TODO (TiagoD): Use of BPMNElementDecorators
        if (flowElement instanceof StartEvent) {
            return converterFactory.startEventConverter().convert((StartEvent) flowElement);
        }
        if (flowElement instanceof EndEvent) {
            return converterFactory.endEventConverter().convert((EndEvent) flowElement);
        }
        if (flowElement instanceof BoundaryEvent) {
            return converterFactory.intermediateCatchEventConverter().convertBoundaryEvent((BoundaryEvent) flowElement);
        }
        if (flowElement instanceof IntermediateCatchEvent) {
            return converterFactory.intermediateCatchEventConverter().convert((IntermediateCatchEvent) flowElement);
        }
        if (flowElement instanceof IntermediateThrowEvent) {
            return converterFactory.intermediateThrowEventConverter().convert((IntermediateThrowEvent) flowElement);
        }
        if (flowElement instanceof Task) {
            return converterFactory.taskConverter().convert((Task) flowElement);
        }
        if (flowElement instanceof Gateway) {
            return converterFactory.gatewayConverter().convert((Gateway) flowElement);
        }
        if (flowElement instanceof SubProcess) {
            return converterFactory.subProcessConverter().convertSubProcess((SubProcess) flowElement);
        }
        if (flowElement instanceof CallActivity) {
            return converterFactory.callActivityConverter().convert((CallActivity) flowElement);
        }
        if (flowElement instanceof TextAnnotation) {
            return converterFactory.textAnnotationConverter().convert((TextAnnotation) flowElement);
        }
        if (flowElement instanceof SequenceFlow) {
            return Result.ignored("sequence flow");
        }
        return ConverterUtils.ignore("FlowElement", flowElement);
    }
}
