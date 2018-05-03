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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.processes;

import java.util.List;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.dd.di.DiagramElement;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.DeclarationList;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.DefinitionsConverter;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.BPMNDiagramMarshallerBase;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProcessTest extends BPMNDiagramMarshallerBase {
    private static final String BPMN_DUPLICATE_ELEMENTS_FILE_PATH =
            "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/subprocessDuplicateElements.bpmn";

    private static final String DIAGRAM_ID = "_WiJ1oDEHEeiPapT7xRTz5Q";

    {
        super.init();
    }

    @Test
    public void RHPAM_629() throws Exception {
        Diagram<Graph, Metadata> d = unmarshall(newMarshaller, BPMN_DUPLICATE_ELEMENTS_FILE_PATH);
        DefinitionsConverter definitionsConverter =
                new DefinitionsConverter(d.getGraph());

        Definitions definitions =
                definitionsConverter.toDefinitions();

        List<DiagramElement> planeElements = definitions.getDiagrams().get(0).getPlane().getPlaneElement();
        assertEquals(10, planeElements.size());

        System.out.println("");

    }

}
