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
package org.kie.workbench.common.services.backend.compiler.internalNioImpl;

import java.util.HashMap;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.TestUtil;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.configuration.Decorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenArgs;
import org.kie.workbench.common.services.backend.compiler.internalNioImpl.impl.InternalNioImplDefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.internalNioImpl.impl.InternalNioImplMavenCompilerFactory;
import org.kie.workbench.common.services.backend.compiler.internalNioImpl.impl.InternalNioImplWorkspaceCompilationInfo;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

public class InternalNioImplDefaultMavenIncrementalCompilerTest {

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
        //NIO creation and copy content
        java.nio.file.Path temp = java.nio.file.Files.createDirectories(java.nio.file.Paths.get(tmpRoot.toString(),
                                                                                                "dummy"));
        TestUtil.copyTree(java.nio.file.Paths.get("src/test/projects/dummy"),
                          temp);
        //end NIO

        InternalNioImplMavenCompiler compiler = InternalNioImplMavenCompilerFactory.getCompiler(mavenRepo,
                                                                                                Decorator.NONE);
        Assert.assertTrue(compiler.isValid());

        InternalNioImplWorkspaceCompilationInfo info = new InternalNioImplWorkspaceCompilationInfo(tmp,
                                                                                                   compiler);
        InternalNioImplCompilationRequest req = new InternalNioImplDefaultCompilationRequest(info,
                                                                                             new String[]{MavenArgs.VERSION},
                                                                                             new HashMap<>(),
                                                                                             Optional.empty());
        CompilationResponse res = compiler.compileSync(req);
        Assert.assertTrue(res.isSuccessful());

        InternalNioImplTestUtil.rm(tmpRoot.toFile());
    }

    @Test()
    public void testIncompleteArguments() {
        InternalNioImplMavenCompiler compiler = InternalNioImplMavenCompilerFactory.getCompiler(Paths.get(""),
                                                                                                Decorator.NONE);
        Assert.assertFalse(compiler.isValid());
    }

    @Test
    public void testIncrementalWithPluginEnabled() throws Exception {
        Path tmpRoot = Files.createTempDirectory("repo");
        Path tmp = Files.createDirectories(Paths.get(tmpRoot.toString(),
                                                     "dummy"));
        //NIO creation and copy content
        java.nio.file.Path temp = java.nio.file.Files.createDirectories(java.nio.file.Paths.get(tmpRoot.toString(),
                                                                                                "dummy"));
        TestUtil.copyTree(java.nio.file.Paths.get("src/test/projects/dummy"),
                          temp);
        //end NIO

        InternalNioImplMavenCompiler compiler = InternalNioImplMavenCompilerFactory.getCompiler(mavenRepo,
                                                                                                Decorator.NONE);

        InternalNioImplWorkspaceCompilationInfo info = new InternalNioImplWorkspaceCompilationInfo(tmp,
                                                                                                   compiler);
        InternalNioImplCompilationRequest req = new InternalNioImplDefaultCompilationRequest(info,
                                                                                             new String[]{MavenArgs.CLEAN, MavenArgs.COMPILE},
                                                                                             new HashMap<>(),
                                                                                             Optional.empty());
        CompilationResponse res = compiler.compileSync(req);
        Assert.assertTrue(res.isSuccessful());

        Path incrementalConfiguration = Paths.get(tmp.toAbsolutePath().toString(),
                                                  "/target/incremental/io.takari.maven.plugins_takari-lifecycle-plugin_compile_compile");
        Assert.assertTrue(incrementalConfiguration.toFile().exists());

        InternalNioImplTestUtil.rm(tmpRoot.toFile());
    }

    @Test
    public void testIncrementalWithPluginEnabledThreeTime() throws Exception {
        Path tmpRoot = Files.createTempDirectory("repo");
        Path tmp = Files.createDirectories(Paths.get(tmpRoot.toString(),
                                                     "dummy"));
        //NIO creation and copy content
        java.nio.file.Path temp = java.nio.file.Files.createDirectories(java.nio.file.Paths.get(tmpRoot.toString(),
                                                                                                "dummy"));
        TestUtil.copyTree(java.nio.file.Paths.get("src/test/projects/dummy"),
                          temp);
        //end NIO

        InternalNioImplMavenCompiler compiler = InternalNioImplMavenCompilerFactory.getCompiler(mavenRepo,
                                                                                                Decorator.NONE);

        InternalNioImplWorkspaceCompilationInfo info = new InternalNioImplWorkspaceCompilationInfo(tmp,
                                                                                                   compiler);
        InternalNioImplCompilationRequest req = new InternalNioImplDefaultCompilationRequest(info,
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

        InternalNioImplTestUtil.rm(tmpRoot.toFile());
    }
}
