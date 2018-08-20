/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.Invocation;
import org.kie.workbench.common.dmn.api.definition.v1_1.List;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.Relation;

public class ExpressionPropertyConverter {

    public static Expression wbFromDMN(final org.kie.dmn.model.v1x.Expression dmn) {
        // SPECIAL CASE: to represent a partially edited DMN file.
        // consider a LiteralExpression with null text as missing expression altogether.
        if (dmn instanceof org.kie.dmn.model.v1x.LiteralExpression) {
            org.kie.dmn.model.v1x.LiteralExpression literalExpression = (org.kie.dmn.model.v1x.LiteralExpression) dmn;
            if (literalExpression.getText() == null) {
                return null;
            }
        }

        if (dmn instanceof org.kie.dmn.model.v1x.LiteralExpression) {
            return LiteralExpressionPropertyConverter.wbFromDMN((org.kie.dmn.model.v1x.LiteralExpression) dmn);
        } else if (dmn instanceof org.kie.dmn.model.v1x.Context) {
            return ContextPropertyConverter.wbFromDMN((org.kie.dmn.model.v1x.Context) dmn);
        } else if (dmn instanceof org.kie.dmn.model.v1x.Relation) {
            return RelationPropertyConverter.wbFromDMN((org.kie.dmn.model.v1x.Relation) dmn);
        } else if (dmn instanceof org.kie.dmn.model.v1x.List) {
            return ListPropertyConverter.wbFromDMN((org.kie.dmn.model.v1x.List) dmn);
        } else if (dmn instanceof org.kie.dmn.model.v1x.Invocation) {
            return InvocationPropertyConverter.wbFromDMN((org.kie.dmn.model.v1x.Invocation) dmn);
        } else if (dmn instanceof org.kie.dmn.model.v1x.FunctionDefinition) {
            return FunctionDefinitionPropertyConverter.wbFromDMN((org.kie.dmn.model.v1x.FunctionDefinition) dmn);
        } else if (dmn instanceof org.kie.dmn.model.v1x.DecisionTable) {
            return DecisionTablePropertyConverter.wbFromDMN((org.kie.dmn.model.v1x.DecisionTable) dmn);
        }
        return null;
    }

    public static org.kie.dmn.model.v1x.Expression dmnFromWB(final Expression wb) {
        // SPECIAL CASE: to represent a partially edited DMN file.
        // reference above.
        if (wb == null) {
            org.kie.dmn.model.v1x.LiteralExpression mockedExpression = new org.kie.dmn.model.v1_1.TLiteralExpression();
            return mockedExpression;
        }

        if (wb instanceof LiteralExpression) {
            return LiteralExpressionPropertyConverter.dmnFromWB((LiteralExpression) wb);
        } else if (wb instanceof Context) {
            return ContextPropertyConverter.dmnFromWB((Context) wb);
        } else if (wb instanceof Relation) {
            return RelationPropertyConverter.dmnFromWB((Relation) wb);
        } else if (wb instanceof List) {
            return ListPropertyConverter.dmnFromWB((List) wb);
        } else if (wb instanceof Invocation) {
            return InvocationPropertyConverter.dmnFromWB((Invocation) wb);
        } else if (wb instanceof FunctionDefinition) {
            return FunctionDefinitionPropertyConverter.dmnFromWB((FunctionDefinition) wb);
        } else if (wb instanceof DecisionTable) {
            return DecisionTablePropertyConverter.dmnFromWB((DecisionTable) wb);
        }
        return null;
    }
}
