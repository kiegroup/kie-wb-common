/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assignmentsEditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssignmentRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.VariableNameTextBox;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests the data get/set behaviour of AssignmentListItemWidget
 */
@RunWith(MockitoJUnitRunner.class)
public class AssignmentListItemWidgetTest {

    ValueListBox<String> dataType;

    ValueListBox<String> processVar;

    TextBox customDataType;

    TextBox constant;

    ComboBox dataTypeComboBox;

    ComboBox processVarComboBox;

    @GwtMock
    VariableNameTextBox name;

    @GwtMock
    Button deleteButton;

    @GwtMock
    DataBinder<AssignmentRow> assignment;

    @Captor
    ArgumentCaptor<String> regExpCaptor;

    //@Spy  // - cannot make Spy because of GWT error
    //@InjectMocks // - cannot InjectMocks because of GWT error
    private AssignmentListItemWidgetViewImpl widget;

    @Before
    public void initTestCase() {
        GwtMockito.initMocks(this);
        dataType = mock(ValueListBox.class);
        processVar = mock(ValueListBox.class);
        customDataType = mock(TextBox.class);
        constant = mock(TextBox.class);
        dataTypeComboBox = mock(ComboBox.class);
        processVarComboBox = mock(ComboBox.class);
        widget = GWT.create(AssignmentListItemWidgetViewImpl.class);
        AssignmentRow assignmentRow = new AssignmentRow();
        widget.dataType = dataType;
        widget.customDataType = customDataType;
        widget.processVar = processVar;
        widget.constant = constant;
        widget.dataTypeComboBox = dataTypeComboBox;
        widget.name = name;
        widget.deleteButton = deleteButton;
        widget.processVarComboBox = processVarComboBox;
        widget.assignment = assignment;
        Mockito.doCallRealMethod().when(widget).setTextBoxModelValue(any(TextBox.class),
                                                                     anyString());
        Mockito.doCallRealMethod().when(widget).setListBoxModelValue(any(ValueListBox.class),
                                                                     anyString());
        Mockito.doCallRealMethod().when(widget).getModelValue(any(ValueListBox.class));
        Mockito.doCallRealMethod().when(widget).setDataType(anyString());
        Mockito.doCallRealMethod().when(widget).getDataType();
        Mockito.doCallRealMethod().when(widget).setCustomDataType(anyString());
        Mockito.doCallRealMethod().when(widget).getCustomDataType();
        Mockito.doCallRealMethod().when(widget).setConstant(anyString());
        Mockito.doCallRealMethod().when(widget).getConstant();
        Mockito.doCallRealMethod().when(widget).setProcessVar(anyString());
        Mockito.doCallRealMethod().when(widget).getProcessVar();
        Mockito.doCallRealMethod().when(widget).setDataTypes(any(ListBoxValues.class));
        Mockito.doCallRealMethod().when(widget).setProcessVariables(any(ListBoxValues.class));
        Mockito.doCallRealMethod().when(widget).init();
        Mockito.doCallRealMethod().when(widget).setModel(any(AssignmentRow.class));
        Mockito.doCallRealMethod().when(processVarComboBox).setAddCustomValues(any(Boolean.class));
        Mockito.doCallRealMethod().when(processVarComboBox).addCustomValueToListBoxValues(any(String.class),
                                                                                          any(String.class));
        Mockito.doCallRealMethod().when(processVarComboBox).setListBoxValues(any(ListBoxValues.class));
        when(widget.getModel()).thenReturn(assignmentRow);
    }

    @Test
    public void testInitWidget() {
        widget.init();
        verify(widget,
               times(1)).init();
        verify(dataTypeComboBox,
               times(1)).init(widget,
                              false,
                              dataType,
                              customDataType,
                              false,
                              true,
                              AssignmentListItemWidgetView.CUSTOM_PROMPT,
                              AssignmentListItemWidgetView.ENTER_TYPE_PROMPT);
        verify(processVarComboBox,
               times(1)).init(widget,
                              false,
                              processVar,
                              constant,
                              true,
                              true,
                              AssignmentListItemWidgetView.CONSTANT_PROMPT,
                              AssignmentListItemWidgetView.ENTER_CONSTANT_PROMPT);
        verify(name,
               times(1)).setRegExp(regExpCaptor.capture(),
                                   anyString(),
                                   anyString());
        RegExp regExp = RegExp.compile(regExpCaptor.getValue());
        assertEquals(true,
                     regExp.test("a1 -_+-*?'/"));
        assertEquals(false,
                     regExp.test("a1 -_+-*?'/1@"));
        assertEquals(true,
                     regExp.test("a1"));
        assertEquals(true,
                     regExp.test("multiple words name"));
        assertEquals(true,
                     regExp.test("multiple-words-name"));
        assertEquals(true,
                     regExp.test("UpperCase"));
        assertEquals(true,
                     regExp.test("_car"));
        verify(customDataType,
               times(1)).addKeyDownHandler(any(KeyDownHandler.class));
        verify(name,
               times(1)).addChangeHandler(any(ChangeHandler.class));
    }

    @Test
    public void testSetTextBoxModelValue() {
        widget.setTextBoxModelValue(customDataType,
                                    "abc");
        verify(widget,
               times(1)).setCustomDataType("abc");
        widget.setTextBoxModelValue(constant,
                                    "abc");
        verify(widget,
               times(1)).setConstant("abc");
    }

    @Test
    public void testSetListBoxModelValue() {
        widget.setListBoxModelValue(dataType,
                                    "abc");
        verify(widget,
               times(1)).setDataType("abc");
        widget.setListBoxModelValue(processVar,
                                    "abc");
        verify(widget,
               times(1)).setProcessVar("abc");
    }

    @Test
    public void testSetModelInput() {
        when(widget.getVariableType()).thenReturn(Variable.VariableType.INPUT);
        widget.setModel(new AssignmentRow());
        verify(deleteButton).setIcon(IconType.TRASH);
        verify(constant,
               never()).setVisible(anyBoolean());
        verify(widget).getCustomDataType();
        verify(widget).getDataType();
        verify(widget).getConstant();
        verify(widget).getProcessVar();
    }

    @Test
    public void testSetModelOutput() {
        when(widget.getVariableType()).thenReturn(Variable.VariableType.OUTPUT);
        widget.setModel(new AssignmentRow());
        verify(deleteButton).setIcon(IconType.TRASH);
        verify(constant).setVisible(false);
        verify(widget).getCustomDataType();
        verify(widget).getDataType();
        verify(widget).getConstant();
        verify(widget).getProcessVar();
    }

    @Test
    public void testQuotedConstant1() {
        AssignmentRow row = new AssignmentRow();
        String s = "abc";
        row.setConstant(s);
        when(widget.getModel()).thenReturn(row);
        widget.setModel(row);
        verify(constant).setValue(s);
    }

    @Test
    public void testQuotedConstant2() {
        AssignmentRow row = new AssignmentRow();
        String s = "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout.";
        row.setConstant(s);
        when(widget.getModel()).thenReturn(row);
        widget.setModel(row);
        verify(constant).setValue(s);
    }

    @Test
    public void testSetGetCustomDataType() {
        String customDataType = "com.test.MyType";
        widget.setTextBoxModelValue(widget.customDataType,
                                    customDataType);
        String returnedCustomDataType1 = widget.getCustomDataType();
        assertEquals(customDataType,
                     returnedCustomDataType1);
        String returnedCustomDataType2 = widget.getModelValue(widget.dataType);
        assertEquals(customDataType,
                     returnedCustomDataType2);
    }

    @Test
    public void testSetGetDataType() {
        String sDataType = "Boolean";
        widget.setListBoxModelValue(widget.dataType,
                                    sDataType);
        String returnedDataType1 = widget.getDataType();
        assertEquals(sDataType,
                     returnedDataType1);
        String returnedDataType2 = widget.getModelValue(widget.dataType);
        assertEquals(sDataType,
                     returnedDataType2);
    }

    @Test
    public void testSetGetConstant() {
        String constant = "any constant";
        widget.setTextBoxModelValue(widget.constant,
                                    constant);
        String returnedConstant = widget.getConstant();
        assertEquals(constant,
                     returnedConstant);
        String returnedConstant2 = widget.getModelValue(widget.processVar);
        assertEquals(constant,
                     returnedConstant2);
    }

    @Test
    public void testSetGetProcessVar() {
        String sProcessVar = "username";
        widget.setListBoxModelValue(widget.processVar,
                                    sProcessVar);
        String returnedProcessVar1 = widget.getProcessVar();
        assertEquals(sProcessVar,
                     returnedProcessVar1);
        String returnedProcessVar2 = widget.getModelValue(widget.processVar);
        assertEquals(sProcessVar,
                     returnedProcessVar2);
    }

    @Test
    public void testSetDataTypes() {
        ListBoxValues dataTypeListBoxValues = new ListBoxValues(null,
                                                                null,
                                                                null);
        String sCustomType = "com.test.CustomType";
        widget.setCustomDataType(sCustomType);
        widget.setDataTypes(dataTypeListBoxValues);
        verify(dataTypeComboBox).setListBoxValues(dataTypeListBoxValues);
        verify(dataTypeComboBox).addCustomValueToListBoxValues(sCustomType,
                                                               "");
    }

    @Test
    public void testSetProcessVariablesVar() {
        ListBoxValues.ValueTester processVarTester = new ListBoxValues.ValueTester() {
            public String getNonCustomValueForUserString(String userValue) {
                return null;
            }
        };
        ListBoxValues processVarListBoxValues = new ListBoxValues(AssignmentListItemWidgetView.CONSTANT_PROMPT,
                                                                  StunnerFormsClientFieldsConstants.INSTANCE.Edit() + " ",
                                                                  processVarTester,
                                                                  ActivityDataIOEditorViewImpl.CONSTANT_MAX_DISPLAY_LENGTH);
        processVarComboBox.setListBoxValues(processVarListBoxValues);
        String sConstant = "sVariableWithALongName";
        widget.setConstant(sConstant);
        widget.setProcessVariables(processVarListBoxValues);
        verify(processVarComboBox,
               times(1)).setListBoxValues(processVarListBoxValues);
        verify(processVarComboBox,
               times(2)).setListBoxValues(any(ListBoxValues.class));
        verify(processVarComboBox).addCustomValueToListBoxValues(sConstant,
                                                                 "");
        verify(processVar).setValue(sConstant);
    }

    @Test
    public void testSetProcessVariablesConst() {
        ListBoxValues.ValueTester processVarTester = new ListBoxValues.ValueTester() {
            public String getNonCustomValueForUserString(String userValue) {
                return null;
            }
        };
        ListBoxValues processVarListBoxValues = new ListBoxValues(AssignmentListItemWidgetView.CONSTANT_PROMPT,
                                                                  StunnerFormsClientFieldsConstants.INSTANCE.Edit() + " ",
                                                                  processVarTester,
                                                                  ActivityDataIOEditorViewImpl.CONSTANT_MAX_DISPLAY_LENGTH);
        processVarComboBox.setAddCustomValues(true);
        processVarComboBox.setListBoxValues(processVarListBoxValues);
        String sConstant = "\"abcdeabcde12345\"";
        widget.setConstant(sConstant);
        widget.setProcessVariables(processVarListBoxValues);
        verify(processVarComboBox,
               times(1)).setListBoxValues(processVarListBoxValues);
        verify(processVarComboBox,
               times(2)).setListBoxValues(any(ListBoxValues.class));
        verify(processVarComboBox).addCustomValueToListBoxValues(sConstant,
                                                                 "");
        verify(processVar).setValue("\"abcdeabcde...\"");
    }
}
