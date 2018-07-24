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

package org.kie.workbench.common.forms.cms.components.shared.model;

import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;

public abstract class BasicComponentSettings {

    @SelectorDataProvider(type = SelectorDataProvider.ProviderType.REMOTE, className = "org.kie.workbench.common.forms.cms.components.service.backend.OUSelectorDataProvider")
    @FormField(type = ListBoxFieldType.class, labelKey = "ou", required = true)
    protected String ou;

    @SelectorDataProvider(type = SelectorDataProvider.ProviderType.REMOTE, className = "org.kie.workbench.common.forms.cms.components.service.backend.ProjectSelectorDataProvider")
    @FormField(type = ListBoxFieldType.class, labelKey = "project", required = true, afterElement = "ou", settings = {@FieldParam(name = "relatedField", value = "ou")})
    protected String project;

    @SelectorDataProvider(type = SelectorDataProvider.ProviderType.REMOTE, className = "org.kie.workbench.common.forms.cms.components.service.backend.DOSelectorDataProvider")
    @FormField(type = ListBoxFieldType.class, labelKey = "dataObject", required = true, afterElement = "project", settings = {@FieldParam(name = "relatedField", value = "project")})
    protected String dataObject;

    public String getOu() {
        return ou;
    }

    public void setOu(String ou) {
        this.ou = ou;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getDataObject() {
        return dataObject;
    }

    public void setDataObject(String dataObject) {
        this.dataObject = dataObject;
    }
}
