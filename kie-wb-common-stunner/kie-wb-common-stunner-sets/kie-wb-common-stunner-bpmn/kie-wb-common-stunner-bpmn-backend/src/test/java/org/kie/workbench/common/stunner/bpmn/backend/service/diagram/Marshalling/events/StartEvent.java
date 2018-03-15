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

import java.util.Arrays;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.Marshalling.BPMNDiagramMarshallerBase;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.Marshalling.Marshaller;
import org.kie.workbench.common.stunner.bpmn.definition.BaseStartEvent;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.workbench.common.stunner.bpmn.backend.service.diagram.Marshalling.Marshaller.NEW;
import static org.kie.workbench.common.stunner.bpmn.backend.service.diagram.Marshalling.Marshaller.OLD;

@RunWith(Parameterized.class)
public abstract class StartEvent extends BPMNDiagramMarshallerBase {

    protected static final String EMPTY_VALUE = "";
    protected static final boolean NON_INTERRUPTING = false;
    protected static final boolean INTERRUPTING = true;

    protected DiagramMarshaller<Graph, Metadata, Diagram<Graph, Metadata>> marshaller = null;

    @Parameterized.Parameters
    public static List<Object[]> marshallers() {
        return Arrays.asList(new Object[][] {
                {OLD}, {NEW}
        });
    }

    public StartEvent(Marshaller marshallerType) {
        super.init();
        switch(marshallerType) {
            case OLD: marshaller = oldMarshaller; break;
            case NEW: marshaller = newMarshaller; break;
        }
    }

    abstract void testUnmarshallTopLevelEventFilledProperties() throws Exception;

    abstract void testUnmarshallTopLevelEmptyEventProperties() throws Exception;

    abstract void testUnmarshallSubprocessLevelEventFilledProperties() throws Exception;

    abstract void testUnmarshallSubprocessLevelEventEmptyProperties() throws Exception;

    abstract void testMarshallTopLevelEventFilledProperties() throws Exception;

    abstract void testMarshallTopLevelEmptyEventProperties() throws Exception;

    abstract void testMarshallSubprocessLevelEventFilledProperties() throws Exception;

    abstract void testMarshallSubprocessLevelEventEmptyProperties() throws Exception;

    protected <T extends BaseStartEvent> void assertNodesEqualsAfterMarshalling(Diagram<Graph, Metadata> before, Diagram<Graph, Metadata> after, String nodeId, Class<T> startType) {
        T nodeBeforeMarshalling = getStartNodeById(before, nodeId, startType);
        T nodeAfterMarshalling = getStartNodeById(after, nodeId, startType);
        assertEquals(nodeBeforeMarshalling, nodeAfterMarshalling);
    }

    @SuppressWarnings("unchecked")
    protected  <T extends BaseStartEvent> T getStartNodeById(Diagram<Graph, Metadata> diagram, String id, Class<T> type) {
        Node<? extends Definition, ?> node = diagram.getGraph().getNode(id);
        assertNotNull(node);
        assertEquals(1, node.getOutEdges().size());
        return type.cast(node.getContent().getDefinition());
    }

    protected void assertGeneralSet(BPMNGeneralSet generalSet, String nodeName, String documentation) {
        assertNotNull(generalSet);
        assertNotNull(generalSet.getName());
        assertNotNull(generalSet.getDocumentation());
        assertEquals(nodeName, generalSet.getName().getValue());
        assertEquals(documentation, generalSet.getDocumentation().getValue());
    }


    protected void assertDataIOSet(DataIOSet dataIOSet, String value) {
        assertNotNull(dataIOSet);
        AssignmentsInfo assignmentsInfo = dataIOSet.getAssignmentsinfo();
        assertNotNull(assignmentsInfo);
        assertEquals(value, assignmentsInfo.getValue());
    }

}
