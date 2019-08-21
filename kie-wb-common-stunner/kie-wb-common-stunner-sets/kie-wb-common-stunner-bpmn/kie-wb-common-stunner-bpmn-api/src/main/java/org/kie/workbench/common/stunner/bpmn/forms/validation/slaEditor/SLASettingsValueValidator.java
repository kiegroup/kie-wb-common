/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.forms.validation.slaEditor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.forms.validation.timerEditor.TimerSettingsValueValidator;

public class SLASettingsValueValidator
        implements ConstraintValidator<ValidSLASettingsValue, SLADueDate> {

    public static final String TimeDurationInvalid = "The SLA Due Date must be a valid ISO-8601 duration or an expression like #{expression}.";


    public void initialize(ValidSLASettingsValue constraintAnnotation) {
    }

    @Override
    public boolean isValid(SLADueDate slaDueDate,
                           ConstraintValidatorContext constraintValidatorContext) {
        String value;
        String errorMessage = null;

        if (slaDueDate != null && TimerSettingsValueValidator.hasSomething(slaDueDate.getValue())) {
            value = slaDueDate.getValue();
            final boolean looksLikeExpression = TimerSettingsValueValidator.looksLikeExpression(value);
            if ((looksLikeExpression && !TimerSettingsValueValidator.isValidExpression(value)) ||
                    (!looksLikeExpression && !TimerSettingsValueValidator.isValidDuration(value))) {
                errorMessage = TimeDurationInvalid;
            }
        }

        if (errorMessage != null) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

}