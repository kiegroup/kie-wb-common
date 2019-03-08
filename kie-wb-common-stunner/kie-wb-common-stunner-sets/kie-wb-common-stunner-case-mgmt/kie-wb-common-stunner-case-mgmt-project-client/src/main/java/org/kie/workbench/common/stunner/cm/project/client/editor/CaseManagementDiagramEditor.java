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

package org.kie.workbench.common.stunner.cm.project.client.editor;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.client.BPMNShapeSet;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerPresenter;
import org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet;
import org.kie.workbench.common.stunner.cm.client.CaseManagementShapeSet;
import org.kie.workbench.common.stunner.cm.project.client.resources.i18n.CaseManagementProjectClientConstants;
import org.kie.workbench.common.stunner.cm.project.client.type.CaseManagementDiagramResourceType;
import org.kie.workbench.common.stunner.cm.project.service.CaseManagementSwitchViewService;
import org.kie.workbench.common.stunner.core.client.annotation.DiagramEditor;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectDiagramEditor;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramLoseFocusEvent;
import org.kie.workbench.common.stunner.project.client.screens.ProjectMessagesListener;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramResourceService;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.views.pfly.multipage.PageImpl;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnLostFocus;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@DiagramEditor
@WorkbenchEditor(identifier = CaseManagementDiagramEditor.EDITOR_ID, supportedTypes = {CaseManagementDiagramResourceType.class})
public class CaseManagementDiagramEditor extends AbstractProjectDiagramEditor<CaseManagementDiagramResourceType> {

    private static final Logger LOGGER = Logger.getLogger(CaseManagementDiagramEditor.class.getName());

    public static final String EDITOR_ID = "CaseManagementDiagramEditor";

    private View processView;

    private Optional<SessionEditorPresenter<EditorSession>> processEditorSessionPresenter = Optional.empty();

    private Caller<CaseManagementSwitchViewService> caseManagementSwitchViewService;

    private AtomicBoolean switchedToProcess;

    @Inject
    public CaseManagementDiagramEditor(final View view,
                                       final DocumentationView documentationView,
                                       final PlaceManager placeManager,
                                       final ErrorPopupPresenter errorPopupPresenter,
                                       final Event<ChangeTitleWidgetEvent> changeTitleNotificationEvent,
                                       final SavePopUpPresenter savePopUpPresenter,
                                       final CaseManagementDiagramResourceType resourceType,
                                       final ClientProjectDiagramService projectDiagramServices,
                                       final ManagedInstance<SessionEditorPresenter<EditorSession>> editorSessionPresenterInstances,
                                       final ManagedInstance<SessionViewerPresenter<ViewerSession>> viewerSessionPresenterInstances,
                                       final CaseManagementProjectEditorMenuSessionItems menuSessionItems,
                                       final Event<OnDiagramFocusEvent> onDiagramFocusEvent,
                                       final Event<OnDiagramLoseFocusEvent> onDiagramLostFocusEvent,
                                       final ProjectMessagesListener projectMessagesListener,
                                       final DiagramClientErrorHandler diagramClientErrorHandler,
                                       final ClientTranslationService translationService,
                                       final TextEditorView xmlEditorView,
                                       final Caller<ProjectDiagramResourceService> projectDiagramResourceServiceCaller,
                                       final View processView,
                                       final Caller<CaseManagementSwitchViewService> caseManagementSwitchViewService) {
        super(view,
              documentationView,
              placeManager,
              errorPopupPresenter,
              changeTitleNotificationEvent,
              savePopUpPresenter,
              resourceType,
              projectDiagramServices,
              editorSessionPresenterInstances,
              viewerSessionPresenterInstances,
              menuSessionItems,
              onDiagramFocusEvent,
              onDiagramLostFocusEvent,
              projectMessagesListener,
              diagramClientErrorHandler,
              translationService,
              xmlEditorView,
              projectDiagramResourceServiceCaller);

        this.processView = processView;
        this.caseManagementSwitchViewService = caseManagementSwitchViewService;
        this.switchedToProcess = new AtomicBoolean();
    }

    @Override
    public void init() {
        super.init();

        this.processView.init(this);
        this.switchedToProcess.set(false);
    }

    @Override
    protected void initialiseKieEditorForSession(ProjectDiagram diagram) {
        super.initialiseKieEditorForSession(diagram);

        this.addPage(
                new PageImpl(processView,
                             this.getTranslationService().getValue(CaseManagementProjectClientConstants.CaseManagementEditorProcessViewTitle)) {

                    @Override
                    public void onFocus() {
                        final boolean toProcess = CaseManagementDiagramEditor.this.switchedToProcess.getAndSet(true);
                        if (!toProcess) {
                            if (!CaseManagementDiagramEditor.this.getProcessSessionPresenter().isPresent()) {
                                CaseManagementDiagramEditor.this.setProcessEditorSessionPresenter(
                                        Optional.of(newProcessSessionEditorPresenter(CaseManagementDiagramEditor.this.processView)));
                            }

                            updateSessionEditorPresenter(CaseManagementDiagramEditor.this.getEditorSessionPresenter(),
                                                         BPMNDefinitionSet.class.getName(),
                                                         BPMNShapeSet.class.getName(),
                                                         CaseManagementDiagramEditor.this.getProcessSessionPresenter());
                        }

                        super.onFocus();
                    }
                });
    }

    @Override
    public void onEditTabSelected() {
        final boolean toProcess = CaseManagementDiagramEditor.this.switchedToProcess.getAndSet(false);
        if (toProcess) {
            updateSessionEditorPresenter(this.getProcessSessionPresenter(),
                                         CaseManagementDefinitionSet.class.getName(),
                                         CaseManagementShapeSet.class.getName(),
                                         this.getEditorSessionPresenter());
        }
    }

    private void updateSessionEditorPresenter(final Optional<SessionEditorPresenter<EditorSession>> sessionEditorPresenter,
                                              final String defSetId, final String shapeSetId,
                                              final Optional<SessionEditorPresenter<EditorSession>> updatedSessionEditorPresenter) {
        sessionEditorPresenter.ifPresent(p -> {
            final Diagram d = p.getHandler().getDiagram();
            CaseManagementDiagramEditor.this.onSwitch(d, defSetId, shapeSetId, updatedSessionEditorPresenter);
        });
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        super.doStartUp(path,
                        place);
    }

    @Override
    protected String getEditorIdentifier() {
        return CaseManagementDiagramEditor.EDITOR_ID;
    }

    @OnOpen
    public void onOpen() {
        super.doOpen();
    }

    @OnClose
    @Override
    public void onClose() {
        super.doClose();
        super.onClose();
    }

    @OnFocus
    public void onFocus() {
        super.doFocus();
    }

    @OnLostFocus
    public void onLostFocus() {
        super.doLostFocus();
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        super.getMenus(menusConsumer);
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }

    @OnMayClose
    public boolean onMayClose() {
        return super.mayClose(getCurrentDiagramHash());
    }

    @Override
    protected SessionEditorPresenter<EditorSession> newSessionEditorPresenter() {
        return (SessionEditorPresenter<EditorSession>) super.newSessionEditorPresenter()
                .displayNotifications(type -> false);
    }

    private SessionEditorPresenter<EditorSession> newProcessSessionEditorPresenter(final View view) {
        final SessionEditorPresenter<EditorSession> presenter =
                (SessionEditorPresenter<EditorSession>) this.getEditorSessionPresenterInstances().get()
                        .withToolbar(false)
                        .withPalette(true)
                        .displayNotifications(type -> false);
        view.setWidget(presenter.getView());
        return presenter;
    }

    public void reopenSession(final ProjectDiagram diagram,
                              final Optional<SessionEditorPresenter<EditorSession>> sessionEditorPresenter) {
        sessionEditorPresenter.ifPresent(
                p -> p.open(diagram,
                            new SessionPresenter.SessionPresenterCallback<Diagram>() {
                                @Override
                                public void afterSessionOpened() {

                                }

                                @Override
                                public void afterCanvasInitialized() {

                                }

                                @Override
                                public void onSuccess() {
                                    CaseManagementDiagramEditor.this.getMenuSessionItems().getCommands()
                                            .getCommands().destroyCommands();
                                    CaseManagementDiagramEditor.this.getMenuSessionItems()
                                            .bind(CaseManagementDiagramEditor.this.getSession());
                                }

                                @Override
                                public void onError(final ClientRuntimeError error) {
                                    onLoadError(error);
                                }
                            })
        );
    }

    @Override
    protected void destroySession() {
        processEditorSessionPresenter.ifPresent(session -> {
            session.destroy();
            processEditorSessionPresenter = Optional.empty();
        });

        super.destroySession();
    }

    private Optional<SessionEditorPresenter<EditorSession>> getProcessSessionPresenter() {
        return processEditorSessionPresenter;
    }

    private void setProcessEditorSessionPresenter(Optional<SessionEditorPresenter<EditorSession>> processEditorSessionPresenter) {
        this.processEditorSessionPresenter = processEditorSessionPresenter;
    }

    @Override
    public SessionPresenter<? extends ClientSession, ?, Diagram> getSessionPresenter() {
        return switchedToProcess.get() ? this.processEditorSessionPresenter.get() : super.getSessionPresenter();
    }

    protected void onSwitch(final Diagram diagram, final String defSetId, final String shapeDefId,
                            final Optional<SessionEditorPresenter<EditorSession>> sessionEditorPresenter) {
        this.processView.showLoading();

        caseManagementSwitchViewService.call(new RemoteCallback<Optional<ProjectDiagram>>() {
            @Override
            public void callback(Optional<ProjectDiagram> diagram) {
                diagram.ifPresent(d -> {
                    CaseManagementDiagramEditor.this.reopenSession(d, sessionEditorPresenter);
                    CaseManagementDiagramEditor.this.processView.hideBusyIndicator();
                });
            }
        }).switchView(diagram, defSetId, shapeDefId);
    }

    @Override
    protected Optional<String> getMainEditorPageTitle() {
        return Optional.of(this.getTranslationService().getValue(CaseManagementProjectClientConstants.CaseManagementMainEditorPageTitle));
    }
}
