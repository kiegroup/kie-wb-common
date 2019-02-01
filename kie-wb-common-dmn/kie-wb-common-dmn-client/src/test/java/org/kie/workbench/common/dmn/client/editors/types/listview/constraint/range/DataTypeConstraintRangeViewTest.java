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
import elemental2.dom.HTMLInputElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeConstraintRangeViewTest {

    @Mock
    private HTMLInputElement startValue;

    @Mock
    private HTMLInputElement endValue;

    @Mock
    private HTMLInputElement includeStartValue;

    @Mock
    private HTMLInputElement includeEndValue;

    @Mock
    private DataTypeConstraintRange presenter;

    private DataTypeConstraintRangeView view;

    @Before
    public void setup() {
        view = new DataTypeConstraintRangeView(startValue,
                                               endValue,
                                               includeStartValue,
                                               includeEndValue);
    }

    @Test
    public void testInit() {
        final DataTypeConstraintRangeView mocked = mock(DataTypeConstraintRangeView.class);
        doCallRealMethod().when(mocked).init(any());
        mocked.init(presenter);
        verify(mocked).setupInputFields();
    }

    @Test
    public void testGetStartValue() {
        final String expected = "someString";
        startValue.value = expected;
        final String actual = view.getStartValue();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetEndValue() {
        final String expected = "someString";
        endValue.value = expected;
        final String actual = view.getEndValue();
        assertEquals(expected, actual);
    }

    @Test
    public void testSetStartValue() {
        final String expected = "someString";
        view.setStartValue(expected);
        final String actual = startValue.value;
        assertEquals(expected, actual);
    }

    @Test
    public void testSetEndValue() {
        final String expected = "someString";
        view.setEndValue(expected);
        final String actual = endValue.value;
        assertEquals(expected, actual);
    }

    @Test
    public void testGetIncludeStartValue() {
        final boolean expected = true;
        includeStartValue.checked = expected;
        final boolean actual = view.getIncludeStartValue();
        assertEquals(expected, actual);
    }

    @Test
    public void testSetIncludeStartValue() {
        final boolean expected = true;
        view.setIncludeStartValue(expected);
        final boolean actual = includeStartValue.checked;
        assertEquals(expected, actual);
    }

    @Test
    public void testGetIncludeEndValue() {
        final boolean expected = true;
        includeEndValue.checked = expected;
        final boolean actual = view.getIncludeEndValue();
        assertEquals(expected, actual);
    }

    @Test
    public void testSetIncludeEndValue() {
        final boolean expected = true;
        view.setIncludeEndValue(expected);
        final boolean actual = includeEndValue.checked;
        assertEquals(expected, actual);
    }
}