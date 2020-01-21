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

import javax.annotation.PostConstruct;

import com.google.gwt.user.client.ui.Composite;
import elemental2.dom.HTMLTableRowElement;
import org.gwtbootstrap3.client.ui.Button;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.editors.ImportsEditorView;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.CustomDataTypeTextBox;

public abstract class ImportListItemViewImpl<T> extends Composite
        implements ImportListItemView<T> {

    abstract DataBinder<T> getImportDataBinder();

    abstract HTMLTableRowElement getImportItem();

    abstract Button getDeleteButton();

    @PostConstruct
    public void init() {
        getImportItem().hidden = false;
    }

    @Override
    public T getValue() {
        return getImportDataBinder().getModel();
    }

    @Override
    public void setValue(T value) {
        getImportDataBinder().setModel(value);
    }

    @Override
    public void setPresenter(final ImportsEditorView.Presenter<T> presenter) {
        if (presenter != null) {
            getDeleteButton().addClickHandler(clickEvent -> presenter.removeImport(this.getValue()));
        }
    }

    void initCustomDataTypeTextBox(CustomDataTypeTextBox customDataTypeTextBox) {
        customDataTypeTextBox.setRegExp(StringUtils.JAVA_IDENTIFIER_REGEXP,
                                        StunnerFormsClientFieldsConstants.CONSTANTS.Removed_invalid_characters_from_name(),
                                        StunnerFormsClientFieldsConstants.CONSTANTS.Invalid_character_in_name());
    }
}
