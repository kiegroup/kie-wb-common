/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.library.api.LibraryInfo;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.preferences.LibraryPreferences;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.projecteditor.util.NewProjectUtils;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.widgets.client.callbacks.CommandWithThrowableDrivenErrorCallback;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

@WorkbenchScreen(identifier = LibraryPlaces.NEW_PROJECT_SCREEN)
public class NewProjectScreen {

    public interface View extends UberElement<NewProjectScreen> {

        void setProjectDescription(String defaultProjectDescription);

        String getCreatingProjectMessage();

        String getProjectCreatedSuccessfullyMessage();

        String getEmptyNameMessage();

        String getInvalidNameMessage();

        String getDuplicatedProjectMessage();
    }

    LibraryInfo libraryInfo;
    private Caller<LibraryService> libraryService;
    private PlaceManager placeManager;
    private ProjectContext projectContext;
    private BusyIndicatorView busyIndicatorView;
    private Event<NotificationEvent> notificationEvent;
    private LibraryPlaces libraryPlaces;
    private View view;

    private LibraryPreferences libraryPreferences;

    private ConflictingRepositoriesPopup conflictingRepositoriesPopup;

    private Caller<ValidationService> validationService;

    @Inject
    public NewProjectScreen(final Caller<LibraryService> libraryService,
                            final PlaceManager placeManager,
                            final ProjectContext projectContext,
                            final BusyIndicatorView busyIndicatorView,
                            final Event<NotificationEvent> notificationEvent,
                            final LibraryPlaces libraryPlaces,
                            final View view,
                            final LibraryPreferences libraryPreferences,
                            final ConflictingRepositoriesPopup conflictingRepositoriesPopup,
                            final Caller<ValidationService> validationService) {
        this.libraryService = libraryService;
        this.placeManager = placeManager;
        this.projectContext = projectContext;
        this.busyIndicatorView = busyIndicatorView;
        this.notificationEvent = notificationEvent;
        this.libraryPlaces = libraryPlaces;
        this.view = view;
        this.libraryPreferences = libraryPreferences;
        this.conflictingRepositoriesPopup = conflictingRepositoriesPopup;
        this.validationService = validationService;
    }

    @OnStartup
    public void load() {
        libraryService.call(new RemoteCallback<LibraryInfo>() {
            @Override
            public void callback(LibraryInfo libraryInfo) {
                NewProjectScreen.this.libraryInfo = libraryInfo;
            }
        }).getLibraryInfo(projectContext.getActiveOrganizationalUnit());

        libraryPreferences.load(loadedLibraryPreferences -> {
                                    view.init(NewProjectScreen.this);
                                    view.setProjectDescription(loadedLibraryPreferences.getProjectPreferences().getDescription());
                                },
                                error -> {
                                });
    }

    public void cancel() {
        libraryPlaces.goToLibrary();
    }

    public void createProject(final String projectName,
                              final String projectDescription,
                              final DeploymentMode deploymentMode) {
        busyIndicatorView.showBusyIndicator(view.getCreatingProjectMessage());

        validateFields(projectName,
                       () -> {
                           libraryService.call(getSuccessCallback(),
                                               getErrorCallback(projectName,
                                                                projectDescription)).createProject(projectName,
                                                                                                   projectContext.getActiveOrganizationalUnit(),
                                                                                                   projectDescription,
                                                                                                   deploymentMode);
                       });
    }

    private void validateFields(final String projectName,
                                final Command successCallback) {
        if (projectName == null || projectName.trim().isEmpty()) {
            hideLoadingBox();
            notificationEvent.fire(new NotificationEvent(view.getEmptyNameMessage(),
                                                         NotificationEvent.NotificationType.ERROR));
            return;
        }

        validationService.call((Boolean isValid) -> {
            final String sanitizeProjectName = NewProjectUtils.sanitizeProjectName(projectName);
            if (Boolean.TRUE.equals(isValid) && !sanitizeProjectName.isEmpty()) {
                if (successCallback != null) {
                    successCallback.execute();
                }
            } else {
                hideLoadingBox();
                notificationEvent.fire(new NotificationEvent(view.getInvalidNameMessage(),
                                                             NotificationEvent.NotificationType.ERROR));
            }
        }).isProjectNameValid(projectName);
    }

    private RemoteCallback<WorkspaceProject> getSuccessCallback() {
        return project -> {

            hideLoadingBox();
            notifySuccess();
            goToProject(project);
        };
    }

    private ErrorCallback<?> getErrorCallback(final String projectName,
                                              final String projectDescription) {

        Map<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable> errors = new HashMap<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable>() {{
            put(GAVAlreadyExistsException.class,
                parameter -> {
                    libraryService.call((GAV gav) -> {
                        hideLoadingBox();
                        conflictingRepositoriesPopup.setContent(gav,
                                                                ((GAVAlreadyExistsException) parameter).getRepositories(),
                                                                () -> {
                                                                    conflictingRepositoriesPopup.hide();
                                                                    createProject(projectName,
                                                                                  projectDescription,
                                                                                  DeploymentMode.FORCED);
                                                                });
                        conflictingRepositoriesPopup.show();
                    }).createGAV(projectName,
                                 projectContext.getActiveOrganizationalUnit());
                });
            put(FileAlreadyExistsException.class,
                parameter -> {
                    hideLoadingBox();
                    notificationEvent.fire(new NotificationEvent(view.getDuplicatedProjectMessage(),
                                                                 NotificationEvent.NotificationType.ERROR));
                });
        }};

        return createErrorCallback(errors);
    }

    ErrorCallback<?> createErrorCallback(Map<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable> errors) {
        return new CommandWithThrowableDrivenErrorCallback(busyIndicatorView,
                                                           errors);
    }

    boolean isDuplicatedProjectName(Throwable throwable) {
        return throwable instanceof FileAlreadyExistsException;
    }

    private void goToProject(final WorkspaceProject project) {
        libraryPlaces.goToProject(project);
    }

    private void notifySuccess() {
        notificationEvent.fire(new NotificationEvent(view.getProjectCreatedSuccessfullyMessage(),
                                                     NotificationEvent.NotificationType.SUCCESS));
    }

    private void hideLoadingBox() {
        busyIndicatorView.hideBusyIndicator();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "New Project Screen";
    }

    @WorkbenchPartView
    public UberElement<NewProjectScreen> getView() {
        return view;
    }
}
