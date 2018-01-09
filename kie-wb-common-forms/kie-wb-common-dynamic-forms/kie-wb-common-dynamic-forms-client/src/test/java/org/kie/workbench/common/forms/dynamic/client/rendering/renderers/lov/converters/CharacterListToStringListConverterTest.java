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

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class CharacterListToStringListConverterTest extends AbstractListToListConverterTest<CharacterListToStringListConverter, Character, String> {

    @Override
    protected CharacterListToStringListConverter getConverter() {
        return new CharacterListToStringListConverter();
    }

    @Override
    protected List<Character> getModelValues() {
        List<Character> modelValue = new ArrayList<>();
        modelValue.add('a');
        modelValue.add('b');
        modelValue.add('c');
        modelValue.add('d');
        return modelValue;
    }

    @Override
    protected List<String> getWidgetValues() {
        List<String> widgetValue = new ArrayList<>();
        widgetValue.add("a");
        widgetValue.add("b");
        widgetValue.add("c");
        widgetValue.add("d");
        return widgetValue;
    }
}
