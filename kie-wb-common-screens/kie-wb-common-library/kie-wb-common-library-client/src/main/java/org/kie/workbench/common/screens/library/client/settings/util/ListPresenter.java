/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.util;

import java.util.List;
import java.util.function.BiConsumer;

import elemental2.dom.Element;
import org.jboss.errai.ioc.client.api.ManagedInstance;

import static java.util.stream.Collectors.toList;

public abstract class ListPresenter<T, P extends ListItemPresenter<T, ?, ?>> {

    private final ManagedInstance<P> itemPresenters;

    private List<T> list;
    private Element listElement;
    private BiConsumer<T, P> itemPresenterConfigurator;

    public ListPresenter(final ManagedInstance<P> itemPresenters) {
        this.itemPresenters = itemPresenters;
    }

    public void setup(final Element listElement,
                      final List<T> list) {

        setup(listElement, list, (o, p) -> {
        });
    }

    public void setup(final Element listElement,
                      final List<T> list,
                      final BiConsumer<T, P> itemPresenterConfigurator) {

        this.list = list;
        this.listElement = listElement;
        this.itemPresenterConfigurator = itemPresenterConfigurator;

        this.listElement.innerHTML = "";
        this.list.forEach(this::addToListElement);
    }

    public void setupWithPresenters(final Element listElement,
                                    final List<P> presenters,
                                    final BiConsumer<T, P> itemPresenterConfigurator) {

        this.list = presenters.stream().map(p -> p.getObject()).collect(toList());
        this.listElement = listElement;
        this.itemPresenterConfigurator = itemPresenterConfigurator;

        this.listElement.innerHTML = "";
        presenters.forEach(this::addPresenter);
    }

    public void add(final T o) {
        addToListElement(o);
        list.add(o);
    }

    private void addToListElement(final T o) {
        addPresenter(newPresenterFor(o));
    }

    private void addPresenter(final P presenter) {
        listElement.appendChild(presenter.getView().getElement());
    }

    private P newPresenterFor(final T o) {
        final P listItemPresenter = this.itemPresenters.get();
        listItemPresenter.setListPresenter(this);
        itemPresenterConfigurator.accept(o, listItemPresenter);
        return listItemPresenter;
    }

    public void remove(final ListItemPresenter<T, ?, ?> listItemPresenter) {
        list.remove(listItemPresenter.getObject());
        listElement.removeChild(listItemPresenter.getView().getElement());
    }

    public List<T> getList() {
        return list;
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }
}
