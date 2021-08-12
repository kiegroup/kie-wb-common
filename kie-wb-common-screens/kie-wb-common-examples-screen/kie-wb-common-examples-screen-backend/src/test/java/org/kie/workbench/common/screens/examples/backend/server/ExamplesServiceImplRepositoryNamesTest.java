/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.examples.backend.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.backend.server.utils.PathUtil;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.structure.backend.organizationalunit.config.SpaceConfigStorageRegistryImpl;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorage;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.repositories.RepositoryFactory;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.exception.ProjectAlreadyExistException;
import org.kie.workbench.common.screens.examples.model.ExampleOrganizationalUnit;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.kie.workbench.common.screens.examples.validation.ImportProjectValidators;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.spaces.Space;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ExamplesServiceImplRepositoryNamesTest {

    @Mock
    private IOService ioService;

    @Mock
    private ConfigurationFactory configurationFactory;

    @Mock
    private RepositoryFactory repositoryFactory;

    @Mock
    private KieModuleService moduleService;

    @Mock
    private OrganizationalUnitService ouService;

    @Mock
    private MetadataService metadataService;

    @Spy
    private Event<NewProjectEvent> newProjectEvent = new EventSourceMock<NewProjectEvent>() {
        @Override
        public void fire(final NewProjectEvent event) {
            //Do nothing. Default implementation throws an exception.
        }
    };

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private User user;

    @Mock
    private WorkspaceProjectService projectService;

    @Mock
    private ProjectScreenService projectScreenService;

    private ExamplesServiceImpl service;

    @Mock
    private ExampleOrganizationalUnit exampleOrganizationalUnit;
    private List<ImportProject> importProjects;

    @Mock
    private OrganizationalUnit ou;

    @Mock
    private Space space;

    @Mock
    private SpaceConfigStorageRegistry spaceConfigStorageRegistry;

    @Mock
    private SpaceConfigStorage spaceConfigStorage;

    @Captor
    private ArgumentCaptor<ProjectScreenModel> modelCapture;

    @Mock
    private ImportProjectValidators validators;

    private PathUtil pathUtil = new PathUtil();

    @Mock
    private FileSystem systemFS;

    @Mock
    private RepositoryService repositoryService;

    @Before
    public void setup() {
        when(ou.getSpace()).thenReturn(space);
        when(space.getName()).thenReturn("ou");

        when(spaceConfigStorageRegistry.get(anyString())).thenReturn(spaceConfigStorage);
        when(spaceConfigStorageRegistry.getBatch(anyString())).thenReturn(new SpaceConfigStorageRegistryImpl.SpaceStorageBatchImpl(spaceConfigStorage));
        when(spaceConfigStorageRegistry.exist(anyString())).thenReturn(true);

        service = spy(new ExamplesServiceImpl(ioService,
                                              repositoryFactory,
                                              moduleService,
                                              ouService,
                                              projectService,
                                              metadataService,
                                              newProjectEvent,
                                              projectScreenService,
                                              validators,
                                              spaceConfigStorageRegistry,
                                              systemFS,
                                              pathUtil,
                                              repositoryService));

        when(validators.getValidators()).thenReturn(new ArrayList<>());

        when(ouService.getOrganizationalUnits()).thenReturn(new HashSet<OrganizationalUnit>() {{
            add(new OrganizationalUnitImpl("ou1Name",
                                           "ou1GroupId"));
        }});
        when(moduleService.resolveModule(any(Path.class))).thenAnswer(new Answer<KieModule>() {
            @Override
            public KieModule answer(final InvocationOnMock invocationOnMock) throws Throwable {
                final Path path = (Path) invocationOnMock.getArguments()[0];
                final KieModule module = new KieModule(path,
                                                       path,
                                                       path,
                                                       path,
                                                       path,
                                                       path,
                                                       mock(POM.class));
                return module;
            }
        });
        when(sessionInfo.getId()).thenReturn("sessionId");
        when(sessionInfo.getIdentity()).thenReturn(user);
        when(user.getIdentifier()).thenReturn("user");
        when(configurationFactory.newConfigGroup(any(ConfigType.class),
                                                 anyString(),
                                                 anyString())).thenReturn(mock(ConfigGroup.class));

        final ImportProject exProject1 = mock(ImportProject.class);
        importProjects = Collections.singletonList(exProject1);

        final GitRepository repository1 = mock(GitRepository.class);
        final Path repositoryRoot = mock(Path.class);
        final Path module1Root = mock(Path.class);

        when(exampleOrganizationalUnit.getName()).thenReturn("ou");
        when(exProject1.getName()).thenReturn("module1");
        when(exProject1.getRoot()).thenReturn(module1Root);

        when(repository1.getBranch("dev_branch")).thenReturn(Optional.of(new Branch("dev_branch",
                                                                                    repositoryRoot)));
        final Optional<Branch> main = Optional.of(new Branch("main",
                                                               PathFactory.newPath("testFile",
                                                                                   "file:///")));
        when(repository1.getDefaultBranch()).thenReturn(main);

        when(repositoryRoot.toURI()).thenReturn("default:///");
        when(module1Root.toURI()).thenReturn("default:///module1");

        when(ouService.getOrganizationalUnit(eq("ou"))).thenReturn(ou);

        final WorkspaceProject project = spy(new WorkspaceProject());
        doReturn("module1").when(repository1).getAlias();
        doReturn(project).when(service).importProject(eq(ou),
                                                      eq(exProject1));

        doReturn(repository1.getAlias()).when(project).getName();
        doReturn(mock(Module.class)).when(project).getMainModule();
        doReturn(ou).when(project).getOrganizationalUnit();
        doReturn(project).when(projectService).resolveProject(repository1);
        doReturn(project).when(projectService).resolveProject(any(Path.class));

        final ProjectScreenModel model = new ProjectScreenModel();
        model.setPOM(new POM());
        doReturn(model).when(projectScreenService).load(any());
    }

    @Test
    public void nameIsNotTaken() {
        service.setupExamples(exampleOrganizationalUnit,
                              importProjects);

        verify(service).importProject(any(OrganizationalUnit.class),
                                      any(ImportProject.class));
        verify(projectScreenService,
               never()).save(any(),
                             any(),
                             any());
    }

    @Test
    public void nameIsTaken() {
        String module1 = "module1";
        String module1_1 = "module1 [1]";

        doCallRealMethod().when(service).importProject(any(), any());

        WorkspaceProject project1 = mock(WorkspaceProject.class);
        doReturn(module1).when(project1).getName();
        List<WorkspaceProject> projects = new ArrayList<>();
        projects.add(project1);
        projects.add(project1);
        doReturn(projects).when(projectService).getAllWorkspaceProjectsByName(any(),
                                                                              eq(module1));

        doReturn(module1_1).when(projectService).createFreshProjectName(any(),
                                                                        eq(module1));

        try {
            service.setupExamples(exampleOrganizationalUnit,
                                  importProjects);
            fail("Should raise exception");
        } catch (ProjectAlreadyExistException ex) {
            assertNotNull(ex);
        }
    }
}
