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

import org.guvnor.common.services.project.model.POM;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.screens.library.client.settings.SettingsBaseSection;
import org.kie.workbench.common.screens.library.client.settings.SettingsBaseSectionView;
import org.kie.workbench.common.screens.projecteditor.util.NewProjectUtils;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.mvp.Command;

public class GeneralPresenter implements SettingsBaseSection {

    public interface View extends UberElemental<GeneralPresenter>,
                                  SettingsBaseSectionView {

        String getName();

        String getDescription();

        String getURL();

        String getGroupId();

        String getArtifactId();

        String getVersion();

        void setName(String name);

        void setDescription(String description);

        void setURL(String url);

        void setGroupId(String groupId);

        void setArtifactId(String artifactId);

        void setVersion(String version);

        void showError(String message);

        void hideError();

        String getEmptyNameMessage();

        String getInvalidNameMessage();

        String getEmptyGroupIdMessage();

        String getInvalidGroupIdMessage();

        String getEmptyArtifactIdMessage();

        String getInvalidArtifactIdMessage();

        String getEmptyVersionMessage();

        String getInvalidVersionMessage();
    }

    private View view;

    private Caller<ValidationService> validationService;

    private POM pom;

    @Inject
    public GeneralPresenter(final View view,
                            final Caller<ValidationService> validationService) {
        this.view = view;
        this.validationService = validationService;
    }

    public void setup(final POM pom) {
        view.init(this);
        this.pom = pom;

        view.setName(pom.getName());
        view.setDescription(pom.getDescription());
        view.setURL(pom.getUrl() != null ? pom.getUrl() : "");
        view.setGroupId(pom.getGav().getGroupId());
        view.setArtifactId(pom.getGav().getArtifactId());
        view.setVersion(pom.getGav().getVersion());
    }

    @Override
    public void validate(final Command successCallback,
                         final Command errorCallback) {
        view.hideError();
        validateFields(view.getName(),
                       view.getDescription(),
                       view.getURL(),
                       view.getGroupId(),
                       view.getArtifactId(),
                       view.getVersion(),
                       successCallback,
                       errorCallback);
    }

    @Override
    public void preSave() {
        pom.setName(view.getName());
        pom.setDescription(view.getDescription());
        pom.setUrl(view.getURL());
        pom.getGav().setGroupId(view.getGroupId());
        pom.getGav().setArtifactId(view.getArtifactId());
        pom.getGav().setVersion(view.getVersion());
    }

    private void validateFields(final String name,
                                final String description,
                                final String url,
                                final String groupId,
                                final String artifactId,
                                final String version,
                                final Command successCallback,
                                final Command errorCallback) {
        final Command validateVersion = () -> validateVersion(version,
                                                              successCallback,
                                                              errorCallback);
        final Command validateArtifactId = () -> validateArtifactId(artifactId,
                                                                    validateVersion,
                                                                    errorCallback);
        final Command validateGroupId = () -> validateGroupId(groupId,
                                                              validateArtifactId,
                                                              errorCallback);
        validateName(name,
                     validateGroupId,
                     errorCallback);
    }

    private void validateName(final String name,
                              final Command successCallback,
                              final Command errorCallback) {
        if (name == null || name.trim().isEmpty()) {
            view.showError(view.getEmptyNameMessage());
            if (errorCallback != null) {
                errorCallback.execute();
            }
            return;
        }

        validationService.call((Boolean isValid) -> {
            final String sanitizeProjectName = NewProjectUtils.sanitizeProjectName(name);
            if (Boolean.TRUE.equals(isValid) && !sanitizeProjectName.isEmpty()) {
                if (successCallback != null) {
                    successCallback.execute();
                }
            } else {
                view.showError(view.getInvalidNameMessage());
                if (errorCallback != null) {
                    errorCallback.execute();
                }
            }
        }).isProjectNameValid(name);
    }

    private void validateGroupId(final String groupId,
                                 final Command successCallback,
                                 final Command errorCallback) {
        if (groupId == null || groupId.trim().isEmpty()) {
            view.showError(view.getEmptyGroupIdMessage());
            if (errorCallback != null) {
                errorCallback.execute();
            }
            return;
        }

        validationService.call((Boolean isValid) -> {
            if (Boolean.TRUE.equals(isValid)) {
                if (successCallback != null) {
                    successCallback.execute();
                }
            } else {
                view.showError(view.getInvalidGroupIdMessage());
                if (errorCallback != null) {
                    errorCallback.execute();
                }
            }
        }).validateGroupId(groupId);
    }

    private void validateArtifactId(final String artifactId,
                                    final Command successCallback,
                                    final Command errorCallback) {
        if (artifactId == null || artifactId.trim().isEmpty()) {
            view.showError(view.getEmptyArtifactIdMessage());
            if (errorCallback != null) {
                errorCallback.execute();
            }
            return;
        }

        validationService.call((Boolean isValid) -> {
            if (Boolean.TRUE.equals(isValid)) {
                if (successCallback != null) {
                    successCallback.execute();
                }
            } else {
                view.showError(view.getInvalidArtifactIdMessage());
                if (errorCallback != null) {
                    errorCallback.execute();
                }
            }
        }).validateArtifactId(artifactId);
    }

    private void validateVersion(final String version,
                                 final Command successCallback,
                                 final Command errorCallback) {
        if (version == null || version.trim().isEmpty()) {
            view.showError(view.getEmptyVersionMessage());
            if (errorCallback != null) {
                errorCallback.execute();
            }
            return;
        }

        validationService.call((Boolean isValid) -> {
            if (Boolean.TRUE.equals(isValid)) {
                if (successCallback != null) {
                    successCallback.execute();
                }
            } else {
                view.showError(view.getInvalidVersionMessage());
                if (errorCallback != null) {
                    errorCallback.execute();
                }
            }
        }).validateGAVVersion(version);
    }

    public View getView() {
        return view;
    }
}
