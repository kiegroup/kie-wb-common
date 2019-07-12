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

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.diff;

import java.util.HashSet;
import java.util.Set;

import elemental2.dom.HTMLElement;
import org.guvnor.structure.repositories.changerequest.ChangeRequestDiff;
import org.guvnor.structure.repositories.changerequest.ChangeType;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.ResourceTypeManagerCache;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.diff.DiffMode;
import org.uberfire.workbench.type.ResourceTypeDefinition;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DiffItemPresenterTest {

    private DiffItemPresenter presenter;

    @Mock
    private DiffItemPresenter.View view;

    @Mock
    private ResourceTypeManagerCache resourceTypeManagerCache;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private TranslationService ts;

    @Mock
    private ChangeRequestDiff diff;

    @Mock
    private Path oldFilePath;

    @Mock
    private Path newFilePath;

    @Before
    public void setUp() {
        doReturn(oldFilePath).when(diff).getOldFilePath();
        doReturn(newFilePath).when(diff).getNewFilePath();
        doReturn("my/old/file").when(oldFilePath).getFileName();
        doReturn("my/new/file").when(newFilePath).getFileName();

        presenter = spy(new DiffItemPresenter(view,
                                              resourceTypeManagerCache,
                                              placeManager,
                                              ts));
    }

    @Test
    public void postConstructTest() {
        presenter.postConstruct();

        verify(view).init(presenter);
    }

    @Test
    public void preDestroyTextualDiffTest() throws NoSuchFieldException {
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("diffMode")).set(DiffMode.TEXTUAL);

        presenter.preDestroy();

        verify(placeManager, never()).closePlace(any(PlaceRequest.class));
    }

    @Test
    public void preDestroyVisualDiffTest() throws NoSuchFieldException {
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("diffMode")).set(DiffMode.VISUAL);
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("placeRequestCustomLeft")).set(mock(PlaceRequest.class));
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("placeRequestCustomRight")).set(mock(PlaceRequest.class));
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("ready")).set(true);
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("open")).set(true);

        presenter.preDestroy();

        verify(placeManager, times(2)).closePlace(any(PlaceRequest.class));
    }

    @Test
    public void preDestroyVisualDiffOnlyLeftTest() throws NoSuchFieldException {
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("diffMode")).set(DiffMode.VISUAL);
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("placeRequestCustomLeft")).set(mock(PlaceRequest.class));
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("ready")).set(true);
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("open")).set(true);

        presenter.preDestroy();

        verify(placeManager, times(1)).closePlace(any(PlaceRequest.class));
    }

    @Test
    public void preDestroyVisualDiffOnlyRightTest() throws NoSuchFieldException {
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("diffMode")).set(DiffMode.VISUAL);
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("placeRequestCustomRight")).set(mock(PlaceRequest.class));
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("ready")).set(true);
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("open")).set(true);

        presenter.preDestroy();

        verify(placeManager, times(1)).closePlace(any(PlaceRequest.class));
    }

    @Test(expected = IllegalStateException.class)
    public void drawDoNothingWhenNotReadyTest() throws NoSuchFieldException {
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("diffMode")).set(DiffMode.TEXTUAL);
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("ready")).set(false);

        presenter.draw();

        verify(view, never()).drawTextualContent(anyString(),
                                                 anyBoolean());
    }

    @Test
    public void drawWhenTextualDiffTest() throws NoSuchFieldException {
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("diffMode")).set(DiffMode.TEXTUAL);
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("ready")).set(true);
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("open")).set(true);
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("diff")).set(diff);

        doReturn(10).when(diff).getAddedLinesCount();

        presenter.draw();

        verify(view).drawTextualContent(anyString(),
                                        anyBoolean());
    }

    @Test
    public void drawWhenVisualDiffTest() throws NoSuchFieldException {
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("diffMode")).set(DiffMode.VISUAL);
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("ready")).set(true);
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("open")).set(true);
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("diff")).set(diff);

        doReturn(mock(PathPlaceRequest.class)).when(presenter).createPlaceRequest(oldFilePath);
        doReturn(mock(PathPlaceRequest.class)).when(presenter).createPlaceRequest(newFilePath);

        presenter.draw();

        verify(view).getCustomLeftContainer();
        verify(view).getCustomRightContainer();
        verify(placeManager, times(2)).goTo(any(PlaceRequest.class), any(HTMLElement.class));
    }

    @Test
    public void drawWhenAddTypeAndVisualDiffTest() throws NoSuchFieldException {
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("diffMode")).set(DiffMode.VISUAL);
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("ready")).set(true);
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("open")).set(true);
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("diff")).set(diff);

        doReturn(ChangeType.ADD).when(diff).getChangeType();
        doReturn("Added").when(ts).getTranslation(LibraryConstants.Added);

        doReturn(mock(PathPlaceRequest.class)).when(presenter).createPlaceRequest(newFilePath);

        presenter.draw();

        verify(view, never()).getCustomLeftContainer();
        verify(view).getCustomRightContainer();
        verify(placeManager, times(1)).goTo(any(PlaceRequest.class), any(HTMLElement.class));
    }

    @Test
    public void drawWhenDeleteTypeAndVisualDiffTest() throws NoSuchFieldException {
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("diffMode")).set(DiffMode.VISUAL);
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("ready")).set(true);
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("open")).set(true);
        new FieldSetter(presenter, DiffItemPresenter.class.getDeclaredField("diff")).set(diff);

        doReturn(ChangeType.DELETE).when(diff).getChangeType();
        doReturn("Deleted").when(ts).getTranslation(LibraryConstants.Deleted);

        doReturn(mock(PathPlaceRequest.class)).when(presenter).createPlaceRequest(oldFilePath);

        presenter.draw();

        verify(view).getCustomLeftContainer();
        verify(view, never()).getCustomRightContainer();
        verify(placeManager, times(1)).goTo(any(PlaceRequest.class), any(HTMLElement.class));
    }

    @Test
    public void setupTextualDiffTest() {
        doReturn(ChangeType.ADD).when(diff).getChangeType();

        presenter.setup(diff);

        verify(view, times(1)).setupTextualContent(anyString(),
                                                   anyString(),
                                                   anyBoolean());
    }

    @Test
    public void setupTextualDiffWhenAddTypeTest() {
        doReturn(ChangeType.ADD).when(diff).getChangeType();
        doReturn("Added").when(ts).getTranslation(LibraryConstants.Added);

        presenter.setup(diff);

        verify(diff, times(1)).getOldFilePath();
        verify(diff, times(2)).getNewFilePath();
        verify(resourceTypeManagerCache).getResourceTypeDefinitions();
        verify(view, times(1)).setupTextualContent(eq("my/new/file"),
                                                   eq("Added"),
                                                   anyBoolean());
    }

    @Test
    public void setupTextualDiffWhenDeleteTypeTest() {
        doReturn(ChangeType.DELETE).when(diff).getChangeType();
        doReturn("Deleted").when(ts).getTranslation(LibraryConstants.Deleted);

        presenter.setup(diff);

        verify(diff, times(2)).getOldFilePath();
        verify(diff, times(1)).getNewFilePath();
        verify(resourceTypeManagerCache).getResourceTypeDefinitions();
        verify(view, times(1)).setupTextualContent(eq("my/old/file"),
                                                   eq("Deleted"),
                                                   anyBoolean());
    }

    @Test
    public void setupTextualDiffWhenModifyTypeTest() {
        doReturn(ChangeType.MODIFY).when(diff).getChangeType();
        doReturn("Updated").when(ts).getTranslation(LibraryConstants.Updated);

        presenter.setup(diff);
        verify(diff, times(2)).getOldFilePath();
        verify(diff, times(1)).getNewFilePath();
        verify(resourceTypeManagerCache).getResourceTypeDefinitions();
        verify(view, times(1)).setupTextualContent(eq("my/old/file"),
                                                   eq("Updated"),
                                                   anyBoolean());
    }

    @Test
    public void setupTextualDiffWhenCopyTypeTest() {
        doReturn(ChangeType.COPY).when(diff).getChangeType();
        doReturn("Copied").when(ts).getTranslation(LibraryConstants.Copied);

        presenter.setup(diff);

        verify(diff, times(2)).getOldFilePath();
        verify(diff, times(1)).getNewFilePath();
        verify(resourceTypeManagerCache).getResourceTypeDefinitions();
        verify(view, times(1)).setupTextualContent(eq("my/old/file -> my/new/file"),
                                                   eq("Copied"),
                                                   anyBoolean());
    }

    @Test
    public void setupTextualDiffWhenRenameTypeTest() {
        doReturn(ChangeType.RENAME).when(diff).getChangeType();
        doReturn("Renamed").when(ts).getTranslation(LibraryConstants.Renamed);

        presenter.setup(diff);

        verify(diff, times(2)).getOldFilePath();
        verify(diff, times(1)).getNewFilePath();
        verify(resourceTypeManagerCache).getResourceTypeDefinitions();
        verify(view, times(1)).setupTextualContent(eq("my/old/file -> my/new/file"),
                                                   eq("Renamed"),
                                                   anyBoolean());
    }

    @Test
    public void setupTextualTest() {
        doReturn(ChangeType.ADD).when(diff).getChangeType();
        ResourceTypeDefinition resourceTypeDefinition = mock(ResourceTypeDefinition.class);

        doReturn(true).when(resourceTypeDefinition).accept(any());
        doReturn(DiffMode.TEXTUAL).when(resourceTypeDefinition).getDiffMode();

        Set<ResourceTypeDefinition> resourceTypeDefinitions = new HashSet<ResourceTypeDefinition>() {{
            add(resourceTypeDefinition);
        }};

        doReturn(resourceTypeDefinitions).when(resourceTypeManagerCache).getResourceTypeDefinitions();

        presenter.setup(diff);

        verify(view, times(1)).setupTextualContent(anyString(),
                                                   anyString(),
                                                   anyBoolean());
    }
}
