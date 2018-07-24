/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.cms.components.shared.model.crud;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.cms.components.shared.model.BasicComponentSettings;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;

@Portable
@Bindable
@FormDefinition(i18n = @I18nSettings(keyPreffix = "FormLabels"), startElement = "ou")
public class CRUDSettings extends BasicComponentSettings {

    @SelectorDataProvider(type = SelectorDataProvider.ProviderType.REMOTE, className = "org.kie.workbench.common.forms.cms.components.service.backend.FormsSelectorDataProvider")
    @FormField(type = ListBoxFieldType.class,
            labelKey = "creationForm",
            afterElement = "dataObject",
            required = true,
            settings = {@FieldParam(name = "relatedField", value = "dataObject")})
    private String creationForm;

    @SelectorDataProvider(type = SelectorDataProvider.ProviderType.REMOTE, className = "org.kie.workbench.common.forms.cms.components.service.backend.FormsSelectorDataProvider")
    @FormField(type = ListBoxFieldType.class,
            labelKey = "editionForm",
            afterElement = "creationForm",
            required = true,
            settings = {@FieldParam(name = "relatedField", value = "dataObject")})
    private String editionForm;

    @SelectorDataProvider(type = SelectorDataProvider.ProviderType.REMOTE, className = "org.kie.workbench.common.forms.cms.components.service.backend.FormsSelectorDataProvider")
    @FormField(type = ListBoxFieldType.class,
            labelKey = "previewForm",
            afterElement = "editionForm",
            required = true,
            settings = {@FieldParam(name = "relatedField", value = "dataObject")})
    private String previewForm;

    @SelectorDataProvider(type = SelectorDataProvider.ProviderType.REMOTE, className = "org.kie.workbench.common.forms.cms.components.service.backend.FormsSelectorDataProvider")
    @FormField(type = ListBoxFieldType.class,
            labelKey = "tableForm",
            afterElement = "previewForm",
            required = true,
            settings = {@FieldParam(name = "relatedField", value = "dataObject")})
    private String tableForm;

    public String getCreationForm() {
        return creationForm;
    }

    public void setCreationForm(String creationForm) {
        this.creationForm = creationForm;
    }

    public String getEditionForm() {
        return editionForm;
    }

    public void setEditionForm(String editionForm) {
        this.editionForm = editionForm;
    }

    public String getPreviewForm() {
        return previewForm;
    }

    public void setPreviewForm(String previewForm) {
        this.previewForm = previewForm;
    }

    public String getTableForm() {
        return tableForm;
    }

    public void setTableForm(String tableForm) {
        this.tableForm = tableForm;
    }
}
