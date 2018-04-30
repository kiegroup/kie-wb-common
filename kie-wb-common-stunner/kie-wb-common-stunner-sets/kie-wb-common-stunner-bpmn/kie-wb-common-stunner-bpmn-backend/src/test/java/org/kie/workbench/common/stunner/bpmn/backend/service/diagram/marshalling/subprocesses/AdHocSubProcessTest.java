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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.subprocesses;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.Unmarshalling;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.BPMNDiagramMarshallerBase;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AdHocSubProcessTest extends BPMNDiagramMarshallerBase {

    private static final String BPMN_ADHOC_SUBPROCESS_AUTOSTART =
            "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/adHocSubProcessAutostart.bpmn";
    private static final String ADHOC_SUBPROCESS_ID = "_8D223345-9B6F-4AD3-997B-582C9222CC35";
    private static final String USER_TASK_ID = "_E386D64B-70FE-45E5-A190-C65EB8695480";
    private static final String BUSINESS_RULE_TASK_ID = "_5AF47879-8D23-4893-8668-ADF88C3EAD1B";

    @Before
    public void setUp() throws Exception {
        super.init();
    }

    @Test
    public void testOldMarshaller() throws Exception {
        testUnmarshallAddHocSubprocessAutostart(oldMarshaller);
    }

    @Test
    public void testNewMarshaller() throws Exception {
        testUnmarshallAddHocSubprocessAutostart(newMarshaller);
    }

    @Test
    public void testMigration() throws Exception {
        Diagram<Graph, Metadata> oldDiagram = Unmarshalling.unmarshall(oldMarshaller, BPMN_ADHOC_SUBPROCESS_AUTOSTART);
        Diagram<Graph, Metadata> newDiagram = Unmarshalling.unmarshall(newMarshaller, BPMN_ADHOC_SUBPROCESS_AUTOSTART);

        assertDiagramEquals(oldDiagram, newDiagram, BPMN_ADHOC_SUBPROCESS_AUTOSTART);
    }

    private void testUnmarshallAddHocSubprocessAutostart(final DiagramMarshaller marshaller) throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(marshaller, BPMN_ADHOC_SUBPROCESS_AUTOSTART);

        Node<? extends Definition, ?> adHocSubProcessNode = diagram.getGraph().getNode(ADHOC_SUBPROCESS_ID);
        AdHocSubprocess adHocSubprocess = (AdHocSubprocess) adHocSubProcessNode.getContent().getDefinition();

        assertNotNull(adHocSubprocess);

        BPMNGeneralSet generalSet = adHocSubprocess.getGeneral();
        AdHocSubprocessTaskExecutionSet executionSet = adHocSubprocess.getExecutionSet();
        ProcessData processData = adHocSubprocess.getProcessData();
        assertNotNull(generalSet);
        assertNotNull(executionSet);
        assertNotNull(processData);

        assertEquals("AdHoc Sub-process",
                     generalSet.getName().getValue());
        assertEquals("for marshalling test",
                     generalSet.getDocumentation().getValue());

        assertNotNull(executionSet.getAdHocCompletionCondition());
        assertNotNull(executionSet.getAdHocCompletionCondition().getValue());
        assertNotNull(executionSet.getAdHocOrdering());
        assertNotNull(executionSet.getOnEntryAction());
        assertNotNull(executionSet.getOnExitAction());

        assertEquals("varA == null",
                     executionSet.getAdHocCompletionCondition().getValue().getScript());
        assertEquals("mvel",
                     executionSet.getAdHocCompletionCondition().getValue().getLanguage());

        assertEquals("Parallel",
                     executionSet.getAdHocOrdering().getValue());

        assertEquals("adHocVariable:Object",
                     processData.getProcessVariables().getValue());

        Node<? extends Definition, ?> userTaskNode = diagram.getGraph().getNode(USER_TASK_ID);
        UserTask userTask = (UserTask) userTaskNode.getContent().getDefinition();
        assertEquals(true,
                     userTask.getExecutionSet().getAdHocAutostart().getValue());

        Node<? extends Definition, ?> businessRuleTaskNode = diagram.getGraph().getNode(BUSINESS_RULE_TASK_ID);
        BusinessRuleTask businessRuleTask = (BusinessRuleTask) businessRuleTaskNode.getContent().getDefinition();
        assertEquals(true,
                     businessRuleTask.getExecutionSet().getAdHocAutostart().getValue());
    }
}
