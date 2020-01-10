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

package org.kie.workbench.common.kogito.webapp.base.client.workarounds;

import java.util.List;

import elemental2.promise.Promise;
import org.appformer.kogito.bridge.client.resource.ResourceContentService;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.promise.Promises;
import org.uberfire.commons.uuid.UUID;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.promise.SyncPromises;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.kogito.webapp.base.client.workarounds.KogitoResourceContentService.CONTENT_PARAMETER_NAME;
import static org.kie.workbench.common.kogito.webapp.base.client.workarounds.KogitoResourceContentService.FILE_NAME_PARAMETER_NAME;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class KogitoResourceContentServiceTest {

    private static final String EDITOR_ID = "EDITOR_ID";
    private static final String FILE_NAME = "FILE_NAME";
    private static final String ALL_PATTERN = "*";
    private static final String DMN_PATTERN = "*.dmn";
    private static final String FILE_CONTENT = "FILE_CONTENT";

    @Mock
    private PlaceManager placeManagerMock;

    @Mock
    private ResourceContentService resourceContentServiceMock;

    @Mock
    private RemoteCallback<String> callbackMock;

    private Promise<String> promiseFile;

    private Promise<String[]> promiseList;

    private String[] files;
    private String[] dmnFiles;

    private KogitoResourceContentService kogitoResourceContentService;

    private Promises promises;

    @Before
    public void setup() {
        promises = new SyncPromises();
        files = new String[6];
        for (int i = 0; i < 6; i++) {
            String suffix = i < 3 ? "scesim" : "dmn";
            files[i] = getFileUriMock(suffix);
        }
        dmnFiles = new String[3];
        System.arraycopy(files, 3, dmnFiles, 0, 3);
        doReturn(promises.resolve(FILE_CONTENT)).when(resourceContentServiceMock).get(FILE_NAME);
        doReturn(promises.resolve(files)).when(resourceContentServiceMock).list(ALL_PATTERN);
        doReturn(promises.resolve(dmnFiles)).when(resourceContentServiceMock).list(DMN_PATTERN);
        kogitoResourceContentService = new KogitoResourceContentService(placeManagerMock, resourceContentServiceMock);
    }

    @Test
    public void openFile() {
        ArgumentCaptor<PlaceRequest> placeRequestCaptor = ArgumentCaptor.forClass(PlaceRequest.class);
        kogitoResourceContentService.openFile(FILE_NAME, EDITOR_ID);
        verify(resourceContentServiceMock, times(1)).get(eq(FILE_NAME));
        verify(placeManagerMock, times(1)).goTo(placeRequestCaptor.capture());
        assertEquals(EDITOR_ID, placeRequestCaptor.getValue().getIdentifier());
        assertEquals(FILE_NAME, placeRequestCaptor.getValue().getParameter(FILE_NAME_PARAMETER_NAME, "WRONG"));
        assertEquals(FILE_CONTENT, placeRequestCaptor.getValue().getParameter(CONTENT_PARAMETER_NAME, "WRONG"));
    }

    @Test
    public void loadFile() {
        kogitoResourceContentService.loadFile(FILE_NAME, callbackMock, mock(ErrorCallback.class));
        verify(resourceContentServiceMock, times(1)).get(eq(FILE_NAME));
        verify(callbackMock, times(1)).callback(eq(FILE_CONTENT));
    }

    @Test
    public void getAllItems() {
        RemoteCallback<List<String>> testingCallback = response -> assertEquals(files.length, response.size());
        kogitoResourceContentService.getAllItems(testingCallback, mock(ErrorCallback.class));
        verify(resourceContentServiceMock, times(1)).list(eq(ALL_PATTERN));
    }

    @Test
    public void getFilteredItems() {
        RemoteCallback<List<String>> testingCallback = response -> {
            assertEquals(dmnFiles.length, response.size());
            response.forEach(fileName -> assertEquals("dmn", fileName.substring(fileName.lastIndexOf('.') + 1)));
        };
        kogitoResourceContentService.getFilteredItems(DMN_PATTERN, testingCallback, mock(ErrorCallback.class));
        verify(resourceContentServiceMock, times(1)).list(eq(DMN_PATTERN));
    }

    private String getFileUriMock(String suffix) {
        return UUID.uuid() + "." + suffix;
    }
}