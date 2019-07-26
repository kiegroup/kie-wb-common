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

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.review.tab.overview;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.changerequest.ChangeRequest;
import org.guvnor.structure.repositories.changerequest.ChangeRequestComment;
import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.guvnor.structure.repositories.changerequest.ChangeRequestStatus;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.ChangeRequestUtils;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.review.comment.CommentItemPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.rpc.SessionInfo;

@Dependent
public class OverviewScreenPresenter {

    public interface View extends UberElemental<OverviewScreenPresenter> {

        void setStatus(final String status);

        void setAuthor(final String author);

        void setCreatedDate(final String createdDate);

        void setSummary(final String summary);

        String getSummaryInputText();

        void setDescription(final String description);

        String getDescriptionInputText();

        void setSourceBranch(final String sourceBranch);

        void setTargetBranch(final String targetBranch);

        void setCommentCounterText(final String counterText);

        void setCommentInputPlaceHolder(final String placeHolder);

        void addCommentItem(final CommentItemPresenter.View item);

        void clearCommentList();

        void setCommentInputError(final String errorMsg);

        void clearCommentInputError();

        String getCommentText();

        void clearCommentInputField();

        void enableSummaryEditMode(final boolean isEnabled);

        void enableDescriptionEditMode(final boolean isEnabled);

        void hideEditModes();

        void showConflictWarning(final boolean isVisible);

        void resetAll();
    }

    private final View view;
    private final TranslationService ts;
    private final ManagedInstance<CommentItemPresenter> commentItemPresenterInstances;
    private final Caller<ChangeRequestService> changeRequestService;
    private final LibraryPlaces libraryPlaces;
    private final ChangeRequestUtils changeRequestUtils;
    private final SessionInfo sessionInfo;

    private WorkspaceProject workspaceProject;
    private long currentChangeRequestId;
    private String changeRequestAuthorId;

    @Inject
    public OverviewScreenPresenter(final View view,
                                   final TranslationService ts,
                                   final ManagedInstance<CommentItemPresenter> commentItemPresenterInstances,
                                   final Caller<ChangeRequestService> changeRequestService,
                                   final LibraryPlaces libraryPlaces,
                                   final ChangeRequestUtils changeRequestUtils,
                                   final SessionInfo sessionInfo) {
        this.view = view;
        this.ts = ts;
        this.commentItemPresenterInstances = commentItemPresenterInstances;
        this.changeRequestService = changeRequestService;
        this.libraryPlaces = libraryPlaces;
        this.changeRequestUtils = changeRequestUtils;
        this.sessionInfo = sessionInfo;
    }

    @PostConstruct
    public void postConstruct() {
        this.workspaceProject = libraryPlaces.getActiveWorkspace();

        this.prepareView();
    }

    public View getView() {
        return view;
    }

    public void addComment() {
        final String commentText = view.getCommentText();

        if (isInvalidContent(commentText)) {
            view.setCommentInputError(ts.getTranslation(LibraryConstants.MissingCommentText));
        } else {
            changeRequestService.call()
                    .addComment(workspaceProject.getSpace().getName(),
                                workspaceProject.getRepository().getAlias(),
                                currentChangeRequestId,
                                commentText);
        }
    }

    public void reset() {
        view.resetAll();
    }

    public void setup(final ChangeRequest changeRequest) {
        this.currentChangeRequestId = changeRequest.getId();
        this.changeRequestAuthorId = changeRequest.getAuthorId();

        this.view.setStatus(changeRequestUtils.formatStatus(changeRequest.getStatus()));
        this.view.setAuthor(changeRequest.getAuthorId());
        this.view.setCreatedDate(changeRequestUtils.formatCreatedDate(changeRequest.getCreatedDate()));
        this.view.setSummary(changeRequest.getSummary());
        this.view.setDescription(changeRequest.getDescription());
        this.view.setSourceBranch(changeRequest.getSourceBranch());
        this.view.setTargetBranch(changeRequest.getTargetBranch());
        this.view.setCommentInputPlaceHolder(ts.getTranslation(LibraryConstants.LeaveAComment));
        this.view.enableSummaryEditMode(false);
        this.view.enableDescriptionEditMode(false);
        this.view.showConflictWarning(changeRequest.isConflict()
                                              && changeRequest.getStatus() == ChangeRequestStatus.OPEN);

        this.setupComments();
        this.checkActionsForAuthor();
    }

    public void startEditSummary() {
        view.enableSummaryEditMode(true);
    }

    public void saveSummaryEdition() {
        if (isUserAuthor()) {
            final String summaryInputText = view.getSummaryInputText();

            if (!isInvalidContent(summaryInputText)) {
                changeRequestService.call(v -> {
                    view.setSummary(summaryInputText);
                    view.enableSummaryEditMode(false);
                }).updateChangeRequestSummary(workspaceProject.getSpace().getName(),
                                              workspaceProject.getRepository().getAlias(),
                                              currentChangeRequestId,
                                              summaryInputText);
            }
        }
    }

    public void cancelSummaryEdition() {
        view.enableSummaryEditMode(false);
    }

    public void startEditDescription() {
        view.enableDescriptionEditMode(true);
    }

    public void saveDescriptionEdition() {
        if (isUserAuthor()) {
            final String descriptionInputText = view.getDescriptionInputText();

            if (!isInvalidContent(descriptionInputText)) {
                changeRequestService.call(v -> {
                    view.setDescription(descriptionInputText);
                    view.enableDescriptionEditMode(false);
                }).updateChangeRequestDescription(workspaceProject.getSpace().getName(),
                                                  workspaceProject.getRepository().getAlias(),
                                                  currentChangeRequestId,
                                                  descriptionInputText);
            }
        }
    }

    public void cancelDescriptionEdition() {
        view.enableDescriptionEditMode(false);
    }

    private void checkActionsForAuthor() {
        if (!isUserAuthor()) {
            view.hideEditModes();
        }
    }

    private boolean isInvalidContent(final String content) {
        return content == null || content.trim().isEmpty();
    }

    private void prepareView() {
        this.view.init(this);
    }

    private void setupComments() {
        this.view.clearCommentList();

        changeRequestService.call((List<ChangeRequestComment> comments) -> {
            if (comments.size() > 1) {
                this.view.setCommentCounterText(ts.format(LibraryConstants.CommentsCount, comments.size()));
            } else {
                this.view.setCommentCounterText(ts.format(LibraryConstants.CommentCount, comments.size()));
            }

            comments.forEach(comment -> {
                CommentItemPresenter item = commentItemPresenterInstances.get();
                item.setup(currentChangeRequestId,
                           comment.getId(),
                           comment.getAuthorId(),
                           comment.getCreatedDate(),
                           comment.getText());
                this.view.addCommentItem(item.getView());
            });
        }).getComments(workspaceProject.getSpace().getName(),
                       workspaceProject.getRepository().getAlias(),
                       currentChangeRequestId);
    }

    private boolean isUserAuthor() {
        return this.changeRequestAuthorId.equals(this.sessionInfo.getIdentity().getIdentifier());
    }
}
