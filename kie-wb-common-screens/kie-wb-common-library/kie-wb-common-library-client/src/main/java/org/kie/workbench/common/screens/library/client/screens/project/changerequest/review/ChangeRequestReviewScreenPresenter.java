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

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.review;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequest;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestStatus;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestUpdatedEvent;
import org.guvnor.structure.repositories.changerequest.portable.NothingToMergeException;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.api.ProjectAssetListUpdated;
import org.kie.workbench.common.screens.library.client.perspective.LibraryPerspective;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.ChangeRequestUtils;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.review.tab.changedfiles.ChangedFilesScreenPresenter;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.review.tab.overview.OverviewScreenPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

@WorkbenchScreen(identifier = LibraryPlaces.CHANGE_REQUEST_REVIEW,
        owningPerspective = LibraryPerspective.class)
public class ChangeRequestReviewScreenPresenter {

    private final View view;
    private final TranslationService ts;
    private final LibraryPlaces libraryPlaces;
    private final Caller<ChangeRequestService> changeRequestService;
    private final BusyIndicatorView busyIndicatorView;
    private final OverviewScreenPresenter overviewScreen;
    private final ChangedFilesScreenPresenter changedFilesScreen;
    private final Promises promises;
    private final ProjectController projectController;
    private final Event<NotificationEvent> notificationEvent;
    private WorkspaceProject workspaceProject;
    private long currentChangeRequestId;
    private Branch currentTargetBranch;
    private boolean overviewTabLoaded;
    private boolean changedFilesTabLoaded;

    @Inject
    public ChangeRequestReviewScreenPresenter(final View view,
                                              final TranslationService ts,
                                              final LibraryPlaces libraryPlaces,
                                              final Caller<ChangeRequestService> changeRequestService,
                                              final BusyIndicatorView busyIndicatorView,
                                              final OverviewScreenPresenter overviewScreen,
                                              final ChangedFilesScreenPresenter changedFilesScreen,
                                              final Promises promises,
                                              final ProjectController projectController,
                                              final Event<NotificationEvent> notificationEvent) {
        this.view = view;
        this.ts = ts;
        this.libraryPlaces = libraryPlaces;
        this.changeRequestService = changeRequestService;
        this.busyIndicatorView = busyIndicatorView;
        this.overviewScreen = overviewScreen;
        this.changedFilesScreen = changedFilesScreen;
        this.promises = promises;
        this.projectController = projectController;
        this.notificationEvent = notificationEvent;
    }

    @PostConstruct
    public void postConstruct() {
        this.workspaceProject = libraryPlaces.getActiveWorkspace();

        this.view.init(this);
        this.view.setTitle(this.getTitle());
    }

    public void refreshOnFocus(@Observes final SelectPlaceEvent selectPlaceEvent) {
        if (workspaceProject != null && workspaceProject.getMainModule() != null) {
            final PlaceRequest place = selectPlaceEvent.getPlace();

            if (place.getIdentifier().equals(LibraryPlaces.CHANGE_REQUEST_REVIEW)) {
                String changeRequestIdValue = place.getParameter(ChangeRequestUtils.CHANGE_REQUEST_ID_KEY, null);

                if (changeRequestIdValue != null && !changeRequestIdValue.equals("") &&
                        place.getIdentifier().equals(LibraryPlaces.CHANGE_REQUEST_REVIEW)) {
                    this.currentChangeRequestId = Long.parseLong(changeRequestIdValue);
                    this.init(false);
                }
            }
        }
    }

    public void onChangeRequestUpdated(@Observes final ChangeRequestUpdatedEvent event) {
        if (event.getRepositoryId().equals(workspaceProject.getRepository().getIdentifier())
                && event.getChangeRequestId() == currentChangeRequestId) {
            this.init(true);
        }
    }

    public void onProjectAssetListUpdated(@Observes final ProjectAssetListUpdated event) {
        if (event.getProject().getRepository().getIdentifier().equals(this.workspaceProject.getRepository().getIdentifier())) {
            this.init(true);
        }
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ts.getTranslation(LibraryConstants.ChangeRequest);
    }

    @WorkbenchPartView
    public View getView() {
        return view;
    }

    public void showOverviewContent() {
        this.view.setContent(this.overviewScreen.getView().getElement());
    }

    public void showChangedFilesContent() {
        this.view.setContent(this.changedFilesScreen.getView().getElement());
    }

    public void cancel() {
        this.reset(false);

        this.libraryPlaces.goToProject(workspaceProject);
    }

    public void reject() {
        this.doActionIfAllowed(this::rejectChangeRequestAction);
    }

    public void accept() {
        this.doActionIfAllowed(this::acceptChangeRequestAction);
    }

    public void revert() {
        this.doActionIfAllowed(this::revertChangeRequestAction);
    }

    private void doActionIfAllowed(final Runnable action) {
        projectController.canUpdateBranch(workspaceProject,
                                          this.currentTargetBranch).then(userCanUpdateBranch -> {
            if (userCanUpdateBranch) {
                action.run();
            }

            return promises.resolve();
        });
    }

    private void init(final boolean isReload) {
        busyIndicatorView.showBusyIndicator(ts.getTranslation(LibraryConstants.Loading));

        this.reset(isReload);
        this.setup(isReload);
    }

    private void reset(final boolean isReload) {
        overviewTabLoaded = false;
        changedFilesTabLoaded = false;

        this.overviewScreen.reset();
        this.changedFilesScreen.reset();

        this.view.resetAll();

        if (!isReload) {
            this.view.activateOverviewTab();
        }
    }

    private void setup(final boolean isReload) {
        changeRequestService.call((ChangeRequest changeRequest) -> this.loadChangeRequest(changeRequest,
                                                                                          isReload),
                                  new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView))
                .getChangeRequest(workspaceProject.getSpace().getName(),
                                  workspaceProject.getRepository().getAlias(),
                                  currentChangeRequestId);
    }

    private void loadChangeRequest(final ChangeRequest changeRequest,
                                   final boolean isReload) {
        this.view.setTitle(ts.format(LibraryConstants.ChangeRequestAndId, currentChangeRequestId));

        this.currentTargetBranch = workspaceProject.getRepository().getBranch(changeRequest.getTargetBranch())
                .orElseThrow(() -> new IllegalStateException(
                        "The branch " + changeRequest.getTargetBranch() + " does not exist."));

        this.setupButtons(changeRequest,
                          this.currentTargetBranch);

        this.overviewScreen.setup(changeRequest,
                                  (Boolean success) -> {
                                      overviewTabLoaded = true;
                                      finishLoading();
                                  });
        this.changedFilesScreen.setup(changeRequest,
                                      (final Boolean success) -> {
                                          changedFilesTabLoaded = true;
                                          finishLoading();
                                      }, this.view::setChangedFilesCount);

        if (!isReload) {
            this.view.activateOverviewTab();
        }
    }

    private void setupButtons(final ChangeRequest changeRequest,
                              final Branch targetBranch) {
        projectController.canUpdateBranch(workspaceProject, targetBranch).then(userCanUpdateBranch -> {
            if (userCanUpdateBranch) {
                if (changeRequest.getStatus() == ChangeRequestStatus.ACCEPTED) {
                    this.view.showRevertButton(true);
                } else if (changeRequest.getStatus() == ChangeRequestStatus.OPEN) {
                    final boolean canBeAccepted = !changeRequest.isConflict() &&
                            changeRequest.getChangedFilesCount() > 0;

                    this.view.showRejectButton(true);
                    this.view.showAcceptButton(true);
                    this.view.enableAcceptButton(canBeAccepted);
                }
            }
            return promises.resolve();
        });
    }

    private void rejectChangeRequestAction() {
        this.changeRequestService.call(v -> {
            fireNotificationEvent(ts.format(LibraryConstants.ChangeRequestRejectMessage,
                                            currentChangeRequestId),
                                  NotificationEvent.NotificationType.SUCCESS);
        }).rejectChangeRequest(workspaceProject.getSpace().getName(),
                               workspaceProject.getRepository().getAlias(),
                               currentChangeRequestId);
    }

    private void acceptChangeRequestAction() {
        busyIndicatorView.showBusyIndicator(ts.getTranslation(LibraryConstants.Loading));

        this.changeRequestService.call((final Boolean succeeded) -> {
            if (succeeded) {
                fireNotificationEvent(ts.format(LibraryConstants.ChangeRequestAcceptMessage,
                                                currentChangeRequestId),
                                      NotificationEvent.NotificationType.SUCCESS);
            }

            busyIndicatorView.hideBusyIndicator();
        }, acceptChangeRequestErrorCallback())
                .acceptChangeRequest(workspaceProject.getSpace().getName(),
                               workspaceProject.getRepository().getAlias(),
                               currentChangeRequestId);
    }

    private ErrorCallback<Object> acceptChangeRequestErrorCallback() {
        return (message, throwable) -> {
            busyIndicatorView.hideBusyIndicator();

            if (throwable instanceof NothingToMergeException) {
                notificationEvent.fire(
                        new NotificationEvent(ts.getTranslation(LibraryConstants.NothingToMergeMessage),
                                              NotificationEvent.NotificationType.WARNING));
                return false;
            }

            return true;
        };
    }

    private void revertChangeRequestAction() {
        this.changeRequestService.call((final Boolean succeeded) -> {
            if (succeeded) {
                fireNotificationEvent(ts.format(LibraryConstants.ChangeRequestRevertMessage,
                                                currentChangeRequestId),
                                      NotificationEvent.NotificationType.SUCCESS);
            } else {
                fireNotificationEvent(ts.format(LibraryConstants.ChangeRequestRevertFailMessage,
                                                currentChangeRequestId),
                                      NotificationEvent.NotificationType.WARNING);
            }
        }).revertChangeRequest(workspaceProject.getSpace().getName(),
                               workspaceProject.getRepository().getAlias(),
                               currentChangeRequestId);
    }

    private void fireNotificationEvent(final String message,
                                       final NotificationEvent.NotificationType type) {
        notificationEvent.fire(new NotificationEvent(message,
                                                     type));
    }

    private void finishLoading() {
        if (overviewTabLoaded && changedFilesTabLoaded) {
            busyIndicatorView.hideBusyIndicator();
        }
    }

    public interface View extends UberElemental<ChangeRequestReviewScreenPresenter> {

        void setTitle(final String title);

        void setChangedFilesCount(final int count);

        void setContent(final HTMLElement content);

        void showRejectButton(final boolean isVisible);

        void showAcceptButton(final boolean isVisible);

        void enableAcceptButton(final boolean isEnabled);

        void showRevertButton(final boolean isVisible);

        void activateOverviewTab();

        void activateChangedFilesTab();

        void resetAll();
    }
}
