/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.forms.validation.process;

import javax.validation.ConstraintValidatorContext;

import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProcessIdValueValidatorTest {

    ProcessIdValueValidator validator = new ProcessIdValueValidator();

    @Mock
    ConstraintValidatorContext context;

    @Test
    public void testNullOrEmpty() {
        assertFalse(validator.isValid(null, context));
        assertFalse(validator.isValid("", context));
    }

    @Test
    public void testIncorrectStart() {
        assertFalse(validator.isValid("213", context));
    }

    @Test
    public void testIncorrectSymbol() {
        assertFalse(validator.isValid("_burbur_£", context));
    }

    @Test
    public void testCorrectName() {
        assertTrue(validator.isValid("_valid", context));
        assertTrue(validator.isValid("_123", context));
        assertTrue(validator.isValid("_", context));
        assertTrue(validator.isValid("abc", context));
    }
}
