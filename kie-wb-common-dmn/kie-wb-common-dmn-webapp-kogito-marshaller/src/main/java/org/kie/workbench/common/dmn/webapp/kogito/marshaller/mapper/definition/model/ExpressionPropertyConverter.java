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
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.Invocation;
import org.kie.workbench.common.dmn.api.definition.model.IsLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITContext;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecisionTable;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITFunctionDefinition;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInvocation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITList;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITLiteralExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITRelation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentWidths;

public class ExpressionPropertyConverter {

    public static Expression wbFromDMN(final JSITExpression dmn,
                                       final JSITExpression parent,
                                       final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        if (JSITLiteralExpression.instanceOf(dmn)) {
            final JSITLiteralExpression jsiExpression = Js.uncheckedCast(dmn);
            final LiteralExpression e = LiteralExpressionPropertyConverter.wbFromDMN(jsiExpression);
            hasComponentWidthsConsumer.accept(dmn.getId(),
                                              e);
            return e;
        } else if (JSITContext.instanceOf(dmn)) {
            final JSITContext jsiExpression = Js.uncheckedCast(dmn);
            final Context e = ContextPropertyConverter.wbFromDMN(jsiExpression,
                                                                 parent,
                                                                 hasComponentWidthsConsumer);
            hasComponentWidthsConsumer.accept(dmn.getId(),
                                              e);
            return e;
        } else if (JSITRelation.instanceOf(dmn)) {
            final JSITRelation jsiExpression = Js.uncheckedCast(dmn);
            final Relation e = RelationPropertyConverter.wbFromDMN(jsiExpression,
                                                                   hasComponentWidthsConsumer);
            hasComponentWidthsConsumer.accept(dmn.getId(),
                                              e);
            return e;
        } else if (JSITList.instanceOf(dmn)) {
            final JSITList jsiExpression = Js.uncheckedCast(dmn);
            final List e = ListPropertyConverter.wbFromDMN(jsiExpression,
                                                           hasComponentWidthsConsumer);
            hasComponentWidthsConsumer.accept(dmn.getId(),
                                              e);
            return e;
        } else if (JSITInvocation.instanceOf(dmn)) {
            final JSITInvocation jsiExpression = Js.uncheckedCast(dmn);
            final Invocation e = InvocationPropertyConverter.wbFromDMN(jsiExpression,
                                                                       hasComponentWidthsConsumer);
            hasComponentWidthsConsumer.accept(dmn.getId(),
                                              e);
            return e;
        } else if (JSITFunctionDefinition.instanceOf(dmn)) {
            final JSITFunctionDefinition jsiExpression = Js.uncheckedCast(dmn);
            final FunctionDefinition e = FunctionDefinitionPropertyConverter.wbFromDMN(jsiExpression,
                                                                                       hasComponentWidthsConsumer);
            hasComponentWidthsConsumer.accept(dmn.getId(),
                                              e);
            return e;
        } else if (JSITDecisionTable.instanceOf(dmn)) {
            final JSITDecisionTable jsiExpression = Js.uncheckedCast(dmn);
            final DecisionTable e = DecisionTablePropertyConverter.wbFromDMN(jsiExpression);
            hasComponentWidthsConsumer.accept(dmn.getId(),
                                              e);
            return e;
        }
        return null;
    }

    public static JSITExpression dmnFromWB(final Expression wb,
                                           final Consumer<JSITComponentWidths> componentWidthsConsumer) {
        // SPECIAL CASE: to represent a partially edited DMN file.
        // reference above.
        if (wb == null) {
            final JSITLiteralExpression mockedExpression = JSITLiteralExpression.newInstance();
            return mockedExpression;
        }

        final String uuid = wb.getId().getValue();
        if (Objects.nonNull(uuid)) {
            final JSITComponentWidths componentWidths = JSITComponentWidths.newInstance();
            componentWidths.setDmnElementRef(uuid);
            //TODO {manstis} Need to convert WB's widths to something JSIxxx friendly
            //componentWidths.setWidth(wb.getComponentWidths());
            componentWidthsConsumer.accept(componentWidths);
        }

        if (wb instanceof IsLiteralExpression) {
            return LiteralExpressionPropertyConverter.dmnFromWB((IsLiteralExpression) wb);
        } else if (wb instanceof Context) {
            return ContextPropertyConverter.dmnFromWB((Context) wb,
                                                      componentWidthsConsumer);
        } else if (wb instanceof Relation) {
            return RelationPropertyConverter.dmnFromWB((Relation) wb,
                                                       componentWidthsConsumer);
        } else if (wb instanceof List) {
            return ListPropertyConverter.dmnFromWB((List) wb,
                                                   componentWidthsConsumer);
        } else if (wb instanceof Invocation) {
            return InvocationPropertyConverter.dmnFromWB((Invocation) wb,
                                                         componentWidthsConsumer);
        } else if (wb instanceof FunctionDefinition) {
            return FunctionDefinitionPropertyConverter.dmnFromWB((FunctionDefinition) wb,
                                                                 componentWidthsConsumer);
        } else if (wb instanceof DecisionTable) {
            return DecisionTablePropertyConverter.dmnFromWB((DecisionTable) wb);
        }
        return null;
    }
}
