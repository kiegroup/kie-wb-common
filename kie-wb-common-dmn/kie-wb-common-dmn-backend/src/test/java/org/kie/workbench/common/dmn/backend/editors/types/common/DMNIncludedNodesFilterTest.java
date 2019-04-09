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

package org.kie.workbench.common.dmn.backend.editors.types.common;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.editors.types.DMNIncludedNode;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNIncludedNodesFilterTest {

    @Mock
    private DMNDiagramHelper diagramHelper;

    @Mock
    private DMNIncludedNodeFactory factory;

    @Mock
    private Diagram<Graph, Metadata> diagram;

    private DMNIncludedNodesFilter filter;

    @Before
    public void setup() {
        filter = new DMNIncludedNodesFilter(diagramHelper, factory);
    }

    @Test
    public void testGetNodesByNamespaces() {

        final Path path = mock(Path.class);
        final List<String> namespaces = asList("://namespace1", "://namespace2", "://namespace3");
        final DMNIncludedNode dmnNode1 = mock(DMNIncludedNode.class);
        final DMNIncludedNode dmnNode2 = mock(DMNIncludedNode.class);
        final Decision node1 = new Decision();
        final InputData node2 = new InputData();
        final List<DRGElement> diagramNodes = asList(node1, node2);

        when(diagramHelper.getDiagramByPath(path)).thenReturn(diagram);
        when(diagramHelper.getNodes(diagram)).thenReturn(diagramNodes);
        when(diagramHelper.getNamespace(diagram)).thenReturn("://namespace1");
        when(factory.makeDMNIncludeModel(path, node1)).thenReturn(dmnNode1);
        when(factory.makeDMNIncludeModel(path, node2)).thenReturn(dmnNode2);

        final List<DMNIncludedNode> actualNodes = filter.getNodesByNamespaces(path, namespaces);
        final List<DMNIncludedNode> expectedNodes = asList(dmnNode1, dmnNode2);

        assertEquals(expectedNodes, actualNodes);
    }

    @Test
    public void testGetNodesByNamespacesWhenPathDoesNotRepresentsAnImportedDiagram() {

        final Path path = mock(Path.class);
        final List<String> namespaces = asList("://namespace1", "://namespace2", "://namespace3");

        when(diagramHelper.getDiagramByPath(path)).thenReturn(diagram);
        when(diagramHelper.getNamespace(diagram)).thenReturn("://namespace4");

        final List<DMNIncludedNode> actualNodes = filter.getNodesByNamespaces(path, namespaces);
        final List<DMNIncludedNode> expectedNodes = emptyList();

        assertEquals(expectedNodes, actualNodes);
    }
}
