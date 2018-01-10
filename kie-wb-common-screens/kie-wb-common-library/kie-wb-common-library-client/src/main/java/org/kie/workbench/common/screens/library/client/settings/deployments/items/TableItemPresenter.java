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

import elemental2.dom.Element;
import org.kie.workbench.common.screens.datamodeller.model.kiedeployment.KieDeploymentDescriptorContent.BlergsModel;
import org.kie.workbench.common.screens.datamodeller.model.kiedeployment.KieDeploymentDescriptorContent.Resolver;
import org.kie.workbench.common.screens.library.client.settings.deployments.DeploymentsPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.KieEnumSelectElement;
import org.kie.workbench.common.screens.library.client.settings.util.ListItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.UberElementalListItem;

@Dependent
public class TableItemPresenter extends ListItemPresenter<BlergsModel, DeploymentsPresenter, TableItemPresenter.View> {

    private final ParametersModal parametersModal;
    private final KieEnumSelectElement<Resolver> resolversSelect;

    private BlergsModel model;
    private DeploymentsPresenter parentPresenter;

    @Inject
    public TableItemPresenter(final View view,
                              final ParametersModal parametersModal,
                              final KieEnumSelectElement<Resolver> resolversSelect) {
        super(view);
        this.parametersModal = parametersModal;
        this.resolversSelect = resolversSelect;
    }

    @Override
    public TableItemPresenter setup(final BlergsModel model,
                                    final DeploymentsPresenter parentPresenter) {
        this.model = model;
        this.parentPresenter = parentPresenter;

        parametersModal.setup(this);

        resolversSelect.setup(view.getResolversContainer(), Resolver.values());
        resolversSelect.setValue(model.getResolver());
        resolversSelect.onChange(resolver -> {
            model.setResolver(resolver);
            parentPresenter.fireChangeEvent();
        });

        view.init(this);

        view.setId(model.getId());
        view.setParametersCount(model.getParameters().size());

        return this;
    }

    @Override
    public void remove() {
        super.remove();
        fireChangeEvent();
    }

    public void fireChangeEvent() {
        parentPresenter.fireChangeEvent();
    }

    public void showParametersModal() {
        parametersModal.show();
    }

    @Override
    public BlergsModel getObject() {
        return model;
    }

    public void add(final ParametersModal.Parameter parameter) {
        model.getParameters().put(parameter.getName(), parameter.getValue());
        view.setParametersCount(model.getParameters().size());
        fireChangeEvent();
    }

    public void remove(final ParametersModal.Parameter parameter) {
        model.getParameters().remove(parameter.getName());
        view.setParametersCount(model.getParameters().size());
        fireChangeEvent();
    }

    public interface View extends UberElementalListItem<TableItemPresenter> {

        Element getResolversContainer();

        void setId(final String id);

        void setParametersCount(final int size);
    }
}
