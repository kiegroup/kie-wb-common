/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.editors;

import java.util.List;

import javax.annotation.PostConstruct;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TableElement;
import org.gwtbootstrap3.client.ui.Button;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.listitems.ImportListItemView;

public abstract class ImportsEditorViewImpl<T, U extends ImportListItemView<T>> implements ImportsEditorView<T> {

    protected abstract DataBinder<List<T>> getImportsDataBinder();

    protected abstract ListComponent<T, U> getImportComponents();

    protected abstract TableElement getImportsTable();

    protected abstract Button getAddButton();

    protected abstract Presenter<T> getPresenter();

    @PostConstruct
    public void init() {
        getPresenter().setView(this);
        getAddButton().addClickHandler(clickEvent -> getPresenter().addImport());
        getImportComponents().addComponentCreationHandler(component -> component.setPresenter(getPresenter()));
        getImportsDataBinder().addPropertyChangeHandler(propertyChangeEvent -> updateImportsTable());
    }

    @Override
    public List<T> getImports() {
        return getImportsDataBinder().getModel();
    }

    @Override
    public void setImports(List<T> imports) {
        getImportsDataBinder().setModel(imports);
        updateImportsTable();
    }

    protected void hideImportsTable() {
        getImportsTable().getStyle().setDisplay(Style.Display.NONE);
    }

    protected void showImportsTable() {
        getImportsTable().getStyle().setDisplay(Style.Display.TABLE);
    }

    protected void updateImportsTable() {
        if (getImports().isEmpty()) {
            hideImportsTable();
        } else {
            showImportsTable();
        }
    }
}
