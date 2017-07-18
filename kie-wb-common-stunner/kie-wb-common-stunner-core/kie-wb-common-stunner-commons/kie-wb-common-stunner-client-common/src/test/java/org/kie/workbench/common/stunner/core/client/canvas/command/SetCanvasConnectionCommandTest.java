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
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.EdgeShape;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SetCanvasConnectionCommandTest extends AbstractCanvasCommandTest {

    private static final String EDGE_ID = "e1";
    private static final String SOURCE_ID = "s1";
    private static final String TARGET_ID = "t1";

    @Mock
    private Edge edge;
    @Mock
    private Node source;
    @Mock
    private Node target;
    @Mock
    private EdgeShape edgeShape;
    @Mock
    private Shape sourceShape;
    @Mock
    private ShapeView sourceShapeView;
    @Mock
    private Shape targetShape;
    @Mock
    private ShapeView targetShapeView;

    private SetCanvasConnectionCommand tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        super.setup();
        when(edge.getUUID()).thenReturn(EDGE_ID);
        when(source.getUUID()).thenReturn(SOURCE_ID);
        when(target.getUUID()).thenReturn(TARGET_ID);
        when(edge.getSourceNode()).thenReturn(source);
        when(edge.getTargetNode()).thenReturn(target);
        when(sourceShape.getShapeView()).thenReturn(sourceShapeView);
        when(targetShape.getShapeView()).thenReturn(targetShapeView);
        when(canvas.getShape(eq(EDGE_ID))).thenReturn(edgeShape);
        when(canvas.getShape(eq(SOURCE_ID))).thenReturn(sourceShape);
        when(canvas.getShape(eq(TARGET_ID))).thenReturn(targetShape);
        when(sourceShape.getShapeView()).thenReturn(sourceShapeView);
        this.tested = new SetCanvasConnectionCommand(edge);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() {
        final CommandResult<CanvasViolation> result = tested.execute(canvasHandler);
        assertNotEquals(CommandResult.Type.ERROR,
                        result.getType());
        verify(edgeShape,
               times(1)).applyConnections(eq(edge),
                                          eq(sourceShapeView),
                                          eq(targetShapeView),
                                          any(MutationContext.class));
        verify(canvasHandler,
               times(1)).notifyCanvasElementUpdated(eq(source));
        verify(canvasHandler,
               times(1)).notifyCanvasElementUpdated(eq(target));
    }
}
