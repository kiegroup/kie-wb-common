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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.field;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.popup.ImportsPopup;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.ImportsValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.WSDLImport;

@Dependent
@Templated
public class ImportsField extends Composite implements TakesValue<ImportsValue> {

    @Inject
    @DataField
    Button importsButton;

    @Inject
    @DataField
    TextBox importsTextBox;

    @Inject
    ImportsPopup importsPopup;

    ImportsValue importsValue;

    public ImportsField() {
    }

    ImportsField(final ImportsValue importsValue, ImportsPopup importsPopup) {
        this.importsValue = importsValue;
        this.importsPopup = importsPopup;
    }

    @PostConstruct
    public void init() {
        importsPopup.setDataCallback(this::setValue);
    }

    @Override
    public ImportsValue getValue() {
        return importsValue;
    }

    @Override
    public void setValue(final ImportsValue value) {
        importsValue = value;
        String importsCountString = buildImportsCountString(importsValue);
        importsTextBox.setText(importsCountString);
    }

    String buildImportsCountString(final ImportsValue importsValue) {
        int defaultImportsCount = 0;
        int wsdlImportsCount = 0;

        if (importsValue != null) {
            defaultImportsCount = importsValue.getDefaultImports().size();
            wsdlImportsCount = importsValue.getWSDLImports().size();
        }

        if (defaultImportsCount == 0 && wsdlImportsCount == 0) {
            return StunnerFormsClientFieldsConstants.CONSTANTS.No_Imports();
        } else {
            String defaultImportsCountString = buildDefaultImportsCountString(defaultImportsCount);
            String wsdlImportsCountString = buildWSDLImportsCountString(wsdlImportsCount);
            return defaultImportsCountString + ", " + wsdlImportsCountString;
        }
    }

    String buildDefaultImportsCountString(final int defaultImportsCount) {
        if (defaultImportsCount == 0) {
            return StunnerFormsClientFieldsConstants.CONSTANTS.No_Data_Type_Import();
        } else if (defaultImportsCount == 1) {
            return StunnerFormsClientFieldsConstants.CONSTANTS.Data_Type_Import();
        } else {
            return defaultImportsCount + " " + StunnerFormsClientFieldsConstants.CONSTANTS.Data_Type_Imports();
        }
    }

    String buildWSDLImportsCountString(final int wsdlImportsCount) {
        if (wsdlImportsCount == 0) {
            return StunnerFormsClientFieldsConstants.CONSTANTS.No_WSDL_Import();
        } else if (wsdlImportsCount == 1) {
            return StunnerFormsClientFieldsConstants.CONSTANTS.WSDL_Import();
        } else {
            return wsdlImportsCount + " " + StunnerFormsClientFieldsConstants.CONSTANTS.WSDL_Imports();
        }
    }

    ImportsValue copyImportsValue(ImportsValue importsValue) {
        ImportsValue copy = new ImportsValue();

        if (importsValue != null) {
            for (DefaultImport defaultImport : importsValue.getDefaultImports()) {
                DefaultImport importCopy = new DefaultImport();
                importCopy.setClassName(defaultImport.getClassName());
                copy.addImport(importCopy);
            }
            for (WSDLImport wsdlImport : importsValue.getWSDLImports()) {
                WSDLImport importCopy = new WSDLImport();
                importCopy.setLocation(wsdlImport.getLocation());
                importCopy.setNamespace(wsdlImport.getNamespace());
                copy.addImport(importCopy);
            }
        }

        return copy;
    }

    void showImportsEditor() {
        ImportsValue importsValueCopy = copyImportsValue(importsValue);
        importsPopup.setDefaultImports(importsValueCopy.getDefaultImports());
        importsPopup.setWSDLImports(importsValueCopy.getWSDLImports());
        importsPopup.show();
    }

    public void setReadOnly(final boolean readOnly) {
        importsTextBox.setEnabled(!readOnly);
        importsButton.setEnabled(!readOnly);
    }

    @EventHandler("importsButton")
    public void onClickImportsButton(final ClickEvent clickEvent) {
        showImportsEditor();
    }

    @EventHandler("importsTextBox")
    public void onClickImportsTextBox(final ClickEvent clickEvent) {
        showImportsEditor();
    }
}