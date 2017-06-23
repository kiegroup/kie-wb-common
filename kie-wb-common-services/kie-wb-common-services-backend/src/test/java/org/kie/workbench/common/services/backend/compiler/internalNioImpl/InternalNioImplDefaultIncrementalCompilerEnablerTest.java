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

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.TestUtil;
import org.kie.workbench.common.services.backend.compiler.configuration.Compilers;
import org.kie.workbench.common.services.backend.compiler.configuration.Decorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenArgs;
import org.kie.workbench.common.services.backend.compiler.internalNioImpl.impl.InternalNioImplDefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.internalNioImpl.impl.InternalNioImplDefaultIncrementalCompilerEnabler;
import org.kie.workbench.common.services.backend.compiler.internalNioImpl.impl.InternalNioImplMavenCompilerFactory;
import org.kie.workbench.common.services.backend.compiler.internalNioImpl.impl.InternalNioImplWorkspaceCompilationInfo;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.java.nio.file.api.FileSystemProviders;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

public class InternalNioImplDefaultIncrementalCompilerEnablerTest {

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
    public void testReadPomsInaPrjTest() throws Exception {

        FileSystemProvider fs = FileSystemProviders.getDefaultProvider();

        Path tmpRoot = Files.createTempDirectory("repo");
        //NIO creation and copy content
        java.nio.file.Path temp = java.nio.file.Files.createDirectories(java.nio.file.Paths.get(tmpRoot.toString(),
                                                                                                "dummy"));
        TestUtil.copyTree(java.nio.file.Paths.get("src/test/projects/dummy_multimodule_untouched"),
                          temp);
        //end NIO
        Path tmp = Paths.get(tmpRoot.toAbsolutePath().toString(),
                             "dummy");

        Path mainPom = Paths.get(temp.toAbsolutePath().toString(),
                                 "pom.xml");

        byte[] encoded = Files.readAllBytes(Paths.get(temp.toAbsolutePath().toString(),
                                                      "pom.xml"));
        String pomAsAstring = new String(encoded,
                                         StandardCharsets.UTF_8);
        Assert.assertFalse(pomAsAstring.contains("<artifactId>takari-lifecycle-plugin</artifactId>"));
        InternalNioImplWorkspaceCompilationInfo info = new InternalNioImplWorkspaceCompilationInfo(tmp,
                                                                                                   InternalNioImplMavenCompilerFactory.getCompiler(mavenRepo,
                                                                                                                                                   Decorator.NONE));

        InternalNioImplCompilationRequest req = new InternalNioImplDefaultCompilationRequest(info,
                                                                                             new String[]{MavenArgs.CLEAN, MavenArgs.COMPILE},
                                                                                             new HashMap<>(),
                                                                                             Optional.empty());
        InternalNioImplDefaultIncrementalCompilerEnabler enabler = new InternalNioImplDefaultIncrementalCompilerEnabler(Compilers.JAVAC);
        Assert.assertTrue(enabler.process(req).getResult());

        encoded = Files.readAllBytes(Paths.get(mainPom.toString()));
        pomAsAstring = new String(encoded,
                                  StandardCharsets.UTF_8);
        Assert.assertTrue(pomAsAstring.contains("<artifactId>takari-lifecycle-plugin</artifactId>"));

        InternalNioImplTestUtil.rm(tmpRoot.toFile());
    }

    @Test
    public void testReadKiePluginTest() throws Exception {

        Path tmpRoot = Files.createTempDirectory("repo");

        //NIO creation and copy content
        java.nio.file.Path temp = java.nio.file.Files.createDirectories(java.nio.file.Paths.get(tmpRoot.toString(),
                                                                                                "dummy"));
        TestUtil.copyTree(java.nio.file.Paths.get("src/test/projects/dummy_kie_multimodule_untouched"),
                          temp);
        //end NIO
        Path tmp = Paths.get(tmpRoot.toAbsolutePath().toString(),
                             "dummy");

        Path mainPom = Paths.get(tmp.toAbsolutePath().toString(),
                                 "pom.xml");
        byte[] encoded = Files.readAllBytes(Paths.get(tmp.toAbsolutePath().toString(),
                                                      "pom.xml"));
        String pomAsAstring = new String(encoded,
                                         StandardCharsets.UTF_8);
        Assert.assertFalse(pomAsAstring.contains("<artifactId>takari-lifecycle-plugin</artifactId>"));

        InternalNioImplWorkspaceCompilationInfo info = new InternalNioImplWorkspaceCompilationInfo(tmp,
                                                                                                   InternalNioImplMavenCompilerFactory.getCompiler(mavenRepo,
                                                                                                                                                   Decorator.NONE));
        InternalNioImplCompilationRequest req = new InternalNioImplDefaultCompilationRequest(info,
                                                                                             new String[]{MavenArgs.CLEAN, MavenArgs.COMPILE},
                                                                                             new HashMap<>(),
                                                                                             Optional.empty());
        InternalNioImplDefaultIncrementalCompilerEnabler enabler = new InternalNioImplDefaultIncrementalCompilerEnabler(Compilers.JAVAC);
        Assert.assertTrue(enabler.process(req).getResult());

        Assert.assertTrue(info.isKiePluginPresent());

        encoded = Files.readAllBytes(Paths.get(mainPom.toString()));
        pomAsAstring = new String(encoded,
                                  StandardCharsets.UTF_8);
        //Assert.assertTrue(pomAsAstring.contains("<artifactId>takari-lifecycle-plugin</artifactId>"));

        Assert.assertTrue(pomAsAstring.contains("kie-maven-plugin"));

        InternalNioImplTestUtil.rm(tmpRoot.toFile());
    }
}
