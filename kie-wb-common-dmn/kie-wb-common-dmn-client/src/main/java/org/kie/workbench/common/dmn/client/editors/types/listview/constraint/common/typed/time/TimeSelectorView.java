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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.editors.types.DMNSimpleTimeZone;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePicker;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeValue;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeValueFormatter;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeZoneProvider;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;

@Templated
@Dependent
public class TimeSelectorView implements TimeSelector.View {

    @DataField("toggle-timezone-button")
    private final HTMLButtonElement toggleTimeZoneButton;

    @DataField("toggle-timezone-icon")
    private final HTMLElement toggleTimeZoneIcon;

    @DataField("time-input")
    private HTMLInputElement timeInput;

    @DataField
    private Select timeZoneSelector;

    static final String NONE_TRANSLATION_KEY = "TimeSelectorView.None";
    static final String SELECT_TIMEZONE_TRANSLATION_KEY = "TimeSelectorView.SelectTimeZone";
    static final String SELECT_UTC_OFFSET_TRANSLATION_KEY = "TimeSelectorView.SelectUTCOffset";
    static final String TIMEZONE_CLASS_ICON = "fa-globe";
    static final String OFFSET_CLASS_ICON = "fa-clock-o";
    static final String NONE_VALUE = "None";

    private final TimeZoneProvider timeZoneProvider;
    private final ClientTranslationService translationService;
    private final List<DMNSimpleTimeZone> timeZones;
    private final TimePicker picker;
    private final TimeValueFormatter formatter;
    private TimeSelectorView presenter;
    private Consumer<BlurEvent> onValueInputBlur;
    private boolean isOffsetMode;

    @Inject
    public TimeSelectorView(final HTMLInputElement timeInput,
                            final TimePicker picker,
                            final TimeZoneProvider timeZoneProvider,
                            final TimeValueFormatter formatter,
                            final @Named("i") HTMLElement toggleTimeZoneIcon,
                            final HTMLButtonElement toggleTimeZoneButton,
                            final ClientTranslationService translationService) {
        this.timeInput = timeInput;
        this.picker = picker;
        this.timeZoneProvider = timeZoneProvider;
        this.formatter = formatter;
        this.toggleTimeZoneIcon = toggleTimeZoneIcon;
        this.toggleTimeZoneButton = toggleTimeZoneButton;
        this.timeZones = new ArrayList<>();
        this.translationService = translationService;

        this.isOffsetMode = false;
        this.timeZoneSelector = GWT.create(Select.class);
        this.timeZoneSelector.setShowTick(true);
        this.timeZoneSelector.setLiveSearch(true);
        this.timeZoneSelector.getElement().setAttribute("data-container", "body");
        this.timeZoneSelector.refresh();
    }

    @PostConstruct
    void init() {
        picker.bind(timeInput);
        timeZoneProvider.getTimeZones(this::timeZoneProviderSuccessCallBack);
    }

    private void timeZoneProviderSuccessCallBack(final List<DMNSimpleTimeZone> timeZones) {

        this.timeZones.clear();
        this.timeZones.addAll(timeZones);
        populateTimeZoneSelectorWithIds();
    }

    boolean getIsOffsetMode() {
        return isOffsetMode;
    }

    List<DMNSimpleTimeZone> getTimeZones() {
        return timeZones;
    }

    Select getTimeZoneSelector() {
        return timeZoneSelector;
    }

    void populateTimeZoneSelectorWithIds() {

        final Select selector = getTimeZoneSelector();
        selector.clear();
        selector.add(createNoneOption());

        for (int i = 0; i < getTimeZones().size(); i++) {
            final DMNSimpleTimeZone timeZone = getTimeZones().get(i);
            final Option option = createOptionWithId(timeZone);
            selector.add(option);
        }

        final String title = translationService.getValue(SELECT_TIMEZONE_TRANSLATION_KEY);
        selector.setTitle(title);
        selector.setLiveSearchPlaceholder(title);
        selector.refresh();
    }

    Option createOptionWithId(final DMNSimpleTimeZone timeZone) {

        final String timeZoneId = timeZone.getId();
        final Option option = getNewOption();
        option.setValue(timeZoneId);
        option.setText(timeZoneId);
        return option;
    }

    Option getNewOption() {
        return GWT.create(Option.class);
    }

    Option createNoneOption() {

        final Option none = getNewOption();
        none.setText(translationService.getValue(NONE_TRANSLATION_KEY));
        none.setValue(NONE_VALUE);
        return none;
    }

    void populateTimeZoneSelectorWithOffSets() {

        final List<String> offSets = timeZoneProvider.getTimeZonesOffsets();
        final Select selector = getTimeZoneSelector();
        selector.clear();
        selector.add(createNoneOption());

        for (int i = 0; i < offSets.size(); i++) {

            final Option option = createOptionWithOffset(offSets.get(i));
            selector.add(option);
        }

        selector.setTitle(translationService.getValue(SELECT_UTC_OFFSET_TRANSLATION_KEY));
        selector.setLiveSearchPlaceholder(translationService.getValue(SELECT_UTC_OFFSET_TRANSLATION_KEY));
        selector.refresh();
    }

    Option createOptionWithOffset(final String timeZoneOffSet) {

        final Option option = getNewOption();
        option.setValue(timeZoneOffSet);
        option.setText(timeZoneOffSet);
        return option;
    }

    @Override
    public String getValue() {

        final String time = picker.getValue();
        final Option selectedItem = getTimeZoneSelector().getSelectedItem();

        final String timeZoneValue;
        if (!Objects.isNull(selectedItem)) {
            final String selectedValue = selectedItem.getValue();
            timeZoneValue = NONE_VALUE.equals(selectedValue) ? "" : selectedValue;
        } else {
            timeZoneValue = "";
        }

        return formatter.buildRawValue(time, timeZoneValue);
    }

    @Override
    public void setValue(final String value) {

        final TimeValue timeValue = formatter.getTimeValue(value);
        picker.setValue(timeValue.getTime());
        switch (timeValue.getTimeZoneMode()) {

            case OFFSET:
                setIsOffsetMode(true);
                refreshTimeZoneOffsetMode(timeValue);
                break;

            case TIMEZONE:
                setIsOffsetMode(false);
                refreshTimeZoneOffsetMode(timeValue);
                break;

            case NONE:
                getTimeZoneSelector().setValue("");
                break;
        }
    }

    void setIsOffsetMode(final boolean isOffsetMode) {
        this.isOffsetMode = isOffsetMode;
    }

    void refreshTimeZoneOffsetMode(final TimeValue timeValue) {
        refreshToggleTimeZoneIcon();
        reloadTimeZoneSelector();
        getTimeZoneSelector().setValue(timeValue.getTimeZoneValue());
    }

    @Override
    public void setPlaceholder(final String placeholder) {
        timeInput.setAttribute("placeholder", placeholder);
    }

    @Override
    public void setOnInputChangeCallback(final Consumer<Event> onValueChanged) {
        timeInput.onchange = (Event event) -> {
            onValueChanged.accept(event);
            return this;
        };

        picker.setOnDateChanged(v -> onValueChanged.accept(null));
    }

    @Override
    public void select() {
        timeInput.select();
    }

    @Override
    public void setOnInputBlurCallback(final Consumer<BlurEvent> onValueInputBlur) {
        this.onValueInputBlur = onValueInputBlur;
    }

    Consumer<BlurEvent> getOnValueInputBlur() {
        return this.onValueInputBlur;
    }

    Object getEventTarget(final BlurEvent blurEvent) {
        return blurEvent.getNativeEvent().getRelatedEventTarget();
    }

    @EventHandler("time-input")
    public void onTimeInputBlur(final BlurEvent blurEvent) {

        final Object target = getEventTarget(blurEvent);
        if (!Objects.isNull(getOnValueInputBlur())
                && !Objects.isNull(target)
                && !isChildrenOfView((Element) target)) {
            getOnValueInputBlur().accept(blurEvent);
        }
    }

    @EventHandler("toggle-timezone-button")
    public void onToggleTimeZoneButtonClick(final ClickEvent clickEvent) {

        setIsOffsetMode(!getIsOffsetMode());
        refreshToggleTimeZoneIcon();
        reloadTimeZoneSelector();
    }

    void reloadTimeZoneSelector() {

        if (getIsOffsetMode()) {
            populateTimeZoneSelectorWithOffSets();
        } else {
            populateTimeZoneSelectorWithIds();
        }
    }

    void refreshToggleTimeZoneIcon() {

        if (getIsOffsetMode()) {
            toggleTimeZoneIcon.classList.remove(TIMEZONE_CLASS_ICON);
            toggleTimeZoneIcon.classList.add(OFFSET_CLASS_ICON);
        } else {
            toggleTimeZoneIcon.classList.add(TIMEZONE_CLASS_ICON);
            toggleTimeZoneIcon.classList.remove(OFFSET_CLASS_ICON);
        }
    }

    boolean isChildrenOfView(final Element element) {

        final Element viewElement = getElement();
        return viewElement.contains(element);
    }

    @Override
    public HTMLInputElement getInput() {
        return this.timeInput;
    }

    @Override
    public void init(final TimeSelectorView presenter) {
        this.presenter = presenter;
    }
}
