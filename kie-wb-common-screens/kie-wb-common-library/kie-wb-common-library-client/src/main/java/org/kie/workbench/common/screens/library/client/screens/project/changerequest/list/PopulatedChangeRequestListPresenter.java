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

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.changerequest.ChangeRequest;
import org.guvnor.structure.repositories.changerequest.ChangeRequestListUpdatedEvent;
import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.guvnor.structure.repositories.changerequest.ChangeRequestStatus;
import org.guvnor.structure.repositories.changerequest.ChangeRequestUpdatedEvent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.screens.EmptyState;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.ChangeRequestUtils;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.list.listitem.ChangeRequestListItemView;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.client.promise.Promises;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.common.client.select.SelectOption;
import org.uberfire.ext.widgets.common.client.select.SelectOptionImpl;
import org.uberfire.mvp.Command;

@Dependent
public class PopulatedChangeRequestListPresenter {

    public interface View extends UberElemental<PopulatedChangeRequestListPresenter> {

        void setCurrentPage(final int currentPage);

        void setPageIndicator(final String pageIndicatorText);

        void setTotalPages(final int totalPages);

        void clearList();

        void enablePreviousButton(final boolean isEnabled);

        void enableNextButton(final boolean isEnabled);

        void setFilterTypes(final List<SelectOption> categories);

        void clearSearch();

        void enableSubmitChangeRequestButton(final boolean isEnabled);

        void showEmptyState(final EmptyState emptyState);

        void hideEmptyState(final EmptyState emptyState);

        void addChangeRequestItem(final ChangeRequestListItemView item);

        void setFilterTextPlaceHolder(final String placeHolder);
    }

    private final View view;
    private final ProjectController projectController;
    private final LibraryPlaces libraryPlaces;
    private final Promises promises;
    private final EmptyState emptyState;
    private final TranslationService ts;
    private final ManagedInstance<ChangeRequestListItemView> changeRequestListItemViewInstances;
    private final Caller<ChangeRequestService> changeRequestService;
    private final BusyIndicatorView busyIndicatorView;
    private final ChangeRequestUtils changeRequestUtils;

    private WorkspaceProject workspaceProject;
    private int currentPage;
    private String searchFilter;
    private int totalPages;
    private String filterType;

    private static final int PAGE_SIZE = 10;

    private static final String FILTER_OPEN = "OPEN";
    private static final String FILTER_CLOSED = "CLOSED";
    private static final String FILTER_ALL = "ALL";

    @Inject
    public PopulatedChangeRequestListPresenter(final View view,
                                               final ProjectController projectController,
                                               final LibraryPlaces libraryPlaces,
                                               final Promises promises,
                                               final EmptyState emptyState,
                                               final TranslationService ts,
                                               final ManagedInstance<ChangeRequestListItemView> changeRequestItem,
                                               final Caller<ChangeRequestService> changeRequestService,
                                               final BusyIndicatorView busyIndicatorView,
                                               final ChangeRequestUtils changeRequestUtils) {
        this.view = view;
        this.projectController = projectController;
        this.libraryPlaces = libraryPlaces;
        this.promises = promises;
        this.emptyState = emptyState;
        this.ts = ts;
        this.changeRequestListItemViewInstances = changeRequestItem;
        this.changeRequestService = changeRequestService;
        this.busyIndicatorView = busyIndicatorView;
        this.changeRequestUtils = changeRequestUtils;
    }

    @PostConstruct
    public void postConstruct() {
        workspaceProject = this.libraryPlaces.getActiveWorkspace();

        this.prepareView();
    }

    public View getView() {
        return view;
    }

    public void onChangeRequestListUpdated(@Observes final ChangeRequestListUpdatedEvent event) {
        if (event.getRepositoryId().equals(workspaceProject.getRepository().getIdentifier())) {
            update();
        }
    }

    public void onChangeRequestUpdated(@Observes final ChangeRequestUpdatedEvent event) {
        if (event.getRepositoryId().equals(workspaceProject.getRepository().getIdentifier())) {
            update();
        }
    }

    public void nextPage() {
        if (this.currentPage + 1 <= this.totalPages) {
            this.currentPage++;
            this.update();
        }
    }

    public void prevPage() {
        if (this.currentPage - 1 >= 1) {
            this.currentPage--;
            this.update();
        }
    }

    public void setCurrentPage(int currentPage) {
        if (currentPage <= totalPages && currentPage > 0) {
            this.currentPage = currentPage;
            update();
        } else {
            this.view.setCurrentPage(this.currentPage);
        }
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
        this.searchFilter = "";
        this.view.clearSearch();
        this.currentPage = 1;
        this.update();
    }

    public void submitChangeRequest() {
        projectController.canSubmitChangeRequest(workspaceProject).then(userCanSubmitChangeRequest -> {
            if (userCanSubmitChangeRequest) {
                this.libraryPlaces.goToSubmitChangeRequestScreen();
            }

            return promises.resolve();
        });
    }

    public void search(String searchText) {
        this.searchFilter = searchText;
        this.currentPage = 1;
        this.update();
    }

    public void showSearchHitNothing() {
        this.showEmptyState(ts.getTranslation(LibraryConstants.EmptySearch),
                            ts.getTranslation(LibraryConstants.NoChangeRequestsFound));
    }

    private void prepareView() {
        this.view.init(this);

        projectController.canSubmitChangeRequest(workspaceProject).then(userCanSubmitChangeRequest -> {
            view.enableSubmitChangeRequestButton(userCanSubmitChangeRequest);
            return promises.resolve();
        });

        this.setupFilter();
        this.update();
    }

    private void setupListItems() {
        busyIndicatorView.showBusyIndicator(ts.getTranslation(LibraryConstants.Loading));

        this.view.clearList();

        if (filterType.equals(FILTER_ALL)) {
            changeRequestService.call((List<ChangeRequest> list) -> {
                createListItems(list);

                busyIndicatorView.hideBusyIndicator();
            }, new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView))
                    .getChangeRequests(workspaceProject.getSpace().getName(),
                                       workspaceProject.getRepository().getAlias(),
                                       Math.max(0, currentPage - 1),
                                       PAGE_SIZE,
                                       searchFilter);
        } else {
            changeRequestService.call((List<ChangeRequest> list) -> {
                createListItems(list);

                busyIndicatorView.hideBusyIndicator();
            }, new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView))
                    .getChangeRequests(workspaceProject.getSpace().getName(),
                                       workspaceProject.getRepository().getAlias(),
                                       Math.max(0, currentPage - 1),
                                       PAGE_SIZE,
                                       getStatusByFilterType(),
                                       searchFilter);
        }
    }

    private void createListItems(List<ChangeRequest> list) {
        list.forEach(item -> {
            ChangeRequestListItemView viewItem = changeRequestListItemViewInstances.get();

            viewItem.init(resolveChangeRequestStatusIcon(item.getStatus()),
                          item.toString(),
                          item.getAuthorId(),
                          changeRequestUtils.formatCreatedDate(item.getCreatedDate()),
                          formatChangedFiles(item.getChangedFilesCount()),
                          String.valueOf(item.getCommentsCount()),
                          selectCommand(item.getId()));

            this.view.addChangeRequestItem(viewItem);
        });
    }

    private String formatChangedFiles(int changedFilesCount) {
        if (changedFilesCount == 1) {
            return ts.getTranslation(LibraryConstants.ChangeRequestFilesSummaryOneFile);
        } else {
            return ts.format(LibraryConstants.ChangeRequestFilesSummaryManyFiles, changedFilesCount);
        }
    }

    private void setupFilter() {
        final List<SelectOption> filterTypes = createFilterTypes();
        this.view.setFilterTypes(filterTypes);
        this.filterType = filterTypes.get(0).getSelector();

        this.searchFilter = "";
        this.currentPage = 1;
        this.view.setFilterTextPlaceHolder(ts.getTranslation(LibraryConstants.SearchByIdOrSummary));
    }

    private void showEmptyState(String title, String message) {
        this.emptyState.clear();
        this.emptyState.setMessage(title, message);
        this.view.showEmptyState(emptyState);
    }

    private void hideEmptyState() {
        this.emptyState.clear();
        this.view.hideEmptyState(emptyState);
    }

    private Command selectCommand(final long changeRequestId) {
        return () -> libraryPlaces.goToChangeRequestReviewScreen(changeRequestId);
    }

    private void update() {
        busyIndicatorView.showBusyIndicator(ts.getTranslation(LibraryConstants.Loading));

        if (filterType.equals(FILTER_ALL)) {
            this.changeRequestService.call((Integer count) -> {
                busyIndicatorView.hideBusyIndicator();
                setupListItems(count);
            }, new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView))
                    .countChangeRequests(workspaceProject.getSpace().getName(),
                                         workspaceProject.getRepository().getAlias(),
                                         searchFilter);
        } else {
            this.changeRequestService.call((Integer count) -> {
                busyIndicatorView.hideBusyIndicator();
                setupListItems(count);
            }, new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView))
                    .countChangeRequests(workspaceProject.getSpace().getName(),
                                         workspaceProject.getRepository().getAlias(),
                                         getStatusByFilterType(),
                                         searchFilter);
        }
    }

    private void setupListItems(final Integer numberOfItems) {
        setupCounters(numberOfItems);
        setupListItems();
    }

    private IsWidget resolveChangeRequestStatusIcon(ChangeRequestStatus status) {
        //TODO: [caponetto] resolve the icons when UX provides them
        return new InlineLabel(changeRequestUtils.formatStatus(status).substring(0, 1).toUpperCase());
    }

    private void setupCounters(int count) {
        if (count == 0) {
            showSearchHitNothing();
        } else {
            hideEmptyState();
        }

        final int offset = (this.currentPage - 1) * PAGE_SIZE;
        final int fromCount = count > 0 ? offset + 1 : offset;
        final int toCount = this.resolveCounter(count,
                                                offset + PAGE_SIZE);
        final int totalCount = this.resolveCounter(count,
                                                   0);

        final String pageIndicatorText = fromCount + "-" + toCount + " " +
                ts.getTranslation(LibraryConstants.Of) + " " + totalCount;

        this.view.setPageIndicator(pageIndicatorText);

        this.totalPages = (int) Math.ceil(count / (float) PAGE_SIZE);
        this.view.setTotalPages(Math.max(this.totalPages, 1));

        this.view.setCurrentPage(this.currentPage);
        this.checkPaginationButtons();
    }

    private List<ChangeRequestStatus> getStatusByFilterType() {
        List<ChangeRequestStatus> statusList = new ArrayList<>();

        if (this.filterType.equals(FILTER_CLOSED)) {
            statusList.add(ChangeRequestStatus.ACCEPTED);
            statusList.add(ChangeRequestStatus.REJECTED);
            statusList.add(ChangeRequestStatus.REVERT_FAILED);
            statusList.add(ChangeRequestStatus.REVERTED);
        } else if (this.filterType.equals(FILTER_OPEN)) {
            statusList.add(ChangeRequestStatus.OPEN);
        } else {
            statusList.addAll(Arrays.asList(ChangeRequestStatus.values()));
        }

        return statusList;
    }

    private void checkPaginationButtons() {
        boolean isPreviousButtonEnabled = this.currentPage > 1;
        boolean isNextButtonEnabled = this.currentPage < this.totalPages;

        this.view.enablePreviousButton(isPreviousButtonEnabled);
        this.view.enableNextButton(isNextButtonEnabled);
    }

    private List<SelectOption> createFilterTypes() {
        return new ArrayList<SelectOption>() {{
            add(new SelectOptionImpl(FILTER_OPEN, ts.getTranslation(LibraryConstants.Open)));
            add(new SelectOptionImpl(FILTER_CLOSED, ts.getTranslation(LibraryConstants.Closed)));
            add(new SelectOptionImpl(FILTER_ALL, ts.getTranslation(LibraryConstants.ALL)));
        }};
    }

    private int resolveCounter(int numberOfChangeRequests,
                               int otherCounter) {
        if (numberOfChangeRequests < otherCounter || otherCounter == 0) {
            return numberOfChangeRequests;
        } else {
            return otherCounter;
        }
    }
}
