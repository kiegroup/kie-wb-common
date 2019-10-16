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
package org.kie.workbench.common.dmn.showcase.client.navigator;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.showcase.client.editor.DMNDiagramEditor;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.mvp.PlaceManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.dmn.showcase.client.navigator.DMNVFSService.DIAGRAM_EDITOR;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNVFSServiceTest {

    private final String CONTENT = "xml-content-of-dmn-file";

    @Mock
    private PlaceManager placeManager;

    @Mock
    private Caller<VFSService> vfsServiceCaller;

    @Mock
    private VFSService vfsService;

    @Mock
    private Path path;

    @Mock
    private ServiceCallback<String> callback;

    @Captor
    private ArgumentCaptor<RemoteCallback<String>> remoteCallbackStringArgumentCaptor;

    @Captor
    private ArgumentCaptor<RemoteCallback<Path>> remoteCallbackPathArgumentCaptor;

    private DMNVFSService dmnvfsService;

    @Before
    public void setup() {
        this.dmnvfsService = new DMNVFSService(placeManager,
                                               vfsServiceCaller);

        when(vfsServiceCaller.call(any(RemoteCallback.class))).thenReturn(vfsService);
    }

    @Test
    public void testNewFile() {
        DIAGRAM_EDITOR.addParameter("p1", "value");

        dmnvfsService.newFile();

        assertThat(DIAGRAM_EDITOR.getParameters()).isEmpty();
        verify(placeManager).goTo(DIAGRAM_EDITOR);
    }

    @Test
    public void testOpenFile() {
        dmnvfsService.openFile(path);

        verify(vfsServiceCaller).call(remoteCallbackStringArgumentCaptor.capture());
        verify(vfsService).readAllString(eq(path));

        final RemoteCallback<String> remoteCallback = remoteCallbackStringArgumentCaptor.getValue();
        assertThat(remoteCallback).isNotNull();

        remoteCallback.callback(CONTENT);

        assertThat(DIAGRAM_EDITOR.getParameter(DMNDiagramEditor.CONTENT_PARAMETER_NAME, "")).isEqualTo(CONTENT);
        verify(placeManager).goTo(DIAGRAM_EDITOR);
    }

    @Test
    public void testSaveFile() {
        dmnvfsService.saveFile(path, CONTENT, callback);

        verify(vfsServiceCaller).call(remoteCallbackPathArgumentCaptor.capture());
        verify(vfsService).write(eq(path), eq(CONTENT));

        final RemoteCallback<Path> remoteCallback = remoteCallbackPathArgumentCaptor.getValue();
        assertThat(remoteCallback).isNotNull();

        remoteCallback.callback(path);

        verify(callback).onSuccess(eq(CONTENT));
    }
}
