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
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.databinding.client.api.Converter;

public class LOVConvertersFactory {
    
    private static Map<String, Converter> converters = new HashMap<>();
    
    static {
        converters.put(Character.class.getName(), new CharacterListToStringListConverter());

        converters.put(BigInteger.class.getName(), new BigIntegerListToLongListConverter());
        converters.put(Byte.class.getName(), new ByteListToLongListConverter());
        converters.put(Integer.class.getName(), new IntegerListToLongListConverter());
        converters.put(Short.class.getName(), new ShortListToLongListConverter());

        converters.put(BigDecimal.class.getName(), new BigDecimalListToDoubleListConverter());
        converters.put(Float.class.getName(), new FloatListToDoubleListConverter());
    }

    public static Converter getConverter(String type) {
        return converters.get(type);
    }

}
