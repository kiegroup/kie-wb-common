/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import java.util.Map.Entry;
import java.util.Optional;

import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

public class FunctionDefinitionPropertyConverter {

    public static FunctionDefinition wbFromDMN(final org.kie.dmn.model.v1_1.FunctionDefinition dmn) {
        if (dmn == null) {
            return null;
        }
        Id id = new Id(dmn.getId());
        Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef());
        Expression expression = ExpressionPropertyConverter.wbFromDMN(dmn.getExpression());
        FunctionDefinition result = new FunctionDefinition(id,
                                                           description,
                                                           typeRef,
                                                           expression);

        for (Entry<javax.xml.namespace.QName, String> kv : dmn.getAdditionalAttributes().entrySet()) {
            QName convertedQName = QNamePropertyConverter.wbFromDMN(kv.getKey());
            result.getOtherAttributes().put(convertedQName, kv.getValue());
        }
        result.getNsContext().putAll(dmn.getNsContext());

        for (org.kie.dmn.model.v1_1.InformationItem ii : dmn.getFormalParameter()) {
            InformationItem iiConverted = InformationItemPropertyConverter.wbFromDMN(ii);
            result.getFormalParameter().add(iiConverted);
        }

        return result;
    }

    public static org.kie.dmn.model.v1_1.FunctionDefinition dmnFromWB(final FunctionDefinition wb) {
        if (wb == null) {
            return null;
        }
        org.kie.dmn.model.v1_1.FunctionDefinition result = new org.kie.dmn.model.v1_1.FunctionDefinition();
        result.setId(wb.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(),
                                            result::setTypeRef);
        result.setExpression(ExpressionPropertyConverter.dmnFromWB(wb.getExpression()));

        for (Entry<QName, String> kv : wb.getOtherAttributes().entrySet()) {
            Optional<javax.xml.namespace.QName> convertedQName = QNamePropertyConverter.dmnFromWB(kv.getKey());
            if (convertedQName.isPresent()) {
                result.getAdditionalAttributes().put(convertedQName.get(), kv.getValue());
            }
        }
        result.getNsContext().putAll(wb.getNsContext());

        for (InformationItem ii : wb.getFormalParameter()) {
            org.kie.dmn.model.v1_1.InformationItem iiConverted = InformationItemPropertyConverter.dmnFromWB(ii);
            result.getFormalParameter().add(iiConverted);
        }

        return result;
    }
}