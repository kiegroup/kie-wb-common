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

package org.kie.workbench.common.stunner.bpmn.forms.service.fieldProviders;

import javax.enterprise.inject.Model;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.stunner.bpmn.forms.model.SLASettingsFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.forms.model.SLASettingsFieldType;

@Model
public class SLASettingsFieldProvider extends BasicTypeFieldProvider<SLASettingsFieldDefinition> {

    @Override
    public int getPriority() {
        return 70000;
    }

    @Override
    protected void doRegisterFields() {
        registerPropertyType(String.class);
    }

    @Override
    public SLASettingsFieldDefinition createFieldByType(TypeInfo typeInfo) {
        return getDefaultField();
    }

    @Override
    public Class<SLASettingsFieldType> getFieldType() {
        return SLASettingsFieldType.class;
    }

    @Override
    public String getFieldTypeName() {
        return SLASettingsFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    public SLASettingsFieldDefinition getDefaultField() {
        return new SLASettingsFieldDefinition();
    }
}
