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

package org.kie.workbench.common.services.backend.ala;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.registry.PipelineRegistry;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.m2repo.backend.server.ExtendedM2RepoService;
import org.guvnor.structure.repositories.RepositoryService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.ResourceChange;

/**
 * Helper class for invoking the build system pipeline.
 */
@ApplicationScoped
public class BuildPipelineInvoker {

    private BuildPipelineInitializer buildPipelineInitializer;

    private PipelineRegistry pipelineRegistry;

    private ExtendedM2RepoService m2RepoService;

    private RepositoryService repositoryService;

    public BuildPipelineInvoker( ) {
    }

    @Inject
    public BuildPipelineInvoker( final BuildPipelineInitializer buildPipelineInitializer,
                                 final PipelineRegistry pipelineRegistry,
                                 final ExtendedM2RepoService m2RepoService,
                                 final RepositoryService repositoryService ) {
        this.buildPipelineInitializer = buildPipelineInitializer;
        this.pipelineRegistry = pipelineRegistry;
        this.m2RepoService = m2RepoService;
        this.repositoryService = repositoryService;
    }

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
        buildPipelineInitializer.getExecutor( ).execute( input, pipe, ( Consumer< LocalBinaryConfig > ) binary -> {
            consumer.accept( binary );
        } );
    }

    public LocalBinaryConfig invokeLocalBuildPipeLine( LocalBuildRequest buildRequest ) {
        final LocalBinaryConfig[] result = new LocalBinaryConfig[ 1 ];
        invokeLocalBuildPipeLine( buildRequest, localBinaryConfig -> {
            result[ 0 ] = localBinaryConfig;
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
        return LocalBuildConfig.BATCH_CHANGE + encodePath( path );
    }

    private String encodeResourceChanges( Collection< ResourceChange > resourceChanges ) {
        return resourceChanges
                .stream( )
                .map( change -> change.getType( ).name( ) )
                .collect( Collectors.joining( "," ) );
    }

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

        public static final LocalBuildRequest newFullBuildRequest( Project project ) {
            return new LocalBuildRequest( project );
        }

        public static final LocalBuildRequest newFullBuildAndDeployRequest( Project project, LocalBuildConfig.DeploymentType deploymentType, boolean suppressHandlers ) {
            return new LocalBuildRequest( project, deploymentType, suppressHandlers );
        }

        public static final LocalBuildRequest newIncrementalBuildRequest( Project project, LocalBuildConfig.BuildType buildType, Path resource ) {
            return new LocalBuildRequest( project, buildType, resource );
        }

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
    }
}