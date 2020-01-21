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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.listitems;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLTableRowElement;
import org.gwtbootstrap3.client.ui.Button;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.editors.ImportsEditorView;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.CustomDataTypeTextBox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants.CONSTANTS;
import static org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils.JAVA_IDENTIFIER_REGEXP;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public abstract class ImportListItemViewImplTest<T> {

    ImportListItemViewImpl<T> tested;
    DataBinder<T> dataBinder;
    HTMLTableRowElement htmlTableRowElement;
    Button button;

    @Before
    public void setUp() {
        GwtMockito.initMocks(this);

        dataBinder = mock(DataBinder.class);
        when(dataBinder.getModel()).thenReturn(mockModelValue());

        htmlTableRowElement = mock(HTMLTableRowElement.class);
        button = mock(Button.class);

        tested = spyListItem();
    }

    @Test
    public void init() {
        tested.init();
        verify(tested).getImportItem();
        assertFalse(tested.getImportItem().hidden);
    }

    @Test
    public void getImportDataBinder() {
        DataBinder<T> result = tested.getImportDataBinder();
        assertEquals(dataBinder, result);
    }

    @Test
    public void getImportItem() {
        HTMLTableRowElement result = tested.getImportItem();
        assertEquals(htmlTableRowElement, result);
    }

    @Test
    public void getDeleteButton() {
        Button result = tested.getDeleteButton();
        assertEquals(button, result);
    }

    @Test
    public void getValue() {
        tested.getValue();
        verify(tested).getImportDataBinder();
        verify(tested.getImportDataBinder()).getModel();
    }

    @Test
    public void setValue() {
        T value = mockModelValue();
        tested.setValue(value);
        verify(tested).getImportDataBinder();
        verify(tested.getImportDataBinder()).setModel(value);
    }

    @Test
    public void setPresenter() {
        tested.setPresenter(null);
        tested.setPresenter(mockPresenter());
        verify(tested).getDeleteButton();
        verify(tested.getDeleteButton()).addClickHandler(any(ClickHandler.class));
    }

    @Test
    public void initCustomDataTypeTextBox() {
        CustomDataTypeTextBox customDataTypeTextBox = mock(CustomDataTypeTextBox.class);
        tested.initCustomDataTypeTextBox(customDataTypeTextBox);
        verify(customDataTypeTextBox).setRegExp(JAVA_IDENTIFIER_REGEXP,
                                                CONSTANTS.Removed_invalid_characters_from_name(),
                                                CONSTANTS.Invalid_character_in_name());
    }

    abstract ImportListItemViewImpl<T> spyListItem();

    abstract T mockModelValue();

    abstract ImportsEditorView.Presenter<T> mockPresenter();
}