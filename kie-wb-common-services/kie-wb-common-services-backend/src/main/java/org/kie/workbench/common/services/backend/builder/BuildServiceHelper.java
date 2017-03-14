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

package org.kie.workbench.common.services.backend.builder;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.kie.workbench.common.services.backend.ala.LocalBinaryConfig;
import org.kie.workbench.common.services.backend.ala.LocalBuildConfig;
import org.kie.workbench.common.services.backend.ala.BuildPipelineInvoker;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.ResourceChange;

@ApplicationScoped
public class BuildServiceHelper {

    private BuildPipelineInvoker buildPipelineInvoker;

    public BuildServiceHelper( ) {
    }

    @Inject
    public BuildServiceHelper( BuildPipelineInvoker buildPipelineInvoker ) {
        this.buildPipelineInvoker = buildPipelineInvoker;
    }

    public BuildResults localBuild( Project project ) {
        final BuildResults[] result = new BuildResults[ 1 ];
        invokeLocalBuildPipeLine( project, localBinaryConfig -> {
            result[ 0 ] = localBinaryConfig.getBuildResults( );
        } );
        return result[ 0 ];
    }

    public void build( Project project, Consumer< LocalBinaryConfig > consumer ) {
        invokeLocalBuildPipeLine( project, consumer );
    }

    public IncrementalBuildResults localBuild( Project project, LocalBuildConfig.BuildType buildType, Path resource ) {
        final IncrementalBuildResults[] result = new IncrementalBuildResults[ 1 ];
        invokeLocalBuildPipeLine( project, buildType, resource, localBinaryConfig -> {
            result[ 0 ] = localBinaryConfig.getIncrementalBuildResults( );
        } );
        return result[ 0 ];
    }

    public IncrementalBuildResults localBuild( Project project, Map< Path, Collection< ResourceChange > > resourceChanges ) {
        final IncrementalBuildResults[] result = new IncrementalBuildResults[ 1 ];
        invokeLocalBuildPipeLine( project, resourceChanges, localBinaryConfig -> {
            result[ 0 ] = localBinaryConfig.getIncrementalBuildResults( );
        } );
        return result[ 0 ];
    }

    public BuildResults localBuildAndDeploy( final Project project,
                                             final DeploymentMode mode,
                                             final boolean suppressHandlers ) {
        final BuildResults[] result = new BuildResults[ 1 ];
        invokeLocalBuildPipeLine( project, suppressHandlers, mode, localBinaryConfig -> {
            result[ 0 ] = localBinaryConfig.getBuildResults( );
        } );
        return result[ 0 ];
    }

    private void invokeLocalBuildPipeLine( Project project,
                                           Consumer< LocalBinaryConfig > consumer ) {
        BuildPipelineInvoker.LocalBuildRequest buildRequest = BuildPipelineInvoker.LocalBuildRequest.newFullBuildRequest( project );
        buildPipelineInvoker.invokeLocalBuildPipeLine( buildRequest, consumer );
    }

    private void invokeLocalBuildPipeLine( Project project,
                                           LocalBuildConfig.BuildType buildType,
                                           Path resource,
                                           Consumer< LocalBinaryConfig > consumer ) {
        BuildPipelineInvoker.LocalBuildRequest buildRequest = BuildPipelineInvoker.LocalBuildRequest.newIncrementalBuildRequest( project, buildType, resource );
        buildPipelineInvoker.invokeLocalBuildPipeLine( buildRequest, consumer );
    }

    private void invokeLocalBuildPipeLine( Project project,
                                           Map< Path, Collection< ResourceChange > > resourceChanges,
                                           Consumer< LocalBinaryConfig > consumer ) {
        BuildPipelineInvoker.LocalBuildRequest buildRequest = BuildPipelineInvoker.LocalBuildRequest.newIncrementalBuildRequest( project, resourceChanges );
        buildPipelineInvoker.invokeLocalBuildPipeLine( buildRequest, consumer );
    }

    private void invokeLocalBuildPipeLine( Project project,
                                           boolean suppressHandlers,
                                           DeploymentMode mode,
                                           Consumer< LocalBinaryConfig > consumer ) {
        BuildPipelineInvoker.LocalBuildRequest buildRequest = BuildPipelineInvoker.LocalBuildRequest.newFullBuildAndDeployRequest( project, toDeploymentType( mode ), suppressHandlers );
        buildPipelineInvoker.invokeLocalBuildPipeLine( buildRequest, consumer );
    }

    private LocalBuildConfig.DeploymentType toDeploymentType( DeploymentMode deploymentMode ) {
        return deploymentMode == DeploymentMode.VALIDATED ? LocalBuildConfig.DeploymentType.VALIDATED : LocalBuildConfig.DeploymentType.FORCED;
    }
}