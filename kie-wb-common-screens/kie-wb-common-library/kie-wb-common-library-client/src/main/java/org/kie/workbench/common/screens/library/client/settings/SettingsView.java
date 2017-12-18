/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;

@Templated
public class SettingsView implements SettingsPresenter.View,
                                     IsElement {

    private SettingsPresenter presenter;

    @Inject
    private TranslationService translationService;

    @Inject
    @DataField("general-tab")
    private HTMLAnchorElement generalTab;

    @Inject
    @DataField("dependencies-tab")
    private HTMLAnchorElement dependenciesTab;

    @Inject
    @DataField("knowledge-bases-tab")
    private HTMLAnchorElement knowledgeBasesTab;

    @Inject
    @DataField("external-data-objects-tab")
    private HTMLAnchorElement externalDataObjectsTab;

    @Inject
    @DataField("validation-tab")
    private HTMLAnchorElement validationTab;

    @Inject
    @DataField("deployments-tab")
    private HTMLAnchorElement deploymentsTab;

    @Inject
    @DataField("persistence-tab")
    private HTMLAnchorElement persistenceTab;

    @Inject
    @DataField("save")
    private HTMLButtonElement save;

    @Inject
    @DataField("reset")
    private HTMLButtonElement reset;

    @Inject
    @DataField("content")
    private HTMLDivElement content;

    @Override
    public void init(final SettingsPresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("save")
    public void save(final ClickEvent event) {
        presenter.save();
    }

    @EventHandler("reset")
    public void reset(final ClickEvent event) {
        presenter.reset();
    }

    @EventHandler("general-tab")
    public void goToGeneralTab(final ClickEvent event) {
        presenter.goToGeneralTab();
    }

    @EventHandler("dependencies-tab")
    public void goToDependenciesTab(final ClickEvent event) {
        presenter.goToDependenciesTab();
    }

    @EventHandler("knowledge-bases-tab")
    public void goToKnowledgeBasesTab(final ClickEvent event) {
        presenter.goToKnowledgeBasesTab();
    }

    @EventHandler("external-data-objects-tab")
    public void goToExternalDataObjectsTab(final ClickEvent event) {
        presenter.goToExternalDataObjectsTab();
    }

    @EventHandler("validation-tab")
    public void goToValidationTab(final ClickEvent event) {
        presenter.goToValidationTab();
    }

    @EventHandler("deployments-tab")
    public void goToDeploymentsTab(final ClickEvent event) {
        presenter.goToDeploymentsTab();
    }

    @EventHandler("persistence-tab")
    public void goToPersistenceTab(final ClickEvent event) {
        presenter.goToPersistenceTab();
    }

    @Override
    public void setContent(final SettingsBaseSectionView contentView) {
        content.innerHTML = "";
        content.appendChild(contentView.getElement());
    }

    @Override
    public String getSaveSuccessfulMessage() {
        return translationService.format(LibraryConstants.SettingsSaveSuccess);
    }

    @Override
    public String getSavingMessage() {
        return translationService.format(LibraryConstants.Saving);
    }

    @Override
    public void showBusyIndicator() {
        showBusyIndicator(translationService.format(LibraryConstants.Loading));
    }

    @Override
    public void showBusyIndicator(final String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }
}
