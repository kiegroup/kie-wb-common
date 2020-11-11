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

import org.junit.Ignore;
import org.junit.Test;
import org.kie.workbench.common.dmn.showcase.client.common.DMNDesignerBaseIT;

public class DMN13BackwardCompatibilityIT extends DMNDesignerBaseIT {

    @Test
    public void testDMN13FilterDMN() throws Exception {
        executeDMNTestCase("dmn13", "0001-filter.dmn");
    }

    @Test
    public void testDMN13InputDataStringDMN() throws Exception {
        executeDMNTestCase("dmn13", "0001-input-data-string.dmn");
    }

    @Test
    public void testDMN13InputDataNumberDMN() throws Exception {
        executeDMNTestCase("dmn13", "0002-input-data-number.dmn");
    }

    @Test
    public void testDMN13StringFunctionsDMN() throws Exception {
        executeDMNTestCase("dmn13", "0002-string-functions.dmn");
    }

    @Test
    public void testDMN13InputDataStringAllowedValuesDMN() throws Exception {
        executeDMNTestCase("dmn13", "0003-input-data-string-allowed-values.dmn");
    }

    @Test
    public void testDMN13IterationDMN() throws Exception {
        executeDMNTestCase("dmn13", "0003-iteration.dmn");
    }

    @Test
    public void testDMN13LendingDMN() throws Exception {
        executeDMNTestCase("dmn13", "0004-lending.dmn");
    }

    @Test
    public void testDMN13SimpletableUDMN() throws Exception {
        executeDMNTestCase("dmn13", "0004-simpletable-U.dmn");
    }

    @Test
    public void testDMN13LiteralInvocationDMN() throws Exception {
        executeDMNTestCase("dmn13", "0005-literal-invocation.dmn");
    }

    @Test
    public void testDMN13SimpletableADMN() throws Exception {
        executeDMNTestCase("dmn13", "0005-simpletable-A.dmn");
    }

    @Test
    public void testDMN13JoinDMN() throws Exception {
        executeDMNTestCase("dmn13", "0006-join.dmn");
    }

    @Test
    public void testDMN13SimpletableP1DMN() throws Exception {
        executeDMNTestCase("dmn13", "0006-simpletable-P1.dmn");
    }

    @Test
    public void testDMN13DateTimeDMN() throws Exception {
        executeDMNTestCase("dmn13", "0007-date-time.dmn");
    }

    @Test
    public void testDMN13SimpletableP2DMN() throws Exception {
        executeDMNTestCase("dmn13", "0007-simpletable-P2.dmn");
    }

    @Test
    public void testDMN13ListGenDMN() throws Exception {
        executeDMNTestCase("dmn13", "0008-listGen.dmn");
    }

    @Test
    public void testDMN13LXArithmeticDMN() throws Exception {
        executeDMNTestCase("dmn13", "0008-LX-arithmetic.dmn");
    }

    @Test
    public void testDMN13AppendFlattenDMN() throws Exception {
        executeDMNTestCase("dmn13", "0009-append-flatten.dmn");
    }

    @Test
    public void testDMN13InvocationArithmeticDMN() throws Exception {
        executeDMNTestCase("dmn13", "0009-invocation-arithmetic.dmn");
    }

    @Test
    public void testDMN13ConcatenateDMN() throws Exception {
        executeDMNTestCase("dmn13", "0010-concatenate.dmn");
    }

    @Test
    public void testDMN13MultiOutputUDMN() throws Exception {
        executeDMNTestCase("dmn13", "0010-multi-output-U.dmn");
    }

    @Test
    public void testDMN13InsertRemoveDMN() throws Exception {
        executeDMNTestCase("dmn13", "0011-insert-remove.dmn");
    }

    @Test
    public void testDMN13ListFunctionsDMN() throws Exception {
        executeDMNTestCase("dmn13", "0012-list-functions.dmn");
    }

    @Test
    public void testDMN13SortDMN() throws Exception {
        executeDMNTestCase("dmn13", "0013-sort.dmn");
    }

    @Test
    public void testDMN13LoanComparisonDMN() throws Exception {
        executeDMNTestCase("dmn13", "0014-loan-comparison.dmn");
    }

    @Test
    public void testDMN13SomeEveryDMN() throws Exception {
        executeDMNTestCase("dmn13", "0016-some-every.dmn");
    }

    @Test
    public void testDMN13TableTestsDMN() throws Exception {
        executeDMNTestCase("dmn13", "0017-tableTests.dmn");
    }

    @Test
    public void testDMN13VacationDaysDMN() throws Exception {
        executeDMNTestCase("dmn13", "0020-vacation-days.dmn");
    }

    @Test
    public void testDMN13SingletonListDMN() throws Exception {
        executeDMNTestCase("dmn13", "0021-singleton-list.dmn");
    }

    @Test
    public void testDMN13UserDefinedFunctionsDMN1() throws Exception {
        executeDMNTestCase("dmn13", "0030-user-defined-functions.dmn");
    }

    @Test
    public void testDMN13UserDefinedFunctionsDMN2() throws Exception {
        executeDMNTestCase("dmn13", "0031-user-defined-functions.dmn");
    }

    @Test
    public void testDMN13ConditionalsDMN() throws Exception {
        executeDMNTestCase("dmn13", "0032-conditionals.dmn");
    }

    @Test
    public void testDMN13ForLoopsDMN() throws Exception {
        executeDMNTestCase("dmn13", "0033-for-loops.dmn");
    }

    @Test
    public void testDMN13DrgScopesDMN() throws Exception {
        executeDMNTestCase("dmn13", "0034-drg-scopes.dmn");
    }

    @Test
    public void testDMN13TestStructureOutputDMN() throws Exception {
        executeDMNTestCase("dmn13", "0035-test-structure-output.dmn");
    }

    @Test
    public void testDMN13DtVariableInputDMN() throws Exception {
        executeDMNTestCase("dmn13", "0036-dt-variable-input.dmn");
    }

    @Test
    public void testDMN13DtOnBkmImplicitParamsDMN() throws Exception {
        executeDMNTestCase("dmn13", "0037-dt-on-bkm-implicit-params.dmn");
    }

    @Test
    public void testDMN13DtOnBkmExplicitParamsDMN() throws Exception {
        executeDMNTestCase("dmn13", "0038-dt-on-bkm-explicit-params.dmn");
    }

    @Test
    public void testDMN13DtListSemanticsDMN() throws Exception {
        executeDMNTestCase("dmn13", "0039-dt-list-semantics.dmn");
    }

    @Test
    public void testDMN13SinglenestedcontextDMN() throws Exception {
        executeDMNTestCase("dmn13", "0040-singlenestedcontext.dmn");
    }

    @Test
    public void testDMN13MultipleNestedcontextDMN() throws Exception {
        executeDMNTestCase("dmn13", "0041-multiple-nestedcontext.dmn");
    }

    @Test
    public void testDMN13FeelAbsFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "0050-feel-abs-function.dmn");
    }

    @Test
    public void testDMN13FeelSqrtFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "0051-feel-sqrt-function.dmn");
    }

    @Test
    public void testDMN13FeelExpFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "0052-feel-exp-function.dmn");
    }

    @Test
    public void testDMN13FeelLogFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "0053-feel-log-function.dmn");
    }

    @Test
    public void testDMN13FeelEvenFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "0054-feel-even-function.dmn");
    }

    @Test
    public void testDMN13FeelOddFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "0055-feel-odd-function.dmn");
    }

    @Test
    public void testDMN13FeelModuloFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "0056-feel-modulo-function.dmn");
    }

    @Test
    public void testDMN13FeelContextDMN() throws Exception {
        executeDMNTestCase("dmn13", "0057-feel-context.dmn");
    }

    @Test
    public void testDMN13FeelNumberFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "0058-feel-number-function.dmn");
    }

    @Test
    public void testDMN13FeelAllFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "0059-feel-all-function.dmn");
    }

    @Test
    public void testDMN13FeelAnyFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "0060-feel-any-function.dmn");
    }

    @Test
    public void testDMN13FeelMedianFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "0061-feel-median-function.dmn");
    }

    @Test
    public void testDMN13FeelModeFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "0062-feel-mode-function.dmn");
    }

    @Test
    public void testDMN13FeelStddevFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "0063-feel-stddev-function.dmn");
    }

    @Test
    public void testDMN13FeelConjunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "0064-feel-conjunction.dmn");
    }

    @Test
    public void testDMN13FeelDisjunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "0065-feel-disjunction.dmn");
    }

    @Test
    public void testDMN13FeelNegationDMN() throws Exception {
        executeDMNTestCase("dmn13", "0066-feel-negation.dmn");
    }

    @Test
    public void testDMN13FeelSplitFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "0067-feel-split-function.dmn");
    }

    @Test
    public void testDMN13FeelEqualityDMN() throws Exception {
        executeDMNTestCase("dmn13", "0068-feel-equality.dmn");
    }

    @Test
    public void testDMN13FeelListDMN() throws Exception {
        executeDMNTestCase("dmn13", "0069-feel-list.dmn");
    }

    @Test
    public void testDMN13FeelInstanceOfDMN() throws Exception {
        executeDMNTestCase("dmn13", "0070-feel-instance-of.dmn");
    }

    @Test
    public void testDMN13FeelBetweenDMN() throws Exception {
        executeDMNTestCase("dmn13", "0071-feel-between.dmn");
    }

    @Test
    @Ignore("KOGITO-3836")
    public void testDMN13FeelInDMN() throws Exception {
        executeDMNTestCase("dmn13", "0072-feel-in.dmn");
    }

    @Test
    public void testDMN13FeelCommentsDMN() throws Exception {
        executeDMNTestCase("dmn13", "0073-feel-comments.dmn");
    }

    @Test
    public void testDMN13FeelPropertiesDMN() throws Exception {
        executeDMNTestCase("dmn13", "0074-feel-properties.dmn");
    }

    @Test
    public void testDMN13FeelExponentDMN() throws Exception {
        executeDMNTestCase("dmn13", "0075-feel-exponent.dmn");
    }

    @Test
    public void testDMN13FeelExternalJavaDMN() throws Exception {
        executeDMNTestCase("dmn13", "0076-feel-external-java.dmn");
    }

    @Test
    public void testDMN13FeelNanDMN() throws Exception {
        executeDMNTestCase("dmn13", "0077-feel-nan.dmn");
    }

    @Test
    public void testDMN13FeelInfinityDMN() throws Exception {
        executeDMNTestCase("dmn13", "0078-feel-infinity.dmn");
    }

    @Test
    public void testDMN13FeelGetvalueFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "0080-feel-getvalue-function.dmn");
    }

    @Test
    public void testDMN13FeelGetentriesFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "0081-feel-getentries-function.dmn");
    }

    @Test
    public void testDMN13FeelCoercionDMN() throws Exception {
        executeDMNTestCase("dmn13", "0082-feel-coercion.dmn");
    }

    @Test
    public void testDMN13FeelUnicodeDMN() throws Exception {
        executeDMNTestCase("dmn13", "0083-feel-unicode.dmn");
    }

    @Test
    public void testDMN13FeelForLoopsDMN() throws Exception {
        executeDMNTestCase("dmn13", "0084-feel-for-loops.dmn");
    }

    @Test
    public void testDMN13DecisionServicesDMN() throws Exception {
        executeDMNTestCase("dmn13", "0085-decision-services.dmn");
    }

    @Test
    public void testDMN13ImportDMN() throws Exception {
        executeDMNTestCase("dmn13", "0086-import.dmn");
    }

    @Test
    public void testDMN13Chapter11ExampleDMN() throws Exception {
        executeDMNTestCase("dmn13", "0087-chapter-11-example.dmn");
    }

    @Test
    public void testDMN13NoDecisionLogicDMN() throws Exception {
        executeDMNTestCase("dmn13", "0088-no-decision-logic.dmn");
    }

    @Test
    public void testDMN13NestedInputdataImportsDMN() throws Exception {
        executeDMNTestCase("dmn13", "0089-nested-inputdata-imports.dmn");
    }

    @Test
    public void testDMN13FeelPathsDMN() throws Exception {
        executeDMNTestCase("dmn13", "0090-feel-paths.dmn");
    }

    @Test
    public void testDMN13FeelConstantsDMN1() throws Exception {
        executeDMNTestCase("dmn13", "0100-feel-constants.dmn");
    }

    @Test
    public void testDMN13FeelConstantsDMN2() throws Exception {
        executeDMNTestCase("dmn13", "0101-feel-constants.dmn");
    }

    @Test
    public void testDMN13FeelConstantsDMN3() throws Exception {
        executeDMNTestCase("dmn13", "0102-feel-constants.dmn");
    }

    @Test
    public void testDMN13FeelMathDMN() throws Exception {
        executeDMNTestCase("dmn13", "0105-feel-math.dmn");
    }

    @Test
    public void testDMN13FeelTernaryLogicDMN() throws Exception {
        executeDMNTestCase("dmn13", "0106-feel-ternary-logic.dmn");
    }

    @Test
    public void testDMN13FeelTernaryLogicNotDMN() throws Exception {
        executeDMNTestCase("dmn13", "0107-feel-ternary-logic-not.dmn");
    }

    @Test
    public void testDMN13FirstHitpolicyDMN() throws Exception {
        executeDMNTestCase("dmn13", "0108-first-hitpolicy.dmn");
    }

    @Test
    public void testDMN13RuleOrderHitpolicyDMN() throws Exception {
        executeDMNTestCase("dmn13", "0109-ruleOrder-hitpolicy.dmn");
    }

    @Test
    public void testDMN13OutputOrderHitpolicyDMN() throws Exception {
        executeDMNTestCase("dmn13", "0110-outputOrder-hitpolicy.dmn");
    }

    @Test
    public void testDMN13FirstHitpolicySingleoutputcolDMN() throws Exception {
        executeDMNTestCase("dmn13", "0111-first-hitpolicy-singleoutputcol.dmn");
    }

    @Test
    public void testDMN13RuleOrderHitpolicySingleinoutcolDMN() throws Exception {
        executeDMNTestCase("dmn13", "0112-ruleOrder-hitpolicy-singleinoutcol.dmn");
    }

    @Test
    public void testDMN13OutputOrderHitpolicySingleinoutcolDMN() throws Exception {
        executeDMNTestCase("dmn13", "0113-outputOrder-hitpolicy-singleinoutcol.dmn");
    }

    @Test
    public void testDMN13MinCollectHitpolicyDMN() throws Exception {
        executeDMNTestCase("dmn13", "0114-min-collect-hitpolicy.dmn");
    }

    @Test
    public void testDMN13SumCollectHitpolicyDMN() throws Exception {
        executeDMNTestCase("dmn13", "0115-sum-collect-hitpolicy.dmn");
    }

    @Test
    public void testDMN13CountCollectHitpolicyDMN() throws Exception {
        executeDMNTestCase("dmn13", "0116-count-collect-hitpolicy.dmn");
    }

    @Test
    public void testDMN13MultiAnyHitpolicyDMN() throws Exception {
        executeDMNTestCase("dmn13", "0117-multi-any-hitpolicy.dmn");
    }

    @Test
    public void testDMN13MultiPriorityHitpolicyDMN() throws Exception {
        executeDMNTestCase("dmn13", "0118-multi-priority-hitpolicy.dmn");
    }

    @Test
    public void testDMN13MultiCollectHitpolicyDMN() throws Exception {
        executeDMNTestCase("dmn13", "0119-multi-collect-hitpolicy.dmn");
    }

    @Test
    public void testDMN13FeelDecimalFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "1100-feel-decimal-function.dmn");
    }

    @Test
    public void testDMN13FeelFloorFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "1101-feel-floor-function.dmn");
    }

    @Test
    public void testDMN13FeelCeilingFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "1102-feel-ceiling-function.dmn");
    }

    @Test
    public void testDMN13FeelSubstringFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "1103-feel-substring-function.dmn");
    }

    @Test
    public void testDMN13FeelStringLengthFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "1104-feel-string-length-function.dmn");
    }

    @Test
    public void testDMN13FeelUpperCaseFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "1105-feel-upper-case-function.dmn");
    }

    @Test
    public void testDMN13FeelLowerCaseFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "1106-feel-lower-case-function.dmn");
    }

    @Test
    public void testDMN13FeelSubstringBeforeFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "1107-feel-substring-before-function.dmn");
    }

    @Test
    public void testDMN13FeelSubstringAfterFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "1108-feel-substring-after-function.dmn");
    }

    @Test
    public void testDMN13FeelReplaceFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "1109-feel-replace-function.dmn");
    }

    @Test
    public void testDMN13FeelContainsFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "1110-feel-contains-function.dmn");
    }

    @Test
    public void testDMN13FeelDateFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "1115-feel-date-function.dmn");
    }

    @Test
    public void testDMN13FeelTimeFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "1116-feel-time-function.dmn");
    }

    @Test
    public void testDMN13FeelDateAndTimeFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "1117-feel-date-and-time-function.dmn");
    }

    @Test
    public void testDMN13FeelDurationFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "1120-feel-duration-function.dmn");
    }

    @Test
    public void testDMN13FeelYearsAndMonthsDurationFunctionDMN() throws Exception {
        executeDMNTestCase("dmn13", "1121-feel-years-and-months-duration-function.dmn");
    }

    @Test
    public void testDMN13ImportedModelDMN() throws Exception {
        executeDMNTestCase("dmn13", "Imported_Model.dmn");
    }

    @Test
    public void testDMN13ModelBDMN() throws Exception {
        executeDMNTestCase("dmn13", "Model_B.dmn");
    }

    @Test
    public void testDMN13ModelB2DMN() throws Exception {
        executeDMNTestCase("dmn13", "Model_B2.dmn");
    }

    @Test
    public void testDMN13Sayhello1ID1DDMN() throws Exception {
        executeDMNTestCase("dmn13", "Say_hello_1ID1D.dmn");
    }
}
