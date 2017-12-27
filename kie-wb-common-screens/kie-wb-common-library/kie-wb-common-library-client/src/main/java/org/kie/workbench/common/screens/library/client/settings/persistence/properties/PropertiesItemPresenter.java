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

import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.screens.datamodeller.model.persistence.Property;
import org.kie.workbench.common.screens.library.client.settings.persistence.PersistencePresenter;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class PropertiesItemPresenter {

    public interface View extends UberElemental<PropertiesItemPresenter>,
                                  IsElement {

        void setName(String name);

        void setValue(String value);
    }

    private final View view;

    private PersistencePresenter presenter;
    private Property property;

    @Inject
    public PropertiesItemPresenter(final View view) {
        this.view = view;
    }

    public PropertiesItemPresenter setup(final Property property,
                                         final PersistencePresenter persistencePresenter) {

        this.property = property;
        this.presenter = persistencePresenter;

        view.init(this);
        view.setName(property.getName());
        view.setValue(property.getValue());

        return this;
    }

    public void remove() {
        presenter.remove(this);
    }

    public Property getProperty() {
        return property;
    }

    public View getView() {
        return view;
    }
}
