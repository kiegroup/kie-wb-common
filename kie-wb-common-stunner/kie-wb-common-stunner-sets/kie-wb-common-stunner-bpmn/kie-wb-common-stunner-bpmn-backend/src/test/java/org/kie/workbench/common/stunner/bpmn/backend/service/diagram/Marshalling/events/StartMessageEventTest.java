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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.Marshalling.events;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.Marshalling.Marshaller;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.InterruptingMessageEventExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StartMessageEventTest extends StartEvent {

    private static final String BPMN_START_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/messageStartEvents.bpmn";

    private static final String FILLED_TOP_LEVEL_EVENT_ID = "2B967C25-C1FE-4945-8511-7A9E5465BA22";
    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "D78124CD-19B0-45C6-AF0A-CD7C16F4F3BD";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_ID = "464FB8BC-F752-4428-A3DC-D5DDCEE2353F";
    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "372D12E1-30F9-4504-8ED5-5F7D1735FEDB";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 11;

    public StartMessageEventTest(Marshaller marshallerType) {
        super(marshallerType);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String eventName = "Message message name ~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String eventDocumentation = "Message documentation\n~`!@#$%^&*()_+=-{}|\\][:\";'?><,./\n";
        final String eventRef = "Message1";
        final String eventDataOutput = "||messageReceived:String||[dout]messageReceived->helloProcess";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartMessageEvent filledTop = getStartNodeById(diagram, FILLED_TOP_LEVEL_EVENT_ID, StartMessageEvent.class);
        assertGeneralSet(filledTop.getGeneral(), eventName, eventDocumentation);
        assertMessageEventExecutionSet(filledTop.getExecutionSet(), eventRef, INTERRUPTING);
        assertDataIOSet(filledTop.getDataIOSet(), eventDataOutput);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartMessageEvent emptyTop = getStartNodeById(diagram, EMPTY_TOP_LEVEL_EVENT_ID, StartMessageEvent.class);
        assertGeneralSet(emptyTop.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertMessageEventExecutionSet(emptyTop.getExecutionSet(), EMPTY_VALUE, NON_INTERRUPTING);
        assertDataIOSet(emptyTop.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String eventName = "Message name ~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String eventDocumentation = "Doc is here\n~`!@#$%^&*()_+=-{}|\\][:\";'?><,./\n";
        final String eventRef = "Message2";
        final String eventDataOutput = "||messageR:String||[dout]messageR->helloProcess";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartMessageEvent filledSubprocess = getStartNodeById(diagram, FILLED_SUBPROCESS_LEVEL_EVENT_ID, StartMessageEvent.class);
        assertGeneralSet(filledSubprocess.getGeneral(), eventName, eventDocumentation);
        assertMessageEventExecutionSet(filledSubprocess.getExecutionSet(), eventRef, INTERRUPTING);
        assertDataIOSet(filledSubprocess.getDataIOSet(), eventDataOutput);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartMessageEvent emptySubprocess = getStartNodeById(diagram, EMPTY_SUBPROCESS_LEVEL_EVENT_ID, StartMessageEvent.class);
        assertGeneralSet(emptySubprocess.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertMessageEventExecutionSet(emptySubprocess.getExecutionSet(), EMPTY_VALUE, NON_INTERRUPTING);
        assertDataIOSet(emptySubprocess.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testMarshallTopLevelEventFilledProperties() throws Exception {
        checkEventMarshalling(StartMessageEvent.class, FILLED_TOP_LEVEL_EVENT_ID);
    }

    @Test
    @Override
    public void testMarshallTopLevelEmptyEventProperties() throws Exception {
        checkEventMarshalling(StartMessageEvent.class, EMPTY_TOP_LEVEL_EVENT_ID);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelEventFilledProperties() throws Exception {
        checkEventMarshalling(StartMessageEvent.class, FILLED_SUBPROCESS_LEVEL_EVENT_ID);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelEventEmptyProperties() throws Exception {
        checkEventMarshalling(StartMessageEvent.class, EMPTY_SUBPROCESS_LEVEL_EVENT_ID);
    }

    @Override
    String getBpmnStartEventFilePath() {
        return BPMN_START_EVENT_FILE_PATH;
    }

    private void assertMessageEventExecutionSet(InterruptingMessageEventExecutionSet executionSet, String eventName, boolean isInterrupting) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getMessageRef());
        assertEquals(eventName, executionSet.getMessageRef().getValue());
        assertEquals(isInterrupting, executionSet.getIsInterrupting().getValue());
    }
}
