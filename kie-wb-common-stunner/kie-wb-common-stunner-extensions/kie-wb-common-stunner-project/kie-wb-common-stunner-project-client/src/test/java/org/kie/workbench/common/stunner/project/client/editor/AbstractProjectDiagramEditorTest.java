/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.project.client.editor;

import java.util.Optional;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.messageconsole.client.console.widget.button.AlertsButtonMenuItemBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenterFactory;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ClearSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ClearStatesSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.CopySelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.CutSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.DeleteSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToBpmnSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToJpgSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToPdfSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToPngSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.PasteSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.RedoSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SessionCommandFactory;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SwitchGridSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.UndoSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ValidateSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.VisitGraphSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientReadOnlySession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramLoseFocusEvent;
import org.kie.workbench.common.stunner.project.client.screens.ProjectMessagesListener;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
import org.kie.workbench.common.widgets.metadata.client.validation.AssetUpdateValidator;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractProjectDiagramEditorTest {

    @Mock
    private AbstractProjectDiagramEditor.View view;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private ErrorPopupPresenter errorPopupPresenter;

    @Mock
    private EventSourceMock<ChangeTitleWidgetEvent> changeTitleNotificationEvent;

    @Mock
    private SavePopUpPresenter savePopUpPresenter;

    @Mock
    private ClientProjectDiagramService clientProjectDiagramService;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private SessionPresenterFactory sessionPresenterFactory;

    @Mock
    private EventSourceMock<OnDiagramFocusEvent> onDiagramFocusEvent;

    @Mock
    private EventSourceMock<OnDiagramLoseFocusEvent> onDiagramLostFocusEvent;

    @Mock
    private BasicFileMenuBuilder menuBuilder;

    @Mock
    private VersionRecordManager versionRecordManager;

    @Spy
    @InjectMocks
    private FileMenuBuilderImpl fileMenuBuilder;

    @Mock
    private ProjectDiagramEditorMenuItemsBuilder projectMenuItemsBuilder;

    @Mock
    private ProjectController projectController;

    @Mock
    private WorkspaceProjectContext workbenchContext;

    @Mock
    private SessionCommandFactory sessionCommandFactory;

    @Mock
    private ProjectMessagesListener projectMessagesListener;

    @Mock
    private ClientResourceTypeMock resourceType;

    @Mock
    private DiagramClientErrorHandler diagramClientErrorHandler;

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private AlertsButtonMenuItemBuilder alertsButtonMenuItemBuilder;

    @Mock
    private MenuItem alertsButtonMenuItem;

    @Mock
    private SessionPresenter<AbstractClientFullSession, ?, Diagram> sessionPresenter;

    @Mock
    private ClearStatesSessionCommand clearStatesSessionCommand;

    @Mock
    private SwitchGridSessionCommand switchGridSessionCommand;

    @Mock
    private VisitGraphSessionCommand visitGraphSessionCommand;

    @Mock
    private ClearSessionCommand clearSessionCommand;

    @Mock
    private DeleteSelectionSessionCommand deleteSelectionSessionCommand;

    @Mock
    private UndoSessionCommand undoSessionCommand;

    @Mock
    private RedoSessionCommand redoSessionCommand;

    @Mock
    private ValidateSessionCommand validateSessionCommand;

    @Mock
    private ExportToPngSessionCommand exportToPngSessionCommand;

    @Mock
    private ExportToJpgSessionCommand exportToJpgSessionCommand;

    @Mock
    private ExportToPdfSessionCommand exportToPdfSessionCommand;

    @Mock
    private ExportToBpmnSessionCommand exportToBpmnSessionCommand;

    @Mock
    private CopySelectionSessionCommand copySelectionSessionCommand;

    @Mock
    private PasteSelectionSessionCommand pasteSelectionSessionCommand;

    @Mock
    private CutSelectionSessionCommand cutSelectionSessionCommand;

    abstract class ClientResourceTypeMock implements ClientResourceType {

    }

    private TestProjectDiagramEditor presenter;

    @Before
    public void setup() {
        doReturn(clearStatesSessionCommand).when(sessionCommandFactory).newClearStatesCommand();
        doReturn(switchGridSessionCommand).when(sessionCommandFactory).newSwitchGridCommand();
        doReturn(visitGraphSessionCommand).when(sessionCommandFactory).newVisitGraphCommand();
        doReturn(clearSessionCommand).when(sessionCommandFactory).newClearCommand();
        doReturn(deleteSelectionSessionCommand).when(sessionCommandFactory).newDeleteSelectedElementsCommand();
        doReturn(undoSessionCommand).when(sessionCommandFactory).newUndoCommand();
        doReturn(redoSessionCommand).when(sessionCommandFactory).newRedoCommand();
        doReturn(validateSessionCommand).when(sessionCommandFactory).newValidateCommand();
        doReturn(exportToPngSessionCommand).when(sessionCommandFactory).newExportToPngSessionCommand();
        doReturn(exportToJpgSessionCommand).when(sessionCommandFactory).newExportToJpgSessionCommand();
        doReturn(exportToPdfSessionCommand).when(sessionCommandFactory).newExportToPdfSessionCommand();
        doReturn(exportToBpmnSessionCommand).when(sessionCommandFactory).newExportToBpmnSessionCommand();
        doReturn(copySelectionSessionCommand).when(sessionCommandFactory).newCopySelectionCommand();
        doReturn(pasteSelectionSessionCommand).when(sessionCommandFactory).newPasteSelectionCommand();
        doReturn(cutSelectionSessionCommand).when(sessionCommandFactory).newCutSelectionCommand();

        doReturn(mock(MenuItem.class)).when(projectMenuItemsBuilder).newClearItem(any(Command.class));
        doReturn(mock(MenuItem.class)).when(projectMenuItemsBuilder).newVisitGraphItem(any(Command.class));
        doReturn(mock(MenuItem.class)).when(projectMenuItemsBuilder).newSwitchGridItem(any(Command.class));
        doReturn(mock(MenuItem.class)).when(projectMenuItemsBuilder).newDeleteSelectionItem(any(Command.class));
        doReturn(mock(MenuItem.class)).when(projectMenuItemsBuilder).newUndoItem(any(Command.class));
        doReturn(mock(MenuItem.class)).when(projectMenuItemsBuilder).newRedoItem(any(Command.class));
        doReturn(mock(MenuItem.class)).when(projectMenuItemsBuilder).newValidateItem(any(Command.class));
        doReturn(mock(MenuItem.class)).when(projectMenuItemsBuilder).newExportsItem(any(Command.class), any(Command.class), any(Command.class), any(Command.class));
        doReturn(mock(MenuItem.class)).when(projectMenuItemsBuilder).newPasteItem(any(Command.class));
        doReturn(mock(MenuItem.class)).when(projectMenuItemsBuilder).newCopyItem(any(Command.class));
        doReturn(mock(MenuItem.class)).when(projectMenuItemsBuilder).newCutItem(any(Command.class));

        when(alertsButtonMenuItemBuilder.build()).thenReturn(alertsButtonMenuItem);

        resourceType = mock(ClientResourceTypeMock.class);

        presenter = new TestProjectDiagramEditor(mock(AbstractProjectDiagramEditor.View.class),
                                                 mock(PlaceManager.class),
                                                 mock(ErrorPopupPresenter.class),
                                                 mock(EventSourceMock.class),
                                                 mock(SavePopUpPresenter.class),
                                                 resourceType,
                                                 mock(ClientProjectDiagramService.class),
                                                 mock(SessionManager.class),
                                                 mock(SessionPresenterFactory.class),
                                                 sessionCommandFactory,
                                                 projectMenuItemsBuilder,
                                                 new EventSourceMock<>(),
                                                 new EventSourceMock<>(),
                                                 projectMessagesListener,
                                                 diagramClientErrorHandler,
                                                 translationService) {
            {
                fileMenuBuilder = AbstractProjectDiagramEditorTest.this.fileMenuBuilder;
                workbenchContext = AbstractProjectDiagramEditorTest.this.workbenchContext;
                projectController = AbstractProjectDiagramEditorTest.this.projectController;
                versionRecordManager = AbstractProjectDiagramEditorTest.this.versionRecordManager;
                alertsButtonMenuItemBuilder = AbstractProjectDiagramEditorTest.this.alertsButtonMenuItemBuilder;
            }
        };
        presenter.init();
    }

    @Test
    public void testMakeMenuBar() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(true).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

        verify(fileMenuBuilder).addSave(any(MenuItem.class));
        verify(fileMenuBuilder).addCopy(any(Path.class),
                                        any(AssetUpdateValidator.class));
        verify(fileMenuBuilder).addRename(any(Path.class),
                                          any(AssetUpdateValidator.class));
        verify(fileMenuBuilder).addDelete(any(Path.class),
                                          any(AssetUpdateValidator.class));
        verify(fileMenuBuilder).addNewTopLevelMenu(alertsButtonMenuItem);
    }

    @Test
    public void testMakeMenuBarWithoutUpdateProjectPermission() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(false).when(projectController).canUpdateProject(any());

        presenter.makeMenuBar();

        verify(fileMenuBuilder,
               never()).addSave(any(MenuItem.class));
        verify(fileMenuBuilder,
               never()).addCopy(any(Path.class),
                                any(AssetUpdateValidator.class));
        verify(fileMenuBuilder,
               never()).addRename(any(Path.class),
                                  any(AssetUpdateValidator.class));
        verify(fileMenuBuilder,
               never()).addDelete(any(Path.class),
                                  any(AssetUpdateValidator.class));
        verify(fileMenuBuilder).addNewTopLevelMenu(alertsButtonMenuItem);
    }

    @Test
    public void testFormatTitle() {
        String title = "testDiagram";
        when(resourceType.getSuffix()).thenReturn("bpmn");
        when(resourceType.getShortName()).thenReturn("Business Process");

        String formattedTitle = presenter.formatTitle(title);
        assertEquals(formattedTitle,
                     "testDiagram.bpmn - Business Process");
    }

    @Test
    public void testCloseEditor() {
        presenter.setSessionPresenter(sessionPresenter);
        presenter.onClose();
        verify(clearStatesSessionCommand, times(1)).unbind();
        verify(switchGridSessionCommand, times(1)).unbind();
        verify(visitGraphSessionCommand, times(1)).unbind();
        verify(clearSessionCommand, times(1)).unbind();
        verify(deleteSelectionSessionCommand, times(1)).unbind();
        verify(undoSessionCommand, times(1)).unbind();
        verify(redoSessionCommand, times(1)).unbind();
        verify(validateSessionCommand, times(1)).unbind();
        verify(exportToPngSessionCommand, times(1)).unbind();
        verify(exportToJpgSessionCommand, times(1)).unbind();
        verify(exportToPdfSessionCommand, times(1)).unbind();
        verify(copySelectionSessionCommand, times(1)).unbind();
        verify(pasteSelectionSessionCommand, times(1)).unbind();
        verify(cutSelectionSessionCommand, times(1)).unbind();
        verify(sessionPresenter, never()).clear();
        verify(sessionPresenter, times(1)).destroy();
    }

    private static class TestProjectDiagramEditor extends AbstractProjectDiagramEditor<ClientResourceTypeMock> {

        public TestProjectDiagramEditor(final View view,
                                        final PlaceManager placeManager,
                                        final ErrorPopupPresenter errorPopupPresenter,
                                        final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent,
                                        final SavePopUpPresenter savePopUpPresenter,
                                        final ClientResourceTypeMock resourceType,
                                        final ClientProjectDiagramService projectDiagramServices,
                                        final SessionManager sessionManager,
                                        final SessionPresenterFactory<Diagram, AbstractClientReadOnlySession, AbstractClientFullSession> sessionPresenterFactory,
                                        final SessionCommandFactory sessionCommandFactory,
                                        final ProjectDiagramEditorMenuItemsBuilder menuItemsBuilder,
                                        final Event<OnDiagramFocusEvent> onDiagramFocusEvent,
                                        final Event<OnDiagramLoseFocusEvent> onDiagramLostFocusEvent,
                                        final ProjectMessagesListener projectMessagesListener,
                                        final DiagramClientErrorHandler diagramClientErrorHandler,
                                        final ClientTranslationService translationService) {
            super(view,
                  placeManager,
                  errorPopupPresenter,
                  changeTitleNotificationEvent,
                  savePopUpPresenter,
                  resourceType,
                  projectDiagramServices,
                  sessionManager,
                  sessionPresenterFactory,
                  sessionCommandFactory,
                  menuItemsBuilder,
                  onDiagramFocusEvent,
                  onDiagramLostFocusEvent,
                  projectMessagesListener,
                  diagramClientErrorHandler,
                  translationService);
        }

        @Override
        protected int getCanvasWidth() {
            return 0;
        }

        @Override
        protected int getCanvasHeight() {
            return 0;
        }

        public void onClose() {
            doClose();
        }

        @Override
        protected String getEditorIdentifier() {
            return null;
        }
    }
}
