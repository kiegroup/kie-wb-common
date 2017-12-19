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

package org.kie.workbench.common.screens.library.client.settings.generalsettings;

import java.util.function.Function;

import javax.inject.Inject;

import elemental2.promise.Promise;
import org.guvnor.common.services.project.model.POM;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.screens.library.client.settings.Promises;
import org.kie.workbench.common.screens.library.client.settings.SettingsPresenter;
import org.kie.workbench.common.services.shared.validation.ValidationService;

public class GeneralSettingsPresenter implements SettingsPresenter.Section {

    public interface View extends SettingsPresenter.View.Section<GeneralSettingsPresenter> {

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

    private final View view;

    private final Caller<ValidationService> validationService;

    private POM pom;

    @Inject
    public GeneralSettingsPresenter(final View view,
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
    public Promise<Object> isValid() {
        view.hideError();
        return Promise.resolve(true)
                .then(this::validateName)
                .then(this::validateGroupId)
                .then(this::validateArtifactId)
                .then(this::validateVersion)
                .catch_(this::onValidationError);
    }

    private Promise<Boolean> validateGroupId(final Boolean b) {
        return validateStringIsNotEmpty(view.getGroupId(), view.getEmptyGroupIdMessage())
                .then(x -> executeValidation(s -> s.validateGroupId(view.getGroupId()), view.getInvalidGroupIdMessage()));
    }

    private Promise<Boolean> validateArtifactId(final Boolean b) {
        return validateStringIsNotEmpty(view.getArtifactId(), view.getEmptyArtifactIdMessage())
                .then(x -> executeValidation(s -> s.validateArtifactId(view.getArtifactId()), view.getInvalidArtifactIdMessage()));
    }

    private Promise<Boolean> validateName(final Boolean b) {
        return validateStringIsNotEmpty(view.getName(), view.getEmptyNameMessage())
                .then(x -> executeValidation(s -> s.isProjectNameValid(view.getName()), view.getInvalidNameMessage()));
    }

    private Promise<Boolean> validateVersion(final Boolean b) {
        return validateStringIsNotEmpty(view.getVersion(), view.getEmptyVersionMessage())
                .then(x -> executeValidation(s -> s.validateGAVVersion(view.getVersion()), view.getInvalidVersionMessage()));
    }

    private Promise<Object> onValidationError(final Object e) {
        view.showError((String) e);
        return Promise.reject(this);
    }

    private Promise<Boolean> validateStringIsNotEmpty(final String string, final String errorMessage) {
        return new Promise<>((resolve, reject) -> {
            if (string == null || string.isEmpty()) {
                reject.onInvoke(errorMessage);
            } else {
                resolve.onInvoke(true);
            }
        });
    }

    private Promise<Boolean> executeValidation(final Function<ValidationService, Boolean> call, final String errorMessage) {
        return Promises.promisify(validationService, call, errorMessage, isValid -> isValid);
    }

    @Override
    public void beforeSave() {
        pom.setName(view.getName());
        pom.setDescription(view.getDescription());
        pom.setUrl(view.getURL());
        pom.getGav().setGroupId(view.getGroupId());
        pom.getGav().setArtifactId(view.getArtifactId());
        pom.getGav().setVersion(view.getVersion());
    }

    @Override
    public SettingsPresenter.View.Section getView() {
        return view;
    }
}
