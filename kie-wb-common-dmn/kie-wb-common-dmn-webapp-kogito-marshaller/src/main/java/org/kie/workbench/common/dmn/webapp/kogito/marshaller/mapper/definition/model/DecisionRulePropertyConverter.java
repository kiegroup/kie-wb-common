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

import java.util.Objects;
import java.util.Optional;

import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;
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
        final Id id = IdPropertyConverter.wbFromDMN(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());

        final DecisionRule result = new DecisionRule();
        result.setId(id);
        result.setDescription(description);

        final JsArrayLike<JSITUnaryTests> jsiInputEntries = JSITDecisionRule.getInputEntry(dmn);
        for (int i = 0; i < jsiInputEntries.getLength(); i++) {
            final JSITUnaryTests jsiInputEntry = Js.uncheckedCast(jsiInputEntries.getAt(i));
            final UnaryTests inputEntryConverted = UnaryTestsPropertyConverter.wbFromDMN(jsiInputEntry);
            if (Objects.nonNull(inputEntryConverted)) {
                inputEntryConverted.setParent(result);
                result.getInputEntry().add(inputEntryConverted);
            }
        }

        final JsArrayLike<JSITLiteralExpression> jsiOutputEntries = JSITDecisionRule.getOutputEntry(dmn);
        for (int i = 0; i < jsiOutputEntries.getLength(); i++) {
            final JSITLiteralExpression jsiOutputEntry = Js.uncheckedCast(jsiOutputEntries.getAt(i));
            final LiteralExpression outputEntryConverted = LiteralExpressionPropertyConverter.wbFromDMN(jsiOutputEntry);
            if (Objects.nonNull(outputEntryConverted)) {
                outputEntryConverted.setParent(result);
                result.getOutputEntry().add(outputEntryConverted);
            }
        }

        return result;
    }

    public static JSITDecisionRule dmnFromWB(final DecisionRule wb) {
        final JSITDecisionRule result = new JSITDecisionRule();
        result.setId(wb.getId().getValue());
        final Optional<String> description = Optional.ofNullable(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        description.ifPresent(result::setDescription);

        for (UnaryTests ie : wb.getInputEntry()) {
            final JSITUnaryTests inputEntryConverted = UnaryTestsPropertyConverter.dmnFromWB(ie);
            JSITDecisionRule.addInputEntry(result, inputEntryConverted);
        }
        for (LiteralExpression oe : wb.getOutputEntry()) {
            final JSITLiteralExpression outputEntryConverted = LiteralExpressionPropertyConverter.dmnFromWB(oe);
            JSITDecisionRule.addOutputEntry(result, outputEntryConverted);
        }

        return result;
    }
}