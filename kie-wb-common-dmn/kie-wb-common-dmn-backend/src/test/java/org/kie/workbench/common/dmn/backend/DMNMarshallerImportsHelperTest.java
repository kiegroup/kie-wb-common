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

package org.kie.workbench.common.dmn.backend;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.dmn.model.api.DRGElement;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.Import;
import org.kie.dmn.model.v1_2.TDecision;
import org.kie.dmn.model.v1_2.TDecisionService;
import org.kie.dmn.model.v1_2.TInputData;
import org.kie.workbench.common.dmn.backend.common.DMNPathsHelper;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.uberfire.backend.vfs.PathFactory.PathImpl;

@PrepareForTest(Paths.class)
@RunWith(PowerMockRunner.class)
public class DMNMarshallerImportsHelperTest {

    @Mock
    private DMNPathsHelper pathsHelper;

    @Mock
    private WorkspaceProjectService projectService;

    @Mock
    private IOService ioService;

    @Mock
    private DMNMarshaller marshaller;

    private DMNMarshallerImportsHelper helper;

    @Before
    public void setup() {
        helper = spy(new DMNMarshallerImportsHelper(pathsHelper, projectService, ioService));
        helper.init(marshaller);
    }

    @Test
    public void testGetImportedDRGElements() {

        final Metadata metadata = mock(Metadata.class);
        final Import import1 = mock(Import.class);
        final Import import2 = mock(Import.class);
        final Import import3 = mock(Import.class);
        final List<Import> imports = asList(import1, import2, import3);
        final Definitions definitions1 = mock(Definitions.class);
        final Definitions definitions2 = mock(Definitions.class);
        final Definitions definitions3 = mock(Definitions.class);
        final List<Definitions> definitions = asList(definitions1, definitions2, definitions3);
        final DRGElement drgElement1 = mock(DRGElement.class);
        final DRGElement drgElement2 = mock(DRGElement.class);
        final DRGElement drgElement3 = mock(DRGElement.class);
        final DRGElement drgElement4 = mock(DRGElement.class);
        final DRGElement drgElement5 = mock(DRGElement.class);
        final DRGElement drgElement6 = mock(DRGElement.class);
        final DRGElement drgElement7 = mock(DRGElement.class);
        final DRGElement drgElement8 = mock(DRGElement.class);
        final DRGElement drgElement9 = mock(DRGElement.class);

        when(definitions1.getNamespace()).thenReturn("://namespace1");
        when(definitions2.getNamespace()).thenReturn("://namespace2");
        when(definitions3.getNamespace()).thenReturn("://namespace3");
        when(import1.getNamespace()).thenReturn("://namespace1");
        when(import2.getNamespace()).thenReturn("://namespace2-diff");
        when(import3.getNamespace()).thenReturn("://namespace3");
        doReturn(definitions).when(helper).getOtherDMNDiagramsDefinitions(metadata);

        doReturn(asList(drgElement1, drgElement2, drgElement3)).when(helper).getDrgElementsWithNamespace(definitions1, import1);
        doReturn(asList(drgElement4, drgElement5)).when(helper).getDrgElementsWithNamespace(definitions2, import2);
        doReturn(asList(drgElement6, drgElement7, drgElement8, drgElement9)).when(helper).getDrgElementsWithNamespace(definitions3, import3);

        final List<DRGElement> actualDRGElements = helper.getImportedDRGElements(metadata, imports);
        final List<DRGElement> expectedDRGElements = asList(drgElement1, drgElement2, drgElement3, drgElement6, drgElement7, drgElement8, drgElement9);

        assertEquals(expectedDRGElements, actualDRGElements);
    }

    @Test
    public void testGetDrgElementsWithNamespace() {

        final Import anImport = mock(Import.class);
        final Definitions definitions = mock(Definitions.class);
        final DRGElement drgElement1 = new TDecision();
        final DRGElement drgElement2 = new TInputData();
        final DRGElement drgElement3 = new TDecisionService();
        final List<DRGElement> drgElements = asList(drgElement1, drgElement2, drgElement3);

        when(anImport.getName()).thenReturn("model");
        drgElement1.setId("0000-1111");
        drgElement2.setId("2222-3333");
        drgElement3.setId("4444-5555");
        drgElement1.setName("Decision");
        drgElement2.setName("Input Data");
        drgElement3.setName("Decision Service");
        when(definitions.getDrgElement()).thenReturn(drgElements);

        final List<DRGElement> elements = helper.getDrgElementsWithNamespace(definitions, anImport);

        assertEquals(3, elements.size());
        assertEquals("model:0000-1111", elements.get(0).getId());
        assertEquals("model.Decision", elements.get(0).getName());
        assertEquals("model:2222-3333", elements.get(1).getId());
        assertEquals("model.Input Data", elements.get(1).getName());
        assertEquals("model:4444-5555", elements.get(2).getId());
        assertEquals("model.Decision Service", elements.get(2).getName());
    }

    @Test
    public void testGetOtherDMNDiagramsDefinitions() {

        final Metadata metadata = mock(Metadata.class);
        final Path path1 = makePath("../file1.dmn");
        final Path path2 = makePath("../file2.dmn");
        final Path path3 = makePath("../file3.dmn");
        final InputStreamReader inputStreamReader1 = mock(InputStreamReader.class);
        final InputStreamReader inputStreamReader2 = mock(InputStreamReader.class);
        final InputStreamReader inputStreamReader3 = mock(InputStreamReader.class);
        final Definitions definitions1 = mock(Definitions.class);
        final Definitions definitions2 = mock(Definitions.class);
        final Definitions definitions3 = mock(Definitions.class);
        final List<Path> paths = asList(path1, path2, path3);

        when(pathsHelper.getDiagramsPaths(any())).thenReturn(paths);
        when(metadata.getPath()).thenReturn(path2);
        doReturn(inputStreamReader1).when(helper).loadPath(path1);
        doReturn(inputStreamReader2).when(helper).loadPath(path2);
        doReturn(inputStreamReader3).when(helper).loadPath(path3);
        when(marshaller.unmarshal(inputStreamReader1)).thenReturn(definitions1);
        when(marshaller.unmarshal(inputStreamReader2)).thenReturn(definitions2);
        when(marshaller.unmarshal(inputStreamReader3)).thenReturn(definitions3);

        final List<Definitions> actualDefinitions = helper.getOtherDMNDiagramsDefinitions(metadata);
        final List<Definitions> expectedDefinitions = asList(definitions1, definitions3);

        assertEquals(expectedDefinitions, actualDefinitions);
    }

    @Test
    public void testLoadPath() {

        final Path path = mock(Path.class);
        final org.uberfire.java.nio.file.Path nioPath = mock(org.uberfire.java.nio.file.Path.class);
        final String expectedContent = "<dmn/>";
        final byte[] contentBytes = expectedContent.getBytes();

        mockStatic(Paths.class);
        when(Paths.convert(path)).thenReturn(nioPath);
        when(ioService.readAllBytes(nioPath)).thenReturn(contentBytes);

        final InputStreamReader actualInputStream = helper.loadPath(path);
        final String actualContent = new Scanner(actualInputStream).next();

        assertEquals(expectedContent, actualContent);
    }

    private Path makePath(final String uri) {

        final PathImpl path = spy(new PathImpl());

        doReturn(uri).when(path).toURI();

        return path;
    }
}
