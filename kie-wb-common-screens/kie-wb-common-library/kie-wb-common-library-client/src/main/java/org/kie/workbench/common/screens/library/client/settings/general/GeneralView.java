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

package org.kie.workbench.common.screens.library.client.settings.general;

import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLTextAreaElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;

@Templated
public class GeneralView implements GeneralPresenter.View,
                                    IsElement {

    private GeneralPresenter presenter;

    @Inject
    private TranslationService translationService;

    @Inject
    @DataField("error")
    private HTMLDivElement error;

    @Inject
    @DataField("name")
    private HTMLInputElement name;

    @Inject
    @Named("span")
    @DataField("error-message")
    private HTMLElement errorMessage;

    @Inject
    @DataField("description")
    private HTMLTextAreaElement description;

    @Inject
    @DataField("url")
    private HTMLInputElement url;

    @Inject
    @DataField("disable-gav-conflict-check")
    private HTMLInputElement disableGAVConflictCheck;

    @Inject
    @DataField("allow-child-gav-edition")
    private HTMLInputElement allowChildGAVEdition;

    @Inject
    @DataField("group-id")
    private HTMLInputElement groupId;

    @Inject
    @DataField("artifact-id")
    private HTMLInputElement artifactId;

    @Inject
    @DataField("version")
    private HTMLInputElement version;

    @Override
    public void init(final GeneralPresenter presenter) {
        this.presenter = presenter;
        hideError();
    }

    @Override
    public String getName() {
        return name.value;
    }

    @Override
    public String getDescription() {
        return description.value;
    }

    @Override
    public String getURL() {
        return url.value;
    }

    @Override
    public String getGroupId() {
        return groupId.value;
    }

    @Override
    public String getArtifactId() {
        return artifactId.value;
    }

    @Override
    public String getVersion() {
        return version.value;
    }

    @Override
    public void setName(final String name) {
        this.name.value = name;
    }

    @Override
    public void setDescription(final String description) {
        this.description.value = description;
    }

    @Override
    public void setURL(final String url) {
        this.url.value = url;
    }

    @Override
    public void setGroupId(final String groupId) {
        this.groupId.value = groupId;
    }

    @Override
    public void setArtifactId(final String artifactId) {
        this.artifactId.value = artifactId;
    }

    @Override
    public void setVersion(final String version) {
        this.version.value = version;
    }

    @Override
    public void showError(final String errorMessage) {
        this.errorMessage.innerHTML = errorMessage;
        this.error.hidden = false;
    }

    @Override
    public void hideError() {
        this.errorMessage.innerHTML = "";
        this.error.hidden = true;
    }

    @Override
    public String getEmptyNameMessage() {
        return translationService.format(LibraryConstants.EmptyFieldValidation,
                                         translationService.getTranslation(LibraryConstants.Name));
    }

    @Override
    public String getInvalidNameMessage() {
        return translationService.format(LibraryConstants.InvalidFieldValidation,
                                         translationService.getTranslation(LibraryConstants.Name));
    }

    @Override
    public String getEmptyGroupIdMessage() {
        return translationService.format(LibraryConstants.EmptyFieldValidation,
                                         translationService.getTranslation(LibraryConstants.GroupId));
    }

    @Override
    public String getInvalidGroupIdMessage() {
        return translationService.format(LibraryConstants.InvalidFieldValidation,
                                         translationService.getTranslation(LibraryConstants.GroupId));
    }

    @Override
    public String getEmptyArtifactIdMessage() {
        return translationService.format(LibraryConstants.EmptyFieldValidation,
                                         translationService.getTranslation(LibraryConstants.ArtifactId));
    }

    @Override
    public String getInvalidArtifactIdMessage() {
        return translationService.format(LibraryConstants.InvalidFieldValidation,
                                         translationService.getTranslation(LibraryConstants.ArtifactId));
    }

    @Override
    public String getEmptyVersionMessage() {
        return translationService.format(LibraryConstants.EmptyFieldValidation,
                                         translationService.getTranslation(LibraryConstants.Version));
    }

    @Override
    public String getInvalidVersionMessage() {
        return translationService.format(LibraryConstants.InvalidFieldValidation,
                                         translationService.getTranslation(LibraryConstants.Version));
    }
}
