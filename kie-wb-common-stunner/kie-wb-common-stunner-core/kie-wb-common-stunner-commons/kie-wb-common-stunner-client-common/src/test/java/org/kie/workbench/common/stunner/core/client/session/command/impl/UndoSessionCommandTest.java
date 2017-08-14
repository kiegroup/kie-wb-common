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

package org.kie.workbench.common.stunner.core.client.session.command.impl;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.registry.command.CommandRegistry;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UndoSessionCommandTest {

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private CommandRegistry commandRegistry;

    @Mock
    private KeyboardControl<AbstractCanvas, ClientSession> keyboardControl;

    @Mock
    private ClientFullSession session;

    @Captor
    private ArgumentCaptor<KeyboardControl.KeyShortcutCallback> keyShortcutCallbackCaptor;

    private UndoSessionCommand command;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.command = spy(new UndoSessionCommand(sessionCommandManager));
        when(session.getKeyboardControl()).thenReturn(keyboardControl);
        when(sessionCommandManager.getRegistry()).thenReturn(commandRegistry);
        when(commandRegistry.getCommandHistory()).thenReturn(Collections.emptyList());
    }

    @Test
    public void checkBindAttachesKeyHandler() {
        command.bind(session);

        verify(keyboardControl,
               times(1)).addKeyShortcutCallback(any(KeyboardControl.KeyShortcutCallback.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkRespondsToCtrlZKey() {
        command.bind(session);

        verify(keyboardControl,
               times(1)).addKeyShortcutCallback(keyShortcutCallbackCaptor.capture());

        final KeyboardControl.KeyShortcutCallback keyShortcutCallback = keyShortcutCallbackCaptor.getValue();
        keyShortcutCallback.onKeyShortcut(KeyboardEvent.Key.CONTROL,
                                          KeyboardEvent.Key.Z);

        verify(command,
               times(1)).execute(any(ClientSessionCommand.Callback.class));
    }
}
