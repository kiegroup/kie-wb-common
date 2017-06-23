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

public class NioMavenOutputTest {

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
                                                                     Optional.of("log"));
        CompilationResponse res = compiler.compileSync(req);
        Assert.assertTrue(res.isSuccessful());
        Assert.assertTrue(res.getMavenOutput().isPresent());
        Assert.assertTrue(res.getMavenOutput().get().size() > 0);

        TestUtil.rm(tmpRoot.toFile());
    }
}
