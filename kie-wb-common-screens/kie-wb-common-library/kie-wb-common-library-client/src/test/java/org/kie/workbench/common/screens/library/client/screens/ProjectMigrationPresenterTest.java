/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.screens.library.client.screens;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectMigrationPresenterTest {

    @Mock
    private ProjectMigrationPresenter.View view;
    @Mock
    private EventSourceMock<NotificationEvent> notificationEvent;
    @Mock
    private LibraryPlaces libraryPlaces;
    @Mock
    private LibraryService libraryService;
    @Mock
    private TranslationService ts;
    @Mock
    private WorkspaceProject project;

    private ProjectMigrationPresenter screen;

    @Before
    public void setUp() throws Exception {

        screen = new ProjectMigrationPresenter(view,
                                               notificationEvent,
                                               libraryPlaces,
                                               new CallerMock<>(libraryService),
                                               ts);

        screen.show(project);
    }

    @Test
    public void migrateIsCalled() throws Exception {
        screen.onMigrate();

        verify(libraryService).migrate(project);
    }

    @Test
    public void goToLibrary() throws Exception {
        screen.onMigrate();

        verify(libraryPlaces).goToLibrary();
    }

    @Test
    public void viewSet() throws Exception {
        assertEquals(view, screen.getView());
    }

    @Test
    public void eventIsFired() throws Exception {

        doReturn("Migration successful").when(ts).format("MigrationWasSuccessful");

        screen.onMigrate();

        ArgumentCaptor<NotificationEvent> argumentCaptor = ArgumentCaptor.forClass(NotificationEvent.class);

        verify(notificationEvent).fire(argumentCaptor.capture());

        assertEquals("Migration successful", argumentCaptor.getValue().getNotification());
    }
}