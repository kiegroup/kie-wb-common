/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.jgroups.util.Util.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataTypeCacheServerTest {

    @Mock
    private DataTypeCacheServer dataTypeCacheServer;

    @Mock
    BpmnNode rootNode;

    @Mock
    BpmnNode node1;

    @Mock
    BpmnNode node2;

    @Test
    public void testCacheServerEmptyDiagrams() {
        Mockito.doCallRealMethod().when(dataTypeCacheServer).cacheDataTypes(anyObject());
        // Empty List
        List<BpmnNode> nodes = new ArrayList<>();
        when(rootNode.getChildren()).thenReturn(nodes);
        dataTypeCacheServer.cacheDataTypes(rootNode);
        assertEquals(nodes, rootNode.getChildren());
        assertEquals(0, nodes.size());
    }

    @Test
    public void testCacheServerWithNodes() {
        Mockito.doCallRealMethod().when(dataTypeCacheServer).cacheDataTypes(anyObject());
        List<BpmnNode> nodes = new ArrayList<>();
        when(rootNode.getChildren()).thenReturn(nodes);
        dataTypeCacheServer.cacheDataTypes(rootNode);
        // Test with Nodes
        nodes.add(node1);
        nodes.add(node2);

        Node valueNode = mock(Node.class);
        Node valueNode2 = mock(Node.class);

        when(node1.value()).thenReturn(valueNode);
        when(node2.value()).thenReturn(valueNode2);

        when(rootNode.getChildren()).thenReturn(nodes);
        dataTypeCacheServer.cacheDataTypes(rootNode);
        assertEquals(nodes, rootNode.getChildren());
    }
}