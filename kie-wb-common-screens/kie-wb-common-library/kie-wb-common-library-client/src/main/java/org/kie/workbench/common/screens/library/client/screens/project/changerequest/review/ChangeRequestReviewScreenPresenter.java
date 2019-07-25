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
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.changerequest.ChangeRequest;
import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.guvnor.structure.repositories.changerequest.ChangeRequestStatus;
import org.guvnor.structure.repositories.changerequest.ChangeRequestUpdatedEvent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.perspective.LibraryPerspective;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.ChangeRequestUtils;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.diff.DiffItemPresenter;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.review.tab.changedfiles.ChangedFilesScreenPresenter;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.review.tab.overview.OverviewScreenPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.lifecycle.OnLostFocus;
import org.uberfire.mvp.PlaceRequest;

@WorkbenchScreen(identifier = LibraryPlaces.CHANGE_REQUEST_REVIEW,
        owningPerspective = LibraryPerspective.class)
public class ChangeRequestReviewScreenPresenter {

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
    }

    private final View view;
    private final TranslationService ts;
    private final LibraryPlaces libraryPlaces;
    private final ManagedInstance<DiffItemPresenter> diffItemPresenters;
    private final Caller<ChangeRequestService> changeRequestService;
    private final BusyIndicatorView busyIndicatorView;
    private final OverviewScreenPresenter overviewScreen;
    private final ChangedFilesScreenPresenter changedFilesScreen;
    private final Promises promises;
    private final ProjectController projectController;

    private WorkspaceProject workspaceProject;
    private long currentChangeRequestId;
    private Branch currentTargetBranch;

    @Inject
    public ChangeRequestReviewScreenPresenter(final View view,
                                              final TranslationService ts,
                                              final LibraryPlaces libraryPlaces,
                                              final ManagedInstance<DiffItemPresenter> diffItemPresenters,
                                              final Caller<ChangeRequestService> changeRequestService,
                                              final BusyIndicatorView busyIndicatorView,
                                              final OverviewScreenPresenter overviewScreen,
                                              final ChangedFilesScreenPresenter changedFilesScreen,
                                              final Promises promises,
                                              final ProjectController projectController) {
        this.view = view;
        this.ts = ts;
        this.libraryPlaces = libraryPlaces;
        this.diffItemPresenters = diffItemPresenters;
        this.changeRequestService = changeRequestService;
        this.busyIndicatorView = busyIndicatorView;
        this.overviewScreen = overviewScreen;
        this.changedFilesScreen = changedFilesScreen;
        this.promises = promises;
        this.projectController = projectController;
    }

    @PostConstruct
    public void postConstruct() {
        workspaceProject = libraryPlaces.getActiveWorkspace();

        this.prepareView();
    }

    @OnLostFocus
    public void onLostFocus() {
        this.reset();

        overviewScreen.reset();
        changedFilesScreen.reset();
    }

    public void refreshOnFocus(@Observes final SelectPlaceEvent selectPlaceEvent) {
        if (workspaceProject != null && workspaceProject.getMainModule() != null) {
            final PlaceRequest place = selectPlaceEvent.getPlace();

            String changeRequestIdValue = place.getParameter(ChangeRequestUtils.CHANGE_REQUEST_ID_KEY, null);

            if (changeRequestIdValue != null && !changeRequestIdValue.equals("") &&
                    place.getIdentifier().equals(LibraryPlaces.CHANGE_REQUEST_REVIEW)) {
                this.currentChangeRequestId = Long.valueOf(changeRequestIdValue);
                this.reset();
                this.setup(false);
            } else if (libraryPlaces.isChangeRequestReviewScreenOpen()) {
                this.setup(true);
            }
        }
    }

    public void onChangeRequestUpdated(@Observes final ChangeRequestUpdatedEvent event) {
        if (event.getRepositoryId().equals(workspaceProject.getRepository().getIdentifier())
                && event.getChangeRequestId() == currentChangeRequestId) {
            this.setup(true);
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
        this.libraryPlaces.goToProject(workspaceProject);
    }

    public void reject() {
        projectController.canUpdateBranch(workspaceProject,
                                          this.currentTargetBranch).then(userCanUpdateBranch -> {
            if (userCanUpdateBranch) {
                doRejectChangeRequest();
            }

            return promises.resolve();
        });
    }

    public void accept() {
        projectController.canUpdateBranch(workspaceProject,
                                          this.currentTargetBranch).then(userCanUpdateBranch -> {
            if (userCanUpdateBranch) {
                doAcceptChangeRequest();
            }

            return promises.resolve();
        });
    }

    public void revert() {
        projectController.canUpdateBranch(workspaceProject,
                                          this.currentTargetBranch).then(userCanUpdateBranch -> {
            if (userCanUpdateBranch) {
                doRevertChangeRequest();
            }

            return promises.resolve();
        });
    }

    private void prepareView() {
        this.view.init(this);
        this.view.setTitle(this.getTitle());
    }

    private void reset() {
        this.view.showAcceptButton(false);
        this.view.showRejectButton(false);
        this.view.showRevertButton(false);
        this.view.enableAcceptButton(true);
    }

    private void setup(final boolean isReload) {
        changeRequestService.call((ChangeRequest changeRequest) -> this.loadChangeRequest(changeRequest,
                                                                                          isReload))
                .getChangeRequest(workspaceProject.getSpace().getName(),
                                  workspaceProject.getRepository().getAlias(),
                                  currentChangeRequestId);
    }

    private void loadChangeRequest(final ChangeRequest changeRequest,
                                   final boolean isReload) {
        this.view.setTitle(ts.format(LibraryConstants.ChangeRequestAndId, currentChangeRequestId));
        this.view.setChangedFilesCount(changeRequest.getChangedFilesCount());

        this.currentTargetBranch = workspaceProject.getRepository().getBranch(changeRequest.getTargetBranch())
                .orElseThrow(() -> new IllegalStateException(
                        "The branch " + changeRequest.getTargetBranch() + " does not exist."));

        this.setupButtons(changeRequest,
                          this.currentTargetBranch);

        this.overviewScreen.reset();
        this.overviewScreen.setup(changeRequest);
        this.changedFilesScreen.reset();
        this.changedFilesScreen.setup(changeRequest);

        if (!isReload) {
            this.view.activateOverviewTab();
            this.showOverviewContent();
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

    private void doRejectChangeRequest() {
        this.changeRequestService.call(v -> this.libraryPlaces.goToProject(workspaceProject))
                .rejectChangeRequest(workspaceProject.getSpace().getName(),
                                     workspaceProject.getRepository().getAlias(),
                                     currentChangeRequestId);
    }

    private void doAcceptChangeRequest() {
        this.changeRequestService.call((Boolean succeeded) -> {
            if (succeeded) {
                this.libraryPlaces.goToProject(workspaceProject);
            } else {
                //TODO: [caponetto] handle this case
            }
        }).acceptChangeRequest(workspaceProject.getSpace().getName(),
                               workspaceProject.getRepository().getAlias(),
                               currentChangeRequestId);
    }

    private void doRevertChangeRequest() {
        this.changeRequestService.call((Boolean succeeded) -> {
            if (succeeded) {
                this.libraryPlaces.goToProject(workspaceProject);
            } else {
                //TODO: [caponetto] handle this case
            }
        }).tryRevertChangeRequest(workspaceProject.getSpace().getName(),
                                  workspaceProject.getRepository().getAlias(),
                                  currentChangeRequestId);
    }
}
