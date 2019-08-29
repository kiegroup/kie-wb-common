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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.namespace.QName;

import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;

public class JsUtils {

    private JsUtils() {
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
        Arrays.stream(elements).forEach(element -> JsUtils.add(jsArrayLike, element));
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
            final JsArrayLike<D> unwrapped = getUnwrappedElementsArray(jsArrayLike);
            for (int i = 0; i < unwrapped.getLength(); i++) {
                final D element = Js.uncheckedCast(unwrapped.getAt(i));
                elements.add(element);
            }
        }
        return elements;
    }

    public static native <D> JsArrayLike<D> getUnwrappedElementsArray(final JsArrayLike<D> original) /*-{
        var toReturn = original.map(function (arrayItem) {
            var retrieved = arrayItem.value
            var toSet = retrieved == null ? arrayItem : retrieved
            console.log(toSet);
            return toSet;
        });
        return toReturn;
    }-*/;

    public static native Object getUnwrappedElement(final Object original) /*-{
        var toReturn = original.value;
        var toSet = toReturn == null ? original : toReturn;
        console.log(toSet);
        return toSet;
    }-*/;

    /**
     * Extracts the otherAttributes property from a JavaScriptObject to a _regular_ Java Map.
     * @param original
     * @return
     */
    public static Map<QName, String> toAttributesMap(final Object original) {
        final Map<QName, String> attributes = new HashMap<>();
        toAttributesMap(attributes, original);
        return attributes;
    }

    private static native void toAttributesMap(final Map<QName, String> toReturn,
                                               final Object original) /*-{
        var keys = Object.keys(original);
        for (var i = 0; i < keys.length; i++) {
            var key = keys[i];
            var value = original[key];
            @org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.utils.JsUtils::put(Ljava/util/Map;Ljava/lang/String;Ljava/lang/String;)(toReturn, key, value);
        }
    }-*/;

    private static void put(final Map<QName, String> toReturn,
                            final String qNameAsString,
                            final String value) {
        toReturn.put(QName.valueOf(qNameAsString), value);
    }
}
