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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.ala.build.maven.model.impl.MavenProjectBinaryBuildImpl;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.execution.PipelineExecutor;
import org.guvnor.ala.registry.PipelineRegistry;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.workbench.events.ResourceChange;

/**
 * Helper class for invoking the build system pipelines.
 */
@ApplicationScoped
public class BuildPipelineInvoker {

    private PipelineExecutor executor;

    private PipelineRegistry pipelineRegistry;

    private RepositoryService repositoryService;

    public BuildPipelineInvoker( ) {
        //Empty constructor for Weld proxying
    }

    @Inject
    public BuildPipelineInvoker( @Named("buildPipelineExecutor") final PipelineExecutor executor,
                                 final PipelineRegistry pipelineRegistry,
                                 final RepositoryService repositoryService ) {
        this.executor = executor;
        this.pipelineRegistry = pipelineRegistry;
        this.repositoryService = repositoryService;
    }

    /**
     * Invokes the local build pipeline.
     *
     * @param buildRequest the buildRequest configures the build to perform.
     *
     * @param consumer a consumer for getting the pipeline output.
     */
    public void invokeLocalBuildPipeLine( LocalBuildRequest buildRequest,
                                          Consumer< LocalBinaryConfig > consumer ) {

        Pipeline pipe = pipelineRegistry.getPipelineByName( BuildPipelineInitializer.LOCAL_BUILD_PIPELINE );

        Input input = new Input( ) {
            {
                put( LocalSourceConfig.ROOT_PATH, buildRequest.getProject( ).getRootPath( ).toURI( ) );
                put( LocalBuildConfig.BUILD_TYPE, buildRequest.getBuildType( ).name( ) );
                if ( buildRequest.isSingleResource( ) ) {
                    put( LocalBuildConfig.RESOURCE, encodePath( buildRequest.getResource( ) ) );
                } else {
                    addResourceChanges( this, buildRequest.getResourceChanges( ) );
                }
                if ( buildRequest.getDeploymentType( ) != null ) {
                    put( LocalBuildConfig.DEPLOYMENT_TYPE, buildRequest.getDeploymentType( ).name( ) );
                    put( LocalBuildConfig.SUPPRESS_HANDLERS, Boolean.toString( buildRequest.isSuppressHandlers( ) ) );
                }
            }
        };
        executor.execute( input, pipe, consumer );
    }

    /**
     * Invokes the local build pipeline.
     *
     * @param buildRequest the buildRequest configures the build to perform.
     *
     * @return the pipeline output.
     */
    public LocalBinaryConfig invokeLocalBuildPipeLine( LocalBuildRequest buildRequest ) {
        final LocalBinaryConfig[] result = new LocalBinaryConfig[ 1 ];
        invokeLocalBuildPipeLine( buildRequest, localBinaryConfig -> {
            result[ 0 ] = localBinaryConfig;
        } );
        return result[ 0 ];
    }

    /**
     * Invokes the maven build pipeline.
     *
     * @param buildRequest a build request with the project to build.
     *
     * @param consumer a consumer for getting the pipeline output.
     */
    public void invokeMavenBuildPipeline( MavenBuildRequest buildRequest,
                                                  Consumer< MavenProjectBinaryBuildImpl > consumer ) {
        final Path rootPath = buildRequest.getProject( ).getRootPath( );
        final Path repoPath = PathFactory.newPath( "repo", rootPath.toURI( ).substring( 0, rootPath.toURI( ).lastIndexOf( rootPath.getFileName( ) ) ) );
        final Repository repository = repositoryService.getRepository( repoPath );

        final Pipeline pipe = pipelineRegistry.getPipelineByName( BuildPipelineInitializer.MAVEN_BUILD_PIPELINE );

        final Input buildInput = new Input( ) {
            {
                put( "repo-name", repository.getAlias( ) );
                put( "branch", getBranchForPath( rootPath, repository ) );
                put( "project-dir", rootPath.getFileName() );
                put( LocalMavenBuildExecConfig.DEPLOY_INTO_KIE_M2_REPOSITORY, Boolean.toString( buildRequest.isDeploy( ) ) );
                put( LocalMavenBuildExecConfig.CAPTURE_ERRORS, "true" );
            }
        };
        executor.execute( buildInput, pipe, consumer );
    }

    /**
     * Invokes the maven build pipeline.
     *
     * @param buildRequest a build request with the project to build.
     *
     * @return the pipeline output.
     */
    public MavenProjectBinaryBuildImpl invokeMavenBuildPipeline( MavenBuildRequest buildRequest ) {
        final MavenProjectBinaryBuildImpl[] result = new MavenProjectBinaryBuildImpl[ 1 ];
        invokeMavenBuildPipeline( buildRequest, mavenBinary -> {
            result[ 0 ] = mavenBinary;
        } );
        return result[ 0 ];
    }

    private void addResourceChanges( Input input, Map< Path, Collection< ResourceChange > > resourceChanges ) {
        resourceChanges.entrySet( ).forEach( entry -> {
            input.put( encodeResourceChangePath( entry.getKey( ) ), encodeResourceChanges( entry.getValue( ) ) );
        } );
    }

    private String encodePath( Path path ) {
        return path.toURI( );
    }

    private String encodeResourceChangePath( Path path ) {
        return LocalBuildConfig.RESOURCE_CHANGE + encodePath( path );
    }

    private String encodeResourceChanges( Collection< ResourceChange > resourceChanges ) {
        return resourceChanges
                .stream( )
                .map( change -> change.getType( ).name( ) )
                .collect( Collectors.joining( "," ) );
    }

    private String getBranchForPath( Path path, Repository repository ) {
        return repository.getBranches().stream().filter( branch -> {
            Path branchRoot = repository.getBranchRoot( branch );
            return branchRoot != null && path.toURI().startsWith( branchRoot.toURI() );
        } ).findFirst().orElse( repository.getDefaultBranch() );
    }

    /**
     * This class models the configuration parameters for a project build execution.
     */
    public static class LocalBuildRequest {

        private Project project;

        private LocalBuildConfig.BuildType buildType = LocalBuildConfig.BuildType.FULL_BUILD;

        private Path resource;

        private Map< Path, Collection< ResourceChange > > resourceChanges = new HashMap<>( );

        private LocalBuildConfig.DeploymentType deploymentType;

        private boolean suppressHandlers;

        private LocalBuildRequest( Project project ) {
            this.project = project;
            this.buildType = LocalBuildConfig.BuildType.FULL_BUILD;
        }

        private LocalBuildRequest( Project project, LocalBuildConfig.BuildType buildType, Path resource ) {
            this.project = project;
            this.buildType = buildType;
            this.resource = resource;
        }

        private LocalBuildRequest( Project project, Map< Path, Collection< ResourceChange > > resourceChanges ) {
            this.project = project;
            this.resourceChanges = resourceChanges;
            this.buildType = LocalBuildConfig.BuildType.INCREMENTAL_BATCH_CHANGES;
        }

        private LocalBuildRequest( Project project, LocalBuildConfig.DeploymentType deploymentType, boolean suppressHandlers ) {
            this.project = project;
            this.deploymentType = deploymentType;
            this.suppressHandlers = suppressHandlers;
            this.buildType = LocalBuildConfig.BuildType.FULL_BUILD_AND_DEPLOY;
        }

        /**
         * Creates a full build request for the given project.
         *
         * @param project the project to build.
         *
         * @return a properly constructed build request.
         */
        public static final LocalBuildRequest newFullBuildRequest( Project project ) {
            return new LocalBuildRequest( project );
        }

        /**
         * Creates a full build request for the given project, and additionally performs the deployment for the build
         * in current m2repository.
         *
         * @param project the project to build.
         *
         * @param deploymentType the type of deployment to perform.
         *
         * @param suppressHandlers true if PostBuildHandlers invocation should be canceled, false in any other case.
         *
         * @return a properly constructed build request.
         */
        public static final LocalBuildRequest newFullBuildAndDeployRequest( Project project, LocalBuildConfig.DeploymentType deploymentType, boolean suppressHandlers ) {
            return new LocalBuildRequest( project, deploymentType, suppressHandlers );
        }

        /**
         * Creates an incremental build request for the given project.
         *
         * @param project the project to build incrementally.
         *
         * @param buildType the incremental build type to perform.
         *
         * @param resource the resource that was added, updated or deleted.
         *
         * @return a properly constructed build request.
         */
        public static final LocalBuildRequest newIncrementalBuildRequest( Project project, LocalBuildConfig.BuildType buildType, Path resource ) {
            return new LocalBuildRequest( project, buildType, resource );
        }

        /**
         * Creates an incremental build request for the given project.
         *
         * @param project the project to build incrementally.
         *
         * @param resourceChanges the set of changes. This incremental build type supports changes for multiple resources.
         *
         * @return a properly constructed build request.
         */
        public static final LocalBuildRequest newIncrementalBuildRequest( Project project, Map< Path, Collection< ResourceChange > > resourceChanges ) {
            return new LocalBuildRequest( project, resourceChanges );
        }

        public LocalBuildConfig.BuildType getBuildType( ) {
            return buildType;
        }

        public Project getProject( ) {
            return project;
        }

        public Path getResource( ) {
            return resource;
        }

        public Map< Path, Collection< ResourceChange > > getResourceChanges( ) {
            return resourceChanges;
        }

        public LocalBuildConfig.DeploymentType getDeploymentType( ) {
            return deploymentType;
        }

        public boolean isSuppressHandlers( ) {
            return suppressHandlers;
        }

        public boolean isSingleResource( ) {
            return resource != null;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass( ) != o.getClass( ) ) return false;

            LocalBuildRequest that = ( LocalBuildRequest ) o;

            if ( suppressHandlers != that.suppressHandlers ) return false;
            if ( project != null ? !project.equals( that.project ) : that.project != null ) return false;
            if ( buildType != that.buildType ) return false;
            if ( resource != null ? !resource.equals( that.resource ) : that.resource != null ) return false;
            if ( resourceChanges != null ? !resourceChanges.equals( that.resourceChanges ) : that.resourceChanges != null )
                return false;
            return deploymentType == that.deploymentType;

        }

        @Override
        public int hashCode( ) {
            int result = project != null ? project.hashCode( ) : 0;
            result = 31 * result + ( buildType != null ? buildType.hashCode( ) : 0 );
            result = 31 * result + ( resource != null ? resource.hashCode( ) : 0 );
            result = 31 * result + ( resourceChanges != null ? resourceChanges.hashCode( ) : 0 );
            result = 31 * result + ( deploymentType != null ? deploymentType.hashCode( ) : 0 );
            result = 31 * result + ( suppressHandlers ? 1 : 0 );
            return result;
        }
    }

    /**
     * This class models the configuration parameters for a project build execution based on the maven build pipeline.
     */
    public static class MavenBuildRequest {

        private Project project;

        private boolean deploy;

        private MavenBuildRequest( Project project, boolean deploy ) {
            this.project = project;
            this.deploy = deploy;
        }

        public Project getProject( ) {
            return project;
        }

        public boolean isDeploy( ) {
            return deploy;
        }

        /**
         * Creates a build request for the given project based on the maven build pipeline.
         *
         * @param project the project to build.
         *
         * @param deploy true if the resulting maven artifact should be deployed into current WB M2 repository, false
         * in any other case.
         *
         * @return a properly constructed build request.
         */
        public static final MavenBuildRequest newMavenBuildRequest( Project project, boolean deploy ) {
            return new MavenBuildRequest( project, deploy );
        }
    }
}