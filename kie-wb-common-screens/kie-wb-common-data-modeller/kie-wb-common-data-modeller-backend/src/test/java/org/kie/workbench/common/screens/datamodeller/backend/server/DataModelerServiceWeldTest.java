/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.backend.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.junit.Test;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Paths;
import t1p1.Pojo1;
import t1p2.Pojo2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Tests for DataModelService
 */
public class DataModelerServiceWeldTest extends AbstractDataModelerServiceWeldTest {

    @Test
    public void testDataModelerService() throws Exception {
        KieModule module = loadProjectFromResources("/DataModelerTest1");

        final Map<String, AnnotationDefinition> systemAnnotations = dataModelService.getAnnotationDefinitions();
        DataModel dataModelOriginal = new DataModelTestUtil(systemAnnotations).createModel(Pojo1.class,
                                                                                           Pojo2.class);

        org.kie.workbench.common.services.datamodeller.core.DataModel dataModel = dataModelService.loadModel(module);
        Map<String, DataObject> objectsMap = new HashMap<>();

        assertNotNull(dataModel);

        assertEquals(dataModelOriginal.getDataObjects().size(),
                     dataModel.getDataObjects().size());

        for (DataObject dataObject : dataModel.getDataObjects()) {
            objectsMap.put(dataObject.getClassName(),
                           dataObject);
        }

        for (DataObject dataObject : dataModelOriginal.getDataObjects()) {
            org.kie.workbench.common.services.datamodeller.DataModelerAssert.assertEqualsDataObject(dataObject,
                                                                                                    objectsMap.get(dataObject.getClassName()));
        }
    }

    @Test
    public void testCreateFileWithPersistable() throws Exception {
        String POM = "pom.xml";
        org.uberfire.java.nio.file.Path tmpRoot = Files.createTempDirectory("repo");
        org.uberfire.java.nio.file.Path tmp = createAndCopyToDirectory(tmpRoot,
                                                                       "dummy",
                                                                       "target/test-classes/dummy_empty_deps");

        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new ByteArrayInputStream(Files.readAllBytes(Paths.get(tmp.toAbsolutePath().toString() + File.separator + POM))));
        assertThat(model.getDependencies()).hasSize(0);

        Map<String, Object> options = new HashMap<>();
        options.put("persistable",
                    true);

        Path pathVfs = PathFactory.newPath(tmp.getFileName().toString(),
                                           tmp.toUri().toString() + File.separator + "src/main/java");
        dataModelService.createJavaFile(pathVfs,
                                        "Test.java",
                                        "Test file",
                                        options);

        model = reader.read(new ByteArrayInputStream(Files.readAllBytes(Paths.get(tmp.toAbsolutePath().toString() + File.separator + POM))));
        assertThat(model.getDependencies()).hasSize(1);
    }

    public static org.uberfire.java.nio.file.Path createAndCopyToDirectory(org.uberfire.java.nio.file.Path root,
                                                                           String dirName,
                                                                           String copyTree) throws IOException {

        org.uberfire.java.nio.file.Path dir = Files.createDirectories(Paths.get(root.toString(),
                                                                                dirName));
        copyTree(Paths.get(copyTree),
                 dir);
        return dir;
    }

    public static void copyTree(org.uberfire.java.nio.file.Path source,
                                org.uberfire.java.nio.file.Path target) throws IOException {
        FileUtils.copyDirectory(source.toFile(),
                                target.toFile());
    }
}
