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

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.GeneralCreateNodeAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxDomainLookups;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public abstract class BaseCanvasShortcutsControlImplTest {

    private AbstractCanvasShortcutsControlImpl canvasShortcutsControl;

    @Mock
    protected ToolboxDomainLookups toolboxDomainLookups;

    @Mock
    protected DefinitionsCacheRegistry definitionsCacheRegistry;

    @Mock
    protected GeneralCreateNodeAction generalCreateNodeAction;

    @Mock
    private Element selectedNodeElement;

    private final String selectedNodeId = "selected-node-id";

    public abstract AbstractCanvasShortcutsControlImpl getCanvasShortcutsControl();

    public abstract List<TestedTuple> getTestedTuples();

    @Test
    public void testAppending() {
        final SoftAssertions softly = new SoftAssertions();
        getTestedTuples().forEach(testedTuple -> {
            canvasShortcutsControl = getCanvasShortcutsControl();

            doReturn(selectedNodeId).when(canvasShortcutsControl).selectedNodeId();
            doReturn(selectedNodeElement).when(canvasShortcutsControl).selectedNodeElement();

            // Mock the source node
            final Definition definition = mock(Definition.class);
            doReturn(definition).when(selectedNodeElement).getContent();
            doReturn(testedTuple.sourceNodeDefinition).when(definition).getDefinition();
            doNothing().when(canvasShortcutsControl).appendNode(eq(selectedNodeId), any(Function.class));

            // simulate pressing key
            canvasShortcutsControl.onKeyDownEvent(testedTuple.pressedKey);

            final long positiveReactionsCount =
                    testedTuple.reactions.values().stream().filter(value -> value == true).count();

            final ArgumentCaptor<Function> checkDefinitionCaptor = ArgumentCaptor.forClass(Function.class);

            // if some positive reaction expected, capture the definition check function
            verify(canvasShortcutsControl, times(Math.toIntExact(positiveReactionsCount))).appendNode(eq(selectedNodeId),
                                                                                                      checkDefinitionCaptor.capture());

            if (positiveReactionsCount > 0) {
                testedTuple.reactions.keySet().stream()
                        .forEach((targetDefinition) ->
                                         softly.assertThat(checkDefinitionCaptor.getValue().apply(targetDefinition))
                                                 .as("Unexpected reaction on key: " + testedTuple.pressedKey + " " +
                                                             "Source definition: " + testedTuple.sourceNodeDefinition.getClass() + " " +
                                                             "Target defintiion: " + targetDefinition.getClass())
                                                 .isEqualTo(testedTuple.reactions.get(targetDefinition)));
            }

            reset(canvasShortcutsControl);
        });

        softly.assertAll();
    }

    public class TestedTuple {

        Object sourceNodeDefinition;

        KeyboardEvent.Key pressedKey;

        Map<Object, Boolean> reactions;

        public TestedTuple(Object sourceNodeDefinition, KeyboardEvent.Key pressedKey, Map<Object, Boolean> reactions) {
            this.sourceNodeDefinition = sourceNodeDefinition;
            this.pressedKey = pressedKey;
            this.reactions = reactions;
        }
    }
}
