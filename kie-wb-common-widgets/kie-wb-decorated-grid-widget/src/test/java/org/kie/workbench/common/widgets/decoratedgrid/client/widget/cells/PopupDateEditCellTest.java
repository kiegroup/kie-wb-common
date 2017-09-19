/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.shared.event.HideEvent;
import org.gwtbootstrap3.client.shared.event.HideHandler;
import org.gwtbootstrap3.client.shared.event.ShowEvent;
import org.gwtbootstrap3.client.shared.event.ShowHandler;
import org.gwtbootstrap3.extras.datepicker.client.ui.base.constants.DatePickerPosition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.uberfire.ext.widgets.common.client.common.DatePicker;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class PopupDateEditCellTest {

    @GwtMock
    @SuppressWarnings("unused")
    private DatePicker datePicker;

    @Captor
    private ArgumentCaptor<ShowHandler> showHandlerCaptor;

    @Captor
    private ArgumentCaptor<HideHandler> hideHandlerCaptor;

    private PopupDateEditCell cell;

    private boolean autoHidePartnerAdded = false;

    private boolean autoHidePartnerRemoved = false;

    @Before
    public void setup() {
        cell = new PopupDateEditCell(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_LONG),
                                     false) {
            @Override
            void addDatePickerAsAutoHidePartner() {
                super.addDatePickerAsAutoHidePartner();
                autoHidePartnerAdded = true;
            }

            @Override
            void removeDatePickerAsAutoHidePartner() {
                super.removeDatePickerAsAutoHidePartner();
                autoHidePartnerRemoved = true;
            }
        };
    }

    @Test
    public void checkDatePickerPosition() {
        verify(datePicker).setPosition(eq(DatePickerPosition.AUTO));
    }

    @Test
    public void checkDatePickerShowHandler() {
        verify(datePicker).addShowHandler(showHandlerCaptor.capture());

        showHandlerCaptor.getValue().onShow(mock(ShowEvent.class));

        assertTrue(autoHidePartnerAdded);
    }

    @Test
    public void checkDatePickerHideHandler() {
        verify(datePicker).addHideHandler(hideHandlerCaptor.capture());

        hideHandlerCaptor.getValue().onHide(mock(HideEvent.class));

        assertTrue(autoHidePartnerRemoved);
    }
}
