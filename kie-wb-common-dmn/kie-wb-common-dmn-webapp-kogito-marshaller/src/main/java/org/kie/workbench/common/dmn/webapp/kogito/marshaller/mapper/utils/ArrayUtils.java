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
package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.utils;

import java.util.Arrays;

public class ArrayUtils {

    private ArrayUtils() {
        //Private constructor to prevent instantiation
    }

    public static <D> D[] add(final D[] array,
                              final D element) {
        final D[] clone = Arrays.copyOf(array, array.length + 1);
        clone[array.length] = element;
        return clone;
    }
}
