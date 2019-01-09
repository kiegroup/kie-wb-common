/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.documentation;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.ui.Button;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.documentation.BPMNDocumentationService;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.documentation.DefaultDiagramDocumentationView;
import org.kie.workbench.common.stunner.core.documentation.model.DocumentationOutput;

@Dependent
@Specializes
@Templated
public class BPMNDocumentationView extends DefaultDiagramDocumentationView {

    private BPMNDocumentationService documentationService;

    @Inject
    @Named("documentationDiv")
    @DataField
    private HTMLElement documentationDiv;

    @Inject
    @DataField
    private Button exportToPDFButton;

    @Inject
    public BPMNDocumentationView(BPMNDocumentationService documentationService) {
        this.documentationService = documentationService;
    }

    @Override
    public BPMNDocumentationView initialize(Diagram diagram) {
        super.initialize(diagram);

        //should be enabled when export it implemented
        exportToPDFButton.setVisible(false);
        exportToPDFButton.setText("Export PDF");

        return refresh();
    }

    @Override
    public BPMNDocumentationView refresh() {
        documentationDiv.innerHTML = getDiagram()
                .map(documentationService::generate)
                .map(DocumentationOutput::getValue)
                .orElse("");
        return this;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    //todo: tests on export pdf
//    private void exportToPDF(Element html) {
//
//        preferences.load(prefs -> {
//            final PdfExportPreferences pdfPreferences = prefs.getPdfPreferences();
//            PdfDocument pdfDocument =
//                    PdfDocument.create(PdfExportPreferences.create(PdfExportPreferences.Orientation.LANDSCAPE,
//                                                                   pdfPreferences.getUnit(),
//                                                                   pdfPreferences.getFormat()));
//            pdfDocument.addHTML(html, 15, 15);
//            pdfFileExport.export(pdfDocument, "documentation.pdf");
//        }, error -> {
//            GWT.log("Error on documentation");
//        });
//    }
}