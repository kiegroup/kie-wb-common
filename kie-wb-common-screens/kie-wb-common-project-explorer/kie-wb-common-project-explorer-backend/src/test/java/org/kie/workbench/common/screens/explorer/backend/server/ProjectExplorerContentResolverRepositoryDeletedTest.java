/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.explorer.backend.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.ActiveOptions;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerContentQuery;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;
import org.uberfire.security.authz.AuthorizationManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class ProjectExplorerContentResolverRepositoryDeletedTest {

    protected final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();
    private ProjectExplorerContentResolver resolver;

    private final OrganizationalUnit organizationalUnit = new OrganizationalUnitImpl( "demo",
                                                                                      "admin",
                                                                                      "groupId" );
    private final GitRepository      repositoryA        = new GitRepository( "repositoryA" );
    private final GitRepository      repositoryB        = new GitRepository( "repositoryB" );

    private Project project = createProject( "master",
                                             "projectA" );

    @Mock
    private ExplorerServiceHelper helper;

    @Before
    public void setUp() throws Exception {

        repositoryA.addBranch( "master", null );
        repositoryB.addBranch( "master", null );

        //Ensure URLs use the default:// scheme
        fs.forceAsDefault();

        final KieProjectService projectService = mock( KieProjectService.class );
        helper = mock( ExplorerServiceHelper.class );
        final AuthorizationManager authorizationManager = mock( AuthorizationManager.class );
        final OrganizationalUnitService organizationalUnitService = mock( OrganizationalUnitService.class );

        final ArrayList<OrganizationalUnit> organizationalUnits = new ArrayList<OrganizationalUnit>();
        organizationalUnits.add( organizationalUnit );

        final HashSet<Project> projects = new HashSet<Project>();
        projects.add( project );
        when( projectService.getProjects( repositoryA, "master" ) ).thenReturn( projects );

        when( organizationalUnitService.getOrganizationalUnits() ).thenReturn( organizationalUnits );

        when( organizationalUnitService.getOrganizationalUnit( "demo" ) ).thenReturn( organizationalUnit );

        when( authorizationManager.authorize( any( Project.class ), any( User.class ) ) ).thenReturn( true );
        when( authorizationManager.authorize( any( OrganizationalUnit.class ), any( User.class ) ) ).thenReturn( true );

        organizationalUnit.getRepositories().add( repositoryA );
//        organizationalUnit.getRepositories().add( repositoryB );

        resolver = new ProjectExplorerContentResolver(
                projectService,
                helper,
                authorizationManager,
                organizationalUnitService );

    }

    @Test
    public void testRepositoryDeleted() throws Exception {

        final UserExplorerLastData lastData = new UserExplorerLastData();
        lastData.setPackage( organizationalUnit,
                             repositoryB,
                             "master",
                             createProject( "master",
                                            "projectB" ),
                             createPackage( "org.test" ) );
        when( helper.getLastContent() ).thenReturn( lastData );

        final ActiveOptions activeOptions = new ActiveOptions();
        activeOptions.add( Option.BUSINESS_CONTENT );

        final ProjectExplorerContent content = resolver.resolve( new ProjectExplorerContentQuery( organizationalUnit,
                                                                                                  null,
                                                                                                  null,
                                                                                                  null,
                                                                                                  activeOptions ) );

        assertEquals( repositoryA, content.getRepository() );
        assertTrue( content.getFolderListing().getContent().isEmpty() );
    }

    private Package createPackage( final String name ) {
        return new Package( createMockPath( "master",
                                            "projectB" ),
                            createMockPath( "master",
                                            "projectB/src/main/java" ),
                            createMockPath( "master",
                                            "projectB/src/main/resources" ),
                            createMockPath( "master",
                                            "projectB/src/test/java" ),
                            createMockPath( "master",
                                            "projectB/src/test/resources" ),
                            name,
                            "",
                            "" );
    }

    private Project createProject( final String branch,
                                   final String projectName ) {
        return new Project( createMockPath( branch,
                                            projectName ),
                            createMockPath( branch,
                                            projectName ),
                            projectName );
    }

    private Path createMockPath( final String branch,
                                 final String projectName ) {

        return new Path() {
            @Override
            public String getFileName() {
                return projectName;
            }

            @Override
            public String toURI() {
                return branch + "@" + projectName;
            }

            @Override
            public int compareTo( Path o ) {
                return toURI().compareTo( o.toURI() );
            }
        };
    }

    private ProjectExplorerContentQuery getContentQuery( final String branchName,
                                                         final Project project ) {

        ProjectExplorerContentQuery projectExplorerContentQuery = new ProjectExplorerContentQuery(
                organizationalUnit,
                getGitRepository(),
                branchName,
                project
        );

        ActiveOptions options = new ActiveOptions();
        options.add( Option.TREE_NAVIGATOR );
        options.add( Option.EXCLUDE_HIDDEN_ITEMS );
        options.add( Option.BUSINESS_CONTENT );
        projectExplorerContentQuery.setOptions( options );

        return projectExplorerContentQuery;
    }

    private GitRepository getGitRepository() {
        GitRepository repository = new GitRepository();

        HashMap<String, Path> branches = new HashMap<String, Path>();
        Path pathToMaster = PathFactory.newPath( "/", "file://master@project/" );
        branches.put( "master", pathToMaster );
        Path pathToDev = PathFactory.newPath( "/", "file://dev-1.0.0@project/" );
        branches.put( "dev-1.0.0", pathToDev );

        repository.setRoot( pathToMaster );
        repository.setBranches( branches );
        return repository;
    }
}