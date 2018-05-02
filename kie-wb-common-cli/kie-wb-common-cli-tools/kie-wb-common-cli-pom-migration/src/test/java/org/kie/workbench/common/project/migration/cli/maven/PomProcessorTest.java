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

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.migration.cli.RealSystemAccess;
import org.kie.workbench.common.migration.cli.SystemAccess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PomProcessorTest {

    private PomProcessor processor;
    private String GENERIC_PRJ = "/target/test-classes/generic/";
    private String SRC_GENERIC_PRJ = "/src/test/projects/generic/";
    private String currentDir;
    private Path path;
    private Path pathSrc;

    @Before
    public void setUp() {
        processor = new PomProcessor(new RealSystemAccess());
        currentDir = new File("").getAbsolutePath();
        path = Paths.get(currentDir + GENERIC_PRJ);
        pathSrc = Paths.get(currentDir + SRC_GENERIC_PRJ);
    }

    @After
    public void cleanUp() throws Exception {
        FileUtils.copyDirectory(pathSrc.toFile(), path.toFile());
    }

    @Test
    public void processProjectTest() {
        try {
            int pomProcessed = processor.processProject(path.toAbsolutePath().toString());
            assertEquals(1, pomProcessed);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
