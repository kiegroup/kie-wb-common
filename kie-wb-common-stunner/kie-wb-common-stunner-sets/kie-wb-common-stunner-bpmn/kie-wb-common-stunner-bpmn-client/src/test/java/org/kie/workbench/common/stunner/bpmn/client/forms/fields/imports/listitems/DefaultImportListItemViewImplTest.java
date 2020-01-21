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

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.gwtbootstrap3.client.ui.ValueListBox;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.editors.ImportsEditorView;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.editors.defaultimport.DefaultImportsEditor;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.CustomDataTypeTextBox;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.listitems.DefaultImportListItemViewImpl.CUSTOM_PROMPT;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.listitems.DefaultImportListItemViewImpl.ENTER_TYPE_PROMPT;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class DefaultImportListItemViewImplTest extends ImportListItemViewImplTest<DefaultImport> {

    private static final String DISPLAY_NAME = "Display Name";
    private static final String VALUE_NAME = "displayName";
    private static final String EMPTY_NAME = "";

    DefaultImportListItemViewImpl concreteTested;

    @Before
    public void setUp() {
        super.setUp();

        concreteTested = (DefaultImportListItemViewImpl) tested;
        concreteTested.importDataBinder = dataBinder;
        concreteTested.importItem = htmlTableRowElement;
        concreteTested.defaultClassNames = mock(ValueListBox.class);
        concreteTested.customClassName = mock(CustomDataTypeTextBox.class);
        concreteTested.classNamesComboBox = mock(ComboBox.class);
        concreteTested.deleteButton = button;

        concreteTested.presenter = mock(DefaultImportsEditor.class);
        when(concreteTested.presenter.getDataType(DISPLAY_NAME)).thenReturn(VALUE_NAME);

        Mockito.reset(concreteTested);
    }

    @Test
    public void init() {
        super.init();

        verify(concreteTested).initCustomDataTypeTextBox(concreteTested.customClassName);
    }

    @Test
    public void setPresenter() {
        super.setPresenter();

        verify(concreteTested).initListItem();
        DefaultImportsEditor presenter = mock(DefaultImportsEditor.class);
        concreteTested.setPresenter(presenter);
        assertEquals(presenter, concreteTested.presenter);
    }

    @Test
    public void setTextBoxModelValue() {
        concreteTested.setTextBoxModelValue(null, null);
        concreteTested.setTextBoxModelValue(null, EMPTY_NAME);
        concreteTested.setTextBoxModelValue(null, VALUE_NAME);
        verify(concreteTested).getValue();
        verify(concreteTested.getValue()).setClassName(VALUE_NAME);
    }

    @Test
    public void setListBoxModelValue() {
        concreteTested.setListBoxModelValue(null, DISPLAY_NAME);
        verify(concreteTested.presenter).getDataType(eq(DISPLAY_NAME));
        verify(concreteTested).getValue();
        verify(concreteTested.getValue()).setClassName(VALUE_NAME);
    }

    @Test
    public void getModelValue() {
        concreteTested.getModelValue(any(ValueListBox.class));
        verify(concreteTested).getValue();
        verify(concreteTested.getValue()).getClassName();
    }

    @Test
    public void notifyModelChanged() {
        concreteTested.notifyModelChanged();
        verify(concreteTested).notifyModelChanged();
        verifyNoMoreInteractions(tested);
    }

    @Test
    public void initListItem() {
        concreteTested.initListItem();
        verify(concreteTested).getListBoxValues();
        verify(concreteTested).getDefaultClassName();

        verify(concreteTested.classNamesComboBox).setShowCustomValues(true);
        verify(concreteTested.classNamesComboBox).setListBoxValues(any(ListBoxValues.class));

        verify(concreteTested.defaultClassNames).setValue(anyString());

        verify(concreteTested.classNamesComboBox).init(concreteTested,
                                                       true,
                                                       concreteTested.defaultClassNames,
                                                       concreteTested.customClassName,
                                                       false,
                                                       true,
                                                       CUSTOM_PROMPT,
                                                       ENTER_TYPE_PROMPT);
    }

    @Test
    public void getDefaultClassName() {
        String simpleName = Object.class.getSimpleName();

        when(concreteTested.getValue().getClassName()).thenReturn(null);
        concreteTested.getDefaultClassName();
        when(concreteTested.getValue().getClassName()).thenReturn(EMPTY_NAME);
        concreteTested.getDefaultClassName();
        when(concreteTested.getValue().getClassName()).thenReturn(VALUE_NAME);
        concreteTested.getDefaultClassName();

        verify(concreteTested.presenter, times(2)).getDataType(simpleName);
        verify(concreteTested.presenter).getDataType(VALUE_NAME);
    }

    @Test
    public void getListBoxValues() {
        Map<String, String> dataTypes = new TreeMap<>();
        dataTypes.put(VALUE_NAME + 1, DISPLAY_NAME + 1);
        dataTypes.put(VALUE_NAME + 2, DISPLAY_NAME + 2);
        dataTypes.put(VALUE_NAME + 3, DISPLAY_NAME + 3);
        when(concreteTested.presenter.getDataTypes()).thenReturn(dataTypes);

        ListBoxValues listBoxValues = concreteTested.getListBoxValues();
        List<String> acceptedValues = listBoxValues.getAcceptableValuesWithCustomValues();

        assertEquals(CUSTOM_PROMPT, acceptedValues.get(0));
        assertEquals(DISPLAY_NAME + 1, acceptedValues.get(1));
        assertEquals(DISPLAY_NAME + 2, acceptedValues.get(2));
        assertEquals(DISPLAY_NAME + 3, acceptedValues.get(3));
    }

    @Override
    ImportListItemViewImpl<DefaultImport> spyListItem() {
        return spy(DefaultImportListItemViewImpl.class);
    }

    @Override
    DefaultImport mockModelValue() {
        return mock(DefaultImport.class);
    }

    @Override
    ImportsEditorView.Presenter<DefaultImport> mockPresenter() {
        return mock(DefaultImportsEditor.class);
    }
}