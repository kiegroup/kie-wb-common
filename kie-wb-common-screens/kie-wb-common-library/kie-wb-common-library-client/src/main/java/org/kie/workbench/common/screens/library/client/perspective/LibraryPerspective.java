/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.library.client.perspective;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.api.search.FilterUpdateEvent;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.widgets.client.search.ContextualSearch;
import org.kie.workbench.common.widgets.client.search.SearchBehavior;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

@ApplicationScoped
@WorkbenchPerspective(identifier = "LibraryPerspective")
public class LibraryPerspective {

    private LibraryPlaces libraryPlaces;

    private ContextualSearch contextualSearch;

    private Event<FilterUpdateEvent> filterUpdateEvent;

    private TranslationService translationService;

    private PlaceManager placeManager;

    private PerspectiveDefinition perspectiveDefinition;

    private boolean refresh = true;

    public LibraryPerspective() {
    }

    @Inject
    public LibraryPerspective(final LibraryPlaces libraryPlaces,
                              final ContextualSearch contextualSearch,
                              final Event<FilterUpdateEvent> filterUpdateEvent,
                              final TranslationService translationService,
                              final PlaceManager placeManager) {
        this.libraryPlaces = libraryPlaces;
        this.contextualSearch = contextualSearch;
        this.filterUpdateEvent = filterUpdateEvent;
        this.translationService = translationService;
        this.placeManager = placeManager;
    }

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        perspectiveDefinition = new PerspectiveDefinitionImpl(MultiListWorkbenchPanelPresenter.class.getName());
        perspectiveDefinition.setName("Library Perspective");

        return perspectiveDefinition;
    }

    @PostConstruct
    public void registerSearchHandler() {
        final SearchBehavior searchBehavior = searchFilter -> {
            filterUpdateEvent.fire(new FilterUpdateEvent(searchFilter));
        };

        contextualSearch.setPerspectiveSearchBehavior(LibraryPlaces.LIBRARY_PERSPECTIVE,
                                                      searchBehavior);
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        final boolean refresh = Boolean.parseBoolean(placeRequest.getParameter("refresh",
                                                                               "true"));
        this.refresh = refresh;
    }

    @OnOpen
    public void onOpen() {
        Command callback = null;
        if (refresh) {
            callback = () -> libraryPlaces.goToLibrary();
        }
        libraryPlaces.refresh(callback);
    }

    @WorkbenchMenu
    @SuppressWarnings("unused")
    public Menus buildMenuBar() {
        return MenuFactory
                .newTopLevelMenu(translationService.getTranslation(LibraryConstants.AssetSearch))
                .position(MenuPosition.RIGHT)
                .respondsWith(() -> {
                    final PlaceRequest placeRequest = new DefaultPlaceRequest(LibraryPlaces.ASSET_SEARCH);
                    final PartDefinition part = new PartDefinitionImpl(placeRequest);
                    part.setSelectable(false);
                    placeManager.goTo(part,
                                      getRootPanel());
                })
                .endMenu()
                .build();
    }

    public PanelDefinition getRootPanel() {
        return perspectiveDefinition.getRoot();
    }
}
