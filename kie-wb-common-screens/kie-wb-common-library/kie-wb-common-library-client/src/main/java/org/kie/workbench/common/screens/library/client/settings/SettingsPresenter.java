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
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

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
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.events.NotificationEvent.NotificationType;

import static org.uberfire.ext.widgets.common.client.common.ConcurrentChangePopup.newConcurrentUpdate;

@WorkbenchScreen(identifier = "project-settings",
        owningPerspective = LibraryPerspective.class)
public class SettingsPresenter {

    public interface View extends UberElemental<SettingsPresenter>,
                                  HasBusyIndicator {

        void showBusyIndicator();

        void setContent(final Section contentView);

        String getSaveSuccessfulMessage();

        String getSavingMessage();

        interface Section<T> extends UberElemental<T>,
                                     IsElement {

        }
    }

    private final View view;

    private final Caller<ProjectScreenService> projectScreenService;

    private final ConflictingRepositoriesPopup conflictingRepositoriesPopup;

    private final ProjectContext workbenchContext;

    private final ManagedInstance<ObservablePath> observablePaths;

    private final Event<NotificationEvent> notificationEvent;

    private final SavePopUpPresenter savePopUpPresenter;

    private ProjectScreenModel model;

    private ObservablePath.OnConcurrentUpdateEvent concurrentUpdateSessionInfo = null;

    private Integer originalHash;

    private ObservablePath pathToPomXml;

    // Sections
    private final DependenciesPresenter dependenciesSettingsSectionPresenter;
    private final DeploymentsPresenter deploymentsSettingsSectionPresenter;
    private final ExternalDataObjectsPresenter externalDataObjectsSettingsSectionPresenter;
    private final GeneralSettingsPresenter generalSettingsSectionPresenter;
    private final KnowledgeBasesPresenter knowledgeBasesSettingsSectionPresenter;
    private final PersistencePresenter persistenceSettingsSectionPresenter;
    private final ValidationPresenter validationSettingsSectionPresenter;

    @Inject
    public SettingsPresenter(final View view,
                             final Caller<ProjectScreenService> projectScreenService,
                             final ConflictingRepositoriesPopup conflictingRepositoriesPopup,
                             final ProjectContext workbenchContext,
                             final ManagedInstance<ObservablePath> observablePaths,
                             final Event<NotificationEvent> notificationEvent,
                             final SavePopUpPresenter savePopUpPresenter,
                             final DependenciesPresenter dependenciesSettingsSectionPresenter,
                             final DeploymentsPresenter deploymentsSettingsSectionPresenter,
                             final ExternalDataObjectsPresenter externalDataObjectsSettingsSectionPresenter,
                             final GeneralSettingsPresenter generalSettingsSectionPresenter,
                             final KnowledgeBasesPresenter knowledgeBasesSettingsSectionPresenter,
                             final PersistencePresenter persistenceSettingsSectionPresenter,
                             final ValidationPresenter validationSettingsSectionPresenter) {
        this.view = view;
        this.projectScreenService = projectScreenService;
        this.conflictingRepositoriesPopup = conflictingRepositoriesPopup;
        this.workbenchContext = workbenchContext;
        this.observablePaths = observablePaths;
        this.notificationEvent = notificationEvent;
        this.savePopUpPresenter = savePopUpPresenter;

        this.dependenciesSettingsSectionPresenter = dependenciesSettingsSectionPresenter;
        this.deploymentsSettingsSectionPresenter = deploymentsSettingsSectionPresenter;
        this.externalDataObjectsSettingsSectionPresenter = externalDataObjectsSettingsSectionPresenter;
        this.generalSettingsSectionPresenter = generalSettingsSectionPresenter;
        this.knowledgeBasesSettingsSectionPresenter = knowledgeBasesSettingsSectionPresenter;
        this.persistenceSettingsSectionPresenter = persistenceSettingsSectionPresenter;
        this.validationSettingsSectionPresenter = validationSettingsSectionPresenter;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
        view.showBusyIndicator();

        if (pathToPomXml != null) {
            pathToPomXml.dispose();
        }

        pathToPomXml = observablePaths.get().wrap(workbenchContext.getActiveProject().getPomXMLPath());
        pathToPomXml.onConcurrentUpdate(eventInfo -> concurrentUpdateSessionInfo = eventInfo);

        projectScreenService
                .call(this::onProjectScreenModelLoadSuccess, new DefaultErrorCallback())
                .load(pathToPomXml);
    }

    private void onProjectScreenModelLoadSuccess(final ProjectScreenModel projectScreenModel) {
        concurrentUpdateSessionInfo = null;
        model = projectScreenModel;
        generalSettingsSectionPresenter.setup(projectScreenModel.getPOM());
        view.hideBusyIndicator();
        originalHash = projectScreenModel.hashCode();
    }

    public void save() {
        for (final Section section : getSectionsInDisplayOrder()) {
            if (!section.isValid()) {
                goTo(section);
                break;
            }
        }

        if (concurrentUpdateSessionInfo != null) {
            newConcurrentUpdate(concurrentUpdateSessionInfo.getPath(),
                                concurrentUpdateSessionInfo.getIdentity(),
                                this::showSavePopup,
                                this::noOp,
                                this::reset).show();
        } else {
            showSavePopup();
            concurrentUpdateSessionInfo = null;
        }
    }

    private void showSavePopup() {
        savePopUpPresenter.show(pathToPomXml, comment -> executeSave(comment, DeploymentMode.VALIDATED));
    }

    private void executeSave(final String comment, final DeploymentMode mode) {
        getSectionsInDisplayOrder().forEach(Section::beforeSave);
        projectScreenService.call(this::onSaveSuccess, onSaveError(comment)).save(pathToPomXml, model, comment, mode);
    }

    private void onSaveSuccess(final Void v) {
        view.hideBusyIndicator();
        notificationEvent.fire(new NotificationEvent(view.getSaveSuccessfulMessage(), NotificationType.SUCCESS));
        originalHash = model.hashCode();
    }

    private ErrorCallback<Message> onSaveError(final String comment) {
        final Map<Class<? extends Throwable>, CommandWithThrowable> callbacksByExceptionTypes = new HashMap<>();
        callbacksByExceptionTypes.put(GAVAlreadyExistsException.class, e -> onGAVAlreadyExistsException(comment, (GAVAlreadyExistsException) e));
        return new CommandWithThrowableDrivenErrorCallback(view, callbacksByExceptionTypes);
    }

    private void onGAVAlreadyExistsException(final String comment, final GAVAlreadyExistsException e) {
        view.hideBusyIndicator();
        conflictingRepositoriesPopup.setContent(
                model.getPOM().getGav(),
                e.getRepositories(),
                () -> {
                    conflictingRepositoriesPopup.hide();
                    executeSave(comment, DeploymentMode.FORCED);
                });
        conflictingRepositoriesPopup.show();
    }

    public void reset() {
        setup();
    }

    public void goToGeneralSettingsSection() {
        goTo(generalSettingsSectionPresenter);
    }

    public void goToDependenciesSection() {
        goTo(dependenciesSettingsSectionPresenter);
    }

    public void goToKnowledgeBasesSection() {
        goTo(knowledgeBasesSettingsSectionPresenter);
    }

    public void goToExternalDataObjectsSection() {
        goTo(externalDataObjectsSettingsSectionPresenter);
    }

    public void goToValidationSection() {
        goTo(validationSettingsSectionPresenter);
    }

    public void goToDeploymentsSection() {
        goTo(deploymentsSettingsSectionPresenter);
    }

    public void goToPersistenceSection() {
        goTo(persistenceSettingsSectionPresenter);
    }

    private void goTo(final Section section) {
        view.setContent(section.getView());
    }

    private List<Section> getSectionsInDisplayOrder() {
        return Arrays.asList(
                generalSettingsSectionPresenter,
                dependenciesSettingsSectionPresenter,
                knowledgeBasesSettingsSectionPresenter,
                externalDataObjectsSettingsSectionPresenter,
                validationSettingsSectionPresenter,
                deploymentsSettingsSectionPresenter,
                persistenceSettingsSectionPresenter
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

    private void noOp() {
    }

    public interface Section {

        void validate(final Command successCallback, final Command errorCallback);

        void beforeSave();

        View.Section getView();

        default boolean isValid() {
            return true;
        }
    }
}
