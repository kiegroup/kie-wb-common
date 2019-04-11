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

package org.kie.workbench.common.dmn.client.api.included.legacy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.Import;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludeModelsService;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedNode;
import org.kie.workbench.common.dmn.api.editors.types.DMNIncludeModel;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNIncludeModelsClientTest {

    @Mock
    private CallerMock<DMNIncludeModelsService> service;

    @Mock
    private WorkspaceProjectContext projectContext;

    @Mock
    private Consumer<List<DMNIncludeModel>> listConsumerDMNModels;

    @Mock
    private Consumer<List<DMNIncludedNode>> listConsumerDMNNodes;

    @Mock
    private ErrorCallback<Object> onError;

    @Mock
    private RemoteCallback<List<DMNIncludeModel>> onSuccess;

    @Mock
    private DMNIncludeModelsService dmnService;

    @Mock
    private WorkspaceProject workspaceProject;

    private DMNIncludeModelsClient client;

    @Before
    public void setup() {
        client = spy(new DMNIncludeModelsClient(service, projectContext));
    }

    @Test
    public void testLoadModels() {

        final Optional<WorkspaceProject> optionalWorkspaceProject = Optional.of(workspaceProject);

        doReturn(onSuccess).when(client).onSuccess(any());
        doReturn(onError).when(client).onError(any());
        when(service.call(onSuccess, onError)).thenReturn(dmnService);
        when(projectContext.getActiveWorkspaceProject()).thenReturn(optionalWorkspaceProject);

        client.loadModels(listConsumerDMNModels);

        verify(dmnService).loadModels(workspaceProject);
    }

    @Test
    public void testLoadNodesFromImports() {

        final Optional<WorkspaceProject> optionalWorkspaceProject = Optional.of(workspaceProject);
        final Import anImport1 = mock(Import.class);
        final Import anImport2 = mock(Import.class);
        final List<Import> imports = asList(anImport1, anImport2);

        doReturn(onSuccess).when(client).onSuccess(any());
        doReturn(onError).when(client).onError(any());
        when(service.call(onSuccess, onError)).thenReturn(dmnService);
        when(projectContext.getActiveWorkspaceProject()).thenReturn(optionalWorkspaceProject);

        client.loadNodesFromImports(imports, listConsumerDMNNodes);

        verify(dmnService).loadNodesFromImports(workspaceProject, imports);
    }

    @Test
    public void testOnError() {

        final boolean error = true;
        final Throwable throwable = mock(Throwable.class);
        doNothing().when(client).warn(any());

        final boolean result = client.onError(listConsumerDMNModels).error(error, throwable);

        assertFalse(result);
        verify(client).warn(eq("[WARNING] DMNIncludeModelsClient could not get the asset list."));
        verify(listConsumerDMNModels).accept(eq(new ArrayList<>()));
    }

    @Test
    public void testOnSuccess() {
        final List<DMNIncludeModel> dmnIncludeModels = new ArrayList<>();

        client.onSuccess(listConsumerDMNModels).callback(dmnIncludeModels);

        verify(listConsumerDMNModels).accept(dmnIncludeModels);
    }
}
