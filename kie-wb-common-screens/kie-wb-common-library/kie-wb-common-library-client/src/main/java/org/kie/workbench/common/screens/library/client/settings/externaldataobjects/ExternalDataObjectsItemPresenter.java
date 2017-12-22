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

package org.kie.workbench.common.screens.library.client.settings.externaldataobjects;

import javax.inject.Inject;

import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.soup.project.datamodel.imports.Import;
import org.uberfire.client.mvp.UberElemental;

public class ExternalDataObjectsItemPresenter {

    private final View view;

    private Import import_;
    private ExternalDataObjectsPresenter presenter;

    public interface View extends UberElemental<ExternalDataObjectsItemPresenter>,
                                  IsElement {

        void setTypeName(final String typeName);
    }

    @Inject
    public ExternalDataObjectsItemPresenter(final View view) {
        this.view = view;
    }

    ExternalDataObjectsItemPresenter setup(final Import import_,
                                           final ExternalDataObjectsPresenter presenter) {

        this.import_ = import_;
        this.presenter = presenter;

        view.init(this);
        view.setTypeName(import_.getType());

        return this;
    }

    public void remove() {
        presenter.remove(this);
    }

    public Import getImport() {
        return import_;
    }

    public View getView() {
        return view;
    }
}
