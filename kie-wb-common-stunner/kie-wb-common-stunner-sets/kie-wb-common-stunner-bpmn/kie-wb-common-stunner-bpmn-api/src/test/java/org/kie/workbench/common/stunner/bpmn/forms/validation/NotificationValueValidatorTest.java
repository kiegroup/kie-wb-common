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

package org.kie.workbench.common.stunner.bpmn.forms.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;

import com.google.gwt.junit.client.GWTTestCase;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationValue;
import org.kie.workbench.common.stunner.bpmn.forms.validation.notification.NotificationValueValidator;

public class NotificationValueValidatorTest extends GWTTestCase {

    private NotificationValueValidator validator;

    private ConstraintValidatorContext context;

    private List<String> errorMessages = new ArrayList<>();

    @Override
    public String getModuleName() {
        return "org.kie.workbench.common.stunner.bpmn.forms.validation.NotificationValueValidatorTest";
    }

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();
        validator = new NotificationValueValidator();
        context = new ConstraintValidatorContext() {
            @Override
            public void disableDefaultConstraintViolation() {
            }

            @Override
            public String getDefaultConstraintMessageTemplate() {
                return null;
            }

            @Override
            public ConstraintViolationBuilder buildConstraintViolationWithTemplate(String message) {
                errorMessages.add(message);
                return new ConstraintViolationBuilder() {
                    @Override
                    public NodeBuilderDefinedContext addNode(String name) {
                        return null;
                    }

                    @Override
                    public ConstraintValidatorContext addConstraintViolation() {
                        return context;
                    }
                };
            }
        };
    }

    @Test
    public void testEmptyNotificationValue() {
        boolean result = validator.isValid(new NotificationValue(), context);
        assertFalse(result);
        assertFalse(errorMessages.isEmpty());
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testISO8601DataTimeRepeatableValue() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R/2019-07-14T13:34-02/PT33M");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testISO8601WithTZ02ZRepeatable1AndPeriodValue() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R1/2019-07-14T13:34-02/PT33M");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testISO8601WithTZ02ZRepeatable1AndPeriodTooBigValue() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R1/2019-07-14T13:34-02/PT3333333333333333333M");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testISO8601WithTZ02ZRepeatableTooBigValue() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R13333333333333333/2019-07-14T13:34-02/PT33M");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testISO8601WithTZ02Repeatable1AndPeriodAndWrongYearValue() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R1/1919-07-14T13:34-02/PT33M");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testISO8601WithTZ00ZRepeatable1AndPeriodAndWrongTZ002Value() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R1/1919-07-14T13:34-002/PT33M");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testISO8601WithTZ00ZRepeatable1AndPeriodAndTZ02Value() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R1/2019-07-14T13:34-02/P33M");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testISO8601WithTZ00ZRepeatable10AndPeriodAndTZ0230Value() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R10/2019-07-14T13:34+02:30/P33M");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testISO8601WithTZ00ZRepeatable10AndPeriodValue() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R10/2019-07-14T13:34:00Z/PT33M");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testISO8601WithTZ00ZRepeatableUntilStateChangesAndPeriodValue() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R/2019-07-14T13:34:00Z/PT33M");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testISO8601WithTZ00ZRepeatableUntilStateChangesValue() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R/2019-07-14T13:34:00Z/PT33M");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testISO8601WrongWithTZ00ZRepeatableUntilStateChangesValue() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R/1819-07-14T13:34:00Z/PT33M");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testISO8601WrongWithTZ00ZRepeatableUntilStateChangesAndPeriodZeroValue() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R/2019-07-14T13:34:00Z/PT0M");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testISO8601WrongWithTZ00ZRepeatableZeroUntilStateChangesAndPeriodZeroValue() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R0/2019-07-14T13:34:00Z/PT0M");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testISO8601WrongWithTZ00ZRepeatableZeroUntilStateChangesValue() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R0/2019-07-14T13:34:00Z/PT22M");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testISO8601WithTZ02RepeatableValue() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("2019-07-14T13:34-02");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testISO8601DataTimeValue() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("2019-07-14T13:34-02");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testISO8601WithWrongDelimiterRepeatableValue() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("2019-07-14W13:34-02");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testISO8601WithWrongYearRepeatableValue() {
        NotificationValue notification = new NotificationValue();
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testISO8601WithWrongYearAndTZRepeatableValue() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("1819-07-14T13:34-022");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testISO8601WithTZ00ZRepeatableValue() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("2019-07-14T13:34:00Z");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testISO8601WithTZ0245RepeatableValue() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("2019-07-14T13:34-02:45");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testISO8601RepeatableValue() {
        NotificationValue notification = new NotificationValue();

        notification.setExpiresAt("R/PT33M");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testMonthRepeatableUntilStateChangesNotification() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R/P33M");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testDayRepeatableUntilStateChangesNotification() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R/PT33D");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testWrongYearRepeatableUntilStateChangesNotification() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R/PT33Y");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testWrongHourRepeatableUntilStateChangesNotification() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R/PT33H");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testWrongDayRepeatableUntilNotification() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R/P33D");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testWrongYearRepeatableUntilNotification() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R/P33Y");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testWrongHourRepeatableUntilNotification() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R/P33H");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testWrongTimesZeroRepeatableUntilNotification() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R/PT0H");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testWrongTimesZeroRepeatable1UntilNotification() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R1/PT0H");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testWrongPeriodNotification() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("PT0H");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testWrongTimesZeroRepeatableZeroNotification() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R0/PT0H");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testWrongDayRepeatableNotification() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R44/P33D");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testWrongYearRepeatableNotification() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R44/P33Y");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testWrongHourRepeatableNotification() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R44/P33H");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testWrongHourRepeatableZeroNotification() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("R0/P33H");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testWrongYearNotificationAndTZ02() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("1819-07-14T13:34-02");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testWrongNotificationAndTZ022() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("1819-07-14T13:34-022");
        boolean result = validator.isValid(notification, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testNotificationAndTZ0245() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("2019-07-14T13:34-02:45");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testNotificationAndTZ002() {
        NotificationValue notification = new NotificationValue();
        notification.setExpiresAt("2019-07-14T13:34:00Z");
        boolean result = validator.isValid(notification, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testNegativeExpiresAtNotificationValue() {
        NotificationValue value = new NotificationValue();
        value.setExpiresAt("-1d");
        boolean result = validator.isValid(value, context);
        assertFalse(result);
        assertFalse(errorMessages.isEmpty());
    }

    @Test
    public void testIsTooBigExpiresAtNotificationValue() {
        NotificationValue value = new NotificationValue();
        value.setExpiresAt("1111111111111111111111111111111111111111111d");
        boolean result = validator.isValid(value, context);
        assertFalse(result);
        assertFalse(errorMessages.isEmpty());
    }

    @Test
    public void test1DigExpiresAtNotificationValue() {
        NotificationValue value = new NotificationValue();
        value.setExpiresAt("1d");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void test2DigExpiresAtNotificationValue() {
        NotificationValue value = new NotificationValue();
        value.setExpiresAt("11d");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void test3DigExpiresAtNotificationValue() {
        NotificationValue value = new NotificationValue();
        value.setExpiresAt("111d");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void test4DigExpiresAtNotificationValue() {
        NotificationValue value = new NotificationValue();
        value.setExpiresAt("1111d");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void test5DigExpiresAtNotificationValue() {
        NotificationValue value = new NotificationValue();
        value.setExpiresAt("11111d");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void test10DigExpiresAtNotificationValue() {
        NotificationValue value = new NotificationValue();
        value.setExpiresAt("1111111111d");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testZAnd10DigExpiresAtNotificationValue() {
        NotificationValue value = new NotificationValue();
        value.setExpiresAt("Z1111111111d");
        boolean result = validator.isValid(value, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.WRONG_EXPIRES_AT_EXPRESSION, errorMessages.get(0));
    }

    @Test
    public void testIntMaxExpiresAtNotificationValue() {
        NotificationValue value = new NotificationValue();
        value.setExpiresAt("2147483647d");
        boolean result = validator.isValid(value, context);
        assertTrue(result);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testIntOverflowExpiresAtNotificationValue() {
        NotificationValue value = new NotificationValue();
        value.setExpiresAt("2147483648d");
        boolean result = validator.isValid(value, context);
        assertFalse(result);
        assertFalse(errorMessages.isEmpty());
    }

    @Test
    public void testZeroExpiresAtNotificationValue() {
        NotificationValue value = new NotificationValue();
        value.setExpiresAt("0d");
        boolean result = validator.isValid(value, context);
        assertFalse(result);
        assertEquals(NotificationValueValidator.NOT_NEGATIVE, errorMessages.get(0));
    }
}
