/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.project.client.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.jgroups.util.Util.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Matchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class DataTypeNamesProjectServiceTest {

    @Mock
    private DataTypeNamesProjectService dataTypeNamesProjectService;

    @Test
    public void testTypes() {
        Mockito.doCallRealMethod().when(dataTypeNamesProjectService).add(anyString(), anyString());
        List<String> localTypes = new ArrayList<>();

        dataTypeNamesProjectService.addedDataTypes = localTypes;
        localTypes.add("One");

        dataTypeNamesProjectService.add("MyNewValue", "One");
        assertEquals(localTypes.get(0), dataTypeNamesProjectService.addedDataTypes.get(0));

        dataTypeNamesProjectService.add("Newest", "OldValueNonExistent");
        assertEquals(localTypes.get(1), dataTypeNamesProjectService.addedDataTypes.get(1));
    }

    @Test
    public void testFilterDefaultDataTypes() {
        Mockito.doCallRealMethod().when(dataTypeNamesProjectService).extractDefaultDataTypes();
        List<String> localTypes = new ArrayList<>();

        dataTypeNamesProjectService.addedDataTypes = localTypes;
        localTypes.add("One");
        localTypes.add("Float");
        localTypes.add("Integer");
        localTypes.add("Object");
        localTypes.add("String");
        localTypes.add("Boolean");

        dataTypeNamesProjectService.extractDefaultDataTypes();
        assertEquals(localTypes.size(), 1);
    }

    @Test
    public void testTypesDefault() {
        Mockito.doCallRealMethod().when(dataTypeNamesProjectService).add(any(), any());
        List<String> localTypes = new ArrayList<>();

        dataTypeNamesProjectService.addedDataTypes = localTypes;

        dataTypeNamesProjectService.add("Float", null);
        dataTypeNamesProjectService.add("Object", null);
        dataTypeNamesProjectService.add("String", null);
        dataTypeNamesProjectService.add("Boolean", null);
        dataTypeNamesProjectService.add("Integer", null);

        assertEquals(localTypes.size(), 0);
    }
}