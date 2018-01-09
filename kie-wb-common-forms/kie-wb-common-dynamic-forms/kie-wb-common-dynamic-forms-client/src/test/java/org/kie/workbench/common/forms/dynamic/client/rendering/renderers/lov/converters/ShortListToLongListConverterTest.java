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

public class ShortListToLongListConverterTest extends AbstractListToListConverterTest<ShortListToLongListConverter, Short, Long> {

    @Override
    protected ShortListToLongListConverter getConverter() {
        return new ShortListToLongListConverter();
    }

    @Override
    protected List<Short> getModelValues() {
        List<Short> modelValue = new ArrayList<>();
        modelValue.add((short) 1);
        modelValue.add((short) 2);
        modelValue.add((short) 3);
        modelValue.add((short) 4);
        return modelValue;
    }

    @Override
    protected List<Long> getWidgetValues() {
        List<Long> widgetValue = new ArrayList<>();
        widgetValue.add(1L);
        widgetValue.add(2L);
        widgetValue.add(3L);
        widgetValue.add(4L);
        return widgetValue;
    }
}
