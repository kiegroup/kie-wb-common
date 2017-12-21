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

import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.inject.Inject;

import elemental2.promise.Promise;
import org.guvnor.common.services.project.client.preferences.ProjectScopedResolutionStrategySupplier;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.preferences.GAVPreferences;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.client.settings.Promises;
import org.kie.workbench.common.screens.library.client.settings.SectionSaveError;
import org.kie.workbench.common.screens.library.client.settings.SettingsPresenter;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.widgets.client.callbacks.CommandWithThrowableDrivenErrorCallback;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

import static org.kie.workbench.common.widgets.client.callbacks.CommandWithThrowableDrivenErrorCallback.CommandWithThrowable;
import static org.uberfire.ext.widgets.common.client.common.ConcurrentChangePopup.newConcurrentUpdate;

public class GeneralSettingsPresenter implements SettingsPresenter.Section {

    public interface View extends SettingsPresenter.View.Section<GeneralSettingsPresenter> {

        String getName();

        String getDescription();

        String getURL();

        String getGroupId();

        String getArtifactId();

        String getVersion();

        Boolean getConflictingGAVCheckDisabled();

        Boolean getChildGavEditEnabled();

        void setName(String name);

        void setDescription(String description);

        void setURL(String url);

        void setGroupId(String groupId);

        void setArtifactId(String artifactId);

        void setVersion(String version);

        void showError(String message);

        void setConflictingGAVCheckDisabled(boolean value);

        void setChildGavEditEnabled(boolean value);

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
    private final Caller<ProjectScreenService> projectScreenService;
    private final ConflictingRepositoriesPopup conflictingRepositoriesPopup;
    private final ProjectContext workbenchContext;
    private final ManagedInstance<ObservablePath> observablePaths;
    private final Caller<ValidationService> validationService;
    private final GAVPreferences gavPreferences;
    private final ProjectScopedResolutionStrategySupplier projectScopedResolutionStrategySupplier;

    private HasBusyIndicator container;
    private ProjectScreenModel model;
    private Integer originalHash;
    private ObservablePath pathToPomXml;
    private ObservablePath.OnConcurrentUpdateEvent concurrentUpdateSessionInfo = null;

    @Inject
    public GeneralSettingsPresenter(final View view,
                                    final Caller<ProjectScreenService> projectScreenService,
                                    final ConflictingRepositoriesPopup conflictingRepositoriesPopup,
                                    final ProjectContext workbenchContext,
                                    final ManagedInstance<ObservablePath> observablePaths,
                                    final Caller<ValidationService> validationService,
                                    final GAVPreferences gavPreferences,
                                    final ProjectScopedResolutionStrategySupplier projectScopedResolutionStrategySupplier) {
        this.view = view;
        this.projectScreenService = projectScreenService;
        this.conflictingRepositoriesPopup = conflictingRepositoriesPopup;
        this.workbenchContext = workbenchContext;
        this.observablePaths = observablePaths;
        this.validationService = validationService;
        this.gavPreferences = gavPreferences;
        this.projectScopedResolutionStrategySupplier = projectScopedResolutionStrategySupplier;
    }

    // Save

    @Override
    public void setup(final HasBusyIndicator container) {
        view.init(this);
        this.container = container;

        if (pathToPomXml != null) {
            pathToPomXml.dispose();
        }

        pathToPomXml = observablePaths.get().wrap(workbenchContext.getActiveProject().getPomXMLPath());
        pathToPomXml.onConcurrentUpdate(eventInfo -> concurrentUpdateSessionInfo = eventInfo);

        loadPom().then(this::onProjectScreenModelLoadSuccess);

        gavPreferences.load(projectScopedResolutionStrategySupplier.get(),
                            this::onGavPreferencesLoadSuccess,
                            this::onGavPreferencesLoadError);
    }

    private Promise<ProjectScreenModel> loadPom() {
        return Promises.promisify(projectScreenService, s -> s.load(pathToPomXml));
    }

    private Promise<Object> onProjectScreenModelLoadSuccess(final ProjectScreenModel projectScreenModel) {
        concurrentUpdateSessionInfo = null;
        model = projectScreenModel;

        final POM pom = model.getPOM();
        view.setName(pom.getName());
        view.setDescription(pom.getDescription());
        view.setURL(pom.getUrl() != null ? pom.getUrl() : "");
        view.setGroupId(pom.getGav().getGroupId());
        view.setArtifactId(pom.getGav().getArtifactId());
        view.setVersion(pom.getGav().getVersion());

        container.hideBusyIndicator();

        originalHash = projectScreenModel.hashCode();
        return Promises.resolve();
    }

    private void onGavPreferencesLoadSuccess(final GAVPreferences gavPreferences) {
        view.setConflictingGAVCheckDisabled(gavPreferences.isConflictingGAVCheckDisabled());
        view.setChildGavEditEnabled(gavPreferences.isChildGAVEditEnabled());
        container.hideBusyIndicator();
    }

    private void onGavPreferencesLoadError(final Throwable throwable) {
        new DefaultErrorCallback().error(null, throwable);
    }

    // Validate

    @Override
    public Promise<Object> validate() {
        view.hideError();
        return Promises.<Boolean>resolve()
                .then(this::validateName)
                .then(this::validateGroupId)
                .then(this::validateArtifactId)
                .then(this::validateVersion)
                .catch_(this::onValidationError);
    }

    private Promise<Boolean> validateGroupId(final Boolean ignore) {
        return validateStringIsNotEmpty(view.getGroupId(), view.getEmptyGroupIdMessage())
                .then(x -> executeValidation(s -> s.validateGroupId(view.getGroupId()), view.getInvalidGroupIdMessage()));
    }

    private Promise<Boolean> validateArtifactId(final Boolean ignore) {
        return validateStringIsNotEmpty(view.getArtifactId(), view.getEmptyArtifactIdMessage())
                .then(x -> executeValidation(s -> s.validateArtifactId(view.getArtifactId()), view.getInvalidArtifactIdMessage()));
    }

    private Promise<Boolean> validateName(final Boolean ignore) {
        return validateStringIsNotEmpty(view.getName(), view.getEmptyNameMessage())
                .then(x -> executeValidation(s -> s.isProjectNameValid(view.getName()), view.getInvalidNameMessage()));
    }

    private Promise<Boolean> validateVersion(final Boolean ignore) {
        return validateStringIsNotEmpty(view.getVersion(), view.getEmptyVersionMessage())
                .then(x -> executeValidation(s -> s.validateGAVVersion(view.getVersion()), view.getInvalidVersionMessage()));
    }

    private Promise<Object> onValidationError(final Object e) {
        view.showError((String) e);
        return Promise.reject(this);
    }

    private Promise<Boolean> validateStringIsNotEmpty(final String string,
                                                      final String errorMessage) {

        return new Promise<>((resolve, reject) -> {
            if (string == null || string.isEmpty()) {
                reject.onInvoke(errorMessage);
            } else {
                resolve.onInvoke(true);
            }
        });
    }

    private Promise<Boolean> executeValidation(final Function<ValidationService, Boolean> call,
                                               final String errorMessage) {

        return Promises.promisify(validationService, call, (m, t) -> {
        }, errorMessage, isValid -> isValid);
    }

    // Save

    @Override
    public Promise<Object> save(final String comment,
                                final DeploymentMode mode,
                                final Supplier<Promise<Object>> saveChain) {

        final POM pom = this.model.getPOM();
        pom.setName(view.getName());
        pom.setDescription(view.getDescription());
        pom.setUrl(view.getURL());
        pom.getGav().setGroupId(view.getGroupId());
        pom.getGav().setArtifactId(view.getArtifactId());
        pom.getGav().setVersion(view.getVersion());

        return Promises.resolve()
                .then(ignore -> checkConcurrentUpdate(comment, saveChain))
                .then(ignore -> saveModel(comment, mode, saveChain))
                .then(ignore -> updateModelHashCode())
                .then(ignore -> saveGavPreferences(comment));
    }

    private Promise<Object> saveGavPreferences(final String comment) {

        gavPreferences.setConflictingGAVCheckDisabled(view.getConflictingGAVCheckDisabled());
        gavPreferences.setChildGAVEditEnabled(view.getChildGavEditEnabled());

        return new Promise<>((resolve, reject) -> {
            gavPreferences.save(projectScopedResolutionStrategySupplier.get(),
                                () -> resolve.onInvoke(Promises.resolve()),
                                (throwable) -> reject.onInvoke(newSectionSaveError(comment)));
        });
    }

    private Promise<Object> updateModelHashCode() {
        originalHash = model.hashCode();
        return Promises.resolve();
    }

    private Promise<Void> saveModel(final String comment,
                                    final DeploymentMode mode,
                                    final Supplier<Promise<Object>> saveChain) {

        return Promises.promisify(projectScreenService,
                                  s -> s.save(pathToPomXml, model, comment, mode),
                                  onSaveModelError(comment, saveChain)::error,
                                  newSectionSaveError(comment)
        );
    }

    private ErrorCallback<Message> onSaveModelError(final String comment,
                                                    final Supplier<Promise<Object>> saveChain) {

        return new CommandWithThrowableDrivenErrorCallback(container, new HashMap<Class<? extends Throwable>, CommandWithThrowable>() {{
            put(GAVAlreadyExistsException.class,
                e -> {
                    container.hideBusyIndicator();
                    conflictingRepositoriesPopup.setContent(model.getPOM().getGav(),
                                                            ((GAVAlreadyExistsException) e).getRepositories(),
                                                            () -> forceSave(comment, saveChain));

                    conflictingRepositoriesPopup.show();
                });
        }});
    }

    private Promise<Object> checkConcurrentUpdate(final String comment,
                                                  final Supplier<Promise<Object>> saveChain) {

        if (concurrentUpdateSessionInfo == null) {
            return Promises.resolve();
        }

        newConcurrentUpdate(concurrentUpdateSessionInfo.getPath(),
                            concurrentUpdateSessionInfo.getIdentity(),
                            () -> forceSave(comment, saveChain),
                            () -> {
                            },
                            () -> this.setup(container)).show();

        return Promise.reject(newSectionSaveError(comment));
    }

    private void forceSave(final String comment,
                           final Supplier<Promise<Object>> saveChain) {

        concurrentUpdateSessionInfo = null;
        conflictingRepositoriesPopup.hide();
        save(comment, DeploymentMode.FORCED, saveChain).then(ignore -> saveChain.get());
    }

    private SectionSaveError newSectionSaveError(final String comment) {
        return new SectionSaveError(comment, this);
    }

    @Override
    public SettingsPresenter.View.Section getView() {
        return view;
    }
}
