/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.converters;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

public class BigDecimalListToDoubleListConverterTest extends AbstractListToListConverterTest<BigDecimalListToDoubleListConverter, BigDecimal, Double> {

    @Override
    protected BigDecimalListToDoubleListConverter getConverter() {
        return new BigDecimalListToDoubleListConverter();
    }

    @Override
    protected List<BigDecimal> getModelValues() {
        List<BigDecimal> modelValue = new ArrayList<>();
        MathContext context = new MathContext(1);
        modelValue.add(new BigDecimal(0.1, context));
        modelValue.add(new BigDecimal(0.2, context));
        modelValue.add(new BigDecimal(0.3, context));
        modelValue.add(new BigDecimal(0.4, context));
        return modelValue;
    }

    @Override
    protected List<Double> getWidgetValues() {
        List<Double> widgetValue = new ArrayList<>();
        widgetValue.add(0.1);
        widgetValue.add(0.2);
        widgetValue.add(0.3);
        widgetValue.add(0.4);
        return widgetValue;
    }
}
