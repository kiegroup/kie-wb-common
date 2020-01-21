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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import elemental2.dom.HTMLTableRowElement;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.editors.ImportsEditorView;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.editors.defaultimport.DefaultImportsEditor;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.variablesEditor.VariableListItemWidgetView;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBoxView;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.CustomDataTypeTextBox;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;

@Templated(value = "/org/kie/workbench/common/stunner/bpmn/client/forms/fields/imports/editors/defaultimport/DefaultImportsEditorViewImpl.html#importItem")
public class DefaultImportListItemViewImpl
        extends ImportListItemViewImpl<DefaultImport>
        implements ComboBoxView.ModelPresenter {

    protected static final String CUSTOM_PROMPT = "Custom" + ListBoxValues.EDIT_SUFFIX;
    protected static final String ENTER_TYPE_PROMPT = "Enter type" + ListBoxValues.EDIT_SUFFIX;

    @Inject
    @AutoBound
    DataBinder<DefaultImport> importDataBinder;

    @Inject
    @DataField
    HTMLTableRowElement importItem;

    @DataField
    ValueListBox<String> defaultClassNames = new ValueListBox<>(new DefaultClassNamesRenderer());

    @Inject
    @DataField
    CustomDataTypeTextBox customClassName;

    @Inject
    ComboBox classNamesComboBox;

    @Inject
    @DataField
    Button deleteButton;

    DefaultImportsEditor presenter;

    @PostConstruct
    @Override
    public void init() {
        initCustomDataTypeTextBox(customClassName);
        super.init();
    }

    @Override
    DataBinder<DefaultImport> getImportDataBinder() {
        return importDataBinder;
    }

    @Override
    HTMLTableRowElement getImportItem() {
        return importItem;
    }

    @Override
    Button getDeleteButton() {
        return deleteButton;
    }

    @Override
    public void setPresenter(final ImportsEditorView.Presenter<DefaultImport> presenter) {
        super.setPresenter(presenter);
        this.presenter = (DefaultImportsEditor) presenter;
        if (presenter != null) {
            initListItem();
        }
    }

    @Override
    public void setTextBoxModelValue(final TextBox textBox, final String value) {
        if (value != null && !value.isEmpty()) {
            getValue().setClassName(value);
        }
    }

    @Override
    public void setListBoxModelValue(final ValueListBox<String> listBox, final String displayName) {
        String value = presenter.getDataType(displayName);
        getValue().setClassName(value);
    }

    @Override
    public String getModelValue(final ValueListBox<String> listBox) {
        return getValue().getClassName();
    }

    @Override
    public void notifyModelChanged() {
        //No need to notify changes.
    }

    void initListItem() {
        ListBoxValues classNameListBoxValues = getListBoxValues();
        String displayName = getDefaultClassName();

        classNamesComboBox.setShowCustomValues(true);
        classNamesComboBox.setListBoxValues(classNameListBoxValues);

        defaultClassNames.setValue(displayName);

        classNamesComboBox.init(this,
                                true,
                                defaultClassNames,
                                customClassName,
                                false,
                                true,
                                CUSTOM_PROMPT,
                                ENTER_TYPE_PROMPT);
    }

    String getDefaultClassName() {
        String className = getValue().getClassName();
        if (className == null || className.isEmpty()) {
            className = Object.class.getSimpleName();
        }

        return presenter.getDataType(className);
    }

    ListBoxValues getListBoxValues() {
        Map<String, String> dataTypes = presenter.getDataTypes();
        List<String> displayNames = new ArrayList<>(dataTypes.values());

        ListBoxValues classNameListBoxValues = new ListBoxValues(VariableListItemWidgetView.CUSTOM_PROMPT,
                                                                 "Edit" + " ",
                                                                 null);
        classNameListBoxValues.addValues(displayNames);
        return classNameListBoxValues;
    }
}
