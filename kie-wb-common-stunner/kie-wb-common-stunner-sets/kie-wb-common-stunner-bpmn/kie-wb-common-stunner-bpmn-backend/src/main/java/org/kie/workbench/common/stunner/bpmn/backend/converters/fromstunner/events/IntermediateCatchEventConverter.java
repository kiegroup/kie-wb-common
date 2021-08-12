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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.events;

import org.kie.workbench.common.stunner.bpmn.backend.converters.NodeMatch;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.CatchEventPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BaseCatchingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateLinkEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.BaseCancellingEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.conditional.CancellingConditionalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.CancellingErrorEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.CancellingEscalationEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.link.LinkEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.CancellingMessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.CancellingSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.CancellingTimerEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.util.PropertyWriterUtils.getDockSourceNode;

public class IntermediateCatchEventConverter {

    protected final PropertyWriterFactory propertyWriterFactory;

    public IntermediateCatchEventConverter(PropertyWriterFactory propertyWriterFactory) {
        this.propertyWriterFactory = propertyWriterFactory;
    }

    public PropertyWriter toFlowElement(Node<View<BaseCatchingIntermediateEvent>, ?> node) {
        return NodeMatch.fromNode(BaseCatchingIntermediateEvent.class, PropertyWriter.class)
                .when(IntermediateMessageEventCatching.class, this::messageEvent)
                .when(IntermediateSignalEventCatching.class, this::signalEvent)
                .when(IntermediateLinkEventCatching.class, this::linkEvent)
                .when(IntermediateErrorEventCatching.class, this::errorEvent)
                .when(IntermediateTimerEvent.class, this::timerEvent)
                .when(IntermediateConditionalEvent.class, this::conditionalEvent)
                .when(IntermediateEscalationEvent.class, this::escalationEvent)
                .when(IntermediateCompensationEvent.class, this::compensationEvent)

                .apply(node)
                .value();
    }

    protected PropertyWriter errorEvent(Node<View<IntermediateErrorEventCatching>, ?> n) {
        CatchEventPropertyWriter p = createCatchEventPropertyWriter(n);
        p.setAbsoluteBounds(n);
        p.getFlowElement().setId(n.getUUID());

        IntermediateErrorEventCatching definition = n.getContent().getDefinition();

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAssignmentsInfo(definition.getDataIOSet().getAssignmentsinfo());

        CancellingErrorEventExecutionSet executionSet = definition.getExecutionSet();
        p.setCancelActivity(executionSet.getCancelActivity().getValue());
        p.addSlaDueDate(executionSet.getSlaDueDate());
        p.addError(executionSet.getErrorRef());

        return p;
    }

    protected PropertyWriter signalEvent(Node<View<IntermediateSignalEventCatching>, ?> n) {
        CatchEventPropertyWriter p = createCatchEventPropertyWriter(n);
        p.setAbsoluteBounds(n);
        p.getFlowElement().setId(n.getUUID());

        IntermediateSignalEventCatching definition = n.getContent().getDefinition();

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAssignmentsInfo(definition.getDataIOSet().getAssignmentsinfo());

        CancellingSignalEventExecutionSet executionSet = definition.getExecutionSet();
        p.setCancelActivity(executionSet.getCancelActivity().getValue());
        p.addSlaDueDate(executionSet.getSlaDueDate());
        p.addSignal(definition.getExecutionSet().getSignalRef());


        return p;
    }

    protected PropertyWriter linkEvent(Node<View<IntermediateLinkEventCatching>, ?> n) {
        CatchEventPropertyWriter p = createCatchEventPropertyWriter(n);
        p.setAbsoluteBounds(n);
        p.getFlowElement().setId(n.getUUID());

        IntermediateLinkEventCatching definition = n.getContent().getDefinition();

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAssignmentsInfo(definition.getDataIOSet().getAssignmentsinfo());

        LinkEventExecutionSet executionSet = definition.getExecutionSet();
        p.addLink(executionSet.getLinkRef());

        return p;
    }

    protected PropertyWriter timerEvent(Node<View<IntermediateTimerEvent>, ?> n) {
        CatchEventPropertyWriter p = createCatchEventPropertyWriter(n);
        p.setAbsoluteBounds(n);
        p.getFlowElement().setId(n.getUUID());

        IntermediateTimerEvent definition = n.getContent().getDefinition();

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAssignmentsInfo(definition.getDataIOSet().getAssignmentsinfo());

        CancellingTimerEventExecutionSet executionSet = definition.getExecutionSet();
        p.setCancelActivity(executionSet.getCancelActivity().getValue());
        p.addSlaDueDate(executionSet.getSlaDueDate());
        p.addTimer(executionSet.getTimerSettings());

        return p;
    }

    protected PropertyWriter messageEvent(Node<View<IntermediateMessageEventCatching>, ?> n) {
        CatchEventPropertyWriter p = createCatchEventPropertyWriter(n);
        p.setAbsoluteBounds(n);
        p.getFlowElement().setId(n.getUUID());

        IntermediateMessageEventCatching definition = n.getContent().getDefinition();

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAssignmentsInfo(definition.getDataIOSet().getAssignmentsinfo());

        CancellingMessageEventExecutionSet executionSet = definition.getExecutionSet();
        p.setCancelActivity(executionSet.getCancelActivity().getValue());
        p.addSlaDueDate(executionSet.getSlaDueDate());
        p.addMessage(executionSet.getMessageRef());

        return p;
    }

    protected PropertyWriter conditionalEvent(Node<View<IntermediateConditionalEvent>, ?> n) {
        CatchEventPropertyWriter p = createCatchEventPropertyWriter(n);
        p.setAbsoluteBounds(n);
        p.getFlowElement().setId(n.getUUID());

        IntermediateConditionalEvent definition = n.getContent().getDefinition();

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAssignmentsInfo(definition.getDataIOSet().getAssignmentsinfo());

        CancellingConditionalEventExecutionSet executionSet = definition.getExecutionSet();
        p.setCancelActivity(executionSet.getCancelActivity().getValue());
        p.addSlaDueDate(executionSet.getSlaDueDate());
        p.addCondition(executionSet.getConditionExpression());

        return p;
    }

    protected PropertyWriter escalationEvent(Node<View<IntermediateEscalationEvent>, ?> n) {
        CatchEventPropertyWriter p = createCatchEventPropertyWriter(n);
        p.setAbsoluteBounds(n);
        p.getFlowElement().setId(n.getUUID());

        IntermediateEscalationEvent definition = n.getContent().getDefinition();

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAssignmentsInfo(definition.getDataIOSet().getAssignmentsinfo());

        CancellingEscalationEventExecutionSet executionSet = definition.getExecutionSet();
        p.setCancelActivity(executionSet.getCancelActivity().getValue());
        p.addSlaDueDate(executionSet.getSlaDueDate());
        p.addEscalation(executionSet.getEscalationRef());

        return p;
    }

    protected PropertyWriter compensationEvent(Node<View<IntermediateCompensationEvent>, ?> n) {
        CatchEventPropertyWriter p = createCatchEventPropertyWriter(n);
        p.setAbsoluteBounds(n);
        p.getFlowElement().setId(n.getUUID());

        IntermediateCompensationEvent definition = n.getContent().getDefinition();

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAssignmentsInfo(definition.getDataIOSet().getAssignmentsinfo());

        BaseCancellingEventExecutionSet executionSet = definition.getExecutionSet();
        p.addSlaDueDate(executionSet.getSlaDueDate());
        p.addCompensation();

        return p;
    }

    protected CatchEventPropertyWriter createCatchEventPropertyWriter(Node<? extends View, ?> n) {
        return getDockSourceNode(n).isPresent() ?
                propertyWriterFactory.of(bpmn2.createBoundaryEvent()) :
                propertyWriterFactory.of(bpmn2.createIntermediateCatchEvent());
    }
}
