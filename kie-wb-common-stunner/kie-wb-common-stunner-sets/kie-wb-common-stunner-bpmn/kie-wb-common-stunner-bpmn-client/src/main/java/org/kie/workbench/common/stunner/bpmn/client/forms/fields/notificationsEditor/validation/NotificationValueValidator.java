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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.google.common.collect.ImmutableMap;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.validation.client.GwtValidation;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Expiration;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.NotificationRow;

import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation.ExpirationTypeOracle.ISO_DATE_TIME;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation.ExpirationTypeOracle.ONE_TIME_EXECUTION;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation.ExpirationTypeOracle.PERIOD;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation.ExpirationTypeOracle.REPEATABLE;

@GwtValidation(NotificationRow.class)
public class NotificationValueValidator implements ConstraintValidator<ValidNotificationValue, NotificationRow> {

    public static final String WRONG_EXPIRES_AT_EXPRESSION = "Expression is not valid";

    static BiFunction<String, String, Optional<MatchResult>> checkIfPatternMatch = (pattern, maybeIso) -> {
        MatchResult result = RegExp.compile(pattern).exec(maybeIso);
        return result != null ? Optional.of(result) : Optional.empty();
    };

    private Predicate<String> lessThenMaxInteger = iso -> {
        try {
            Integer.valueOf(iso);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    };

    private Predicate<String> checkIfValueIsNotEmptyOrNegative = iso -> lessThenMaxInteger.test(iso) && Integer.parseInt(iso) > 0;

    private Predicate<String> checkIfValueRepeatable = iso -> iso.isEmpty() || checkIfValueIsNotEmptyOrNegative.test(iso);
    //Note: "P1M" is a one-month duration and "PT1M" is a one-minute duration;
    private BiFunction<String, String, Boolean> checkIfValueIsMinuteOrMonth = (t, m)
            -> ((!m.equals("M") && !t.isEmpty()) || (t.isEmpty() || t.equals("T")) && m.equals("M")) ? true : false;

    private Predicate<String> isTimePeriodExpressionWithRepeatableSection = (maybeIso) -> {
        Optional<MatchResult> result = checkIfPatternMatch.apply(REPEATABLE + "/" + PERIOD, maybeIso);
        if (result.isPresent()) {
            if (checkIfValueRepeatable.test(result.get().getGroup(1))
                    && checkIfValueIsNotEmptyOrNegative.test(result.get().getGroup(3))
                    && checkIfValueIsMinuteOrMonth.apply(result.get().getGroup(2), result.get().getGroup(4))) {
                return true;
            }
        }
        return false;
    };

    private Predicate<String> isTimePeriodExpression = (maybeIso) -> {
        Optional<MatchResult> result = checkIfPatternMatch.apply("^" + PERIOD, maybeIso);
        if (result.isPresent()) {
            if (checkIfValueIsNotEmptyOrNegative.test(result.get().getGroup(2))
                    && checkIfValueIsMinuteOrMonth.apply(result.get()
                                                                 .getGroup(1), result.get()
                    .getGroup(3))) {
                return true;
            }
        }
        return false;
    };

    private Predicate<String> isOneTimeExecution = (maybeIso) -> {
        Optional<MatchResult> result = checkIfPatternMatch.apply(ONE_TIME_EXECUTION, maybeIso);
        if (result.isPresent()) {
            return checkIfValueIsNotEmptyOrNegative.test(result.get().getGroup(1));
        }
        return false;
    };

    private Predicate<String> isValidRepeatableExpression = (maybeIso) ->
            isTimePeriodExpressionWithRepeatableSection.or(isTimePeriodExpression).or(isOneTimeExecution).test(maybeIso);

    private Predicate<String> isRepeatableDataTimeExpression = (maybeIso) -> {
        Optional<MatchResult> result = checkIfPatternMatch.apply(REPEATABLE + "/" + ISO_DATE_TIME + "/" + PERIOD, maybeIso);
        if (result.isPresent()) {
            if (checkIfValueRepeatable.test(result.get().getGroup(1))
                    && checkIfValueIsMinuteOrMonth.apply(result.get().getGroup(5), result.get().getGroup(7))
                    && checkIfValueIsNotEmptyOrNegative.test(result.get().getGroup(6))) {
                return true;
            }
        }
        return false;
    };

    private Predicate<String> isDataTimeExpression = (maybeIso)
            -> checkIfPatternMatch.apply("^" + ISO_DATE_TIME, maybeIso).isPresent();

    private Predicate<String> isValidDataTimeExpression = (maybeIso)
            -> isRepeatableDataTimeExpression.or(isDataTimeExpression).test(maybeIso);

    public Map<Expiration, Predicate> validators = ImmutableMap.of(
            Expiration.TIMEPERIOD, isValidRepeatableExpression,
            Expiration.DATATIME, isValidDataTimeExpression,
            Expiration.EXPRESSION, isValidRepeatableExpression);

    private Predicate getValidator(Expiration expiration) {
        return validators.get(expiration);
    }

    @Override
    public void initialize(ValidNotificationValue constraintAnnotation) {

    }

    @Override
    public boolean isValid(NotificationRow value, ConstraintValidatorContext context) {
        if (!isValid(value)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(WRONG_EXPIRES_AT_EXPRESSION)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    public boolean isValid(NotificationRow value) {
        return getValidator(new ExpirationTypeOracle()
                                    .guess(value.getExpiresAt()))
                .test(value.getExpiresAt());
    }
}