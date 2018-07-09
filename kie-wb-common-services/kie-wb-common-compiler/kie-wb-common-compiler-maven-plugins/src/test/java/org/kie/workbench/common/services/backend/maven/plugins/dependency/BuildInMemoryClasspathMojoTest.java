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
package org.kie.workbench.common.services.backend.maven.plugins.dependency;

import java.io.File;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieMavenCompilerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class BuildInMemoryClasspathMojoTest {

    private static Path tmpRoot;
    private Path mavenRepo;
    private static Logger logger = LoggerFactory.getLogger(BuildInMemoryClasspathMojoTest.class);
    private String alternateSettingsAbsPath;

    @Before
    public void setUp() throws Exception {
        mavenRepo = Paths.get(System.getProperty("user.home"), "/.m2/repository");

        if (!Files.exists(mavenRepo)) {
            if (!Files.exists(Files.createDirectories(mavenRepo))) {
                throw new Exception("Folder not writable in the project");
            }
        }


        mavenRepo = Paths.get(System.getProperty("user.home"),
                              "/.m2/repository");

        if (!Files.exists(mavenRepo)) {
            logger.info("Creating a m2_repo into " + mavenRepo);
            if (!Files.exists(Files.createDirectories(mavenRepo))) {
                throw new Exception("Folder not writable in the project");
            }
        }
        tmpRoot = Files.createTempDirectory("repo");
        alternateSettingsAbsPath = new File("src/test/settings.xml").getAbsolutePath();

    }


    @Test
    public void getClassloaderFromAllDependenciesTestSimple(){

        Path path = Paths.get(".").resolve("src/test/projects/dummy_deps_simple");
        AFCompiler compiler = KieMavenCompilerFactory.getCompiler(KieDecorator.CLASSPATH_DEPS_AFTER_DECORATOR);
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(path);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                               info,
                                                               new String[]{ "-X", MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath},
                                                               Boolean.FALSE);

        CompilationResponse res = compiler.compile(req);
        assertThat(res.isSuccessful()).isTrue();
        assertThat(res.getDependencies().isEmpty()).isFalse();
        assertThat(res.getDependencies().size()).isEqualTo(4);
    }

    @Test
    public void getClassloaderFromAllDependenciesTestComplex() {

        Path path = Paths.get(".").resolve("src/test/projects/dummy_deps_complex");
        AFCompiler compiler = KieMavenCompilerFactory.getCompiler(KieDecorator.CLASSPATH_DEPS_AFTER_DECORATOR);
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(path);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                               info,
                                                               new String[]{"-X", MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath},
                                                               Boolean.FALSE);

        CompilationResponse res = compiler.compile(req);
        assertThat(res.isSuccessful()).isTrue();
        assertThat(res.getDependencies().isEmpty()).isFalse();
        assertThat(res.getDependencies().size()).isEqualTo(7);
    }

    @AfterClass
    public static void tearDown() {
        if(tmpRoot!= null) {
            rm(tmpRoot.toFile());
        }
    }

    public static void rm(File f) {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                rm(c);
            }
        }
        if (!f.delete()) {
            logger.error("Couldn't delete file {}", f);
        }
    }

}
