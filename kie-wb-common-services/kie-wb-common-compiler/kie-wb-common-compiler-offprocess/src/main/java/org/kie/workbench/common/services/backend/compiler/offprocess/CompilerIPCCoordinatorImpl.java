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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import net.openhft.chronicle.queue.ChronicleQueue;
import org.apache.commons.io.IOUtils;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultKieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompilerIPCCoordinatorImpl implements CompilerIPCCoordinator {

    private static String placeholder = "<maven_repo>";
    private String javaHome;
    private String javaBin;
    private Logger logger = LoggerFactory.getLogger(CompilerIPCCoordinatorImpl.class);
    private String classpathTemplate;
    private ChronicleQueue queue;

    public CompilerIPCCoordinatorImpl() {
        queue = QueueProvider.getQueue();
        javaHome = System.getProperty("java.home");
        javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        try {
            classpathTemplate = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("classpath.template"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public CompilationResponse compile(CompilationRequest req) {
        return internalBuild(req.getMavenRepo(),
                             req.getInfo().getPrjPath().toAbsolutePath().toString(),
                             getAlternateSettings(req.getOriginalArgs()));
    }

    private String getAlternateSettings(String[] args) {
        for (String arg : args) {
            if (arg.startsWith(MavenCLIArgs.ALTERNATE_USER_SETTINGS)) {
                return arg.substring(2, arg.length());
            }
        }
        return "";
    }

    private CompilationResponse internalBuild(String mavenRepo, String projectPath, String alternateSettingsAbsPath) {
        String classpath = classpathTemplate.replace(placeholder, mavenRepo);
        String uuid = UUID.randomUUID().toString();
        try {
            invokeServerBuild(mavenRepo, projectPath, uuid, classpath, alternateSettingsAbsPath);
            KieCompilationResponse res = ClientIPC.listenObjs(uuid);
            if (res != null) {
                return res;
            } else {
                return new DefaultKieCompilationResponse(true);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new DefaultKieCompilationResponse(false);
        }
    }

    private void invokeServerBuild(String mavenRepo, String projectPath, String uuid, String classpath, String alternateSettingsAbsPath) throws Exception {
        String[] commandArrayServer =
                {
                        javaBin,
                        "-cp",
                        System.getProperty("user.dir") + "/" + "target/kie-wb-common-compiler-offprocess-7.10.0-SNAPSHOT.jar:" + classpath,
                        "org.kie.workbench.common.services.backend.compiler.offprocess.ServerAppender",
                        uuid,
                        projectPath,
                        mavenRepo,
                        alternateSettingsAbsPath
                };
        logger.info("************************** \n Invoking server in a separate process with args: \n{} \n{} \n{} \n{} \n{} \n{} \n{} \n**************************", commandArrayServer);
        ProcessBuilder serverPb = new ProcessBuilder(commandArrayServer);
        serverPb.directory(new File(projectPath));
        serverPb.redirectErrorStream(true);
        serverPb.inheritIO();
        writeStdOut(serverPb, "Waiting for client.", 10);
    }

    private void writeStdOut(ProcessBuilder builder, String terminationMsg, int secondsTimeout) throws Exception {
        Process process = builder.start();
        process.waitFor(secondsTimeout, TimeUnit.SECONDS);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null && (!line.endsWith("BUILD SUCCESS") || !line.endsWith("BUILD FAILURE") )) {
            if (logger.isInfoEnabled()) {
                logger.info(line);
            }
        }
        if (line != null) {
            if (logger.isInfoEnabled()) {
                logger.info(line);
            }
            return;
        }
    }
}
