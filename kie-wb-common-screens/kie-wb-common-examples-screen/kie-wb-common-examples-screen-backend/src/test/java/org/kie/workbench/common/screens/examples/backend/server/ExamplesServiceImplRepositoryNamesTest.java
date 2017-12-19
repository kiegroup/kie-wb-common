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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import javax.enterprise.event.Event;

import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryCopier;
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
import org.kie.workbench.common.screens.examples.model.ExampleOrganizationalUnit;
import org.kie.workbench.common.screens.examples.model.ExampleProject;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.rpc.SessionInfo;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
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
    private RepositoryService repositoryService;

    @Mock
    private RepositoryCopier repositoryCopier;

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

    private ExamplesServiceImpl service;

    @Mock
    private ExampleOrganizationalUnit exampleOrganizationalUnit;
    private List<ExampleProject> exampleProjects;

    @Before
    public void setup() {
        service = spy(new ExamplesServiceImpl(ioService,
                                              configurationFactory,
                                              repositoryFactory,
                                              moduleService,
                                              repositoryService,
                                              repositoryCopier,
                                              ouService,
                                              projectService,
                                              metadataService,
                                              newProjectEvent));
        when(ouService.getOrganizationalUnits()).thenReturn(new HashSet<OrganizationalUnit>() {{
            add(new OrganizationalUnitImpl("ou1Name",
                                           "ou1Owner",
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

        final ExampleProject exProject1 = mock(ExampleProject.class);
        exampleProjects = new ArrayList<ExampleProject>() {{
            add(exProject1);
        }};
        final OrganizationalUnit ou = mock(OrganizationalUnit.class);
        doReturn("ou").when(ou).getName();

        final GitRepository repository1 = mock(GitRepository.class);
        final Path repositoryRoot = mock(Path.class);
        final Path module1Root = mock(Path.class);

        when(exampleOrganizationalUnit.getName()).thenReturn("ou");
        when(exProject1.getName()).thenReturn("module1");
        when(exProject1.getRoot()).thenReturn(module1Root);

        when(repository1.getBranch("dev_branch")).thenReturn(Optional.of(new Branch("dev_branch",
                                                                                    repositoryRoot)));
        final Optional<Branch> master = Optional.of(new Branch("master",
                                                               PathFactory.newPath("testFile",
                                                                                   "file:///")));
        when(repository1.getDefaultBranch()).thenReturn(master);

        when(repositoryRoot.toURI()).thenReturn("default:///");
        when(module1Root.toURI()).thenReturn("default:///module1");

        when(ouService.getOrganizationalUnit(eq("ou"))).thenReturn(ou);

        doReturn(repository1).when(repositoryCopier).copy(eq(ou),
                                                          anyString(),
                                                          eq(module1Root));
        final WorkspaceProject project = new WorkspaceProject();
        doReturn(project).when(projectService).resolveProject(repository1);
    }

    @Test
    public void nameIsNotTaken() {

        service.setupExamples(exampleOrganizationalUnit,
                              exampleProjects);

        verify(repositoryCopier).copy(any(OrganizationalUnit.class),
                                      eq("module1"),
                                      any(Path.class));
    }

    @Test
    public void nameIsTaken() {

        doReturn(mock(Repository.class)).when(repositoryService).getRepository("module1");

        service.setupExamples(exampleOrganizationalUnit,
                              exampleProjects);

        verify(repositoryCopier).copy(any(OrganizationalUnit.class),
                                      eq("ou-module1"),
                                      any(Path.class));
    }

    @Test
    public void evenTheOuRepositoryComboNameIsTaken() {

        doReturn(mock(Repository.class)).when(repositoryService).getRepository("module1");
        doReturn(mock(Repository.class)).when(repositoryService).getRepository("ou-module1");

        service.setupExamples(exampleOrganizationalUnit,
                              exampleProjects);

        verify(repositoryCopier).copy(any(OrganizationalUnit.class),
                                      eq("ou-module1-1"),
                                      any(Path.class));
    }

    @Test
    public void evenTheOuRepositoryComboPlusNumberNameIsTaken() {

        doReturn(mock(Repository.class)).when(repositoryService).getRepository("module1");
        doReturn(mock(Repository.class)).when(repositoryService).getRepository("ou-module1");
        doReturn(mock(Repository.class)).when(repositoryService).getRepository("ou-module1-1");

        service.setupExamples(exampleOrganizationalUnit,
                              exampleProjects);

        verify(repositoryCopier).copy(any(OrganizationalUnit.class),
                                      eq("ou-module1-2"),
                                      any(Path.class));
    }
}
