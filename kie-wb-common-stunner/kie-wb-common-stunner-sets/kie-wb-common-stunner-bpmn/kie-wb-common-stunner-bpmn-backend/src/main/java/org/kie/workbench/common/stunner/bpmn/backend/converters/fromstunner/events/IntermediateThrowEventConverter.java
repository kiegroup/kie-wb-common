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

import org.eclipse.bpmn2.IntermediateThrowEvent;
import org.kie.workbench.common.stunner.bpmn.backend.converters.NodeMatch;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.ThrowEventPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.BaseThrowingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateLinkEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.escalation.EscalationEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.link.LinkEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.ScopedSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class IntermediateThrowEventConverter {

    protected final PropertyWriterFactory propertyWriterFactory;

    public IntermediateThrowEventConverter(PropertyWriterFactory propertyWriterFactory) {
        this.propertyWriterFactory = propertyWriterFactory;
    }

    public PropertyWriter toFlowElement(Node<View<BaseThrowingIntermediateEvent>, ?> node) {
        return NodeMatch.fromNode(BaseThrowingIntermediateEvent.class, PropertyWriter.class)
                .when(IntermediateMessageEventThrowing.class, this::messageEvent)
                .when(IntermediateSignalEventThrowing.class, this::signalEvent)
                .when(IntermediateLinkEventThrowing.class, this::linkEvent)
                .when(IntermediateEscalationEventThrowing.class, this::escalationEvent)
                .when(IntermediateCompensationEventThrowing.class, this::compensationEvent)
                .apply(node).value();
    }

    protected PropertyWriter signalEvent(Node<View<IntermediateSignalEventThrowing>, ?> n) {
        IntermediateThrowEvent event = bpmn2.createIntermediateThrowEvent();
        event.setId(n.getUUID());
        ThrowEventPropertyWriter p = propertyWriterFactory.of(event);

        IntermediateSignalEventThrowing definition = n.getContent().getDefinition();

        p.setAbsoluteBounds(n);

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAssignmentsInfo(definition.getDataIOSet().getAssignmentsinfo());

        ScopedSignalEventExecutionSet executionSet = definition.getExecutionSet();
        p.addSignal(executionSet.getSignalRef());
        p.addSignalScope(executionSet.getSignalScope());

        return p;
    }

    protected PropertyWriter linkEvent(Node<View<IntermediateLinkEventThrowing>, ?> n) {
        IntermediateThrowEvent event = bpmn2.createIntermediateThrowEvent();
        event.setId(n.getUUID());
        ThrowEventPropertyWriter p = propertyWriterFactory.of(event);

        IntermediateLinkEventThrowing definition = n.getContent().getDefinition();

        p.setAbsoluteBounds(n);

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAssignmentsInfo(definition.getDataIOSet().getAssignmentsinfo());

        LinkEventExecutionSet executionSet = definition.getExecutionSet();
        p.addLink(executionSet.getLinkRef());

        return p;
    }

    protected PropertyWriter messageEvent(Node<View<IntermediateMessageEventThrowing>, ?> n) {
        IntermediateThrowEvent event = bpmn2.createIntermediateThrowEvent();
        event.setId(n.getUUID());
        ThrowEventPropertyWriter p = propertyWriterFactory.of(event);

        IntermediateMessageEventThrowing definition = n.getContent().getDefinition();

        p.setAbsoluteBounds(n);

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAssignmentsInfo(definition.getDataIOSet().getAssignmentsinfo());

        MessageEventExecutionSet executionSet = definition.getExecutionSet();
        p.addMessage(executionSet.getMessageRef());

        return p;
    }

    protected PropertyWriter escalationEvent(Node<View<IntermediateEscalationEventThrowing>, ?> n) {
        IntermediateThrowEvent event = bpmn2.createIntermediateThrowEvent();
        event.setId(n.getUUID());
        ThrowEventPropertyWriter p = propertyWriterFactory.of(event);

        IntermediateEscalationEventThrowing definition = n.getContent().getDefinition();

        p.setAbsoluteBounds(n);

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAssignmentsInfo(definition.getDataIOSet().getAssignmentsinfo());

        EscalationEventExecutionSet executionSet = definition.getExecutionSet();
        p.addEscalation(executionSet.getEscalationRef());

        return p;
    }

    protected PropertyWriter compensationEvent(Node<View<IntermediateCompensationEventThrowing>, ?> n) {
        IntermediateThrowEvent event = bpmn2.createIntermediateThrowEvent();
        event.setId(n.getUUID());
        ThrowEventPropertyWriter p = propertyWriterFactory.of(event);

        IntermediateCompensationEventThrowing definition = n.getContent().getDefinition();

        p.setAbsoluteBounds(n);

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());
        p.setMetaData(definition.getAdvancedData().getMetaDataAttributes());

        p.setAssignmentsInfo(definition.getDataIOSet().getAssignmentsinfo());

        p.addCompensation();

        return p;
    }
}
