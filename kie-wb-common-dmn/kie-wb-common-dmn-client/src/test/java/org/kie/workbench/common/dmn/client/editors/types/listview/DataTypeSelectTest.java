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

package org.kie.workbench.common.dmn.client.editors.types.listview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeUtils;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType.BOOLEAN;
import static org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType.CONTEXT;
import static org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType.STRING;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem.CAN_NOT_HAVE_CONSTRAINT;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeSelect.STRUCTURE;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeSelectTest {

    @Mock
    private DataTypeSelect.View view;

    @Mock
    private DataTypeUtils dataTypeUtils;

    @Mock
    private DataTypeListItem listItem;

    @Mock
    private DataTypeManager dataTypeManager;

    private DataTypeSelect dataTypeSelect;

    @Before
    public void setup() {
        dataTypeSelect = spy(new DataTypeSelect(view, dataTypeUtils, dataTypeManager));
    }

    @Test
    public void testInit() {

        final DataType dataType = mock(DataType.class);
        final List<DataType> expectedDataTypes = new ArrayList<DataType>() {{
            add(mock(DataType.class));
        }};
        when(dataType.getSubDataTypes()).thenReturn(expectedDataTypes);

        dataTypeSelect.init(listItem, dataType);

        assertEquals(dataType, dataTypeSelect.getDataType());
        assertEquals(expectedDataTypes, dataTypeSelect.getSubDataTypes());
        verify(view).setDataType(dataType);
    }

    @Test
    public void testSetup() {
        dataTypeSelect.setup();

        verify(view).init(dataTypeSelect);
    }

    @Test
    public void testGetElement() {

        final HTMLElement expected = mock(HTMLElement.class);
        when(view.getElement()).thenReturn(expected);

        final HTMLElement actual = dataTypeSelect.getElement();

        assertEquals(actual, expected);
    }

    @Test
    public void testGetDefaultDataTypes() {

        final List<DataType> expectedDataTypes = new ArrayList<DataType>() {{
            add(mock(DataType.class));
        }};

        when(dataTypeUtils.defaultDataTypes()).thenReturn(expectedDataTypes);

        final List<DataType> actualDataTypes = dataTypeSelect.getDefaultDataTypes();

        assertThat(actualDataTypes).hasSameElementsAs(expectedDataTypes);
    }

    @Test
    public void testGetCustomDataTypes() {

        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final DataType dataType3 = mock(DataType.class);
        final List<DataType> customDataTypes = asList(dataType1, dataType2, dataType3);

        when(dataType1.getName()).thenReturn("tUUID");
        when(dataType2.getName()).thenReturn("tPerson");
        when(dataType3.getName()).thenReturn("tCity");
        when(dataTypeUtils.customDataTypes()).thenReturn(customDataTypes);
        doReturn(dataType2).when(dataTypeSelect).getDataType();

        final List<DataType> actualDataTypes = dataTypeSelect.getCustomDataTypes();
        final List<DataType> expectedDataTypes = asList(dataType1, dataType3);

        assertEquals(expectedDataTypes, actualDataTypes);
    }

    @Test
    public void testEnableEditMode() {
        dataTypeSelect.enableEditMode();

        verify(dataTypeSelect).refresh();
        verify(view).enableEditMode();
    }

    @Test
    public void testDisableEditMode() {
        dataTypeSelect.disableEditMode();

        verify(view).disableEditMode();
    }

    @Test
    public void testRefresh() {
        dataTypeSelect.refresh();

        verify(view).setupDropdown();
    }

    @Test
    public void testRefreshView() {

        final String typeName = "typeName";
        final DataType parent = mock(DataType.class);
        final List<DataType> expectedDataTypes = Collections.singletonList(mock(DataType.class));

        doReturn(parent).when(dataTypeSelect).getDataType();
        when(dataTypeManager.from(parent)).thenReturn(dataTypeManager);
        when(dataTypeManager.makeExternalDataTypes(typeName)).thenReturn(expectedDataTypes);

        dataTypeSelect.init(listItem, parent);
        dataTypeSelect.refreshView(typeName);

        assertEquals(expectedDataTypes, dataTypeSelect.getSubDataTypes());
        verify(listItem).refreshSubItems(expectedDataTypes);
        verify(listItem).refreshConstraintComponent();
    }

    @Test
    public void testGetValue() {

        final String expectedValue = "typeName";

        when(view.getValue()).thenReturn(expectedValue);

        final String actualValue = dataTypeSelect.getValue();

        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testIsIndirectTypeOfWhenIsBoolean() {

        final String currentValue = "tIndirectBoolean";
        final DataType dataType1 = mock(DataType.class);
        final List<DataType> customDataTypes = asList(dataType1);

        when(dataType1.getName()).thenReturn(currentValue);
        when(dataType1.getType()).thenReturn(BOOLEAN.getName());
        doReturn(customDataTypes).when(dataTypeSelect).getCustomDataTypes();

        final boolean actual = dataTypeSelect.isIndirectTypeOf(currentValue, CAN_NOT_HAVE_CONSTRAINT);

        assertTrue(actual);
    }

    @Test
    public void testIsIndirectTypeOfWhenIsBooleanRecursive() {

        // some(tIndirectType) -> tIndirectType(tIndirectBoolean) -> tIndirectBoolean(boolean)
        final String indirectType = "tIndirectType";
        final String tBoolean = "tIndirectBoolean";
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final List<DataType> customDataTypes = asList(dataType1, dataType2);

        when(dataType1.getName()).thenReturn(indirectType);
        when(dataType1.getType()).thenReturn(tBoolean);
        when(dataType2.getName()).thenReturn(tBoolean);
        when(dataType2.getType()).thenReturn(BOOLEAN.getName());

        doReturn(customDataTypes).when(dataTypeSelect).getCustomDataTypes();

        final boolean actual = dataTypeSelect.isIndirectTypeOf(indirectType, CAN_NOT_HAVE_CONSTRAINT);

        assertTrue(actual);
    }

    @Test
    public void testIsIndirectTypeOfWhenIsStructureRecursive() {

        // some(tIndirectType) -> tIndirectType(tIndirectStructure) -> tIndirectStructure(Structure)
        final String indirectType = "tIndirectType";
        final String tIndirectStructure = "tIndirectStructure";
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final List<DataType> customDataTypes = asList(dataType1, dataType2);

        when(dataType1.getName()).thenReturn(indirectType);
        when(dataType1.getType()).thenReturn(tIndirectStructure);
        when(dataType2.getName()).thenReturn(tIndirectStructure);
        when(dataType2.getType()).thenReturn(STRUCTURE);

        doReturn(customDataTypes).when(dataTypeSelect).getCustomDataTypes();

        final boolean actual = dataTypeSelect.isIndirectTypeOf(indirectType, CAN_NOT_HAVE_CONSTRAINT);

        assertTrue(actual);
    }

    @Test
    public void testIsIndirectTypeOfWhenIsStructure() {

        final String currentValue = "tIndirectStructure";
        final DataType dataType1 = mock(DataType.class);
        final List<DataType> customDataTypes = asList(dataType1);

        when(dataType1.getName()).thenReturn(currentValue);
        when(dataType1.getType()).thenReturn(STRUCTURE);
        doReturn(customDataTypes).when(dataTypeSelect).getCustomDataTypes();

        final boolean actual = dataTypeSelect.isIndirectTypeOf(currentValue, CAN_NOT_HAVE_CONSTRAINT);

        assertTrue(actual);
    }


    @Test
    public void testIsIndirectTypeOfWhenIsContextRecursive() {

        // some(tIndirectType) -> tIndirectType(tIndirectContext) -> tIndirectContext(Context)
        final String indirectType = "tIndirectType";
        final String tIndirectContext = "tIndirectContext";
        final DataType dataType1 = mock(DataType.class);
        final DataType dataType2 = mock(DataType.class);
        final List<DataType> customDataTypes = asList(dataType1, dataType2);

        when(dataType1.getName()).thenReturn(indirectType);
        when(dataType1.getType()).thenReturn(tIndirectContext);
        when(dataType2.getName()).thenReturn(tIndirectContext);
        when(dataType2.getType()).thenReturn(CONTEXT.getName());

        doReturn(customDataTypes).when(dataTypeSelect).getCustomDataTypes();

        final boolean actual = dataTypeSelect.isIndirectTypeOf(indirectType, CAN_NOT_HAVE_CONSTRAINT);

        assertTrue(actual);
    }

    @Test
    public void testIsIndirectTypeOfWhenIsContext() {

        final String currentValue = "tIndirectContext";
        final DataType dataType1 = mock(DataType.class);
        final List<DataType> customDataTypes = asList(dataType1);

        when(dataType1.getName()).thenReturn(currentValue);
        when(dataType1.getType()).thenReturn(CONTEXT.getName());
        doReturn(customDataTypes).when(dataTypeSelect).getCustomDataTypes();

        final boolean actual = dataTypeSelect.isIndirectTypeOf(currentValue, CAN_NOT_HAVE_CONSTRAINT);

        assertTrue(actual);
    }

    @Test
    public void testIsIndirectTypeOfWhenIsOtherType() {

        final String currentValue = "tIndirectOtherType";
        final DataType dataType1 = mock(DataType.class);
        final List<DataType> customDataTypes = asList(dataType1);

        when(dataType1.getName()).thenReturn(currentValue);
        when(dataType1.getType()).thenReturn(STRING.getName());
        doReturn(customDataTypes).when(dataTypeSelect).getCustomDataTypes();

        final boolean actual = dataTypeSelect.isIndirectTypeOf(currentValue, CAN_NOT_HAVE_CONSTRAINT);

        assertFalse(actual);
    }
}
