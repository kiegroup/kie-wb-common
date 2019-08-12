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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.slaEditor;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.datetimepicker.client.ui.DateTimePicker;
import org.gwtbootstrap3.extras.datetimepicker.client.ui.base.constants.DateTimePickerPosition;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.CSSStyleDeclaration;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.RadioInput;
import org.jboss.errai.common.client.dom.Select;
import org.jboss.errai.common.client.dom.TextInput;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;

import static java.lang.Math.abs;
import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.slaEditor.SLASettingsFieldEditorView.DATA_CONTENT_ATTR;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.slaEditor.SLASettingsFieldEditorView.DurationTimer_Help_Header;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.slaEditor.SLASettingsFieldEditorView.DurationTimer_Help_Line_1;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.slaEditor.SLASettingsFieldEditorView.Expression_Help_Line;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.slaEditor.SLASettingsFieldEditorView.PLACEHOLDER_ATTR;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.slaEditor.SLASettingsFieldEditorView.TimeDuration_Placeholder;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class SLASettingsFieldEditorViewTest {

    private static final String SOME_VALUE = "SOME_VALUE";

    @Mock
    private RadioInput durationTimer;

    @Mock
    private Anchor durationTimerHelp;

    @Mock
    private JQueryProducer.JQuery<Popover> durationTimerHelpPopover;

    @Mock
    private Div durationTimerParamsContainer;

    @Mock
    private CSSStyleDeclaration durationTimerParamsContainerCSS;

    @Mock
    private TextInput timeDuration;

    @Mock
    private RadioInput multipleTimer;

    @Mock
    private Anchor multipleTimerHelp;

    @Mock
    private JQueryProducer.JQuery<Popover> multipleTimerHelpPopover;

    @Mock
    private Popover multipleTimerHelpPopoverWrapped;

    @Mock
    private Div multipleTimerParamsContainer;

    @Mock
    private CSSStyleDeclaration multipleTimerParamsContainerCSS;

    @Mock
    private Select timeCycleLanguage;

    @Mock
    private CSSStyleDeclaration dateTimerParamsContainerCSS;

    @Mock
    private TextInput timeDate;

    @Mock
    private CSSStyleDeclaration timeDateCSS;

    @Mock
    private DateTimePicker timeDateTimePicker;

    @Mock
    private TextBox timeDateTimePickerTextBox;

    @Mock
    private Button pickerButton;

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private SLASettingsFieldEditorPresenter presenter;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @InjectMocks
    private SLASettingsFieldEditorView view;

    @Before
    public void setUp() {
        view.init(presenter);
        when(multipleTimerParamsContainer.getStyle()).thenReturn(multipleTimerParamsContainerCSS);
        when(durationTimerParamsContainer.getStyle()).thenReturn(durationTimerParamsContainerCSS);
        when(timeDate.getStyle()).thenReturn(timeDateCSS);
        when(timeDateTimePicker.getTextBox()).thenReturn(timeDateTimePickerTextBox);

        when(multipleTimerHelpPopover.wrap(multipleTimerHelp)).thenReturn(multipleTimerHelpPopoverWrapped);

        when(translationService.getValue(TimeDuration_Placeholder)).thenReturn(TimeDuration_Placeholder);

        when(translationService.getValue(DurationTimer_Help_Header)).thenReturn(DurationTimer_Help_Header);
        when(translationService.getValue(DurationTimer_Help_Line_1)).thenReturn(DurationTimer_Help_Line_1);
        when(translationService.getValue(Expression_Help_Line)).thenReturn(Expression_Help_Line);

        when(translationService.getValue(Expression_Help_Line)).thenReturn(Expression_Help_Line);

        when(translationService.getValue(Expression_Help_Line)).thenReturn(Expression_Help_Line);

       }

    @Test
    public void testInit() {
        view.init();
        verify(timeDuration).setAttribute(PLACEHOLDER_ATTR, TimeDuration_Placeholder);

        String expectedDurationHelp = DurationTimer_Help_Header + ":" + "<UL>" +
                "<LI>" + DurationTimer_Help_Line_1 + "</LI>" +
                "<LI>" + Expression_Help_Line + "</LI>" +
                "</UL>";
        verify(durationTimerHelp).setAttribute(DATA_CONTENT_ATTR, expectedDurationHelp);
   verify(timeDateTimePicker).setAutoClose(true);
        verify(timeDateTimePicker).setHighlightToday(true);
        verify(timeDateTimePicker).setShowTodayButton(true);
        verify(timeDateTimePicker).setForceParse(false);
        verify(timeDateTimePicker).addValueChangeHandler(anyObject());
        verify(timeDateTimePicker).addHideHandler(anyObject());
        verify(timeDateTimePicker).setPosition(DateTimePickerPosition.BOTTOM_RIGHT);
        verify(timeDateTimePicker).reload();
    }

    @Test
    public void testSetTimeDuration() {
        view.setTimeDuration(SOME_VALUE);
        verify(timeDuration).setValue(SOME_VALUE);
    }

    @Test
    public void testGetTimeDuration() {
        when(timeDuration.getValue()).thenReturn(SOME_VALUE);
        assertEquals(SOME_VALUE, view.getTimeDuration());
    }


    @Test
    public void setDurationTimerChecked() {
        boolean arbitraryValue = true;
        view.setDurationTimerChecked(arbitraryValue);
        verify(durationTimer).setChecked(arbitraryValue);
    }


    @Test
    public void testShowDurationTimerParams() {
        testShowDurationTimerParams(true);
        testShowDurationTimerParams(false);
    }

    private void testShowDurationTimerParams(boolean show) {
        view.showDurationTimerParams(show);
        verifyElementShown(durationTimerParamsContainer, show);
    }



    private void verifyElementShown(HTMLElement element, boolean show) {
        if (show) {
            verify(element.getStyle()).removeProperty("display");
        } else {
            verify(element.getStyle()).setProperty("display", "none");
        }
    }


    @Test
    public void testParseFromISO1() {
        testParseFromISO(2019, Calendar.DECEMBER, 25, 9, 10, 15);
    }

    @Test
    public void testParseFromISO2() {
        testParseFromISO(2019, Calendar.AUGUST, 25, 9, 10, 15);
    }

    /**
     * @param year
     * @param month from 0..11
     * @param hour
     * @param minute
     * @param second
     */
    private void testParseFromISO(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault());
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.AM_PM, Calendar.AM);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        int zone = calendar.get(Calendar.ZONE_OFFSET) / 60 / 60 / 1000;
        int daylightSaving = calendar.get(Calendar.DST_OFFSET) / 60 / 60 / 1000;
        zone = zone + daylightSaving;
        String currentValue = calendar.get(Calendar.YEAR) + "-" + fullInt(calendar.get(Calendar.MONTH) + 1) + "-" + fullInt(calendar.get(Calendar.DAY_OF_MONTH)) +
                "T" + fullInt(calendar.get(Calendar.HOUR)) + ":" + fullInt(calendar.get(Calendar.MINUTE)) + ":" + fullInt(calendar.get(Calendar.SECOND)) + (zone >= 0 ? "+" : "-") + fullInt(abs(zone)) + ":00";

        Date date = view.parseFromISO(currentValue);
        GregorianCalendar result = new GregorianCalendar(TimeZone.getDefault());
        result.setTime(date);
        assertEquals(year, result.get(Calendar.YEAR));
        assertEquals(month, result.get(Calendar.MONTH));
        assertEquals(dayOfMonth, result.get(Calendar.DAY_OF_MONTH));
        assertEquals(hour, result.get(Calendar.HOUR));
        assertEquals(minute, result.get(Calendar.MINUTE));
        assertEquals(second, result.get(Calendar.SECOND));
    }

    @Test
    public void testParseFromISOUnSuccessful() {
        expectedException.expect(IllegalArgumentException.class);
        view.parseFromISO("wrong value");
    }

    @Test
    public void testFormatISO1() {
        testFormatToISO(2019, Calendar.DECEMBER, 25, 9, 10, 15);
    }

    @Test
    public void testFormatISO2() {
        testFormatToISO(2019, Calendar.AUGUST, 25, 9, 10, 15);
    }

    /**
     * @param year
     * @param month from 0..11
     * @param hour
     * @param minute
     * @param second
     */
    private void testFormatToISO(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault());
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.AM_PM, Calendar.AM);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        int zone = calendar.get(Calendar.ZONE_OFFSET) / 60 / 60 / 1000;
        int daylightSaving = calendar.get(Calendar.DST_OFFSET) / 60 / 60 / 1000;
        zone = zone + daylightSaving;
        Date date = calendar.getTime();
        String expectedValue = calendar.get(Calendar.YEAR) + "-" + fullInt(calendar.get(Calendar.MONTH) + 1) + "-" + fullInt(calendar.get(Calendar.DAY_OF_MONTH)) +
                "T" + fullInt(calendar.get(Calendar.HOUR)) + ":" + fullInt(calendar.get(Calendar.MINUTE)) + ":" + fullInt(calendar.get(Calendar.SECOND)) + (zone >= 0 ? "+" : "-") + fullInt(abs(zone)) + ":00";
        assertEquals(expectedValue, view.formatToISO(date));
    }

    private static String fullInt(int value) {
        return value < 10 ? "0" + value : Integer.toString(value);
    }

    @Test
    public void testSetReadOnly() {
        boolean arbitraryValue = false;
        view.setReadOnly(arbitraryValue);
        verify(durationTimer).setDisabled(arbitraryValue);
        verify(timeDuration).setDisabled(arbitraryValue);
        verify(multipleTimer).setDisabled(arbitraryValue);
        verify(timeCycleLanguage).setDisabled(arbitraryValue);
        verify(timeDate).setDisabled(arbitraryValue);
        verify(pickerButton).setDisabled(arbitraryValue);
    }

    @Test
    public void testOnTimeDurationChange() {
        view.onTimeDurationChange(mock(Event.class));
        verify(presenter).onTimerDurationChange();
    }
}
