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
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.InterruptingErrorEventExecutionSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StartErrorEventTest extends StartEvent {

    private static final String BPMN_START_EVENT_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/startErrorEvents.bpmn";

    private static final String FILLED_TOP_LEVEL_EVENT_ID = "470CB3B0-B2E6-4252-B41E-353AED109847";
    private static final String EMPTY_TOP_LEVEL_EVENT_ID = "A180871D-2E3A-4CD3-AED7-43E8397FF30C";
    private static final String FILLED_SUBPROCESS_LEVEL_EVENT_ID = "25676AF3-FD4D-4A07-BA58-4D0E331D0579";
    private static final String EMPTY_SUBPROCESS_LEVEL_EVENT_ID = "1BB182E3-B7B9-45DB-8579-66A2F1B4DC53";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 11;

    public StartErrorEventTest(Marshaller marshallerType) {
        super(marshallerType);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEventFilledProperties() throws Exception {
        final String eventName = "Filled Top-Level Error start event";
        final String eventDocumentation = "Some documentation\n~`!@#$%^&*()_+=-{}|\\][:\";'?><,./\n";
        final String errorRef = "Error1";
        final String eventDataOutput = "||someVar:String||[dout]someVar->prVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartErrorEvent filledTop = getStartNodeById(diagram, FILLED_TOP_LEVEL_EVENT_ID, StartErrorEvent.class);
        assertGeneralSet(filledTop.getGeneral(), eventName, eventDocumentation);
        assertErrorEventExecutionSet(filledTop.getExecutionSet(), errorRef, INTERRUPTING);
        assertDataIOSet(filledTop.getDataIOSet(), eventDataOutput);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartErrorEvent emptyTop = getStartNodeById(diagram, EMPTY_TOP_LEVEL_EVENT_ID, StartErrorEvent.class);
        assertGeneralSet(emptyTop.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertErrorEventExecutionSet(emptyTop.getExecutionSet(), EMPTY_VALUE, NON_INTERRUPTING);
        assertDataIOSet(emptyTop.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception {
        final String eventName = "Event subprocess filled error event";
        final String eventDocumentation = "Some documentation as well\n~`!@#$%^&*()_+=-{}|\\][:\";'?><,./";
        final String eventRef = "Error2";
        final String eventDataOutput = "||newVar:String||[dout]newVar->prVar";

        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartErrorEvent filledSubprocess = getStartNodeById(diagram, FILLED_SUBPROCESS_LEVEL_EVENT_ID, StartErrorEvent.class);
        assertGeneralSet(filledSubprocess.getGeneral(), eventName, eventDocumentation);
        assertErrorEventExecutionSet(filledSubprocess.getExecutionSet(), eventRef, INTERRUPTING);
        assertDataIOSet(filledSubprocess.getDataIOSet(), eventDataOutput);
    }

    @Test
    @Override
    public void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        StartErrorEvent emptySubprocess = getStartNodeById(diagram, EMPTY_SUBPROCESS_LEVEL_EVENT_ID, StartErrorEvent.class);
        assertGeneralSet(emptySubprocess.getGeneral(), EMPTY_VALUE, EMPTY_VALUE);
        assertErrorEventExecutionSet(emptySubprocess.getExecutionSet(), EMPTY_VALUE, NON_INTERRUPTING);
        assertDataIOSet(emptySubprocess.getDataIOSet(), EMPTY_VALUE);
    }

    @Test
    @Override
    public void testMarshallTopLevelEventFilledProperties() throws Exception {
        Diagram<Graph, Metadata> initialDiagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        String resultXml = marshaller.marshall(initialDiagram);

        Diagram<Graph, Metadata> marshalledDiagram = unmarshall(marshaller, getStream(resultXml));
        assertDiagram(marshalledDiagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        assertNodesEqualsAfterMarshalling(initialDiagram, marshalledDiagram, FILLED_TOP_LEVEL_EVENT_ID, StartErrorEvent.class);
    }

    @Test
    @Override
    public void testMarshallTopLevelEmptyEventProperties() throws Exception {
        Diagram<Graph, Metadata> initialDiagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        String resultXml = marshaller.marshall(initialDiagram);

        Diagram<Graph, Metadata> marshalledDiagram = unmarshall(marshaller, getStream(resultXml));
        assertDiagram(marshalledDiagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        assertNodesEqualsAfterMarshalling(initialDiagram, marshalledDiagram, EMPTY_TOP_LEVEL_EVENT_ID, StartErrorEvent.class);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelEventFilledProperties() throws Exception {
        Diagram<Graph, Metadata> initialDiagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        String resultXml = marshaller.marshall(initialDiagram);

        Diagram<Graph, Metadata> marshalledDiagram = unmarshall(marshaller, getStream(resultXml));
        assertDiagram(marshalledDiagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        assertNodesEqualsAfterMarshalling(initialDiagram, marshalledDiagram, FILLED_SUBPROCESS_LEVEL_EVENT_ID, StartErrorEvent.class);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelEventEmptyProperties() throws Exception {
        Diagram<Graph, Metadata> initialDiagram = unmarshall(marshaller, BPMN_START_EVENT_FILE_PATH);
        String resultXml = marshaller.marshall(initialDiagram);

        Diagram<Graph, Metadata> marshalledDiagram = unmarshall(marshaller, getStream(resultXml));
        assertDiagram(marshalledDiagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        assertNodesEqualsAfterMarshalling(initialDiagram, marshalledDiagram, EMPTY_SUBPROCESS_LEVEL_EVENT_ID, StartErrorEvent.class);
    }

    private void assertErrorEventExecutionSet(InterruptingErrorEventExecutionSet executionSet, String eventName, boolean isInterrupting) {
        assertNotNull(executionSet);
        assertNotNull(executionSet.getErrorRef());
        assertEquals(eventName, executionSet.getErrorRef().getValue());
        assertEquals(isInterrupting, executionSet.getIsInterrupting().getValue());
    }
}
