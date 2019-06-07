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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.time;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DateTimeValueTest {

    @Mock
    private DateTimeValue dateTimeValue;

    @Test
    public void testIsDateAndTimeSetWhenBothAreSet() {

        final String date = "date";
        final String time = "time";

        when(dateTimeValue.getDate()).thenReturn(date);
        when(dateTimeValue.getTime()).thenReturn(time);

        when(dateTimeValue.isDateAndTimeSet()).thenCallRealMethod();
        when(dateTimeValue.hasDate()).thenCallRealMethod();
        when(dateTimeValue.hasTime()).thenCallRealMethod();

        final boolean actual = dateTimeValue.isDateAndTimeSet();
        assertTrue(actual);
    }

    @Test
    public void testIsDateAndTimeSetWhenTimeAreNotSet() {

        final String date = "date";

        when(dateTimeValue.getDate()).thenReturn(date);

        when(dateTimeValue.isDateAndTimeSet()).thenCallRealMethod();
        when(dateTimeValue.hasDate()).thenCallRealMethod();
        when(dateTimeValue.hasTime()).thenCallRealMethod();

        final boolean actual = dateTimeValue.isDateAndTimeSet();
        assertFalse(actual);
    }

    @Test
    public void testIsDateAndTimeSetWhenDateAreNotSet() {

        final String time = "time";

        when(dateTimeValue.getTime()).thenReturn(time);

        when(dateTimeValue.isDateAndTimeSet()).thenCallRealMethod();
        when(dateTimeValue.hasDate()).thenCallRealMethod();
        when(dateTimeValue.hasTime()).thenCallRealMethod();

        final boolean actual = dateTimeValue.isDateAndTimeSet();
        assertFalse(actual);
    }
}