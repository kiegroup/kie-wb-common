/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells;

import java.util.Date;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.commons.util.Maps;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.uberfire.ext.widgets.common.client.common.DatePicker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.workbench.common.services.shared.preferences.ApplicationPreferences.DATE_FORMAT;
import static org.kie.workbench.common.services.shared.preferences.ApplicationPreferences.KIE_TIMEZONE_OFFSET;
import static org.kie.workbench.common.widgets.client.util.TimeZoneUtils.FORMATTER;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class PopupUniversalDateEditCellTest {

    private static final String TEST_DATE_FORMAT = "MM-dd-yyyy";

    @GwtMock
    private DatePicker datePicker;

    @GwtMock
    private ValueUpdater<String> valueUpdater;

    @GwtMock
    private SafeHtmlRenderer<String> renderer;

    @Captor
    private ArgumentCaptor<Date> dateCaptor;

    private PopupUniversalDateEditCell cell;

    @Before
    public void setup() {
        ApplicationPreferences.setUp(new Maps.Builder()
                                             .put(KIE_TIMEZONE_OFFSET, "10800000")
                                             .put(DATE_FORMAT, TEST_DATE_FORMAT)
                                             .build());

        cell = spy(new PopupUniversalDateEditCell(false) {

            @Override
            DatePicker getDatePicker() {
                return PopupUniversalDateEditCellTest.this.datePicker;
            }

            @Override
            ValueUpdater<String> getValueUpdater() {
                return PopupUniversalDateEditCellTest.this.valueUpdater;
            }

            @Override
            SafeHtmlRenderer<String> getRenderer() {
                return PopupUniversalDateEditCellTest.this.renderer;
            }

            @Override
            public void setValue(final Context context,
                                 final Element parent,
                                 final String value) {
                // Nothing.
            }
        });
    }

    @Test
    public void testStartEditing() {

        final Cell.Context context = mock(Cell.Context.class);
        final Element parent = mock(Element.class);
        final String sExpectedDate = "04-01-2018";
        final Date expectedDate = FORMATTER.parse(sExpectedDate);

        cell.startEditing(context, parent, sExpectedDate);

        verify(datePicker).setValue(dateCaptor.capture());

        final Date actualDate = dateCaptor.getValue();

        assertEquals(expectedDate, actualDate);
    }

    @Test
    public void testStartEditingEmptyValue() {

        final Cell.Context context = mock(Cell.Context.class);
        final Element parent = mock(Element.class);
        final String sExpectedDate = "";

        cell.startEditing(context, parent, sExpectedDate);

        verify(datePicker).setValue(dateCaptor.capture());

        final Date actualDate = dateCaptor.getValue();

        // we can not assert precise value because CI
        assertNotNull(actualDate);
    }

    @Test
    public void testStartEditingNullValue() {

        final Cell.Context context = mock(Cell.Context.class);
        final Element parent = mock(Element.class);
        final String sExpectedDate = null;

        cell.startEditing(context, parent, sExpectedDate);

        verify(datePicker).setValue(dateCaptor.capture());

        final Date actualDate = dateCaptor.getValue();

        // we can not assert precise value because CI
        assertNotNull(actualDate);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStartEditingInvalidValue() {

        final Cell.Context context = mock(Cell.Context.class);
        final Element parent = mock(Element.class);
        final String sExpectedDate = "XYZ";

        cell.startEditing(context, parent, sExpectedDate);
    }

    @Test
    public void testCommit() {
        final Cell.Context context = mock(Cell.Context.class);
        final Element parent = mock(Element.class);
        final String sExpectedDate = "04-01-2018";
        final Date expectedDate = FORMATTER.parse(sExpectedDate);

        cell.lastContext = context;
        cell.lastParent = parent;
        when(datePicker.getValue()).thenReturn(expectedDate);

        cell.commit();

        verify(valueUpdater).update(sExpectedDate);
        verify(cell).setValue(eq(context),
                              eq(parent),
                              eq(sExpectedDate));
    }

    @Test
    public void testCommitNullValue() {
        final Cell.Context context = mock(Cell.Context.class);
        final Element parent = mock(Element.class);

        cell.lastContext = context;
        cell.lastParent = parent;

        cell.commit();

        verify(valueUpdater).update(null);
        verify(cell).setValue(eq(context),
                              eq(parent),
                              eq(null));
    }

    @Test
    public void testCommitWhenValueUpdaterIsNull() {

        final Cell.Context context = mock(Cell.Context.class);
        final Element parent = mock(Element.class);
        final String sExpectedDate = "04-01-2018";
        final Date expectedDate = FORMATTER.parse(sExpectedDate);

        cell.lastContext = context;
        cell.lastParent = parent;
        when(datePicker.getValue()).thenReturn(expectedDate);
        doReturn(null).when(cell).getValueUpdater();

        cell.commit();

        verify(valueUpdater, never()).update(anyString());
        verify(cell).setValue(eq(context),
                              eq(parent),
                              eq(sExpectedDate));
    }

    @Test
    public void testRender() {

        final Cell.Context context = mock(Cell.Context.class);
        final SafeHtmlBuilder safeHtmlBuilder = mock(SafeHtmlBuilder.class);
        final String clientDate = "05-01-2018";

        cell.render(context, clientDate, safeHtmlBuilder);

        verify(renderer).render(eq("05-01-2018"));
    }

    @Test
    public void testRenderWhenValueIsNull() {

        final Cell.Context context = mock(Cell.Context.class);
        final SafeHtmlBuilder safeHtmlBuilder = mock(SafeHtmlBuilder.class);

        cell.render(context, null, safeHtmlBuilder);

        verify(renderer, never()).render(anyString());
    }

    @Test
    public void testGetPattern() {
        assertEquals(TEST_DATE_FORMAT, cell.getPattern());
    }
}
