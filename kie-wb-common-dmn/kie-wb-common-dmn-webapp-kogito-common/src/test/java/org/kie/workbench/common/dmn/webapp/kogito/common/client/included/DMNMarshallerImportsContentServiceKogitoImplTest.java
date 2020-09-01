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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.included;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.promise.Promise;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.kogito.webapp.base.client.workarounds.KogitoResourceContentService;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.webapp.kogito.common.client.included.DMNMarshallerImportsContentServiceKogitoImpl.DMN_FILES_PATTERN;
import static org.kie.workbench.common.dmn.webapp.kogito.common.client.included.DMNMarshallerImportsContentServiceKogitoImpl.MODEL_FILES_PATTERN;
import static org.kie.workbench.common.dmn.webapp.kogito.common.client.included.DMNMarshallerImportsContentServiceKogitoImpl.PMML_FILES_PATTERN;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNMarshallerImportsContentServiceKogitoImplTest {

    @Mock
    private KogitoResourceContentService contentService;

    private DMNMarshallerImportsContentServiceKogitoImpl service;

    @Before
    public void setup() {
        service = new DMNMarshallerImportsContentServiceKogitoImpl(contentService);
    }

    @Test
    public void testLoadFile() {
        final String file = "file.dmn";
        final Promise<String> expected = makePromise();
        when(contentService.loadFile(file)).thenReturn(expected);
        final Promise<String> actual = service.loadFile(file);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetModelsURIs() {
        final Promise<String[]> expected = makePromise();
        when(contentService.getFilteredItems(eq(MODEL_FILES_PATTERN), any())).thenReturn(expected);
        final Promise<String[]> actual = service.getModelsURIs();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetModelsDMNFilesURIs() {
        final Promise<String[]> expected = makePromise();
        when(contentService.getFilteredItems(eq(DMN_FILES_PATTERN), any())).thenReturn(expected);
        final Promise<String[]> actual = service.getModelsDMNFilesURIs();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetModelsPMMLFilesURIs() {
        final Promise<String[]> expected = makePromise();
        when(contentService.getFilteredItems(eq(PMML_FILES_PATTERN), any())).thenReturn(expected);
        final Promise<String[]> actual = service.getModelsPMMLFilesURIs();
        assertEquals(expected, actual);
    }

    private <T> Promise<T> makePromise() {
        return new Promise<>(null);
    }
}
