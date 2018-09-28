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

package org.kie.workbench.common.screens.library.client.settings.util.sections;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.client.settings.util.modal.doublevalue.AddDoubleValueModal;
import org.kie.workbench.common.screens.library.client.settings.util.modal.single.AddSingleValueModal;
import org.kie.workbench.common.widgets.client.widget.ListItemPresenter;
import org.kie.workbench.common.widgets.client.widget.ListPresenter;

import elemental2.dom.Element;

public abstract class SectionListPresenter<T, P extends ListItemPresenter<T, ?, ?>> extends ListPresenter<T, P> {

    private AddSingleValueModal singleValueModal;
    private AddDoubleValueModal doubleValueModal;

    public SectionListPresenter (final ManagedInstance<P> itemPresenters){
        super(itemPresenters);
    }

    public void setup(final Element listElement,
          final List<T> objects,
          final BiConsumer<T, P> itemPresenterConfigurator,
          final AddSingleValueModal singleValuemodal,
          final AddDoubleValueModal doubleValueModal){
        super.setup(listElement,objects,itemPresenterConfigurator);
        this.singleValueModal = singleValuemodal;
        this.doubleValueModal = doubleValueModal;
    }

    public void setSingleValueEditModal(final String value) {
        if(singleValueModal != null) {
            singleValueModal.getView().setValue(value);
        }
    }
    public void showSingleValueAddModal(final Consumer<String> onAdd){
        if(singleValueModal != null) {
            singleValueModal.show(onAdd);
        }
    }
    
    public void showSingleValueEditModal( final String value,final Consumer<String> onAdd) {
        if(singleValueModal != null) {
            singleValueModal.getView().setValue(value);
            singleValueModal.showEditModel(onAdd);
        }
    }
    
    public void showDoubleValueAddModal(final BiConsumer<String, String> onAdd){
        if(doubleValueModal != null) {
            doubleValueModal.show(onAdd);
        }
    }
    
    public void showDoubleValueEditModal(final String name, String value, final BiConsumer<String, String> onAdd) {
        if(doubleValueModal != null) {
            doubleValueModal.getView().setName(name);
            doubleValueModal.getView().setValue(value);
            doubleValueModal.showEditModal(onAdd);
        }
    }
}
