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
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition.Kind;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocument;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocumentModel;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITFunctionDefinition;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITFunctionKind;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInformationItem;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.ComponentWidths;

public class FunctionDefinitionPropertyConverter {

    public static FunctionDefinition wbFromDMN(final JSITFunctionDefinition dmn,
                                               final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        if (dmn == null) {
            return null;
        }
        final Id id = new Id(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef(), dmn);
        final JSITExpression jsiExpression = Js.uncheckedCast(JsUtils.getUnwrappedElement(dmn.getExpression()));
        final Expression expression = ExpressionPropertyConverter.wbFromDMN(jsiExpression,
                                                                            hasComponentWidthsConsumer);
        final FunctionDefinition result = new FunctionDefinition(id,
                                                                 description,
                                                                 typeRef,
                                                                 expression);
        if (expression != null) {
            expression.setParent(result);
        }

        //JSITFunctionKind is a String JSO so convert into the real type
        final String sKind = Js.uncheckedCast(dmn.getKind());
        final JSITFunctionKind kind = JSITFunctionKind.valueOf(sKind);
        switch (kind) {
            case FEEL:
                result.setKind(Kind.FEEL);
                break;
            case JAVA:
                result.setKind(Kind.JAVA);
                break;
            case PMML:
                result.setKind(Kind.PMML);
                convertPMMLFunctionExpression(result);
                break;
            default:
                result.setKind(Kind.FEEL);
                break;
        }

        final JsArrayLike<JSITInformationItem> wrappedInformationItems = dmn.getFormalParameter();
        if (Objects.nonNull(wrappedInformationItems)) {
            final JsArrayLike<JSITInformationItem> jsiInformationItems = JsUtils.getUnwrappedElementsArray(wrappedInformationItems);
            for (int i = 0; i < jsiInformationItems.getLength(); i++) {
                final JSITInformationItem jsiInformationItem = Js.uncheckedCast(jsiInformationItems.getAt(i));
                final InformationItem iiConverted = InformationItemPropertyConverter.wbFromDMN(jsiInformationItem);
                if (iiConverted != null) {
                    iiConverted.setParent(result);
                }
                result.getFormalParameter().add(iiConverted);
            }
        }

        return result;
    }

    private static void convertPMMLFunctionExpression(final FunctionDefinition function) {
        final Expression expression = function.getExpression();
        if (expression instanceof Context) {
            final Context context = (Context) expression;
            context.getContextEntry().forEach(FunctionDefinitionPropertyConverter::convertContextEntryExpression);
        }
    }

    private static void convertContextEntryExpression(final ContextEntry contextEntry) {
        final Expression expression = contextEntry.getExpression();
        if (expression instanceof LiteralExpression) {
            final LiteralExpression le = (LiteralExpression) expression;
            final String variableName = contextEntry.getVariable().getName().getValue();
            if (Objects.equals(LiteralExpressionPMMLDocument.VARIABLE_DOCUMENT,
                               variableName)) {
                contextEntry.setExpression(convertLiteralExpressionToPMMLDocument(le));
            } else if (Objects.equals(LiteralExpressionPMMLDocumentModel.VARIABLE_MODEL,
                                      variableName)) {
                contextEntry.setExpression(convertLiteralExpressionToPMMLDocumentModel(le));
            }
        }
    }

    private static LiteralExpressionPMMLDocument convertLiteralExpressionToPMMLDocument(final LiteralExpression le) {
        return new LiteralExpressionPMMLDocument(le.getId(),
                                                 le.getDescription(),
                                                 le.getTypeRef(),
                                                 le.getText(),
                                                 le.getImportedValues(),
                                                 le.getExpressionLanguage());
    }

    private static LiteralExpressionPMMLDocumentModel convertLiteralExpressionToPMMLDocumentModel(final LiteralExpression le) {
        return new LiteralExpressionPMMLDocumentModel(le.getId(),
                                                      le.getDescription(),
                                                      le.getTypeRef(),
                                                      le.getText(),
                                                      le.getImportedValues(),
                                                      le.getExpressionLanguage());
    }

    public static JSITFunctionDefinition dmnFromWB(final FunctionDefinition wb,
                                                   final Consumer<ComponentWidths> componentWidthsConsumer) {
        if (wb == null) {
            return null;
        }
        final JSITFunctionDefinition result = new JSITFunctionDefinition();
        result.setId(wb.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(), result::setTypeRef);
        result.setExpression(ExpressionPropertyConverter.dmnFromWB(wb.getExpression(),
                                                                   componentWidthsConsumer));

        final Kind kind = wb.getKind();
        switch (kind) {
            case FEEL:
                result.setKind(JSITFunctionKind.FEEL);
                break;
            case JAVA:
                result.setKind(JSITFunctionKind.JAVA);
                break;
            case PMML:
                result.setKind(JSITFunctionKind.PMML);
                break;
            default:
                result.setKind(JSITFunctionKind.FEEL);
                break;
        }

        for (InformationItem ii : wb.getFormalParameter()) {
            final JSITInformationItem iiConverted = InformationItemPropertyConverter.dmnFromWB(ii);
            if (iiConverted != null) {
                iiConverted.setParent(result);
            }
            JsUtils.add(result.getFormalParameter(), iiConverted);
        }

        return result;
    }
}