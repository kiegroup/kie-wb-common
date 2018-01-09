/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl;

import java.util.Date;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.ui.RootPanel;
import org.gwtbootstrap3.extras.datetimepicker.client.ui.DateTimePicker;
import org.gwtbootstrap3.extras.datetimepicker.client.ui.base.constants.DateTimePickerPosition;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.KEYDOWN;

public class DateTimePickerCell extends AbstractEditableCell<Date, Date> {

    private final DateTimeFormat format = DateTimeFormat.getFormat(DateEditableColumnGenerator.DEFAULT_DATE_AND_TIME_FORMAT_MASK);
    private final SafeHtmlRenderer<String> renderer = SimpleSafeHtmlRenderer.getInstance();

    private Element lastParent;
    private int lastIndex;
    private int lastColumn;
    private Date lastValue;
    private Object lastKey;
    private ValueUpdater<Date> valueUpdater;

    private boolean isEdit = false;
    private DateTimePicker dateTimePicker;

    public DateTimePickerCell() {
        super(CLICK,
              KEYDOWN);

        dateTimePicker = new DateTimePicker();

        dateTimePicker.setPlaceholder(DateEditableColumnGenerator.DEFAULT_DATE_AND_TIME_FORMAT_MASK);
        dateTimePicker.setGWTFormat(DateEditableColumnGenerator.DEFAULT_DATE_AND_TIME_FORMAT_MASK);
        dateTimePicker.setHighlightToday(true);
        dateTimePicker.setShowTodayButton(true);
        dateTimePicker.setPosition(DateTimePickerPosition.BOTTOM_LEFT);
        dateTimePicker.setWidth("1px");

        dateTimePicker.addValueChangeHandler(event -> {
            if(isEdit) {
                Element cellParent = lastParent;
                Date oldValue = lastValue;
                Object key = lastKey;
                int index = lastIndex;
                int column = lastColumn;

                // Update the cell and value updater.
                Date date = event.getValue();
                setViewData(key,
                            date);
                setValue(new Context(index,
                                     column,
                                     key),
                         cellParent,
                         oldValue);
                if (valueUpdater != null) {
                    valueUpdater.update(date);
                }

                dateTimePicker.hide();
            }
        });

        dateTimePicker.addHideHandler(hideEvent -> {
          hideEvent.stopPropagation();
          if(isEdit) {
              isEdit = false;
              RootPanel.get().remove(dateTimePicker);
          }
        });
    }

    protected void render() {
        dateTimePicker.setVisible(true);

        int left = lastParent.getAbsoluteLeft();
        int top = lastParent.getAbsoluteTop();
        dateTimePicker.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        dateTimePicker.getElement().getStyle().setLeft(left, Style.Unit.PX);
        dateTimePicker.getElement().getStyle().setTop(top, Style.Unit.PX);
    }

    @Override
    public boolean isEditing(Context context,
                             Element parent,
                             Date value) {
        return isEdit;
    }

    @Override
    public void onBrowserEvent(Context context, Element parent, Date value,
                               NativeEvent event, ValueUpdater<Date> valueUpdater) {
        super.onBrowserEvent(context, parent, value, event, valueUpdater);
        if (CLICK.equals(event.getType())) {
            RootPanel.get().add(dateTimePicker);

            this.isEdit = true;
            this.lastKey = context.getKey();
            this.lastParent = parent;
            this.lastValue = value;
            this.lastIndex = context.getIndex();
            this.lastColumn = context.getColumn();
            this.valueUpdater = valueUpdater;

            Date viewData = getViewData(lastKey);
            Date date = (viewData == null) ? lastValue : viewData;

            dateTimePicker.setValue(date);

            render();
            dateTimePicker.show();
        }
    }

    @Override
    public void render(Context context,
                       Date value,
                       SafeHtmlBuilder sb) {
        Object key = context.getKey();
        Date viewData = getViewData(key);
        if (viewData != null && viewData.equals(value)) {
            clearViewData(key);
            viewData = null;
        }

        String s = null;
        if (viewData != null) {
            s = format.format(viewData);
        } else if (value != null) {
            s = format.format(value);
        }
        if (s != null) {
            sb.append(renderer.render(s));
        }
    }
}
