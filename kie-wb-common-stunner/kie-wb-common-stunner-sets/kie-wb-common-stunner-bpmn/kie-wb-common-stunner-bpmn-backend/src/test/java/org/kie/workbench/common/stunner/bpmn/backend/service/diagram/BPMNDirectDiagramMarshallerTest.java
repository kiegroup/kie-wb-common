/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.backend.ApplicationFactoryManager;
import org.kie.workbench.common.stunner.backend.definition.factory.TestScopeModelFactory;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.backend.BPMNDirectDiagramMarshaller;
import org.kie.workbench.common.stunner.bpmn.definition.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.IsInterrupting;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.SignalRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskTypes;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.UserTaskExecutionSet;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.annotation.RuntimeDefinitionAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.annotation.RuntimeDefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.annotation.RuntimePropertyAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.annotation.RuntimePropertySetAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.factory.impl.EdgeFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.GraphFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.NodeFactoryImpl;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.backend.service.diagram.Assertions.assertDiagram;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BPMNDirectDiagramMarshallerTest {

    static final String BPMN_DEF_SET_ID = BindableAdapterUtils.getDefinitionSetId(BPMNDefinitionSet.class);

    private static final String BPMN_BASIC = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/basic.bpmn";
    private static final String BPMN_EVALUATION = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/evaluation.bpmn";
    private static final String BPMN_LANES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/lanes.bpmn";
    private static final String BPMN_BOUNDARY_EVENTS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/boundaryIntmEvent.bpmn";
    private static final String BPMN_NOT_BOUNDARY_EVENTS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/notBoundaryIntmEvent.bpmn";
    private static final String BPMN_PROCESSVARIABLES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/processVariables.bpmn";
    private static final String BPMN_USERTASKASSIGNMENTS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/userTaskAssignments.bpmn";
    private static final String BPMN_BUSINESSRULETASKASSIGNMENTS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/businessRuleTaskAssignments.bpmn";
    private static final String BPMN_STARTNONEEVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/startNoneEvent.bpmn";
    private static final String BPMN_STARTTIMEREVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/startTimerEvent.bpmn";
    private static final String BPMN_STARTSIGNALEVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/startSignalEvent.bpmn";
    private static final String BPMN_STARTMESSAGEEVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/startMessageEvent.bpmn";
    private static final String BPMN_STARTERROREVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/startErrorEvent.bpmn";
    private static final String BPMN_INTERMEDIATE_SIGNAL_EVENTCATCHING = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/intermediateSignalEventCatching.bpmn";
    private static final String BPMN_INTERMEDIATE_ERROR_EVENTCATCHING = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/intermediateErrorEventCatching.bpmn";
    private static final String BPMN_INTERMEDIATE_SIGNAL_EVENTTHROWING = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/intermediateSignalEventThrowing.bpmn";
    private static final String BPMN_INTERMEDIATE_MESSAGE_EVENTCATCHING = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/intermediateMessageEventCatching.bpmn";
    private static final String BPMN_INTERMEDIATE_MESSAGE_EVENTTHROWING = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/intermediateMessageEventThrowing.bpmn";
    private static final String BPMN_INTERMEDIATE_TIMER_EVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/intermediateTimerEvent.bpmn";
    private static final String BPMN_ENDSIGNALEVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/endSignalEvent.bpmn";
    private static final String BPMN_ENDMESSAGEEVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/endMessageEvent.bpmn";
    private static final String BPMN_ENDNONEEVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/endNoneEvent.bpmn";
    private static final String BPMN_ENDTERMINATEEVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/endTerminateEvent.bpmn";
    private static final String BPMN_PROCESSPROPERTIES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/processProperties.bpmn";
    private static final String BPMN_BUSINESSRULETASKRULEFLOWGROUP = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/businessRuleTask.bpmn";
    private static final String BPMN_REUSABLE_SUBPROCESS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/reusableSubprocessCalledElement.bpmn";
    private static final String BPMN_EMBEDDED_SUBPROCESS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/embeddedSubprocess.bpmn";
    private static final String BPMN_SCRIPTTASK = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/scriptTask.bpmn";
    private static final String BPMN_USERTASKASSIGNEES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/userTaskAssignees.bpmn";
    private static final String BPMN_USERTASKPROPERTIES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/userTaskProperties.bpmn";
    private static final String BPMN_SEQUENCEFLOW = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/sequenceFlow.bpmn";
    private static final String BPMN_XORGATEWAY = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/xorGateway.bpmn";
        private static final String BPMN_TIMER_EVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/timerEvent.bpmn";
    private static final String BPMN_SIMULATIONPROPERTIES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/simulationProperties.bpmn";
    private static final String BPMN_MAGNETDOCKERS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/magnetDockers.bpmn";
    private static final String BPMN_MAGNETSINLANE = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/magnetsInLane.bpmn";
    private static final String BPMN_ENDERROR_EVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/endErrorEvent.bpmn";

    @Mock
    DefinitionManager definitionManager;

    @Mock
    AdapterManager adapterManager;

    @Mock
    AdapterRegistry adapterRegistry;

    ApplicationFactoryManager applicationFactoryManager;

    private BPMNDirectDiagramMarshaller tested;

    private Diagram<Graph, Metadata> unmarshall(String s) throws Exception {
        return Unmarshalling.unmarshall(tested, s);
    }

    private Diagram<Graph, Metadata> unmarshall(ByteArrayInputStream s) throws Exception {
        return Unmarshalling.unmarshall(tested, s);
    }

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {

        // Graph utils.
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.registry()).thenReturn(adapterRegistry);
        // initApplicationFactoryManagerAlt();

        DefinitionUtils definitionUtils = new DefinitionUtils(definitionManager,
                                                              applicationFactoryManager);
        TestScopeModelFactory testScopeModelFactory = new TestScopeModelFactory(new BPMNDefinitionSet.BPMNDefinitionSetBuilder().build());
        // Definition manager.
        final RuntimeDefinitionAdapter definitionAdapter = new RuntimeDefinitionAdapter(definitionUtils);
        final RuntimeDefinitionSetAdapter definitionSetAdapter = new RuntimeDefinitionSetAdapter(definitionAdapter);
        final RuntimePropertySetAdapter propertySetAdapter = new RuntimePropertySetAdapter();
        final RuntimePropertyAdapter propertyAdapter = new RuntimePropertyAdapter();
        mockAdapterManager(definitionAdapter, definitionSetAdapter, propertySetAdapter, propertyAdapter);
        mockAdapterRegistry(definitionAdapter, definitionSetAdapter, propertySetAdapter, propertyAdapter);
        applicationFactoryManager = new MockApplicationFactoryManager(
                new GraphFactoryImpl(definitionManager),
                testScopeModelFactory,
                new EdgeFactoryImpl(definitionManager),
                new NodeFactoryImpl(definitionUtils)
        );

        // The tested BPMN marshaller.
        tested = new BPMNDirectDiagramMarshaller(applicationFactoryManager);
    }

    private void mockAdapterRegistry(RuntimeDefinitionAdapter definitionAdapter, RuntimeDefinitionSetAdapter definitionSetAdapter, RuntimePropertySetAdapter propertySetAdapter, RuntimePropertyAdapter propertyAdapter) {
        when(adapterRegistry.getDefinitionSetAdapter(any(Class.class))).thenReturn(definitionSetAdapter);
        when(adapterRegistry.getDefinitionAdapter(any(Class.class))).thenReturn(definitionAdapter);
        when(adapterRegistry.getPropertySetAdapter(any(Class.class))).thenReturn(propertySetAdapter);
        when(adapterRegistry.getPropertyAdapter(any(Class.class))).thenReturn(propertyAdapter);
    }

    private void mockAdapterManager(RuntimeDefinitionAdapter definitionAdapter, RuntimeDefinitionSetAdapter definitionSetAdapter, RuntimePropertySetAdapter propertySetAdapter, RuntimePropertyAdapter propertyAdapter) {
        when(adapterManager.forDefinitionSet()).thenReturn(definitionSetAdapter);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(adapterManager.forPropertySet()).thenReturn(propertySetAdapter);
        when(adapterManager.forProperty()).thenReturn(propertyAdapter);
    }

    // 4 nodes expected: BPMNDiagram, StartNode, Task and EndNode
    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallBasic() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_BASIC);
        assertDiagram(diagram, 4);
        assertEquals("Basic process", diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> task1 = diagram.getGraph().getNode("810797AB-7D09-4E1F-8A5B-96C424E4B031");
        assertTrue(task1.getContent().getDefinition() instanceof NoneTask);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallUserTaskAssignments() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_USERTASKASSIGNMENTS);
        assertDiagram(diagram,
                      8);
        assertEquals("UserTaskAssignments",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> selfEvaluationNode = diagram.getGraph().getNode("_6063D302-9D81-4C86-920B-E808A45377C2");
        UserTask selfEvaluationTask = (UserTask) selfEvaluationNode.getContent().getDefinition();
        assertEquals(selfEvaluationTask.getTaskType().getValue(),
                     TaskTypes.USER);
        UserTaskExecutionSet executionSet = selfEvaluationTask.getExecutionSet();
        AssignmentsInfo assignmentsinfo = executionSet.getAssignmentsinfo();
         assertEquals(assignmentsinfo.getValue(),
                      "|reason:com.test.Reason,Comment:Object,Skippable:Object||performance:Object|[din]reason->reason,[dout]performance->performance");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallStartNoneEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTNONEEVENT);
        assertDiagram(diagram,
                      4);
        assertEquals("startNoneEvent",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> startNoneEventNode = diagram.getGraph().getNode("processStartEvent");
        StartNoneEvent startNoneEvent = (StartNoneEvent) startNoneEventNode.getContent().getDefinition();
        assertNotNull(startNoneEvent.getGeneral());
        assertEquals("MyStartNoneEvent",
                     startNoneEvent.getGeneral().getName().getValue());
        assertEquals("MyStartNoneEventDocumentation",
                     startNoneEvent.getGeneral().getDocumentation().getValue());
    }


    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallStartTimerEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTTIMEREVENT);
        assertDiagram(diagram,
                      4);
        assertEquals("StartTimerEvent",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> startTimerEventNode = diagram.getGraph().getNode("_49ADC988-B63D-4AEB-B811-67969F305FD0");
        StartTimerEvent startTimerEvent = (StartTimerEvent) startTimerEventNode.getContent().getDefinition();
        IsInterrupting isInterrupting = startTimerEvent.getExecutionSet().getIsInterrupting();
        assertEquals(false,
                     isInterrupting.getValue());
    }


    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallStartSignalEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTSIGNALEVENT);
        assertDiagram(diagram,
                      4);
        assertEquals("StartSignalEvent",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> startSignalEventNode = diagram.getGraph().getNode("_1876844A-4DAC-4214-8BCD-2ABA3FCC8EB5");
        StartSignalEvent startSignalEvent = (StartSignalEvent) startSignalEventNode.getContent().getDefinition();
        assertNotNull(startSignalEvent.getExecutionSet());
        SignalRef signalRef = startSignalEvent.getExecutionSet().getSignalRef();
        assertEquals("sig1",
                     signalRef.getValue());
    }


    @Test
    public void testUnmarshallStartErrorEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTERROREVENT);
        assertDiagram(diagram,
                      3);
        assertEquals("startErrorEventProcess",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> startEventNode = diagram.getGraph().getNode("3BD5BBC8-F1C7-45DE-8BDF-A06D8464A61B");
        StartErrorEvent startErrorEvent = (StartErrorEvent) startEventNode.getContent().getDefinition();
        assertNotNull(startErrorEvent.getGeneral());
        assertEquals("MyStartErrorEvent",
                     startErrorEvent.getGeneral().getName().getValue());
        assertEquals("MyStartErrorEventDocumentation",
                     startErrorEvent.getGeneral().getDocumentation().getValue());
        assertNotNull(startErrorEvent.getExecutionSet());
        assertNotNull(startErrorEvent.getExecutionSet().getErrorRef());
        assertEquals("MyError",
                     startErrorEvent.getExecutionSet().getErrorRef().getValue());

        DataIOSet dataIOSet = startErrorEvent.getDataIOSet();
        AssignmentsInfo assignmentsInfo = dataIOSet.getAssignmentsinfo();
        assertEquals("||errorOutput_:String||[dout]errorOutput_->var1",
                     assignmentsInfo.getValue());
    }


    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallStartMessageEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTMESSAGEEVENT);
        assertDiagram(diagram,
                      2);
        assertEquals("StartMessageEvent",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> startMessageEventNode = diagram.getGraph().getNode("_34C4BBFC-544F-4E23-B17B-547BB48EEB63");
        StartMessageEvent startMessageEvent = (StartMessageEvent) startMessageEventNode.getContent().getDefinition();
        assertNotNull(startMessageEvent.getExecutionSet());
        MessageRef messageRef = startMessageEvent.getExecutionSet().getMessageRef();
        IsInterrupting isInterrupting = startMessageEvent.getExecutionSet().getIsInterrupting();
        assertEquals("msgref",
                     messageRef.getValue());
        assertEquals(true,
                     isInterrupting.getValue());
        DataIOSet dataIOSet = startMessageEvent.getDataIOSet();
        AssignmentsInfo assignmentsInfo = dataIOSet.getAssignmentsinfo();
        assertEquals("||StartMessageEventOutputVar1:String||[dout]StartMessageEventOutputVar1->var1",
                     assignmentsInfo.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallIntermediateTimerEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_TIMER_EVENT);
        assertDiagram(diagram,
                      2);
        assertEquals("intermediateTimer",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> intermediateEventNode = diagram.getGraph().getNode("_8D881072-284F-4F0D-8CF2-AD1F4540FC4E");
        IntermediateTimerEvent intermediateTimerEvent = (IntermediateTimerEvent) intermediateEventNode.getContent().getDefinition();
        assertNotNull(intermediateTimerEvent.getGeneral());
        assertEquals("MyTimer",
                     intermediateTimerEvent.getGeneral().getName().getValue());
        assertNotNull(intermediateTimerEvent.getExecutionSet());
        assertEquals("abc",
                     intermediateTimerEvent.getExecutionSet().getTimerSettings().getValue().getTimeCycle());
        assertEquals("none",
                     intermediateTimerEvent.getExecutionSet().getTimerSettings().getValue().getTimeCycleLanguage());
        assertEquals("abc",
                     intermediateTimerEvent.getExecutionSet().getTimerSettings().getValue().getTimeDate());
        assertEquals("abc",
                     intermediateTimerEvent.getExecutionSet().getTimerSettings().getValue().getTimeDuration());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallIntermediateSignalEventCatching() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_SIGNAL_EVENTCATCHING);
        assertDiagram(diagram,
                      2);
        assertEquals("intermediateSignalCatching",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> intermediateEventNode = diagram.getGraph().getNode("_2C9B14A3-F663-476D-9FDF-31590D3A9CC5");
        IntermediateSignalEventCatching intermediateSignalEventCatching = (IntermediateSignalEventCatching) intermediateEventNode.getContent().getDefinition();
        assertNotNull(intermediateSignalEventCatching.getGeneral());
        assertEquals("MySignalCatchingEvent",
                     intermediateSignalEventCatching.getGeneral().getName().getValue());
        assertEquals("MySignalCatchingEventDocumentation",
                     intermediateSignalEventCatching.getGeneral().getDocumentation().getValue());
        assertNotNull(intermediateSignalEventCatching.getExecutionSet());
        assertEquals(true,
                     intermediateSignalEventCatching.getExecutionSet().getCancelActivity().getValue());
        assertEquals("MySignal",
                     intermediateSignalEventCatching.getExecutionSet().getSignalRef().getValue());

        DataIOSet dataIOSet = intermediateSignalEventCatching.getDataIOSet();
        AssignmentsInfo assignmentsInfo = dataIOSet.getAssignmentsinfo();
        assertEquals("||output1_:String||[dout]output1_->var1",
                     assignmentsInfo.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallIntermediateErrorEventCatching() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_ERROR_EVENTCATCHING);
        assertDiagram(diagram,
                      2);
        assertEquals("intermediateErrorCatching",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> intermediateEventNode = diagram.getGraph().getNode("80A2A7A9-7C68-408C-BE3B-467562A2C139");
        IntermediateErrorEventCatching intermediateErrorEventCatching = (IntermediateErrorEventCatching) intermediateEventNode.getContent().getDefinition();
        assertNotNull(intermediateErrorEventCatching.getGeneral());
        assertEquals("MyErrorCatchingEvent",
                     intermediateErrorEventCatching.getGeneral().getName().getValue());
        assertEquals("MyErrorCatchingEventDocumentation",
                     intermediateErrorEventCatching.getGeneral().getDocumentation().getValue());
        assertNotNull(intermediateErrorEventCatching.getExecutionSet());
        assertEquals(true,
                     intermediateErrorEventCatching.getExecutionSet().getCancelActivity().getValue());
        assertEquals("MyError",
                     intermediateErrorEventCatching.getExecutionSet().getErrorRef().getValue());

        DataIOSet dataIOSet = intermediateErrorEventCatching.getDataIOSet();
        AssignmentsInfo assignmentsInfo = dataIOSet.getAssignmentsinfo();
        assertEquals("||theErrorEventOutput:String||[dout]theErrorEventOutput->errorVar",
                     assignmentsInfo.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallIntermediateSignalEventThrowing() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_SIGNAL_EVENTTHROWING);
        assertDiagram(diagram,
                      2);
        assertEquals("intermediateSignalThrowing",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> intermediateEventNode = diagram.getGraph().getNode("_A45EC77D-5414-4348-BA8F-05C4FFD660EE");
        IntermediateSignalEventThrowing intermediateSignalEventThrowing = (IntermediateSignalEventThrowing) intermediateEventNode.getContent().getDefinition();
        assertNotNull(intermediateSignalEventThrowing.getGeneral());
        assertEquals("MySignalThrowingEvent",
                     intermediateSignalEventThrowing.getGeneral().getName().getValue());
        assertEquals("MySignalThrowingEventDocumentation",
                     intermediateSignalEventThrowing.getGeneral().getDocumentation().getValue());
        assertNotNull(intermediateSignalEventThrowing.getExecutionSet());
        assertEquals("processInstance",
                     intermediateSignalEventThrowing.getExecutionSet().getSignalScope().getValue());
        assertEquals("MySignal",
                     intermediateSignalEventThrowing.getExecutionSet().getSignalRef().getValue());

        DataIOSet dataIOSet = intermediateSignalEventThrowing.getDataIOSet();
        AssignmentsInfo assignmentsInfo = dataIOSet.getAssignmentsinfo();
        assertEquals("_input1:String||||[din]var1->_input1",
                     assignmentsInfo.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallIntermediateMessageEventCatching() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_MESSAGE_EVENTCATCHING);
        assertDiagram(diagram,
                      2);
        assertEquals("IntermediateMessageEventCatching",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> intermediateMessageEventCatchingNode = diagram.getGraph().getNode("_BD708E30-CA48-4051-BAEA-BBCB5F396CEE");
        IntermediateMessageEventCatching intermediateMessageEventCatching = (IntermediateMessageEventCatching) intermediateMessageEventCatchingNode.getContent().getDefinition();

        assertNotNull(intermediateMessageEventCatching.getExecutionSet());
        MessageRef messageRef = intermediateMessageEventCatching.getExecutionSet().getMessageRef();
        assertEquals("msgref1",
                     messageRef.getValue());
        DataIOSet dataIOSet = intermediateMessageEventCatching.getDataIOSet();
        AssignmentsInfo assignmentsInfo = dataIOSet.getAssignmentsinfo();
        assertEquals("||IntermediateMessageEventCatchingOutputVar1:String||[dout]IntermediateMessageEventCatchingOutputVar1->var1",
                     assignmentsInfo.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallIntermediateMessageEventThrowing() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_MESSAGE_EVENTTHROWING);
        assertDiagram(diagram,
                      2);
        assertEquals("IntermediateMessageEventThrowing",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> intermediateMessageEventThrowingNode = diagram.getGraph().getNode("_85823DF6-02A0-4B8D-AE7A-61641A3A2E4B");
        IntermediateMessageEventThrowing intermediateMessageEventThrowing = (IntermediateMessageEventThrowing) intermediateMessageEventThrowingNode.getContent().getDefinition();

        assertNotNull(intermediateMessageEventThrowing.getExecutionSet());
        MessageRef messageRef = intermediateMessageEventThrowing.getExecutionSet().getMessageRef();
        assertEquals("msgref",
                     messageRef.getValue());
        DataIOSet dataIOSet = intermediateMessageEventThrowing.getDataIOSet();
        AssignmentsInfo assignmentsInfo = dataIOSet.getAssignmentsinfo();
        assertEquals("IntermediateMessageEventThrowingInputVar1:String||||[din]var1->IntermediateMessageEventThrowingInputVar1",
                     assignmentsInfo.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallEndNoneEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ENDNONEEVENT);
        assertDiagram(diagram,
                      3);
        assertEquals("endNoneEvent",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> endNoneEventNode = diagram.getGraph().getNode("_9DF2C9D3-15DF-4436-B6C6-85B58B8696B6");
        EndNoneEvent endNoneEvent = (EndNoneEvent) endNoneEventNode.getContent().getDefinition();
        assertNotNull(endNoneEvent.getGeneral());
        assertEquals("MyEndNoneEvent",
                     endNoneEvent.getGeneral().getName().getValue());
        assertEquals("MyEndNoneEventDocumentation",
                     endNoneEvent.getGeneral().getDocumentation().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallEndTerminateEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ENDTERMINATEEVENT);
        assertDiagram(diagram,
                      3);
        assertEquals("endTerminateEvent",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> endNoneEventNode = diagram.getGraph().getNode("_1B379E3E-E4ED-4BD2-AEE8-CD85374CEC78");
        EndTerminateEvent endTerminateEvent = (EndTerminateEvent) endNoneEventNode.getContent().getDefinition();
        assertNotNull(endTerminateEvent.getGeneral());
        assertEquals("MyEndTerminateEvent",
                     endTerminateEvent.getGeneral().getName().getValue());
        assertEquals("MyEndTerminateEventDocumentation",
                     endTerminateEvent.getGeneral().getDocumentation().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallEndSignalEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ENDSIGNALEVENT);
        assertDiagram(diagram,
                      2);
        assertEquals("EndEventAssignments",
                     diagram.getMetadata().getTitle());

        Node<? extends Definition, ?> endSignalEventNode = diagram.getGraph().getNode("_C9151E0C-2E3E-4558-AFC2-34038E3A8552");
        EndSignalEvent endSignalEvent = (EndSignalEvent) endSignalEventNode.getContent().getDefinition();
        DataIOSet dataIOSet = endSignalEvent.getDataIOSet();
        AssignmentsInfo assignmentsinfo = dataIOSet.getAssignmentsinfo();
        assertEquals("EndSignalEventInput1:String||||[din]employee->EndSignalEventInput1",
                     assignmentsinfo.getValue());
        assertEquals("project",
                     endSignalEvent.getExecutionSet().getSignalScope().getValue());
        assertEquals("employee",
                     endSignalEvent.getExecutionSet().getSignalRef().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallEndMessageEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ENDMESSAGEEVENT);
        assertDiagram(diagram,
                      2);
        assertEquals("EndMessageEvent",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> endMessageEventNode = diagram.getGraph().getNode("_4A8A0A9E-D4A5-4B6E-94A6-20817A57B3C6");
        EndMessageEvent endMessageEvent = (EndMessageEvent) endMessageEventNode.getContent().getDefinition();

        assertNotNull(endMessageEvent.getExecutionSet());
        MessageRef messageRef = endMessageEvent.getExecutionSet().getMessageRef();
        assertEquals("msgref",
                     messageRef.getValue());
        DataIOSet dataIOSet = endMessageEvent.getDataIOSet();
        AssignmentsInfo assignmentsInfo = dataIOSet.getAssignmentsinfo();
        assertEquals("EndMessageEventInputVar1:String||||[din]var1->EndMessageEventInputVar1",
                     assignmentsInfo.getValue());
    }

    public void testUnmarshallEndErrorEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ENDERROR_EVENT);
        assertDiagram(diagram,
                      2);
        assertEquals("endErrorEventProcess",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> endEventNode = diagram.getGraph().getNode("_E69BD781-AB7F-45C4-85DA-B1F3BAE5BCCB");
        EndErrorEvent endErrorEvent = (EndErrorEvent) endEventNode.getContent().getDefinition();
        assertNotNull(endErrorEvent.getGeneral());
        assertEquals("MyErrorEventName",
                     endErrorEvent.getGeneral().getName().getValue());
        assertEquals("MyErrorEventDocumentation",
                     endErrorEvent.getGeneral().getDocumentation().getValue());
        assertNotNull(endErrorEvent.getExecutionSet());
        assertNotNull(endErrorEvent.getExecutionSet().getErrorRef());
        assertEquals("MyError",
                     endErrorEvent.getExecutionSet().getErrorRef().getValue());

        DataIOSet dataIOSet = endErrorEvent.getDataIOSet();
        AssignmentsInfo assignmentsInfo = dataIOSet.getAssignmentsinfo();
        assertEquals("myErrorEventInput:String||||[din]var1->myErrorEventInput",
                     assignmentsInfo.getValue());
    }
}
