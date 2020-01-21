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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.popup;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Button;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.editors.defaultimport.DefaultImportsEditorViewImpl;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.editors.wsdlimport.WSDLImportsEditorViewImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.ImportsValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.WSDLImport;
import org.uberfire.client.views.pfly.widgets.Modal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ImportsPopupTest {

    private ImportsPopup tested;

    @Before
    public void setUp() {
        GwtMockito.initMocks(this);

        tested = spy(ImportsPopup.class);
        tested.modal = GWT.create(Modal.class);
        tested.okButton = GWT.create(Button.class);

        tested.defaultImportsEditorWidgetView = GWT.create(DefaultImportsEditorViewImpl.class);
        when(tested.defaultImportsEditorWidgetView.getImports()).thenReturn(mock(List.class));

        tested.wsdlImportsEditorWidgetView = GWT.create(WSDLImportsEditorViewImpl.class);
        when(tested.wsdlImportsEditorWidgetView.getImports()).thenReturn(mock(List.class));
    }

    @Test
    public void getDefaultImports() {
        tested.getDefaultImports();
        verify(tested.defaultImportsEditorWidgetView).getImports();
    }

    @Test
    public void setDefaultImports() {
        List<DefaultImport> importsList = mock(List.class);
        tested.setDefaultImports(importsList);
        verify(tested.defaultImportsEditorWidgetView).setImports(importsList);
    }

    @Test
    public void getWSDLImports() {
        tested.getWSDLImports();
        verify(tested.wsdlImportsEditorWidgetView).getImports();
    }

    @Test
    public void setWSDLImports() {
        List<WSDLImport> importsList = mock(List.class);
        tested.setWSDLImports(importsList);
        verify(tested.wsdlImportsEditorWidgetView).setImports(importsList);
    }

    @Test
    public void setCallback() {
        ImportsPopup.DataCallback callback = mock(ImportsPopup.DataCallback.class);

        tested.setDataCallback(callback);

        assertEquals(callback, tested.callback);
    }

    @Test
    public void show() {
        tested.show();
        verify(tested.modal).show();
    }

    @Test
    public void handleOKButton() {
        tested.callback = null;
        tested.handleOKButton(any(ClickEvent.class));
        tested.callback = mock(ImportsPopup.DataCallback.class);
        tested.handleOKButton(any(ClickEvent.class));

        verify(tested.callback).getData(any(ImportsValue.class));
        verify(tested.modal, times(2)).hide();
    }
}