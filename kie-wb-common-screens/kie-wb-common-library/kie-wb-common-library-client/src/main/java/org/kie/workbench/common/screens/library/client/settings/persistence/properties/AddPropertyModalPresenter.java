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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.screens.datamodeller.model.persistence.Property;
import org.kie.workbench.common.screens.library.client.settings.persistence.PersistencePresenter;
import org.kie.workbench.common.screens.library.client.settings.util.modal.Elemental2Modal;

@Dependent
public class AddPropertyModalPresenter extends Elemental2Modal<AddPropertyModalPresenter.View> {

    public interface View extends Elemental2Modal.View<AddPropertyModalPresenter> {

        void focus();

        String getName();

        String getValue();

        void clearForm();
    }

    private PersistencePresenter presenter;

    @Inject
    public AddPropertyModalPresenter(final View view) {
        super(view);
    }

    public void setup(final PersistencePresenter presenter) {
        super.setup();
        this.presenter = presenter;
    }

    @Override
    public void show() {
        getView().clearForm();
        super.show();
        getView().focus();
    }

    public void add() {
        presenter.add(new Property(getView().getName(), getView().getValue()));
        hide();
    }

    public void cancel() {
        hide();
    }
}
