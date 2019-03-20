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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time;

class DayTimeValue {

    private static final int NONE = 0;

    private int days;

    private int hours;

    private int minutes;

    private int seconds;

    DayTimeValue() {
        this(NONE, NONE, NONE, NONE);
    }

    DayTimeValue(final int days,
                 final int hours,
                 final int minutes,
                 final int seconds) {
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    Integer getDays() {
        return days;
    }

    Integer getHours() {
        return hours;
    }

    Integer getMinutes() {
        return minutes;
    }

    Integer getSeconds() {
        return seconds;
    }

    void setDays(final int days) {
        this.days = days;
    }

    void setHours(final int hours) {
        this.hours = hours;
    }

    void setMinutes(final int minutes) {
        this.minutes = minutes;
    }

    void setSeconds(final int seconds) {
        this.seconds = seconds;
    }
}
