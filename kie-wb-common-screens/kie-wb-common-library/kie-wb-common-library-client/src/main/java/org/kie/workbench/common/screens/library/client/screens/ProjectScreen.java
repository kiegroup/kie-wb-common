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

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.kie.workbench.common.screens.library.client.perspective.LibraryPerspective;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.lifecycle.OnStartup;

@WorkbenchScreen(identifier = LibraryPlaces.PROJECT_SCREEN,
        owningPerspective = LibraryPerspective.class)
public class ProjectScreen {

    private WorkspaceProject project;

    public interface View
            extends IsElement {

        void setContent(HTMLElement element);
    }

    private LibraryPlaces libraryPlaces;
    private Caller<LibraryService> libraryService;

    private EmptyProjectPresenter emptyProjectPresenter;
    private ProjectListAssetsPresenter projectListAssetsPresenter;
    private ProjectMigrationPresenter projectMigrationPresenter;
    private ProjectContext projectContext;

    private Event<ProjectDetailEvent> projectDetailEvent;

    private View view;

    public ProjectScreen() {
    }

    @Inject
    public ProjectScreen(final View view,
                         final LibraryPlaces libraryPlaces,
                         final Caller<LibraryService> libraryService,
                         final EmptyProjectPresenter emptyProjectPresenter,
                         final ProjectListAssetsPresenter projectListAssetsPresenter,
                         final ProjectMigrationPresenter projectMigrationPresenter,
                         final ProjectContext projectContext,
                         final Event<ProjectDetailEvent> projectDetailEvent) {
        this.view = view;
        this.libraryPlaces = libraryPlaces;
        this.libraryService = libraryService;
        this.emptyProjectPresenter = emptyProjectPresenter;
        this.projectListAssetsPresenter = projectListAssetsPresenter;
        this.projectMigrationPresenter = projectMigrationPresenter;
        this.projectContext = projectContext;
        this.projectDetailEvent = projectDetailEvent;
    }

    @OnStartup
    public void onStartup() {

        setup();
    }

    public void refreshOnFocus(@Observes final PlaceGainFocusEvent placeGainFocusEvent) {

        if (project != null && placeGainFocusEvent.getPlace().getIdentifier().equals(LibraryPlaces.PROJECT_SCREEN)) {

            setup();
        }
    }

    private void setup() {
        project = projectContext.getActiveWorkspaceProject();
        libraryPlaces.setUpBranches();

        if (projectContext.getActiveWorkspaceProject().getMainModule() == null) {
            showMigration();
        } else {

            projectDetailEvent.fire(new ProjectDetailEvent(project));

            libraryService.call(hasAssets -> {
                if ((Boolean) hasAssets) {
                    showList();
                } else {
                    showEmptyProject();
                }
            }).hasAssets(project);
        }
    }

    private void showEmptyProject() {
        emptyProjectPresenter.show(project);
        view.setContent(emptyProjectPresenter.getView().getElement());
    }

    private void showList() {
        projectListAssetsPresenter.show(project);
        view.setContent(projectListAssetsPresenter.getView().getElement());
    }

    private void showMigration() {
        projectMigrationPresenter.show(project);
        view.setContent(projectMigrationPresenter.getView().getElement());
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Project Screen";
    }

    @WorkbenchPartView
    public IsElement asWidget() {
        return view;
    }
}
