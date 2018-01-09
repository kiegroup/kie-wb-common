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

import java.util.ArrayList;
import java.util.List;

public class FloatListToDoubleListConverterTest extends AbstractListToListConverterTest<FloatListToDoubleListConverter, Float, Double> {

    @Override
    protected FloatListToDoubleListConverter getConverter() {
        return new FloatListToDoubleListConverter();
    }

    @Override
    protected List<Float> getModelValues() {
        List<Float> modelValue = new ArrayList<>();
        modelValue.add(0.1f);
        modelValue.add(0.2f);
        modelValue.add(0.3f);
        modelValue.add(0.4f);
        return modelValue;
    }

    @Override
    protected List<Double> getWidgetValues() {
        List<Double> widgetValues = new ArrayList<>();
        widgetValues.add(0.1);
        widgetValues.add(0.2);
        widgetValues.add(0.3);
        widgetValues.add(0.4);
        return widgetValues;
    }
}
