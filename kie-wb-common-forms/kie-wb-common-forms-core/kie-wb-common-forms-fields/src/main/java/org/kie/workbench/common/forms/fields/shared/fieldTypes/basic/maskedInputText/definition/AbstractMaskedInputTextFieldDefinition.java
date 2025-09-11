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

package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.maskedInputText.definition;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.fields.shared.AbstractFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.maskedInputText.type.MaskedInputTextFieldType;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.HasPlaceHolder;

public abstract class AbstractMaskedInputTextFieldDefinition extends AbstractFieldDefinition implements HasPlaceHolder {

    public static MaskedInputTextFieldType FIELD_TYPE = new MaskedInputTextFieldType();

    @FormField(
            labelKey = "placeHolder",
            afterElement = "label"
    )
    protected String placeHolder = "";

    @FormField(
            labelKey = "FieldProperties.minLength",
            afterElement = "maxLength"
    )
    @Min(0)
    protected Integer minLength;

    @FormField(
            labelKey = "FieldProperties.maskingCharacter",
            afterElement = "minLength"
    )
    @Size(min = 1, max = 1, message = "Masking character must be exactly one character")
    @Pattern(regexp = "[a-zA-Z0-9*#@$%&!?]", message = "Masking character must be a single alphanumeric or special character")
    protected String maskingCharacter;

    @FormField(
            labelKey = "FieldProperties.maskingStartIndex",
            afterElement = "maskingCharacter",
            settings = {@FieldParam(name = "horizontalSpan", value = "6")}
    )
    @Min(value = 0, message = "Masking start index cannot be negative")
    protected Integer maskingStartIndex;

    @FormField(
            labelKey = "FieldProperties.maskingFromStartLength",
            afterElement = "maskingStartIndex",
            settings = {@FieldParam(name = "horizontalSpan", value = "6")}
    )
    @Min(value = 0, message = "Masking from start length cannot be negative")
    protected Integer maskingFromStartLength;

    @FormField(
            labelKey = "FieldProperties.maskingFromEndLength",
            afterElement = "maskingFromStartLength"
    )
    @Min(value = 0, message = "Masking from end length cannot be negative")
    protected Integer maskingFromEndLength;

    @FormField(
            labelKey = "FieldProperties.maskInDatabase",
            afterElement = "maskingFromEndLength"
    )
    protected Boolean isMaskedInDB;

    public AbstractMaskedInputTextFieldDefinition(String className) {
        super(className);
    }

    @Override
    public MaskedInputTextFieldType getFieldType() {
        return FIELD_TYPE;
    }

    @Override
    public String getPlaceHolder() {
        return placeHolder;
    }

    @Override
    public void setPlaceHolder(String placeHolder) {
        this.placeHolder = placeHolder;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public String getMaskingCharacter() {
        return maskingCharacter;
    }

    public void setMaskingCharacter(String maskingCharacter) {
        this.maskingCharacter = maskingCharacter;
    }

    public Integer getMaskingStartIndex() {
        return maskingStartIndex;
    }

    public void setMaskingStartIndex(Integer maskingStartIndex) {
        this.maskingStartIndex = maskingStartIndex;
    }

    public Integer getMaskingFromStartLength() {
        return maskingFromStartLength;
    }

    public void setMaskingFromStartLength(Integer maskingFromStartLength) {
        this.maskingFromStartLength = maskingFromStartLength;
    }

    public Integer getMaskingFromEndLength() {
        return maskingFromEndLength;
    }

    public void setMaskingFromEndLength(Integer maskingFromEndLength) {
        this.maskingFromEndLength = maskingFromEndLength;
    }

    public Boolean getIsMaskedInDB() {
        return isMaskedInDB;
    }

    public void setIsMaskedInDB(Boolean isMaskedInDB) {
        this.isMaskedInDB = isMaskedInDB;
    }

    @Override
    protected void doCopyFrom(FieldDefinition other) {
        if (other instanceof HasPlaceHolder) {
            setPlaceHolder(((HasPlaceHolder) other).getPlaceHolder());
        }
        if (other instanceof AbstractMaskedInputTextFieldDefinition) {
            AbstractMaskedInputTextFieldDefinition otherField = (AbstractMaskedInputTextFieldDefinition) other;
            setMinLength(otherField.getMinLength());
            setMaskingCharacter(otherField.getMaskingCharacter());
            setMaskingStartIndex(otherField.getMaskingStartIndex());
            setMaskingFromStartLength(otherField.getMaskingFromStartLength());
            setMaskingFromEndLength(otherField.getMaskingFromEndLength());
            setIsMaskedInDB(otherField.getIsMaskedInDB());
        }
    }

    /**
     * Validates that if maskingStartIndex is provided, then maskingFromStartLength is also required
     */
    @AssertTrue(message = "Masking from start length is required when masking start index is specified")
    public boolean isMaskingFromStartLengthValidWhenStartIndexProvided() {
        if (maskingStartIndex != null) {
            return maskingFromStartLength != null;
        }
        return true;
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

        AbstractMaskedInputTextFieldDefinition that = (AbstractMaskedInputTextFieldDefinition) o;

        if (placeHolder != null ? !placeHolder.equals(that.placeHolder) : that.placeHolder != null) {
            return false;
        }
        if (minLength != null ? !minLength.equals(that.minLength) : that.minLength != null) {
            return false;
        }
        if (maskingCharacter != null ? !maskingCharacter.equals(that.maskingCharacter) : that.maskingCharacter != null) {
            return false;
        }
        if (maskingStartIndex != null ? !maskingStartIndex.equals(that.maskingStartIndex) : that.maskingStartIndex != null) {
            return false;
        }
        if (maskingFromStartLength != null ? !maskingFromStartLength.equals(that.maskingFromStartLength) : that.maskingFromStartLength != null) {
            return false;
        }
        if (maskingFromEndLength != null ? !maskingFromEndLength.equals(that.maskingFromEndLength) : that.maskingFromEndLength != null) {
            return false;
        }
        if (isMaskedInDB != null ? !isMaskedInDB.equals(that.isMaskedInDB) : that.isMaskedInDB != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (placeHolder != null ? placeHolder.hashCode() : 0);
        result = 31 * result + (minLength != null ? minLength.hashCode() : 0);
        result = 31 * result + (maskingCharacter != null ? maskingCharacter.hashCode() : 0);
        result = 31 * result + (maskingStartIndex != null ? maskingStartIndex.hashCode() : 0);
        result = 31 * result + (maskingFromStartLength != null ? maskingFromStartLength.hashCode() : 0);
        result = 31 * result + (maskingFromEndLength != null ? maskingFromEndLength.hashCode() : 0);
        result = 31 * result + (isMaskedInDB != null ? isMaskedInDB.hashCode() : 0);
        return result;
    }
}
