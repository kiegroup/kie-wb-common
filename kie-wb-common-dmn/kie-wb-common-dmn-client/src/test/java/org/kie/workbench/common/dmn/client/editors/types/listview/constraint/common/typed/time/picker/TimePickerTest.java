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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker;

import java.util.Date;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLInputElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePicker.TIME_FORMAT;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class TimePickerTest {

    @Mock
    private HTMLInputElement input;

    @Mock
    private TimePicker.View view;

    @Captor
    private ArgumentCaptor<Long> dateInMillisArgumentCaptor;

    @Captor
    private ArgumentCaptor<Date> dateArgumentCaptor;

    private TimePicker picker;

    @Before
    public void setup() {
        picker = spy(new TimePicker(view));
        doReturn(input).when(picker).getInputBind();
    }

    @Test
    public void testRefreshDateInPopup() {

        final String inputValue = "22:30:51";
        final long expected = TIME_FORMAT.parse(inputValue).getTime();

        input.value = inputValue;

        picker.refreshDateInPopup();

        verify(view).setDate(dateInMillisArgumentCaptor.capture());

        final long actual = dateInMillisArgumentCaptor.getValue();

        assertEquals(expected, actual);
    }

    @Test
    public void testIsDateSetInInput() {

        input.value = "01:25";
        final boolean actual = picker.isDateSetInInput();
        assertTrue(actual);
    }

    @Test
    public void testIsDateSetInInputDateNotSet() {

        input.value = "";
        final boolean actual = picker.isDateSetInInput();
        assertFalse(actual);
    }

    @Test
    public void testIsDateSetInInputNotATime() {

        input.value = "1234";
        final boolean actual = picker.isDateSetInInput();
        assertFalse(actual);
    }

    @Test
    public void testOnDateChanged() {

        final Consumer<java.util.Date> consumer = mock(Consumer.class);
        picker.setOnDateChanged(consumer);
        final java.util.Date javaDate = new java.util.Date();
        final String expected = TIME_FORMAT.format(javaDate);

        picker.onDateChanged(javaDate.getTime());

        assertEquals(expected, input.value);

        verify(consumer).accept(dateArgumentCaptor.capture());
        assertEquals(javaDate.getTime(), dateArgumentCaptor.getValue().getTime());
    }
}