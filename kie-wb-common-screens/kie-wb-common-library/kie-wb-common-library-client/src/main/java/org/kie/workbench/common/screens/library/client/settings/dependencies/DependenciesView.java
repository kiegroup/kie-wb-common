/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.dependencies;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;

@Templated
public class DependenciesView implements DependenciesPresenter.View,
                                         IsElement {

    private DependenciesPresenter presenter;

    @Inject
    private TranslationService translationService;

    @Inject
    @DataField("table")
    private HTMLDivElement table;

    @Inject
    @DataField("select-all")
    private HTMLInputElement selectAll;

    @Inject
    @DataField("add")
    private HTMLButtonElement add;

    @Inject
    @DataField("add-from-repository")
    private HTMLButtonElement addFromRepository;

    @Override
    public void init(final DependenciesPresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("add")
    public void add(final ClickEvent event) {
        presenter.add();
    }

    @EventHandler("add-from-repository")
    public void addFromRepository(final ClickEvent event) {
        presenter.addFromRepository();
    }

    @Override
    public void addItem(final DependenciesItemPresenter.View dependenciesItemView) {
        table.appendChild(dependenciesItemView.getElement());
    }

    @Override
    public void showBusyIndicator() {
        showBusyIndicator(translationService.format(LibraryConstants.Loading));
    }

    @Override
    public void showBusyIndicator(final String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }
}
