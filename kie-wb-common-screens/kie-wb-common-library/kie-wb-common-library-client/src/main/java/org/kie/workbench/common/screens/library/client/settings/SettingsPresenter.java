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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.promise.Promise;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.screens.library.client.perspective.LibraryPerspective;
import org.kie.workbench.common.screens.library.client.settings.dependencies.DependenciesPresenter;
import org.kie.workbench.common.screens.library.client.settings.deployments.DeploymentsPresenter;
import org.kie.workbench.common.screens.library.client.settings.externaldataobjects.ExternalDataObjectsPresenter;
import org.kie.workbench.common.screens.library.client.settings.generalsettings.GeneralSettingsPresenter;
import org.kie.workbench.common.screens.library.client.settings.knowledgebases.KnowledgeBasesPresenter;
import org.kie.workbench.common.screens.library.client.settings.persistence.PersistencePresenter;
import org.kie.workbench.common.screens.library.client.settings.validation.ValidationPresenter;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.widgets.client.callbacks.CommandWithThrowableDrivenErrorCallback;
import org.kie.workbench.common.widgets.client.callbacks.CommandWithThrowableDrivenErrorCallback.CommandWithThrowable;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.screens.library.client.settings.Promises.promisify;
import static org.kie.workbench.common.screens.library.client.settings.Promises.reduceLazily;
import static org.kie.workbench.common.screens.library.client.settings.Promises.reduceLazilyChaining;
import static org.kie.workbench.common.screens.library.client.settings.Promises.resolve;
import static org.uberfire.ext.widgets.common.client.common.ConcurrentChangePopup.newConcurrentUpdate;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.ERROR;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.SUCCESS;

@WorkbenchScreen(identifier = "project-settings",
        owningPerspective = LibraryPerspective.class)
public class SettingsPresenter {

    public interface View extends UberElemental<SettingsPresenter>,
                                  HasBusyIndicator {

        void showBusyIndicator();

        void setContent(final Section contentView);

        String getSaveSuccessMessage();

        String getSavingMessage();

        interface Section<T> extends UberElemental<T>,
                                     IsElement {

        }
    }

    private final View view;
    private final Event<NotificationEvent> notificationEvent;
    private final SavePopUpPresenter savePopUpPresenter;

    // Sections
    private final DependenciesPresenter dependenciesSettingsSection;
    private final DeploymentsPresenter deploymentsSettingsSection;
    private final ExternalDataObjectsPresenter externalDataObjectsSettingsSection;
    private final GeneralSettingsPresenter generalSettingsSection;
    private final KnowledgeBasesPresenter knowledgeBasesSettingsSection;
    private final PersistencePresenter persistenceSettingsSection;
    private final ValidationPresenter validationSettingsSection;

    private final Caller<ProjectScreenService> projectScreenService;
    private final ProjectContext projectContext;
    private final ManagedInstance<ObservablePath> observablePaths;
    private final ConflictingRepositoriesPopup conflictingRepositoriesPopup;

    private ObservablePath pathToPom;
    private ObservablePath.OnConcurrentUpdateEvent concurrentPomUpdateInfo = null;
    private ProjectScreenModel model;

    @Inject
    public SettingsPresenter(final View view,
                             final Event<NotificationEvent> notificationEvent,
                             final SavePopUpPresenter savePopUpPresenter,
                             final DependenciesPresenter dependenciesSettingsSection,
                             final DeploymentsPresenter deploymentsSettingsSection,
                             final ExternalDataObjectsPresenter externalDataObjectsSettingsSection,
                             final GeneralSettingsPresenter generalSettingsSection,
                             final KnowledgeBasesPresenter knowledgeBasesSettingsSection,
                             final PersistencePresenter persistenceSettingsSection,
                             final ValidationPresenter validationSettingsSection,
                             final Caller<ProjectScreenService> projectScreenService,
                             final ProjectContext projectContext,
                             final ManagedInstance<ObservablePath> observablePaths,
                             final ConflictingRepositoriesPopup conflictingRepositoriesPopup) {
        this.view = view;
        this.notificationEvent = notificationEvent;
        this.savePopUpPresenter = savePopUpPresenter;

        this.dependenciesSettingsSection = dependenciesSettingsSection;
        this.deploymentsSettingsSection = deploymentsSettingsSection;
        this.externalDataObjectsSettingsSection = externalDataObjectsSettingsSection;
        this.generalSettingsSection = generalSettingsSection;
        this.knowledgeBasesSettingsSection = knowledgeBasesSettingsSection;
        this.persistenceSettingsSection = persistenceSettingsSection;
        this.validationSettingsSection = validationSettingsSection;
        this.projectScreenService = projectScreenService;
        this.projectContext = projectContext;
        this.observablePaths = observablePaths;
        this.conflictingRepositoriesPopup = conflictingRepositoriesPopup;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
        view.showBusyIndicator();

        if (pathToPom != null) {
            pathToPom.dispose();
        }

        pathToPom = observablePaths.get().wrap(projectContext.getActiveProject().getPomXMLPath());
        pathToPom.onConcurrentUpdate(info -> concurrentPomUpdateInfo = info);

        promisify(projectScreenService, s -> s.load(pathToPom)).then(model -> {
            this.model = model;
            getSectionsInDisplayOrder().forEach(section -> section.setup(getView(), model));
            goTo(generalSettingsSection);
            view.hideBusyIndicator();
            return resolve();
        });
    }

    public void save() {

        reduceLazily(null, getSectionsInDisplayOrder(), Section::validate)
                .then(o -> {
                    savePopUpPresenter.show(comment -> executeSave(comment, DeploymentMode.VALIDATED));
                    return resolve();
                })
                .catch_(e -> Promises.handleExceptionOr(e, (final Section section) -> {
                    view.hideBusyIndicator();
                    goTo(section);
                    return resolve();
                }))
                .catch_(this::defaultErrorResolution);
    }

    private void executeSave(final String comment,
                             final DeploymentMode mode) {

        resolve()
                .then(ignore -> reduceLazily(null, getSectionsInDisplayOrder(), Section::beforeSave))
                .then(ignore -> reduceLazily(null, getSectionsInDisplayOrder(), Section::save))
                .then(ignore -> reduceLazilyChaining(null, getSavingSteps(comment, mode), this::executeSavingStep))
                .then(ignore -> {
                    view.hideBusyIndicator();
                    notificationEvent.fire(new NotificationEvent(view.getSaveSuccessMessage(), SUCCESS));
                    return resolve();
                })
                .catch_(e -> Promises.handleExceptionOr(e, (final Void ignore) -> {
                    notificationEvent.fire(new NotificationEvent("An error has occurred while saving settings", ERROR)); //FIXME: tiago: improve error message
                    return resolve();
                }))
                .catch_(this::defaultErrorResolution);
    }

    private Promise<Void> executeSavingStep(final Supplier<Promise<Void>> chain, final SavingStep savingStep) {
        return savingStep.execute(chain);
    }

    private List<SavingStep> getSavingSteps(final String comment,
                                            final DeploymentMode mode) {

        return Arrays.asList(chain -> saveProjectScreenModel(comment, mode, chain));
    }

    private Promise<Void> saveProjectScreenModel(final String comment,
                                                 final DeploymentMode mode,
                                                 final Supplier<Promise<Void>> chain) {

        return checkConcurrentPomUpdate(comment, chain)
                .then(ignore -> promisify(projectScreenService,
                                          s -> s.save(pathToPom, model, comment, mode),
                                          onSaveProjectScreenModelError(comment, chain)::error));
    }

    private Promise<Void> checkConcurrentPomUpdate(final String comment, final Supplier<Promise<Void>> chain) {
        return new Promise<>((resolve, reject) -> {
            if (this.concurrentPomUpdateInfo == null) {
                resolve.onInvoke((Void) null);
            } else {
                newConcurrentUpdate(this.concurrentPomUpdateInfo.getPath(),
                                    this.concurrentPomUpdateInfo.getIdentity(),
                                    () -> forceSave(comment, chain),
                                    () -> {
                                    },
                                    this::setup).show();
                reject.onInvoke(null);
            }
        });
    }

    private ErrorCallback<Message> onSaveProjectScreenModelError(final String comment,
                                                                 final Supplier<Promise<Void>> saveChain) {

        return new CommandWithThrowableDrivenErrorCallback(view, new HashMap<Class<? extends Throwable>, CommandWithThrowable>() {{
            put(GAVAlreadyExistsException.class,
                e -> {
                    view.hideBusyIndicator();
                    conflictingRepositoriesPopup.setContent(model.getPOM().getGav(),
                                                            ((GAVAlreadyExistsException) e).getRepositories(),
                                                            () -> forceSave(comment, saveChain));

                    conflictingRepositoriesPopup.show();
                });
        }});
    }

    private void forceSave(final String comment,
                           final Supplier<Promise<Void>> saveChain) {

        concurrentPomUpdateInfo = null;
        conflictingRepositoriesPopup.hide();
        saveProjectScreenModel(comment, DeploymentMode.FORCED, saveChain).then(ignore -> saveChain.get());
    }

    private Promise<Void> defaultErrorResolution(final Object e) {
        new DefaultErrorCallback().error(null, (Throwable) e);
        return resolve();
    }

    public void reset() {
        setup();
    }

    public void goToGeneralSettingsSection() {
        goTo(generalSettingsSection);
    }

    public void goToDependenciesSection() {
        goTo(dependenciesSettingsSection);
    }

    public void goToKnowledgeBasesSection() {
        goTo(knowledgeBasesSettingsSection);
    }

    public void goToExternalDataObjectsSection() {
        goTo(externalDataObjectsSettingsSection);
    }

    public void goToValidationSection() {
        goTo(validationSettingsSection);
    }

    public void goToDeploymentsSection() {
        goTo(deploymentsSettingsSection);
    }

    public void goToPersistenceSection() {
        goTo(persistenceSettingsSection);
    }

    private void goTo(final Section section) {
        view.setContent(section.getView());
    }

    private List<Section> getSectionsInDisplayOrder() {
        return Arrays.asList(
                generalSettingsSection,
                dependenciesSettingsSection,
                knowledgeBasesSettingsSection,
                externalDataObjectsSettingsSection,
                validationSettingsSection,
                deploymentsSettingsSection,
                persistenceSettingsSection
        );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Settings Screen";
    }

    @WorkbenchPartView
    public View getView() {
        return view;
    }

    public interface Section {

        //FIXME: remove default
        default Promise<Void> beforeSave() {
            DomGlobal.console.info("Before saving " + getClass().getSimpleName());
            return resolve();
        }

        //FIXME: remove default
        default Promise<Void> save() {
            DomGlobal.console.info("Saving " + getClass().getSimpleName());
            return resolve();
        }

        //FIXME: remove default
        default Promise<Object> validate() {
            DomGlobal.console.info("Validating " + getClass().getSimpleName());
            return Promise.resolve(true);
        }

        //FIXME: remove default
        default void setup(final HasBusyIndicator container, ProjectScreenModel model) {
            DomGlobal.console.info("Setting up " + getClass().getSimpleName());
        }

        View.Section getView();
    }

    @FunctionalInterface
    private interface SavingStep {

        Promise<Void> execute(final Supplier<Promise<Void>> chain);
    }
}
