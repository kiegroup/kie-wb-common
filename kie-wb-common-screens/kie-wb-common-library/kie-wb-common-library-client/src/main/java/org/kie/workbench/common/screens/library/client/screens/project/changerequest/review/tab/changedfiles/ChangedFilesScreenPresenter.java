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

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.review.tab.changedfiles;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.changerequest.ChangeRequest;
import org.guvnor.structure.repositories.changerequest.ChangeRequestDiff;
import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.guvnor.structure.repositories.changerequest.ChangeRequestStatus;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.ChangeRequestUtils;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.diff.DiffItemPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class ChangedFilesScreenPresenter {

    public interface View extends UberElemental<ChangedFilesScreenPresenter> {

        void addDiffItem(final DiffItemPresenter.View item, Runnable draw);

        void clearDiffList();

        void setFilesSummary(final String text);

        void resetAll();
    }

    private final View view;
    private final ManagedInstance<DiffItemPresenter> diffItemPresenterInstances;
    private final ChangeRequestUtils changeRequestUtils;
    private final Caller<ChangeRequestService> changeRequestService;
    private final LibraryPlaces libraryPlaces;

    private WorkspaceProject workspaceProject;

    @Inject
    public ChangedFilesScreenPresenter(final View view,
                                       final ManagedInstance<DiffItemPresenter> diffItemPresenterInstances,
                                       final ChangeRequestUtils changeRequestUtils,
                                       final Caller<ChangeRequestService> changeRequestService,
                                       final LibraryPlaces libraryPlaces) {
        this.view = view;
        this.diffItemPresenterInstances = diffItemPresenterInstances;
        this.changeRequestUtils = changeRequestUtils;
        this.changeRequestService = changeRequestService;
        this.libraryPlaces = libraryPlaces;
    }

    @PostConstruct
    public void postConstruct() {
        this.workspaceProject = this.libraryPlaces.getActiveWorkspace();

        this.prepareView();
    }

    public View getView() {
        return view;
    }

    private void prepareView() {
        this.view.init(this);
    }

    public void reset() {
        this.view.resetAll();
    }

    public void setup(final ChangeRequest changeRequest) {
        changeRequestService.call((final List<ChangeRequestDiff> diffList) -> {
            final boolean warnConflict = changeRequest.getStatus() == ChangeRequestStatus.OPEN;
            this.setupFilesSummary(diffList);
            this.setupDiffList(diffList, warnConflict);
        }).getDiff(workspaceProject.getSpace().getName(),
                   workspaceProject.getRepository().getAlias(),
                   changeRequest.getId());
    }

    private void setupFilesSummary(final List<ChangeRequestDiff> diffList) {
        final int changedFilesCount = diffList.size();
        final int addedLinesCount = diffList.stream().mapToInt(elem -> elem.getAddedLinesCount()).sum();
        final int deletedLinesCount = diffList.stream().mapToInt(elem -> elem.getDeletedLinesCount()).sum();

        view.setFilesSummary(changeRequestUtils.formatFilesSummary(changedFilesCount,
                                                                   addedLinesCount,
                                                                   deletedLinesCount));
    }

    private void setupDiffList(final List<ChangeRequestDiff> diffList,
                               final boolean warnConflict) {
        diffList.forEach(diff -> {
            DiffItemPresenter item = diffItemPresenterInstances.get();
            item.setup(diff, warnConflict);
            this.view.addDiffItem(item.getView(), () -> item.draw());
        });
    }
}
