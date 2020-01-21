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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.listitems;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.editors.ImportsEditorView;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.editors.wsdlimport.WSDLImportsEditor;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.CustomDataTypeTextBox;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.WSDLImport;
import org.mockito.Mockito;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WSDLImportListItemViewImplTest extends ImportListItemViewImplTest<WSDLImport> {

    WSDLImportListItemViewImpl concreteTested;

    @Before
    public void setUp() {
        super.setUp();

        concreteTested = (WSDLImportListItemViewImpl) tested;
        concreteTested.importDataBinder = dataBinder;
        concreteTested.importItem = htmlTableRowElement;
        concreteTested.location = mock(CustomDataTypeTextBox.class);
        concreteTested.namespace = mock(CustomDataTypeTextBox.class);
        concreteTested.deleteButton = button;

        when(concreteTested.getImportDataBinder().getModel()).thenReturn(mockModelValue());

        Mockito.reset(concreteTested);
    }

    @Test
    public void init() {
        super.init();

        verify(concreteTested).initCustomDataTypeTextBox(concreteTested.location);
        verify(concreteTested).initCustomDataTypeTextBox(concreteTested.namespace);
    }

    @Test
    public void getLocation() {
        concreteTested.getLocation();
        verify(concreteTested).getValue();
        verify(concreteTested.getValue()).getLocation();
    }

    @Test
    public void setLocation() {
        concreteTested.setLocation(anyString());
        verify(concreteTested).getValue();
        verify(concreteTested.getValue()).setLocation(anyString());
    }

    @Test
    public void getNamespace() {
        concreteTested.getNamespace();
        verify(concreteTested).getValue();
        verify(concreteTested.getValue()).getNamespace();
    }

    @Test
    public void setNamespace() {
        concreteTested.setNamespace(anyString());
        verify(concreteTested).getValue();
        verify(concreteTested.getValue()).setNamespace(anyString());
    }

    @Override
    ImportListItemViewImpl<WSDLImport> spyListItem() {
        return spy(WSDLImportListItemViewImpl.class);
    }

    @Override
    WSDLImport mockModelValue() {
        return mock(WSDLImport.class);
    }

    @Override
    ImportsEditorView.Presenter<WSDLImport> mockPresenter() {
        return mock(WSDLImportsEditor.class);
    }
}