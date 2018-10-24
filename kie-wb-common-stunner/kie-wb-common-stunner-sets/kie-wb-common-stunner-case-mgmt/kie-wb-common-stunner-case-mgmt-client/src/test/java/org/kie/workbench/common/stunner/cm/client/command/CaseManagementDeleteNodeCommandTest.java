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

package org.kie.workbench.common.stunner.cm.client.command;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.cm.client.command.canvas.CaseManagementDeleteCanvasNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.DeleteCanvasNodeCommand;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.impl.SafeDeleteNodeCommand;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementDeleteNodeCommandTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testCaseManagementCanvasDeleteProcessor() {
        final Node node = mock(Node.class);

        final CaseManagementDeleteNodeCommand.CaseManagementCanvasDeleteProcessor processor =
                new CaseManagementDeleteNodeCommand.CaseManagementCanvasDeleteProcessor(
                        SafeDeleteNodeCommand.Options.defaults());

        final DeleteCanvasNodeCommand command = processor.createDeleteCanvasNodeCommand(node);

        assertNotNull(command);
        assertTrue(command instanceof CaseManagementDeleteCanvasNodeCommand);
    }
}