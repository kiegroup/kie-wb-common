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
import java.util.List;

import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.defaulteditor.client.editor.NewFileUploader;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.util.ResourceUtils;
import org.kie.workbench.common.screens.projecteditor.client.handlers.NewPackageHandler;
import org.kie.workbench.common.screens.projecteditor.client.handlers.NewProjectHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EmptyProjectListAssetsPresenterTest {

    @Mock
    private EmptyProjectPresenter.View view;

    @Mock
    private ResourceUtils resourceUtils;

    @Mock
    private NewResourcePresenter newResourcePresenter;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private ProjectsDetailScreen projectsDetailScreen;

    private EmptyProjectPresenter emptyProjectPresenter;

    @Before
    public void setup() {
        emptyProjectPresenter = spy(new EmptyProjectPresenter(view,
                                                        resourceUtils,
                                                        newResourcePresenter,
                                                        placeManager,
                                                        libraryPlaces,
                                                        projectsDetailScreen));
    }

    @Test
    public void onStartupTest() {
        NewResourceHandler projectHandler = mock(NewProjectHandler.class);
        doReturn(true).when(projectHandler).canCreate();
        NewResourceHandler packageHandler = mock(NewPackageHandler.class);
        doReturn(true).when(packageHandler).canCreate();
        NewResourceHandler uploadHandler = mock(NewFileUploader.class);
        doReturn(true).when(uploadHandler).canCreate();
        NewResourceHandler type1Handler = mock(NewResourceHandler.class);
        doReturn(true).when(type1Handler).canCreate();
        NewResourceHandler type2Handler = mock(NewResourceHandler.class);
        doReturn(true).when(type2Handler).canCreate();

        List<NewResourceHandler> handlers = new ArrayList<>();
        handlers.add(projectHandler);
        handlers.add(packageHandler);
        handlers.add(uploadHandler);
        handlers.add(type1Handler);
        handlers.add(type2Handler);
        doReturn(handlers).when(resourceUtils).getOrderedNewResourceHandlers();

        final WorkspaceProject project = mock(WorkspaceProject.class);
        doReturn("projectName").when(project).getName();
        final ProjectDetailEvent projectDetailEvent = mock(ProjectDetailEvent.class);
        doReturn(project).when(projectDetailEvent).getProject();

        emptyProjectPresenter.show(project);

        assertEquals(uploadHandler,
                     emptyProjectPresenter.getUploadHandler());

        verify(view,
               times(2)).addResourceHandler(any(NewResourceHandler.class));
        verify(view).addResourceHandler(type1Handler);
        verify(view).addResourceHandler(type2Handler);

        verify(view).setProjectName("projectName");
        verify(placeManager).closePlace(LibraryPlaces.LIBRARY_SCREEN);
    }

    @Test
    public void refreshOnFocusProjectWithoutAssetsTest() {
        doReturn(false).when(libraryService).hasAssets(any());

        final PlaceRequest place = new DefaultPlaceRequest(LibraryPlaces.EMPTY_PROJECT_SCREEN);
        final PlaceGainFocusEvent placeGainFocusEvent = new PlaceGainFocusEvent(place);

        emptyProjectScreen.projectInfo = mock(ProjectInfo.class);
        emptyProjectScreen.refreshOnFocus(placeGainFocusEvent);

        verify(libraryPlaces,
               never()).goToProject(any(ProjectInfo.class));
    }

    @Test
    public void refreshOnFocusProjectWithAssetsTest() {
        doReturn(true).when(libraryService).hasAssets(any());

        final PlaceRequest place = new DefaultPlaceRequest(LibraryPlaces.EMPTY_PROJECT_SCREEN);
        final PlaceGainFocusEvent placeGainFocusEvent = new PlaceGainFocusEvent(place);

        emptyProjectScreen.projectInfo = mock(ProjectInfo.class);
        emptyProjectScreen.refreshOnFocus(placeGainFocusEvent);

        verify(libraryPlaces).goToProject(any(ProjectInfo.class));
    }

    @Test
    public void dontRefreshOnFocusOnAnotherScreenTest() {
        final PlaceRequest place = new DefaultPlaceRequest("anotherScreen");
        final PlaceGainFocusEvent placeGainFocusEvent = new PlaceGainFocusEvent(place);

        emptyProjectScreen.refreshOnFocus(placeGainFocusEvent);

        verify(libraryPlaces,
               never()).goToProject(any(ProjectInfo.class));
    }

    @Test
    public void goToSettingsTest() {
        emptyProjectPresenter.goToSettings();

        verify(libraryPlaces).goToSettings();
    }
}
