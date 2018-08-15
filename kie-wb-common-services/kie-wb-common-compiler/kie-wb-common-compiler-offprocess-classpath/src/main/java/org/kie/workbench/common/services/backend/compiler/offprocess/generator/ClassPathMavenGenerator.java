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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * Executed by Maven with the exec-maven-plugin after the execution of the maven-dependency-plugin.
 * <p>The dependency plugin create the offprocess.cpath file, this class read this file and replace the initial path
 * to the mavenrepo used with a placeholder (<maven_repo>).</p>
 * <p>This placeholder wil be replaced with the maven repo used,
 * and sent as a param by the compiler-offprocess module when
 * a offprocess build will be required.</p>
 */
public class ClassPathMavenGenerator {

    private static Logger logger = LoggerFactory.getLogger(ClassPathMavenGenerator.class);
    private static final String servicesMod = "kie-wb-common-services",
    compilerMod = "kie-wb-common-compiler",
    offprocessMod = "kie-wb-common-compiler-offprocess-classpath",
    cpathPathFile = "offprocess.cpath",
    classPathFile = "offprocess.classpath.template",
    SEP = File.separator;

    public static void main(String[] args) throws Exception {
        String kieVersion = args[0];
        String mavenRepo = getMavenRepo();
        Path pwd = Paths.get("").toAbsolutePath();
        StringBuilder sb = new StringBuilder();
        sb.append(pwd.toAbsolutePath()).append(SEP).
                append(servicesMod).append(SEP).
                append(compilerMod).append(SEP).
                append(offprocessMod).append(SEP).
                append(cpathPathFile);
        Path filePath = Paths.get(sb.toString());

        String content = new String(Files.readAllBytes(filePath));
        String replaced = content.replace(mavenRepo, "<maven_repo>");
        replaced = replaceTargetInTheClassPathFile(kieVersion, replaced);

        StringBuilder sbo = new StringBuilder();
                    sbo.append(pwd.toAbsolutePath()).append(SEP).
                            append(servicesMod).append(SEP).
                            append(compilerMod).append(SEP).
                            append(offprocessMod).append(SEP).
                            append("target").append(SEP).
                            append("classes").append(SEP).
                            append(classPathFile);
        Path offProcessModule = Paths.get(sbo.toString());
        write(offProcessModule.toAbsolutePath().toString(), replaced);
        logger.info("\n************************************\nSaving {} to {} \n************************************\n\n",classPathFile, offProcessModule.toAbsolutePath().toString());
    }

    @NotNull
    private static String replaceTargetInTheClassPathFile(String kieVersion, String replaced) {
        while(replaced.contains("target")){
            int targetIndex = replaced.lastIndexOf("target");
            String tmp = replaced.substring(0, targetIndex + 6);
            int lastDotsIndex = tmp.lastIndexOf(":",targetIndex);
            int longToJar = replaced.substring(lastDotsIndex+1).indexOf(".jar");
            String jarTmp = replaced.substring(lastDotsIndex+1, lastDotsIndex+1 +longToJar);
            String artifact = jarTmp.substring(jarTmp.lastIndexOf(File.separator)+1);
            String artifactNoVersionTmp = artifact.replace(kieVersion,"");
            String artifactNoVersion = artifactNoVersionTmp.substring(0,artifactNoVersionTmp.length()-1);
            String toClean = tmp.substring(lastDotsIndex+1);
            StringBuilder sbi = new StringBuilder();
            sbi.append("<maven_repo>").
                    append(SEP).
                    append("org").
                    append(SEP).
                    append("kie").
                    append(SEP).
                    append("workbench").
                    append(SEP).
                    append("services").
                    append(SEP).
                    append(artifactNoVersion).
                    append(SEP).
                    append(kieVersion);
            replaced = replaced.replace(toClean, sbi.toString());
        }
        return replaced;
    }

    private static void write(String filename, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write(content);
        writer.close();
    }

    public static String getMavenRepo() throws Exception {
        List<String> repos = Arrays.asList("M2_REPO", "MAVEN_REPO_LOCAL", "MAVEN_REPO", "M2_REPO_LOCAL");
        String mavenRepo = "";
        for (String repo : repos) {
            if (System.getenv(repo) != null) {
                mavenRepo = System.getenv(repo);
                break;
            }
        }
        return StringUtils.isEmpty(mavenRepo) ? createMavenRepo().toAbsolutePath().toString() : mavenRepo;
    }

    public static Path createMavenRepo() throws Exception {
        Path mavenRepository = Paths.get(System.getProperty("user.home"), ".m2/repository");
        if (!Files.exists(mavenRepository)) {
            logger.info("Creating a m2_repo into " + mavenRepository);
            if (!Files.exists(Files.createDirectories(mavenRepository))) {
                logger.error("Folder not writable to create Maven repo{}", mavenRepository);
                throw new Exception("Folder not writable to create Maven repo:"+mavenRepository);
            }
        }
        return mavenRepository;
    }
}
