/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.backend.editors.types;

import java.util.List;
import java.util.Map;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.commons.util.Maps;
import org.kie.soup.project.datamodel.commons.oracle.ModuleDataModelOracleImpl;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.workbench.common.dmn.api.editors.types.DataObject;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataObjectsServiceImplTest {

    @Mock
    private DataModelService dataModelService;

    @Mock
    private WorkspaceProject workspaceProject;

    @Mock
    private Path projectRootPath;

    private ModuleDataModelOracle dataModelOracle;

    private DataObjectsServiceImpl service;

    @Before
    public void setup() {
        service = new DataObjectsServiceImpl(dataModelService);
        dataModelOracle = new ModuleDataModelOracleImpl();

        when(workspaceProject.getRootPath()).thenReturn(projectRootPath);
        when(dataModelService.getModuleDataModel(projectRootPath)).thenReturn(dataModelOracle);
    }

    @Test
    public void testLoadDataObjects() {
        final Maps.Builder<String, ModelField[]> modelFieldsBuilder = new Maps.Builder<>();
        modelFieldsBuilder.put("APerson",
                               new ModelField[]{
                                       new ModelField(DataType.TYPE_THIS,
                                                      "APerson",
                                                      ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                      ModelField.FIELD_ORIGIN.SELF,
                                                      FieldAccessorsAndMutators.BOTH,
                                                      "APerson"),
                                       new ModelField("name",
                                                      String.class.getName(),
                                                      ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                      ModelField.FIELD_ORIGIN.SELF,
                                                      FieldAccessorsAndMutators.BOTH,
                                                      DataType.TYPE_STRING),
                                       new ModelField("age",
                                                      Integer.class.getName(),
                                                      ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                      ModelField.FIELD_ORIGIN.SELF,
                                                      FieldAccessorsAndMutators.BOTH,
                                                      DataType.TYPE_NUMERIC_INTEGER)
                               });
        modelFieldsBuilder.put("ZPet",
                               new ModelField[]{
                                       new ModelField(DataType.TYPE_THIS,
                                                      "ZPet",
                                                      ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                      ModelField.FIELD_ORIGIN.SELF,
                                                      FieldAccessorsAndMutators.BOTH,
                                                      "ZPet")
                               });
        final Map<String, ModelField[]> modelFields = modelFieldsBuilder.build();
        dataModelOracle.addModuleModelFields(modelFields);

        final List<DataObject> dataObjects = service.loadDataObjects(workspaceProject);

        assertThat(dataObjects).isNotEmpty();
        assertThat(dataObjects).hasSize(2);

        assertThat(dataObjects.get(0).getClassType()).isEqualTo("APerson");
        assertThat(dataObjects.get(0).getProperties()).hasSize(2);
        assertThat(dataObjects.get(0).getProperties().get(0).getProperty()).isEqualTo("name");
        assertThat(dataObjects.get(0).getProperties().get(0).getType()).isEqualTo(String.class.getName());
        assertThat(dataObjects.get(0).getProperties().get(1).getProperty()).isEqualTo("age");
        assertThat(dataObjects.get(0).getProperties().get(1).getType()).isEqualTo(Integer.class.getName());

        assertThat(dataObjects.get(1).getClassType()).isEqualTo("ZPet");
        assertThat(dataObjects.get(1).getProperties()).isEmpty();
    }

    @Test
    public void testLoadDataObjectsWhenThereIsNoJavaFilesAvailable() {
        final List<DataObject> dataObjects = service.loadDataObjects(workspaceProject);

        assertThat(dataObjects).isEmpty();
    }
}