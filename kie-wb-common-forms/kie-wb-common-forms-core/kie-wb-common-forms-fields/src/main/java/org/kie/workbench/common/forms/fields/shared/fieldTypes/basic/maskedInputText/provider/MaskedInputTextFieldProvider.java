/*
 * Copyright 2024 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.maskedInputText.provider;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.maskedInputText.definition.AbstractMaskedInputTextFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.maskedInputText.definition.MaskedInputTextFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.maskedInputText.type.MaskedInputTextFieldType;
import org.kie.workbench.common.forms.model.TypeInfo;

@Dependent
public class MaskedInputTextFieldProvider extends BasicTypeFieldProvider<AbstractMaskedInputTextFieldDefinition> {

    @Override
    public Class<MaskedInputTextFieldType> getFieldType() {
        return MaskedInputTextFieldType.class;
    }

    @Override
    public String getFieldTypeName() {
        return AbstractMaskedInputTextFieldDefinition.FIELD_TYPE.getTypeName();
    }

    @Override
    protected void doRegisterFields() {
        registerPropertyType(String.class);
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public AbstractMaskedInputTextFieldDefinition getDefaultField() {
        return new MaskedInputTextFieldDefinition();
    }

    @Override
    public AbstractMaskedInputTextFieldDefinition createFieldByType(TypeInfo typeInfo) {
        return getDefaultField();
    }
}
