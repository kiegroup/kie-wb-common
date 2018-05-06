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
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.migration.cli.RealSystemAccess;
import org.kie.workbench.common.migration.cli.SystemAccess;
import org.kie.workbench.common.project.migration.cli.ServiceCDIWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PomEditorTest {

    private final Logger logger = LoggerFactory.getLogger(PomEditorTest.class);
    private PomEditor editor;
    private String currentDir;
    private String CURRICULUM_COURSE_PRJ = "/target/test-classes/curriculumcourse/";
    private String DINNER_PARTY_PRJ = "/target/test-classes/dinnerparty/";
    private String EMPLOYEE_ROSTERING_PRJ = "/target/test-classes/employee-rostering/";
    private String EVALUATION_PRJ = "/target/test-classes/employee-rostering/";
    private String ITORDERS_PRJ = "/target/test-classes/itorders/";
    private String MORTGAGES_PRJ = "/target/test-classes/mortgages/";
    private String OPTACLOUD_PRJ = "/target/test-classes/optacloud/";
    private WeldContainer weldContainer;
    private ServiceCDIWrapper cdiWrapper;

    @Before
    public void setUp() {
        weldContainer = new Weld().initialize();
        cdiWrapper = weldContainer.instance().select(ServiceCDIWrapper.class).get();
        currentDir = new File(".").getAbsolutePath();
        editor = new PomEditor(new RealSystemAccess(),cdiWrapper);
    }

    @After
    public void tearDown() {
        weldContainer.close();
    }

    private void testDefault(String prj) {
        Path path = Paths.get("file://" + currentDir + prj + "pom.xml");
        Path pathCopy = Paths.get("file://" + currentDir + prj + "copy_pom.xml");
        Files.copy(path, pathCopy);
        try {
            Model original = editor.getModel(path);
            assertTrue(original.getBuild().getPlugins().size() == 1);

            Model modelUpdated = editor.updatePom(path);
            assertEquals(modelUpdated.getPackaging(),"kjar");
            assertNotNull(modelUpdated);
            assertTrue(modelUpdated.getBuild().getPlugins().size() == 1);
            List<Dependency> deps = modelUpdated.getDependencies();
            for (Dependency dep : deps) {
                assertTrue(dep.getVersion() != null);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            fail(e.getMessage());
        } finally {
            Files.delete(path);
            Files.copy(pathCopy, path);
            Files.delete(pathCopy);
        }
    }

    @Test
    public void updateCurriculumCourse() { testDefault(CURRICULUM_COURSE_PRJ); }

    @Test
    public void updateDinnerParty() {
        testDefault(DINNER_PARTY_PRJ);
    }

    @Test
    public void updateEmployeeRostering() { testDefault(EMPLOYEE_ROSTERING_PRJ); }

    @Test
    public void updateEvaluation() { testDefault(EVALUATION_PRJ); }

    @Test
    public void updateItOrders() {
        testDefault(ITORDERS_PRJ);
    }

    @Test
    public void updateMortgages() {
        testDefault(MORTGAGES_PRJ);
    }

    @Test
    public void updateOptacloud() {
        testDefault(OPTACLOUD_PRJ);
    }

    @Test
    public void updateGenericPom() {
        String prj = "/target/test-classes/generic/";
        Path jsonPath = Paths.get("file:" + currentDir + prj + "/pom-migration.json");
        Path path = Paths.get("file:" + currentDir + prj + "pom.xml");
        Path pathCopy = Paths.get("file:" + currentDir + prj + "copy_pom.xml");
        Files.copy(path, pathCopy);
        try {

            Model original = editor.getModel(path);
            assertEquals(original.getPackaging(),"jar");
            assertTrue(original.getBuild().getPlugins().size() == 1);
            assertTrue(original.getDependencies().size() == 3);
            assertTrue(original.getRepositories().size() == 0);
            assertTrue(original.getPluginRepositories().size() == 0);

            Model modelUpdated = editor.updatePom(path, jsonPath.toAbsolutePath().toString());
            assertNotNull(modelUpdated);
            assertEquals(modelUpdated.getPackaging(),"kjar");
            assertTrue(modelUpdated.getBuild().getPlugins().size() == 1);
            assertTrue(modelUpdated.getDependencies().size() == 6);
            assertTrue(modelUpdated.getRepositories().size() == 2);
            assertTrue(modelUpdated.getPluginRepositories().size() == 2);
        } catch (Exception e) {
            logger.error(e.getMessage());
            fail(e.getMessage());
        } finally {
            Files.delete(path);
            Files.copy(pathCopy, path);
            Files.delete(pathCopy);
        }
    }

    @Test
    public void updateGenericPomWithBrokenParams() {
        String prj = "/target/test-classes/generic/";
        Path path = Paths.get("file:" + currentDir + prj + "pom.xml");
        Path pathCopy = Paths.get("file:" + currentDir + prj + "copy_pom.xml");
        Files.copy(path, pathCopy);
        try {

            Model original = editor.getModel(path);
            assertTrue(original.getBuild().getPlugins().size() == 1);
            assertTrue(original.getDependencies().size() == 3);
            assertTrue(original.getRepositories().size() == 0);
            assertTrue(original.getPluginRepositories().size() == 0);

            Model modelUpdated;
            modelUpdated = editor.updatePom(path, null);
            assertTrue(modelUpdated.getGroupId() == null);
            modelUpdated = editor.updatePom(null, null);
            assertTrue(modelUpdated.getGroupId() == null);
        } catch (Exception e) {
            logger.error(e.getMessage());
            fail(e.getMessage());
        } finally {
            Files.delete(path);
            Files.copy(pathCopy, path);
            Files.delete(pathCopy);
        }
    }
}
