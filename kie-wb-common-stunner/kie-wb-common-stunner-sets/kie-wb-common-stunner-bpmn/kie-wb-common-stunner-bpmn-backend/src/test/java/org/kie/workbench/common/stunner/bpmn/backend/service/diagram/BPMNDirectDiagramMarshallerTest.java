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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.backend.ApplicationFactoryManager;
import org.kie.workbench.common.stunner.backend.definition.factory.TestScopeModelFactory;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.backend.BPMNDirectDiagramMarshaller;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
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
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;
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
        when(adapterManager.forDefinitionSet()).thenReturn(definitionSetAdapter);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(adapterManager.forPropertySet()).thenReturn(propertySetAdapter);
        when(adapterManager.forProperty()).thenReturn(propertyAdapter);
        when(adapterRegistry.getDefinitionSetAdapter(any(Class.class))).thenReturn(definitionSetAdapter);
        when(adapterRegistry.getDefinitionAdapter(any(Class.class))).thenReturn(definitionAdapter);
        when(adapterRegistry.getPropertySetAdapter(any(Class.class))).thenReturn(propertySetAdapter);
        when(adapterRegistry.getPropertyAdapter(any(Class.class))).thenReturn(propertyAdapter);
        applicationFactoryManager = new MockApplicationFactoryManager(
                new GraphFactoryImpl(definitionManager),
                testScopeModelFactory,
                new EdgeFactoryImpl(definitionManager),
                new NodeFactoryImpl(definitionUtils)
        );

        // The tested BPMN marshaller.
        tested = new BPMNDirectDiagramMarshaller(applicationFactoryManager);
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

}
