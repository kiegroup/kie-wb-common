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

import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.fields.shared.AbstractFieldDefinition;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FieldType;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.forms.model.TypeKind;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;

public abstract class AbstractLOVInputFieldDefinition extends AbstractFieldDefinition {

    private static final LOVInputFieldType FIELD_TYPE = new LOVInputFieldType();

    @FormField(
            labelKey = "pageSize",
            afterElement = "label"
    )
    private Integer pageSize = 5;

    public AbstractLOVInputFieldDefinition(String className) {
        super(className);
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    protected void doCopyFrom(FieldDefinition other) {
        if (other instanceof AbstractLOVInputFieldDefinition) {
            AbstractLOVInputFieldDefinition otherInput = (AbstractLOVInputFieldDefinition) other;
            this.pageSize = otherInput.pageSize;
        }
    }

    @Override
    public TypeInfo getFieldTypeInfo() {
        return new TypeInfoImpl(TypeKind.BASE,
                                getStandaloneClassName(),
                                true);
    }

    @Override
    public FieldType getFieldType() {
        return FIELD_TYPE;
    }
}
