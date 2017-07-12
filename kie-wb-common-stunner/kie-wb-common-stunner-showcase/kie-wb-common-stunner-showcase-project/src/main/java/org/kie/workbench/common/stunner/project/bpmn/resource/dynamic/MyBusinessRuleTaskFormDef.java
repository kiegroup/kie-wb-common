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

package org.kie.workbench.common.stunner.project.bpmn.resource.dynamic;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.adf.definitions.settings.ColSpan;
import org.kie.workbench.common.forms.adf.definitions.settings.DynamicFormDefinition;
import org.kie.workbench.common.forms.adf.service.definitions.FormDefinitionSettings;
import org.kie.workbench.common.forms.adf.service.definitions.I18nSettings;
import org.kie.workbench.common.forms.adf.service.definitions.elements.FieldElement;
import org.kie.workbench.common.forms.adf.service.definitions.elements.FormElement;
import org.kie.workbench.common.forms.adf.service.definitions.layout.LayoutColumnDefinition;
import org.kie.workbench.common.forms.adf.service.definitions.layout.LayoutDefinition;
import org.kie.workbench.common.forms.model.TypeKind;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;

@Dependent
public class MyBusinessRuleTaskFormDef implements DynamicFormDefinition {

    @Override
    public String getClassName() {
        return MyBusinessRuleTask.class.getName();
    }

    @Override
    public FormDefinitionSettings getFormDefinition() {
        return new MyBusinessRuleTaskFormBuilder().getSettings();
    }

    private static final class MyBusinessRuleTaskFormBuilder {

        public FormDefinitionSettings getSettings() {
            FormDefinitionSettings settings = new FormDefinitionSettings("org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask");
            settings.setI18nSettings(new I18nSettings("org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask"));
            settings.setLayout(new LayoutDefinition(new LayoutColumnDefinition[]{new LayoutColumnDefinition(ColSpan.AUTO)}));
            List<FormElement> elements = new ArrayList<FormElement>();
            elements.add(getFormElement_general());
            elements.add(getFormElement_executionSet());
            elements.add(getFormElement_dataIOSet());
            elements.add(getFormElement_myProperty());
            settings.getFormElements().addAll(elements);
            return settings;
        }

        private FormElement getFormElement_general() {
            FieldElement field = new FieldElement("general",
                                                  "general",
                                                  new TypeInfoImpl(TypeKind.OBJECT,
                                                                   "org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet",
                                                                   false));
            field.setPreferredType(org.kie.workbench.common.forms.model.FieldType.class);
            field.setLabelKey("org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet.propertySetName");
            field.setRequired(false);
            field.setReadOnly(false);
            field.getLayoutSettings().setAfterElement("");
            field.getLayoutSettings().setHorizontalSpan(1);
            field.getLayoutSettings().setVerticalSpan(1);
            field.getLayoutSettings().setWrap(false);
            return field;
        }

        private FormElement getFormElement_executionSet() {
            FieldElement field = new FieldElement("executionSet",
                                                  "executionSet",
                                                  new TypeInfoImpl(TypeKind.OBJECT,
                                                                   "org.kie.workbench.common.stunner.bpmn.definition.property.task.BusinessRuleTaskExecutionSet",
                                                                   false));
            field.setPreferredType(org.kie.workbench.common.forms.model.FieldType.class);
            field.setLabelKey("org.kie.workbench.common.stunner.bpmn.definition.property.task.BusinessRuleTaskExecutionSet.propertySetName");
            field.setRequired(false);
            field.setReadOnly(false);
            field.getLayoutSettings().setAfterElement("general");
            field.getLayoutSettings().setHorizontalSpan(1);
            field.getLayoutSettings().setVerticalSpan(1);
            field.getLayoutSettings().setWrap(false);
            return field;
        }

        private FormElement getFormElement_dataIOSet() {
            FieldElement field = new FieldElement("dataIOSet",
                                                  "dataIOSet",
                                                  new TypeInfoImpl(TypeKind.OBJECT,
                                                                   "org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet",
                                                                   false));
            field.setPreferredType(org.kie.workbench.common.forms.model.FieldType.class);
            field.setLabelKey("org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet.propertySetName");
            field.setRequired(false);
            field.setReadOnly(false);
            field.getLayoutSettings().setAfterElement("executionSet");
            field.getLayoutSettings().setHorizontalSpan(1);
            field.getLayoutSettings().setVerticalSpan(1);
            field.getLayoutSettings().setWrap(false);
            return field;
        }

        private FormElement getFormElement_myProperty() {
            FieldElement field = new FieldElement("myProperty",
                                                  "myProperty",
                                                  new TypeInfoImpl(TypeKind.OBJECT,
                                                                   "org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet",
                                                                   false));
            field.setPreferredType(org.kie.workbench.common.forms.model.FieldType.class);
            field.setLabelKey("org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet.propertySetName");
            field.setRequired(false);
            field.setReadOnly(false);
            field.getLayoutSettings().setAfterElement("dataIOSet");
            field.getLayoutSettings().setHorizontalSpan(1);
            field.getLayoutSettings().setVerticalSpan(1);
            field.getLayoutSettings().setWrap(false);
            return field;
        }
    }
}
