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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.subprocesses;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.Unmarshalling;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.BPMNDiagramMarshallerBase;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import static org.junit.Assert.assertEquals;

public class MultiInstanceSubProcessTest extends BPMNDiagramMarshallerBase {

    private static final String BPMN_MULTI_INSTANCE_SUBPROCESS =
            "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/multiInstanceSubprocess.bpmn";
    private static final String MULTI_INSTANCE_SUBPROCESS_ID = "_8DBFC130-F97C-4A2E-B4A9-4A95865F44FF";

    @Before
    public void setUp() throws Exception {
        super.init();
    }

    @Test
    public void testOldMarshaller() throws Exception {
        testUnmarshallMultiInstanceSubprocess(oldMarshaller);
    }

    @Test
    @Ignore("BPMNDirectDiagramMarshaller doesn't support multi instance subprocess yet")
    public void testNewMarshaller() throws Exception {
        testUnmarshallMultiInstanceSubprocess(newMarshaller);
    }

    @Test
    @Ignore("BPMNDirectDiagramMarshaller doesn't support multi instance subprocess yet")
    public void testMigration() throws Exception {
        Diagram<Graph, Metadata> oldDiagram = Unmarshalling.unmarshall(oldMarshaller, BPMN_MULTI_INSTANCE_SUBPROCESS);
        Diagram<Graph, Metadata> newDiagram = Unmarshalling.unmarshall(newMarshaller, BPMN_MULTI_INSTANCE_SUBPROCESS);

        assertDiagramEquals(oldDiagram, newDiagram, BPMN_MULTI_INSTANCE_SUBPROCESS);
    }

    private void testUnmarshallMultiInstanceSubprocess(final DiagramMarshaller marshaller) throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_MULTI_INSTANCE_SUBPROCESS);

        Node<? extends Definition, ?> miSubProcessNode = diagram.getGraph().getNode(MULTI_INSTANCE_SUBPROCESS_ID);
        MultipleInstanceSubprocess miSubprocess = (MultipleInstanceSubprocess) miSubProcessNode.getContent().getDefinition();

        assertEquals("myCollection",
                     miSubprocess.getExecutionSet().getMultipleInstanceCollectionInput().getValue());

        assertEquals("myCollectionResult",
                     miSubprocess.getExecutionSet().getMultipleInstanceCollectionOutput().getValue());

        assertEquals("myIterator",
                     miSubprocess.getExecutionSet().getMultipleInstanceDataInput().getValue());

        assertEquals("myResultIterator",
                     miSubprocess.getExecutionSet().getMultipleInstanceDataOutput().getValue());

        assertEquals("myCollection.size == 0",
                     miSubprocess.getExecutionSet().getMultipleInstanceCompletionCondition().getValue());
    }
}
