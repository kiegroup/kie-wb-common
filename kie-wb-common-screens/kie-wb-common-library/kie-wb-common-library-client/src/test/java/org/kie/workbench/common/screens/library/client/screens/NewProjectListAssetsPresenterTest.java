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

import java.util.ArrayList;
import javax.enterprise.event.Event;

import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.events.NewModuleEvent;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.LibraryInfo;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.preferences.LibraryPreferences;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.mocks.CallerMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.jgroups.util.Util.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NewProjectListAssetsPresenterTest {

    @Mock
    private LibraryService libraryService;
    private CallerMock<LibraryService> libraryServiceCaller;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock
    private Event<NotificationEvent> notificationEvent;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private NewProjectScreen.View view;

    @Mock
    private Event<NewModuleEvent> newModuleEvent;

    @Mock
    private LibraryPreferences libraryPreferences;

    @Mock
    private ProjectContext projectContext;

    @Mock
    private ConflictingRepositoriesPopup conflictingRepositoriesPopup;

    @Mock
    private ValidationService validationService;

    private NewProjectScreen newProjectScreen;

    private LibraryInfo libraryInfo;

    @Before
    public void setup() {
        libraryServiceCaller = new CallerMock<>(libraryService);

        final OrganizationalUnit selectedOrganizationalUnit = mock(OrganizationalUnit.class);
        doReturn("selectedOrganizationalUnit").when(selectedOrganizationalUnit).getIdentifier();
        doReturn(selectedOrganizationalUnit).when(projectContext).getActiveOrganizationalUnit();

        newProjectScreen = spy(new NewProjectScreen(libraryServiceCaller,
                                                    placeManager,
                                                    projectContext,
                                                    busyIndicatorView,
                                                    notificationEvent,
                                                    libraryPlaces,
                                                    view,
                                                    libraryPreferences,
                                                    conflictingRepositoriesPopup,
                                                    new CallerMock<ValidationService>(validationService)));

        doReturn("emptyNameMessage").when(view).getEmptyNameMessage();
        doReturn("invalidNameMessage").when(view).getInvalidNameMessage();
        doReturn("duplicatedProjectMessage").when(view).getDuplicatedProjectMessage();

        libraryInfo = new LibraryInfo(new ArrayList<>());
        doReturn(libraryInfo).when(libraryService).getLibraryInfo(selectedOrganizationalUnit);

        doReturn(true).when(validationService).isProjectNameValid(any());

        newProjectScreen.load();
    }

    @Test
    public void loadTest() {
        assertEquals(libraryInfo,
                     newProjectScreen.libraryInfo);
    }

    @Test
    public void cancelTest() {
        newProjectScreen.cancel();

        verify(libraryPlaces).goToLibrary();
    }

    @Test
    public void createProjectSuccessfullyTest() {
        newProjectScreen.createProject("moduleName",
                                       "description",
                                       DeploymentMode.VALIDATED);

        verify(busyIndicatorView).showBusyIndicator(anyString());
        verify(busyIndicatorView).hideBusyIndicator();
        verify(notificationEvent).fire(any(NotificationEvent.class));
        verify(libraryPlaces).goToProject(any(WorkspaceProject.class));
    }

    @Test
    public void createProjectWithEmptyNameFailedTest() {
        newProjectScreen.createProject("",
                                       "description",
                                       DeploymentMode.VALIDATED);

        verify(busyIndicatorView).showBusyIndicator(anyString());

        verify(busyIndicatorView).hideBusyIndicator();
        verify(notificationEvent).fire(new NotificationEvent(view.getEmptyNameMessage(),
                                                             NotificationEvent.NotificationType.ERROR));
        verify(libraryPlaces,
               never()).goToProject(any(WorkspaceProject.class));
        verify(placeManager,
               never()).closePlace(LibraryPlaces.NEW_PROJECT_SCREEN);
    }

    @Test
    public void createProjectWithDuplicatedNameTest() {
        doThrow(new FileAlreadyExistsException()).when(libraryService).createProject(anyString(),
                                                                                     any(OrganizationalUnit.class),
                                                                                     anyString(),
                                                                                     eq(DeploymentMode.VALIDATED));
        doAnswer(invocationOnMock -> ((Throwable) invocationOnMock.getArguments()[0]).getCause() instanceof FileAlreadyExistsException)
                .when(newProjectScreen).isDuplicatedProjectName(any());

        newProjectScreen.createProject("projectName",
                                       "description",
                                       DeploymentMode.VALIDATED);

        verify(busyIndicatorView).showBusyIndicator(anyString());
        verify(busyIndicatorView).hideBusyIndicator();
        verify(notificationEvent).fire(new NotificationEvent(view.getDuplicatedProjectMessage(),
                                                             NotificationEvent.NotificationType.ERROR));
        verify(libraryPlaces,
               never()).goToProject(any(WorkspaceProject.class));
        verify(placeManager,
               never()).closePlace(LibraryPlaces.NEW_PROJECT_SCREEN);
    }

    @Test
    public void createProjectWithInvalidNameFailedTest() {
        doReturn(false).when(validationService).isProjectNameValid(any());

        newProjectScreen.createProject("name!",
                                       "description",
                                       DeploymentMode.VALIDATED);

        verify(busyIndicatorView).showBusyIndicator(anyString());
        verify(busyIndicatorView).hideBusyIndicator();
        verify(notificationEvent).fire(new NotificationEvent(view.getInvalidNameMessage(),
                                                             NotificationEvent.NotificationType.ERROR));
        verify(libraryPlaces,
               never()).goToProject(any(WorkspaceProject.class));
        verify(placeManager,
               never()).closePlace(LibraryPlaces.NEW_PROJECT_SCREEN);
    }

    @Test
    public void createProjectWithInvalidArtifactIdFailedTest() {
        newProjectScreen.createProject("!",
                                       "description",
                                       DeploymentMode.VALIDATED);

        verify(busyIndicatorView).showBusyIndicator(anyString());
        verify(busyIndicatorView).hideBusyIndicator();
        verify(notificationEvent).fire(new NotificationEvent(view.getInvalidNameMessage(),
                                                             NotificationEvent.NotificationType.ERROR));
        verify(libraryPlaces,
               never()).goToProject(any(WorkspaceProject.class));
        verify(placeManager,
               never()).closePlace(LibraryPlaces.NEW_PROJECT_SCREEN);
    }
}