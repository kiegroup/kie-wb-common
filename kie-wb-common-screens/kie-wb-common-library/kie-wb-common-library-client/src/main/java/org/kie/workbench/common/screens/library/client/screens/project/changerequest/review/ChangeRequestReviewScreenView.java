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

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLLIElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class ChangeRequestReviewScreenView implements ChangeRequestReviewScreenPresenter.View,
                                                      IsElement {

    private static final String ACTIVE = "active";

    private ChangeRequestReviewScreenPresenter presenter;

    @Inject
    @DataField("title")
    private HTMLDivElement title;

    @Inject
    @DataField("overview-tab-link")
    private HTMLAnchorElement overviewTabLink;

    @Inject
    @DataField("overview-tab")
    private HTMLLIElement overviewTabItem;

    @Inject
    @DataField("changed-files-tab-link")
    private HTMLAnchorElement changedFilesTabLink;

    @Inject
    @DataField("changed-files-tab")
    private HTMLLIElement changedFilesTabItem;

    @Inject
    @Named("span")
    @DataField("changed-files-count-badge")
    private HTMLElement changedFilesCountBadge;

    @Inject
    @DataField("cancel-button")
    private HTMLButtonElement cancelButton;

    @Inject
    @DataField("reject-button")
    private HTMLButtonElement rejectButton;

    @Inject
    @DataField("accept-button")
    private HTMLButtonElement acceptButton;

    @Inject
    @DataField("revert-button")
    private HTMLButtonElement revertButton;

    @Inject
    @DataField("main-container")
    private HTMLDivElement mainContainer;

    @Inject
    private Elemental2DomUtil domUtil;

    @Override
    public void init(ChangeRequestReviewScreenPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setTitle(final String title) {
        this.title.textContent = title;
    }

    @Override
    public void setChangedFilesCount(final int count) {
        changedFilesCountBadge.textContent = String.valueOf(count);
    }

    @Override
    public void setContent(final HTMLElement content) {
        this.domUtil.removeAllElementChildren(this.mainContainer);
        this.mainContainer.appendChild(content);
    }

    @Override
    public void showRejectButton(final boolean isVisible) {
        this.rejectButton.hidden = !isVisible;
    }

    @Override
    public void showAcceptButton(final boolean isVisible) {
        this.acceptButton.hidden = !isVisible;
    }

    @Override
    public void enableAcceptButton(boolean isEnabled) {
        this.acceptButton.disabled = !isEnabled;
    }

    @Override
    public void showRevertButton(final boolean isVisible) {
        this.revertButton.hidden = !isVisible;
    }

    @Override
    public void activateOverviewTab() {
        this.deactivateAllTabs();
        this.activate(this.overviewTabItem);
    }

    @Override
    public void activateChangedFilesTab() {
        this.deactivateAllTabs();
        this.activate(this.changedFilesTabItem);
    }

    @EventHandler("overview-tab-link")
    public void onOverviewTabLinkClicked(final ClickEvent event) {
        this.activateOverviewTab();
        this.presenter.showOverviewContent();
    }

    @EventHandler("changed-files-tab-link")
    public void onChangedFilesTabLinkClicked(final ClickEvent event) {
        this.activateChangedFilesTab();
        this.presenter.showChangedFilesContent();
    }

    @EventHandler("cancel-button")
    public void onCancelClicked(final ClickEvent event) {
        presenter.cancel();
    }

    @EventHandler("reject-button")
    public void onRejectClicked(final ClickEvent event) {
        presenter.reject();
    }

    @EventHandler("accept-button")
    public void onAcceptClicked(final ClickEvent event) {
        presenter.accept();
    }

    @EventHandler("revert-button")
    public void onRevertClicked(final ClickEvent event) {
        presenter.revert();
    }

    private void activate(HTMLLIElement element) {
        element.classList.add(ACTIVE);
    }

    private void deactivate(HTMLLIElement element) {
        element.classList.remove(ACTIVE);
    }

    private void deactivateAllTabs() {
        this.deactivate(this.overviewTabItem);
        this.deactivate(this.changedFilesTabItem);
    }
}