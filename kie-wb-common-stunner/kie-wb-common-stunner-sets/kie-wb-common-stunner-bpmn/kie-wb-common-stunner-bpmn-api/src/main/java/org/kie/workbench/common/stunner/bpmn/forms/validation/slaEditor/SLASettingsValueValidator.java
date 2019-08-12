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

import com.google.gwt.regexp.shared.RegExp;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;

public class SLASettingsValueValidator
        implements ConstraintValidator<ValidSLASettingsValue, SLADueDate> {

    public static final String TimeDurationInvalid = "The SLA Due Date must be a valid ISO-8601 duration or an expression like #{expression}.";

    public static final String ISO = "none";

    /**
     * ISO8601-Duration
     */
    private static final String ISO_DURATION = "P(?:\\d+(?:\\.\\d+)?D)?(?:T(?:\\d+(?:\\.\\d+)?H)?(?:\\d+(?:\\.\\d+)?M)?(?:\\d+(?:\\.\\d+)?S)?)?";

    private static final String EXPRESSION = "#{(.+)}";

    private static final RegExp durationExpr = RegExp.compile("^" + ISO_DURATION + "$");

   // private static final RegExp expressionExpr = RegExp.compile("^" + EXPRESSION + "$");


    public void initialize(ValidSLASettingsValue constraintAnnotation) {
    }

    @Override
    public boolean isValid(SLADueDate timerSettings,
                           ConstraintValidatorContext constraintValidatorContext) {
        String value;
        String errorMessage = null;

        if (timerSettings != null && hasSomething(timerSettings.getValue())) {
            value = timerSettings.getValue();
            if ((looksLikeExpression(value) && !isValidExpression(value)) ||
                    (!looksLikeExpression(value) && !isValidDuration(value))) {
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

    private static boolean looksLikeExpression(final String value) {
        return hasSomething(value) && (value.startsWith("#{") || value.contains("{") || value.contains("}"));
    }

    private static boolean isValidExpression(final String value) {
        return hasSomething(value) && RegExp.compile("^" + EXPRESSION + "$").test(value) && value.length() > 3;
    }

    private static boolean isValidDuration(final String value) {
        return hasSomething(value) && durationExpr.test(value);
    }

    private static boolean hasSomething(final String value) {
        return value != null && !value.trim().isEmpty();
    }
}