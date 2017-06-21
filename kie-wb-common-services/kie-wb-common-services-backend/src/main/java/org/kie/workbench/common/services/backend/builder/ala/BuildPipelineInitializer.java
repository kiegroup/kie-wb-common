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
import java.util.List;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.ala.build.maven.config.MavenBuildConfig;
import org.guvnor.ala.build.maven.config.MavenProjectConfig;
import org.guvnor.ala.config.BinaryConfig;
import org.guvnor.ala.config.BuildConfig;
import org.guvnor.ala.config.ProjectConfig;
import org.guvnor.ala.config.SourceConfig;
import org.guvnor.ala.pipeline.ConfigExecutor;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.PipelineFactory;
import org.guvnor.ala.pipeline.Stage;
import org.guvnor.ala.pipeline.execution.PipelineExecutor;
import org.guvnor.ala.registry.PipelineRegistry;
import org.guvnor.ala.source.git.config.GitConfig;
import org.kie.workbench.common.services.backend.builder.ala.impl.LocalBuildConfigImpl;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;

import static org.guvnor.ala.pipeline.StageUtil.*;

/**
 * This class is responsible for the build pipeline initialization at system startup. Additionally it provides access
 * to a pipeline executor for interested parties on having raw access to the build pipeline execution.
 */
@ApplicationScoped
@Startup( StartupType.BOOTSTRAP )
public class BuildPipelineInitializer {

    public static final String LOCAL_BUILD_PIPELINE = "local-build-pipeline";

    public static final String MAVEN_BUILD_PIPELINE = "maven-build-pipeline";

    private PipelineRegistry pipelineRegistry;

    private Instance< ConfigExecutor > configExecutors;

    private PipelineExecutor executor;

    public BuildPipelineInitializer( ) {
        //Empty constructor for Weld proxying
    }

    @Inject
    public BuildPipelineInitializer( final PipelineRegistry pipelineRegistry,
                                     final Instance< ConfigExecutor > configExecutors ) {
        this.pipelineRegistry = pipelineRegistry;
        this.configExecutors = configExecutors;
    }

    /**
     * Intended mainly for testing
     */
    public BuildPipelineInitializer( PipelineRegistry pipelineRegistry,
                                     Collection< ConfigExecutor > configs ) {
        this.pipelineRegistry = pipelineRegistry;
        initPipelines();
        initExecutor( configs );
    }

    /**
     * @return A pipeline executor for executing the initialized pipelines.
     */
    @Produces
    @Named("buildPipelineExecutor")
    public PipelineExecutor getExecutor( ) {
        return executor;
    }

    @PostConstruct
    private void init( ) {
        initPipelines();
        initExecutor( );
    }

    private void initPipelines() {
        initLocalBuildPipeline();
        initMavenBuildPipeline();
    }

    /**
     * Initializes a build pipeline based on local structures and optimizations required by the workbench.
     */
    private void initLocalBuildPipeline( ) {

        final Stage< Input, SourceConfig > sourceConfigStage = config( "Local Source Config",
                ( s ) -> new LocalSourceConfig( ) { } );

        final Stage< SourceConfig, ProjectConfig > projectConfigStage = config( "Local Project Config",
                ( s ) -> new LocalProjectConfig( ) { } );

        final Stage< ProjectConfig, BuildConfig > localBuildConfigStage = config( "Local Build Config",
                ( s ) -> new LocalBuildConfigImpl( ) );

        final Stage< BuildConfig, BinaryConfig > localBuildExecStage = config( "Local Build Exec",
                ( s ) -> new LocalBuildExecConfig( ) { } );

        final Pipeline localBuildPipeline = PipelineFactory
                .startFrom( sourceConfigStage )
                .andThen( projectConfigStage )
                .andThen( localBuildConfigStage )
                .andThen( localBuildExecStage )
                .buildAs( LOCAL_BUILD_PIPELINE );
        pipelineRegistry.registerPipeline( localBuildPipeline );
    }

    /**
     * Initializes a build pipeline based on a maven build.
     */
    private void initMavenBuildPipeline( ) {
        final Stage< Input, SourceConfig > sourceConfig = config( "Git Source",
                ( s ) -> new GitConfig( ) { } );

        final Stage< SourceConfig, ProjectConfig > projectConfig = config( "Maven Project",
                ( s ) -> new MavenProjectConfig( ) {
        } );

        final Stage< ProjectConfig, BuildConfig > buildConfig = config( "Maven Build Config",
                ( s ) -> new MavenBuildConfig( ) {
                    @Override
                    public List< String > getGoals( ) {
                        final List< String > result = new ArrayList<>( );
                        result.add( "clean" );
                        result.add( "package" );
                        return result;
                    }

                    @Override
                    public Properties getProperties( ) {
                        final Properties result = new Properties( );
                        result.setProperty( "failIfNoTests", "false" );
                        return result;
                    }
                } );

        final Stage< BuildConfig, BinaryConfig > buildExecution = config( "Local Maven Build",
                ( s ) -> new LocalMavenBuildExecConfig( ) {
                } );

        final Pipeline alaBuilderPipeline = PipelineFactory
                .startFrom( sourceConfig )
                .andThen( projectConfig )
                .andThen( buildConfig )
                .andThen( buildExecution )
                .buildAs( MAVEN_BUILD_PIPELINE );
        pipelineRegistry.registerPipeline( alaBuilderPipeline );
    }

    private void initExecutor( ) {
        final Collection< ConfigExecutor > configs = new ArrayList<>( );
        configExecutors.iterator( ).forEachRemaining( configs::add );
        initExecutor( configs );
    }

    private void initExecutor( final Collection< ConfigExecutor > configs ) {
        executor = new PipelineExecutor( configs );
    }
}