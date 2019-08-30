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

package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model;

import gwt.jsonix.marshallers.xjc.plugin.JsUtils;
import org.kie.workbench.common.dmn.api.definition.model.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecisionRule;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITLiteralExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITUnaryTests;

public class DecisionRulePropertyConverter {

    public static DecisionRule wbFromDMN(final JSITDecisionRule dmn) {
        final Id id = new Id(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());

        final DecisionRule result = new DecisionRule();
        result.setId(id);
        result.setDescription(description);

        for (JSITUnaryTests ie : dmn.getInputEntry().asArray()) {
            final UnaryTests inputEntryConverted = UnaryTestsPropertyConverter.wbFromDMN(ie);
            if (inputEntryConverted != null) {
                inputEntryConverted.setParent(result);
            }
            result.getInputEntry().add(inputEntryConverted);
        }
        for (JSITLiteralExpression oe : dmn.getOutputEntry().asArray()) {
            final LiteralExpression outputEntryConverted = LiteralExpressionPropertyConverter.wbFromDMN(oe);
            if (outputEntryConverted != null) {
                outputEntryConverted.setParent(result);
            }
            result.getOutputEntry().add(outputEntryConverted);
        }

        return result;
    }

    public static JSITDecisionRule dmnFromWB(final DecisionRule wb) {
        final JSITDecisionRule result = new JSITDecisionRule();
        result.setId(wb.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));

        for (UnaryTests ie : wb.getInputEntry()) {
            final JSITUnaryTests inputEntryConverted = UnaryTestsPropertyConverter.dmnFromWB(ie);
            if (inputEntryConverted != null) {
                inputEntryConverted.setParent(result);
            }
            JsUtils.add(result.getInputEntry(), inputEntryConverted);
        }
        for (LiteralExpression oe : wb.getOutputEntry()) {
            final JSITLiteralExpression outputEntryConverted = LiteralExpressionPropertyConverter.dmnFromWB(oe);
            if (outputEntryConverted != null) {
                outputEntryConverted.setParent(result);
            }
            JsUtils.add(result.getOutputEntry(), outputEntryConverted);
        }

        return result;
    }
}