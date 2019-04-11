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

package org.kie.workbench.common.dmn.backend.editors.types;

import java.util.List;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.Import;
import org.kie.workbench.common.dmn.api.editors.types.DMNIncludeModel;
import org.kie.workbench.common.dmn.api.editors.types.DMNIncludedNode;
import org.kie.workbench.common.dmn.backend.editors.types.common.DMNIncludeModelFactory;
import org.kie.workbench.common.dmn.backend.editors.types.common.DMNIncludedNodesFilter;
import org.kie.workbench.common.dmn.backend.editors.types.exceptions.DMNIncludeModelCouldNotBeCreatedException;
import org.kie.workbench.common.services.refactoring.backend.server.query.RefactoringQueryServiceImpl;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.stunner.core.lookup.LookupManager.LookupResponse;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramLookupRequest;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramRepresentation;
import org.kie.workbench.common.stunner.core.service.DiagramLookupService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.paging.PageResponse;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNIncludeModelsServiceImplTest {

    @Mock
    private RefactoringQueryServiceImpl refactoringQueryService;

    @Mock
    private DiagramLookupService diagramLookupService;

    @Mock
    private DMNIncludeModelFactory includeModelFactory;

    @Mock
    private DMNIncludedNodesFilter includedNodesFilter;

    @Mock
    private LookupResponse<DiagramRepresentation> lookupResponse;

    @Mock
    private PageResponse<RefactoringPageRow> pageResponse;

    private DMNIncludeModelsServiceImpl service;

    @Before
    public void setup() {
        service = spy(new DMNIncludeModelsServiceImpl(refactoringQueryService, diagramLookupService, includeModelFactory, includedNodesFilter));
    }

    @Test
    public void testLoadModelsWhenWorkspaceProjectIsNull() throws Exception {

        final WorkspaceProject workspaceProject = null;
        final DiagramRepresentation representation1 = mock(DiagramRepresentation.class);
        final DiagramRepresentation representation2 = mock(DiagramRepresentation.class);
        final DiagramRepresentation representation3 = mock(DiagramRepresentation.class);
        final Path path1 = mock(Path.class);
        final Path path2 = mock(Path.class);
        final Path path3 = mock(Path.class);
        final DMNIncludeModel dmnIncludeModel1 = mock(DMNIncludeModel.class);
        final DMNIncludeModel dmnIncludeModel2 = mock(DMNIncludeModel.class);
        final List<DiagramRepresentation> results = asList(representation1, representation2, representation3);

        when(diagramLookupService.lookup(any(DiagramLookupRequest.class))).thenReturn(lookupResponse);
        when(lookupResponse.getResults()).thenReturn(results);
        when(representation1.getPath()).thenReturn(path1);
        when(representation2.getPath()).thenReturn(path2);
        when(representation3.getPath()).thenReturn(path3);
        when(includeModelFactory.create(path1)).thenReturn(dmnIncludeModel1);
        when(includeModelFactory.create(path2)).thenReturn(dmnIncludeModel2);
        when(includeModelFactory.create(path3)).thenThrow(new DMNIncludeModelCouldNotBeCreatedException());

        final List<DMNIncludeModel> dmnIncludeModels = service.loadModels(workspaceProject);

        assertEquals(2, dmnIncludeModels.size());
        assertEquals(dmnIncludeModel1, dmnIncludeModels.get(0));
        assertEquals(dmnIncludeModel2, dmnIncludeModels.get(1));
    }

    @Test
    public void testLoadModelsWhenWorkspaceProjectIsNotNull() throws Exception {

        final WorkspaceProject workspaceProject = mock(WorkspaceProject.class);
        final Path rootPath = mock(Path.class);
        final String uri = "/src/path/file.dmn";
        final RefactoringPageRow row1 = mock(RefactoringPageRow.class);
        final RefactoringPageRow row2 = mock(RefactoringPageRow.class);
        final RefactoringPageRow row3 = mock(RefactoringPageRow.class);
        final List<RefactoringPageRow> rows = asList(row1, row2);
        final Path path1 = mock(Path.class);
        final Path path2 = mock(Path.class);
        final Path path3 = mock(Path.class);
        final DMNIncludeModel dmnIncludeModel1 = mock(DMNIncludeModel.class);
        final DMNIncludeModel dmnIncludeModel2 = mock(DMNIncludeModel.class);

        when(workspaceProject.getRootPath()).thenReturn(rootPath);
        when(rootPath.toURI()).thenReturn(uri);
        when(refactoringQueryService.query(any(RefactoringPageRequest.class))).thenReturn(pageResponse);
        when(pageResponse.getPageRowList()).thenReturn(rows);
        when(row1.getValue()).thenReturn(path1);
        when(row2.getValue()).thenReturn(path2);
        when(row3.getValue()).thenReturn(path3);
        when(includeModelFactory.create(path1)).thenReturn(dmnIncludeModel1);
        when(includeModelFactory.create(path2)).thenReturn(dmnIncludeModel2);
        when(includeModelFactory.create(path3)).thenThrow(new DMNIncludeModelCouldNotBeCreatedException());

        final List<DMNIncludeModel> dmnIncludeModels = service.loadModels(workspaceProject);

        assertEquals(2, dmnIncludeModels.size());
        assertEquals(dmnIncludeModel1, dmnIncludeModels.get(0));
        assertEquals(dmnIncludeModel2, dmnIncludeModels.get(1));
    }

    @Test
    public void testLoadNodesFromImports() {

        final WorkspaceProject workspaceProject = mock(WorkspaceProject.class);
        final Import anImport1 = mock(Import.class);
        final Import anImport2 = mock(Import.class);
        final Import anImport3 = mock(Import.class);
        final Path path1 = mock(Path.class);
        final Path path2 = mock(Path.class);
        final Path path3 = mock(Path.class);
        final DMNIncludedNode node1 = mock(DMNIncludedNode.class);
        final DMNIncludedNode node2 = mock(DMNIncludedNode.class);
        final DMNIncludedNode node3 = mock(DMNIncludedNode.class);
        final DMNIncludedNode node4 = mock(DMNIncludedNode.class);
        final DMNIncludedNode node5 = mock(DMNIncludedNode.class);
        final DMNIncludedNode node6 = mock(DMNIncludedNode.class);
        final DMNIncludedNode node7 = mock(DMNIncludedNode.class);
        final List<DMNIncludedNode> path1Nodes = asList(node1, node2, node3, node4);
        final List<DMNIncludedNode> path2Nodes = singletonList(node5);
        final List<DMNIncludedNode> path3Nodes = asList(node6, node7);
        final List<Import> namespaces = asList(anImport1, anImport2, anImport3);
        final List<Path> paths = asList(path1, path2, path3);

        doReturn(paths).when(service).getPaths(workspaceProject);
        when(includedNodesFilter.getNodesFromImports(path1, namespaces)).thenReturn(path1Nodes);
        when(includedNodesFilter.getNodesFromImports(path2, namespaces)).thenReturn(path2Nodes);
        when(includedNodesFilter.getNodesFromImports(path3, namespaces)).thenReturn(path3Nodes);

        final List<DMNIncludedNode> actualNodes = service.loadNodesFromImports(workspaceProject, namespaces);
        final List<DMNIncludedNode> expectedNodes = asList(node1, node2, node3, node4, node5, node6, node7);

        assertEquals(expectedNodes, actualNodes);
    }
}
