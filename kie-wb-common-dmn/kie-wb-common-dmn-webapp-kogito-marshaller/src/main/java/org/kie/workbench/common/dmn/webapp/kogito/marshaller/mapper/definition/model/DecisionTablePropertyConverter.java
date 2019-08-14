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

import org.kie.workbench.common.dmn.api.definition.model.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.model.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTableOrientation;
import org.kie.workbench.common.dmn.api.definition.model.HitPolicy;
import org.kie.workbench.common.dmn.api.definition.model.InputClause;
import org.kie.workbench.common.dmn.api.definition.model.OutputClause;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITBuiltinAggregator;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecisionRule;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecisionTable;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecisionTableOrientation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITHitPolicy;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInputClause;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITOutputClause;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.utils.JsArrayLikeUtils;

public class DecisionTablePropertyConverter {

    public static DecisionTable wbFromDMN(final JSITDecisionTable dmn) {
        final Id id = new Id(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef(), dmn);

        final DecisionTable result = new DecisionTable();
        result.setId(id);
        result.setDescription(description);
        result.setTypeRef(typeRef);

        for (JSITInputClause input : dmn.getInput().asArray()) {
            final InputClause inputClauseConverted = InputClausePropertyConverter.wbFromDMN(input);
            if (inputClauseConverted != null) {
                inputClauseConverted.setParent(result);
            }
            result.getInput().add(inputClauseConverted);
        }
        for (JSITOutputClause input : dmn.getOutput().asArray()) {
            final OutputClause outputClauseConverted = OutputClausePropertyConverter.wbFromDMN(input);
            if (outputClauseConverted != null) {
                outputClauseConverted.setParent(result);
            }
            result.getOutput().add(outputClauseConverted);
        }
        for (JSITDecisionRule dr : dmn.getRule().asArray()) {
            final DecisionRule decisionRuleConverted = DecisionRulePropertyConverter.wbFromDMN(dr);
            if (decisionRuleConverted != null) {
                decisionRuleConverted.setParent(result);
            }
            result.getRule().add(decisionRuleConverted);
        }
        if (dmn.getHitPolicy() != null) {
            result.setHitPolicy(HitPolicy.fromValue(dmn.getHitPolicy().value()));
        }
        if (dmn.getAggregation() != null) {
            result.setAggregation(BuiltinAggregator.fromValue(dmn.getAggregation().name()));
        }
        if (dmn.getPreferredOrientation() != null) {
            result.setPreferredOrientation(DecisionTableOrientation.fromValue(dmn.getPreferredOrientation().value()));
        }

        result.setOutputLabel(dmn.getOutputLabel());

        return result;
    }

    public static JSITDecisionTable dmnFromWB(final DecisionTable wb) {
        final JSITDecisionTable result = new JSITDecisionTable();
        result.setId(wb.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(), result::setTypeRef);

        for (InputClause input : wb.getInput()) {
            final JSITInputClause c = InputClausePropertyConverter.dmnFromWB(input);
            if (c != null) {
                c.setParent(result);
            }
            JsArrayLikeUtils.add(result.getInput(), c);
        }
        for (OutputClause input : wb.getOutput()) {
            final JSITOutputClause c = OutputClausePropertyConverter.dmnFromWB(input);
            if (c != null) {
                c.setParent(result);
            }
            JsArrayLikeUtils.add(result.getOutput(), c);
        }
        if (result.getOutput().getLength() == 1) {
            result.getOutput().getAt(0).setName(null); // DROOLS-3281
        }
        for (DecisionRule dr : wb.getRule()) {
            final JSITDecisionRule c = DecisionRulePropertyConverter.dmnFromWB(dr);
            if (c != null) {
                c.setParent(result);
            }
            JsArrayLikeUtils.add(result.getRule(), c);
        }
        if (wb.getHitPolicy() != null) {
            result.setHitPolicy(JSITHitPolicy.valueOf(wb.getHitPolicy().name()));
        }
        if (wb.getAggregation() != null) {
            result.setAggregation(JSITBuiltinAggregator.valueOf(wb.getAggregation().name()));
        }
        if (wb.getPreferredOrientation() != null) {
            result.setPreferredOrientation(JSITDecisionTableOrientation.valueOf(wb.getPreferredOrientation().name()));
        }

        result.setOutputLabel(wb.getOutputLabel());

        return result;
    }
}