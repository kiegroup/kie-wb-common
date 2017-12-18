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

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.client.perspective.LibraryPerspective;
import org.kie.workbench.common.screens.library.client.settings.general.GeneralPresenter;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.widgets.client.callbacks.CommandWithThrowableDrivenErrorCallback;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.events.NotificationEvent;

import static org.uberfire.ext.widgets.common.client.common.ConcurrentChangePopup.newConcurrentDelete;
import static org.uberfire.ext.widgets.common.client.common.ConcurrentChangePopup.newConcurrentRename;
import static org.uberfire.ext.widgets.common.client.common.ConcurrentChangePopup.newConcurrentUpdate;

@WorkbenchScreen(identifier = "project-settings",
        owningPerspective = LibraryPerspective.class)
public class SettingsPresenter {

    public interface View extends UberElemental<SettingsPresenter>,
                                  HasBusyIndicator {

        void showBusyIndicator();

        void setContent(SettingsBaseSectionView contentView);

        String getSaveSuccessfulMessage();

        String getSavingMessage();
    }

    private View view;

    private Caller<ProjectScreenService> projectScreenService;

    private ConflictingRepositoriesPopup conflictingRepositoriesPopup;

    private Caller<ValidationService> validationService;

    private ProjectContext workbenchContext;

    private ManagedInstance<ObservablePath> observablePaths;

    private Event<NotificationEvent> notificationEvent;

    private SavePopUpPresenter savePopUpPresenter;

    private GeneralPresenter generalPresenter;

    private ProjectScreenModel model;

    private ObservablePath.OnConcurrentUpdateEvent concurrentUpdateSessionInfo = null;

    private Integer originalHash;

    private ObservablePath pathToPomXML;

    @Inject
    public SettingsPresenter(final View view,
                             final Caller<ProjectScreenService> projectScreenService,
                             final ConflictingRepositoriesPopup conflictingRepositoriesPopup,
                             final Caller<ValidationService> validationService,
                             final ProjectContext workbenchContext,
                             final ManagedInstance<ObservablePath> observablePaths,
                             final Event<NotificationEvent> notificationEvent,
                             final SavePopUpPresenter savePopUpPresenter,
                             final GeneralPresenter generalPresenter) {
        this.view = view;
        this.projectScreenService = projectScreenService;
        this.conflictingRepositoriesPopup = conflictingRepositoriesPopup;
        this.validationService = validationService;
        this.workbenchContext = workbenchContext;
        this.observablePaths = observablePaths;
        this.notificationEvent = notificationEvent;
        this.savePopUpPresenter = savePopUpPresenter;
        this.generalPresenter = generalPresenter;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
        view.showBusyIndicator();

        setupPathToPomXML();

        projectScreenService.call((ProjectScreenModel model) -> {
            concurrentUpdateSessionInfo = null;
            SettingsPresenter.this.model = model;

            generalPresenter.setup(model.getPOM());

            view.hideBusyIndicator();
            originalHash = model.hashCode();
        },
        new DefaultErrorCallback()).load(pathToPomXML);
    }

    public void save() {
        saveProject(v -> {
            view.hideBusyIndicator();
            notificationEvent.fire(new NotificationEvent(view.getSaveSuccessfulMessage(),
                                                         NotificationEvent.NotificationType.SUCCESS));
            originalHash = model.hashCode();
        },
        DeploymentMode.VALIDATED);
    }

    private void saveProject(final RemoteCallback<Void> callback,
                             final DeploymentMode mode) {
        generalPresenter.validate(() -> {
            if (concurrentUpdateSessionInfo != null) {
                newConcurrentUpdate(concurrentUpdateSessionInfo.getPath(),
                                    concurrentUpdateSessionInfo.getIdentity(),
                                    () -> save(callback,
                                               mode),
                                    () -> {},
                                    () -> reset()).show();
            } else {
                save(callback,
                     mode);
            }
        },
        () -> {});
    }

    private void save(final RemoteCallback<Void> callback,
                      final DeploymentMode mode) {
        savePopUpPresenter.show(pathToPomXML,
                                comment -> doSave(comment,
                                                  callback,
                                                  mode));
        concurrentUpdateSessionInfo = null;
    }

    private void doSave(final String comment,
                        final RemoteCallback<Void> callback,
                        final DeploymentMode mode) {
        generalPresenter.preSave();

        final Map<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable> onSaveGavExistsHandler = new HashMap<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable>() {{
            put(GAVAlreadyExistsException.class,
                parameter -> {
                    view.hideBusyIndicator();
                    conflictingRepositoriesPopup.setContent(model.getPOM().getGav(),
                                                            ((GAVAlreadyExistsException) parameter).getRepositories(),
                                                            () -> {
                                                                conflictingRepositoriesPopup.hide();
                                                                doSave(comment,
                                                                       callback,
                                                                       DeploymentMode.FORCED);
                                                            });
                    conflictingRepositoriesPopup.show();
                });
        }};

        projectScreenService.call(new RemoteCallback<Void>() {
                                      @Override
                                      public void callback(Void v) {
                                          if (callback != null) {
                                              callback.callback(v);
                                          }
                                      }
                                  },
                                  new CommandWithThrowableDrivenErrorCallback(view,
                                                                              onSaveGavExistsHandler)).save(pathToPomXML,
                                                                                                            model,
                                                                                                            comment,
                                                                                                            mode);
    }

    public void reset() {
        setup();
    }

    public void goToGeneralTab() {
        view.setContent(generalPresenter.getView());
    }

    public void goToDependenciesTab() {
        // TODO
    }

    public void goToKnowledgeBasesTab() {
        // TODO
    }

    public void goToExternalDataObjectsTab() {
        // TODO
    }

    public void goToValidationTab() {
        // TODO
    }

    public void goToDeploymentsTab() {
        // TODO
    }

    public void goToPersistenceTab() {
        // TODO
    }

    protected void setupPathToPomXML() {
        if (pathToPomXML != null) {
            pathToPomXML.dispose();
        }

        pathToPomXML = observablePaths.get().wrap(workbenchContext.getActiveProject().getPomXMLPath());

        pathToPomXML.onConcurrentUpdate(eventInfo -> concurrentUpdateSessionInfo = eventInfo);
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Settings Screen";
    }

    @WorkbenchPartView
    public View getView() {
        return view;
    }
}
