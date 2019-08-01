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
import java.util.function.Consumer;

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

        void setCommentsHeader(final String header);

        void setCommentCurrentPage(final int page);

        void setCommentPageIndicator(final String pageIndicatorText);

        void setCommentTotalPages(final int total);

        void enableCommentPreviousButton(final boolean isEnabled);

        void enableCommentNextButton(final boolean isEnabled);

        void showCommentsToolbar(final boolean isVisible);
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

    private int commentCurrentPage;
    private int commentTotalPages;

    private static final int COMMENTS_PAGE_SIZE = 10;

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

        this.commentCurrentPage = 1;
    }

    public void setup(final ChangeRequest changeRequest,
                      final Consumer<Boolean> finishLoading) {
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

        this.updateComments(finishLoading);
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

    public void nextCommentPage() {
        if (this.commentCurrentPage + 1 <= this.commentTotalPages) {
            this.commentCurrentPage++;
            this.updateComments(b -> {
            });
        }
    }

    public void prevCommentPage() {
        if (this.commentCurrentPage - 1 >= 1) {
            this.commentCurrentPage--;
            this.updateComments(b -> {
            });
        }
    }

    public void setCommentCurrentPage(int currentCommentPage) {
        if (currentCommentPage <= commentTotalPages && currentCommentPage > 0) {
            this.commentCurrentPage = currentCommentPage;
            updateComments(b -> {
            });
        } else {
            this.view.setCommentCurrentPage(this.commentCurrentPage);
        }
    }

    private void updateComments(final Consumer<Boolean> finishLoading) {
        this.changeRequestService.call((Integer count) -> {
            setupCommentCounters(count);

            setupComments(finishLoading);
        }).countChangeRequestComments(workspaceProject.getSpace().getName(),
                                      workspaceProject.getRepository().getAlias(),
                                      currentChangeRequestId);
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

    private void setupComments(final Consumer<Boolean> finishLoading) {
        this.view.clearCommentList();

        changeRequestService.call((List<ChangeRequestComment> comments) -> {
            final boolean isEmpty = comments.isEmpty();

            this.view.setCommentsHeader(isEmpty ?
                                                ts.getTranslation(LibraryConstants.NoComments) :
                                                ts.getTranslation(LibraryConstants.Comments));

            this.view.showCommentsToolbar(!isEmpty);

            comments.stream().forEach(comment -> {
                CommentItemPresenter item = commentItemPresenterInstances.get();
                item.setup(currentChangeRequestId,
                           comment.getId(),
                           comment.getAuthorId(),
                           comment.getCreatedDate(),
                           comment.getText());
                this.view.addCommentItem(item.getView());
            });

            finishLoading.accept(true);
        }).getComments(workspaceProject.getSpace().getName(),
                       workspaceProject.getRepository().getAlias(),
                       currentChangeRequestId,
                       Math.max(0, commentCurrentPage - 1),
                       COMMENTS_PAGE_SIZE);
    }

    private boolean isUserAuthor() {
        return this.changeRequestAuthorId.equals(this.sessionInfo.getIdentity().getIdentifier());
    }

    private void setupCommentCounters(int count) {
        int offset = (this.commentCurrentPage - 1) * COMMENTS_PAGE_SIZE;

        final int fromCount = count > 0 ? offset + 1 : offset;
        final int toCount = this.resolveCommentCounter(count,
                                                       offset + COMMENTS_PAGE_SIZE);
        final int totalCount = this.resolveCommentCounter(count,
                                                          0);
        final String pageIndicatorText = fromCount + "-" + toCount + " " +
                ts.getTranslation(LibraryConstants.Of) + " " + totalCount;
        this.view.setCommentPageIndicator(pageIndicatorText);

        this.commentTotalPages = (int) Math.ceil(count / (float) COMMENTS_PAGE_SIZE);
        this.view.setCommentTotalPages(Math.max(this.commentTotalPages, 1));

        this.view.setCommentCurrentPage(this.commentCurrentPage);
        this.checkCommentPaginationButtons();
    }

    private void checkCommentPaginationButtons() {
        boolean isPreviousButtonEnabled = this.commentCurrentPage > 1;
        boolean isNextButtonEnabled = this.commentCurrentPage < this.commentTotalPages;

        this.view.enableCommentPreviousButton(isPreviousButtonEnabled);
        this.view.enableCommentNextButton(isNextButtonEnabled);
    }

    private int resolveCommentCounter(int numberOfComments,
                                      int otherCounter) {
        if (numberOfComments < otherCounter || otherCounter == 0) {
            return numberOfComments;
        } else {
            return otherCounter;
        }
    }
}
