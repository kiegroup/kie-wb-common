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
package org.kie.workbench.common.services.backend.compiler.offprocess.generator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;

import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
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

public class ClasspathFileGenerator {

    private static Logger logger = LoggerFactory.getLogger(ClasspathFileGenerator.class);
    private static String module = "kie-wb-common-compiler-offprocess";

    public static void main(String[] args) throws Exception {
        String mavenRepo = createMavenRepo().toAbsolutePath().toString();
        String compilerPath = Paths.get(module + "/target/classes/compiler_classpath_prj/").toAbsolutePath().toString();
        String cp = "";
        logger.info("\n********************************\nBuild to generate the classpath template\n********************************");
        cp = createClasspathFile(mavenRepo, compilerPath);
        logger.info("\n************************************\nEnd build to generate the classpath template\n************************************\n\n");
        write(Paths.get(module + "/target/classes").toAbsolutePath().toString() + "/classpath.template", cp.replace(mavenRepo, "<maven_repo>"));
        logger.info("\n************************************\nSaving content to /target/classes/classpath.template \n************************************\n\n");
    }

    private static void write(String filename, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write(content);
        writer.close();
    }

    public static Path createMavenRepo() throws Exception {
        Path mavenRepository = Paths.get(System.getProperty("user.home"), "/.m2/repository");
        if (!Files.exists(mavenRepository)) {
            logger.info("Creating a m2_repo into " + mavenRepository);
            if (!Files.exists(Files.createDirectories(mavenRepository))) {
                throw new Exception("Folder not writable in the project");
            }
        }
        return mavenRepository;
    }

    private static String createClasspathFile(String mavenRepo, String projectPath) {
        AFCompiler compiler = KieMavenCompilerFactory.getCompiler(KieDecorator.CLASSPATH_DEPS_AFTER_DECORATOR);
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(URI.create("file://" + projectPath)));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.DEPENDENCY_RESOLVE, MavenConfig.DEPS_IN_MEMORY_BUILD_CLASSPATH},
                                                               Boolean.FALSE);
        CompilationResponse res = compiler.compile(req);
        StringBuffer cp = new StringBuffer();
        for (String dep : res.getDependencies()) {
            cp.append(dep.replace("file:", "")).append(":");
        }
        return cp.toString();
    }
}
