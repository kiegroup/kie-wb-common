/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.project.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.project.client.type.BPMNDiagramResourceType;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerPresenter;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenDiagramModelUnSelectedEvent;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenDiagramPropertiesSwitchingSessionsEvent;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenMaximizedEvent;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenMinimizedEvent;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenPreMaximizedStateEvent;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.documentation.DocumentationPage;
import org.kie.workbench.common.stunner.kogito.client.editor.AbstractDiagramEditorMenuSessionItems;
import org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorExplorerScreen;
import org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorPropertiesScreen;
import org.kie.workbench.common.stunner.project.client.docks.StunnerDocksHandler;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectDiagramEditor;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectDiagramEditorCore;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectDiagramEditorTest;
import org.kie.workbench.common.stunner.project.client.editor.ProjectDiagramEditorProxy;
import org.kie.workbench.common.stunner.project.client.resources.i18n.StunnerProjectClientConstants;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.stunner.project.diagram.editor.ProjectDiagramResource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class BPMNDiagramEditorTest extends AbstractProjectDiagramEditorTest {

    @Mock
    private PlaceRequest currentPlace;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private BPMNProjectEditorMenuSessionItems bpmnMenuSessionItems;

    @Mock
    private UberfireDocks uberfireDocks;

    @Mock
    private UberfireDock propertiesDock;

    @Mock
    private PlaceRequest propertiesPlace;

    @Mock
    private UberfireDock explorerDock;

    @Mock
    private PlaceRequest explorerPlace;

    @Mock
    private StunnerDocksHandler stunnerDocksHandler;

    @Mock
    private PerspectiveActivity currentPerspective;

    @Mock
    private ScreenMaximizedEvent maximizedEvent;

    @Mock
    private ScreenMinimizedEvent minimizedEvent;

    @Mock
    private ScreenPreMaximizedStateEvent preMaximizedStateEvent;

    private BPMNDiagramEditor diagramEditor;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        super.setUp();
        when(canvasHandler.getDiagram()).thenReturn(diagram);
    }

    @Override
    protected AbstractDiagramEditorMenuSessionItems getMenuSessionItems() {
        return bpmnMenuSessionItems;
    }

    @Override
    protected BPMNDiagramResourceType mockResourceType() {
        final BPMNDiagramResourceType resourceType = mock(BPMNDiagramResourceType.class);
        when(resourceType.getSuffix()).thenReturn("bpmn");
        when(resourceType.getShortName()).thenReturn("Business Process");
        return resourceType;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected AbstractProjectDiagramEditor createDiagramEditor() {
        diagramEditor = spy(new BPMNDiagramEditor(view,
                                                  xmlEditorView,
                                                  sessionEditorPresenters,
                                                  sessionViewerPresenters,
                                                  onDiagramFocusEvent,
                                                  onDiagramLostFocusEvent,
                                                  notificationEvent,
                                                  errorPopupPresenter,
                                                  diagramClientErrorHandler,
                                                  documentationView,
                                                  (BPMNDiagramResourceType) getResourceType(),
                                                  bpmnMenuSessionItems,
                                                  projectMessagesListener,
                                                  translationService,
                                                  clientProjectDiagramService,
                                                  projectDiagramResourceServiceCaller,
                                                  uberfireDocks,
                                                  stunnerDocksHandler) {
            {
                docks = defaultEditorDock;
                perspectiveManager = perspectiveManagerMock;
                fileMenuBuilder = BPMNDiagramEditorTest.this.fileMenuBuilder;
                workbenchContext = BPMNDiagramEditorTest.this.workbenchContext;
                projectController = BPMNDiagramEditorTest.this.projectController;
                versionRecordManager = BPMNDiagramEditorTest.this.versionRecordManager;
                alertsButtonMenuItemBuilder = BPMNDiagramEditorTest.this.alertsButtonMenuItemBuilder;
                place = BPMNDiagramEditorTest.this.currentPlace;
                kieView = BPMNDiagramEditorTest.this.kieView;
                overviewWidget = BPMNDiagramEditorTest.this.overviewWidget;
                notification = BPMNDiagramEditorTest.this.notificationEvent;
                placeManager = BPMNDiagramEditorTest.this.placeManager;
                changeTitleNotification = BPMNDiagramEditorTest.this.changeTitleNotificationEvent;
                savePopUpPresenter = BPMNDiagramEditorTest.this.savePopUpPresenter;
            }

            @Override
            protected AbstractProjectDiagramEditorCore<ProjectMetadata, ProjectDiagram, ProjectDiagramResource, ProjectDiagramEditorProxy<ProjectDiagramResource>> makeCore(final AbstractProjectDiagramEditor.View view,
                                                                                                                                                                            final TextEditorView xmlEditorView,
                                                                                                                                                                            final Event<NotificationEvent> notificationEvent,
                                                                                                                                                                            final ManagedInstance<SessionEditorPresenter<EditorSession>> editorSessionPresenterInstances,
                                                                                                                                                                            final ManagedInstance<SessionViewerPresenter<ViewerSession>> viewerSessionPresenterInstances,
                                                                                                                                                                            final AbstractDiagramEditorMenuSessionItems<?> menuSessionItems,
                                                                                                                                                                            final ErrorPopupPresenter errorPopupPresenter,
                                                                                                                                                                            final DiagramClientErrorHandler diagramClientErrorHandler,
                                                                                                                                                                            final ClientTranslationService translationService) {
                presenterCore = spy(super.makeCore(view,
                                                   xmlEditorView,
                                                   notificationEvent,
                                                   editorSessionPresenterInstances,
                                                   viewerSessionPresenterInstances,
                                                   menuSessionItems,
                                                   errorPopupPresenter,
                                                   diagramClientErrorHandler,
                                                   translationService));
                this.saveAndRenameCommandBuilder = saveAndRenameCommandBuilderMock;
                return presenterCore;
            }

            @Override
            protected boolean isReadOnly() {
                return BPMNDiagramEditorTest.this.isReadOnly;
            }

            @Override
            protected void log(Level level,
                               String message) {
                //avoid GWT log initialization.
            }
        });
        return diagramEditor;
    }

    @Override
    public void testOpen() {
        super.testOpen();
        // TODO (Kogito): verify(presenter).addDocumentationPage(diagram);
    }

    @Test
    public void testOnOpen() {
        Collection<UberfireDock> stunnerDocks = new ArrayList<>();
        stunnerDocks.add(propertiesDock);
        stunnerDocks.add(explorerDock);

        String perspectiveIdentifier = "Test Perspective ID";

        when(perspectiveManagerMock.getCurrentPerspective()).thenReturn(currentPerspective);
        when(currentPerspective.getIdentifier()).thenReturn(perspectiveIdentifier);

        when(stunnerDocksHandler.provideDocks(perspectiveIdentifier)).thenReturn(stunnerDocks);

        when(propertiesDock.getPlaceRequest()).thenReturn(propertiesPlace);
        when(propertiesPlace.getIdentifier()).thenReturn(DiagramEditorPropertiesScreen.SCREEN_ID);

        when(explorerDock.getPlaceRequest()).thenReturn(explorerPlace);
        when(explorerPlace.getIdentifier()).thenReturn(DiagramEditorExplorerScreen.SCREEN_ID);

        diagramEditor.onOpen();
        verify(uberfireDocks, times(1)).open(propertiesDock);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddDocumentationPage() {
        when(documentationView.isEnabled()).thenReturn(Boolean.TRUE);
        when(translationService.getValue(StunnerProjectClientConstants.DOCUMENTATION)).thenReturn(DOC_LABEL);
        when(documentationView.initialize(diagram)).thenReturn(documentationView);
        ArgumentCaptor<DocumentationPage> documentationPageCaptor = ArgumentCaptor.forClass(DocumentationPage.class);
        presenter.addDocumentationPage(diagram);
        verify(translationService).getValue(StunnerProjectClientConstants.DOCUMENTATION);
        verify(kieView).addPage(documentationPageCaptor.capture());
        DocumentationPage documentationPage = documentationPageCaptor.getValue();
        assertEquals(documentationPage.getDocumentationView(), documentationView);
        assertEquals(documentationPage.getLabel(), DOC_LABEL);
    }

    @Test
    public void testMaximizedState() {
        Collection<UberfireDock> stunnerDocks = new ArrayList<>();
        stunnerDocks.add(propertiesDock);
        stunnerDocks.add(explorerDock);

        String perspectiveIdentifier = "Test Perspective ID";

        when(perspectiveManagerMock.getCurrentPerspective()).thenReturn(currentPerspective);
        when(currentPerspective.getIdentifier()).thenReturn(perspectiveIdentifier);

        when(stunnerDocksHandler.provideDocks(perspectiveIdentifier)).thenReturn(stunnerDocks);

        when(propertiesDock.getPlaceRequest()).thenReturn(propertiesPlace);
        when(propertiesPlace.getIdentifier()).thenReturn(DiagramEditorPropertiesScreen.SCREEN_ID);

        when(explorerDock.getPlaceRequest()).thenReturn(explorerPlace);
        when(explorerPlace.getIdentifier()).thenReturn(DiagramEditorExplorerScreen.SCREEN_ID);

        diagramEditor.onOpen();
        verify(uberfireDocks, times(1)).open(propertiesDock);

        diagramEditor.closePropertiesDocks();
        verify(uberfireDocks, times(1)).close(propertiesDock);

        diagramEditor.onScreenMaximizedEvent(maximizedEvent);
        diagramEditor.onScreenMinimizedEvent(minimizedEvent);

        // properties should not be opened since it was closed before maximized
        verify(uberfireDocks, times(1)).open(propertiesDock);

        diagramEditor.openPropertiesDocks();
        verify(uberfireDocks, times(2)).open(propertiesDock);

        diagramEditor.onScreenMaximizedEvent(maximizedEvent);
        diagramEditor.onScreenPreMaximizedStateEvent(preMaximizedStateEvent);
        diagramEditor.onScreenMinimizedEvent(minimizedEvent);

        // properties should be opened since it was opened before maximized
        verify(uberfireDocks, times(3)).open(propertiesDock);

        diagramEditor.onOpen();
        verify(uberfireDocks, times(4)).open(propertiesDock);

        diagramEditor.closeExplorerDocks();
        verify(uberfireDocks, times(1)).close(explorerDock);

        diagramEditor.onScreenMaximizedEvent(maximizedEvent);
        diagramEditor.onScreenMinimizedEvent(minimizedEvent);

        // explore should not be opened since it was closed before maximized
        verify(uberfireDocks, times(0)).open(explorerDock);

        diagramEditor.openExplorerDocks();
        verify(uberfireDocks, times(1)).open(explorerDock);

        diagramEditor.onScreenMaximizedEvent(maximizedEvent);
        when(preMaximizedStateEvent.isExplorerScreen()).thenReturn(true);
        diagramEditor.onScreenPreMaximizedStateEvent(preMaximizedStateEvent);
        diagramEditor.onScreenMinimizedEvent(minimizedEvent);

        // explore should be opened since it was opened before maximized
        verify(uberfireDocks, times(2)).open(explorerDock);

        diagramEditor.onScreenMaximizedEvent(maximizedEvent);
        when(preMaximizedStateEvent.isExplorerScreen()).thenReturn(true);
        diagramEditor.onScreenPreMaximizedStateEvent(preMaximizedStateEvent);

        // now open properties panel
        diagramEditor.openPropertiesDocks();
        // verify properties panel is now open
        verify(uberfireDocks, times(5)).open(propertiesDock);
        diagramEditor.onScreenMinimizedEvent(minimizedEvent);

        // verify explore panel is now closed
        verify(uberfireDocks, times(3)).open(explorerDock);

        // verify properties remains opened after minimize
        verify(uberfireDocks, times(5)).open(propertiesDock);

        diagramEditor.onScreenMaximizedEvent(maximizedEvent);
        when(preMaximizedStateEvent.isExplorerScreen()).thenReturn(false);
        diagramEditor.onScreenPreMaximizedStateEvent(preMaximizedStateEvent);

        // now open properties panel
        diagramEditor.openExplorerDocks();
        // verify properties panel is now open
        verify(uberfireDocks, times(4)).open(explorerDock);
        diagramEditor.onScreenMinimizedEvent(minimizedEvent);

        // verify explore panel is now closed
        verify(uberfireDocks, times(6)).open(propertiesDock);

        // verify properties remains opened after minimize
        verify(uberfireDocks, times(4)).open(explorerDock);
    }

    @Test
    public void testSwitchingViews() {
        Collection<UberfireDock> stunnerDocks = new ArrayList<>();
        stunnerDocks.add(propertiesDock);
        stunnerDocks.add(explorerDock);

        String perspectiveIdentifier = "Test Perspective ID";

        when(perspectiveManagerMock.getCurrentPerspective()).thenReturn(currentPerspective);
        when(currentPerspective.getIdentifier()).thenReturn(perspectiveIdentifier);

        when(stunnerDocksHandler.provideDocks(perspectiveIdentifier)).thenReturn(stunnerDocks);

        when(propertiesDock.getPlaceRequest()).thenReturn(propertiesPlace);
        when(propertiesPlace.getIdentifier()).thenReturn(DiagramEditorPropertiesScreen.SCREEN_ID);

        when(explorerDock.getPlaceRequest()).thenReturn(explorerPlace);
        when(explorerPlace.getIdentifier()).thenReturn(DiagramEditorExplorerScreen.SCREEN_ID);

        diagramEditor.onOpen();
        verify(uberfireDocks, times(1)).open(propertiesDock);

        // Properties Panel
        // close properties
        diagramEditor.onOverviewSelected();
        diagramEditor.closePropertiesDocks();

        verify(uberfireDocks, times(1)).close(propertiesDock);
        diagramEditor.fireDiagramFocusEvent(); // Simulate Documentation

        verify(uberfireDocks, times(1)).close(propertiesDock); // Should remain closed

        ScreenDiagramModelUnSelectedEvent screenDiagramModelUnSelectedEvent = new ScreenDiagramModelUnSelectedEvent(diagramEditor.getClientSession(), false);
        diagramEditor.onScreenDiagramModelUnSelectedEvent(screenDiagramModelUnSelectedEvent);
        diagramEditor.onEditTabSelected(); // open properties

        // properties should now be opened since it was opened before switching tabs
        verify(uberfireDocks, times(2)).open(propertiesDock);

        diagramEditor.closePropertiesDocks(); // close properties
        verify(uberfireDocks, times(2)).close(propertiesDock);

        // close properties
        diagramEditor.onOverviewSelected();
        diagramEditor.closePropertiesDocks();

        diagramEditor.fireDiagramFocusEvent(); // Simulate Documentation

        screenDiagramModelUnSelectedEvent = new ScreenDiagramModelUnSelectedEvent(new ClientSession() {
            @Override
            public String getSessionUUID() {
                return "test";
            }

            @Override
            public Canvas getCanvas() {
                return null;
            }

            @Override
            public CanvasHandler getCanvasHandler() {
                return null;
            }
        }, false);

        // Simulate not firing this Event, meaning properties is closed
        diagramEditor.onEditTabUnselected();
        //diagramEditor.onScreenDiagramModelUnSelectedEvent(screenDiagramModelUnSelectedEvent);
        diagramEditor.onEditTabSelected();

        // properties should not be opened it was closed before switching tabs
        verify(uberfireDocks, times(2)).open(propertiesDock);

        // Explore Panel

        // close properties
        diagramEditor.openExplorerDocks();
        verify(uberfireDocks, times(1)).open(explorerDock);

        // close properties
        diagramEditor.onOverviewSelected();
        diagramEditor.closeExplorerDocks();

        verify(uberfireDocks, times(1)).close(explorerDock);
        diagramEditor.fireDiagramFocusEvent(); // Simulate Documentation

        verify(uberfireDocks, times(1)).close(explorerDock); // Should remain closed

        screenDiagramModelUnSelectedEvent = new ScreenDiagramModelUnSelectedEvent(diagramEditor.getClientSession(), true);
        diagramEditor.onScreenDiagramModelUnSelectedEvent(screenDiagramModelUnSelectedEvent);
        diagramEditor.onEditTabSelected(); // open explorer

        // explorer should now be opened since it was opened before switching tabs
        verify(uberfireDocks, times(2)).open(explorerDock);

        diagramEditor.closeExplorerDocks(); // close explorer
        verify(uberfireDocks, times(2)).close(explorerDock);

        // close properties
        diagramEditor.onOverviewSelected();
        diagramEditor.closeExplorerDocks();

        diagramEditor.fireDiagramFocusEvent(); // Simulate Documentation

        screenDiagramModelUnSelectedEvent = new ScreenDiagramModelUnSelectedEvent(new ClientSession() {
            @Override
            public String getSessionUUID() {
                return "test";
            }

            @Override
            public Canvas getCanvas() {
                return null;
            }

            @Override
            public CanvasHandler getCanvasHandler() {
                return null;
            }
        }, true);

        // Simulate not firing this Event, meaning explorer is closed
        diagramEditor.onEditTabUnselected();
        //diagramEditor.onScreenDiagramModelUnSelectedEvent(screenDiagramModelUnSelectedEvent);
        diagramEditor.onEditTabSelected();

        // explorer should not be opened it was closed before switching tabs
        verify(uberfireDocks, times(2)).open(explorerDock);

        // Verify changing states

        // Properties Panel
        diagramEditor.openPropertiesDocks();
        verify(uberfireDocks, times(3)).close(propertiesDock);

        verify(uberfireDocks, times(3)).open(propertiesDock);
        diagramEditor.onOverviewSelected();
        diagramEditor.closePropertiesDocks();

        verify(uberfireDocks, times(4)).close(propertiesDock);

        diagramEditor.fireDiagramFocusEvent(); // Simulate Documentation
        diagramEditor.closePropertiesDocks();

        verify(uberfireDocks, times(5)).close(propertiesDock); // Should remain closed

        screenDiagramModelUnSelectedEvent = new ScreenDiagramModelUnSelectedEvent(diagramEditor.getClientSession(), true);
        diagramEditor.openExplorerDocks(); // open explore panel
        diagramEditor.onScreenDiagramModelUnSelectedEvent(screenDiagramModelUnSelectedEvent);

        verify(uberfireDocks, times(3)).open(explorerDock);

        diagramEditor.onEditTabSelected(); // open edit

        // properties should now not be opened since explore panel was opened during Documentation / Overview
        verify(uberfireDocks, times(3)).open(propertiesDock);
        verify(uberfireDocks, times(4)).open(explorerDock);

        // Explore Panel
        verify(uberfireDocks, times(3)).close(explorerDock);

        diagramEditor.openExplorerDocks();
        verify(uberfireDocks, times(5)).open(explorerDock);
        verify(uberfireDocks, times(3)).open(propertiesDock);

        diagramEditor.onOverviewSelected();
        diagramEditor.closeExplorerDocks();

        verify(uberfireDocks, times(4)).close(explorerDock);

        diagramEditor.fireDiagramFocusEvent(); // Simulate Documentation
        diagramEditor.closeExplorerDocks();

        verify(uberfireDocks, times(5)).close(explorerDock); // Should remain closed

        screenDiagramModelUnSelectedEvent = new ScreenDiagramModelUnSelectedEvent(diagramEditor.getClientSession(), false);
        diagramEditor.openPropertiesDocks(); // open properties panel
        diagramEditor.onScreenDiagramModelUnSelectedEvent(screenDiagramModelUnSelectedEvent);

        verify(uberfireDocks, times(4)).open(propertiesDock);

        diagramEditor.onEditTabSelected(); // open properties

        // properties should now not be opened since explore panel was opened during Documentation / Overview
        verify(uberfireDocks, times(5)).open(propertiesDock);
        verify(uberfireDocks, times(5)).open(explorerDock);
    }

    @Test
    public void testSimulateSessionSwitch() {
        Collection<UberfireDock> stunnerDocks = new ArrayList<>();
        stunnerDocks.add(propertiesDock);
        stunnerDocks.add(explorerDock);

        String perspectiveIdentifier = "Test Perspective ID";

        when(perspectiveManagerMock.getCurrentPerspective()).thenReturn(currentPerspective);
        when(currentPerspective.getIdentifier()).thenReturn(perspectiveIdentifier);

        when(stunnerDocksHandler.provideDocks(perspectiveIdentifier)).thenReturn(stunnerDocks);

        when(propertiesDock.getPlaceRequest()).thenReturn(propertiesPlace);
        when(propertiesPlace.getIdentifier()).thenReturn(DiagramEditorPropertiesScreen.SCREEN_ID);

        when(explorerDock.getPlaceRequest()).thenReturn(explorerPlace);
        when(explorerPlace.getIdentifier()).thenReturn(DiagramEditorExplorerScreen.SCREEN_ID);

        diagramEditor.onOpen();
        verify(uberfireDocks, times(1)).open(propertiesDock);

        diagramEditor.onLostFocus();
        // Simulate Event Fired by FormProperties being opened
        ScreenDiagramPropertiesSwitchingSessionsEvent screenDiagramPropertiesSwitchingSessionsEvent = new ScreenDiagramPropertiesSwitchingSessionsEvent(diagramEditor.getClientSession(), false);

        diagramEditor.onScreenDiagramPropertiesSwitchingSessionsEvent(screenDiagramPropertiesSwitchingSessionsEvent);
        doAnswer(i -> {
            ((com.google.gwt.user.client.Command) i.getArguments()[0]).execute();
            return null;
        }).when(diagramEditor).deferOpenDocks(any());
        diagramEditor.onFocus();

        verify(uberfireDocks, times(2)).open(propertiesDock);

        diagramEditor.onLostFocus();
        // Since no event fired, form properties was not opened
        diagramEditor.onFocus();

        verify(uberfireDocks, times(2)).open(propertiesDock);

        diagramEditor.onLostFocus();

        screenDiagramPropertiesSwitchingSessionsEvent = new ScreenDiagramPropertiesSwitchingSessionsEvent(new ClientSession() {
            @Override
            public String getSessionUUID() {
                return "test";
            }

            @Override
            public Canvas getCanvas() {
                return null;
            }

            @Override
            public CanvasHandler getCanvasHandler() {
                return null;
            }
        }, false);

        diagramEditor.onScreenDiagramPropertiesSwitchingSessionsEvent(screenDiagramPropertiesSwitchingSessionsEvent);
        diagramEditor.onFocus();

        verify(uberfireDocks, times(2)).open(propertiesDock);

        // Explore Panel

        diagramEditor.openExplorerDocks();
        verify(uberfireDocks, times(1)).open(explorerDock);

        diagramEditor.onLostFocus();
        // Simulate Event Fired by FormProperties being opened
        screenDiagramPropertiesSwitchingSessionsEvent = new ScreenDiagramPropertiesSwitchingSessionsEvent(diagramEditor.getClientSession(), true);

        diagramEditor.onScreenDiagramPropertiesSwitchingSessionsEvent(screenDiagramPropertiesSwitchingSessionsEvent);
        doAnswer(i -> {
            ((com.google.gwt.user.client.Command) i.getArguments()[0]).execute();
            return null;
        }).when(diagramEditor).deferOpenDocks(any());
        diagramEditor.onFocus();

        verify(uberfireDocks, times(2)).open(explorerDock);

        diagramEditor.onLostFocus();
        // Since no event fired, form properties was not opened
        diagramEditor.onFocus();

        verify(uberfireDocks, times(2)).open(explorerDock);

        diagramEditor.onLostFocus();

        screenDiagramPropertiesSwitchingSessionsEvent = new ScreenDiagramPropertiesSwitchingSessionsEvent(new ClientSession() {
            @Override
            public String getSessionUUID() {
                return "test";
            }

            @Override
            public Canvas getCanvas() {
                return null;
            }

            @Override
            public CanvasHandler getCanvasHandler() {
                return null;
            }
        }, true);

        diagramEditor.onScreenDiagramPropertiesSwitchingSessionsEvent(screenDiagramPropertiesSwitchingSessionsEvent);
        diagramEditor.onFocus();

        verify(uberfireDocks, times(2)).open(explorerDock);
    }
}
