/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITContextEntry;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInformationItem;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.ComponentWidths;

public class ContextEntryPropertyConverter {

    public static ContextEntry wbFromDMN(final JSITContextEntry dmn,
                                         final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        final InformationItem variable = InformationItemPropertyConverter.wbFromDMN(dmn.getVariable());
        final JSITExpression jsiExpression = Js.uncheckedCast(JsUtils.getUnwrappedElement(dmn.getExpression()));
        final Expression expression = ExpressionPropertyConverter.wbFromDMN(jsiExpression,
                                                                            hasComponentWidthsConsumer);

        final ContextEntry result = new ContextEntry();
        if (variable != null) {
            variable.setParent(result);
        }
        result.setVariable(variable);
        if (expression != null) {
            expression.setParent(result);
        }
        result.setExpression(expression);
        return result;
    }

    public static JSITContextEntry dmnFromWB(final ContextEntry wb,
                                             final Consumer<ComponentWidths> componentWidthsConsumer) {
        final JSITContextEntry result = new JSITContextEntry();

        final JSITInformationItem variable = InformationItemPropertyConverter.dmnFromWB(wb.getVariable());
        if (variable != null) {
            variable.setParent(result);
        }
        final JSITExpression expression = ExpressionPropertyConverter.dmnFromWB(wb.getExpression(),
                                                                                componentWidthsConsumer);
        if (expression != null) {
            expression.setParent(result);
        }

        result.setVariable(variable);
        result.setExpression(expression);
        return result;
    }
}
