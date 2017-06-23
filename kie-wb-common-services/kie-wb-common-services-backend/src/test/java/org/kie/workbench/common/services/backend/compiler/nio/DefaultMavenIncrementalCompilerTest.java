/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.compiler.nio;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.TestUtil;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.configuration.Decorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenArgs;
import org.kie.workbench.common.services.backend.compiler.nio.impl.NIODefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.impl.NIOMavenCompilerFactory;
import org.kie.workbench.common.services.backend.compiler.nio.impl.NIOWorkspaceCompilationInfo;

public class DefaultMavenIncrementalCompilerTest {

    private Path mavenRepo;

    @Before
    public void setUp() throws Exception {
        mavenRepo = Paths.get(System.getProperty("user.home"),
                              "/.m2/repository");

        if (!Files.exists(mavenRepo)) {
            System.out.println("Creating a m2_repo into " + mavenRepo);
            if (!Files.exists(Files.createDirectories(mavenRepo))) {
                throw new Exception("Folder not writable in the project");
            }
        }
    }

    @Test
    public void testIsValidMavenHome() throws Exception {
        Path tmpRoot = Files.createTempDirectory("repo");
        Path tmp = Files.createDirectories(Paths.get(tmpRoot.toString(),
                                                     "dummy"));
        TestUtil.copyTree(Paths.get("src/test/projects/dummy"),
                          tmp);

        NIOMavenCompiler compiler = NIOMavenCompilerFactory.getCompiler(mavenRepo,
                                                                        Decorator.NONE);
        Assert.assertTrue(compiler.isValid());

        NIOWorkspaceCompilationInfo info = new NIOWorkspaceCompilationInfo(tmp,
                                                                           compiler);
        NIOCompilationRequest req = new NIODefaultCompilationRequest(info,
                                                                     new String[]{MavenArgs.VERSION},
                                                                     new HashMap(),
                                                                     Optional.empty());
        CompilationResponse res = compiler.compileSync(req);
        Assert.assertTrue(res.isSuccessful());

        TestUtil.rm(tmpRoot.toFile());
    }

    @Test()
    public void testIncompleteArguments() {
        NIOMavenCompiler compiler = NIOMavenCompilerFactory.getCompiler(Paths.get(""),
                                                                        Decorator.NONE);
        Assert.assertFalse(compiler.isValid());
    }

    @Test
    public void testIncrementalWithPluginEnabled() throws Exception {
        Path tmpRoot = Files.createTempDirectory("repo");
        Path tmp = Files.createDirectories(Paths.get(tmpRoot.toString(),
                                                     "dummy"));
        TestUtil.copyTree(Paths.get("src/test/projects/dummy"),
                          tmp);

        NIOMavenCompiler compiler = NIOMavenCompilerFactory.getCompiler(mavenRepo,
                                                                        Decorator.NONE);

        NIOWorkspaceCompilationInfo info = new NIOWorkspaceCompilationInfo(tmp,
                                                                           compiler);
        NIOCompilationRequest req = new NIODefaultCompilationRequest(info,
                                                                     new String[]{MavenArgs.CLEAN, MavenArgs.COMPILE},
                                                                     new HashMap<>(),
                                                                     Optional.empty());
        CompilationResponse res = compiler.compileSync(req);
        Assert.assertTrue(res.isSuccessful());

        Path incrementalConfiguration = Paths.get(tmp.toAbsolutePath().toString(),
                                                  "/target/incremental/io.takari.maven.plugins_takari-lifecycle-plugin_compile_compile");
        Assert.assertTrue(incrementalConfiguration.toFile().exists());

        TestUtil.rm(tmpRoot.toFile());
    }

    @Test
    public void testIncrementalWithPluginEnabledThreeBuild() throws Exception {
        Path tmpRoot = Files.createTempDirectory("repo");
        Path tmp = Files.createDirectories(Paths.get(tmpRoot.toString(),
                                                     "dummy"));
        TestUtil.copyTree(Paths.get("src/test/projects/dummy"),
                          tmp);

        NIOMavenCompiler compiler = NIOMavenCompilerFactory.getCompiler(mavenRepo,
                                                                        Decorator.NONE);

        NIOWorkspaceCompilationInfo info = new NIOWorkspaceCompilationInfo(tmp,
                                                                           compiler);
        NIOCompilationRequest req = new NIODefaultCompilationRequest(info,
                                                                     new String[]{MavenArgs.CLEAN, MavenArgs.COMPILE},
                                                                     new HashMap<>(),
                                                                     Optional.empty());
        CompilationResponse res = compiler.compileSync(req);
        Assert.assertTrue(res.isSuccessful());

        res = compiler.compileSync(req);
        Assert.assertTrue(res.isSuccessful());

        res = compiler.compileSync(req);
        Assert.assertTrue(res.isSuccessful());

        Path incrementalConfiguration = Paths.get(tmp.toAbsolutePath().toString(),
                                                  "/target/incremental/io.takari.maven.plugins_takari-lifecycle-plugin_compile_compile");
        Assert.assertTrue(incrementalConfiguration.toFile().exists());

        TestUtil.rm(tmpRoot.toFile());
    }
}
