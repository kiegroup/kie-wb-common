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
    @DataField("general-section")
    private HTMLAnchorElement generalSection;

    @Inject
    @DataField("dependencies-section")
    private HTMLAnchorElement dependenciesSection;

    @Inject
    @DataField("knowledge-bases-section")
    private HTMLAnchorElement knowledgeBasesSection;

    @Inject
    @DataField("external-data-objects-section")
    private HTMLAnchorElement externalDataObjectsSection;

    @Inject
    @DataField("validation-section")
    private HTMLAnchorElement validationSection;

    @Inject
    @DataField("deployments-section")
    private HTMLAnchorElement deploymentsSection;

    @Inject
    @DataField("persistence-section")
    private HTMLAnchorElement persistenceSection;

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

    @EventHandler("general-section")
    public void goToGeneralSection(final ClickEvent event) {
        presenter.goToGeneralSettingsSection();
    }

    @EventHandler("dependencies-section")
    public void goToDependenciesSection(final ClickEvent event) {
        presenter.goToDependenciesSection();
    }

    @EventHandler("knowledge-bases-section")
    public void goToKnowledgeBasesSection(final ClickEvent event) {
        presenter.goToKnowledgeBasesSection();
    }

    @EventHandler("external-data-objects-section")
    public void goToExternalDataObjectsSection(final ClickEvent event) {
        presenter.goToExternalDataObjectsSection();
    }

    @EventHandler("validation-section")
    public void goToValidationSection(final ClickEvent event) {
        presenter.goToValidationSection();
    }

    @EventHandler("deployments-section")
    public void goToDeploymentsSection(final ClickEvent event) {
        presenter.goToDeploymentsSection();
    }

    @EventHandler("persistence-section")
    public void goToPersistenceSection(final ClickEvent event) {
        presenter.goToPersistenceSection();
    }

    @Override
    public void setSection(final Section section) {
        content.innerHTML = "";
        content.appendChild(section.getElement());
        //FIXME: set active section css class active=true
    }

    @Override
    public String getSaveSuccessMessage() {
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
