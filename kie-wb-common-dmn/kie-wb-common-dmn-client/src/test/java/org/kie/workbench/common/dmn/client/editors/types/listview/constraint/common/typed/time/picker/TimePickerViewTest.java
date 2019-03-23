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

import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.core.Date;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TimePickerViewTest {

    @Mock
    private HTMLDivElement timePickerContainer;

    @Mock
    private HTMLAnchorElement increaseHours;

    @Mock
    private HTMLAnchorElement decreaseHours;

    @Mock
    private HTMLAnchorElement increaseMinutes;

    @Mock
    private HTMLAnchorElement decreaseMinutes;

    @Mock
    private HTMLAnchorElement increaseSeconds;

    @Mock
    private HTMLAnchorElement decreaseSeconds;

    @Mock
    private HTMLElement hours;

    @Mock
    private HTMLElement minutes;

    @Mock
    private HTMLElement seconds;

    @Mock
    private Date date;

    private TimePickerView view;

    @Before
    public void setup() {
        view = spy(new TimePickerView(timePickerContainer,
                                      increaseHours,
                                      decreaseHours,
                                      increaseMinutes,
                                      decreaseMinutes,
                                      increaseSeconds,
                                      decreaseSeconds,
                                      hours,
                                      minutes,
                                      seconds));

        doReturn(date).when(view).getDate();
    }

    @Test
    public void testRefresh() {

        final double hours = 14.0d;
        final double minutes = 25.0d;
        final double seconds = 17.0d;
        final double timeInMillis = 12345676.0d;
        final Consumer<Long> onDateChanged = mock(Consumer.class);
        view.setOnDateChanged(onDateChanged);

        when(date.getHours()).thenReturn(hours);
        when(date.getMinutes()).thenReturn(minutes);
        when(date.getSeconds()).thenReturn(seconds);
        when(date.getTime()).thenReturn(timeInMillis);

        view.refresh();

        verify(view).setHours(hours);
        verify(view).setMinutes(minutes);
        verify(view).setSeconds(seconds);
        verify(onDateChanged).accept((long) timeInMillis);
    }

    @Test
    public void testFormatSingleDigit() {

        final double input = 4.0d;
        testFormat(input, "04");
    }

    @Test
    public void testFormatTwoDigits() {

        final double input = 14.0d;
        testFormat(input, "14");
    }

    @Test
    public void testFormatZero() {

        final double input = 0.0d;
        testFormat(input, "00");
    }

    private void testFormat(final double input, final String expected) {

        final String actual = view.format(input);
        assertEquals(expected, actual);
    }

    @Test
    public void testOnIncreaseHoursClick() {

        when(date.getHours()).thenReturn(1.0d);
        doNothing().when(view).refresh();

        view.onIncreaseHoursClick(null);

        verify(date).setHours(2.0d);
    }

    @Test
    public void testOnDecreaseHoursClick() {

        when(date.getHours()).thenReturn(2.0d);
        doNothing().when(view).refresh();

        view.onDecreaseHoursClick(null);

        verify(date).setHours(1.0d);
    }

    @Test
    public void testOnIncreaseMinutesClick() {

        when(date.getMinutes()).thenReturn(1.0d);
        doNothing().when(view).refresh();

        view.onIncreaseMinutesClick(null);

        verify(date).setMinutes(2.0d);
    }

    @Test
    public void testOnDecreaseMinutesClick() {

        when(date.getMinutes()).thenReturn(2.0d);
        doNothing().when(view).refresh();

        view.onDecreaseMinutesClick(null);

        verify(date).setMinutes(1.0d);
    }

    @Test
    public void testOnIncreaseSecondsClick() {

        when(date.getSeconds()).thenReturn(1.0d);
        doNothing().when(view).refresh();

        view.onIncreaseSecondsClick(null);

        verify(date).setSeconds(2.0d);
    }

    @Test
    public void testOnDecreaseSecondsClick() {

        when(date.getSeconds()).thenReturn(2.0d);
        doNothing().when(view).refresh();

        view.onDecreaseSecondsClick(null);

        verify(date).setSeconds(1.0d);
    }

    @Test
    public void testSetSeconds() {

        when(date.getHours()).thenReturn(23.0d);
        when(date.getMinutes()).thenReturn(59.0d);
        when(date.getSeconds()).thenReturn(59.0d);
        doNothing().when(view).refresh();

        view.setSeconds(0);

        verify(date).setSeconds(0.0d);
        verify(date).setMinutes(59.0d);
        verify(date).setHours(23.0d);
        verify(view).refresh();
    }
}