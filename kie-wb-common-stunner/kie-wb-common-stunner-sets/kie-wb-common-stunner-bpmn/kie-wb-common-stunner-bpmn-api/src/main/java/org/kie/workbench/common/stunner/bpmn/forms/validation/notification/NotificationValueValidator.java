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

package org.kie.workbench.common.stunner.bpmn.forms.validation.notification;

import java.util.function.Function;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationValue;

public class NotificationValueValidator implements ConstraintValidator<ValidNotificationValue, NotificationValue> {

    public static final String ISO_DATA_TIME = "(2[0-9][0-9]{2}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2})([:|+|-]([0-9]{2}:[0-9]{2}|[0-9]{2}|00Z))";
    public static final String REPEATABLE = "^R([0-9]*)";
    public static final String PERIOD = "[P](T?)([0-9]*)([MHDY])";
    public static final String ONE_TIME_EXECUTION = "^(\\d+)([mhdyMHDY])";

    public static final String NOT_NEGATIVE = "The value should not be negative or zero.";

    public static final String INVALID_CHARACTERS = "Period property contains invalid characters. Only positive number can be used as the value.";

    public static final String WRONG_EXPIRES_AT_EXPRESSION = "expression is not valid";

    @Override
    public void initialize(ValidNotificationValue constraintAnnotation) {

    }

    @Override
    public boolean isValid(NotificationValue value, ConstraintValidatorContext context) {
        String errorMessage = guessExpirationType(value.getExpiresAt()).validate.apply(value.getExpiresAt());
        if (errorMessage != null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    protected Expiration guessExpirationType(String maybeIso) {
        if (maybeIso == null || maybeIso.isEmpty()) {
            return Expiration.TIMEPERIOD;
        }
        MatchResult result = RegExp.compile(REPEATABLE + "/" + ISO_DATA_TIME + "/" + PERIOD).exec(maybeIso);
        if (result != null) {
            return Expiration.DATATIME;
        }
        result = RegExp.compile(ISO_DATA_TIME).exec(maybeIso);
        if (result != null) {
            return Expiration.DATATIME;
        }
        result = RegExp.compile("^" + REPEATABLE + "/" + PERIOD).exec(maybeIso);
        if (result != null) {
            return Expiration.TIMEPERIOD;
        }
        result = RegExp.compile("^" + PERIOD).exec(maybeIso);
        if (result != null) {
            return Expiration.TIMEPERIOD;
        }
        return Expiration.EXPRESSION;
    }

    private enum Expiration {
        TIMEPERIOD(s -> isRepeatableExpression(s) ? null : WRONG_EXPIRES_AT_EXPRESSION),
        DATATIME(s -> isDataTimeExpression(s) ? null : WRONG_EXPIRES_AT_EXPRESSION),
        EXPRESSION(s -> isOneTimeExecution(s));

        Function<String, String> validate;

        Expiration(Function<String, String> validate) {
            this.validate = validate;
        }

        private static boolean isRepeatableExpression(String expiresAt) {
            MatchResult result = RegExp.compile(REPEATABLE + "/" + PERIOD).exec(expiresAt);
            if (result != null) {
                if (!checkRepeatIsValid(result.getGroup(1))
                        || !checkNotZeroOrNegative(result.getGroup(3))
                        || !minuteOrMonth(result.getGroup(2), result.getGroup(4))) {
                    return false;
                }
                return true;
            }
            result = RegExp.compile("^" + PERIOD).exec(expiresAt);
            if (result != null) {
                if (!checkNotZeroOrNegative(result.getGroup(2))
                        || !minuteOrMonth(result.getGroup(1), result.getGroup(3))) {
                    return false;
                }
                return true;
            }
            return false;
        }

        private static boolean checkRepeatIsValid(String repeat) {
            if (repeat.isEmpty()) {
                return true;
            }
            return checkNotZeroOrNegative(repeat);
        }

        //Note: "P1M" is a one-month duration and "PT1M" is a one-minute duration;
        private static boolean minuteOrMonth(String t, String m) {
            return (!t.isEmpty() || (t.isEmpty() && m.equals("M"))) ? true : false;
        }

        private static boolean isDataTimeExpression(String expiresAt) {
            MatchResult result = RegExp.compile(REPEATABLE + "/" + ISO_DATA_TIME + "/" + PERIOD).exec(expiresAt);
            if (result != null) {
                if (!checkRepeatIsValid(result.getGroup(1)) || !checkNotZeroOrNegative(result.getGroup(6))) {
                    return false;
                }
                return true;
            }
            result = RegExp.compile("^" + ISO_DATA_TIME).exec(expiresAt);
            if (result != null) {
                return true;
            }

            return false;
        }

        private static String isNegative(String value) {
            if (value != null && value.startsWith("-")) {
                return NOT_NEGATIVE;
            }
            return null;
        }

        private static String isOneTimeExecution(String expr) {
            String isNegative = isNegative(expr);
            if (isNegative != null) {
                return isNegative;
            }

            MatchResult result = RegExp.compile("^" + ONE_TIME_EXECUTION).exec(expr);
            if (result != null) {
                String isTooBig = isTooBigExpression(expr);
                if (isTooBig != null) {
                    return isTooBig;
                }
                if (!checkNotZeroOrNegative(result.getGroup(1))) {
                    return NOT_NEGATIVE;
                }
            } else {
                return WRONG_EXPIRES_AT_EXPRESSION;
            }

            return null;
        }

        private static boolean checkNotZeroOrNegative(String result) {
            if (isTooBig(result)) {
                return false;
            }
            int number = Integer.parseInt(result);

            if (number > 0) {
                return true;
            }
            return false;
        }

        private static boolean isTooBig(String value) {
            try {
                Integer.valueOf(value);
            } catch (NumberFormatException nfe) {
                return true;
            }
            return false;
        }

        private static String isTooBigExpression(String value) {
            if (value != null) {
                String duration = value.substring(0, value.length() - 1);
                if (isTooBig(duration)) {
                    return INVALID_CHARACTERS;
                }
            }
            return null;
        }
    }
}