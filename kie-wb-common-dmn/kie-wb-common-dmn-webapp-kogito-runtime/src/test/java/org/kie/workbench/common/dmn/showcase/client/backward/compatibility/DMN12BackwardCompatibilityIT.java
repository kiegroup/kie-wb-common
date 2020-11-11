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

public class DMN12BackwardCompatibilityIT extends DMNDesignerBaseIT {

    @Test
    public void testDMN12FilterDMN() throws Exception {
        executeDMNTestCase("dmn12", "0001-filter.dmn");
    }

    @Test
    public void testDMN12InputDataStringDMN() throws Exception {
        executeDMNTestCase("dmn12", "0001-input-data-string.dmn");
    }

    @Test
    public void testDMN12InputDataNumberDMN() throws Exception {
        executeDMNTestCase("dmn12", "0002-input-data-number.dmn");
    }

    @Test
    public void testDMN12StringFunctionsDMN() throws Exception {
        executeDMNTestCase("dmn12", "0002-string-functions.dmn");
    }

    @Test
    public void testDMN12InputDataStringAllowedValuesDMN() throws Exception {
        executeDMNTestCase("dmn12", "0003-input-data-string-allowed-values.dmn");
    }

    @Test
    public void testDMN12IterationDMN() throws Exception {
        executeDMNTestCase("dmn12", "0003-iteration.dmn");
    }

    @Test
    public void testDMN12LendingDMN() throws Exception {
        executeDMNTestCase("dmn12", "0004-lending.dmn");
    }

    @Test
    public void testDMN12SimpletableUDMN() throws Exception {
        executeDMNTestCase("dmn12", "0004-simpletable-U.dmn");
    }

    @Test
    public void testDMN12LiteralInvocationDMN() throws Exception {
        executeDMNTestCase("dmn12", "0005-literal-invocation.dmn");
    }

    @Test
    public void testDMN12SimpletableADMN() throws Exception {
        executeDMNTestCase("dmn12", "0005-simpletable-A.dmn");
    }

    @Test
    public void testDMN12JoinDMN() throws Exception {
        executeDMNTestCase("dmn12", "0006-join.dmn");
    }

    @Test
    public void testDMN12SimpletableP1DMN() throws Exception {
        executeDMNTestCase("dmn12", "0006-simpletable-P1.dmn");
    }

    @Test
    public void testDMN12DateTimeDMN() throws Exception {
        executeDMNTestCase("dmn12", "0007-date-time.dmn");
    }

    @Test
    public void testDMN12SimpletableP2DMN() throws Exception {
        executeDMNTestCase("dmn12", "0007-simpletable-P2.dmn");
    }

    @Test
    public void testDMN12ListGenDMN() throws Exception {
        executeDMNTestCase("dmn12", "0008-listGen.dmn");
    }

    @Test
    public void testDMN12LXArithmeticDMN() throws Exception {
        executeDMNTestCase("dmn12", "0008-LX-arithmetic.dmn");
    }

    @Test
    public void testDMN12AppendFlattenDMN() throws Exception {
        executeDMNTestCase("dmn12", "0009-append-flatten.dmn");
    }

    @Test
    public void testDMN12InvocationArithmeticDMN() throws Exception {
        executeDMNTestCase("dmn12", "0009-invocation-arithmetic.dmn");
    }

    @Test
    public void testDMN12ConcatenateDMN() throws Exception {
        executeDMNTestCase("dmn12", "0010-concatenate.dmn");
    }

    @Test
    public void testDMN12MultiOutputUDMN() throws Exception {
        executeDMNTestCase("dmn12", "0010-multi-output-U.dmn");
    }

    @Test
    public void testDMN12InsertRemoveDMN() throws Exception {
        executeDMNTestCase("dmn12", "0011-insert-remove.dmn");
    }

    @Test
    public void testDMN12ListFunctionsDMN() throws Exception {
        executeDMNTestCase("dmn12", "0012-list-functions.dmn");
    }

    @Test
    public void testDMN12SortDMN() throws Exception {
        executeDMNTestCase("dmn12", "0013-sort.dmn");
    }

    @Test
    public void testDMN12LoanComparisonDMN() throws Exception {
        executeDMNTestCase("dmn12", "0014-loan-comparison.dmn");
    }

    @Test
    public void testDMN12SomeEveryDMN() throws Exception {
        executeDMNTestCase("dmn12", "0016-some-every.dmn");
    }

    @Test
    public void testDMN12TableTestsDMN() throws Exception {
        executeDMNTestCase("dmn12", "0017-tableTests.dmn");
    }

    @Test
    public void testDMN12VacationDaysDMN() throws Exception {
        executeDMNTestCase("dmn12", "0020-vacation-days.dmn");
    }

    @Test
    public void testDMN12SingletonListDMN() throws Exception {
        executeDMNTestCase("dmn12", "0021-singleton-list.dmn");
    }

    @Test
    public void testDMN12UserDefinedFunctions1DMN() throws Exception {
        executeDMNTestCase("dmn12", "0030-user-defined-functions.dmn");
    }

    @Test
    public void testDMN12UserDefinedFunctions2DMN() throws Exception {
        executeDMNTestCase("dmn12", "0031-user-defined-functions.dmn");
    }

    @Test
    public void testDMN12ConditionalsDMN() throws Exception {
        executeDMNTestCase("dmn12", "0032-conditionals.dmn");
    }

    @Test
    public void testDMN12ForLoopsDMN() throws Exception {
        executeDMNTestCase("dmn12", "0033-for-loops.dmn");
    }

    @Test
    public void testDMN12DrgScopesDMN() throws Exception {
        executeDMNTestCase("dmn12", "0034-drg-scopes.dmn");
    }

    @Test
    public void testDMN12TestStructureOutputDMN() throws Exception {
        executeDMNTestCase("dmn12", "0035-test-structure-output.dmn");
    }

    @Test
    public void testDMN12DtVariableInputDMN() throws Exception {
        executeDMNTestCase("dmn12", "0036-dt-variable-input.dmn");
    }

    @Test
    public void testDMN12DtOnBkmImplicitParamsDMN() throws Exception {
        executeDMNTestCase("dmn12", "0037-dt-on-bkm-implicit-params.dmn");
    }

    @Test
    public void testDMN12DtOnBkmExplicitParamsDMN() throws Exception {
        executeDMNTestCase("dmn12", "0038-dt-on-bkm-explicit-params.dmn");
    }

    @Test
    public void testDMN12DtListSemanticsDMN() throws Exception {
        executeDMNTestCase("dmn12", "0039-dt-list-semantics.dmn");
    }

    @Test
    public void testDMN12SinglenestedcontextDMN() throws Exception {
        executeDMNTestCase("dmn12", "0040-singlenestedcontext.dmn");
    }

    @Test
    public void testDMN12MultipleNestedcontextDMN() throws Exception {
        executeDMNTestCase("dmn12", "0041-multiple-nestedcontext.dmn");
    }

    @Test
    public void testDMN12FeelConstantsDMN1() throws Exception {
        executeDMNTestCase("dmn12", "0100-feel-constants.dmn");
    }

    @Test
    public void testDMN12FeelConstantsDMN2() throws Exception {
        executeDMNTestCase("dmn12", "0101-feel-constants.dmn");
    }

    @Test
    public void testDMN12FeelConstantsDMN3() throws Exception {
        executeDMNTestCase("dmn12", "0102-feel-constants.dmn");
    }

    @Test
    public void testDMN12FeelMathDMN() throws Exception {
        executeDMNTestCase("dmn12", "0105-feel-math.dmn");
    }

    @Test
    public void testDMN12FeelTernaryLogicDMN() throws Exception {
        executeDMNTestCase("dmn12", "0106-feel-ternary-logic.dmn");
    }

    @Test
    public void testDMN12FeelTernaryLogicNotDMN() throws Exception {
        executeDMNTestCase("dmn12", "0107-feel-ternary-logic-not.dmn");
    }

    @Test
    public void testDMN12FirstHitpolicyDMN() throws Exception {
        executeDMNTestCase("dmn12", "0108-first-hitpolicy.dmn");
    }

    @Test
    public void testDMN12RuleOrderHitpolicyDMN() throws Exception {
        executeDMNTestCase("dmn12", "0109-ruleOrder-hitpolicy.dmn");
    }

    @Test
    public void testDMN12OutputOrderHitpolicyDMN() throws Exception {
        executeDMNTestCase("dmn12", "0110-outputOrder-hitpolicy.dmn");
    }

    @Test
    public void testDMN12FirstHitpolicySingleoutputcolDMN() throws Exception {
        executeDMNTestCase("dmn12", "0111-first-hitpolicy-singleoutputcol.dmn");
    }

    @Test
    public void testDMN12RuleOrderHitpolicySingleinoutcolDMN() throws Exception {
        executeDMNTestCase("dmn12", "0112-ruleOrder-hitpolicy-singleinoutcol.dmn");
    }

    @Test
    public void testDMN12OutputOrderHitpolicySingleinoutcolDMN() throws Exception {
        executeDMNTestCase("dmn12", "0113-outputOrder-hitpolicy-singleinoutcol.dmn");
    }

    @Test
    public void testDMN12MinCollectHitpolicyDMN() throws Exception {
        executeDMNTestCase("dmn12", "0114-min-collect-hitpolicy.dmn");
    }

    @Test
    public void testDMN12SumCollectHitpolicyDMN() throws Exception {
        executeDMNTestCase("dmn12", "0115-sum-collect-hitpolicy.dmn");
    }

    @Test
    public void testDMN12CountCollectHitpolicyDMN() throws Exception {
        executeDMNTestCase("dmn12", "0116-count-collect-hitpolicy.dmn");
    }

    @Test
    public void testDMN12MultiAnyHitpolicyDMN() throws Exception {
        executeDMNTestCase("dmn12", "0117-multi-any-hitpolicy.dmn");
    }

    @Test
    public void testDMN12MultiPriorityHitpolicyDMN() throws Exception {
        executeDMNTestCase("dmn12", "0118-multi-priority-hitpolicy.dmn");
    }

    @Test
    public void testDMN12MultiCollectHitpolicyDMN() throws Exception {
        executeDMNTestCase("dmn12", "0119-multi-collect-hitpolicy.dmn");
    }

    @Test
    public void testDMN12FeelDecimalFunctionDMN() throws Exception {
        executeDMNTestCase("dmn12", "1100-feel-decimal-function.dmn");
    }

    @Test
    public void testDMN12FeelFloorFunctionDMN() throws Exception {
        executeDMNTestCase("dmn12", "1101-feel-floor-function.dmn");
    }

    @Test
    public void testDMN12FeelCeilingFunctionDMN() throws Exception {
        executeDMNTestCase("dmn12", "1102-feel-ceiling-function.dmn");
    }

    @Test
    public void testDMN12FeelSubstringFunctionDMN() throws Exception {
        executeDMNTestCase("dmn12", "1103-feel-substring-function.dmn");
    }

    @Test
    public void testDMN12FeelStringLengthFunctionDMN() throws Exception {
        executeDMNTestCase("dmn12", "1104-feel-string-length-function.dmn");
    }

    @Test
    public void testDMN12FeelUpperCaseFunctionDMN() throws Exception {
        executeDMNTestCase("dmn12", "1105-feel-upper-case-function.dmn");
    }

    @Test
    public void testDMN12FeelLowerCaseFunctionDMN() throws Exception {
        executeDMNTestCase("dmn12", "1106-feel-lower-case-function.dmn");
    }

    @Test
    public void testDMN12FeelSubstringBeforeFunctionDMN() throws Exception {
        executeDMNTestCase("dmn12", "1107-feel-substring-before-function.dmn");
    }

    @Test
    public void testDMN12FeelSubstringAfterFunctionDMN() throws Exception {
        executeDMNTestCase("dmn12", "1108-feel-substring-after-function.dmn");
    }

    @Test
    public void testDMN12FeelReplaceFunctionDMN() throws Exception {
        executeDMNTestCase("dmn12", "1109-feel-replace-function.dmn");
    }

    @Test
    public void testDMN12FeelContainsFunctionDMN() throws Exception {
        executeDMNTestCase("dmn12", "1110-feel-contains-function.dmn");
    }

    @Test
    public void testDMN12FeelDateFunctionDMN() throws Exception {
        executeDMNTestCase("dmn12", "1115-feel-date-function.dmn");
    }

    @Test
    public void testDMN12FeelTimeFunctionDMN() throws Exception {
        executeDMNTestCase("dmn12", "1116-feel-time-function.dmn");
    }

    @Test
    public void testDMN12FeelDateAndTimeFunctionDMN() throws Exception {
        executeDMNTestCase("dmn12", "1117-feel-date-and-time-function.dmn");
    }

    @Test
    public void testDMN12FeelDurationFunctionDMN() throws Exception {
        executeDMNTestCase("dmn12", "1120-feel-duration-function.dmn");
    }

    @Test
    public void testDMN12FeelYearsAndMonthsDurationFunctionDMN() throws Exception {
        executeDMNTestCase("dmn12", "1121-feel-years-and-months-duration-function.dmn");
    }
}
