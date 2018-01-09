/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.fields.shared.MultipleValueFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.impl.BooleanLOVInputFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.impl.CharacterLOVInputFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.impl.DateLOVInputFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.impl.DecimalLOVInputFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.impl.IntegerLOVInputFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.impl.StringLOVInputFieldDefinition;
import org.kie.workbench.common.forms.model.FieldType;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.forms.model.TypeKind;

@Dependent
public class LOVInputProvider extends BasicTypeFieldProvider<AbstractLOVInputFieldDefinition> implements MultipleValueFieldProvider<AbstractLOVInputFieldDefinition> {

    @Override
    public int getPriority() {
        return 60;
    }

    @Override
    protected void doRegisterFields() {
        // Integer types
        registerPropertyType(BigInteger.class);
        registerPropertyType(Byte.class);
        registerPropertyType(Integer.class);
        registerPropertyType(Long.class);
        registerPropertyType(Short.class);

        // Decimal types
        registerPropertyType(BigDecimal.class);
        registerPropertyType(Double.class);
        registerPropertyType(Float.class);

        // Date types
        registerPropertyType(Date.class);
        // TODO: Replace by class.getName once GWT supports the following types
        registerPropertyType("java.time.LocalDate");
        registerPropertyType("java.time.LocalDateTime");
        registerPropertyType("java.time.LocalTime");
        registerPropertyType("java.time.OffsetDateTime");

        registerPropertyType(Character.class);
        registerPropertyType(String.class);
        registerPropertyType(Boolean.class);
    }

    @Override
    public AbstractLOVInputFieldDefinition createFieldByType(TypeInfo typeInfo) {
        if (typeInfo.getType().equals(TypeKind.BASE) && typeInfo.isMultiple()) {
            if (typeInfo.getClassName().equals(Boolean.class.getName())) {
                return new BooleanLOVInputFieldDefinition();
            }
            if (typeInfo.getClassName().equals(Character.class.getName())) {
                return new CharacterLOVInputFieldDefinition();
            }
            if (typeInfo.getClassName().equals(String.class.getName())) {
                return new StringLOVInputFieldDefinition();
            }
            if (typeInfo.getClassName().equals(BigInteger.class.getName()) ||
                    typeInfo.getClassName().equals(Byte.class.getName()) ||
                    typeInfo.getClassName().equals(Integer.class.getName()) ||
                    typeInfo.getClassName().equals(Long.class.getName()) ||
                    typeInfo.getClassName().equals(Short.class.getName())) {
                return new IntegerLOVInputFieldDefinition();
            }
            if (typeInfo.getClassName().equals(BigDecimal.class.getName()) ||
                    typeInfo.getClassName().equals(Double.class.getName()) ||
                    typeInfo.getClassName().equals(Float.class.getName())) {
                return new DecimalLOVInputFieldDefinition();
            }
            if (typeInfo.getClassName().equals(Date.class.getName()) ||
                    typeInfo.getClassName().equals("java.time.LocalDate") ||
                    typeInfo.getClassName().equals("java.time.LocalDateTime") ||
                    typeInfo.getClassName().equals("java.time.LocalTime") ||
                    typeInfo.getClassName().equals("java.time.OffsetDateTime")) {
                return new DateLOVInputFieldDefinition();
            }
        }
        return null;
    }

    @Override
    public Class<? extends FieldType> getFieldType() {
        return LOVInputFieldType.class;
    }

    @Override
    public String getFieldTypeName() {
        return LOVInputFieldType.NAME;
    }

    @Override
    public AbstractLOVInputFieldDefinition getDefaultField() {
        return new StringLOVInputFieldDefinition();
    }

    @Override
    public boolean isSupported(TypeInfo typeInfo) {
        if (!typeInfo.getType().equals(TypeKind.BASE) || !typeInfo.isMultiple()) {
            return false;
        }
        return super.isSupported(typeInfo);
    }
}
