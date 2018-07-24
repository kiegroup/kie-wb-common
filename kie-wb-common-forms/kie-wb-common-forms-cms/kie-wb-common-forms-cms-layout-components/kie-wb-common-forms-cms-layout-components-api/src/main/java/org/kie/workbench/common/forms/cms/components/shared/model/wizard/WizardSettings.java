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

package org.kie.workbench.common.forms.cms.components.shared.model.wizard;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.cms.components.shared.model.BasicComponentSettings;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;

@Portable
@Bindable
@FormDefinition(i18n = @I18nSettings(keyPreffix = "FormLabels"), startElement = "ou")
public class WizardSettings extends BasicComponentSettings {

    @SelectorDataProvider(type = SelectorDataProvider.ProviderType.REMOTE, className = "org.kie.workbench.common.forms.cms.components.service.backend.FormsSelectorDataProvider")
    @FormField(type = ListBoxFieldType.class,
            labelKey = "steps",
            afterElement = "dataObject")
    private List<WizardStep> steps = new ArrayList<>();

    public List<WizardStep> getSteps() {
        return steps;
    }

    public void setSteps(List<WizardStep> steps) {
        this.steps = steps;
    }
}
