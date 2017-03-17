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

package org.kie.workbench.common.services.backend.builder.ala;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

import org.guvnor.ala.build.maven.model.impl.MavenProjectBinaryBuildImpl;
import org.guvnor.ala.config.Config;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.execution.PipelineExecutor;
import org.guvnor.ala.registry.PipelineRegistry;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.workbench.events.ResourceChange;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class BuildPipelineInvokerTest
        extends BuildPipelineTestBase {

    @Mock
    private BuildPipelineInitializer pipelineInitializer;

    @Mock
    private PipelineRegistry pipelineRegistry;

    @Mock
    private RepositoryService repositoryService;

    private BuildPipelineInvoker pipelineInvoker;

    @Mock
    private PipelineExecutor pipelineExecutor;

    @Mock
    private Pipeline pipeline;

    @Mock
    private BuildPipelineInvoker.LocalBuildRequest buildRequest;

    @Mock
    private BuildPipelineInvoker.MavenBuildRequest mavenBuildRequest;

    @Mock
    private Project project;

    @Mock
    private Path rootPath;

    @Mock
    private Path resource;

    @Mock
    private LocalBinaryConfig localBinaryConfig;

    @Mock
    private MavenProjectBinaryBuildImpl mavenProjectBinaryBuild;

    private Input input;

    @Mock
    private Repository repository;

    @Before
    public void setUp( ) {
        pipelineInvoker = new BuildPipelineInvoker( pipelineExecutor, pipelineRegistry, repositoryService );
        when( pipelineRegistry.getPipelineByName( BuildPipelineInitializer.LOCAL_BUILD_PIPELINE ) ).thenReturn( pipeline );
        when( pipelineRegistry.getPipelineByName( BuildPipelineInitializer.MAVEN_BUILD_PIPELINE ) ).thenReturn( pipeline );

        when( buildRequest.getProject( ) ).thenReturn( project );
        when( project.getRootPath( ) ).thenReturn( rootPath );
        when( rootPath.toURI( ) ).thenReturn( ROOT_PATH_URI );
        when( resource.toURI( ) ).thenReturn( RESOURCE_URI_1 );
    }

    @Test
    public void testFullBuildRequest( ) {
        when( buildRequest.getBuildType( ) ).thenReturn( LocalBuildConfig.BuildType.FULL_BUILD );

        // the pipeline should be invoked with this input.
        input = createFullBuildInput( ROOT_PATH_URI );

        preparePipeline( input, localBinaryConfig );

        LocalBinaryConfig result = pipelineInvoker.invokeLocalBuildPipeLine( buildRequest );
        verifyPipelineInvocation( localBinaryConfig, result );
    }

    @Test
    public void testFullBuildAndDeployValidatedRequest( ) {
        testFullBuildAndDeployRequest( LocalBuildConfig.DeploymentType.VALIDATED );
    }

    @Test
    public void testFullBuildAndDeployForcedRequest( ) {
        testFullBuildAndDeployRequest( LocalBuildConfig.DeploymentType.FORCED );
    }

    private void testFullBuildAndDeployRequest( LocalBuildConfig.DeploymentType deploymentType ) {
        when( buildRequest.getBuildType( ) ).thenReturn( LocalBuildConfig.BuildType.FULL_BUILD_AND_DEPLOY );
        when( buildRequest.getDeploymentType( ) ).thenReturn( deploymentType );
        when( buildRequest.isSuppressHandlers( ) ).thenReturn( false );

        // the pipeline should be invoked with this input.
        input = createFullBuildAndDeployInput( ROOT_PATH_URI, deploymentType.name( ), false );

        preparePipeline( input, localBinaryConfig );

        LocalBinaryConfig result = pipelineInvoker.invokeLocalBuildPipeLine( buildRequest );
        verifyPipelineInvocation( localBinaryConfig, result );
    }

    @Test
    public void testIncrementalBuildAddResource( ) {
        testIncrementalBuildResourceRequest( LocalBuildConfig.BuildType.INCREMENTAL_ADD_RESOURCE, resource );
    }

    @Test
    public void testIncrementalBuildDeleteResource( ) {
        testIncrementalBuildResourceRequest( LocalBuildConfig.BuildType.INCREMENTAL_DELETE_RESOURCE, resource );
    }

    @Test
    public void testIncrementalBuildUpdateResource( ) {
        testIncrementalBuildResourceRequest( LocalBuildConfig.BuildType.INCREMENTAL_UPDATE_RESOURCE, resource );
    }

    @Test
    public void testIncrementalBuildResourceChanges( ) {
        Map< Path, Collection< ResourceChange > > resourceChanges = createResourceChanges( changes );
        when( buildRequest.getBuildType( ) ).thenReturn( LocalBuildConfig.BuildType.INCREMENTAL_BATCH_CHANGES );
        when( buildRequest.isSingleResource( ) ).thenReturn( false );
        when( buildRequest.getResourceChanges( ) ).thenReturn( resourceChanges );

        // the pipeline should be invoked with this input.
        input = createBatchChangesInput( ROOT_PATH_URI, LocalBuildConfig.BuildType.INCREMENTAL_BATCH_CHANGES.name( ), changes );

        preparePipeline( input, localBinaryConfig );

        LocalBinaryConfig result = pipelineInvoker.invokeLocalBuildPipeLine( buildRequest );
        verifyPipelineInvocation( localBinaryConfig, result );
    }

    @Test
    public void testMavenBuildRequest( ) {
        when( mavenBuildRequest.getProject( ) ).thenReturn( project );
        when( project.getRootPath( ) ).thenReturn( rootPath );
        when( rootPath.toURI( ) ).thenReturn( ROOT_PATH_URI );
        when( rootPath.getFileName() ).thenReturn( "testProject" );

        String repoPathURI = rootPath.toURI( ).substring( 0, rootPath.toURI( ).lastIndexOf( rootPath.getFileName( ) ) );
        final Path repoPath = PathFactory.newPath( "repo", repoPathURI );
        when( repositoryService.getRepository( repoPath ) ).thenReturn( repository );
        when( repository.getAlias() ).thenReturn( "TestRepo" );
        ArrayList<String> branches = new ArrayList<>( );
        branches.add( "master" );
        when ( repository.getBranches() ).thenReturn( branches );
        when ( repository.getBranchRoot( "master" ) ).thenReturn( repoPath );

        // the maven pipeline should be invoked with this input.
        input = createMavenBuildInput( "TestRepo", "master", "testProject", false );

        preparePipeline( input, mavenProjectBinaryBuild );

        MavenProjectBinaryBuildImpl result = pipelineInvoker.invokeMavenBuildPipeline( mavenBuildRequest );
        verifyPipelineInvocation( mavenProjectBinaryBuild, result );
    }

    private void testIncrementalBuildResourceRequest( LocalBuildConfig.BuildType buildType, Path resource ) {
        when( buildRequest.getBuildType( ) ).thenReturn( buildType );
        when( buildRequest.getResource( ) ).thenReturn( resource );
        when( buildRequest.isSingleResource( ) ).thenReturn( true );

        // the pipeline should be invoked with this input.
        input = createIncrementalBuildInput( ROOT_PATH_URI, RESOURCE_URI_1, buildType.name( ) );

        preparePipeline( input, localBinaryConfig );

        LocalBinaryConfig result = pipelineInvoker.invokeLocalBuildPipeLine( buildRequest );
        verifyPipelineInvocation( localBinaryConfig, result );
    }

    private void preparePipeline( Input input, Config config ) {
        doAnswer( new Answer< Void >( ) {
            public Void answer( InvocationOnMock invocation ) {
                Consumer consumer = ( Consumer ) invocation.getArguments( )[ 2 ];
                consumer.accept( config );
                return null;
            }
        } ).when( pipelineExecutor ).execute( eq( input ), eq( pipeline ), any( Consumer.class ) );
    }

    private void verifyPipelineInvocation( Config expectedResult, Config result ) {
        assertEquals( expectedResult, result );
        verify( pipelineExecutor, times( 1 ) ).execute( eq( input ), eq( pipeline ), any( Consumer.class ) );
    }
}