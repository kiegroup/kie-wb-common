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

package org.kie.workbench.common.screens.library.client.widgets.project;

import java.util.function.Supplier;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.projecteditor.client.build.BuildExecutor;
import org.kie.workbench.common.screens.projecteditor.client.validation.ProjectNameValidator;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.promise.Promises;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.workbench.events.NotificationEvent;

public class ProjectActionsWidget {

    public interface View extends UberElement<ProjectActionsWidget>,
                                  BuildExecutor.View {

    }

    private View view;

    private BuildExecutor buildExecutor;

    private LibraryPlaces libraryPlaces;

    private ProjectContext projectContext;

    private ProjectController projectController;

    private Supplier<Path> pomXmlPathSupplier;

    private final Promises promises;
    private final Event<NotificationEvent> notificationEvent;
    private final Caller<ProjectScreenService> projectScreenService;
    private final TranslationService translationService;
    private final CopyPopUpPresenter copyPopUpPresenter;
    private final DeletePopUpPresenter deletePopUpPresenter;
    private final ProjectNameValidator projectNameValidator;

    @Inject
    public ProjectActionsWidget(final View view,
                                final BuildExecutor buildExecutor,
                                final LibraryPlaces libraryPlaces,
                                final ProjectContext projectContext,
                                final ProjectController projectController,
                                final Promises promises,
                                final Event<NotificationEvent> notificationEvent,
                                final Caller<ProjectScreenService> projectScreenService,
                                final TranslationService translationService,
                                final CopyPopUpPresenter copyPopUpPresenter,
                                final DeletePopUpPresenter deletePopUpPresenter,
                                final ProjectNameValidator projectNameValidator) {
        this.view = view;
        this.buildExecutor = buildExecutor;
        this.libraryPlaces = libraryPlaces;
        this.projectContext = projectContext;
        this.projectController = projectController;
        this.promises = promises;
        this.notificationEvent = notificationEvent;
        this.projectScreenService = projectScreenService;
        this.translationService = translationService;
        this.copyPopUpPresenter = copyPopUpPresenter;
        this.deletePopUpPresenter = deletePopUpPresenter;
        this.projectNameValidator = projectNameValidator;
    }

    public void init(final Supplier<Path> pomXmlPathSupplier) {

        view.init(this);
        buildExecutor.init(view);
        this.pomXmlPathSupplier = pomXmlPathSupplier;
    }

    public void goToProjectSettings() {
        libraryPlaces.goToSettings();
    }

    public void goToPreferences() {
        libraryPlaces.goToPreferences();
    }

    public void compileProject() {
        if (userCanBuildProject()) {
            buildExecutor.triggerBuild();
        }
    }

    public void buildAndDeployProject() {
        if (userCanBuildProject()) {
            buildExecutor.triggerBuildAndDeploy();
        }
    }

    public void goToMessages() {
        libraryPlaces.goToMessages();
    }

    public void copy() {

        copyPopUpPresenter.show(
                pomXmlPathSupplier.get(),
                projectNameValidator,
                details -> {

                    copyPopUpPresenter.getView().hide();

                    view.showBusyIndicator(translationService.getTranslation(LibraryConstants.Loading));

                    promises.promisify(projectScreenService, s -> {
                        s.copy(pomXmlPathSupplier.get(), details.getNewFileName(), details.getCommitMessage());
                    }).then(i -> {
                        view.hideBusyIndicator();
                        notificationEvent.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemCopiedSuccessfully()));
                        return promises.resolve();
                    }).catch_(this::onError);
                });
    }

    public void delete() {

        deletePopUpPresenter.show(commitMessage -> {

            view.showBusyIndicator(translationService.getTranslation(LibraryConstants.Loading));

            promises.promisify(projectScreenService, s -> {
                s.delete(pomXmlPathSupplier.get(), commitMessage);
            }).then(i -> {
                view.hideBusyIndicator();
                return promises.resolve();
            }).catch_(this::onError);
        });
    }

    public void reimport() {

        view.showBusyIndicator(translationService.getTranslation(LibraryConstants.Loading));

        promises.promisify(projectScreenService, s -> {
            s.reImport(pomXmlPathSupplier.get());
        }).then(i -> {
            view.hideBusyIndicator();
            notificationEvent.fire(new NotificationEvent(CommonConstants.INSTANCE.ReimportSuccessful()));
            return promises.resolve();
        }).catch_(this::onError);
    }

    private Promise<Object> onError(final Object object) {
        return promises.catchOrExecute(
                object,
                e -> {
                    new HasBusyIndicatorDefaultErrorCallback(view).error(null, e);
                    return promises.resolve();
                }, (final Promises.Error<Message> e) -> {
                    new HasBusyIndicatorDefaultErrorCallback(view).error(e.getObject(), e.getThrowable());
                    return promises.resolve();
                }
        );
    }

    public boolean userCanBuildProject() {
        final Project activeProject = projectContext.getActiveProject();
        return projectController.canBuildProjects() && projectController.canBuildProject(activeProject);
    }

    public View getView() {
        return view;
    }
}
