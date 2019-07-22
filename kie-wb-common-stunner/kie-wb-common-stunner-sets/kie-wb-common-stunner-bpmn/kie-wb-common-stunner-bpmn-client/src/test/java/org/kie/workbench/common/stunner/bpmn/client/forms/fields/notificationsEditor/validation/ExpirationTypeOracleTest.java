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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Expiration;

import static org.junit.Assert.assertEquals;

@RunWith(GwtMockitoTestRunner.class)
public class ExpirationTypeOracleTest {

    private ExpirationTypeOracle oracle;

    @Before
    public void setUp() throws Exception {
        oracle = new ExpirationTypeOracle();
    }

    @Test
    public void testEmptyNotificationRow() {
        Expiration result = oracle.guess("1d");
        assertEquals(Expiration.EXPRESSION, result);
    }

    @Test
    public void testISO8601DataTimeRepeatableValue() {
        Expiration result = oracle.guess("R/2019-07-14T13:34-02/PT33M");
        assertEquals(Expiration.DATATIME, result);
    }

    @Test
    public void testISO8601WithTZ02ZRepeatable1AndPeriodValue() {
        Expiration result = oracle.guess("R1/2019-07-14T13:34-02/PT33M");
        assertEquals(Expiration.DATATIME, result);
    }

    @Test
    public void testISO8601WithTZ00ZRepeatable1AndPeriodAndTZ02Value() {
        Expiration result = oracle.guess("R1/2019-07-14T13:34-02/PT33D");
        assertEquals(Expiration.DATATIME, result);
    }

    @Test
    public void testISO8601WithTZ00ZRepeatable10AndPeriodAndTZ0230Value() {
        Expiration result = oracle.guess("R10/2019-07-14T13:34+02:30/P33M");
        assertEquals(Expiration.DATATIME, result);
    }

    @Test
    public void testISO8601WithTZ02RepeatableValue() {
        Expiration result = oracle.guess("2019-07-14T13:34-02");
        assertEquals(Expiration.DATATIME, result);
    }

    @Test
    public void testISO8601DataTimeValue() {
        Expiration result = oracle.guess("2019-07-14T13:34-02");
        assertEquals(Expiration.DATATIME, result);
    }

    @Test
    public void testISO8601RepeatableValue() {
        Expiration result = oracle.guess("R/PT33M");
        assertEquals(Expiration.TIMEPERIOD, result);
    }

    @Test
    public void testMonthRepeatableUntilStateChangesNotification() {
        Expiration result = oracle.guess("R/P33M");
        assertEquals(Expiration.TIMEPERIOD, result);
    }

    @Test
    public void testNotificationAndTZ0245() {
        Expiration result = oracle.guess("2019-07-14T13:34-02:45");
        assertEquals(Expiration.DATATIME, result);
    }

    @Test
    public void testNotificationAndTZ002() {
        Expiration result = oracle.guess("2019-07-14T13:34:00Z");
        assertEquals(Expiration.DATATIME, result);
    }

    @Test
    public void test1DigExpiresAtNotificationRow() {
        Expiration result = oracle.guess("1d");
        assertEquals(Expiration.EXPRESSION, result);
    }

    @Test
    public void testDataTimeExpiresAtNotificationRow() {
        Expiration result = oracle.guess("R19/2019-07-24T19:00+05/PT1H");
        assertEquals(Expiration.DATATIME, result);
    }
}
