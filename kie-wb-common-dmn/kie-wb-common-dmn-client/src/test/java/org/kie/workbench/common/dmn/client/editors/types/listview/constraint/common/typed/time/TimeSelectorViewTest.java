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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time;

import java.util.List;
import java.util.function.Consumer;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.Element;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.editors.types.DMNSimpleTimeZone;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePicker;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeValue;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeValueFormatter;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeZoneProvider;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.mockito.Mock;

import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelectorView.NONE_TRANSLATION_KEY;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelectorView.NONE_VALUE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelectorView.OFFSET_CLASS_ICON;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelectorView.TIMEZONE_CLASS_ICON;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeValue.TimeZoneMode.NONE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeValue.TimeZoneMode.OFFSET;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeValue.TimeZoneMode.TIMEZONE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TimeSelectorViewTest {

    @Mock
    private HTMLButtonElement toggleTimeZoneButton;

    @Mock
    private HTMLElement toggleTimeZoneIcon;

    @Mock
    private HTMLInputElement timeInput;

    @Mock
    private Select timeZoneSelector;

    @Mock
    private TimePicker picker;

    @Mock
    private TimeValueFormatter formatter;

    @Mock
    private Consumer<BlurEvent> onValueInputBlur;

    @Mock
    private TimeZoneProvider timeZoneProvider;

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private DOMTokenList toggleTimeZoneIconClassList;

    private TimeSelectorView view;

    @Before
    public void setup() {

        toggleTimeZoneIcon.classList = toggleTimeZoneIconClassList;

        view = spy(new TimeSelectorView(timeInput,
                                        picker,
                                        timeZoneProvider,
                                        formatter,
                                        toggleTimeZoneIcon,
                                        toggleTimeZoneButton,
                                        translationService)
        );

        doReturn(timeZoneSelector).when(view).getTimeZoneSelector();
        doReturn(onValueInputBlur).when(view).getOnValueInputBlur();
    }

    @Test
    public void testPopulateTimeZoneSelectorWithIds() {

        final Option noneOption = mock(Option.class);
        final Option tz1Option = mock(Option.class);
        final Option tz2Option = mock(Option.class);
        final List<DMNSimpleTimeZone> timeZones = mock(List.class);
        final DMNSimpleTimeZone tz1 = mock(DMNSimpleTimeZone.class);
        final DMNSimpleTimeZone tz2 = mock(DMNSimpleTimeZone.class);
        when(tz1.getId()).thenReturn("time zone 1");
        when(tz2.getId()).thenReturn("other time zone");
        when(timeZones.size()).thenReturn(2);
        when(timeZones.get(0)).thenReturn(tz1);
        when(timeZones.get(1)).thenReturn(tz2);

        doReturn(tz1Option).when(view).createOptionWithId(tz1);
        doReturn(tz2Option).when(view).createOptionWithId(tz2);
        doReturn(timeZones).when(view).getTimeZones();
        doReturn(noneOption).when(view).createNoneOption();

        view.populateTimeZoneSelectorWithIds();

        verify(timeZoneSelector).clear();
        verify(timeZoneSelector).add(noneOption);
        verify(timeZoneSelector).add(tz1Option);
        verify(timeZoneSelector).add(tz2Option);
        verify(timeZoneSelector).refresh();
    }

    @Test
    public void testCreateOptionWithId() {

        final String optionId = "some id";
        final Option option = mock(Option.class);
        final DMNSimpleTimeZone tz = new DMNSimpleTimeZone();
        tz.setId(optionId);

        doReturn(option).when(view).getNewOption();

        view.createOptionWithId(tz);

        verify(option).setValue(optionId);
        verify(option).setText(optionId);
    }

    @Test
    public void testCreateNoneOption() {

        final Option noneOption = mock(Option.class);
        final String text = "text";
        when(translationService.getValue(NONE_TRANSLATION_KEY)).thenReturn(text);
        doReturn(noneOption).when(view).getNewOption();

        view.createNoneOption();

        verify(noneOption).setValue(NONE_VALUE);
        verify(noneOption).setText(text);
    }

    @Test
    public void testPopulateTimeZoneSelectorWithOffSets() {

        final Option noneOption = mock(Option.class);
        final Option option0 = mock(Option.class);
        final Option option1 = mock(Option.class);

        final String os0 = "+01:00";
        final String os1 = "-03:00";
        final List<String> offsets = mock(List.class);
        when(offsets.size()).thenReturn(2);
        when(offsets.get(0)).thenReturn(os0);
        when(offsets.get(1)).thenReturn(os1);

        doReturn(noneOption).when(view).createNoneOption();
        doReturn(option0).when(view).createOptionWithOffset(os0);
        doReturn(option1).when(view).createOptionWithOffset(os1);

        when(timeZoneProvider.getTimeZonesOffsets()).thenReturn(offsets);

        view.populateTimeZoneSelectorWithOffSets();

        verify(timeZoneSelector).clear();
        verify(timeZoneSelector).add(noneOption);
        verify(timeZoneSelector).add(option0);
        verify(timeZoneSelector).add(option1);
        verify(timeZoneSelector).refresh();
    }

    @Test
    public void testCreateOptionWithOffset() {

        final String offset = "offset";
        final Option option = mock(Option.class);

        doReturn(option).when(view).getNewOption();

        view.createOptionWithOffset(offset);

        verify(option).setValue(offset);
        verify(option).setText(offset);
    }

    @Test
    public void testGetValue() {

        final String time = "10:20:00";
        final String selectedValue = "selected-value";
        final Option selectedItem = mock(Option.class);

        when(picker.getValue()).thenReturn(time);
        when(timeZoneSelector.getSelectedItem()).thenReturn(selectedItem);
        when(selectedItem.getValue()).thenReturn(selectedValue);

        when(formatter.buildRawValue(anyString(), anyString())).thenReturn("");

        view.getValue();

        verify(formatter).buildRawValue(time, selectedValue);
    }

    @Test
    public void testGetValueWithNoneTimeZone() {

        final String time = "10:20:00";
        final Option selectedItem = mock(Option.class);

        when(picker.getValue()).thenReturn(time);
        when(timeZoneSelector.getSelectedItem()).thenReturn(selectedItem);
        when(selectedItem.getValue()).thenReturn(NONE_VALUE);

        when(formatter.buildRawValue(anyString(), anyString())).thenReturn("");

        view.getValue();

        verify(formatter).buildRawValue(time, "");
    }

    @Test
    public void testGetValueWithNullTimeZone() {

        final String time = "10:20:00";

        when(picker.getValue()).thenReturn(time);
        when(timeZoneSelector.getSelectedItem()).thenReturn(null);

        when(formatter.buildRawValue(anyString(), anyString())).thenReturn("");

        view.getValue();

        verify(formatter).buildRawValue(time, "");
    }

    @Test
    public void testSetValueOffset() {

        final String value = "value";
        final TimeValue timeValue = mock(TimeValue.class);
        when(formatter.getTimeValue(value)).thenReturn(timeValue);
        when(timeValue.getTimeZoneMode()).thenReturn(OFFSET);
        doNothing().when(view).refreshTimeZoneOffsetMode(any());

        view.setValue(value);

        verify(view).setIsOffsetMode(true);
        verify(view).refreshTimeZoneOffsetMode(timeValue);
    }

    @Test
    public void testSetValueTimezone() {

        final String value = "value";
        final TimeValue timeValue = mock(TimeValue.class);
        when(formatter.getTimeValue(value)).thenReturn(timeValue);
        when(timeValue.getTimeZoneMode()).thenReturn(TIMEZONE);
        doNothing().when(view).refreshTimeZoneOffsetMode(any());

        view.setValue(value);

        verify(view).setIsOffsetMode(false);
        verify(view).refreshTimeZoneOffsetMode(timeValue);
    }

    @Test
    public void testSetValueNoTimezoneOrOffset() {

        final String value = "value";
        final TimeValue timeValue = mock(TimeValue.class);
        when(formatter.getTimeValue(value)).thenReturn(timeValue);
        when(timeValue.getTimeZoneMode()).thenReturn(NONE);
        doNothing().when(view).refreshTimeZoneOffsetMode(any());

        view.setValue(value);

        verify(view, never()).setIsOffsetMode(anyBoolean());
        verify(view, never()).refreshTimeZoneOffsetMode(timeValue);
        verify(timeZoneSelector).setValue("");
    }

    @Test
    public void testRefreshTimeZoneOffsetMode() {

        final String tzValue = "timezone-value";
        final TimeValue timeValue = mock(TimeValue.class);
        when(timeValue.getTimeZoneValue()).thenReturn(tzValue);
        doNothing().when(view).refreshToggleTimeZoneIcon();
        doNothing().when(view).reloadTimeZoneSelector();

        view.refreshTimeZoneOffsetMode(timeValue);

        verify(view).refreshToggleTimeZoneIcon();
        verify(view).reloadTimeZoneSelector();
        verify(timeZoneSelector).setValue(tzValue);
    }

    @Test
    public void testOnTimeInputBlur() {

        final BlurEvent event = mock(BlurEvent.class);
        final Element target = mock(Element.class);
        doReturn(target).when(view).getEventTarget(event);
        doReturn(false).when(view).isChildrenOfView(target);

        view.onTimeInputBlur(event);
        verify(onValueInputBlur).accept(event);
    }

    @Test
    public void testOnTimeInputBlurToChildrenElement() {

        final BlurEvent event = mock(BlurEvent.class);
        final Element target = mock(Element.class);
        doReturn(target).when(view).getEventTarget(event);
        doReturn(true).when(view).isChildrenOfView(target);

        view.onTimeInputBlur(event);
        verify(onValueInputBlur, never()).accept(event);
    }

    @Test
    public void testOnToggleTimeZoneButtonClickIsOffsetMode() {
        testOnToggleTimeZoneButtonClick(true);
    }

    @Test
    public void testOnToggleTimeZoneButtonClickIsNotOffsetMode() {
        testOnToggleTimeZoneButtonClick(false);
    }

    private void testOnToggleTimeZoneButtonClick(final boolean isOffsetMode) {

        doReturn(isOffsetMode).when(view).getIsOffsetMode();
        doNothing().when(view).refreshToggleTimeZoneIcon();
        doNothing().when(view).reloadTimeZoneSelector();

        view.onToggleTimeZoneButtonClick(null);

        verify(view).setIsOffsetMode(!isOffsetMode);
        verify(view).refreshToggleTimeZoneIcon();
        verify(view).reloadTimeZoneSelector();
    }

    @Test
    public void testReloadTimeZoneSelectorIsOffsetMode() {

        doReturn(true).when(view).getIsOffsetMode();

        view.reloadTimeZoneSelector();

        verify(view).populateTimeZoneSelectorWithOffSets();
        verify(view, never()).populateTimeZoneSelectorWithIds();
    }

    @Test
    public void testReloadTimeZoneSelectorIsNotOffsetMode() {

        doReturn(false).when(view).getIsOffsetMode();

        view.reloadTimeZoneSelector();

        verify(view, never()).populateTimeZoneSelectorWithOffSets();
        verify(view).populateTimeZoneSelectorWithIds();
    }

    @Test
    public void testRefreshToggleTimeZoneIconWhenIsOffsetMode() {

        doReturn(true).when(view).getIsOffsetMode();

        view.refreshToggleTimeZoneIcon();

        verify(toggleTimeZoneIconClassList).remove(TIMEZONE_CLASS_ICON);
        verify(toggleTimeZoneIconClassList).add(OFFSET_CLASS_ICON);
    }

    @Test
    public void testRefreshToggleTimeZoneIconWhenIsNotOffsetMode() {

        doReturn(false).when(view).getIsOffsetMode();

        view.refreshToggleTimeZoneIcon();

        verify(toggleTimeZoneIconClassList).add(TIMEZONE_CLASS_ICON);
        verify(toggleTimeZoneIconClassList).remove(OFFSET_CLASS_ICON);
    }
}