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

import org.junit.Test;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.maskedInputText.type.MaskedInputTextFieldType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class MaskedInputTextFieldDefinitionTest {

    @Test
    public void testFieldCreation() {
        MaskedInputTextFieldDefinition field = new MaskedInputTextFieldDefinition();
        
        assertNotNull("Field should not be null", field);
        assertNotNull("Field type should not be null", field.getFieldType());
        assertTrue("Field type should be MaskedInputTextFieldType", 
                  field.getFieldType() instanceof MaskedInputTextFieldType);
        assertEquals("Field type name should match", "MaskedInputText", 
                    field.getFieldType().getTypeName());
        assertEquals("Standalone class name should be String", String.class.getName(), 
                    field.getStandaloneClassName());
    }

    @Test
    public void testDefaultValues() {
        MaskedInputTextFieldDefinition field = new MaskedInputTextFieldDefinition();
        
        assertEquals("Default maxLength should be 100", Integer.valueOf(100), field.getMaxLength());
        assertEquals("Default placeholder should be empty", "", field.getPlaceHolder());
        assertNull("Default minLength should be null", field.getMinLength());
        assertNull("Default maskingCharacter should be null", field.getMaskingCharacter());
        assertNull("Default maskingStartIndex should be null", field.getMaskingStartIndex());
        assertNull("Default maskingFromStartLength should be null", field.getMaskingFromStartLength());
        assertNull("Default maskingFromEndLength should be null", field.getMaskingFromEndLength());
        assertNull("Default isMaskedInDB should be null", field.getIsMaskedInDB());
    }

    @Test
    public void testPropertySettersAndGetters() {
        MaskedInputTextFieldDefinition field = new MaskedInputTextFieldDefinition();
        
        // Test maxLength
        field.setMaxLength(50);
        assertEquals("MaxLength should be set correctly", Integer.valueOf(50), field.getMaxLength());
        
        // Test placeHolder
        field.setPlaceHolder("Enter masked text...");
        assertEquals("PlaceHolder should be set correctly", "Enter masked text...", field.getPlaceHolder());
        
        // Test minLength
        field.setMinLength(5);
        assertEquals("MinLength should be set correctly", Integer.valueOf(5), field.getMinLength());
        
        // Test maskingCharacter
        field.setMaskingCharacter("*");
        assertEquals("MaskingCharacter should be set correctly", "*", field.getMaskingCharacter());
        
        // Test maskingStartIndex
        field.setMaskingStartIndex(2);
        assertEquals("MaskingStartIndex should be set correctly", Integer.valueOf(2), field.getMaskingStartIndex());
        
        // Test maskingFromStartLength
        field.setMaskingFromStartLength(4);
        assertEquals("MaskingFromStartLength should be set correctly", Integer.valueOf(4), field.getMaskingFromStartLength());
        
        // Test maskingFromEndLength
        field.setMaskingFromEndLength(3);
        assertEquals("MaskingFromEndLength should be set correctly", Integer.valueOf(3), field.getMaskingFromEndLength());
        
        // Test isMaskedInDB
        field.setIsMaskedInDB(true);
        assertEquals("IsMaskedInDB should be set correctly", Boolean.TRUE, field.getIsMaskedInDB());
    }

    @Test
    public void testValidationMethods() {
        MaskedInputTextFieldDefinition field = new MaskedInputTextFieldDefinition();
        
        // Test valid configuration
        field.setMinLength(5);
        field.setMaxLength(20);
        field.setMaskingStartIndex(2);
        field.setMaskingFromStartLength(4);
        
        assertTrue("Min length validation should pass", field.isMinLengthValidAgainstMaxLength());
        assertTrue("Masking from start length validation should pass", field.isMaskingFromStartLengthValidAgainstMaxLength());
        
        // Test invalid min length
        field.setMinLength(25);
        assertFalse("Min length validation should fail when minLength > maxLength", field.isMinLengthValidAgainstMaxLength());
        
        // Test invalid masking from start length
        field.setMinLength(5);
        field.setMaskingFromStartLength(25);
        assertFalse("Masking from start length validation should fail when > maxLength", field.isMaskingFromStartLengthValidAgainstMaxLength());
    }

    @Test
    public void testCopyFrom() {
        MaskedInputTextFieldDefinition source = new MaskedInputTextFieldDefinition();
        source.setMaxLength(30);
        source.setPlaceHolder("Source placeholder");
        source.setMinLength(3);
        source.setMaskingCharacter("#");
        source.setMaskingStartIndex(1);
        source.setMaskingFromStartLength(2);
        source.setMaskingFromEndLength(1);
        source.setIsMaskedInDB(true);
        
        MaskedInputTextFieldDefinition target = new MaskedInputTextFieldDefinition();
        target.doCopyFrom(source);
        
        assertEquals("MaxLength should be copied", source.getMaxLength(), target.getMaxLength());
        assertEquals("PlaceHolder should be copied", source.getPlaceHolder(), target.getPlaceHolder());
        assertEquals("MinLength should be copied", source.getMinLength(), target.getMinLength());
        assertEquals("MaskingCharacter should be copied", source.getMaskingCharacter(), target.getMaskingCharacter());
        assertEquals("MaskingStartIndex should be copied", source.getMaskingStartIndex(), target.getMaskingStartIndex());
        assertEquals("MaskingFromStartLength should be copied", source.getMaskingFromStartLength(), target.getMaskingFromStartLength());
        assertEquals("MaskingFromEndLength should be copied", source.getMaskingFromEndLength(), target.getMaskingFromEndLength());
        assertEquals("IsMaskedInDB should be copied", source.getIsMaskedInDB(), target.getIsMaskedInDB());
    }

    @Test
    public void testEqualsAndHashCode() {
        MaskedInputTextFieldDefinition field1 = new MaskedInputTextFieldDefinition();
        field1.setMaxLength(25);
        field1.setMinLength(5);
        field1.setMaskingCharacter("*");
        
        MaskedInputTextFieldDefinition field2 = new MaskedInputTextFieldDefinition();
        field2.setMaxLength(25);
        field2.setMinLength(5);
        field2.setMaskingCharacter("*");
        
        MaskedInputTextFieldDefinition field3 = new MaskedInputTextFieldDefinition();
        field3.setMaxLength(30);
        field3.setMinLength(5);
        field3.setMaskingCharacter("*");
        
        assertTrue("Equal fields should be equal", field1.equals(field2));
        assertTrue("Equal fields should have same hashCode", field1.hashCode() == field2.hashCode());
        assertFalse("Different fields should not be equal", field1.equals(field3));
    }
}
