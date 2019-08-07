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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.processes;

import java.util.Collections;
import java.util.UUID;

import org.eclipse.bpmn2.AdHocOrdering;
import org.eclipse.bpmn2.AdHocSubProcess;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.MultiInstanceLoopCharacteristics;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Result;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.ConverterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.AdHocSubProcessPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.subProcess.execution.EmbeddedSubprocessExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.subProcess.execution.EventSubprocessExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.di;
import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SubProcessConverterTest {

    private DefinitionResolver definitionResolver;

    private SubProcessConverter tested;

    @Before
    public void setUp() {
        Definitions definitions = bpmn2.createDefinitions();
        definitions.getRootElements().add(bpmn2.createProcess());
        BPMNDiagram bpmnDiagram = di.createBPMNDiagram();
        bpmnDiagram.setPlane(di.createBPMNPlane());
        definitions.getDiagrams().add(bpmnDiagram);

        definitionResolver = new DefinitionResolver(definitions, Collections.emptyList());

        Node adHocNode = new NodeImpl("");
        View<AdHocSubprocess> adHocContent =
                new ViewImpl<>(new AdHocSubprocess(), Bounds.create());
        adHocNode.setContent(adHocContent);

        Node multipleInstanceNode = new NodeImpl("");
        View<MultipleInstanceSubprocess> miContent =
                new ViewImpl<>(new MultipleInstanceSubprocess(), Bounds.create());
        multipleInstanceNode.setContent(miContent);

        Node embeddedNode = new NodeImpl("");
        View<EmbeddedSubprocess> embeddedContent =
                new ViewImpl<>(new EmbeddedSubprocess(), Bounds.create());
        embeddedNode.setContent(embeddedContent);

        Node eventNode = new NodeImpl("");
        View<EventSubprocess> eventSubprocess =
                new ViewImpl<>(new EventSubprocess(), Bounds.create());
        eventNode.setContent(eventSubprocess);

        FactoryManager factoryManager = mock(FactoryManager.class);
        when(factoryManager.newElement(anyString(), eq(getDefinitionId(AdHocSubprocess.class))))
                .thenReturn(adHocNode);
        when(factoryManager.newElement(anyString(), eq(getDefinitionId(MultipleInstanceSubprocess.class))))
                .thenReturn(multipleInstanceNode);
        when(factoryManager.newElement(anyString(), eq(getDefinitionId(EmbeddedSubprocess.class))))
                .thenReturn(embeddedNode);
        when(factoryManager.newElement(anyString(), eq(getDefinitionId(EventSubprocess.class))))
                .thenReturn(eventNode);

        TypedFactoryManager typedFactoryManager = new TypedFactoryManager(factoryManager);

        tested = new SubProcessConverter(typedFactoryManager,
                                         new PropertyReaderFactory(definitionResolver),
                                         definitionResolver,
                                         new ConverterFactory(definitionResolver, typedFactoryManager));
    }

    @Test
    public void createNode() {
        assertTrue(AdHocSubprocess.class.isInstance(tested.createNode("id").getContent().getDefinition()));
    }

    @Test
    public void createProcessData() {
        assertTrue(ProcessData.class.isInstance(tested.createProcessData("id")));
    }

    @Test
    public void testCreateAdHocSubprocessTaskExecutionSet() {
        AdHocSubProcess adHocSubProcess = mock(AdHocSubProcess.class);
        when(adHocSubProcess.getCompletionCondition()).thenReturn(mock(FormalExpression.class));
        when(adHocSubProcess.getOrdering()).thenReturn(AdHocOrdering.SEQUENTIAL);

        assertTrue(AdHocSubprocessTaskExecutionSet.class.isInstance(tested.createAdHocSubprocessTaskExecutionSet(
                new AdHocSubProcessPropertyReader(adHocSubProcess,
                                                  definitionResolver.getDiagram(),
                                                  definitionResolver))));
    }

    @Test
    public void testConvertAdHocSubprocessNode_SlaDueDate() {
        final String SLA_DUE_DATE = "12/25/1983";

        SubProcess subProcess = bpmn2.createAdHocSubProcess();
        subProcess.setId(UUID.randomUUID().toString());
        subProcess.setTriggeredByEvent(Boolean.TRUE);
        CustomElement.slaDueDate.setValue(subProcess, SLA_DUE_DATE);

        Result<BpmnNode> result = tested.convertSubProcess(subProcess);
        BpmnNode node = result.value();
        AdHocSubprocess adHocSubprocess = (AdHocSubprocess) node.value().getContent().getDefinition();
        assertNotNull(adHocSubprocess);
        assertNotNull(adHocSubprocess.getExecutionSet());

        AdHocSubprocessTaskExecutionSet executionSet = adHocSubprocess.getExecutionSet();
        SLADueDate slaDueDate = executionSet.getSlaDueDate();
        assertNotNull(slaDueDate);
        assertTrue(slaDueDate.getValue().contains(SLA_DUE_DATE));
    }

    @Test
    public void testConvertMultInstanceSubprocessNode_SlaDueDate() {
        final String SLA_DUE_DATE = "12/25/1983";

        SubProcess subProcess = bpmn2.createSubProcess();
        MultiInstanceLoopCharacteristics loopCharacteristics = bpmn2.createMultiInstanceLoopCharacteristics();
        subProcess.setLoopCharacteristics(loopCharacteristics);
        subProcess.setId(UUID.randomUUID().toString());
        CustomElement.slaDueDate.setValue(subProcess, SLA_DUE_DATE);

        Result<BpmnNode> result = tested.convertSubProcess(subProcess);
        BpmnNode node = result.value();
        MultipleInstanceSubprocess miSubProcces = (MultipleInstanceSubprocess) node.value().getContent().getDefinition();
        assertNotNull(miSubProcces);
        assertNotNull(miSubProcces.getExecutionSet());

        MultipleInstanceSubprocessTaskExecutionSet executionSet = miSubProcces.getExecutionSet();
        SLADueDate slaDueDate = executionSet.getSlaDueDate();
        assertNotNull(slaDueDate);
        assertTrue(slaDueDate.getValue().contains(SLA_DUE_DATE));
    }

    @Test
    public void testConvertEmbeddedSubprocessNode_SlaDueDate() {
        final String SLA_DUE_DATE = "12/25/1983";

        SubProcess subProcess = bpmn2.createSubProcess();
        subProcess.setId(UUID.randomUUID().toString());
        CustomElement.slaDueDate.setValue(subProcess, SLA_DUE_DATE);

        Result<BpmnNode> result = tested.convertSubProcess(subProcess);
        BpmnNode node = result.value();
        EmbeddedSubprocess embeddedSubProcces = (EmbeddedSubprocess) node.value().getContent().getDefinition();
        assertNotNull(embeddedSubProcces);
        assertNotNull(embeddedSubProcces.getExecutionSet());

        EmbeddedSubprocessExecutionSet executionSet = embeddedSubProcces.getExecutionSet();
        SLADueDate slaDueDate = executionSet.getSlaDueDate();
        assertNotNull(slaDueDate);
        assertTrue(slaDueDate.getValue().contains(SLA_DUE_DATE));
    }

    @Test
    public void testConvertEventSubprocessNode_SlaDueDate() {
        final String SLA_DUE_DATE = "12/25/1983";

        SubProcess subProcess = bpmn2.createSubProcess();
        subProcess.setId(UUID.randomUUID().toString());
        subProcess.setTriggeredByEvent(Boolean.TRUE);
        CustomElement.slaDueDate.setValue(subProcess, SLA_DUE_DATE);

        Result<BpmnNode> result = tested.convertSubProcess(subProcess);
        BpmnNode node = result.value();
        EventSubprocess eventSubprocess = (EventSubprocess) node.value().getContent().getDefinition();
        assertNotNull(eventSubprocess);
        assertNotNull(eventSubprocess.getExecutionSet());

        EventSubprocessExecutionSet executionSet = eventSubprocess.getExecutionSet();
        SLADueDate slaDueDate = executionSet.getSlaDueDate();
        assertNotNull(slaDueDate);
        assertTrue(slaDueDate.getValue().contains(SLA_DUE_DATE));
    }
}