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

package org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.GeneralCreateNodeAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxDomainLookups;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.lookup.domain.CommonDomainLookups;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractCanvasShortcutsControlImplTest {

    @Mock
    private ToolboxDomainLookups toolboxDomainLookups;

    @Mock
    private DefinitionsCacheRegistry definitionsCacheRegistry;

    @Mock
    private GeneralCreateNodeAction generalCreateNodeAction;

    @Mock
    private AbstractCanvasHandler canvasHandlerMock;

    private AbstractCanvasShortcutsControlImpl canvasShortcutsControl;

    @Before
    public void setUp() throws Exception {
        canvasShortcutsControl = new AbstractCanvasShortcutsControlImpl(toolboxDomainLookups,
                                                                        definitionsCacheRegistry,
                                                                        generalCreateNodeAction) {
            {
                this.canvasHandler = canvasHandlerMock;
            }

            @Override
            public void onKeyDownEvent(KeyboardEvent.Key... keys) {
                // Tested via BaseCanvasShortcutsControlImplTest
            }
        };
    }

    @Test
    public void testBind() {
        assertThat(canvasShortcutsControl.editorSession).isNull();

        final EditorSession session = mock(EditorSession.class);
        final KeyboardControl keyboardControl = mock(KeyboardControl.class);
        doReturn(keyboardControl).when(session).getKeyboardControl();

        canvasShortcutsControl.bind(session);

        assertThat(canvasShortcutsControl.editorSession).isEqualTo(session);
        verify(keyboardControl).addKeyShortcutCallback(any(KeyboardControl.KeyShortcutCallback.class));
    }

    @Test
    public void testAppendNode() {
        final String sourceNodeId = "source-node";

        final Index index = mock(Index.class);
        doReturn(index).when(canvasHandlerMock).getGraphIndex();
        final Element selectedElement = mock(Element.class);
        doReturn(selectedElement).when(index).get(sourceNodeId);
        final Node selectedNode = mock(Node.class);
        doReturn(selectedNode).when(selectedElement).asNode();

        final Diagram diagram = mock(Diagram.class);
        final Metadata metadata = mock(Metadata.class);
        final String definitionSetId = "def-set-id";
        doReturn(diagram).when(canvasHandlerMock).getDiagram();
        doReturn(metadata).when(diagram).getMetadata();
        doReturn(definitionSetId).when(metadata).getDefinitionSetId();

        final CommonDomainLookups commonDomainLookups = mock(CommonDomainLookups.class);
        doReturn(commonDomainLookups).when(toolboxDomainLookups).get(definitionSetId);

        final String targetConnectorDefinitionId = "connector-def-id";
        final Set<String> targetConnectorIds = Collections.singleton(targetConnectorDefinitionId);
        doReturn(targetConnectorIds).when(commonDomainLookups).lookupTargetConnectors(selectedNode);

        final String targetNodeDefinitionId = "target-node-id";
        final Set<String> targetNodeDefinitionIds = Collections.singleton(targetNodeDefinitionId);
        doReturn(targetNodeDefinitionIds).when(commonDomainLookups).lookupTargetNodes(any(Graph.class),
                                                                                      eq(selectedNode),
                                                                                      eq(targetConnectorDefinitionId));

        final Object targetNodeDefinition = mock(Object.class);
        doReturn(targetNodeDefinition).when(definitionsCacheRegistry).getDefinitionById(targetNodeDefinitionId);

        // Positive check, desired target definition
        final Function<Object, Boolean> positiveDefinitionCheck = (def) -> def == targetNodeDefinition;
        canvasShortcutsControl.appendNode(sourceNodeId, positiveDefinitionCheck);
        verify(generalCreateNodeAction).executeAction(eq(canvasHandlerMock),
                                                      eq(sourceNodeId),
                                                      eq(targetNodeDefinitionId),
                                                      eq(targetConnectorDefinitionId));

        reset(generalCreateNodeAction);

        // Negative check, not desired target definition
        final Function<Object, Boolean> negativeDefinitionCheck = (def) -> def != targetNodeDefinition;
        canvasShortcutsControl.appendNode(sourceNodeId, negativeDefinitionCheck);
        verify(generalCreateNodeAction, never()).executeAction(eq(canvasHandlerMock),
                                                               eq(sourceNodeId),
                                                               eq(targetNodeDefinitionId),
                                                               eq(targetConnectorDefinitionId));
    }

    @Test
    public void testSelectedNodeNothingSelected() {
        mockSelectedElements();

        assertThat(canvasShortcutsControl.selectedNodeId()).isNull();
        assertThat(canvasShortcutsControl.selectedNodeElement()).isNull();
    }

    @Test
    public void testSelectedNodeTooManySelected() {
        mockSelectedElements("node-a", "node-b");

        assertThat(canvasShortcutsControl.selectedNodeId()).isNull();
        assertThat(canvasShortcutsControl.selectedNodeElement()).isNull();
    }

    @Test
    public void testSelectedNodeExactlyOneSelected() {
        final String selectedNodeId = "node-id";
        mockSelectedElements(selectedNodeId);

        final Index index = mock(Index.class);
        doReturn(index).when(canvasHandlerMock).getGraphIndex();
        final Element selectedElement = mock(Element.class);
        doReturn(selectedElement).when(index).get(selectedNodeId);

        assertThat(canvasShortcutsControl.selectedNodeId()).isEqualTo(selectedNodeId);
        assertThat(canvasShortcutsControl.selectedNodeElement()).isEqualTo(selectedElement);
    }

    private void mockSelectedElements(final String... selectedIds) {

        final EditorSession session = mock(EditorSession.class);
        final SelectionControl selectionControl = mock(SelectionControl.class);
        final KeyboardControl keyboardControl = mock(KeyboardControl.class);
        doReturn(selectionControl).when(session).getSelectionControl();
        doReturn(keyboardControl).when(session).getKeyboardControl();
        doReturn(Arrays.asList(selectedIds)).when(selectionControl).getSelectedItems();

        canvasShortcutsControl.bind(session);
    }
}
