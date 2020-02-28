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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.artifacts;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import elemental2.dom.HTMLOptionsCollection;
import elemental2.dom.HTMLSelectElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.artifacts.widgets.DataObjectTypeSelect;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ReflectionUtilsTest;
import org.kie.workbench.common.stunner.bpmn.definition.property.artifacts.DataObjectTypeValue;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(LienzoMockitoTestRunner.class)
public class DataObjectTypeWidgetTest extends ReflectionUtilsTest {

    @Mock
    private DataObjectTypeWidget widget;

    private DataObjectTypeSelect dataObjectTypeSelect;

    @Mock
    private HTMLOptionsCollection options;

    @Mock
    private HTMLSelectElement select;

    @Before
    public void setUp() throws Exception {
        dataObjectTypeSelect = spy(DataObjectTypeSelect.class);
        setFieldValue(widget, "select", dataObjectTypeSelect);
        setFieldValue(dataObjectTypeSelect, "select", select);

        setFieldValue(select, "options", options);

        doCallRealMethod().when(widget).setReadOnly(anyBoolean());
        doCallRealMethod().when(widget).addValueChangeHandler(any(ValueChangeHandler.class));
        doCallRealMethod().when(widget).addHandler(any(ValueChangeHandler.class), any(GwtEvent.Type.class));
        doCallRealMethod().when(widget).init();

        doCallRealMethod().when(dataObjectTypeSelect).addValueChangeHandler(any(ValueChangeHandler.class));
        doCallRealMethod().when(dataObjectTypeSelect).addValueChangeHandler(any(ValueChangeHandler.class));

        doNothing().when(widget).setValue(any(DataObjectTypeValue.class));
        doNothing().when(dataObjectTypeSelect).setValue(any(String.class));
    }

    @Test
    public void testReadOnly() {
        widget.setReadOnly(true);
        verify(dataObjectTypeSelect, times(1)).setReadOnly(true);
    }

    @Test
    public void testInit() {
        widget.init();
        verify(dataObjectTypeSelect, times(1)).setValue(String.class.getSimpleName());
        verify(dataObjectTypeSelect, times(1)).addValueChangeHandler(any(ValueChangeHandler.class));
        verify(dataObjectTypeSelect, times(1)).addValueChangeHandler(any(ValueChangeHandler.class));
    }

    @Test
    public void testSetValue() {
        doCallRealMethod().when(widget).setValue(any(DataObjectTypeValue.class));
        doCallRealMethod().when(widget).setValue(any(DataObjectTypeValue.class), anyBoolean());
        doCallRealMethod().when(widget).getValue();

        DataObjectTypeValue value = new DataObjectTypeValue("any_name");
        widget.setValue(value);

        assertEquals(value, widget.getValue());
        verify(widget, times(1)).setValue(value, false);
        verify(widget, times(1)).getValue();
    }

    @Test
    public void addValueChangeHandler() {
        HandlerRegistration registration = widget.addValueChangeHandler(event -> {
        });
        verify(widget, times(1)).addHandler(any(ValueChangeHandler.class), any(GwtEvent.Type.class));
        assertNotNull(registration);
    }

}
