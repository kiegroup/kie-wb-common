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

package org.kie.workbench.common.screens.library.client.settings.deployments.items;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.screens.library.client.settings.util.modal.Elemental2Modal;

@Dependent
public class AddParameterModal extends Elemental2Modal<AddParameterModal.View> {

    public interface View extends Elemental2Modal.View<AddParameterModal> {

        void focus();

        String getName();

        String getValue();

        void clearForm();
    }

    private ParametersModal presenter;

    @Inject
    public AddParameterModal(final View view) {
        super(view);
    }

    public void setup(final ParametersModal presenter) {
        this.presenter = presenter;
        super.setup();
    }

    @Override
    public void show() {
        getView().clearForm();
        super.show();
        getView().focus();
    }

    public void add() {
        presenter.add(new ParametersModal.Parameter(getView().getName(), getView().getValue()));
        hide();
    }

    public void remove() {
        hide();
    }
}
