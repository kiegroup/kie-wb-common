/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.integration.tests.modelslookup;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.ResourceNotFoundException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.data.modeller.service.DataObjectFinderService;
import org.kie.workbench.common.forms.data.modeller.service.DataObjectFormModelCreationService;
import org.kie.workbench.common.forms.data.modeller.service.impl.DataObjectFinderServiceImpl;
import org.kie.workbench.common.forms.data.modeller.service.impl.DataObjectFormModelCreationServiceImpl;
import org.kie.workbench.common.forms.data.modeller.service.impl.DataObjectFormModelHandler;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class GetDataObjectModelsTest extends GetModelsTest {

    private static DataModelerService dataModelerService;
    private static FieldManager fieldManager;

    private static DataObjectFinderService finderService;
    private static DataObjectFormModelHandler handler;
    private static DataObjectFormModelCreationService creationService;

    private static final String
            DATAOBJECTS_PACKAGE = "/src/main/java/com/myteam/modelslookup/",
            DO_PATH_FORMAT = PROJECT_ROOT + DATAOBJECTS_PACKAGE + "%s.java";

    private static final String
            HUMAN = "Human",
            NEW_PERSON = "NewPerson",
            PERSON_BACKUP = "PersonBackup",
            CREATED_DO = "CreatedDataObject",
            PERSON_BACKUP_FQN = DO_PACKAGE + PERSON_BACKUP;

    private static final Set<String>
            ADDRESS_FIELDS = new HashSet<>(Arrays.asList("street", "number", "city", "zip")),
            ORDER_FIELDS = new HashSet<>(Collections.singletonList("listOfItems")),
            PERSON_FIELDS = new HashSet<>(Arrays.asList("name", "address", "salary", "married")),
            ITEM_FIELDS = new HashSet<>(Arrays.asList("id", "name", "price"));

    private static final Map<String, Set<String>> ORIGINAL_MODEL = new HashMap<String, Set<String>>() {{
        put(ADDRESS_FQN, ADDRESS_FIELDS);
        put(PERSON_FQN, PERSON_FIELDS);
        put(PERSON_BACKUP_FQN, PERSON_FIELDS);
        put(ITEM_FQN, ITEM_FIELDS);
        put(ORDER_FQN, ORDER_FIELDS);
    }};

    @BeforeClass
    public static void setup() throws Exception {
        dataModelerService = weldContainer.select(DataModelerService.class).get();
        fieldManager = weldContainer.select(FieldManager.class).get();

        finderService = new DataObjectFinderServiceImpl(moduleService, dataModelerService);
        handler = new DataObjectFormModelHandler(moduleService, classLoaderHelper, finderService, fieldManager);
        creationService = new DataObjectFormModelCreationServiceImpl(finderService, handler);
    }

    @Test
    public void testGetModelsAfterRename() throws URISyntaxException, IOException {
        renameDO(PERSON, HUMAN);

        final Map<String, Set<String>> expectedDataObjects = new HashMap<String, Set<String>>(ORIGINAL_MODEL) {{
            remove(PERSON_FQN);
            put(DO_PACKAGE + HUMAN, PERSON_FIELDS);
        }};

        assertExpectedLoaded(expectedDataObjects);
    }

    @Test
    public void testGetModelsAfterCopy() throws IOException, URISyntaxException {
        copyDO(PERSON, NEW_PERSON);

        final Map<String, Set<String>> expectedDataObjects = new HashMap<String, Set<String>>(ORIGINAL_MODEL) {{
            put(DO_PACKAGE + NEW_PERSON, PERSON_FIELDS);
        }};

        assertExpectedLoaded(expectedDataObjects);
    }

    @Test
    public void testGetModelsAfterDelete() throws IOException, URISyntaxException {
        deleteDO(PERSON);

        final Map<String, Set<String>> expectedDataObjects = new HashMap<String, Set<String>>(ORIGINAL_MODEL) {{
            remove(PERSON_FQN);
        }};

        assertExpectedLoaded(expectedDataObjects);
    }

    @Test
    public void testGetModelsAfterCreate() throws URISyntaxException {
        createDO(CREATED_DO);

        final Map<String, Set<String>> expectedDataObjects = new HashMap<String, Set<String>>(ORIGINAL_MODEL) {{
            put(DO_PACKAGE + CREATED_DO, Collections.emptySet());
        }};

        assertExpectedLoaded(expectedDataObjects);
    }

    @After
    public void cleanUp() throws Exception {
        if (isDataObjectInResources(HUMAN)) {
            renameDO(HUMAN, PERSON);
        }
        if (isDataObjectInResources(NEW_PERSON)) {
            deleteDO(NEW_PERSON);
        }
        if (!isDataObjectInResources(PERSON)) {
            copyDO(PERSON_BACKUP, PERSON);
        }
        if (isDataObjectInResources(CREATED_DO)) {
            deleteDO(CREATED_DO);
        }
    }

    private void assertExpectedLoaded(Map<String, Set<String>> expectedDataObjects) {
        final List<DataObjectFormModel> dataObjects = creationService.getAvailableDataObjects(rootPath);
        final Map<String, Set<String>> actualDataObjects = getDataObjectsMap(dataObjects);
        assertThat(actualDataObjects).isEqualTo(expectedDataObjects);
    }

    private boolean isDataObjectInResources(String dataObject) {
        return isResourcePresent(getDOPath(dataObject));
    }

    private void renameDO(String oldName, String newName) throws URISyntaxException, IOException {
        final java.nio.file.Path targetPath = renameResource(getDOPath(oldName), newName + ".java");
        refactorClass(targetPath.toFile(), oldName, newName);
        File[] files = targetPath.getParent().toFile().listFiles();
        refactorReferencesInOtherClasses(files, oldName, newName);
    }

    private void copyDO(String source, String newName) throws URISyntaxException, IOException {
        final java.nio.file.Path targetPath = copyResource(String.format(DO_PATH_FORMAT, source), newName + ".java");
        refactorClass(targetPath.toFile(), source, newName);
    }

    private void deleteDO(String dataObject) throws URISyntaxException, IOException {
        deleteResource(String.format(DO_PATH_FORMAT, dataObject));
    }

    private void createDO(String name) throws URISyntaxException {
        //DataObject dataObject = new DataObjectImpl(name, pkg);
        Path dataObjectPath = PathFactory.newPath(name + ".java",
                                                  "file://" + ROOT_URL.toURI().getPath() + DATAOBJECTS_PACKAGE);
        dataModelerService.createJavaFile(dataObjectPath, name + ".java", "comment");
    }

    private void refactorClass(File file, String oldName, String newName) throws IOException {
        String fileContent = FileUtils.readFileToString(file, Charset.defaultCharset());
        //update class name and constructors
        fileContent = replaceAllSurroundedBy(fileContent, Arrays.asList(" class ", " "), Arrays.asList(" ", "("), oldName, newName);
        FileUtils.write(file, fileContent, Charset.defaultCharset());
    }

    private void refactorReferencesInOtherClasses(File[] files, String oldFieldType, String newFieldType) throws IOException {
        final String fieldTypeFQN = DO_PACKAGE + oldFieldType;
        for (File file : files) {
            if (isCandidateForRefactoring(oldFieldType, newFieldType, file)) {
                String fileContent = FileUtils.readFileToString(file, Charset.defaultCharset());
                if (needsRefactoring(fieldTypeFQN, fileContent)) {
                    fileContent = refactorFields(oldFieldType, newFieldType, fileContent);
                    FileUtils.write(file, fileContent, Charset.defaultCharset());
                }
            }
        }
    }

    private boolean needsRefactoring(String fieldTypeFQN, String fileContent) {
        return fileContent.contains(fieldTypeFQN);
    }

    private boolean isCandidateForRefactoring(String oldClassName, String newClassName, File file) {
        return !Objects.equals(file.getName(), oldClassName + ".java") && !Objects.equals(file.getName(), newClassName + ".java");
    }

    private String refactorFields(String oldFieldType, String newFieldType, String fileContent) {
        final String oldFieldName = StringUtils.uncapitalize(oldFieldType);
        final String newFieldName = StringUtils.uncapitalize(newFieldType);
        //replace field types, strings, getters and setters
        fileContent = replaceAllSurroundedBy(fileContent, Arrays.asList(DO_PACKAGE, "\"", "get", "set"), Arrays.asList("", "\"", "(", "("), oldFieldType, newFieldType);
        //replace field declarations, method parameters and inner field references
        fileContent = replaceAllSurroundedBy(fileContent, Arrays.asList(" ", " ", " ", ".", ".", "."), Arrays.asList(";", ",", ")", " ", ";", ","), oldFieldName, newFieldName);
        return fileContent;
    }

    private String replaceAllSurroundedBy(String fileContent, List<String> prefixes, List<String> suffixes, String oldName, String newName) {
        for (int i = 0; i < prefixes.size(); i++) {
            String prefix = prefixes.get(i);
            String suffix = suffixes.get(i);
            String regexSuffix = suffix;
            if (")".equals(suffix) || "(".equals(suffix)) {
                regexSuffix = "[" + suffix + "]";
            }
            fileContent = fileContent.replaceAll(prefix + oldName + regexSuffix, prefix + newName + suffix);
        }
        return fileContent;
    }

    private java.nio.file.Path getDataObjectPath(String dataObject) throws URISyntaxException {
        final URL url = getDataObjectURL(dataObject);
        if (url == null) {
            throw new ResourceNotFoundException("The resource: " + dataObject + ".java was not found");
        }
        return java.nio.file.Paths.get(url.toURI());
    }

    private URL getDataObjectURL(String dataObject) {
        return getClass().getResource(getDOPath(dataObject));
    }

    private String getDOPath(String dataObject) {
        return String.format(DO_PATH_FORMAT, dataObject);
    }

    private Map<String, Set<String>> getDataObjectsMap(List<DataObjectFormModel> dataObjects) {
        Map<String, Set<String>> actualDataObjects = new HashMap<>();
        for (DataObjectFormModel dataObject : dataObjects) {
            actualDataObjects.put(dataObject.getClassName(), dataObject.getProperties().stream().map(ModelProperty::getName).collect(Collectors.toSet()));
        }
        return actualDataObjects;
    }
}
