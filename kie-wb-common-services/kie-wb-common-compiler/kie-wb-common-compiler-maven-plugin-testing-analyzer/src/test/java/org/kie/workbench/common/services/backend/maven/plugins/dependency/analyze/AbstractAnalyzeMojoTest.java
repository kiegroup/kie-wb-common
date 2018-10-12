/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.maven.plugins.dependency.analyze;

import java.util.EnumSet;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.TestUtilMaven;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenConfig;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieMavenCompilerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

import static org.assertj.core.api.Assertions.*;

public class AbstractAnalyzeMojoTest {

    private static Path tmpRoot;
    private String mavenRepoPath;
    private static Logger logger = LoggerFactory.getLogger(AbstractAnalyzeMojoTest.class);
    private String alternateSettingsAbsPath;

    @BeforeClass
    public static void setup() {
        System.setProperty("org.uberfire.nio.git.daemon.enabled", "false");
        System.setProperty("org.uberfire.nio.git.ssh.enabled", "false");
    }

    @Before
    public void setUp() throws Exception {
        mavenRepoPath = TestUtilMaven.getMavenRepo();
        tmpRoot = Files.createTempDirectory("repo");
        alternateSettingsAbsPath = TestUtilMaven.getSettingsFile();
    }

    @Test
    public void getUnusedDepsTest(){

        Path path = Paths.get(".").resolve("target/test-classes/dummy_unused_deps");
        final AFCompiler compiler = KieMavenCompilerFactory.getCompiler(EnumSet.of(KieDecorator.ENABLE_LOGGING, KieDecorator.STORE_UNUSED_DEPS));
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(path);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepoPath,
                                                               info,
                                                               new String[]{ MavenCLIArgs.COMPILE, MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath, MavenConfig.UNUSED_DEPS_IN_MEMORY},
                                                               Boolean.FALSE);

        CompilationResponse res = compiler.compile(req);
        assertThat(res.isSuccessful()).isTrue();
        assertThat(res.getUnusedDependencies()).isNotEmpty();
        assertThat(res.getUnusedDependencies()).hasSize(2);
        Set<String> unusedDeps = res.getUnusedDependencies();
        assertThat(unusedDeps.contains("org.springframework:spring-aop:jar:4.3.8.RELEASE:compile"));
        assertThat(unusedDeps.contains("com.fasterxml.jackson.core:jackson-databind:jar:2.8.8.1:compile"));
    }

}
