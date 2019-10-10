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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.docks;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.docks.preview.PreviewDiagramScreen;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocks;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class KogitoPreviewDiagramDockTest {

    private static final String PERSPECTIVE_ID = "perspectiveId";

    @Mock
    private UberfireDocks uberfireDocks;

    @Mock
    private TranslationService translationService;

    @Captor
    private ArgumentCaptor<UberfireDock> dockArgumentCaptor;

    private KogitoPreviewDiagramDock dock;

    @Before
    public void setup() {
        this.dock = spy(new KogitoPreviewDiagramDock(uberfireDocks,
                                                     translationService));

        dock.init(PERSPECTIVE_ID);
    }

    @Test
    public void testInit() {
        verify(uberfireDocks).add(dockArgumentCaptor.capture());
        verify(uberfireDocks).show(eq(position()), eq(PERSPECTIVE_ID));

        assertEquals(screen(), dockArgumentCaptor.getValue().getIdentifier());
    }

    @Test
    public void testOpenWhenClosed() {
        dock.open();

        verify(uberfireDocks).open(dockArgumentCaptor.capture());

        assertEquals(screen(), dockArgumentCaptor.getValue().getIdentifier());
    }

    @Test
    public void testOpenWhenAlreadyOpen() {
        dock.open();

        reset(uberfireDocks);

        dock.open();

        verify(uberfireDocks, never()).open(any(UberfireDock.class));
    }

    @Test
    public void testCloseWhenOpen() {
        dock.open();
        dock.close();

        verify(uberfireDocks).close(dockArgumentCaptor.capture());

        assertEquals(screen(), dockArgumentCaptor.getValue().getIdentifier());
    }

    @Test
    public void testCloseWhenAlreadyClosed() {
        dock.close();

        verify(uberfireDocks, never()).close(any(UberfireDock.class));
    }

    protected UberfireDockPosition position() {
        return UberfireDockPosition.EAST;
    }

    protected String screen() {
        return PreviewDiagramScreen.SCREEN_ID;
    }
}
