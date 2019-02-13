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

package org.kie.workbench.common.stunner.core.client.session.command.impl;

import org.junit.Test;
import org.kie.workbench.common.stunner.core.client.components.layout.UndoableLayoutExecutor;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.processing.layout.LayoutService;
import org.mockito.Mock;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class PerformAutomaticLayoutCommandTest {

    @Mock
    private LayoutService service;

    @Mock
    private ClientSessionCommand.Callback callback;

    @Mock
    private Diagram diagram;

    @Test
    public void testGetExecutor() {
        final PerformAutomaticLayoutCommand command = new PerformAutomaticLayoutCommand(service);
        final UndoableLayoutExecutor executor = command.getExecutor();

        assertTrue(executor instanceof UndoableLayoutExecutor);
    }

    @Test
    public void testExecute() {
        initMocks(this);
        doReturn(null).when(diagram).getGraph();
        final PerformAutomaticLayoutCommand command = mock(PerformAutomaticLayoutCommand.class);
        doCallRealMethod().when(command).execute(callback);
        doReturn(diagram).when(command).getDiagram();
        command.execute(callback);

        verify(command).getExecutor();
        verify(callback).onSuccess();
    }
}