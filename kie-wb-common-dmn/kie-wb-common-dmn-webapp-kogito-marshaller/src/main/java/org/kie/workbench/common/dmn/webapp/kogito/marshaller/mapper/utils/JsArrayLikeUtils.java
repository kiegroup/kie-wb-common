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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;

public class JsArrayLikeUtils {

    private JsArrayLikeUtils() {
        //Private constructor to prevent instantiation
    }

    public static <D> void add(final JsArrayLike<D> jsArrayLike,
                               final D element) {
        final int length = jsArrayLike.getLength();
        jsArrayLike.setLength(length + 1);
        jsArrayLike.setAt(length, element);
    }

    public static <D> void addAll(final JsArrayLike<D> jsArrayLike,
                                  final D... elements) {
        Arrays.stream(elements).forEach(element -> JsArrayLikeUtils.add(jsArrayLike, element));
    }

    public static <D> void remove(final JsArrayLike<D> jsArrayLike,
                                  final int index) {
        int targetIndex = 0;
        for (int sourceIndex = 0; sourceIndex < jsArrayLike.getLength(); sourceIndex++) {
            if (sourceIndex != index) {
                jsArrayLike.setAt(targetIndex++, jsArrayLike.getAt(sourceIndex));
            }
        }
        jsArrayLike.setLength(targetIndex);
    }

    public static <D> List<D> toList(final JsArrayLike<D> jsArrayLike) {
        final List<D> elements = new ArrayList<>();
        if (Objects.nonNull(jsArrayLike)) {
            for (int i = 0; i < jsArrayLike.getLength(); i++) {
                final D element = Js.uncheckedCast(jsArrayLike.getAt(i));
                elements.add(element);
            }
        }
        return elements;
    }

    public static native JsArrayLike<?> getUnwrappedElemetsArray(final JsArrayLike<?> original) /*-{
        var toReturn = original.map(function (arrayItem) {
            var retrieved = arrayItem.value
            var toSet = retrieved == null ? arrayItem : retrieved
            console.log(toSet);
            return toSet;
        });
        return toReturn;
    }-*/;
}
