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

package org.kie.workbench.common.dmn.client.canvas.controls.keyboard;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.GeneralCreateNodeAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxDomainLookups;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.mockito.Mock;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class DMNCanvasShortcutsControlImplTest {

    private DMNCanvasShortcutsControlImpl canvasShortcutsControl;

    @Mock
    private ToolboxDomainLookups toolboxDomainLookups;

    @Mock
    private DefinitionsCacheRegistry definitionsCacheRegistry;

    @Mock
    private GeneralCreateNodeAction generalCreateNodeAction;

    @Mock
    private Element selectedNodeElement;

    private final String selectedNodeId = "selected-node-id";

    @Before
    public void setUp() {
        canvasShortcutsControl = spy(new DMNCanvasShortcutsControlImpl(toolboxDomainLookups,
                                                                       definitionsCacheRegistry,
                                                                       generalCreateNodeAction));

        doReturn(selectedNodeId).when(canvasShortcutsControl).selectedNodeId();
        doReturn(selectedNodeElement).when(canvasShortcutsControl).selectedNodeElement();
    }

    @Test
    public void testDecisionIsAppendedIfDecisionSelected() {
        final Definition definition = mock(Definition.class);
        doReturn(definition).when(selectedNodeElement).getContent();
        doReturn(mock(Decision.class)).when(definition).getDefinition();
        doNothing().when(canvasShortcutsControl).appendNode(selectedNodeId, Decision.class);

        canvasShortcutsControl.onKeyDownEvent(KeyboardEvent.Key.D);

        verify(canvasShortcutsControl).appendNode(selectedNodeId, Decision.class);
    }

    @Test
    public void testDecisionIsAppendedIfInputDataIsSelected() {
        final Definition definition = mock(Definition.class);
        doReturn(definition).when(selectedNodeElement).getContent();
        doReturn(mock(InputData.class)).when(definition).getDefinition();
        doNothing().when(canvasShortcutsControl).appendNode(selectedNodeId, Decision.class);

        canvasShortcutsControl.onKeyDownEvent(KeyboardEvent.Key.D);

        verify(canvasShortcutsControl).appendNode(selectedNodeId, Decision.class);
    }
}
