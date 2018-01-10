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

package org.kie.workbench.common.screens.library.client.settings.deployments.items;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.client.settings.util.ListItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.ListPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.modal.Elemental2Modal;

import static java.util.stream.Collectors.toList;

@Dependent
public class ParametersModal extends Elemental2Modal<ParametersModalView> {

    private final ParametersListPresenter parametersListPresenter;
    private final AddParameterModal addParameterModal;
    private TableItemPresenter parentPresenter;

    @Inject
    public ParametersModal(final ParametersModalView view,
                           final ParametersListPresenter parametersListPresenter,
                           final AddParameterModal addParameterModal) {

        super(view);
        this.parametersListPresenter = parametersListPresenter;
        this.addParameterModal = addParameterModal;
    }

    public void showAddParameterModal() {
        addParameterModal.show();
    }

    public void setup(final TableItemPresenter parentPresenter) {

        this.parentPresenter = parentPresenter;

        addParameterModal.setup(this);

        final List<Parameter> parametersList = parentPresenter.getObject()
                .getParameters()
                .entrySet()
                .stream()
                .map(s -> new Parameter(s.getKey(), s.getValue()))
                .collect(toList());

        parametersListPresenter.setup(
                getView().getParametersTable(),
                parametersList,
                (parameter, presenter) -> presenter.setup(parameter, this));

        super.setup();
    }

    public void add(final Parameter parameter) {
        parametersListPresenter.add(parameter);
        parentPresenter.add(parameter);
    }

    public void remove(final ParameterItemPresenter parameterItemPresenter) {
        parametersListPresenter.remove(parameterItemPresenter);
        parentPresenter.remove(parameterItemPresenter.getObject());
    }

    @Dependent
    public static class ParametersListPresenter extends ListPresenter<Parameter, ParameterItemPresenter> {

        @Inject
        public ParametersListPresenter(final ManagedInstance<ParameterItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }

    @Dependent
    public static class ParameterItemPresenter extends ListItemPresenter<Parameter, ParametersModal, ParametersModalView.Parameter> {

        private Parameter parameter;
        private ParametersModal parentPresenter;

        @Inject
        public ParameterItemPresenter(final ParametersModalView.Parameter parametersModalView) {
            super(parametersModalView);
        }

        @Override
        public ParameterItemPresenter setup(final Parameter parameter,
                                            final ParametersModal parentPresenter) {

            this.parameter = parameter;
            this.parentPresenter = parentPresenter;

            view.init(this);
            view.setName(parameter.name);
            view.setValue(parameter.value);
            return this;
        }

        @Override
        public Parameter getObject() {
            return parameter;
        }

        @Override
        public void remove() {
            parentPresenter.remove(this);
        }
    }

    public static class Parameter {

        public final String name;
        public final String value;

        public Parameter(final String name, final String value) {
            this.name = name;
            this.value = value;
        }
    }
}
