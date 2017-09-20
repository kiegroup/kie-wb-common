/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import org.gwtbootstrap3.client.shared.event.HideEvent;
import org.gwtbootstrap3.client.shared.event.HideHandler;
import org.gwtbootstrap3.client.shared.event.ShowEvent;
import org.gwtbootstrap3.client.shared.event.ShowHandler;
import org.gwtbootstrap3.extras.datepicker.client.ui.base.constants.DatePickerPosition;
import org.uberfire.ext.widgets.common.client.common.DatePicker;

/**
 * A Popup Date Editor.
 */
public class PopupDateEditCell extends AbstractPopupEditCell<Date, Date> {

    private final DatePicker datePicker;
    private final DateTimeFormat format;

    public PopupDateEditCell(DateTimeFormat format,
                             boolean isReadOnly) {
        super(isReadOnly);
        if (format == null) {
            throw new IllegalArgumentException("format == null");
        }

        this.format = format;
        this.datePicker = GWT.create(DatePicker.class);
        datePicker.setFormat(format.getPattern());
        datePicker.setPosition(DatePickerPosition.AUTO);

        // See https://issues.jboss.org/browse/GUVNOR-2322
        // Register the DatePicker DOM components as "auto hide partners"
        // to ensure click events on them do not close the PopupPanel.
        panel.addAutoHidePartner(datePicker.getElement());
        datePicker.addShowHandler(new ShowHandler() {
            @Override
            public void onShow(final ShowEvent showEvent) {
                addDatePickerAsAutoHidePartner();
            }
        });
        datePicker.addHideHandler(new HideHandler() {
            @Override
            public void onHide(final HideEvent hideEvent) {
                removeDatePickerAsAutoHidePartner();
            }
        });

        // Hide the panel and call valueUpdater.update when a date is selected
        datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
            @Override
            public void onValueChange(final ValueChangeEvent<Date> event) {
                commit();
            }
        });

        vPanel.add(datePicker);
    }

    void addDatePickerAsAutoHidePartner() {
        panel.addAutoHidePartner(panel.getElement().getNextSiblingElement());
    }

    void removeDatePickerAsAutoHidePartner() {
        panel.removeAutoHidePartner(panel.getElement().getNextSiblingElement());
    }

    @Override
    public void render(Context context,
                       Date value,
                       SafeHtmlBuilder sb) {
        if (value != null) {
            sb.append(renderer.render(format.format(value)));
        }
    }

    // Commit the change
    @Override
    protected void commit() {

        // Update value
        Date date = datePicker.getValue();
        setValue(lastContext,
                 lastParent,
                 date);
        if (valueUpdater != null) {
            valueUpdater.update(date);
        }
        panel.hide();
    }

    // Start editing the cell
    @Override
    @SuppressWarnings("deprecation")
    protected void startEditing(final Context context,
                                final Element parent,
                                final Date value) {

        // Default date
        Date date = value;
        if (value == null) {
            Date d = new Date();
            int year = d.getYear();
            int month = d.getMonth();
            int dom = d.getDate();
            date = new Date(year,
                            month,
                            dom);
        }
        datePicker.setValue(date);

        panel.setPopupPositionAndShow(new PositionCallback() {
            public void setPosition(int offsetWidth,
                                    int offsetHeight) {
                panel.setPopupPosition(parent.getAbsoluteLeft()
                                               + offsetX,
                                       parent.getAbsoluteTop()
                                               + offsetY);
            }
        });
    }
}
