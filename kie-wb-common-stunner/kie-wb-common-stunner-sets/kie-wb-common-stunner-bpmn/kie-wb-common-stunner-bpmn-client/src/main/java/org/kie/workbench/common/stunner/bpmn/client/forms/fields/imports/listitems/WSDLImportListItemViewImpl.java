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
import javax.inject.Inject;

import elemental2.dom.HTMLTableRowElement;
import org.gwtbootstrap3.client.ui.Button;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.CustomDataTypeTextBox;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.WSDLImport;

@Templated(value = "/org/kie/workbench/common/stunner/bpmn/client/forms/fields/imports/editors/wsdlimport/WSDLImportsEditorViewImpl.html#importItem")
public class WSDLImportListItemViewImpl extends ImportListItemViewImpl<WSDLImport> {

    @Inject
    @AutoBound
    DataBinder<WSDLImport> importDataBinder;

    @Inject
    @DataField
    HTMLTableRowElement importItem;

    @Inject
    @Bound
    @DataField
    CustomDataTypeTextBox location;

    @Inject
    @Bound
    @DataField
    CustomDataTypeTextBox namespace;

    @Inject
    @DataField
    Button deleteButton;

    @PostConstruct
    @Override
    public void init() {
        initCustomDataTypeTextBox(location);
        initCustomDataTypeTextBox(namespace);
        super.init();
    }

    @Override
    DataBinder<WSDLImport> getImportDataBinder() {
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

    public String getLocation() {
        return getValue().getLocation();
    }

    public void setLocation(String location) {
        getValue().setLocation(location);
    }

    public String getNamespace() {
        return getValue().getNamespace();
    }

    public void setNamespace(String namespace) {
        getValue().setNamespace(namespace);
    }
}
