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
package org.kie.workbench.common.screens.library.client.screens.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.preferences.SpaceScopedResolutionStrategySupplier;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.archetype.mgmt.shared.preferences.ArchetypePreferences;
import org.kie.workbench.common.screens.library.api.LibraryInfo;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.preferences.LibraryOrganizationalUnitPreferences;
import org.kie.workbench.common.screens.library.api.preferences.LibraryPreferences;
import org.kie.workbench.common.screens.library.api.preferences.LibraryProjectPreferences;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.services.refactoring.model.index.events.IndexingFinishedEvent;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.views.pfly.widgets.ErrorPopup;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.SessionInfoMock;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.NotificationEvent;

import static org.jgroups.util.Util.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.uberfire.mocks.ParametrizedCommandMock.executeParametrizedCommandWith;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AddProjectPopUpPresenterTest {

    @Mock
    private LibraryService libraryService;
    private CallerMock<LibraryService> libraryServiceCaller;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock
    private Event<NotificationEvent> notificationEvent;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private AddProjectPopUpPresenter.View view;

    private SessionInfo sessionInfo;

    @Mock
    private Event<NewProjectEvent> newProjectEvent;

    @Mock
    private LibraryPreferences libraryPreferences;

    @Mock
    private ConflictingRepositoriesPopup conflictingRepositoriesPopup;

    @Mock
    private WorkspaceProjectContext projectContext;

    @Mock
    private ValidationService validationService;
    private CallerMock<ValidationService> validationServiceCaller;

    @Mock
    private ErrorPopup errorPopup;

    @Mock
    private TranslationService translationService;

    @Mock
    private WorkspaceProjectService projectService;
    private CallerMock<WorkspaceProjectService> projectServiceCaller;

    @Mock
    private Logger logger;

    private AddProjectPopUpPresenter presenter;

    private LibraryInfo libraryInfo;
    private OrganizationalUnit selectedOrganizationalUnit;

    @Mock
    private ArchetypePreferences archetypePreferences;

    @Mock
    private SpaceScopedResolutionStrategySupplier spaceScopedResolutionStrategySupplier;

    @Before
    public void setup() {
        libraryServiceCaller = new CallerMock<>(libraryService);
        validationServiceCaller = new CallerMock<>(validationService);
        projectServiceCaller = new CallerMock<>(projectService);
        sessionInfo = new SessionInfoMock();

        selectedOrganizationalUnit = mock(OrganizationalUnit.class);
        doReturn("selectedOrganizationalUnit").when(selectedOrganizationalUnit).getIdentifier();

        when(projectContext.getActiveOrganizationalUnit()).thenReturn(Optional.of(selectedOrganizationalUnit));
        when(projectContext.getActiveWorkspaceProject()).thenReturn(Optional.empty());
        when(projectContext.getActiveModule()).thenReturn(Optional.empty());
        when(projectContext.getActiveRepositoryRoot()).thenReturn(Optional.empty());
        when(projectContext.getActivePackage()).thenReturn(Optional.empty());

        presenter = spy(new AddProjectPopUpPresenter(libraryServiceCaller,
                                                     busyIndicatorView,
                                                     notificationEvent,
                                                     libraryPlaces,
                                                     projectContext,
                                                     view,
                                                     sessionInfo,
                                                     newProjectEvent,
                                                     libraryPreferences,
                                                     conflictingRepositoriesPopup,
                                                     validationServiceCaller,
                                                     errorPopup,
                                                     translationService,
                                                     projectServiceCaller,
                                                     logger,
                                                     archetypePreferences,
                                                     spaceScopedResolutionStrategySupplier));

        doReturn("baseUrl").when(presenter).getBaseURL();

        doReturn("emptyNameMessage").when(view).getEmptyNameMessage();
        doReturn("invalidNameMessage").when(view).getInvalidNameMessage();
        doReturn("duplicatedProjectMessage").when(view).getDuplicatedProjectMessage();

        libraryInfo = new LibraryInfo(new ArrayList<>());
        doReturn(libraryInfo).when(libraryService).getLibraryInfo(selectedOrganizationalUnit);

        doReturn(true).when(validationService).isProjectNameValid(any());
        doReturn(true).when(validationService).validateGroupId(any());
        doReturn(true).when(validationService).validateArtifactId(any());
        doReturn(true).when(validationService).validateGAVVersion(any());

        doReturn(mock(WorkspaceProject.class)).when(libraryService).createProject(any(), any(), any(), Mockito.<String> any());

        presenter.setup();
    }

    @Test
    public void loadTest() {
        assertEquals(libraryInfo,
                     presenter.libraryInfo);
    }

    @Test
    public void cancelTest() {
        presenter.cancel();

        verify(view).hide();
    }

    @Test
    public void newProjectIsCreated() throws Exception {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        when(projectContext.getActiveOrganizationalUnit()).thenReturn(Optional.of(organizationalUnit));

        doReturn("test").when(view).getName();
        doReturn("description").when(view).getDescription();

        presenter.add();

        final ArgumentCaptor<POM> pomArgumentCaptor = ArgumentCaptor.forClass(POM.class);

        verify(view).setAddButtonEnabled(false);
        verify(libraryService).createProject(eq(organizationalUnit),
                                             pomArgumentCaptor.capture(),
                                             eq(DeploymentMode.VALIDATED),
                                             Mockito.<String> any());
        verify(view).setAddButtonEnabled(true);
    }

    @Test
    public void newWorkbenchProjectWithAdvancedSettingsIsCreated() throws Exception {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        when(projectContext.getActiveOrganizationalUnit()).thenReturn(Optional.of(organizationalUnit));

        doReturn("test").when(view).getName();
        doReturn("description").when(view).getDescription();
        doReturn("groupId").when(view).getGroupId();
        doReturn("artifactId").when(view).getArtifactId();
        doReturn("version").when(view).getVersion();
        doReturn(true).when(view).isAdvancedOptionsSelected();

        presenter.add();

        final ArgumentCaptor<POM> pomArgumentCaptor = ArgumentCaptor.forClass(POM.class);

        verify(view).setAddButtonEnabled(false);
        verify(libraryService).createProject(eq(organizationalUnit),
                                             pomArgumentCaptor.capture(),
                                             eq(DeploymentMode.VALIDATED),
                                             Mockito.<String> any());
        verify(view).setAddButtonEnabled(true);

        final POM pom = pomArgumentCaptor.getValue();

        assertEquals("test", pom.getName());
        assertEquals("description", pom.getDescription());
        assertEquals("groupId", pom.getGav().getGroupId());
        assertEquals("artifactId", pom.getGav().getArtifactId());
        assertEquals("version", pom.getGav().getVersion());
        
        //Checking default kie-server options
        assertEquals("kjar", pom.getPackaging());
        assertEquals("org.kie", pom.getBuild().getPlugins().get(0).getGroupId());
        assertEquals("kie-maven-plugin", pom.getBuild().getPlugins().get(0).getArtifactId());
    }
    
    @Test
    public void newWorkbenchProjectWithQuickSettingsIsCreated() throws Exception {
        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        when(projectContext.getActiveOrganizationalUnit()).thenReturn(Optional.of(organizationalUnit));

        doReturn("test").when(view).getName();
        doReturn("description").when(view).getDescription();
        doReturn("groupId").when(view).getGroupId();
        doReturn("artifactId").when(view).getArtifactId();
        doReturn("version").when(view).getVersion();
        doReturn(false).when(view).isAdvancedOptionsSelected();

        presenter.add();

        final ArgumentCaptor<POM> pomArgumentCaptor = ArgumentCaptor.forClass(POM.class);

        verify(view).setAddButtonEnabled(false);
        verify(libraryService).createProject(eq(organizationalUnit),
                                             pomArgumentCaptor.capture(),
                                             eq(DeploymentMode.VALIDATED),
                                             Mockito.<String> any());
        verify(view).setAddButtonEnabled(true);
        
        final POM pom = pomArgumentCaptor.getValue();

        assertEquals("test", pom.getName());
        assertEquals("description", pom.getDescription());
        assertEquals("groupId", pom.getGav().getGroupId());
        assertEquals("artifactId", pom.getGav().getArtifactId());
        assertEquals("version", pom.getGav().getVersion());
        
        //Checking default kie-server options
        assertEquals("kjar", pom.getPackaging());
        assertEquals("org.kie", pom.getBuild().getPlugins().get(0).getGroupId());
        assertEquals("kie-maven-plugin", pom.getBuild().getPlugins().get(0).getArtifactId());
    }

    @Test
    public void createProjectSuccessfullyTest() {
        doReturn("test").when(view).getName();
        doReturn("description").when(view).getDescription();

        presenter.add();

        verify(view).setAddButtonEnabled(false);
        verify(view).showBusyIndicator(Mockito.<String> any());
        verify(view).setAddButtonEnabled(true);
        verify(view).hide();
        verify(notificationEvent).fire(any(NotificationEvent.class));
        verify(libraryPlaces, never()).goToProject(any(WorkspaceProject.class));
    }

    @Test
    public void createProjectFromTemplateTest() {
        doReturn("test").when(view).getName();
        doReturn("description").when(view).getDescription();
        doReturn(true).when(view).isBasedOnTemplate();
        doReturn("template").when(view).getSelectedTemplate();

        presenter.add();

        verify(libraryService).createProject(any(),
                                             any(),
                                             any(),
                                             eq("template"));
    }

    @Test
    public void createProjectWithDuplicatedNameTest() {
        doReturn("test").when(view).getName();
        doReturn("description").when(view).getDescription();

        doThrow(new FileAlreadyExistsException()).when(libraryService).createProject(any(),
                                                                                     any(),
                                                                                     any(),
                                                                                     Mockito.<String> any());
        doAnswer(invocationOnMock -> ((Throwable) invocationOnMock.getArguments()[0]).getCause() instanceof FileAlreadyExistsException)
                .when(presenter).isDuplicatedProjectName(any());

        presenter.add();

        verify(view).setAddButtonEnabled(false);
        verify(view).showBusyIndicator(Mockito.<String> any());
        verify(newProjectEvent,
               never()).fire(any(NewProjectEvent.class));
        verify(view).hideBusyIndicator();
        verify(view,
               never()).hide();
        verify(view).showError(Mockito.<String> any());
        verify(libraryPlaces,
               never()).goToProject(any(WorkspaceProject.class));
        verify(view).setAddButtonEnabled(true);
    }

    @Test
    public void testCreateProjectWithUnexpectedError() {
        doReturn("test").when(view).getName();
        doReturn("description").when(view).getDescription();

        doThrow(new IllegalStateException("New repository should always have a branch."))
                .when(libraryService).createProject(any(), any(), any(), Mockito.<String> any());

        presenter.add();

        verify(view).hide();
        verify(view).setAddButtonEnabled(true);
        verify(view).hideBusyIndicator();

        verify(busyIndicatorView).hideBusyIndicator();

        verify(errorPopup).showError(Mockito.<String> any(), Mockito.<String> any());

        verify(translationService).format(Mockito.<String> any(), Mockito.<String> any());
    }

    @Test
    public void createProjectWithEmptyNameFailedTest() {
        doReturn("").when(view).getName();
        doReturn("description").when(view).getDescription();
        doReturn("groupId").when(view).getGroupId();
        doReturn("artifactId").when(view).getArtifactId();
        doReturn("version").when(view).getVersion();
        doReturn(true).when(view).isAdvancedOptionsSelected();

        presenter.add();

        verify(view).setAddButtonEnabled(false);
        verify(view).showBusyIndicator(Mockito.<String> any());
        verify(newProjectEvent,
               never()).fire(any(NewProjectEvent.class));
        verify(view).hideBusyIndicator();
        verify(view,
               never()).hide();
        verify(view).showError(Mockito.<String> any());
        verify(libraryPlaces,
               never()).goToProject(any(WorkspaceProject.class));
        verify(view).setAddButtonEnabled(true);
    }

    @Test
    public void createProjectWithEmptyGroupIdFailedTest() {
        doReturn("name").when(view).getName();
        doReturn("description").when(view).getDescription();
        doReturn("").when(view).getGroupId();
        doReturn("artifactId").when(view).getArtifactId();
        doReturn("version").when(view).getVersion();
        doReturn(true).when(view).isAdvancedOptionsSelected();

        presenter.add();

        verify(view).setAddButtonEnabled(false);
        verify(view).showBusyIndicator(Mockito.<String> any());
        verify(newProjectEvent,
               never()).fire(any(NewProjectEvent.class));
        verify(view).hideBusyIndicator();
        verify(view,
               never()).hide();
        verify(view).showError(Mockito.<String> any());
        verify(libraryPlaces,
               never()).goToProject(any(WorkspaceProject.class));
        verify(view).setAddButtonEnabled(true);
    }

    @Test
    public void createProjectWithEmptyArtifactIdFailedTest() {
        doReturn("name").when(view).getName();
        doReturn("description").when(view).getDescription();
        doReturn("groupId").when(view).getGroupId();
        doReturn("").when(view).getArtifactId();
        doReturn("version").when(view).getVersion();
        doReturn(true).when(view).isAdvancedOptionsSelected();

        presenter.add();

        verify(view).setAddButtonEnabled(false);
        verify(view).showBusyIndicator(Mockito.<String> any());
        verify(newProjectEvent,
               never()).fire(any(NewProjectEvent.class));
        verify(view).hideBusyIndicator();
        verify(view,
               never()).hide();
        verify(view).showError(Mockito.<String> any());
        verify(libraryPlaces,
               never()).goToProject(any(WorkspaceProject.class));
        verify(view).setAddButtonEnabled(true);
    }

    @Test
    public void createProjectWithEmptyVersionFailedTest() {
        doReturn("name").when(view).getName();
        doReturn("description").when(view).getDescription();
        doReturn("groupId").when(view).getGroupId();
        doReturn("artifactId").when(view).getArtifactId();
        doReturn("").when(view).getVersion();
        doReturn(true).when(view).isAdvancedOptionsSelected();

        presenter.add();

        verify(view).setAddButtonEnabled(false);
        verify(view).showBusyIndicator(Mockito.<String> any());
        verify(newProjectEvent,
               never()).fire(any(NewProjectEvent.class));
        verify(view).hideBusyIndicator();
        verify(view,
               never()).hide();
        verify(view).showError(Mockito.<String> any());
        verify(libraryPlaces,
               never()).goToProject(any(WorkspaceProject.class));
        verify(view).setAddButtonEnabled(true);
    }

    @Test
    public void createProjectWithInvalidNameFailedTest() {
        doReturn("name").when(view).getName();
        doReturn("description").when(view).getDescription();
        doReturn("groupId").when(view).getGroupId();
        doReturn("artifactId").when(view).getArtifactId();
        doReturn("version").when(view).getVersion();
        doReturn(true).when(view).isAdvancedOptionsSelected();

        doReturn(false).when(validationService).isProjectNameValid(any());

        presenter.add();

        verify(view).setAddButtonEnabled(false);
        verify(view).showBusyIndicator(Mockito.<String> any());
        verify(newProjectEvent,
               never()).fire(any(NewProjectEvent.class));
        verify(view).hideBusyIndicator();
        verify(view,
               never()).hide();
        verify(view).showError(Mockito.<String> any());
        verify(libraryPlaces,
               never()).goToProject(any(WorkspaceProject.class));
        verify(view).setAddButtonEnabled(true);
    }

    @Test
    public void createProjectWithInvalidGroupIdFailedTest() {
        doReturn("name").when(view).getName();
        doReturn("description").when(view).getDescription();
        doReturn("groupId").when(view).getGroupId();
        doReturn("artifactId").when(view).getArtifactId();
        doReturn("version").when(view).getVersion();
        doReturn(true).when(view).isAdvancedOptionsSelected();

        doReturn(false).when(validationService).validateGroupId(Mockito.<String> any());

        presenter.add();

        verify(view).setAddButtonEnabled(false);
        verify(view).showBusyIndicator(Mockito.<String> any());
        verify(newProjectEvent,
               never()).fire(any(NewProjectEvent.class));
        verify(view).hideBusyIndicator();
        verify(view,
               never()).hide();
        verify(view).showError(Mockito.<String> any());
        verify(libraryPlaces,
               never()).goToProject(any(WorkspaceProject.class));
        verify(view).setAddButtonEnabled(true);
    }

    @Test
    public void createProjectWithInvalidArtifactIdFailedTest() {
        doReturn("name").when(view).getName();
        doReturn("description").when(view).getDescription();
        doReturn("groupId").when(view).getGroupId();
        doReturn("artifactId").when(view).getArtifactId();
        doReturn("version").when(view).getVersion();
        doReturn(true).when(view).isAdvancedOptionsSelected();

        doReturn(false).when(validationService).validateArtifactId(Mockito.<String> any());

        presenter.add();

        verify(view).setAddButtonEnabled(false);
        verify(view).showBusyIndicator(Mockito.<String> any());
        verify(newProjectEvent,
               never()).fire(any(NewProjectEvent.class));
        verify(view).hideBusyIndicator();
        verify(view,
               never()).hide();
        verify(view).showError(Mockito.<String> any());
        verify(libraryPlaces,
               never()).goToProject(any(WorkspaceProject.class));
        verify(view).setAddButtonEnabled(true);
    }

    @Test
    public void createProjectWithInvalidVersionFailedTest() {
        doReturn("name").when(view).getName();
        doReturn("description").when(view).getDescription();
        doReturn("groupId").when(view).getGroupId();
        doReturn("artifactId").when(view).getArtifactId();
        doReturn("version").when(view).getVersion();
        doReturn(true).when(view).isAdvancedOptionsSelected();

        doReturn(false).when(validationService).validateGAVVersion(Mockito.<String> any());

        presenter.add();

        verify(view).setAddButtonEnabled(false);
        verify(view).showBusyIndicator(Mockito.<String> any());
        verify(newProjectEvent,
               never()).fire(any(NewProjectEvent.class));
        verify(view).hideBusyIndicator();
        verify(view,
               never()).hide();
        verify(view).showError(Mockito.<String> any());
        verify(libraryPlaces,
               never()).goToProject(any(WorkspaceProject.class));
        verify(view).setAddButtonEnabled(true);
    }


    @Test
    public void createProjectWithExternalSuccessCallbackTest() {
        doReturn("test").when(view).getName();
        doReturn("description").when(view).getDescription();
        ParameterizedCommand<WorkspaceProject> command = mock(ParameterizedCommand.class);
        presenter.setSuccessCallback(command);

        presenter.add();

        verify(view).setAddButtonEnabled(false);
        verify(view).showBusyIndicator(Mockito.<String> any());
        verify(view).setAddButtonEnabled(true);
        verify(command, never()).execute(any());
    }

    @Test
    public void createProjectCallbackAfterProjectIndexedEventTest() {
        final WorkspaceProject project = mock(WorkspaceProject.class);
        final Path projectRootPath = mock(Path.class);
        doReturn(projectRootPath).when(project).getRootPath();
        doReturn(project).when(projectService).resolveProject(any(Path.class));
        doReturn(project).when(libraryService).createProject(any(), any(), any(), Mockito.<String> any());
        doReturn("test").when(view).getName();
        doReturn("description").when(view).getDescription();

        presenter.onProjectIndexingFinishedEvent(new IndexingFinishedEvent("kClusterId",
                                                                           projectRootPath));
        presenter.add();

        verify(view).setAddButtonEnabled(false);
        verify(view).showBusyIndicator(Mockito.<String> any());
        verify(view).setAddButtonEnabled(true);
        verify(view).hide();
        verify(notificationEvent).fire(any(NotificationEvent.class));
        verify(libraryPlaces).goToProject(project);
    }

    @Test
    public void createProjectCallbackBeforeProjectIndexedEventTest() {
        final WorkspaceProject project = mock(WorkspaceProject.class);
        final Path projectRootPath = mock(Path.class);
        doReturn(projectRootPath).when(project).getRootPath();
        doReturn(project).when(projectService).resolveProject(any(Path.class));
        doReturn(project).when(libraryService).createProject(any(), any(), any(), Mockito.<String> any());
        doReturn("test").when(view).getName();
        doReturn("description").when(view).getDescription();

        presenter.add();
        presenter.onProjectIndexingFinishedEvent(new IndexingFinishedEvent("kClusterId",
                                                                           projectRootPath));

        verify(view).setAddButtonEnabled(false);
        verify(view).showBusyIndicator(Mockito.<String> any());
        verify(view).setAddButtonEnabled(true);
        verify(view).hide();
        verify(notificationEvent).fire(any(NotificationEvent.class));
        verify(libraryPlaces).goToProject(project);
    }

    @Test
    public void testRestoreAdvancedOptionsWithActiveOrganizationalUnit() {
        LibraryPreferences loadedLibraryPreferences = mock(LibraryPreferences.class);
        LibraryProjectPreferences libraryProjectPreferences = mock(LibraryProjectPreferences.class);

        doReturn(libraryProjectPreferences).when(loadedLibraryPreferences).getProjectPreferences();
        doReturn("version").when(libraryProjectPreferences).getVersion();
        when(projectContext.getActiveOrganizationalUnit().get().getDefaultGroupId()).thenReturn("group");
        when(view.getName()).thenReturn("Project Test");

        executeParametrizedCommandWith(0, loadedLibraryPreferences).when(libraryPreferences).load(any(ParameterizedCommand.class),
                                                                                                  any(ParameterizedCommand.class));

        presenter.restoreAdvancedOptions();

        verify(libraryPreferences).load(any(ParameterizedCommand.class), any(ParameterizedCommand.class));
        verify(view).setVersion("version");
        verify(view).setGroupId("group");
        verify(view, never()).setDescription(any());
        verify(view).setArtifactId("ProjectTest");
    }

    @Test
    public void testRestoreAdvancedOptionsWithoutActiveOrganizationalUnit() {
        LibraryPreferences loadedLibraryPreferences = mock(LibraryPreferences.class);
        LibraryProjectPreferences libraryProjectPreferences = mock(LibraryProjectPreferences.class);
        LibraryOrganizationalUnitPreferences libraryOrganizationalUnitPreferences = mock(LibraryOrganizationalUnitPreferences.class);

        doReturn(libraryProjectPreferences).when(loadedLibraryPreferences).getProjectPreferences();
        doReturn("version").when(libraryProjectPreferences).getVersion();
        doReturn(libraryOrganizationalUnitPreferences).when(loadedLibraryPreferences).getOrganizationalUnitPreferences();
        when(libraryOrganizationalUnitPreferences.getGroupId()).thenReturn("group");
        when(projectContext.getActiveOrganizationalUnit()).thenReturn(Optional.empty());
        when(view.getName()).thenReturn("Project Test");

        executeParametrizedCommandWith(0, loadedLibraryPreferences).when(libraryPreferences).load(any(ParameterizedCommand.class),
                                                                                                  any(ParameterizedCommand.class));

        presenter.restoreAdvancedOptions();

        verify(view).setVersion("version");
        verify(view).setGroupId("group");
        verify(view, never()).setDescription(any());
        verify(view).setArtifactId("ProjectTest");
    }

    @Test
    public void testLoadTemplatesWhenAllAvailable() {
        final ArchetypePreferences preferences = mock(ArchetypePreferences.class);

        final Map<String, Boolean> archetypeSelectionMap = new HashMap<>();
        archetypeSelectionMap.put("template1", true);
        archetypeSelectionMap.put("template2", true);
        archetypeSelectionMap.put("template3", true);

        doReturn(archetypeSelectionMap).when(preferences).getArchetypeSelectionMap();
        doReturn("template2").when(preferences).getDefaultSelection();

        final List<String> templates = Arrays.asList("template1", "template2", "template3");

        presenter.finishLoadTemplates(preferences);

        verify(view).setTemplates(templates, 1);
        verify(view).enableBasedOnTemplatesCheckbox(true);
        verify(view).enableTemplatesSelect(true);
    }

    @Test
    public void testLoadTemplatesWhenSomeAvailable() {
        final ArchetypePreferences preferences = mock(ArchetypePreferences.class);

        final Map<String, Boolean> archetypeSelectionMap = new HashMap<>();
        archetypeSelectionMap.put("template1", true);
        archetypeSelectionMap.put("template2", true);
        archetypeSelectionMap.put("template3", false);

        doReturn(archetypeSelectionMap).when(preferences).getArchetypeSelectionMap();
        doReturn("template2").when(preferences).getDefaultSelection();

        final List<String> templates = Arrays.asList("template1", "template2");

        presenter.finishLoadTemplates(preferences);

        verify(view).setTemplates(templates, 1);
        verify(view).enableBasedOnTemplatesCheckbox(true);
        verify(view).enableTemplatesSelect(true);
    }

    @Test
    public void testLoadTemplatesWhenDefaultNotPresent() {
        final ArchetypePreferences preferences = mock(ArchetypePreferences.class);

        final Map<String, Boolean> archetypeSelectionMap = new HashMap<>();
        archetypeSelectionMap.put("template1", true);
        archetypeSelectionMap.put("template2", true);
        archetypeSelectionMap.put("template3", false);

        doReturn(archetypeSelectionMap).when(preferences).getArchetypeSelectionMap();
        doReturn("template3").when(preferences).getDefaultSelection();

        final List<String> templates = Arrays.asList("template1", "template2");

        presenter.finishLoadTemplates(preferences);

        verify(view).setTemplates(templates, 0);
        verify(view).enableBasedOnTemplatesCheckbox(true);
        verify(view).enableTemplatesSelect(false);
    }

    @Test
    public void testLoadTemplatesWhenEmpty() {
        final ArchetypePreferences preferences = mock(ArchetypePreferences.class);
        doReturn(Collections.emptyMap()).when(preferences).getArchetypeSelectionMap();

        presenter.finishLoadTemplates(preferences);

        verify(view).setTemplates(Collections.singletonList(
                translationService.getTranslation(LibraryConstants.NoTemplatesAvailable)),
                                  0);
        verify(view).enableBasedOnTemplatesCheckbox(false);
        verify(view).enableTemplatesSelect(false);
    }

    @Test
    public void testLoadTemplatesWhenNoOneAvailable() {
        final ArchetypePreferences preferences = mock(ArchetypePreferences.class);
        final Map<String, Boolean> archetypeSelectionMap = new HashMap<>();
        archetypeSelectionMap.put("template1", false);
        archetypeSelectionMap.put("template2", false);
        archetypeSelectionMap.put("template3", false);

        doReturn(archetypeSelectionMap).when(preferences).getArchetypeSelectionMap();

        presenter.finishLoadTemplates(preferences);

        verify(view).setTemplates(Collections.singletonList(
                translationService.getTranslation(LibraryConstants.NoTemplatesAvailable)),
                                  0);
        verify(view).enableBasedOnTemplatesCheckbox(false);
        verify(view).enableTemplatesSelect(false);
    }
}
