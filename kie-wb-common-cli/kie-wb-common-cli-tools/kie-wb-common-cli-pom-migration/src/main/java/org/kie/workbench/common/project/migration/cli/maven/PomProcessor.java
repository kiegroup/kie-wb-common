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
package org.kie.workbench.common.project.migration.cli.maven;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

public class PomProcessor {

    private static final Logger logger = LoggerFactory.getLogger(PomProcessor.class);
    private final static String POM_NAME = "pom.xml";
    private PomEditor editor;

    public PomProcessor() {
        editor = new PomEditor();
    }

    public static List<String> searchPoms(Path file) {
        List<String> poms = new ArrayList<>();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(file.toAbsolutePath())) {
            for (Path p : ds) {
                if (Files.isDirectory(p)) {
                    poms.addAll(searchPoms(p));
                } else if (p.endsWith(POM_NAME)) {
                    poms.add(p.toAbsolutePath().toString());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return poms;
    }

    public int processProject(String projectPath) {
        Path project = Paths.get("file:" + projectPath);
        List<String> pomsList = searchPoms(project);
        int counter = 0;
        for (String pom : pomsList) {
            Model model = editor.updatePom(Paths.get("file:" + pom));
            if (!model.getBuild().getPlugins().isEmpty()) {
                counter++;
            }
        }
        return counter;
    }

    public int processProject(String projectPath, String jsonPath) {
        Path project = Paths.get("file:" + projectPath);
        List<String> pomsList = searchPoms(project);
        int counter = 0;
        for (String pom : pomsList) {
            Model model = editor.updatePom(Paths.get("file:" + pom), jsonPath);
            if (model.getGroupId() != null && !model.getGroupId().isEmpty()) {
                counter++;
            }
        }
        return counter;
    }
}
