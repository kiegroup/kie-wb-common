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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.editors.defaultimport;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.TableElement;
import org.gwtbootstrap3.client.ui.Button;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.jboss.errai.databinding.client.components.ListContainer;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.editors.ImportsEditorViewImpl;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.listitems.DefaultImportListItemViewImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;

@Dependent
@Templated("DefaultImportsEditorViewImpl.html")
public class DefaultImportsEditorViewImpl
        extends ImportsEditorViewImpl<DefaultImport, DefaultImportListItemViewImpl> {

    @Inject
    @AutoBound
    DataBinder<List<DefaultImport>> importsDataBinder;

    @Inject
    @DataField
    @Bound
    @ListContainer("tbody")
    ListComponent<DefaultImport, DefaultImportListItemViewImpl> importComponents;

    @Inject
    @DataField
    TableElement importsTable;

    @Inject
    @DataField
    Button addImportButton;

    @Inject
    DefaultImportsEditor presenter;

    @Override
    protected DataBinder<List<DefaultImport>> getImportsDataBinder() {
        return importsDataBinder;
    }

    @Override
    protected ListComponent<DefaultImport, DefaultImportListItemViewImpl> getImportComponents() {
        return importComponents;
    }

    @Override
    public TableElement getImportsTable() {
        return importsTable;
    }

    @Override
    public Button getAddButton() {
        return addImportButton;
    }

    @Override
    protected Presenter<DefaultImport> getPresenter() {
        return presenter;
    }
}
