/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells;

import java.util.Date;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import org.uberfire.ext.widgets.common.client.common.DatePicker;

import static org.kie.workbench.common.widgets.client.util.TimeZoneUtils.FORMATTER;

/**
 * A Popup Date Editor used in Guided Rule Template Editor if date (Date or LocalDate) needs to be shown on 'Data' tab
 */
public class PopupUniversalDateEditCell extends AbstractPopupEditCell<String, String> {

    private final DatePicker datePicker;

    public PopupUniversalDateEditCell(final boolean isReadOnly) {

        super(isReadOnly);

        this.datePicker = GWT.create(DatePicker.class);
        datePicker.setFormat(getPattern());

        // See https://issues.jboss.org/browse/GUVNOR-2322
        // The DatePicker was being closed, before the ValueChangeHandler invoked, in response to the
        // containing PopupPanel being automatically hidden when another Element received events.
        datePicker.setContainer(vPanel);
        panel.addAutoHidePartner(datePicker.getElement());

        // Hide the panel and call valueUpdater.update when a date is selected
        datePicker.addValueChangeHandler(event -> {
            internalCommit();
        });

        vPanel.add(datePicker);
    }

    @Override
    public void render(final Context context,
                       final String value,
                       final SafeHtmlBuilder sb) {
        if (value != null) {
            sb.append(getRenderer().render(value));
        }
    }

    // Commit the change
    @Override
    protected void commit() {
        internalCommit();
    }

    void internalCommit() {
        final Date date = getDatePicker().getValue();
        final String sDate = (date == null ? null : FORMATTER.format(date));
        setValue(lastContext,
                 lastParent,
                 sDate);

        if (getValueUpdater() != null) {
            getValueUpdater().update(sDate);
        }
        panel.hide();
    }

    // Start editing the cell
    @Override
    @SuppressWarnings("deprecation")
    protected void startEditing(final Context context,
                                final Element parent,
                                final String value) {

        Date date = value == null || value.isEmpty() ? null : FORMATTER.parse(value);
        // Default date
        if (date == null) {
            final Date d = new Date();
            final int year = d.getYear();
            final int month = d.getMonth();
            final int dom = d.getDate();
            date = new Date(year,
                            month,
                            dom);
        }
        getDatePicker().setValue(date);

        panel.setPopupPositionAndShow((offsetWidth, offsetHeight) ->
                                              panel.setPopupPosition(parent.getAbsoluteLeft() + offsetX,
                                                                     parent.getAbsoluteTop() + offsetY));
    }

    String getPattern() {
        return FORMATTER.getPattern();
    }

    DatePicker getDatePicker() {
        return datePicker;
    }

    ValueUpdater<String> getValueUpdater() {
        return valueUpdater;
    }

    SafeHtmlRenderer<String> getRenderer() {
        return renderer;
    }
}
