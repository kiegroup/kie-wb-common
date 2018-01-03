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

package org.kie.workbench.common.screens.library.client.settings.persistence.properties;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class NewPropertyPopupView implements NewPropertyPopupPresenter.View {

    @Inject
    @DataField("name")
    private HTMLInputElement name;

    @Inject
    @DataField("value")
    private HTMLInputElement value;

    @Inject
    @DataField("add-button")
    private HTMLButtonElement addButton;

    @Inject
    @DataField("cancel-button")
    private HTMLButtonElement cancelButton;

    private NewPropertyPopupPresenter presenter;

    @Override
    public void init(final NewPropertyPopupPresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("add-button")
    private void onAddClicked(final ClickEvent ignore) {
        presenter.add();
    }

    @EventHandler("cancel-button")
    private void onCancelClicked(final ClickEvent ignore) {
        presenter.cancel();
    }

    @Override
    public String getName() {
        return name.value;
    }

    @Override
    public String getValue() {
        return value.value;
    }
}
