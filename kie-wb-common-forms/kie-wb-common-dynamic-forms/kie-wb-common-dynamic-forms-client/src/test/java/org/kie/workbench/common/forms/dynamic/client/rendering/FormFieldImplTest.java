/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.client.rendering;

import java.util.Collection;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition.SubFormFieldDefinition;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeListener;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class FormFieldImplTest {

    @Mock
    private FormGroup formGroup;

    @Mock
    private SubFormFieldDefinition subFormFieldDefinition;

    @Mock
    private IsWidget isWidget;

    @GwtMock
    private Widget widget;

    private FormFieldImpl tested = null;

    @Before
    public void init() {
        when(formGroup.getBindableWidget()).thenReturn(isWidget);
        when(isWidget.asWidget()).thenReturn(widget);

        tested = new FormFieldImpl(subFormFieldDefinition, formGroup) {
            @Override
            protected void doSetReadOnly(boolean readOnly) {
            }

            @Override
            public Collection<FieldChangeListener> getChangeListeners() {
                return null;
            }
        };
    }

    @Test
    public void isActive() {
        tested.setActive(true);
        assertTrue(tested.isActive());

        tested.setActive(false);
        assertFalse(tested.isActive());
    }

    @Test
    public void setActive() {
        ArgumentCaptor<FocusHandler> focusCaptor = ArgumentCaptor.forClass(FocusHandler.class);
        verify(widget, times(1)).addDomHandler(focusCaptor.capture(), eq(FocusEvent.getType()));
        FocusHandler focusHandler = focusCaptor.getValue();
        focusHandler.onFocus(null);
        assertTrue(tested.isActive());

        ArgumentCaptor<BlurHandler> blurCaptor = ArgumentCaptor.forClass(BlurHandler.class);
        verify(widget, times(1)).addDomHandler(blurCaptor.capture(), eq(BlurEvent.getType()));
        BlurHandler blurHandler = blurCaptor.getValue();
        blurHandler.onBlur(null);
        assertFalse(tested.isActive());
    }

    @Test
    public void testInitActiveStatus() {
        verify(widget, times(1)).addDomHandler(any(FocusHandler.class), eq(FocusEvent.getType()));
        verify(widget, times(1)).addDomHandler(any(BlurHandler.class), eq(BlurEvent.getType()));
    }
}