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

import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionEditorPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionViewerPresenter;
import org.kie.workbench.common.stunner.cm.project.client.type.CaseManagementDiagramResourceType;
import org.kie.workbench.common.stunner.core.client.annotation.DiagramEditor;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectDiagramEditor;
import org.kie.workbench.common.stunner.project.client.screens.ProjectMessagesListener;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramResourceService;
import org.kie.workbench.common.stunner.submarine.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.submarine.client.editor.event.OnDiagramLoseFocusEvent;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextEditorView;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnLostFocus;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@DiagramEditor
@WorkbenchEditor(identifier = CaseManagementDiagramEditor.EDITOR_ID, supportedTypes = {CaseManagementDiagramResourceType.class})
public class CaseManagementDiagramEditor extends AbstractProjectDiagramEditor<CaseManagementDiagramResourceType> {

    public static final String EDITOR_ID = "CaseManagementDiagramEditor";

    @Inject
    public CaseManagementDiagramEditor(final AbstractProjectDiagramEditor.View view,
                                       final TextEditorView xmlEditorView,
                                       final ManagedInstance<SessionEditorPresenter<EditorSession>> editorSessionPresenterInstances,
                                       final ManagedInstance<SessionViewerPresenter<ViewerSession>> viewerSessionPresenterInstances,
                                       final Event<OnDiagramFocusEvent> onDiagramFocusEvent,
                                       final Event<OnDiagramLoseFocusEvent> onDiagramLostFocusEvent,
                                       final Event<NotificationEvent> notificationEvent,
                                       final ErrorPopupPresenter errorPopupPresenter,
                                       final DiagramClientErrorHandler diagramClientErrorHandler,
                                       final DocumentationView documentationView,
                                       final CaseManagementDiagramResourceType resourceType,
                                       final CaseManagementProjectEditorMenuSessionItems menuSessionItems,
                                       final ProjectMessagesListener projectMessagesListener,
                                       final ClientTranslationService translationService,
                                       final ClientProjectDiagramService projectDiagramServices,
                                       final Caller<ProjectDiagramResourceService> projectDiagramResourceServiceCaller) {
        super(view,
              xmlEditorView,
              editorSessionPresenterInstances,
              viewerSessionPresenterInstances,
              onDiagramFocusEvent,
              onDiagramLostFocusEvent,
              notificationEvent,
              errorPopupPresenter,
              diagramClientErrorHandler,
              documentationView,
              resourceType,
              menuSessionItems,
              projectMessagesListener,
              translationService,
              projectDiagramServices,
              projectDiagramResourceServiceCaller);
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        super.doStartUp(path,
                        place);
    }

    @Override
    public String getEditorIdentifier() {
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
    public SessionEditorPresenter<EditorSession> newSessionEditorPresenter() {
        return (SessionEditorPresenter<EditorSession>) super.newSessionEditorPresenter()
                .displayNotifications(type -> false);
    }
}
