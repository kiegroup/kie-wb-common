/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.client.settings.util.radio;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.inject.Inject;

import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.commons.uuid.UUID;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Templated
public class DynamicRadioGroup<T> implements IsElement {

    private final ManagedInstance<SingleRadioElementView> singleRadioElementViewFactory;
    private final Elemental2DomUtil elemental2DomUtil;

    private Consumer<String> onChange;
    private Map<T, SingleRadioElementView> viewsByElement;

    @Inject
    public DynamicRadioGroup(final ManagedInstance<SingleRadioElementView> singleRadioElementViewFactory,
                             final Elemental2DomUtil elemental2DomUtil) {

        this.singleRadioElementViewFactory = singleRadioElementViewFactory;
        this.elemental2DomUtil = elemental2DomUtil;
    }

    public void init(final List<T> elements,
                     final Function<T, String> valueMapper,
                     final Function<T, String> labelMapper) {

        final String name = UUID.uuid();

        elemental2DomUtil.removeAllElementChildren(getElement());

        viewsByElement = elements.stream().collect(toMap(identity(), element -> {
            final SingleRadioElementView view = singleRadioElementViewFactory.get();
            final String value = valueMapper.apply(element);
            final String label = labelMapper.apply(element);
            return view.setup(this, name, value, label);
        }));

        viewsByElement.values().forEach(view -> {
            getElement().appendChild(view.getElement());
        });
    }

    public void onChange(final Consumer<String> onChange) {
        this.onChange = onChange;
    }

    public void notifyChange(final String value) {
        onChange.accept(value);
    }

    public void setValue(final T value) {
        viewsByElement.get(value).check();
    }

    public String getValue() {
        return viewsByElement.values().stream()
                .filter(SingleRadioElementView::isChecked)
                .findFirst().map(SingleRadioElementView::getValue)
                .orElse(null);
    }
}
