/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.library.client.perspective;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.search.FilterUpdateEvent;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.widgets.client.search.ContextualSearch;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;
import org.uberfire.workbench.model.menu.Menus;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class LibraryPerspectiveTest {

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private ContextualSearch contextualSearch;

    @Mock
    private EventSourceMock<FilterUpdateEvent> filterUpdateEvent;

    @Mock
    private TranslationService translationService;

    @Mock
    private PlaceManager placeManager;

    @Captor
    private ArgumentCaptor<PartDefinition> partDefinitionArgumentCaptor;

    @Captor
    private ArgumentCaptor<PanelDefinition> panelDefinitionArgumentCaptor;

    private LibraryPerspective perspective;

    @Before
    public void setup() {
        perspective = new LibraryPerspective(libraryPlaces,
                                             contextualSearch,
                                             filterUpdateEvent,
                                             translationService,
                                             placeManager);
        doAnswer((InvocationOnMock invocation) -> invocation.getArguments()[0]).when(translationService).getTranslation(any(String.class));
    }

    @Test
    public void libraryRefreshesPlacesOnStartupTest() {
        perspective.onOpen();

        verify(libraryPlaces).refresh(any());
    }

    @Test
    public void libraryRegisterSearchHandlerTest() {
        perspective.registerSearchHandler();

        verify(contextualSearch).setPerspectiveSearchBehavior(eq(LibraryPlaces.LIBRARY_PERSPECTIVE),
                                                              any());
    }

    @Test
    public void libraryMenus() {
        final PerspectiveDefinition perspectiveDefinition = perspective.buildPerspective();
        final Menus menus = perspective.buildMenuBar();
        assertNotNull(menus);
        assertEquals(1,
                     menus.getItems().size());

        final MenuItem menuItem = menus.getItems().get(0);
        assertNotNull(menuItem);
        assertTrue(menuItem instanceof MenuItemCommand);

        final MenuItemCommand command = (MenuItemCommand) menuItem;
        command.getCommand().execute();

        verify(placeManager,
               times(1)).goTo(partDefinitionArgumentCaptor.capture(),
                              panelDefinitionArgumentCaptor.capture());

        final PartDefinition partDefinition = partDefinitionArgumentCaptor.getValue();
        final PanelDefinition panelDefinition = panelDefinitionArgumentCaptor.getValue();
        assertEquals(LibraryPlaces.ASSET_SEARCH,
                     partDefinition.getPlace().getIdentifier());
        assertEquals(perspectiveDefinition.getRoot(),
                     panelDefinition);
    }
}