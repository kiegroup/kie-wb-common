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

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.tab;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.changerequest.ChangeRequest;
import org.guvnor.structure.repositories.changerequest.ChangeRequestListUpdatedEvent;
import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.guvnor.structure.repositories.changerequest.ChangeRequestStatus;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.screens.EmptyState;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.tab.listitem.ChangeRequestListItemView;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.client.promise.Promises;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.common.client.select.SelectOption;
import org.uberfire.ext.widgets.common.client.select.SelectOptionImpl;
import org.uberfire.mvp.Command;

@Dependent
public class PopulatedChangeRequestListPresenter {

    public interface View extends UberElemental<PopulatedChangeRequestListPresenter> {

        void setCurrentPage(int currentPage);

        void setPageIndicator(int from, int to, int total);

        void setTotalPages(int totalPages);

        void clearList();

        void enablePreviousButton(boolean isEnabled);

        void enableNextButton(boolean isEnabled);

        void setFilterTypes(List<SelectOption> categories);

        void clearSearch();

        void enableSubmitChangeRequestButton(boolean isEnabled);

        void showEmptyState(EmptyState emptyState);

        void hideEmptyState(EmptyState emptyState);

        void addChangeRequestItem(ChangeRequestListItemView item);
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

    private WorkspaceProject workspaceProject;
    private int currentPage;
    private String searchFilter;
    private int totalPages;
    private String filterType;

    private static final int PAGE_SIZE = 10;

    private static final String FILTER_OPEN = "OPEN";
    private static final String FILTER_CLOSED = "CLOSED";
    private static final String FILTER_ALL = "ALL";
    private static final String CREATED_DATE_FORMAT = "MMM d, yyyy";

    @Inject
    public PopulatedChangeRequestListPresenter(final View view,
                                               final ProjectController projectController,
                                               final LibraryPlaces libraryPlaces,
                                               final Promises promises,
                                               final EmptyState emptyState,
                                               final TranslationService ts,
                                               final ManagedInstance<ChangeRequestListItemView> changeRequestItem,
                                               final Caller<ChangeRequestService> changeRequestService,
                                               final BusyIndicatorView busyIndicatorView) {
        this.view = view;
        this.projectController = projectController;
        this.libraryPlaces = libraryPlaces;
        this.promises = promises;
        this.emptyState = emptyState;
        this.ts = ts;
        this.changeRequestListItemViewInstances = changeRequestItem;
        this.changeRequestService = changeRequestService;
        this.busyIndicatorView = busyIndicatorView;
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
        busyIndicatorView.showBusyIndicator(ts.getTranslation(LibraryConstants.LoadingChangeRequests));
        this.update(busyIndicatorView::hideBusyIndicator);
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

        setupFilter();
        update();
    }

    private void setupListItems() {
        this.view.clearList();

        if (filterType.equals(FILTER_ALL)) {
            changeRequestService.call((List<ChangeRequest> list) -> createListItems(list))
                    .getChangeRequests(workspaceProject.getSpace().getName(),
                                       workspaceProject.getRepository().getAlias(),
                                       Math.max(0, currentPage - 1),
                                       PAGE_SIZE,
                                       searchFilter);
        } else {
            changeRequestService.call((List<ChangeRequest> list) -> createListItems(list))
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
                          item.getAuthor(),
                          formatDate(item.getCreatedDate()),
                          formatChangedFiles(item.getChangedFilesCount()),
                          String.valueOf(item.getComments().size()),
                          selectCommand(item.toString()));

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

    private String formatDate(Date date) {
        return DateTimeFormat.getFormat(CREATED_DATE_FORMAT).format(date);
    }

    private void setupFilter() {
        List<SelectOption> filterTypes = createFilterTypes();
        this.view.setFilterTypes(filterTypes);
        filterType = filterTypes.get(0).getSelector();

        this.searchFilter = "";
        this.currentPage = 1;
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

    private Command selectCommand(final String changeRequestTitle) {
        return () -> libraryPlaces.goToChangeRequestReviewScreen(changeRequestTitle);
    }

    private void update() {
        this.update(() -> {
        });
    }

    private void update(Runnable runnable) {
        this.resolveChangeRequestsCount();
        this.setupListItems();
        this.view.setCurrentPage(this.currentPage);
        this.checkPaginationButtons();

        runnable.run();
    }

    private void resolveChangeRequestsCount() {
        if (filterType.equals(FILTER_ALL)) {
            this.changeRequestService.call((Integer count) -> setupCounters(count))
                    .countChangeRequests(workspaceProject.getSpace().getName(),
                                         workspaceProject.getRepository().getAlias(),
                                         searchFilter);
        } else {
            this.changeRequestService.call((Integer count) -> setupCounters(count))
                    .countChangeRequests(workspaceProject.getSpace().getName(),
                                         workspaceProject.getRepository().getAlias(),
                                         getStatusByFilterType(),
                                         searchFilter);
        }
    }

    private IsWidget resolveChangeRequestStatusIcon(ChangeRequestStatus status) {
        //TODO: [caponetto] resolve the icons when UX provides them
        return null;
    }

    private void setupCounters(int count) {
        if (count == 0) {
            showSearchHitNothing();
        } else {
            hideEmptyState();
        }

        int offset = (this.currentPage - 1) * PAGE_SIZE;
        this.view.setPageIndicator(count > 0 ? offset + 1 : offset,
                                   this.resolverCounter(count,
                                                        offset + PAGE_SIZE),
                                   this.resolverCounter(count,
                                                        0));
        this.totalPages = (int) Math.ceil(count / (float) PAGE_SIZE);
        this.view.setTotalPages(Math.max(this.totalPages, 1));
    }

    private ChangeRequestStatus getStatusByFilterType() {
        ChangeRequestStatus result = ChangeRequestStatus.OPEN;

        if (this.filterType.equals(FILTER_CLOSED)) {
            result = ChangeRequestStatus.CLOSED;
        }

        return result;
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

    private int resolverCounter(int numberOfChangeRequests,
                                int otherCounter) {
        if (numberOfChangeRequests < otherCounter || otherCounter == 0) {
            return numberOfChangeRequests;
        } else {
            return otherCounter;
        }
    }
}
