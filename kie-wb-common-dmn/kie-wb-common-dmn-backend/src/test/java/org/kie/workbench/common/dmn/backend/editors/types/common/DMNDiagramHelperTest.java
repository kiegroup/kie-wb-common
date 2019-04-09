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
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.service.DiagramService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNDiagramHelperTest {

    @Mock
    private DiagramService diagramService;

    @Mock
    private Diagram<Graph, Metadata> diagram;

    @Mock
    private Graph<?, Node> graph;

    @Mock
    private Node node;

    @Mock
    private Definition definition;

    @Mock
    private DMNDiagram dmnDiagram;

    @Mock
    private Definitions definitions;

    @Mock
    private Path path;

    private String namespace = "://namespace";

    private DMNDiagramHelper helper;

    @Before
    public void setup() {
        helper = new DMNDiagramHelper(diagramService);

        when(diagram.getGraph()).thenReturn(graph);
        when(graph.nodes()).thenReturn(singletonList(node));
        when(node.getContent()).thenReturn(definition);
    }

    @Test
    public void testGetNodes() {

        final DRGElement drgElement = mock(DRGElement.class);

        when(definition.getDefinition()).thenReturn(drgElement);

        final List<DRGElement> actualNodes = helper.getNodes(diagram);
        final List<DRGElement> expectedNodes = singletonList(drgElement);

        assertEquals(expectedNodes, actualNodes);
    }

    @Test
    public void testGetNamespaceByDiagram() {

        when(definition.getDefinition()).thenReturn(dmnDiagram);
        when(dmnDiagram.getDefinitions()).thenReturn(definitions);
        when(definitions.getNamespace()).thenReturn(new Text(namespace));

        final String actualNamespace = helper.getNamespace(diagram);

        assertEquals(namespace, actualNamespace);
    }

    @Test
    public void testGetDiagramByPath() {

        when(diagramService.getDiagramByPath(path)).thenReturn(diagram);

        Diagram<Graph, Metadata> actualDiagram = helper.getDiagramByPath(path);

        assertEquals(diagram, actualDiagram);
    }

    @Test
    public void testGetNamespaceByPath() {

        when(diagramService.getDiagramByPath(path)).thenReturn(diagram);
        when(definition.getDefinition()).thenReturn(dmnDiagram);
        when(dmnDiagram.getDefinitions()).thenReturn(definitions);
        when(definitions.getNamespace()).thenReturn(new Text(namespace));

        final String actualNamespace = helper.getNamespace(path);

        assertEquals(namespace, actualNamespace);
    }
}
