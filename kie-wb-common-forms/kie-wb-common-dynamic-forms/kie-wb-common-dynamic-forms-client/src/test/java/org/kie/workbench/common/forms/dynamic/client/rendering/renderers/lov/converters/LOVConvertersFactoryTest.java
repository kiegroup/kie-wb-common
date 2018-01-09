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

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class LOVConvertersFactoryTest {

    @Test
    public void testFunctionality() {
        testExisting(BigDecimal.class, BigDecimalListToDoubleListConverter.class);
        testExisting(BigInteger.class, BigIntegerListToLongListConverter.class);
        testExisting(Byte.class, ByteListToLongListConverter.class);
        testExisting(Character.class, CharacterListToStringListConverter.class);
        testExisting(Float.class, FloatListToDoubleListConverter.class);
        testExisting(Integer.class, IntegerListToLongListConverter.class);
        testExisting(Short.class, ShortListToLongListConverter.class);

        testNonExisting(null);
        testNonExisting(" ");
    }

    protected void testExisting(Class type, Class converterClass) {
        Assertions.assertThat(LOVConvertersFactory.getConverter(type.getName()))
                .isNotNull()
                .isInstanceOf(converterClass);
    }

    protected void testNonExisting(String className) {
        Assertions.assertThat(LOVConvertersFactory.getConverter(className))
                .isNull();
    }
}
