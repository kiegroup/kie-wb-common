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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModal;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeConstraintRangeTest {

    @Mock
    private DataTypeConstraintRange.View view;

    @Mock
    private DataTypeConstraintModal modal;

    private DataTypeConstraintRange constraintRange;

    @Before
    public void setup() {
        constraintRange = new DataTypeConstraintRange(view);
    }

    @Test
    public void testSetup() {
        constraintRange.setup();

        verify(view).init(constraintRange);
    }

    @Test
    public void testSetModal(){
        constraintRange.setModal(modal);
        verify(modal).disableOkButton();
    }

    @Test
    public void testDisableOkButton(){
        constraintRange.setModal(modal);
        constraintRange.enableOkButton();
        constraintRange.disableOkButton();
        verify(modal, times(2)).disableOkButton();
    }

    @Test
    public void testEnableOkButton(){
        constraintRange.setModal(modal);
        constraintRange.enableOkButton();
        verify(modal).enableOkButton();
    }

    @Test
    public void testSetUnkownValue() {
        constraintRange.setValue("some random string");
        verify(view).setIncludeStartValue(false);
        verify(view).setIncludeEndValue(false);
        verify(view).setStartValue("");
        verify(view).setEndValue("");
    }

    @Test
    public void testSetMalformedValue1() {
        constraintRange.setValue("..]");
        verify(view).setIncludeStartValue(false);
        verify(view).setIncludeEndValue(false);
        verify(view).setStartValue("");
        verify(view).setEndValue("");
    }

    @Test
    public void testSetMalformedValue2() {
        constraintRange.setValue("asd.(.45]");
        verify(view).setIncludeStartValue(false);
        verify(view).setIncludeEndValue(false);
        verify(view).setStartValue("");
        verify(view).setEndValue("");
    }

    @Test
    public void testSetValueWithLeadingAndTrailingSpaces() {
        constraintRange.setValue("   [1..0]    ");
        verify(view).setIncludeStartValue(true);
        verify(view).setIncludeEndValue(true);
        verify(view).setStartValue("1");
        verify(view).setEndValue("0");
    }

    @Test
    public void testSetEmptyValue() {
        constraintRange.setValue("");
        verify(view).setIncludeStartValue(false);
        verify(view).setIncludeEndValue(false);
        verify(view).setStartValue("");
        verify(view).setEndValue("");
    }

    @Test
    public void testSetValueIncludeStartExcludeEnd() {
        constraintRange.setValue("[0..1)");
        verify(view).setIncludeStartValue(true);
        verify(view).setIncludeEndValue(false);
        verify(view).setStartValue("0");
        verify(view).setEndValue("1");
    }

    @Test
    public void testSetValueExcludeStartIncludeEnd() {
        constraintRange.setValue("(0..1]");
        verify(view).setIncludeStartValue(false);
        verify(view).setIncludeEndValue(true);
        verify(view).setStartValue("0");
        verify(view).setEndValue("1");
    }

    @Test
    public void testSetValueExcludeBoth() {
        constraintRange.setValue("(0..1)");
        verify(view).setIncludeStartValue(false);
        verify(view).setIncludeEndValue(false);
        verify(view).setStartValue("0");
        verify(view).setEndValue("1");
    }

    @Test
    public void testSetValueIncludeBoth() {
        constraintRange.setValue("[0..1]");
        verify(view).setIncludeStartValue(true);
        verify(view).setIncludeEndValue(true);
        verify(view).setStartValue("0");
        verify(view).setEndValue("1");
    }

    @Test
    public void testSetValue() {
        constraintRange.setValue("[123..456]");
        verify(view).setIncludeStartValue(true);
        verify(view).setIncludeEndValue(true);
        verify(view).setStartValue("123");
        verify(view).setEndValue("456");
    }

    @Test
    public void testGetValueExcludeBoth() {
        when(view.getIncludeStartValue()).thenReturn(false);
        when(view.getStartValue()).thenReturn("1");
        when(view.getEndValue()).thenReturn("6");
        when(view.getIncludeEndValue()).thenReturn(false);

        final String expected = "(1..6)";
        final String actual = constraintRange.getValue();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetValueIncludeBoth() {
        when(view.getIncludeStartValue()).thenReturn(true);
        when(view.getStartValue()).thenReturn("1");
        when(view.getEndValue()).thenReturn("6");
        when(view.getIncludeEndValue()).thenReturn(true);

        final String expected = "[1..6]";
        final String actual = constraintRange.getValue();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetValueIncludeStartExcludeEnd() {
        when(view.getIncludeStartValue()).thenReturn(true);
        when(view.getStartValue()).thenReturn("1");
        when(view.getEndValue()).thenReturn("6");
        when(view.getIncludeEndValue()).thenReturn(false);

        final String expected = "[1..6)";
        final String actual = constraintRange.getValue();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetValueExcludeStartIncludeEnd() {
        when(view.getIncludeStartValue()).thenReturn(false);
        when(view.getStartValue()).thenReturn("1");
        when(view.getEndValue()).thenReturn("6");
        when(view.getIncludeEndValue()).thenReturn(true);

        final String expected = "(1..6]";
        final String actual = constraintRange.getValue();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetValue() {
        when(view.getIncludeStartValue()).thenReturn(true);
        when(view.getStartValue()).thenReturn("some_value");
        when(view.getEndValue()).thenReturn("other_value");
        when(view.getIncludeEndValue()).thenReturn(true);

        final String expected = "[some_value..other_value]";
        final String actual = constraintRange.getValue();
        assertEquals(expected, actual);
    }
}
