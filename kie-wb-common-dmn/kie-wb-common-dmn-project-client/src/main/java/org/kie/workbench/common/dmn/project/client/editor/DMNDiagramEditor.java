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
package org.kie.workbench.common.dmn.project.client.editor;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.commands.general.NavigateToExpressionEditorCommand;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorDock;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenter;
import org.kie.workbench.common.dmn.client.docks.navigator.common.LazyCanvasFocusUtils;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramTuple;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.editors.drd.DRDNameChanger;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPage;
import org.kie.workbench.common.dmn.client.editors.search.DMNEditorSearchIndex;
import org.kie.workbench.common.dmn.client.editors.search.DMNSearchableElement;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent;
import org.kie.workbench.common.dmn.client.editors.types.DataTypesPage;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELInitializer;
import org.kie.workbench.common.dmn.project.client.type.DMNDiagramResourceType;
import org.kie.workbench.common.services.shared.resources.PerspectiveIds;
import org.kie.workbench.common.stunner.client.widgets.editor.StunnerEditor;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPresenter;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.core.client.annotation.DiagramEditor;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.components.layout.LayoutHelper;
import org.kie.workbench.common.stunner.core.client.components.layout.OpenDiagramLayoutExecutor;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;
import org.kie.workbench.common.stunner.core.util.HashUtil;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.kie.workbench.common.stunner.project.client.editor.AbstractProjectDiagramEditor;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramLoseFocusEvent;
import org.kie.workbench.common.stunner.project.client.screens.ProjectMessagesListener;
import org.kie.workbench.common.stunner.project.client.service.ClientProjectDiagramService;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramResourceService;
import org.kie.workbench.common.widgets.client.search.component.SearchBarComponent;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.views.pfly.multipage.MultiPageEditorSelectedPageEvent;
import org.uberfire.ext.editor.commons.client.menu.MenuItems;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnLostFocus;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

import static elemental2.dom.DomGlobal.setTimeout;

@Dependent
@DiagramEditor
@WorkbenchEditor(identifier = DMNDiagramEditor.EDITOR_ID, supportedTypes = {DMNDiagramResourceType.class})
public class DMNDiagramEditor extends AbstractProjectDiagramEditor<DMNDiagramResourceType> {

    public static final String EDITOR_ID = "DMNDiagramEditor";

    //Editor tabs: [0] Main editor, [1] Documentation, [2] Data-Types, [3] Imported Models, [4] Overview
    protected static final int DATA_TYPES_PAGE_INDEX = 2;

    private final SessionManager sessionManager;
    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    private final DecisionNavigatorDock decisionNavigatorDock;

    private final LayoutHelper layoutHelper;
    private final OpenDiagramLayoutExecutor openDiagramLayoutExecutor;
    private final DataTypesPage dataTypesPage;
    private final IncludedModelsPage includedModelsPage;
    private final DMNEditorSearchIndex editorSearchIndex;
    private final SearchBarComponent<DMNSearchableElement> searchBarComponent;
    private final MonacoFEELInitializer feelInitializer;
    private final DRDNameChanger drdNameChanger;
    private final LazyCanvasFocusUtils lazyCanvasFocusUtils;
    private final DMNDiagramsSession diagramsSession;
    private final DecisionNavigatorPresenter decisionNavigatorPresenter;

    @Inject
    public DMNDiagramEditor(final View view,
                            final Event<OnDiagramFocusEvent> onDiagramFocusEvent,
                            final Event<OnDiagramLoseFocusEvent> onDiagramLostFocusEvent,
                            final @DMNEditor DocumentationView documentationView,
                            final DMNDiagramResourceType resourceType,
                            final DMNEditorMenuSessionItems menuSessionItems,
                            final ProjectMessagesListener projectMessagesListener,
                            final ClientTranslationService translationService,
                            final @DMNEditor ClientProjectDiagramService projectDiagramServices,
                            final Caller<ProjectDiagramResourceService> projectDiagramResourceServiceCaller,
                            final StunnerEditor stunnerEditor,
                            final SessionManager sessionManager,
                            final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                            final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                            final DecisionNavigatorDock decisionNavigatorDock,
                            final LayoutHelper layoutHelper,
                            final OpenDiagramLayoutExecutor openDiagramLayoutExecutor,
                            final DataTypesPage dataTypesPage,
                            final IncludedModelsPage includedModelsPage,
                            final DMNEditorSearchIndex editorSearchIndex,
                            final SearchBarComponent<DMNSearchableElement> searchBarComponent,
                            final MonacoFEELInitializer feelInitializer,
                            final DRDNameChanger drdNameChanger,
                            final LazyCanvasFocusUtils lazyCanvasFocusUtils,
                            final DMNDiagramsSession diagramsSession,
                            final DecisionNavigatorPresenter decisionNavigatorPresenter) {
        super(view,
              onDiagramFocusEvent,
              onDiagramLostFocusEvent,
              documentationView,
              resourceType,
              menuSessionItems,
              projectMessagesListener,
              translationService,
              projectDiagramServices,
              projectDiagramResourceServiceCaller,
              stunnerEditor);
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.refreshFormPropertiesEvent = refreshFormPropertiesEvent;
        this.decisionNavigatorDock = decisionNavigatorDock;
        this.layoutHelper = layoutHelper;
        this.openDiagramLayoutExecutor = openDiagramLayoutExecutor;
        this.dataTypesPage = dataTypesPage;
        this.includedModelsPage = includedModelsPage;
        this.editorSearchIndex = editorSearchIndex;
        this.searchBarComponent = searchBarComponent;
        this.feelInitializer = feelInitializer;
        this.drdNameChanger = drdNameChanger;
        this.lazyCanvasFocusUtils = lazyCanvasFocusUtils;
        this.diagramsSession = diagramsSession;
        this.decisionNavigatorPresenter = decisionNavigatorPresenter;
    }

    @Override
    @PostConstruct
    public void init() {
        super.init();
        getMenuSessionItems().setErrorConsumer(e -> hideLoadingViews());
        editorSearchIndex.setCurrentAssetHashcodeSupplier(getGetCurrentContentHashSupplier());
        editorSearchIndex.setIsDataTypesTabActiveSupplier(getIsDataTypesTabActiveSupplier());
    }

    private void setupSessionHeaderContainer() {
        SessionDiagramPresenter presenter = getStunnerEditor().getPresenter();
        drdNameChanger.setSessionPresenterView(presenter.getView());
        presenter.getView().setSessionHeaderContainer(getWidget(drdNameChanger.getElement()));
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        super.doStartUp(path, place);
        decisionNavigatorDock.init(PerspectiveIds.LIBRARY);
    }

    @Override
    public void initialiseKieEditorForSession(final ProjectDiagram diagram) {
        onDiagramLoad();

        superInitialiseKieEditorForSession(diagram);

        kieView.getMultiPage().addPage(dataTypesPage);
        kieView.getMultiPage().addPage(includedModelsPage);

        kieView.addOverviewPage(overviewWidget,
                                () -> overviewWidget.refresh(versionRecordManager.getVersion()));

        setupSearchComponent();
    }

    void superInitialiseKieEditorForSession(final ProjectDiagram diagram) {
        super.initialiseKieEditorForSession(diagram);
    }

    @Override
    protected void resetEditorPages(final Overview overview) {
        overviewWidget.setContent(overview,
                                  versionRecordManager.getPathToLatest());

        resetMetadata(overview);

        kieView.clear();
        kieView.addMainEditorPage(baseView);
    }

    @Override
    protected void resetEditorPagesOnLoadError(final Overview overview) {
        super.resetEditorPages(overview);
    }

    @Override
    public void showDocks() {
        super.showDocks();
        decisionNavigatorDock.open();
    }

    @Override
    public Annotation[] getDockQualifiers() {
        //GWT really hates this being a lamda. Keep as an anonymous inner class!
        return new Annotation[]{new Annotation() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return DMNEditor.class;
            }
        }};
    }

    @Override
    public void hideDocks() {
        super.hideDocks();
        decisionNavigatorDock.close();
        decisionNavigatorDock.resetContent();
    }

    public void onMultiPageEditorSelectedPageEvent(final @Observes MultiPageEditorSelectedPageEvent event) {
        if (isSameSession()) {
            searchBarComponent.disableSearch();
        }
    }

    public void onRefreshFormPropertiesEvent(final @Observes RefreshFormPropertiesEvent event) {
        if (isSameSession()) {
            searchBarComponent.disableSearch();
        }
    }

    Supplier<Boolean> getIsDataTypesTabActiveSupplier() {
        return () -> {
            final int selectedPageIndex = kieView.getMultiPage().selectedPage();
            return selectedPageIndex == DATA_TYPES_PAGE_INDEX;
        };
    }

    Supplier<Integer> getGetCurrentContentHashSupplier() {
        return this::getCurrentContentHash;
    }

    @Override
    protected Integer getCurrentContentHash() {
        if (Objects.isNull(diagramsSession.getDMNDiagrams()) ||
                diagramsSession.getDMNDiagrams().isEmpty()) {
            return super.getCurrentContentHash();
        }
        int hash = 0;
        for (final DMNDiagramTuple dmnDiagram : diagramsSession.getDMNDiagrams()) {
            hash = HashUtil.combineHashCodes(hash,
                                             dmnDiagram.getStunnerDiagram().hashCode(),
                                             dmnDiagram.getDMNDiagram().hashCode());
        }
        return hash;
    }

    /**
     * Stunner validates diagrams before saving them. If a {@see Violation.Type.ERROR} is reported by the underlying
     * validation implementation Stunner prevents saving of the diagram. DMN's validation reports errors for states
     * that can be successfully saved as they represent a partially authored diagram. Therefore override Stunners
     * behavior and prevent saving of DMN diagrams containing errors.
     */
    @Override
    public boolean isSaveAllowedAfterValidationFailed(Violation.Type maxSeverity) {
        return true;
    }

    void setupSearchComponent() {
        final HTMLElement element = searchBarComponent.getView().getElement();

        searchBarComponent.init(editorSearchIndex);
        kieView.getMultiPage().addTabBarWidget(getWidget(element));
    }

    public void onDataTypePageNavTabActiveEvent(final @Observes DataTypePageTabActiveEvent event) {
        kieView.getMultiPage().selectPage(DATA_TYPES_PAGE_INDEX);
    }

    @SuppressWarnings("all")
    @Override
    public void open(final ProjectDiagram diagram,
                     final SessionPresenter.SessionPresenterCallback callback) {
        final AbstractSession currentSession = !getStunnerEditor().isClosed() ? (AbstractSession) getStunnerEditor().getSession() : null;
        this.layoutHelper.applyLayout(diagram, openDiagramLayoutExecutor);

        feelInitializer.initializeFEELEditor();
        decisionNavigatorPresenter.setIsRefreshComponentsViewSuspended(true);
        super.open(diagram, new SessionPresenter.SessionPresenterCallback() {
            @Override
            public void onSuccess() {
                setupSessionHeaderContainer();
                callback.onSuccess();
                if (null != currentSession) {
                    currentSession.close();
                }
                decisionNavigatorPresenter.setIsRefreshComponentsViewSuspended(false);
                decisionNavigatorPresenter.refreshComponentsView();
            }

            @Override
            public void onError(ClientRuntimeError error) {
                decisionNavigatorPresenter.setIsRefreshComponentsViewSuspended(false);
                callback.onError(error);
            }
        });
    }

    @OnOpen
    public void onOpen() {
    }

    @OnClose
    @Override
    public void onClose() {
        super.doClose();
        dataTypesPage.disableShortcuts();
        super.onClose();
    }

    void onDiagramLoad() {
        if (null != getStunnerEditor().getCanvasHandler()) {
            final ExpressionEditorView.Presenter expressionEditor = ((DMNSession) getStunnerEditor().getSession()).getExpressionEditor();
            expressionEditor.setToolbarStateHandler(new DMNProjectToolbarStateHandler(getMenuSessionItems()));
            decisionNavigatorDock.reload();
            dataTypesPage.reload();
            includedModelsPage.reload();
            lazyCanvasFocusUtils.releaseFocus();
        }
    }

    @OnFocus
    public void onFocus() {
        if (!getStunnerEditor().isClosed()) {
            getStunnerEditor().focus();
            onDiagramLoad();
        }
        dataTypesPage.onFocus();
        dataTypesPage.enableShortcuts();
    }

    @OnLostFocus
    public void onLostFocus() {
        if (!getStunnerEditor().isClosed()) {
            getStunnerEditor().lostFocus();
        }
        dataTypesPage.onLostFocus();
    }

    @Override
    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @Override
    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @Override
    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        super.getMenus(menusConsumer);
    }

    @Override
    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }

    @OnMayClose
    public boolean onMayClose() {
        return super.mayClose(getStunnerEditor().getCurrentContentHash());
    }

    @Override
    public String getEditorIdentifier() {
        return EDITOR_ID;
    }

    public void onDataTypeEditModeToggle(final @Observes DataTypeEditModeToggleEvent event) {
        /* Delaying the 'onDataTypeEditModeToggleCallback' since external events
         * refresh the menu widget and override this change. */
        setTimeout(getOnDataTypeEditModeToggleCallback(event), 250);
    }

    DomGlobal.SetTimeoutCallbackFn getOnDataTypeEditModeToggleCallback(final DataTypeEditModeToggleEvent event) {
        return (e) -> {
            if (event.isEditModeEnabled()) {
                disableMenuItem(MenuItems.SAVE);
            } else {
                enableMenuItem(MenuItems.SAVE);
            }
        };
    }

    void onEditExpressionEvent(final @Observes EditExpressionEvent event) {
        searchBarComponent.disableSearch();
        if (isSameSession(event.getSession())) {
            final DMNSession session = (DMNSession) getStunnerEditor().getSession();
            final ExpressionEditorView.Presenter expressionEditor = session.getExpressionEditor();
            sessionCommandManager.execute(session.getCanvasHandler(),
                                          new NavigateToExpressionEditorCommand(expressionEditor,
                                                                                getStunnerEditor().getPresenter(),
                                                                                sessionManager,
                                                                                sessionCommandManager,
                                                                                refreshFormPropertiesEvent,
                                                                                event.getNodeUUID(),
                                                                                event.getHasExpression(),
                                                                                event.getHasName(),
                                                                                event.isOnlyVisualChangeAllowed()));
        }
    }

    ElementWrapperWidget<?> getWidget(final HTMLElement element) {
        return ElementWrapperWidget.getWidget(element);
    }

    private boolean isSameSession() {
        return isSameSession(sessionManager.getCurrentSession());
    }
}
