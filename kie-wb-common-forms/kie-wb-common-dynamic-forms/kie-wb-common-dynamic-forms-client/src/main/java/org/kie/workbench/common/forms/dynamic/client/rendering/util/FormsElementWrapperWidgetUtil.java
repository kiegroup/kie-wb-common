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

package org.kie.workbench.common.forms.dynamic.client.rendering.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;

public class FormsElementWrapperWidgetUtil {

    private static Map<Object, List<ElementWrapperWidget<?>>> mappedWidgets = new HashMap<>();

    private FormsElementWrapperWidgetUtil() {
    }

    public static Widget getWidget(Object source, HTMLElement element) {
        return getWidget(source, element, ElementWrapperWidget::getWidget);
    }

    public static Widget getWidget(Object source, elemental2.dom.HTMLElement element) {
        return getWidget(source, element, ElementWrapperWidget::getWidget);
    }

    private static <T> Widget getWidget(Object source, T element, Function<T, ElementWrapperWidget> function) {
        ElementWrapperWidget<?> widget = function.apply(element);

        mappedWidgets.computeIfAbsent(source, key -> new ArrayList<>())
                .add(widget);
        return widget;
    }

    public static void clear(Object source) {
        mappedWidgets.computeIfPresent(source, (o, wrapperWidgets) -> {
            wrapperWidgets.forEach(widget -> {
                ElementWrapperWidget.removeWidget(widget);
                widget.removeFromParent();
            });
            return null;
        });
    }
}
