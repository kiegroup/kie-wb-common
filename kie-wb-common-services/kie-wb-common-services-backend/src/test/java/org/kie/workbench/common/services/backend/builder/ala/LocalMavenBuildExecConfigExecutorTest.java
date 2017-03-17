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
import java.util.Optional;

import org.guvnor.ala.build.maven.executor.MavenBuildExecConfigExecutorTestBase;
import org.guvnor.ala.config.BinaryConfig;
import org.guvnor.ala.registry.BuildRegistry;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.backend.server.ExtendedM2RepoService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class LocalMavenBuildExecConfigExecutorTest
        extends MavenBuildExecConfigExecutorTestBase
        implements MavenBuildConstants {

    @Mock
    private BuildRegistry buildRegistry;

    @Mock
    private ExtendedM2RepoService m2RepoService;

    @Mock
    private LocalMavenBuildExecConfig localMavenBuildExecConfig;

    private LocalMavenBuildExecConfigExecutor executor;

    @Before
    public void setUp( ) throws Exception {
        setUpTestProject();
        executor = new LocalMavenBuildExecConfigExecutor( buildRegistry, m2RepoService );
    }

    @Test
    public void testApplyForProjectBuild( ) throws Exception {
        doTestBuild( );
        verify( m2RepoService, never( ) ).deployJar( any( InputStream.class ), any( GAV.class ) );
        clearTempDir( );
    }

    @Test
    public void testApplyForProjectBuildAndDeploy( ) throws Exception {
        when( localMavenBuildExecConfig.getDeployIntoKieM2Repository( ) ).thenReturn( Boolean.toString( true ) );
        doTestBuild( );
        verify( m2RepoService, times( 1 ) ).deployJar( any( InputStream.class ), eq( TEST_PROJECT_GAV ) );
        clearTempDir( );
    }

    private void doTestBuild( ) {
        prepareMavenBuild();
        Optional< BinaryConfig > result = executor.apply( mavenBuild, localMavenBuildExecConfig );
        verifyBinary( result );
    }

    public String getTestProject() {
        return "MavenBuildTest";
    }

    @Override
    protected String getTestProjectJar( ) {
        return TEST_PROJECT_JAR;
    }
}