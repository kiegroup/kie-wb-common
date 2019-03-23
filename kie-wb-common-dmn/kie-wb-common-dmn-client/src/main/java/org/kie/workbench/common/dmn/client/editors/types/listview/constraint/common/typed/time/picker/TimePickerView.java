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

import java.util.Objects;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.core.Date;
import elemental2.dom.Event;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
@Dependent
public class TimePickerView implements TimePicker.View {

    @DataField("time-picker-container")
    private final HTMLDivElement timePickerContainer;

    @DataField("increase-hours")
    private final HTMLAnchorElement increaseHours;

    @DataField("decrease-hours")
    private final HTMLAnchorElement decreaseHours;

    @DataField("increase-minutes")
    private final HTMLAnchorElement increaseMinutes;

    @DataField("decrease-minutes")
    private final HTMLAnchorElement decreaseMinutes;

    @DataField("increase-seconds")
    private final HTMLAnchorElement increaseSeconds;

    @DataField("decrease-seconds")
    private final HTMLAnchorElement decreaseSeconds;

    @DataField("hours")
    private final HTMLElement hours;

    @DataField("minutes")
    private final HTMLElement minutes;

    @DataField("seconds")
    private final HTMLElement seconds;

    private TimePickerView presenter;

    private Date date;

    private Consumer<Long> onDateChanged;
    private Consumer<Event> onBlur;

    @Inject
    public TimePickerView(final HTMLDivElement timePickerContainer,
                          final HTMLAnchorElement increaseHours,
                          final HTMLAnchorElement decreaseHours,
                          final HTMLAnchorElement increaseMinutes,
                          final HTMLAnchorElement decreaseMinutes,
                          final HTMLAnchorElement increaseSeconds,
                          final HTMLAnchorElement decreaseSeconds,
                          final @Named("span") HTMLElement hours,
                          final @Named("span") HTMLElement minutes,
                          final @Named("span") HTMLElement seconds) {
        this.timePickerContainer = timePickerContainer;
        this.increaseHours = increaseHours;
        this.decreaseHours = decreaseHours;
        this.increaseMinutes = increaseMinutes;
        this.decreaseMinutes = decreaseMinutes;
        this.increaseSeconds = increaseSeconds;
        this.decreaseSeconds = decreaseSeconds;
        this.minutes = minutes;
        this.hours = hours;
        this.seconds = seconds;
    }

    @Override
    public void init(final TimePickerView presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setDate(final long timeInMillis) {

        this.date = new Date();
        this.date.setTime(timeInMillis);
        refresh();
    }

    void refresh() {

        setHours(getDate().getHours());
        setMinutes(getDate().getMinutes());
        setSeconds(getDate().getSeconds());

        if (!Objects.isNull(onDateChanged)) {
            onDateChanged.accept((long) getDate().getTime());
        }
    }

    void setHours(final double hours) {
        this.hours.textContent = format(hours);
    }

    void setMinutes(final double minutes) {
        this.minutes.textContent = format(minutes);
    }

    void setSeconds(final double seconds) {
        this.seconds.textContent = format(seconds);
    }

    String format(final double value) {

        String str = String.valueOf((int)value);
        str = str.length() < 2 ? "0" + str : str;
        return str;
    }

    @EventHandler("increase-hours")
    public void onIncreaseHoursClick(final ClickEvent event) {
        getDate().setHours(getDate().getHours() + 1);
        refresh();
    }

    @EventHandler("decrease-hours")
    public void onDecreaseHoursClick(final ClickEvent event) {
        getDate().setHours(getDate().getHours() - 1);
        refresh();
    }

    @EventHandler("increase-minutes")
    public void onIncreaseMinutesClick(final ClickEvent event) {
        final double currentHours = getDate().getHours();
        getDate().setMinutes(getDate().getMinutes() + 1);
        getDate().setHours(currentHours);
        refresh();
    }

    @EventHandler("decrease-minutes")
    public void onDecreaseMinutesClick(final ClickEvent event) {
        final double currentHours = getDate().getHours();
        getDate().setMinutes(getDate().getMinutes() - 1);
        getDate().setHours(currentHours);
        refresh();
    }

    @EventHandler("increase-seconds")
    public void onIncreaseSecondsClick(final ClickEvent event) {
        setSeconds((int) getDate().getSeconds() + 1);
    }

    @EventHandler("decrease-seconds")
    public void onDecreaseSecondsClick(final ClickEvent event) {
        setSeconds((int) getDate().getSeconds() - 1);
    }

    void setSeconds(final int seconds) {

        // This is to prevent that when we increase seconds beyond 59 seconds,
        // the minutes are also increased and also the hours if minutes == 59.

        final double currentHours = getDate().getHours();
        final double currentMinutes = getDate().getMinutes();
        getDate().setSeconds(seconds);
        getDate().setMinutes(currentMinutes);
        getDate().setHours(currentHours);
        refresh();
    }

    public void setOnDateChanged(final Consumer<Long> onDateChanged) {
        this.onDateChanged = onDateChanged;
    }

    @Override
    public elemental2.core.Date getDate() {
        return date;
    }

    @Override
    public void setOnBlur(final Consumer<Event> onBlur) {
        this.onBlur = onBlur;
        getElement().onblur = this::onBlur;
        increaseHours.onblur = this::onBlur;
        decreaseHours.onblur = this::onBlur;
        increaseMinutes.onblur = this::onBlur;
        decreaseMinutes.onblur = this::onBlur;
        increaseSeconds.onblur = this::onBlur;
        decreaseSeconds.onblur = this::onBlur;
    }

    private Object onBlur(final Event event) {
        if (!Objects.isNull(this.onBlur)) {
            onBlur.accept(event);
        }
        return this;
    }
}
