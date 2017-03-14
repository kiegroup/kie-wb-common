/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.command.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CompositeCommandTest {

    @Mock
    GraphCommandExecutionContext commandExecutionContext;
    private CompositeCommandImpl tested;

    @Before
    public void setup() throws Exception {
        this.tested = spy(new CompositeCommandImpl<>(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInitialize() {
        assertFalse(tested.isInitialized());
        tested.ensureInitialized(commandExecutionContext);
        assertTrue(tested.isInitialized());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAllow() {
        CommandResult<RuleViolation> result = tested.allow(commandExecutionContext);
        assertNotNull(result);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());

        //fails here
        verify(tested).addCommand(any(Command.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() {
        CommandResult<RuleViolation> result = tested.execute(commandExecutionContext);
        assertNotNull(result);
        assertEquals(CommandResult.Type.INFO,
                     result.getType());

        //fails here
        verify(tested).addCommand(any(Command.class));
    }
}
