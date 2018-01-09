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

package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.fields.shared.MultipleValueFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.impl.BooleanLOVFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.impl.CharacterLOVFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.impl.DateLOVFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.impl.DecimalLOVFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.impl.IntegerLOVFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.impl.StringLOVFieldDefinition;
import org.kie.workbench.common.forms.model.FieldType;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.forms.model.TypeKind;

@Dependent
public class LOVSelectorProvider extends BasicTypeFieldProvider<AbstractLOVSelectorFieldDefinition> implements MultipleValueFieldProvider<AbstractLOVSelectorFieldDefinition> {

    @Override
    public int getPriority() {
        return 50;
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
    public AbstractLOVSelectorFieldDefinition createFieldByType(TypeInfo typeInfo) {
        if(typeInfo.getType().equals(TypeKind.BASE) && typeInfo.isMultiple()) {
            if(typeInfo.getClassName().equals(String.class.getName())) {
                return new StringLOVFieldDefinition();
            }
            if (typeInfo.getClassName().equals(Boolean.class.getName())) {
                return new BooleanLOVFieldDefinition();
            }
            if (typeInfo.getClassName().equals(Character.class.getName())) {
                return new CharacterLOVFieldDefinition();
            }
            if (typeInfo.getClassName().equals(BigInteger.class.getName()) ||
                    typeInfo.getClassName().equals(Byte.class.getName()) ||
                    typeInfo.getClassName().equals(Integer.class.getName()) ||
                    typeInfo.getClassName().equals(Long.class.getName()) ||
                    typeInfo.getClassName().equals(Short.class.getName())) {
                return new IntegerLOVFieldDefinition();
            }
            if (typeInfo.getClassName().equals(BigDecimal.class.getName()) ||
                    typeInfo.getClassName().equals(Double.class.getName()) ||
                    typeInfo.getClassName().equals(Float.class.getName())) {
                return new DecimalLOVFieldDefinition();
            }
            if (typeInfo.getClassName().equals(Date.class.getName()) ||
                    typeInfo.getClassName().equals("java.time.LocalDate") ||
                    typeInfo.getClassName().equals("java.time.LocalDateTime") ||
                    typeInfo.getClassName().equals("java.time.LocalTime") ||
                    typeInfo.getClassName().equals("java.time.OffsetDateTime")) {
                return new DateLOVFieldDefinition();
            }
        }
        return null;
    }

    @Override
    public Class<? extends FieldType> getFieldType() {
        return LOVSelectorFieldType.class;
    }

    @Override
    public String getFieldTypeName() {
        return LOVSelectorFieldType.NAME;
    }

    @Override
    public AbstractLOVSelectorFieldDefinition getDefaultField() {
        return new StringLOVFieldDefinition();
    }

    @Override
    public boolean isSupported(TypeInfo typeInfo) {
        if(!typeInfo.getType().equals(TypeKind.BASE) || !typeInfo.isMultiple()) {
            return false;
        }
        return super.isSupported(typeInfo);
    }
}
