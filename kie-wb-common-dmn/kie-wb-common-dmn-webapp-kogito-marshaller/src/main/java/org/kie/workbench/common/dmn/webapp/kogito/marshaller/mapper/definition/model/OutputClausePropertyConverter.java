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

import org.kie.workbench.common.dmn.api.definition.model.OutputClause;
import org.kie.workbench.common.dmn.api.definition.model.OutputClauseLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.OutputClauseUnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITLiteralExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITOutputClause;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITUnaryTests;
import org.kie.workbench.common.stunner.core.util.StringUtils;

public class OutputClausePropertyConverter {

    public static OutputClause wbFromDMN(final JSITOutputClause dmn) {
        final Id id = new Id(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final OutputClauseUnaryTests outputValues = OutputClauseUnaryTestsPropertyConverter.wbFromDMN(dmn.getOutputValues());
        final OutputClauseLiteralExpression defaultOutputEntry = OutputClauseLiteralExpressionPropertyConverter.wbFromDMN(dmn.getDefaultOutputEntry());
        final QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef(), dmn);

        final OutputClause result = new OutputClause();
        result.setId(id);
        result.setName(dmn.getName());
        result.setDescription(description);
        result.setOutputValues(outputValues);
        result.setDefaultOutputEntry(defaultOutputEntry);
        result.setTypeRef(typeRef);

        if (outputValues != null) {
            outputValues.setParent(result);
        }
        if (defaultOutputEntry != null) {
            defaultOutputEntry.setParent(result);
        }

        return result;
    }

    public static JSITOutputClause dmnFromWB(final OutputClause wb) {
        final JSITOutputClause result = new JSITOutputClause();
        result.setId(wb.getId().getValue());
        result.setName(wb.getName());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));

        final JSITUnaryTests outputValues = UnaryTestsPropertyConverter.dmnFromWB(wb.getOutputValues());
        if (outputValues != null && StringUtils.nonEmpty(outputValues.getText())) {
            outputValues.setParent(result);
            result.setOutputValues(outputValues);
        }

        final JSITLiteralExpression defaultOutputEntry = LiteralExpressionPropertyConverter.dmnFromWB(wb.getDefaultOutputEntry());
        if (defaultOutputEntry != null && StringUtils.nonEmpty(defaultOutputEntry.getText())) {
            defaultOutputEntry.setParent(result);
            result.setDefaultOutputEntry(defaultOutputEntry);
        }

        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(), result::setTypeRef);

        return result;
    }
}