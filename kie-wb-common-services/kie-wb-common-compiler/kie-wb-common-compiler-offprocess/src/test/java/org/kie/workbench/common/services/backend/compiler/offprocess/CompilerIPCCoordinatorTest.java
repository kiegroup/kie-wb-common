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
package org.kie.workbench.common.services.backend.compiler.offprocess;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.offprocess.utils.TestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

public class CompilerIPCCoordinatorTest {

    private static Path prjPath;
    private Path mavenRepo;
    private String alternateSettingsAbsPath;
    private Logger logger = LoggerFactory.getLogger(CompilerIPCCoordinatorTest.class);


    @Before
    public void setUp() throws Exception {
        mavenRepo = TestUtil.createMavenRepo();
        prjPath = Paths.get("target/test-classes/kjar-2-single-resources");
        alternateSettingsAbsPath = new File("src/test/settings.xml").getAbsolutePath();
    }

    @Test
    public void offProcessBuildTest(){

        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(prjPath);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                               info,
                                                               new String[]{
                                                                       MavenCLIArgs.COMPILE,
                                                                       MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath
                                                               },
                                                               Boolean.FALSE);
        CompilerIPCCoordinator compiler = new CompilerIPCCoordinatorImpl();
        CompilationResponse res = compiler.compile(req, 10);
        Assert.assertNotNull(res);
        Assert.assertTrue(res.isSuccessful());
    }

}
