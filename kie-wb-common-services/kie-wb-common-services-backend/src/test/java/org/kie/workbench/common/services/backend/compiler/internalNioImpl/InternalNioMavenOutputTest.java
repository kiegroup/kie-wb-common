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

public class InternalNioMavenOutputTest {

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
    public void testOutputWithTakari() throws Exception {
        java.nio.file.Path tmpRoot = java.nio.file.Files.createTempDirectory("repo");
        java.nio.file.Path tmpNio = java.nio.file.Files.createDirectories(java.nio.file.Paths.get(tmpRoot.toString(),
                                                                                                  "dummy"));
        TestUtil.copyTree(java.nio.file.Paths.get("src/test/projects/dummy"),
                          tmpNio);

        Path tmp = Paths.get(tmpNio.toAbsolutePath().toString());

        InternalNioImplMavenCompiler compiler = InternalNioImplMavenCompilerFactory.getCompiler(mavenRepo,
                                                                                                Decorator.NONE);

        InternalNioImplWorkspaceCompilationInfo info = new InternalNioImplWorkspaceCompilationInfo(tmp,
                                                                                                   compiler);
        InternalNioImplCompilationRequest req = new InternalNioImplDefaultCompilationRequest(info,
                                                                                             new String[]{MavenArgs.CLEAN, MavenArgs.COMPILE},
                                                                                             new HashMap<>(),
                                                                                             Optional.of("log"));
        CompilationResponse res = compiler.compileSync(req);
        Assert.assertTrue(res.isSuccessful());
        Assert.assertTrue(res.getMavenOutput().isPresent());
        Assert.assertTrue(res.getMavenOutput().get().size() > 0);

        InternalNioImplTestUtil.rm(tmpRoot.toFile());
    }
}
