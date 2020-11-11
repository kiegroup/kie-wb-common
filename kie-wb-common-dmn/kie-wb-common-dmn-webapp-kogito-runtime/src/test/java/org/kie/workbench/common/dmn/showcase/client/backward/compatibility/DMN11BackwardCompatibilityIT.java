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

package org.kie.workbench.common.dmn.showcase.client.backward.compatibility;

import org.junit.Test;
import org.kie.workbench.common.dmn.showcase.client.common.DMNDesignerBaseIT;

public class DMN11BackwardCompatibilityIT extends DMNDesignerBaseIT {

    @Test
    public void testDMN11FilterDMN() throws Exception {
        executeDMNTestCase("dmn11", "0001-filter.dmn");
    }

    @Test
    public void testDMN11InputDataStringDMN() throws Exception {
        executeDMNTestCase("dmn11", "0001-input-data-string.dmn");
    }

    @Test
    public void testDMN11InputDataNumberDMN() throws Exception {
        executeDMNTestCase("dmn11", "0002-input-data-number.dmn");
    }

    @Test
    public void testDMN11StringFunctionsDMN() throws Exception {
        executeDMNTestCase("dmn11", "0002-string-functions.dmn");
    }

    @Test
    public void testDMN11InputDataStringAllowedValuesDMN() throws Exception {
        executeDMNTestCase("dmn11", "0003-input-data-string-allowed-values.dmn");
    }

    @Test
    public void testDMN11IterationDMN() throws Exception {
        executeDMNTestCase("dmn11", "0003-iteration.dmn");
    }

    @Test
    public void testDMN11LendingDMN() throws Exception {
        executeDMNTestCase("dmn11", "0004-lending.dmn");
    }

    @Test
    public void testDMN11SimpletableUDMN() throws Exception {
        executeDMNTestCase("dmn11", "0004-simpletable-U.dmn");
    }

    @Test
    public void testDMN11LiteralInvocationDMN() throws Exception {
        executeDMNTestCase("dmn11", "0005-literal-invocation.dmn");
    }

    @Test
    public void testDMN11SimpletableADMN() throws Exception {
        executeDMNTestCase("dmn11", "0005-simpletable-A.dmn");
    }

    @Test
    public void testDMN11JoinDMN() throws Exception {
        executeDMNTestCase("dmn11", "0006-join.dmn");
    }

    @Test
    public void testDMN11SimpletableP1DMN() throws Exception {
        executeDMNTestCase("dmn11", "0006-simpletable-P1.dmn");
    }

    @Test
    public void testDMN11DateTimeDMN() throws Exception {
        executeDMNTestCase("dmn11", "0007-date-time.dmn");
    }

    @Test
    public void testDMN11SimpletableP2DMN() throws Exception {
        executeDMNTestCase("dmn11", "0007-simpletable-P2.dmn");
    }

    @Test
    public void testDMN11ListGenDMN() throws Exception {
        executeDMNTestCase("dmn11", "0008-listGen.dmn");
    }

    @Test
    public void testDMN11LXArithmeticDMN() throws Exception {
        executeDMNTestCase("dmn11", "0008-LX-arithmetic.dmn");
    }

    @Test
    public void testDMN11AppendFlattenDMN() throws Exception {
        executeDMNTestCase("dmn11", "0009-append-flatten.dmn");
    }

    @Test
    public void testDMN11InvocationArithmeticDMN() throws Exception {
        executeDMNTestCase("dmn11", "0009-invocation-arithmetic.dmn");
    }

    @Test
    public void testDMN11ConcatenateDMN() throws Exception {
        executeDMNTestCase("dmn11", "0010-concatenate.dmn");
    }

    @Test
    public void testDMN11MultiOutputUDMN() throws Exception {
        executeDMNTestCase("dmn11", "0010-multi-output-U.dmn");
    }

    @Test
    public void testDMN11InsertRemoveDMN() throws Exception {
        executeDMNTestCase("dmn11", "0011-insert-remove.dmn");
    }

    @Test
    public void testDMN11ListFunctionsDMN() throws Exception {
        executeDMNTestCase("dmn11", "0012-list-functions.dmn");
    }

    @Test
    public void testDMN11SortDMN() throws Exception {
        executeDMNTestCase("dmn11", "0013-sort.dmn");
    }

    @Test
    public void testDMN11LoanComparisonDMN() throws Exception {
        executeDMNTestCase("dmn11", "0014-loan-comparison.dmn");
    }

    @Test
    public void testDMN11AllAnyDMN() throws Exception {
        executeDMNTestCase("dmn11", "0015-all-any.dmn");
    }

    @Test
    public void testDMN11SomeVeryDMN() throws Exception {
        executeDMNTestCase("dmn11", "0016-some-every.dmn");
    }

    @Test
    public void testDMN11TableTestsDMN() throws Exception {
        executeDMNTestCase("dmn11", "0017-tableTests.dmn");
    }

    @Test
    public void testDMN11FlightRebookingDMN() throws Exception {
        executeDMNTestCase("dmn11", "0019-flight-rebooking.dmn");
    }
}
