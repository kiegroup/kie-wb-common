/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.converters;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.appformer.kogito.bridge.client.resource.interop.ResourceListOptions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.services.DMNClientDiagramServiceImpl;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.services.PMMLMarshallerService;
import org.kie.workbench.common.kogito.webapp.base.client.workarounds.KogitoResourceContentService;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.mockito.Mock;
import org.uberfire.client.promise.Promises;
import org.uberfire.promise.SyncPromises;

import static org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.DMNMarshallerImportsHelperKogitoImpl.MODEL_FILES_PATTERN;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNMarshallerImportsHelperKogitoImplTest {

    @Mock
    private DMNClientDiagramServiceImpl dmnClientDiagramServiceMock;
    @Mock
    private DMNDiagramUtils dmnDiagramUtilsMock;
    @Mock
    private DMNIncludedNodeFactory dmnIncludedNodeFactoryMock;
    @Mock
    private KogitoResourceContentService kogitoResourceContentServiceMock;
    @Mock
    private PMMLMarshallerService pmmlMarshallerServiceMock;
    @Mock
    private ServiceCallback serviceCallbackMock;

    private static final String DMN_FILE = "test-dmn.dmn";
    private static final String DMN_PATH = "dmntest/" + DMN_FILE;
    private static final String DMN_CONTENT = "<xml> xml DMN content </xml>";

    private static final String PMML_FILE = "test-pmml.pmml";
    private static final String PMML_PATH = "dmnpmml/" + PMML_FILE;
    private static final String PMML_CONTENT = "<xml> xml PMML content </xml>";

    private DMNMarshallerImportsHelperKogitoImpl dmnMarshallerImportsHelperKogitoImpl;
    private Promises promises;

    @Before
    public void setup() {
        promises = new SyncPromises();
        dmnMarshallerImportsHelperKogitoImpl = new DMNMarshallerImportsHelperKogitoImpl(kogitoResourceContentServiceMock,
                                                                                        dmnClientDiagramServiceMock,
                                                                                        promises,
                                                                                        dmnDiagramUtilsMock,
                                                                                        dmnIncludedNodeFactoryMock,
                                                                                        pmmlMarshallerServiceMock);

    }

    @Test
    public void loadModelsDMNFile() {
        when(kogitoResourceContentServiceMock.getFilteredItems(eq(MODEL_FILES_PATTERN), isA(ResourceListOptions.class))).thenReturn(promises.resolve(new String[]{DMN_PATH}));
        when(kogitoResourceContentServiceMock.loadFile(DMN_PATH)).thenReturn(promises.resolve(DMN_CONTENT));
        dmnMarshallerImportsHelperKogitoImpl.loadModels(serviceCallbackMock);
        verify(kogitoResourceContentServiceMock, times(1)).getFilteredItems(eq(MODEL_FILES_PATTERN), isA(ResourceListOptions.class));
        verify(kogitoResourceContentServiceMock, times(1)).loadFile(eq(DMN_PATH));
        verify(dmnClientDiagramServiceMock, times(1)).transform(eq(DMN_CONTENT), isA(ServiceCallback.class));
    }

    @Test
    public void loadModelsPMMLFile() {
        when(kogitoResourceContentServiceMock.getFilteredItems(eq(MODEL_FILES_PATTERN), isA(ResourceListOptions.class))).thenReturn(promises.resolve(new String[]{PMML_PATH}));
        when(kogitoResourceContentServiceMock.loadFile(PMML_PATH)).thenReturn(promises.resolve(PMML_CONTENT));
        dmnMarshallerImportsHelperKogitoImpl.loadModels(serviceCallbackMock);
        verify(kogitoResourceContentServiceMock, times(1)).getFilteredItems(eq(MODEL_FILES_PATTERN), isA(ResourceListOptions.class));
        verify(kogitoResourceContentServiceMock, times(1)).loadFile(eq(PMML_PATH));
        verify(pmmlMarshallerServiceMock, times(1)).getDocumentMetadata(eq(PMML_PATH), eq(PMML_CONTENT));
    }

}
