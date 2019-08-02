/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.list;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.changerequest.ChangeRequestListUpdatedEvent;
import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;

@Dependent
public class ChangeRequestListPresenter {

    private final View view;
    private final LibraryPlaces libraryPlaces;
    private final EmptyChangeRequestListPresenter emptyChangeRequestsScreen;
    private final PopulatedChangeRequestListPresenter populatedChangeRequestsScreen;
    private final TranslationService ts;
    private final BusyIndicatorView busyIndicatorView;
    private final Caller<ChangeRequestService> changeRequestService;
    private boolean empty;
    private WorkspaceProject workspaceProject;

    @Inject
    public ChangeRequestListPresenter(final View view,
                                      final LibraryPlaces libraryPlaces,
                                      final EmptyChangeRequestListPresenter emptyChangeRequestsScreen,
                                      final PopulatedChangeRequestListPresenter populatedChangeRequestsScreen,
                                      final TranslationService ts,
                                      final BusyIndicatorView busyIndicatorView,
                                      final Caller<ChangeRequestService> changeRequestService) {
        this.view = view;
        this.libraryPlaces = libraryPlaces;
        this.emptyChangeRequestsScreen = emptyChangeRequestsScreen;
        this.populatedChangeRequestsScreen = populatedChangeRequestsScreen;
        this.ts = ts;
        this.busyIndicatorView = busyIndicatorView;
        this.changeRequestService = changeRequestService;
    }

    @PostConstruct
    public void postConstruct() {
        this.workspaceProject = libraryPlaces.getActiveWorkspace();

        this.prepareView();
    }

    public View getView() {
        return view;
    }

    public void onChangeRequestListUpdated(@Observes final ChangeRequestListUpdatedEvent event) {
        if (workspaceProject != null && this.empty &&
                event.getRepositoryId().equals(workspaceProject.getRepository().getIdentifier())) {
            this.setupList();
        }
    }

    private void prepareView() {
        this.view.init(this);
        this.empty = true;
        this.setupList();
    }

    private void setupList() {
        busyIndicatorView.showBusyIndicator(ts.getTranslation(LibraryConstants.Loading));

        changeRequestService.call((Integer count) -> {
                                      this.empty = count == 0;
                                      final HTMLElement element = (empty) ?
                                              emptyChangeRequestsScreen.getView().getElement() :
                                              populatedChangeRequestsScreen.getView().getElement();
                                      ensureContentSet(element);
                                      busyIndicatorView.hideBusyIndicator();
                                  },
                                  new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView))
                .countChangeRequests(workspaceProject.getSpace().getName(),
                                     workspaceProject.getRepository().getAlias());
    }

    private void ensureContentSet(final HTMLElement element) {
        if (element.parentNode == null) {
            this.view.setContent(element);
        }
    }

    public interface View extends UberElemental<ChangeRequestListPresenter> {

        void setContent(HTMLElement element);
    }
}