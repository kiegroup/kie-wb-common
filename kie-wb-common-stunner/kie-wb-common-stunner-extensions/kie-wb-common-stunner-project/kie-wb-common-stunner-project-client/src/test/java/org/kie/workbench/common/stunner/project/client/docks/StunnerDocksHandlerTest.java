/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.project.client.docks;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionPausedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionResumedEvent;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class StunnerDocksHandlerTest {

    private StunnerDocksHandler handler;

    @Mock
    private Command command;

    @Mock
    private ClientSession clientSession;

    @Before
    public void init() {
        handler = new StunnerDocksHandler();

        handler.init(command);

        assertEquals(2,
                     handler.provideDocks("").size());
    }

    @Test
    public void testOnSessionOpenedEvent() {
        handler.onCanvasSessionOpened(new SessionOpenedEvent(clientSession));

        assertTrue(handler.shouldRefreshDocks());

        assertFalse(handler.shouldDisableDocks());

        verify(command).execute();
    }

    @Test
    public void testOnCanvasSessionDestroyed() {
        handler.onCanvasSessionDestroyed(new SessionDestroyedEvent(clientSession));

        assertTrue(handler.shouldRefreshDocks());

        assertTrue(handler.shouldDisableDocks());

        verify(command).execute();
    }

    @Test
    public void testOnCanvasSessionPaused() {
        handler.onCanvasSessionPaused(new SessionPausedEvent(clientSession));

        assertTrue(handler.shouldRefreshDocks());

        assertTrue(handler.shouldDisableDocks());
    }

    @Test
    public void testOnCanvasSessionResumed() {
        handler.onCanvasSessionResumed(new SessionResumedEvent(clientSession));

        assertTrue(handler.shouldRefreshDocks());

        assertFalse(handler.shouldDisableDocks());

        verify(command).execute();
    }
}
