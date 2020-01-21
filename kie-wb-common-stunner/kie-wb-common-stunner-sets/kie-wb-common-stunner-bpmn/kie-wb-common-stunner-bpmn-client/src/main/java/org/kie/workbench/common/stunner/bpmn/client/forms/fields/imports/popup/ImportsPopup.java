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

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import org.gwtbootstrap3.client.ui.Button;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.editors.defaultimport.DefaultImportsEditorViewImpl;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.editors.wsdlimport.WSDLImportsEditorViewImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.ImportsValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.WSDLImport;
import org.uberfire.client.views.pfly.widgets.Modal;

@Templated
public class ImportsPopup implements IsElement {

    public interface DataCallback {

        void getData(ImportsValue importsValue);
    }

    @Inject
    @DataField
    Modal modal;

    @Inject
    @DataField
    DefaultImportsEditorViewImpl defaultImportsEditorWidgetView;

    @Inject
    @DataField
    WSDLImportsEditorViewImpl wsdlImportsEditorWidgetView;

    @Inject
    @DataField
    Button okButton;

    ImportsPopup.DataCallback callback;

    public List<DefaultImport> getDefaultImports() {
        return defaultImportsEditorWidgetView.getImports();
    }

    public void setDefaultImports(List<DefaultImport> defaultImports) {
        defaultImportsEditorWidgetView.setImports(defaultImports);
    }

    public List<WSDLImport> getWSDLImports() {
        return wsdlImportsEditorWidgetView.getImports();
    }

    public void setWSDLImports(List<WSDLImport> wsdlImports) {
        wsdlImportsEditorWidgetView.setImports(wsdlImports);
    }

    public void setDataCallback(ImportsPopup.DataCallback callback) {
        this.callback = callback;
    }

    public void show() {
        modal.show();
    }

    @EventHandler("okButton")
    public void handleOKButton(final ClickEvent e) {
        if (callback != null) {
            ImportsValue importsValue = new ImportsValue(getDefaultImports(), getWSDLImports());
            callback.getData(importsValue);
        }
        modal.hide();
    }
}
