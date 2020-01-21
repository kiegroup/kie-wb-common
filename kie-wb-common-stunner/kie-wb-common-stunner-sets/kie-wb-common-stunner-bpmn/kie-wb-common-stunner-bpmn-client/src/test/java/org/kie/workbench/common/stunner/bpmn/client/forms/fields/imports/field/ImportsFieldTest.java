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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.field;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwtmockito.GwtMockito;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.popup.ImportsPopup;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.ImportsValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.WSDLImport;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants.CONSTANTS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ValueChangeEvent.class)
public class ImportsFieldTest {

    private static final String CLASSNAME = "Classname";
    private static final String LOCATION = "Location";
    private static final String NAMESPACE = "Namespace";

    private ImportsField tested;

    @Before
    public void setUp() {
        GwtMockito.initMocks(this);

        mockStatic(ValueChangeEvent.class);
        when(ValueChangeEvent.getType()).thenReturn(mock(GwtEvent.Type.class));

        tested = GWT.create(ImportsField.class);
        tested.importsButton = mock(Button.class);
        tested.importsTextBox = mock(TextBox.class);
        tested.importsPopup = mock(ImportsPopup.class);

        doCallRealMethod().when(tested).init();
        doCallRealMethod().when(tested).getValue();
        doCallRealMethod().when(tested).setValue(any(ImportsValue.class));
        doCallRealMethod().when(tested).buildImportsCountString(any(ImportsValue.class));
        doCallRealMethod().when(tested).buildDefaultImportsCountString(anyInt());
        doCallRealMethod().when(tested).buildWSDLImportsCountString(anyInt());
        doCallRealMethod().when(tested).copyImportsValue(any(ImportsValue.class));
        doCallRealMethod().when(tested).showImportsEditor();
        doCallRealMethod().when(tested).setReadOnly(anyBoolean());
        doCallRealMethod().when(tested).onClickImportsButton(any(ClickEvent.class));
        doCallRealMethod().when(tested).onClickImportsTextBox(any(ClickEvent.class));
    }

    @Test
    public void constructor() {
        ImportsField tested1 = new ImportsField();
        assertNull(tested1.importsValue);

        ImportsValue importsValue = createImportsValue(3, 2);
        ImportsPopup importsPopup = mock(ImportsPopup.class);
        ImportsField tested2 = new ImportsField(importsValue, importsPopup);
        assertEquals(importsValue, tested2.importsValue);
    }

    @Test
    public void init() {
        tested.init();
        verify(tested.importsPopup).setDataCallback(any(ImportsPopup.DataCallback.class));
    }

    @Test
    public void getValue() {
        tested.importsValue = createImportsValue(2, 2);

        ImportsValue result = tested.getValue();
        assertEquals(tested.importsValue, result);
    }

    @Test
    public void setValue() {
        ImportsValue importsValue = createImportsValue(3, 3);
        tested.setValue(importsValue);

        assertEquals(importsValue, tested.importsValue);
        verify(tested).buildImportsCountString(eq(importsValue));
        verify(tested.importsTextBox).setText(anyString());
    }

    @Test
    public void buildImportsCountString() {

        ImportsValue importsValue1 = createImportsValue(0, 0);
        String result1 = tested.buildImportsCountString(importsValue1);
        assertEquals(CONSTANTS.No_Imports(), result1);

        ImportsValue importsValue2 = createImportsValue(1, 1);
        String result2 = tested.buildImportsCountString(importsValue2);
        assertEquals(CONSTANTS.Data_Type_Import() + ", " + CONSTANTS.WSDL_Import(), result2);

        ImportsValue importsValue3 = createImportsValue(2, 2);
        String result3 = tested.buildImportsCountString(importsValue3);
        assertEquals("2 " + CONSTANTS.Data_Type_Imports() + ", 2 " + CONSTANTS.WSDL_Imports(), result3);

        ImportsValue importsValue4 = createImportsValue(0, 1);
        String result4 = tested.buildImportsCountString(importsValue4);
        assertEquals(CONSTANTS.No_Data_Type_Import() + ", " + CONSTANTS.WSDL_Import(), result4);

        ImportsValue importsValue5 = createImportsValue(0, 2);
        String result5 = tested.buildImportsCountString(importsValue5);
        assertEquals(CONSTANTS.No_Data_Type_Import() + ", 2 " + CONSTANTS.WSDL_Imports(), result5);

        ImportsValue importsValue6 = createImportsValue(1, 0);
        String result6 = tested.buildImportsCountString(importsValue6);
        assertEquals(CONSTANTS.Data_Type_Import() + ", " + CONSTANTS.No_WSDL_Import(), result6);

        ImportsValue importsValue7 = createImportsValue(2, 0);
        String result7 = tested.buildImportsCountString(importsValue7);
        assertEquals("2 " + CONSTANTS.Data_Type_Imports() + ", " + CONSTANTS.No_WSDL_Import(), result7);

        ImportsValue importsValue8 = createImportsValue(1, 2);
        String result8 = tested.buildImportsCountString(importsValue8);
        assertEquals(CONSTANTS.Data_Type_Import() + ", 2 " + CONSTANTS.WSDL_Imports(), result8);

        ImportsValue importsValue9 = createImportsValue(2, 1);
        String result9 = tested.buildImportsCountString(importsValue9);
        assertEquals("2 " + CONSTANTS.Data_Type_Imports() + ", " + CONSTANTS.WSDL_Import(), result9);

        String result10 = tested.buildImportsCountString(null);
        assertEquals(CONSTANTS.No_Imports(), result10);
    }

    @Test
    public void buildDefaultImportsCountString() {
        String result1 = tested.buildDefaultImportsCountString(0);
        assertEquals(CONSTANTS.No_Data_Type_Import(), result1);

        String result2 = tested.buildDefaultImportsCountString(1);
        assertEquals(CONSTANTS.Data_Type_Import(), result2);

        String result3 = tested.buildDefaultImportsCountString(15);
        assertTrue(result3.contains(CONSTANTS.Data_Type_Imports()));
    }

    @Test
    public void buildWSDLImportsCountString() {
        String result1 = tested.buildWSDLImportsCountString(0);
        assertEquals(CONSTANTS.No_WSDL_Import(), result1);

        String result2 = tested.buildWSDLImportsCountString(1);
        assertEquals(CONSTANTS.WSDL_Import(), result2);

        String result3 = tested.buildWSDLImportsCountString(15);
        assertTrue(result3.contains(CONSTANTS.WSDL_Imports()));
    }

    @Test
    public void copyImportsValue() {
        ImportsValue result1 = tested.copyImportsValue(null);
        assertNotNull(result1);

        ImportsValue importsValue = createImportsValue(5, 5);
        ImportsValue result2 = tested.copyImportsValue(importsValue);
        assertEquals(importsValue.getDefaultImports().size(), result2.getDefaultImports().size());
        assertEquals(importsValue.getWSDLImports().size(), result2.getWSDLImports().size());

        importsValue.setDefaultImports(new ArrayList<>());
        importsValue.setWSDLImports(new ArrayList<>());
        assertNotEquals(importsValue.getDefaultImports().size(), result2.getDefaultImports().size());
        assertNotEquals(importsValue.getWSDLImports().size(), result2.getWSDLImports().size());
    }

    @Test
    public void showImportsEditor() {
        tested.showImportsEditor();
        verify(tested, times(1)).copyImportsValue(any(ImportsValue.class));
        verify(tested.importsPopup, times(1)).setDefaultImports(any(List.class));
        verify(tested.importsPopup, times(1)).setWSDLImports(any(List.class));
        verify(tested.importsPopup, times(1)).show();
    }

    @Test
    public void setReadOnly() {
        tested.setReadOnly(false);
        verify(tested.importsTextBox).setEnabled(eq(true));
        verify(tested.importsButton).setEnabled(eq(true));

        tested.setReadOnly(true);
        verify(tested.importsTextBox).setEnabled(eq(false));
        verify(tested.importsButton).setEnabled(eq(false));
    }

    @Test
    public void onClickImportsButton() {
        tested.onClickImportsButton(any(ClickEvent.class));
        verify(tested, times(1)).showImportsEditor();
    }

    @Test
    public void onClickImportsTextBox() {
        tested.onClickImportsTextBox(any(ClickEvent.class));
        verify(tested, times(1)).showImportsEditor();
    }

    private ImportsValue createImportsValue(int defaultImportsQty, int wsdlImportsQty) {
        ImportsValue importsValue = spy(new ImportsValue());

        for (int i = 0; i < defaultImportsQty; i++) {
            DefaultImport defaultImport = new DefaultImport(CLASSNAME);
            importsValue.addImport(defaultImport);
        }

        for (int i = 0; i < wsdlImportsQty; i++) {
            WSDLImport wsdlImport = new WSDLImport(LOCATION, NAMESPACE);
            importsValue.addImport(wsdlImport);
        }

        return importsValue;
    }
}