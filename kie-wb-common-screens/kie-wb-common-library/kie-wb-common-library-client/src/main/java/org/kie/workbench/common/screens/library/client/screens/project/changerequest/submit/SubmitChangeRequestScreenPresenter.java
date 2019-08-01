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

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.submit;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.changerequest.ChangeRequest;
import org.guvnor.structure.repositories.changerequest.ChangeRequestDiff;
import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.perspective.LibraryPerspective;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.ChangeRequestUtils;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.diff.DiffItemPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.services.datamodel.util.SortHelper;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnLostFocus;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

@WorkbenchScreen(identifier = LibraryPlaces.SUBMIT_CHANGE_REQUEST,
        owningPerspective = LibraryPerspective.class)
public class SubmitChangeRequestScreenPresenter {

    public interface View extends UberElemental<SubmitChangeRequestScreenPresenter> {

        void setTitle(final String title);

        void setDescription(final String description);

        void setDestinationBranches(final List<String> branches, int selectedIdx);

        void showWarning(final boolean isVisible);

        void addDiffItem(final DiffItemPresenter.View item, Runnable draw);

        String getSummary();

        String getDescription();

        void clearErrors();

        void clearDiffList();

        void setFilesSummary(final String text);

        void enableSubmitButton(boolean isEnabled);

        void setSummaryError();

        void setDescriptionError();

        void showDiff(boolean isVisible);

        void clearInputFields();
    }

    private final View view;
    private final TranslationService ts;
    private final LibraryPlaces libraryPlaces;
    private final ManagedInstance<DiffItemPresenter> diffItemPresenterInstances;
    private final Caller<ChangeRequestService> changeRequestService;
    private final ProjectController projectController;
    private final Promises promises;
    private final BusyIndicatorView busyIndicatorView;
    private final ChangeRequestUtils changeRequestUtils;
    private final Event<NotificationEvent> notificationEvent;

    private WorkspaceProject workspaceProject;
    private String currentBranchName;
    private Branch defaultBranch;
    private String destinationBranch;

    @Inject
    public SubmitChangeRequestScreenPresenter(final View view,
                                              final TranslationService ts,
                                              final LibraryPlaces libraryPlaces,
                                              final ManagedInstance<DiffItemPresenter> diffItemPresenterInstances,
                                              final Caller<ChangeRequestService> changeRequestService,
                                              final ProjectController projectController,
                                              final Promises promises,
                                              final BusyIndicatorView busyIndicatorView,
                                              final ChangeRequestUtils changeRequestUtils,
                                              final Event<NotificationEvent> notificationEvent) {
        this.view = view;
        this.ts = ts;
        this.libraryPlaces = libraryPlaces;
        this.diffItemPresenterInstances = diffItemPresenterInstances;
        this.changeRequestService = changeRequestService;
        this.projectController = projectController;
        this.promises = promises;
        this.busyIndicatorView = busyIndicatorView;
        this.changeRequestUtils = changeRequestUtils;
        this.notificationEvent = notificationEvent;
    }

    @PostConstruct
    public void postConstruct() {
        workspaceProject = libraryPlaces.getActiveWorkspace();
        currentBranchName = workspaceProject.getBranch().getName();

        defaultBranch = workspaceProject.getRepository().getDefaultBranch()
                .orElseThrow(() -> new IllegalStateException("The default branch does not exist"));

        destinationBranch = defaultBranch.getName();

        this.prepareView();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ts.getTranslation(LibraryConstants.SubmitChangeRequest);
    }

    @WorkbenchPartView
    public View getView() {
        return view;
    }

    @OnClose
    public void onClose() {
        destroyDiffItems();
    }

    @OnLostFocus
    public void onLostFocus() {
        destroyDiffItems();
    }

    public void refreshOnFocus(@Observes final SelectPlaceEvent selectPlaceEvent) {
        if (workspaceProject != null && workspaceProject.getMainModule() != null) {
            final PlaceRequest place = selectPlaceEvent.getPlace();
            if (place.getIdentifier().equals(LibraryPlaces.SUBMIT_CHANGE_REQUEST)) {
                this.reset();
                this.setup();
            }
        }
    }

    public void cancel() {
        this.libraryPlaces.goToProject(workspaceProject);
    }

    public void submit() {
        if (!validateFields()) {
            return;
        }

        projectController.canSubmitChangeRequest(workspaceProject,
                                                 destinationBranch).then(userCanSubmitChangeRequest -> {
            if (userCanSubmitChangeRequest) {
                changeRequestService.call((ChangeRequest item) -> {
                    notificationEvent.fire(
                            new NotificationEvent(ts.format(LibraryConstants.ChangeRequestSubmitMessage,
                                                            item.getId()),
                                                  NotificationEvent.NotificationType.SUCCESS));

                    this.libraryPlaces.goToChangeRequestReviewScreen(item.getId());
                }).createChangeRequest(workspaceProject.getSpace().getName(),
                                       workspaceProject.getRepository().getAlias(),
                                       currentBranchName,
                                       destinationBranch,
                                       view.getSummary(),
                                       view.getDescription());
            }
            return promises.resolve();
        });
    }

    public void selectBranch(String branchName) {
        destinationBranch = branchName;
        updateDiffContainer();
    }

    public void updateDiffContainer() {
        busyIndicatorView.showBusyIndicator(ts.getTranslation(LibraryConstants.Loading));

        view.showWarning(false);
        view.showDiff(false);
        view.setFilesSummary("");
        view.clearDiffList();
        destroyDiffItems();

        changeRequestService.call((final List<ChangeRequestDiff> diffList) -> {
            boolean hideDiff = diffList.isEmpty();

            if (hideDiff) {
                setupEmptyDiffList();
            } else {
                setupPopulatedDiffList(diffList);
            }

            view.showDiff(!hideDiff);
            busyIndicatorView.hideBusyIndicator();
        }, new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView))
                .getDiff(workspaceProject.getSpace().getName(),
                         workspaceProject.getRepository().getAlias(),
                         currentBranchName,
                         destinationBranch);
    }

    private void destroyDiffItems() {
        diffItemPresenterInstances.destroyAll();
    }

    private void prepareView() {
        this.view.init(this);
        this.view.setTitle(this.getTitle());
    }

    private void reset() {
        view.clearInputFields();
    }

    private void setup() {
        updateDestinationBranchList();
        updateDiffContainer();
    }

    private void setupEmptyDiffList() {
        view.enableSubmitButton(false);
        view.setFilesSummary(ts.getTranslation(LibraryConstants.BranchesAreEven));
    }

    private boolean validateFields() {
        boolean isValid = true;

        Predicate<String> isInvalidContent = content -> content == null || content.trim().isEmpty();

        view.clearErrors();

        if (isInvalidContent.test(view.getSummary())) {
            view.setSummaryError();
            isValid = false;
        }

        if (isInvalidContent.test(view.getDescription())) {
            view.setDescriptionError();
            isValid = false;
        }

        return isValid;
    }

    private void setupPopulatedDiffList(final List<ChangeRequestDiff> diffList) {
        final int changedFilesCount = diffList.size();
        final int addedLinesCount = diffList.stream().mapToInt(elem -> elem.getAddedLinesCount()).sum();
        final int deletedLinesCount = diffList.stream().mapToInt(elem -> elem.getDeletedLinesCount()).sum();

        view.showWarning(diffList.stream().anyMatch(elem -> elem.isConflict()));
        view.enableSubmitButton(true);
        view.setFilesSummary(changeRequestUtils.formatFilesSummary(changedFilesCount,
                                                                   addedLinesCount,
                                                                   deletedLinesCount));

        diffList.forEach(diff -> {
            DiffItemPresenter item = diffItemPresenterInstances.get();
            item.setup(diff, true);
            this.view.addDiffItem(item.getView(), () -> item.draw());
        });
    }

    private void updateDestinationBranchList() {
        projectController.getReadableBranches(libraryPlaces.getActiveWorkspace()).then(branches -> {
            List<String> destinationBranchNames = branches.stream()
                    .map(Branch::getName)
                    .filter(branchName -> !branchName.equals(currentBranchName))
                    .sorted(SortHelper.ALPHABETICAL_ORDER_COMPARATOR)
                    .collect(Collectors.toList());

            OptionalInt defaultBranchIdx = IntStream.range(0, destinationBranchNames.size())
                    .filter(i -> defaultBranch.getName().equals(destinationBranchNames.get(i)))
                    .findFirst();

            view.setDestinationBranches(destinationBranchNames, defaultBranchIdx.orElse(0));

            return promises.resolve();
        });
    }
}