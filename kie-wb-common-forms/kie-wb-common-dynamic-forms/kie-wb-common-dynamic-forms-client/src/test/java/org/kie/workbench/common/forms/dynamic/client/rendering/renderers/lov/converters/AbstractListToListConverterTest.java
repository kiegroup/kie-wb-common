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

import java.util.List;

import org.assertj.core.api.Assertions;
import org.jboss.errai.databinding.client.api.Converter;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractListToListConverterTest<CONVERTER extends Converter<List, List>, MODEL_TYPE, WIDGET_TYPE> {

    private CONVERTER converter;

    @Before
    public void init() {
        converter = getConverter();
    }

    abstract CONVERTER getConverter();

    abstract List<MODEL_TYPE> getModelValues();

    abstract List<WIDGET_TYPE> getWidgetValues();

    @Test
    public void testConvertToModelValueFromNullValue() {
        Assertions.assertThat(converter.toModelValue(null))
                .isNull();
    }

    @Test
    public void testConvertToWidgetValueFromNullValue() {
        Assertions.assertThat(converter.toWidgetValue(null))
                .isNotNull()
                .isEmpty();
    }


    @Test
    public void testConvertToWidgetValue() {
        List<MODEL_TYPE> modelValue = getModelValues();

        Assertions.assertThat(converter.toWidgetValue(modelValue))
                .isNotNull()
                .isNotEmpty()
                .hasSameSizeAs(modelValue)
                .contains(getWidgetValues().toArray());
    }

    @Test
    public void testConvertToModelValue() {
        List<WIDGET_TYPE> widgetValue = getWidgetValues();

        Assertions.assertThat(converter.toModelValue(widgetValue))
                .isNotNull()
                .isNotEmpty()
                .hasSameSizeAs(widgetValue)
                .contains(getModelValues().toArray());
    }
}
