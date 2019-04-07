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
package org.kie.workbench.common.stunner.bpmn.backend.service;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@Ignore

@RunWith(MockitoJUnitRunner.class)
public class FindDataTypesServiceTest {

    /*@Mock
    private DataModelService dataModelService;

    @Mock
    private PackageDataModelOracle oracle;

    @Mock
    private Path path;

    private DataTypesService service;

    @Before
    public void setup() {
        this.service = new FindDataTypesService(dataModelService);
    }

    @Test
    public void testGetJavaTypeNames() throws Exception {
        final Map<String, ModelField[]> fields = new java.util.HashMap<>();

        when(oracle.getModulePackageNames()).thenReturn(Collections.singletonList("org"));
        when(dataModelService.getDataModel(path)).thenReturn(oracle);

        //Types in the package
        fields.put("org.Zebra",
                   new ModelField[]{new ModelField("this",
                                                   "org.Zebra",
                                                   ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                   ModelField.FIELD_ORIGIN.SELF,
                                                   FieldAccessorsAndMutators.ACCESSOR,
                                                   DataType.TYPE_THIS)});

        fields.put("org.Antelope",
                   new ModelField[]{new ModelField("this",
                                                   "org.Antelope",
                                                   ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                   ModelField.FIELD_ORIGIN.SELF,
                                                   FieldAccessorsAndMutators.ACCESSOR,
                                                   DataType.TYPE_THIS)});

        //Type not in the package
        fields.put("smurf.Pupa",
                   new ModelField[]{new ModelField("this",
                                                   "smurf.Pupa",
                                                   ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                                   ModelField.FIELD_ORIGIN.SELF,
                                                   FieldAccessorsAndMutators.ACCESSOR,
                                                   DataType.TYPE_THIS)});

        when(oracle.getModuleModelFields()).thenReturn(fields);

        final List<String> dataTypeNames = service.getDataTypeNames(path);

        assertNotNull(dataTypeNames);
        assertEquals(2, dataTypeNames.size());
        assertEquals("org.Antelope", dataTypeNames.get(0));
        assertEquals("org.Zebra", dataTypeNames.get(1));
    }*/
}
