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

package org.kie.workbench.common.services.backend.builder.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.ala.build.maven.model.MavenBuildMessage;
import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.shared.message.Level;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.shared.builder.service.MavenBuildService;
import org.kie.workbench.common.services.backend.builder.ala.BuildPipelineInvoker;

@Service
@ApplicationScoped
public class MavenBuildServiceImpl
        implements MavenBuildService {

    private BuildPipelineInvoker buildPipelineInvoker;

    public MavenBuildServiceImpl( ) {
        //Empty constructor for Weld proxying
    }

    @Inject
    public MavenBuildServiceImpl( final BuildPipelineInvoker buildPipelineInvoker ) {
        this.buildPipelineInvoker = buildPipelineInvoker;
    }

    @Override
    public BuildResults build( Project project, boolean deploy ) {
        final BuildResults results = new BuildResults( project.getPom( ).getGav( ) );
        buildPipelineInvoker.invokeMavenBuildPipeline(
                BuildPipelineInvoker.MavenBuildRequest.newMavenBuildRequest( project, deploy ),
                mavenBinary -> {
                    results.addAllBuildMessages( translateBuildMessages( mavenBinary.getMavenBuildResult( ).getBuildMessages( ) ) );
                    results.addAllBuildMessages( translateBuildExceptions( mavenBinary.getMavenBuildResult( ).getBuildExceptions( ) ) );
                } );
        return results;
    }

    private List< BuildMessage > translateBuildMessages( List< MavenBuildMessage > mavenBuildMessages ) {
        if ( mavenBuildMessages != null ) {
            return mavenBuildMessages.stream( ).map( this::translateBuildMessage ).collect( Collectors.toList( ) );
        } else {
            return new ArrayList<>( );
        }
    }

    private BuildMessage translateBuildMessage( MavenBuildMessage mavenBuildMessage ) {
        switch ( mavenBuildMessage.getLevel( ) ) {
            case DEBUG:
            case INFO:
                return newBuildMessage( Level.INFO, mavenBuildMessage.getMessage( ) );
            case WARNING:
                return newBuildMessage( Level.WARNING, mavenBuildMessage.getMessage( ) );
            case ERROR:
            case FATAL_ERROR:
                return newBuildMessage( Level.ERROR, mavenBuildMessage.getMessage( ) );
            default:
                return newBuildMessage( Level.INFO, mavenBuildMessage.getMessage( ) );
        }
    }

    private List< BuildMessage > translateBuildExceptions( List< String > mavenExceptions ) {
        if ( mavenExceptions != null ) {
            return mavenExceptions.stream( ).map( this::translateMavenException ).collect( Collectors.toList( ) );
        } else {
            return new ArrayList<>( );
        }
    }

    private BuildMessage translateMavenException( String mavenException ) {
        return newBuildMessage( Level.ERROR, mavenException );
    }

    private BuildMessage newBuildMessage( Level level, String message ) {
        BuildMessage buildMessage = new BuildMessage( );
        buildMessage.setLevel( level );
        buildMessage.setText( message );
        return buildMessage;
    }

}