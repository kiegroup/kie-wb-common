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

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.review;

import java.util.Optional;

import javax.enterprise.event.Event;

import elemental2.dom.HTMLElement;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.changerequest.ChangeRequest;
import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.guvnor.structure.repositories.changerequest.ChangeRequestStatus;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.ChangeRequestUtils;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.review.tab.changedfiles.ChangedFilesScreenPresenter;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.review.tab.overview.OverviewScreenPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.promise.SyncPromises;
import org.uberfire.spaces.Space;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ChangeRequestReviewScreenPresenterTest {

    private ChangeRequestReviewScreenPresenter presenter;

    @Mock
    private ChangeRequestReviewScreenPresenter.View view;

    @Mock
    private TranslationService ts;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private ChangeRequestService changeRequestService;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock
    private OverviewScreenPresenter overviewScreen;

    @Mock
    private ChangedFilesScreenPresenter changedFilesScreen;

    private Promises promises;

    @Mock
    private ProjectController projectController;

    @Mock
    private Event<NotificationEvent> notificationEvent;

    @Mock
    private WorkspaceProject workspaceProject;

    @Mock
    private Branch branch;

    @Mock
    private ChangeRequest changeRequest;

    @Before
    public void setUp() {
        promises = new SyncPromises();

        Repository repository = mock(Repository.class);

        doReturn(workspaceProject).when(libraryPlaces).getActiveWorkspace();
        doReturn(mock(KieModule.class)).when(workspaceProject).getMainModule();
        doReturn(repository).when(workspaceProject).getRepository();
        doReturn(mock(Space.class)).when(workspaceProject).getSpace();
        doReturn(Optional.of(branch)).when(repository).getBranch("branch");

        this.presenter = spy(new ChangeRequestReviewScreenPresenter(view,
                                                                    ts,
                                                                    libraryPlaces,
                                                                    new CallerMock<>(changeRequestService),
                                                                    busyIndicatorView,
                                                                    overviewScreen,
                                                                    changedFilesScreen,
                                                                    promises,
                                                                    projectController,
                                                                    notificationEvent));
    }

    @Test
    public void postConstructTest() {
        presenter.postConstruct();

        verify(view).init(presenter);
        verify(view).setTitle(anyString());
    }

    @Test
    public void onLostFocusTest() {
        presenter.onLostFocus();

        verify(overviewScreen).reset();
        verify(changedFilesScreen).reset();
    }

    @Test
    public void refreshOnFocusOtherPlaceTest() {
        PlaceRequest place = mock(PlaceRequest.class);
        SelectPlaceEvent event = new SelectPlaceEvent(place);

        doReturn(LibraryPlaces.PROJECT_SCREEN).when(place).getIdentifier();

        presenter.refreshOnFocus(event);

        verify(changeRequestService, never()).getChangeRequest(anyString(),
                                                               anyString(),
                                                               anyLong());
    }

    @Test
    public void refreshOnFocusGoToScreenCanReadTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        ChangeRequestReviewScreenPresenter.class.getDeclaredField("workspaceProject"))
                .set(workspaceProject);

        PlaceRequest place = mock(PlaceRequest.class);
        SelectPlaceEvent event = new SelectPlaceEvent(place);

        doReturn(LibraryPlaces.CHANGE_REQUEST_REVIEW).when(place).getIdentifier();
        doReturn("1").when(place).getParameter(ChangeRequestUtils.CHANGE_REQUEST_ID_KEY, null);

        doReturn(changeRequest).when(changeRequestService).getChangeRequest(anyString(),
                                                                            anyString(),
                                                                            anyLong());
        doReturn("branch").when(changeRequest).getTargetBranch();

        doReturn(promises.resolve(true)).when(projectController)
                .canReadBranch(workspaceProject,
                               "branch");

        doReturn(promises.resolve(false)).when(projectController)
                .canUpdateBranch(workspaceProject,
                                 branch);

        doReturn(mock(OverviewScreenPresenter.View.class)).when(overviewScreen).getView();

        presenter.refreshOnFocus(event);

        verify(overviewScreen).reset();
        verify(changedFilesScreen).reset();
        verify(changeRequestService).getChangeRequest(anyString(),
                                                      anyString(),
                                                      anyLong());
        verify(view).activateOverviewTab();
        verify(view, never()).showAcceptButton(true);
        verify(overviewScreen).reset();
        verify(changedFilesScreen).reset();
    }

    @Test
    public void refreshOnFocusGoToScreenCanUpdateTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        ChangeRequestReviewScreenPresenter.class.getDeclaredField("workspaceProject"))
                .set(workspaceProject);

        PlaceRequest place = mock(PlaceRequest.class);
        SelectPlaceEvent event = new SelectPlaceEvent(place);

        doReturn(ChangeRequestStatus.OPEN).when(changeRequest).getStatus();

        doReturn(LibraryPlaces.CHANGE_REQUEST_REVIEW).when(place).getIdentifier();
        doReturn("1").when(place).getParameter(ChangeRequestUtils.CHANGE_REQUEST_ID_KEY, null);

        doReturn(changeRequest).when(changeRequestService).getChangeRequest(anyString(),
                                                                            anyString(),
                                                                            anyLong());
        doReturn("branch").when(changeRequest).getTargetBranch();

        doReturn(promises.resolve(true)).when(projectController)
                .canReadBranch(workspaceProject,
                               "branch");

        doReturn(promises.resolve(true)).when(projectController)
                .canUpdateBranch(workspaceProject,
                                 branch);

        doReturn(mock(OverviewScreenPresenter.View.class)).when(overviewScreen).getView();

        presenter.refreshOnFocus(event);

        verify(overviewScreen).reset();
        verify(changedFilesScreen).reset();
        verify(changeRequestService).getChangeRequest(anyString(),
                                                      anyString(),
                                                      anyLong());
        verify(view).activateOverviewTab();
        verify(view, times(2)).showAcceptButton(anyBoolean());
        verify(overviewScreen).reset();
        verify(changedFilesScreen).reset();
    }

    @Test
    public void showOverviewContentTest() {
        doReturn(mock(OverviewScreenPresenter.View.class)).when(overviewScreen).getView();

        presenter.showOverviewContent();

        verify(view).setContent(any(HTMLElement.class));
    }

    @Test
    public void showChangedFilesContentTest() {
        doReturn(mock(ChangedFilesScreenPresenter.View.class)).when(changedFilesScreen).getView();

        presenter.showChangedFilesContent();

        verify(view).setContent(any(HTMLElement.class));
    }

    @Test
    public void cancelTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        ChangeRequestReviewScreenPresenter.class.getDeclaredField("workspaceProject"))
                .set(workspaceProject);

        presenter.cancel();

        verify(libraryPlaces).goToProject(workspaceProject);
    }

    @Test
    public void rejectWhenHasPermissionTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        ChangeRequestReviewScreenPresenter.class.getDeclaredField("workspaceProject"))
                .set(workspaceProject);

        new FieldSetter(presenter,
                        ChangeRequestReviewScreenPresenter.class.getDeclaredField("currentTargetBranch"))
                .set(branch);

        doReturn(promises.resolve(true)).when(projectController)
                .canUpdateBranch(workspaceProject,
                                 branch);

        presenter.reject();

        verify(changeRequestService).rejectChangeRequest(anyString(),
                                                         anyString(),
                                                         anyLong());
        verify(notificationEvent).fire(any(NotificationEvent.class));
    }

    @Test
    public void rejectWhenNotAllowedTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        ChangeRequestReviewScreenPresenter.class.getDeclaredField("workspaceProject"))
                .set(workspaceProject);

        new FieldSetter(presenter,
                        ChangeRequestReviewScreenPresenter.class.getDeclaredField("currentTargetBranch"))
                .set(branch);

        doReturn(promises.resolve(false)).when(projectController)
                .canUpdateBranch(workspaceProject,
                                 branch);

        presenter.reject();

        verify(changeRequestService, never()).rejectChangeRequest(anyString(),
                                                                  anyString(),
                                                                  anyLong());
    }

    @Test
    public void acceptWhenHasPermissionTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        ChangeRequestReviewScreenPresenter.class.getDeclaredField("workspaceProject"))
                .set(workspaceProject);

        new FieldSetter(presenter,
                        ChangeRequestReviewScreenPresenter.class.getDeclaredField("currentTargetBranch"))
                .set(branch);

        doReturn(promises.resolve(true)).when(projectController)
                .canUpdateBranch(workspaceProject,
                                 branch);

        doReturn(true).when(changeRequestService)
                .acceptChangeRequest(anyString(),
                                     anyString(),
                                     anyLong());

        presenter.accept();

        verify(changeRequestService).acceptChangeRequest(anyString(),
                                                         anyString(),
                                                         anyLong());
        verify(notificationEvent).fire(any(NotificationEvent.class));
    }

    @Test
    public void acceptWhenNotAllowedTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        ChangeRequestReviewScreenPresenter.class.getDeclaredField("workspaceProject"))
                .set(workspaceProject);

        new FieldSetter(presenter,
                        ChangeRequestReviewScreenPresenter.class.getDeclaredField("currentTargetBranch"))
                .set(branch);

        doReturn(promises.resolve(false)).when(projectController)
                .canUpdateBranch(workspaceProject,
                                 branch);

        presenter.accept();

        verify(changeRequestService, never()).acceptChangeRequest(anyString(),
                                                                  anyString(),
                                                                  anyLong());
    }

    @Test
    public void revertWhenHasPermissionTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        ChangeRequestReviewScreenPresenter.class.getDeclaredField("workspaceProject"))
                .set(workspaceProject);

        new FieldSetter(presenter,
                        ChangeRequestReviewScreenPresenter.class.getDeclaredField("currentTargetBranch"))
                .set(branch);

        doReturn(promises.resolve(true)).when(projectController)
                .canUpdateBranch(workspaceProject,
                                 branch);

        doReturn(true).when(changeRequestService)
                .revertChangeRequest(anyString(),
                                     anyString(),
                                     anyLong());

        presenter.revert();

        verify(changeRequestService).revertChangeRequest(anyString(),
                                                         anyString(),
                                                         anyLong());
        verify(notificationEvent).fire(any(NotificationEvent.class));
    }

    @Test
    public void revertWhenNotAllowedTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        ChangeRequestReviewScreenPresenter.class.getDeclaredField("workspaceProject"))
                .set(workspaceProject);

        new FieldSetter(presenter,
                        ChangeRequestReviewScreenPresenter.class.getDeclaredField("currentTargetBranch"))
                .set(branch);

        doReturn(promises.resolve(false)).when(projectController)
                .canUpdateBranch(workspaceProject,
                                 branch);

        presenter.revert();

        verify(changeRequestService, never()).revertChangeRequest(anyString(),
                                                                  anyString(),
                                                                  anyLong());
    }
}