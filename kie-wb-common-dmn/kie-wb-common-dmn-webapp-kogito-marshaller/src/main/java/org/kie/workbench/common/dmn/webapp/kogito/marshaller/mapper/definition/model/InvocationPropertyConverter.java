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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.Binding;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.Invocation;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITBinding;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInvocation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.ComponentWidths;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.utils.JsArrayLikeUtils;

public class InvocationPropertyConverter {

    public static Invocation wbFromDMN(final JSITInvocation dmn,
                                       final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        if (dmn == null) {
            return null;
        }
        final Id id = new Id(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final QName typeRef = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef(), dmn);

        final Invocation result = new Invocation();
        result.setId(id);
        result.setDescription(description);
        result.setTypeRef(typeRef);

        final Expression convertedExpression = ExpressionPropertyConverter.wbFromDMN(dmn.getExpression(),
                                                                                     hasComponentWidthsConsumer);
        result.setExpression(convertedExpression);
        if (convertedExpression != null) {
            convertedExpression.setParent(result);
        }

        for (JSITBinding b : dmn.getBinding().asArray()) {
            final Binding bConverted = BindingPropertyConverter.wbFromDMN(b,
                                                                          hasComponentWidthsConsumer);
            if (bConverted != null) {
                bConverted.setParent(result);
            }
            result.getBinding().add(bConverted);
        }

        return result;
    }

    public static JSITInvocation dmnFromWB(final Invocation wb,
                                           final Consumer<ComponentWidths> componentWidthsConsumer) {
        if (wb == null) {
            return null;
        }
        final JSITInvocation result = new JSITInvocation();
        result.setId(wb.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(),
                                            result::setTypeRef);

        final JSITExpression convertedExpression = ExpressionPropertyConverter.dmnFromWB(wb.getExpression(),
                                                                                         componentWidthsConsumer);
        if (convertedExpression != null) {
            convertedExpression.setParent(result);
        }
        result.setExpression(convertedExpression);

        for (Binding b : wb.getBinding()) {
            final JSITBinding bConverted = BindingPropertyConverter.dmnFromWB(b, componentWidthsConsumer);
            if (bConverted != null) {
                bConverted.setParent(result);
            }
            JsArrayLikeUtils.add(result.getBinding(), bConverted);
        }

        return result;
    }
}