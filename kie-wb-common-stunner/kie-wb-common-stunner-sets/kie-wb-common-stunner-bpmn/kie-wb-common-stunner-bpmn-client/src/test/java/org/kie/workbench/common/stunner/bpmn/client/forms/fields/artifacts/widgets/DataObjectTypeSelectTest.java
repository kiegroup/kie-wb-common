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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.artifacts.widgets;

import java.util.Arrays;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.Event;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLOptionsCollection;
import elemental2.dom.HTMLSelectElement;
import elemental2.dom.KeyboardEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ReflectionUtilsTest;
import org.mockito.Mock;
import org.mockito.internal.verification.VerificationModeFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DataObjectTypeSelectTest extends ReflectionUtilsTest {

    @Mock
    private HTMLOptionsCollection collection;
    @Mock
    private DataObjectTypeSelect tested;
    @Mock
    private HTMLSelectElement select;
    @Mock
    private HTMLInputElement addOrEdit;

    @Before
    public void setUp() throws Exception {

        setFieldValue(select, "options", collection);
        setFieldValue(tested, "select", select);
        setFieldValue(tested, "select", select);
        setFieldValue(tested, "selectedValue", "");
        setFieldValue(tested, "buildin", Arrays.asList("Boolean", "Float", "Integer", "Object", "String"));
        setFieldValue(tested, "addOrEdit", addOrEdit);
        doCallRealMethod().when(tested).addValueChangeHandler(any(ValueChangeHandler.class));
        doCallRealMethod().when(tested).checkIfNotExists(anyString());
        doCallRealMethod().when(tested).getOptionByText(anyString());
        doCallRealMethod().when(tested).getValue();
        doCallRealMethod().when(tested).setValue(anyString());
        doCallRealMethod().when(tested).setValue(anyString(), anyBoolean());
        doCallRealMethod().when(tested).onChange(any(Event.class));
        doCallRealMethod().when(tested).onKeyup(any(Event.class));
        doCallRealMethod().when(select).add(any(HTMLOptionElement.class));

        doCallRealMethod().when(tested).init();
    }

    @Test
    public void testInit() {
        collection.length = 0;
        tested.init();
        verify(tested, times(1)).addDataTypeToSelect("Custom ...", "Custom ...", DataObjectTypeSelect.Mode.ADD, true, false, false);
        verify(select, times(1)).addEventListener(eq("change"), any(EventListener.class));
        verify(addOrEdit, times(1)).addEventListener(eq("focusout"), any(EventListener.class));
        verify(addOrEdit, times(1)).addEventListener(eq("keyup"), any(EventListener.class));
    }

    @Test
    public void setReadOnly() {
        doCallRealMethod().when(tested).setReadOnly(anyBoolean());
        tested.setReadOnly(false);
        assertFalse(select.disabled);
    }

    @Test
    public void addValueChangeHandler() {
        tested.addValueChangeHandler(event -> {
        });
        verify(tested, VerificationModeFactory.times(1))
                .addHandler(any(ValueChangeHandler.class), any(GwtEvent.Type.class));
    }

    @Test
    public void checkIfNotExists() {
        assertTrue(tested.checkIfNotExists("ZZZ"));
        HTMLOptionElement option = spy(HTMLOptionElement.class);
        option.text = "test_name";
        select.options.length = 1;
        when(collection.item(anyInt())).thenReturn(option);
        assertFalse(tested.checkIfNotExists("test_name"));
    }

    @Test
    public void getOptionByText() {
        HTMLOptionElement option = spy(HTMLOptionElement.class);
        option.text = "test_name";
        select.options.length = 1;
        when(collection.item(anyInt())).thenReturn(option);
        assertEquals(option, tested.getOptionByText("test_name"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getOptionByTextWithException() {
        HTMLOptionElement option = spy(HTMLOptionElement.class);
        option.text = "test_name";
        select.options.length = 1;
        when(collection.item(anyInt())).thenReturn(option);
        assertEquals(option, tested.getOptionByText("test_name1"));
    }

    @Test
    public void addDataTypeToSelect() {
        doCallRealMethod().when(tested).addDataTypeToSelect(anyString(), anyString(), any(DataObjectTypeSelect.Mode.class), anyBoolean(), anyBoolean(), anyBoolean());
        tested.addDataTypeToSelect("aaaZZZ", "aaa", DataObjectTypeSelect.Mode.LABEL, true, false, false);
        verify(tested).createDataObjectType("aaaZZZ", "aaa", DataObjectTypeSelect.Mode.LABEL, true, false, false);
        tested.addDataTypeToSelect("aaaZZZ", "aaa", DataObjectTypeSelect.Mode.EDIT, true, true, true);
        verify(tested).createDataObjectType("Edit aaaZZZ ...", "aaa", DataObjectTypeSelect.Mode.EDIT, true, false, false);
    }

    @Test
    public void addDataTypeToSelectDups() {
        doCallRealMethod().when(tested).addDataTypeToSelect(anyString(), anyString(), any(DataObjectTypeSelect.Mode.class), anyBoolean(), anyBoolean(), anyBoolean());
        HTMLOptionElement option = spy(HTMLOptionElement.class);
        option.text = "test_name";
        select.options.length = 1;
        when(collection.item(anyInt())).thenReturn(option);
        tested.addDataTypeToSelect("test_name", "aaa", DataObjectTypeSelect.Mode.EDIT, true, true, true);
        verify(tested, never()).createDataObjectType(anyString(), anyString(), any(DataObjectTypeSelect.Mode.class), anyBoolean(), anyBoolean(), anyBoolean());
    }

    @Test
    public void setValue() {
        HTMLOptionElement option = spy(HTMLOptionElement.class);
        option.text = "test_value";
        select.options.length = 1;
        when(collection.item(anyInt())).thenReturn(option);

        tested.setValue("test_value");
        verify(tested).checkIfNotExists("test_value");
        assertTrue(tested.getValue().equals("test_value"));
    }

    @Test
    public void setValueAndFireEvent() {
        HTMLOptionElement option = spy(HTMLOptionElement.class);
        option.text = "test_value";
        select.options.length = 1;
        when(collection.item(anyInt())).thenReturn(option);

        tested.setValue("test_value", true);
        verify(tested).checkIfNotExists("test_value");
    }

    @Test
    public void onKeyupModeNONE() {
        setFieldValue(tested, "currentAction", DataObjectTypeSelect.Mode.NONE);
        select.style = new CSSStyleDeclaration();
        addOrEdit.style = new CSSStyleDeclaration();
        KeyboardEvent event = spy(new KeyboardEvent("keyup"));
        event.key = "Enter";
        addOrEdit.value = "value";

        EventListener listener = event1 -> tested.onKeyup(event1);
        listener.handleEvent(event);

        verify(event, times(1)).preventDefault();
    }

    @Test
    public void onKeyupModeEDIT() {
        setFieldValue(tested, "currentAction", DataObjectTypeSelect.Mode.EDIT);
        select.style = new CSSStyleDeclaration();
        addOrEdit.style = new CSSStyleDeclaration();
        KeyboardEvent event = spy(new KeyboardEvent("keyup"));
        event.key = "Enter";
        addOrEdit.value = "value";
        HTMLOptionElement option = spy(HTMLOptionElement.class);
        option.text = "value";
        option.value = "value";
        select.options.length = 1;
        when(collection.item(anyInt())).thenReturn(option);
        EventListener listener = event1 -> tested.onKeyup(event1);
        listener.handleEvent(event);
        verify(event, times(1)).preventDefault();
    }

    @Test
    public void onKeyupModeADD() {
        setFieldValue(tested, "currentAction", DataObjectTypeSelect.Mode.ADD);
        select.style = new CSSStyleDeclaration();
        addOrEdit.style = new CSSStyleDeclaration();
        KeyboardEvent event = spy(new KeyboardEvent("keyup"));
        event.key = "Enter";
        addOrEdit.value = "value";
        HTMLOptionElement option = spy(HTMLOptionElement.class);
        option.text = "value";
        option.value = "value";
        select.options.length = 1;
        when(collection.item(anyInt())).thenReturn(option);
        EventListener listener = event1 -> tested.onKeyup(event1);
        listener.handleEvent(event);
        verify(tested).addDataTypeToSelect("value", "value", DataObjectTypeSelect.Mode.LABEL, true, true, true);
    }

    @Test
    public void onChange() {
        HTMLOptionElement option = spy(HTMLOptionElement.class);
        when(option.getAttribute(anyString())).thenReturn(DataObjectTypeSelect.Mode.ADD.name());
        select.style = new CSSStyleDeclaration();
        addOrEdit.style = new CSSStyleDeclaration();
        select.options.length = 1;
        when(collection.item(anyInt())).thenReturn(option);
        Event event = spy(new Event("change"));
        tested.onChange(event);
        verify(addOrEdit).addEventListener(eq("keyup"), any(EventListener.class));
    }

    @Test
    public void onEdit() {
        HTMLOptionElement option = spy(HTMLOptionElement.class);
        when(option.getAttribute(anyString())).thenReturn(DataObjectTypeSelect.Mode.EDIT.name());
        select.style = new CSSStyleDeclaration();
        addOrEdit.style = new CSSStyleDeclaration();
        select.options.length = 1;
        when(collection.item(anyInt())).thenReturn(option);
        Event event = spy(new Event("change"));
        tested.onChange(event);
        verify(addOrEdit).addEventListener(eq("keyup"), any(EventListener.class));    }
}
