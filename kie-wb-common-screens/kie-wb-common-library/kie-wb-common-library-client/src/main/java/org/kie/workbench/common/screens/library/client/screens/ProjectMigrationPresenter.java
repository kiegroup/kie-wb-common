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

package org.kie.workbench.common.screens.library.client.screens;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.workbench.events.NotificationEvent;

public class ProjectMigrationPresenter {

    public interface View
            extends IsElement {

        void init(ProjectMigrationPresenter projectMigrationPresenter);
    }

    private View view;
    private Event<NotificationEvent> notificationEvent;
    private LibraryPlaces libraryPlaces;
    private Caller<LibraryService> libraryService;
    private TranslationService ts;
    private WorkspaceProject project;

    public ProjectMigrationPresenter() {
    }

    @Inject
    public ProjectMigrationPresenter(final View view,
                                     final Event<NotificationEvent> notificationEvent,
                                     final LibraryPlaces libraryPlaces,
                                     final Caller<LibraryService> libraryService,
                                     final TranslationService ts) {
        this.view = view;
        this.notificationEvent = notificationEvent;
        this.libraryPlaces = libraryPlaces;
        this.libraryService = libraryService;
        this.ts = ts;
        view.init(this);
    }

    public void show(final WorkspaceProject project) {
        this.project = PortablePreconditions.checkNotNull("ProjectListPresenter.project",
                                                          project);
    }

    public void onMigrate() {

        libraryService.call(new RemoteCallback<Object>() {
            @Override
            public void callback(Object response) {

                notificationEvent.fire(new NotificationEvent(ts.format(LibraryConstants.MigrationWasSuccessful)));

                libraryPlaces.goToLibrary();
            }
        }).migrate(project);
    }

    public IsElement getView() {
        return view;
    }
}
