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

package org.kie.workbench.common.stunner.core.graph.command.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionCommandHelperTest {

    private static final String SOURCE_UUID = "SOURCE_UUID";

    private static final String TARGET_UUID = "TARGET_UUID";

    private static final String EXPECTED_MESSAGE = "A node can not be connected to itself ";

    @Mock
    private Node<? extends View<?>, Edge> sourceNode;

    @Mock
    private Node<? extends View<?>, Edge> targetNode;

    @Before
    public void setUp() {
        when(sourceNode.getUUID()).thenReturn(SOURCE_UUID);
        when(targetNode.getUUID()).thenReturn(TARGET_UUID);
    }

    @Test
    public void testWhenNodesAreNotEqual() {
        verifyCheckSucceeded(ConnectionCommandHelper.checkSelfReferenceViolations(sourceNode,
                                                                                  targetNode));
    }

    @Test
    public void testWhenNodesAreEqual() {
        when(targetNode.getUUID()).thenReturn(SOURCE_UUID);
        verifyCheckFailed(ConnectionCommandHelper.checkSelfReferenceViolations(sourceNode,
                                                                               targetNode));
    }

    @Test
    public void testWhenTargetIsNull() {
        verifyCheckSucceeded(ConnectionCommandHelper.checkSelfReferenceViolations(sourceNode,
                                                                                  null));
    }

    @Test
    public void testWhenSourceIsNull() {
        verifyCheckSucceeded(ConnectionCommandHelper.checkSelfReferenceViolations(null,
                                                                                  targetNode));
    }

    @Test
    public void testWhenBothAreNull() {
        verifyCheckFailed(ConnectionCommandHelper.checkSelfReferenceViolations(null,
                                                                               null));
    }

    private void verifyCheckFailed(CommandResult<RuleViolation> commandResult) {
        assertTrue(CommandUtils.isError(commandResult));
        assertTrue(commandResult.getViolations().iterator().hasNext());
        assertEquals(EXPECTED_MESSAGE,
                     commandResult.getViolations().iterator().next().getArguments().get()[0]);
    }

    private void verifyCheckSucceeded(CommandResult commandResult) {
        assertFalse(CommandUtils.isError(commandResult));
    }
}
