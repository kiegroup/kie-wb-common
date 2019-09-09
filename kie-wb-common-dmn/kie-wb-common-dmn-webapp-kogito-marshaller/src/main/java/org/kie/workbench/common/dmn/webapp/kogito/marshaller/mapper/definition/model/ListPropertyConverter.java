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

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITList;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.ComponentWidths;

public class ListPropertyConverter {

    public static List wbFromDMN(final JSITList dmn,
                                 final JSITDefinitions jsiDefinitions,
                                 final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        final Id id = IdPropertyConverter.wbFromDMN(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef(),
                                                               dmn,
                                                               jsiDefinitions);

        final java.util.List<Expression> expression = new ArrayList<>();
        final JsArrayLike<JSITExpression> wrappedExpressions = dmn.getExpression();
        final List result = new List(id, description, typeRef, expression);
        if (Objects.nonNull(wrappedExpressions)) {
            final JsArrayLike<JSITExpression> jsiExpressions = JsUtils.getUnwrappedElementsArray(wrappedExpressions);
            for (int i = 0; i < jsiExpressions.getLength(); i++) {
                final JSITExpression jsitExpression = Js.uncheckedCast(jsiExpressions.getAt(i));
                Expression eConverted = ExpressionPropertyConverter.wbFromDMN(jsitExpression,
                                                                              Js.uncheckedCast(dmn),
                                                                              jsiDefinitions,
                                                                              hasComponentWidthsConsumer);
                expression.add(eConverted);
            }

            for (Expression e : expression) {
                if (e != null) {
                    e.setParent(result);
                }
            }
        }
        return result;
    }

    public static JSITList dmnFromWB(final List wb,
                                     final Consumer<ComponentWidths> componentWidthsConsumer) {
        final JSITList result = new JSITList();
        result.setId(wb.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(),
                                            result::setTypeRef);

        for (Expression e : wb.getExpression()) {
            final JSITExpression eConverted = ExpressionPropertyConverter.dmnFromWB(e, componentWidthsConsumer);
            if (eConverted != null) {
                eConverted.setParent(result);
            }
            JsUtils.add(result.getExpression(), eConverted);
        }

        return result;
    }
}
