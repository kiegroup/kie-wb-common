/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.core.client.canvas.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RemoveCanvasChildCommandTest extends AbstractCanvasCommandTest {

    @Mock
    private Shape connectorShape1;

    @Mock
    private Shape connectorShape2;

    @Mock
    private ShapeView connectorShapeView1;

    @Mock
    private ShapeView connectorShapeView2;

    private RemoveCanvasChildCommand tested;
    private TestingGraphMockHandler graphTestHandler;
    private TestingGraphInstanceBuilder.TestGraph1 graph1Instance;

    @Before
    public void setup() throws Exception {
        super.setup();
        this.graphTestHandler = new TestingGraphMockHandler();
        graph1Instance = TestingGraphInstanceBuilder.newGraph1(graphTestHandler);
        when(diagram.getGraph()).thenReturn(graph1Instance.graph);
        when(graphIndex.getGraph()).thenReturn(graph1Instance.graph);
        final String edge1UUID = graph1Instance.edge1.getUUID();
        final String edge2UUID = graph1Instance.edge2.getUUID();
        when(canvas.getShape(edge1UUID)).thenReturn(connectorShape1);
        when(canvas.getShape(edge2UUID)).thenReturn(connectorShape2);
        when(connectorShape1.getShapeView()).thenReturn(connectorShapeView1);
        when(connectorShape2.getShapeView()).thenReturn(connectorShapeView2);
        this.tested = new RemoveCanvasChildCommand(graph1Instance.startNode,
                                                   graph1Instance.intermNode);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() {
        final CommandResult<CanvasViolation> result = tested.execute(canvasHandler);
        assertNotEquals(CommandResult.Type.ERROR,
                        result.getType());
        verify(canvasHandler,
               times(1)).removeChild(eq(graph1Instance.startNode),
                                     eq(graph1Instance.intermNode));
        verify(canvasHandler,
               times(1)).applyElementMutation(eq(graph1Instance.startNode),
                                              any(MutationContext.class));
        verify(canvasHandler,
               times(1)).applyElementMutation(eq(graph1Instance.intermNode),
                                              any(MutationContext.class));
        verify(connectorShapeView1,
               times(1)).moveToTop();
        verify(connectorShapeView1,
               never()).moveToBottom();
        verify(connectorShapeView1,
               never()).moveUp();
        verify(connectorShapeView1,
               never()).moveDown();
        verify(connectorShapeView2,
               times(1)).moveToTop();
        verify(connectorShapeView2,
               never()).moveToBottom();
        verify(connectorShapeView2,
               never()).moveUp();
        verify(connectorShapeView2,
               never()).moveDown();
    }
}
