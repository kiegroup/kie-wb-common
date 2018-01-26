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

import java.util.List;

import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.fields.shared.AbstractFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FieldType;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.forms.model.TypeKind;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;

public abstract class AbstractLOVSelectorFieldDefinition<TYPE> extends AbstractFieldDefinition {

    public static LOVSelectorFieldType FIELD_TYPE = new LOVSelectorFieldType();

    @FormField(
            labelKey = "maxDropdownElements",
            afterElement = "label"
    )
    private Integer maxDropdownElements = 10;

    @FormField(
            labelKey = "maxElementsOnTitle",
            afterElement = "maxDropdownElements"
    )
    private Integer maxElementsOnTitle = 5;

    @FormField(
            labelKey = "allowFilter",
            afterElement = "maxElementsOnTitle"
    )
    private Boolean allowFilter = true;

    @FormField(
            labelKey = "allowClearSelection",
            afterElement = "allowFilter"
    )
    private Boolean allowClearSelection = true;

    public AbstractLOVSelectorFieldDefinition(String className) {
        super(className);
    }

    @Override
    public FieldType getFieldType() {
        return FIELD_TYPE;
    }

    public Integer getMaxDropdownElements() {
        return maxDropdownElements;
    }

    public void setMaxDropdownElements(Integer maxDropdownElements) {
        this.maxDropdownElements = maxDropdownElements;
    }

    public Integer getMaxElementsOnTitle() {
        return maxElementsOnTitle;
    }

    public void setMaxElementsOnTitle(Integer maxElementsOnTitle) {
        this.maxElementsOnTitle = maxElementsOnTitle;
    }

    public Boolean getAllowFilter() {
        return allowFilter;
    }

    public void setAllowFilter(Boolean allowFilter) {
        this.allowFilter = allowFilter;
    }

    public Boolean getAllowClearSelection() {
        return allowClearSelection;
    }

    public void setAllowClearSelection(Boolean allowClearSelection) {
        this.allowClearSelection = allowClearSelection;
    }

    public abstract List<TYPE> getListOfValues();

    public abstract void setListOfValues(List<TYPE> listOfValues);

    @Override
    public TypeInfo getFieldTypeInfo() {
        return new TypeInfoImpl(TypeKind.BASE, getStandaloneClassName(), true);
    }

    @Override
    protected void doCopyFrom(FieldDefinition other) {
        if(other instanceof AbstractLOVSelectorFieldDefinition) {
            AbstractLOVSelectorFieldDefinition otherLOV = (AbstractLOVSelectorFieldDefinition) other;
            setMaxDropdownElements(otherLOV.maxDropdownElements);
            setMaxElementsOnTitle(otherLOV.maxElementsOnTitle);
            setAllowFilter(otherLOV.allowFilter);
            setAllowClearSelection(otherLOV.allowClearSelection);
            setListOfValues(otherLOV.getListOfValues());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        AbstractLOVSelectorFieldDefinition<?> that = (AbstractLOVSelectorFieldDefinition<?>) o;

        if (maxDropdownElements != null ? !maxDropdownElements.equals(that.maxDropdownElements) : that.maxDropdownElements != null) {
            return false;
        }
        if (maxElementsOnTitle != null ? !maxElementsOnTitle.equals(that.maxElementsOnTitle) : that.maxElementsOnTitle != null) {
            return false;
        }
        if (allowFilter != null ? !allowFilter.equals(that.allowFilter) : that.allowFilter != null) {
            return false;
        }
        return allowClearSelection != null ? allowClearSelection.equals(that.allowClearSelection) : that.allowClearSelection == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (maxDropdownElements != null ? maxDropdownElements.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (maxElementsOnTitle != null ? maxElementsOnTitle.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (allowFilter != null ? allowFilter.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (allowClearSelection != null ? allowClearSelection.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (getListOfValues() != null ? getListOfValues().hashCode() : 0);
        result = ~~result;
        return result;
    }
}
