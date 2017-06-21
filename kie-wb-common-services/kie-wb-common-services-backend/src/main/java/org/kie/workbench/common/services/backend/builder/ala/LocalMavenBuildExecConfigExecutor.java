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

import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import org.guvnor.ala.build.maven.config.MavenBuildExecConfig;
import org.guvnor.ala.build.maven.executor.MavenBuildExecConfigExecutor;
import org.guvnor.ala.build.maven.model.MavenBuild;
import org.guvnor.ala.build.maven.model.impl.MavenProjectBinaryBuildImpl;
import org.guvnor.ala.config.BinaryConfig;
import org.guvnor.ala.config.Config;
import org.guvnor.ala.exceptions.BuildException;
import org.guvnor.ala.registry.BuildRegistry;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.backend.server.ExtendedM2RepoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Executor for a LocalMavenBuildExecConfig configuration.
 */
@Specializes
public class LocalMavenBuildExecConfigExecutor
        extends MavenBuildExecConfigExecutor {

    private static final Logger logger = LoggerFactory.getLogger( LocalMavenBuildExecConfigExecutor.class );

    private ExtendedM2RepoService m2RepoService;

    public LocalMavenBuildExecConfigExecutor( ) {
        //Empty constructor for Weld proxying
    }

    @Inject
    public LocalMavenBuildExecConfigExecutor( BuildRegistry buildRegistry, ExtendedM2RepoService m2RepoService ) {
        super( buildRegistry );
        this.m2RepoService = m2RepoService;
    }

    @Override
    public Optional< BinaryConfig > apply( MavenBuild mavenBuild, MavenBuildExecConfig mavenBuildExecConfig ) {
        Optional< BinaryConfig > binaryConfig = super.apply( mavenBuild, mavenBuildExecConfig );
        if ( performDeploy( mavenBuildExecConfig ) &&
                binaryConfig.isPresent( ) &&
                binaryConfig.get( ) instanceof MavenProjectBinaryBuildImpl ) {
            MavenProjectBinaryBuildImpl mavenBinary = ( MavenProjectBinaryBuildImpl ) binaryConfig.get( );
            if ( !mavenBinary.getMavenBuildResult( ).hasExceptions( ) ) {
                GAV gav = new GAV( mavenBinary.getGroupId( ), mavenBinary.getArtifactId( ), mavenBinary.getVersion( ) );
                try (
                        final InputStream in = Files.newInputStream( mavenBinary.getPath( ).toFile( ).toPath( ) )
                ) {
                    m2RepoService.deployJar( in, gav );
                } catch ( Exception e ) {
                    logger.error( "Maven build deployment into local m2 repository failed. ", e );
                    throw new BuildException( "Maven build deployment into local m2 repository failed.", e );
                }
            }
        }
        return binaryConfig;
    }

    @Override
    public Class< ? extends Config > executeFor( ) {
        return LocalMavenBuildExecConfig.class;
    }

    private boolean performDeploy( MavenBuildExecConfig mavenBuildExecConfig ) {
        return mavenBuildExecConfig instanceof LocalMavenBuildExecConfig &&
                Boolean.parseBoolean( ( ( LocalMavenBuildExecConfig ) mavenBuildExecConfig ).getDeployIntoKieM2Repository( ) );
    }
}