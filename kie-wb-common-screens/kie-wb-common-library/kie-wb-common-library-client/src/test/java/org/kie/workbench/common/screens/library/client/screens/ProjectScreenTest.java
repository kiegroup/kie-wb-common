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

import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectScreenTest {

    @Mock
    private ProjectScreen.View view;
    @Mock
    private LibraryPlaces libraryPlaces;
    @Mock
    private LibraryService libraryService;
    @Mock
    private EmptyProjectPresenter emptyProjectPresenter;
    @Mock
    private ProjectListAssetsPresenter projectListAssetsPresenter;
    @Mock
    private ProjectMigrationPresenter projectMigrationPresenter;
    @Mock
    private ProjectContext projectContext;
    @Mock
    private EventSourceMock<ProjectDetailEvent> projectDetailEvent;

    @Captor
    private ArgumentCaptor<ProjectDetailEvent> projectDetailEventArgumentCaptor;

    private ProjectScreen screen;

    @Before
    public void setUp() throws Exception {
        screen = new ProjectScreen(view,
                                   libraryPlaces,
                                   new CallerMock<>(libraryService),
                                   emptyProjectPresenter,
                                   projectListAssetsPresenter,
                                   projectMigrationPresenter,
                                   projectContext,
                                   projectDetailEvent);
    }

    @Test
    public void setUpBranches() throws Exception {

        final ProjectMigrationPresenter.View view = mock(ProjectMigrationPresenter.View.class);
        doReturn(view).when(projectMigrationPresenter).getView();

        final WorkspaceProject project = new WorkspaceProject(mock(OrganizationalUnit.class),
                                                              mock(Repository.class),
                                                              mock(Branch.class),
                                                              null);
        doReturn(project).when(projectContext).getActiveWorkspaceProject();

        screen.onStartup();

        verify(libraryPlaces).setUpBranches();
    }

    @Test
    public void showMigration() throws Exception {

        final ProjectMigrationPresenter.View view = mock(ProjectMigrationPresenter.View.class);
        doReturn(view).when(projectMigrationPresenter).getView();
        final HTMLElement element = mock(HTMLElement.class);
        doReturn(element).when(view).getElement();

        final WorkspaceProject project = new WorkspaceProject(mock(OrganizationalUnit.class),
                                                              mock(Repository.class),
                                                              mock(Branch.class),
                                                              null);
        doReturn(project).when(projectContext).getActiveWorkspaceProject();

        screen.onStartup();

        verify(projectMigrationPresenter).show(project);
        verify(this.view).setContent(element);
    }

    @Test
    public void showEmptyProject() throws Exception {

        final ProjectMigrationPresenter.View view = mock(ProjectMigrationPresenter.View.class);
        doReturn(view).when(emptyProjectPresenter).getView();
        final HTMLElement element = mock(HTMLElement.class);
        doReturn(element).when(view).getElement();

        final WorkspaceProject project = new WorkspaceProject(mock(OrganizationalUnit.class),
                                                              mock(Repository.class),
                                                              mock(Branch.class),
                                                              mock(KieModule.class));
        doReturn(project).when(projectContext).getActiveWorkspaceProject();

        doReturn(false).when(libraryService).hasAssets(project);

        screen.onStartup();

        verify(projectDetailEvent).fire(projectDetailEventArgumentCaptor.capture());
        assertEquals(project, projectDetailEventArgumentCaptor.getValue().getProject());

        verify(emptyProjectPresenter).show(project);
        verify(this.view).setContent(element);
    }

    @Test
    public void showList() throws Exception {

        final ProjectMigrationPresenter.View view = mock(ProjectMigrationPresenter.View.class);
        doReturn(view).when(projectListAssetsPresenter).getView();
        final HTMLElement element = mock(HTMLElement.class);
        doReturn(element).when(view).getElement();

        final WorkspaceProject project = new WorkspaceProject(mock(OrganizationalUnit.class),
                                                              mock(Repository.class),
                                                              mock(Branch.class),
                                                              mock(KieModule.class));

        doReturn(project).when(projectContext).getActiveWorkspaceProject();

        doReturn(true).when(libraryService).hasAssets(project);

        screen.onStartup();

        verify(projectDetailEvent).fire(projectDetailEventArgumentCaptor.capture());
        assertEquals(project, projectDetailEventArgumentCaptor.getValue().getProject());

        verify(projectListAssetsPresenter).show(project);
        verify(this.view).setContent(element);
    }

    @Test
    public void goToSettingsTest() {
        projectScreen.goToSettings();

        verify(assetDetailEvent).fire(new AssetDetailEvent(projectInfo,
                                                           null));
    }

    @Test
    public void getProjectNameTest() {
        assertEquals("projectName",
                     projectScreen.getProjectName());
    }

    @Test
    public void selectCommandTest() {
        final Path assetPath = mock(Path.class);

        projectScreen.selectCommand(assetPath).execute();

        verify(libraryPlaces).goToAsset(projectInfo,
                                        assetPath);
    }

    @Test
    public void detailsCommandTest() {
        final Path assetPath = mock(Path.class);

        projectScreen.detailsCommand(assetPath).execute();

        verify(libraryPlaces).goToAsset(projectInfo,
                                        assetPath);
    }
}