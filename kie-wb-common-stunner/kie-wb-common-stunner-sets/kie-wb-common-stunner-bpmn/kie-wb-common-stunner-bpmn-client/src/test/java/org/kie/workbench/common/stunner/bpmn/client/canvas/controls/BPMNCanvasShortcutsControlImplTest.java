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

package org.kie.workbench.common.stunner.bpmn.client.canvas.controls;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.AbstractCanvasShortcutsControlImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.BaseCanvasShortcutsControlImplTest;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;

import static org.mockito.Mockito.spy;

@RunWith(GwtMockitoTestRunner.class)
public class BPMNCanvasShortcutsControlImplTest extends BaseCanvasShortcutsControlImplTest {

    @Override
    public AbstractCanvasShortcutsControlImpl getCanvasShortcutsControl() {
        return spy(new BPMNCanvasShortcutsControlImpl(toolboxDomainLookups,
                                                      definitionsCacheRegistry,
                                                      generalCreateNodeAction));
    }

    @Override
    public List<TestedTuple> getTestedTuples() {
        return Arrays.asList(new TestedTuple(BpmnNode.TaskNode.definition,
                                             KeyboardEvent.Key.T,
                                             expectedPositiveReactionOnNode(BpmnNode.TaskNode)),
                             new TestedTuple(BpmnNode.TaskNode.definition,
                                             KeyboardEvent.Key.E,
                                             expectedPositiveReactionOnNode(BpmnNode.EndEventNode)),
                             new TestedTuple(BpmnNode.TaskNode.definition,
                                             KeyboardEvent.Key.G,
                                             expectedPositiveReactionOnNode(BpmnNode.ParallelGatewayNode)),
                             new TestedTuple(BpmnNode.TaskNode.definition,
                                             KeyboardEvent.Key.S,
                                             expectedPositiveReactionOnNode(BpmnNode.SubprocessNode)),
                             new TestedTuple(BpmnNode.TaskNode.definition,
                                             KeyboardEvent.Key.D,
                                             expectedPositiveReactionOnNode(null)),
                             new TestedTuple(BpmnNode.StartEventNode.definition,
                                             KeyboardEvent.Key.T,
                                             expectedPositiveReactionOnNode(BpmnNode.TaskNode)),
                             new TestedTuple(BpmnNode.StartEventNode.definition,
                                             KeyboardEvent.Key.E,
                                             expectedPositiveReactionOnNode(BpmnNode.EndEventNode)),
                             new TestedTuple(BpmnNode.StartEventNode.definition,
                                             KeyboardEvent.Key.G,
                                             expectedPositiveReactionOnNode(BpmnNode.ParallelGatewayNode)),
                             new TestedTuple(BpmnNode.StartEventNode.definition,
                                             KeyboardEvent.Key.S,
                                             expectedPositiveReactionOnNode(BpmnNode.SubprocessNode)),
                             new TestedTuple(BpmnNode.StartEventNode.definition,
                                             KeyboardEvent.Key.D,
                                             expectedPositiveReactionOnNode(null)),
                             new TestedTuple(BpmnNode.SubprocessNode.definition,
                                             KeyboardEvent.Key.T,
                                             expectedPositiveReactionOnNode(BpmnNode.TaskNode)),
                             new TestedTuple(BpmnNode.SubprocessNode.definition,
                                             KeyboardEvent.Key.E,
                                             expectedPositiveReactionOnNode(BpmnNode.EndEventNode)),
                             new TestedTuple(BpmnNode.SubprocessNode.definition,
                                             KeyboardEvent.Key.G,
                                             expectedPositiveReactionOnNode(BpmnNode.ParallelGatewayNode)),
                             new TestedTuple(BpmnNode.SubprocessNode.definition,
                                             KeyboardEvent.Key.S,
                                             expectedPositiveReactionOnNode(BpmnNode.SubprocessNode)),
                             new TestedTuple(BpmnNode.SubprocessNode.definition,
                                             KeyboardEvent.Key.D,
                                             expectedPositiveReactionOnNode(null)),
                             new TestedTuple(BpmnNode.ParallelGatewayNode.definition,
                                             KeyboardEvent.Key.T,
                                             expectedPositiveReactionOnNode(BpmnNode.TaskNode)),
                             new TestedTuple(BpmnNode.ParallelGatewayNode.definition,
                                             KeyboardEvent.Key.E,
                                             expectedPositiveReactionOnNode(BpmnNode.EndEventNode)),
                             new TestedTuple(BpmnNode.ParallelGatewayNode.definition,
                                             KeyboardEvent.Key.G,
                                             expectedPositiveReactionOnNode(BpmnNode.ParallelGatewayNode)),
                             new TestedTuple(BpmnNode.ParallelGatewayNode.definition,
                                             KeyboardEvent.Key.S,
                                             expectedPositiveReactionOnNode(BpmnNode.SubprocessNode)),
                             new TestedTuple(BpmnNode.ParallelGatewayNode.definition,
                                             KeyboardEvent.Key.D,
                                             expectedPositiveReactionOnNode(null)),
                             new TestedTuple(BpmnNode.EndEventNode.definition,
                                             KeyboardEvent.Key.T,
                                             expectedPositiveReactionOnNode(null)),
                             new TestedTuple(BpmnNode.EndEventNode.definition,
                                             KeyboardEvent.Key.E,
                                             expectedPositiveReactionOnNode(null)),
                             new TestedTuple(BpmnNode.EndEventNode.definition,
                                             KeyboardEvent.Key.G,
                                             expectedPositiveReactionOnNode(null)),
                             new TestedTuple(BpmnNode.EndEventNode.definition,
                                             KeyboardEvent.Key.S,
                                             expectedPositiveReactionOnNode(null)),
                             new TestedTuple(BpmnNode.EndEventNode.definition,
                                             KeyboardEvent.Key.D,
                                             expectedPositiveReactionOnNode(null)));
    }

    private Map<Object, Boolean> expectedPositiveReactionOnNode(final BpmnNode node) {
        final Map<Object, Boolean> reactions = new HashMap<>();
        Stream.of(BpmnNode.values()).forEach(bpmnNode -> reactions.put(bpmnNode.definition, false));

        if (node != null) {
            reactions.put(node.definition, true);
        }

        return reactions;
    }

    public enum BpmnNode {
        TaskNode(new NoneTask()),
        ParallelGatewayNode(new ParallelGateway()),
        EndEventNode(new EndNoneEvent()),
        StartEventNode(new StartNoneEvent()),
        SubprocessNode(new EmbeddedSubprocess());

        private Object definition;

        BpmnNode(final Object definition) {
            this.definition = definition;
        }
    }
}
