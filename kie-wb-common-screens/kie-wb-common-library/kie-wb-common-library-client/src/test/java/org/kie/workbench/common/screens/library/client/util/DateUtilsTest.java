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

package org.kie.workbench.common.screens.library.client.util;

import java.util.Date;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DateUtilsTest {

    private DateUtils dateUtils;

    @Mock
    private TranslationService ts;

    @Before
    public void setUp() {
        dateUtils = new DateUtils(ts);
    }

    private Date createDateFromNow(int daysToSubtract) {
        Date today = new Date();
        int miliSecondsToSubtract = daysToSubtract * DateUtils.ONE_DAY_IN_MS;
        Date targetDate = new Date(today.getTime() - miliSecondsToSubtract);
        return targetDate;
    }

    @Test
    public void printToday() {
        when(ts.getTranslation(LibraryConstants.Today)).thenReturn("today");

        String expected = "today";
        String actual = dateUtils.format(new Date());

        assertEquals(expected, actual);
    }

    @Test
    public void printOneDayAgo() {
        when(ts.getTranslation(LibraryConstants.DayAgo)).thenReturn("day ago");

        String expected = "1 day ago";
        String actual = dateUtils.format(createDateFromNow(1));

        assertEquals(expected, actual);
    }

    @Test
    public void printTwoDaysAgo() {
        when(ts.getTranslation(LibraryConstants.DaysAgo)).thenReturn("days ago");

        String expected = "2 days ago";
        String actual = dateUtils.format(createDateFromNow(2));

        assertEquals(expected, actual);
    }

    @Test
    public void printOneWeekAgo() {
        when(ts.getTranslation(LibraryConstants.OneWeekAgo)).thenReturn("1 week ago");

        String expected = "1 week ago";
        String actual = dateUtils.format(createDateFromNow(7));

        assertEquals(expected, actual);
    }

    @Test
    public void printOneWeekAgo2() {
        when(ts.getTranslation(LibraryConstants.OneWeekAgo)).thenReturn("1 week ago");

        String expected = "1 week ago";
        String actual = dateUtils.format(createDateFromNow(8));

        assertEquals(expected, actual);
    }

    @Test
    public void printTwoWeeksAgo1() {
        when(ts.getTranslation(LibraryConstants.WeeksAgo)).thenReturn("weeks ago");

        String expected = "2 weeks ago";
        String actual = dateUtils.format(createDateFromNow(14));

        assertEquals(expected, actual);
    }

    @Test
    public void printTwoWeeksAgo() {
        when(ts.getTranslation(LibraryConstants.WeeksAgo)).thenReturn("weeks ago");

        String expected = "2 weeks ago";
        String actual = dateUtils.format(createDateFromNow(15));

        assertEquals(expected, actual);
    }
}