/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.workitem;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNCategories;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(GwtMockitoTestRunner.class)
public class WorkItemDefinitionClientParserTest {

    final static String WID = "  [\n" +
            "    [\n" +
            "      \"name\" : \"Email\",\n" +
            "      \"parameters\" : [\n" +
            "        \"From\" : new StringDataType()   ,   \n" +
            "        \"To\" : new StringDataType(),\n" +
            "        \"Subject\" :\n new StringDataType()  , \n" +
            "        \"Body\"\n : new StringDataType()\n" +
            "      ],\n" +
            "      \"displayName\" : \"Email\"  ,  \n" +
            "      \"documentation\" : \"Some documentation\"  ,  \n" +
            "      \"icon\" : \"defaultemailicon.gif\"   \n" +
            "    ]\n" +
            "    ,\n" +
            "   \n" +
            "\n" +
            "\n  " +
            "    [\n" +
            "      \"name\" : \"IncidentPriorityService\",\n" +
            "      \"parameters\" : [   \n" +
            "        \"Incident\" : new ObjectDataType()\n" +
            "      ],     \n  " +
            "      \"results\" :  [  \n " +
            "          \"IncidentPriority\" : new ObjectDataType()  ,  \n" +
            "      ],\n" +
            "      \"displayName\" : \"Incident Priority Service\" ,  \n" +
            "      \"icon\" : \"incidentpriorityicon.png\"\n" +
            "    ],\n" +
            "    [\n" +
            "      \"name\" : \"Rest\",\n" +
            "      \"parameters\" : [\n" +
            "          \"ContentData\" : new StringDataType(),\n" +
            "          \"Url\" : new StringDataType(),\n" +
            "          \"Method\" : new StringDataType(),\n" +
            "          \"ConnectTimeout\" : new StringDataType(),\n" +
            "          \"ReadTimeout\" : new StringDataType()   ,   \n" +
            "          \"Username\" : new StringDataType(),\n" +
            "          \"Password\" : new StringDataType()\n" +
            "      ],\n" +
            "      \"results\" : [\n" +
            "          \"Result\" : new ObjectDataType(),\n" +
            "      ],\n" +
            "      \"displayName\" : \"REST\",\n" +
            "      \"icon\" : \"defaultservicenodeicon.png\"\n" +
            "    ],\n" +
            "     [\n" +
            "      \"name\" : \"Milestone\",\n" +
            "      \"parameters\" : [\n" +
            "          \"Condition\" : new StringDataType()\n" +
            "      ],\n" +
            "      \"displayName\" : \"Milestone\",\n" +
            "      \"icon\" : \"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAgAAAAIACAYAAAD0eNT6AAAYK0lEQVR4nO3de9z1+Vzv8dcMcxCjDMMwjHMOUw5D52ypFMmmaJuaKFTa6YBSm+hgt4lIOmt33nJKg8gWiYrHVDpK2zk1coxhNDOYw93+Y/VoYua+5z5c1/VZa/2ez8fj9b+xfuv9Xfd1rev3KwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA23rWrU6qTqxOr42f/5wAAR+rY6ubV3apvrn6o+uXqFdWbqwuqf7uCLqk+VL21em31gurp1fdUX13dojp67/4zAIBPdUx1p+ph1dOq367+rHpvta8rPuB3oo9Vf1E9s/qW6jbVUbv83woAi3WL6huqn6rObnUQ79Yhf6h9oHpuq582XHeX/vsBYOud1OpH7k+oXt7qx/LTh/zBdmn1J9V35cMAABzQcdW9Wv2e/p3NH+I71cXVi//9v813BwCgukb1gFY\",\n" +
            "      \"category\" : \"Milestone\"\n" +
            "      ]\n" +
            "  ]";

    final static String EMAIL_WID_EXTRACTED_PARAMETERS = "|Body:String,From:String,Subject:String,To:String|";

    final static String INCIDENT_WID_EXTRACTED_PARAMETERS = "|Incident:java.lang.Object|";

    final static String INCIDENT_WID_EXTRACTED_RETURN_PARAMETERS = "|IncidentPriority:java.lang.Object|";

    final static String REST_WID_EXTRACTED_PARAMETERS = "|ConnectTimeout:String,ContentData:String,Method:String," +
            "Password:String,ReadTimeout:String,Url:String,Username:String|";

    final static String REST_WID_RETURN_EXTRACTED_PARAMETERS = "|Result:java.lang.Object|";

    final static private String ICON_64 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAgAAAAIACAYAAAD0eNT6AAAYK0lEQVR4nO3de9z1+Vzv8dcMcxCjDMMwjHMOUw5D52ypFMmmaJuaKFTa6YBSm+hgt4lIOmt33nJKg8gWiYrHVDpK2zk1coxhNDOYw93+Y/VoYua+5z5c1/VZa/2ez8fj9b+xfuv9Xfd1rev3KwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA23rWrU6qTqxOr42f/5wAAR+rY6ubV3apvrn6o+uXqFdWbqwuqf7uCLqk+VL21em31gurp1fdUX13dojp67/4zAIBPdUx1p+ph1dOq367+rHpvta8rPuB3oo9Vf1E9s/qW6jbVUbv83woAi3WL6huqn6rObnUQ79Yhf6h9oHpuq582XHeX/vsBYOud1OpH7k+oXt7qx/LTh/zBdmn1J9V35cMAABzQcdW9Wv2e/p3NH+I71cXVi//9v813BwCgukb1gFY";

    @Test
    public void emptyWidsTest() {
        List<WorkItemDefinition> defs = WorkItemDefinitionClientParser.parse("");
        assertTrue(defs.isEmpty());
        defs = WorkItemDefinitionClientParser.parse("[]");
        assertTrue(defs.isEmpty());
        defs = WorkItemDefinitionClientParser.parse("[\n]");
        assertTrue(defs.isEmpty());
        defs = WorkItemDefinitionClientParser.parse(null);
        assertTrue(defs.isEmpty());
        defs = WorkItemDefinitionClientParser.parse("");
        assertTrue(defs.isEmpty());
    }

    @Test
    public void testWidParseLineFeed() {
        testWidParse(WID);
    }

    @Test
    public void testWidParseCarriageReturn() {
        testWidParse(WID.replace("\n", "\r"));
    }

    @Test
    public void testWidParseCarriageReturnAndLineFeed() {
        testWidParse(WID.replace("\n", "\r\n"));
    }

    private void testWidParse(final String wid) {
        List<WorkItemDefinition> defs = WorkItemDefinitionClientParser.parse(wid);
        assertEquals(4, defs.size());
        WorkItemDefinition wid1 = defs.get(0);
        assertEquals("Email", wid1.getName());
        assertEquals("Email", wid1.getDisplayName());
        assertEquals("defaultemailicon.gif", wid1.getIconDefinition().getUri());
        assertEquals(BPMNCategories.CUSTOM_TASKS, wid1.getCategory());
        assertEquals("Some documentation", wid1.getDocumentation());
        assertTrue(wid1.getResults().isEmpty());
        assertEquals(EMAIL_WID_EXTRACTED_PARAMETERS, wid1.getParameters());

        WorkItemDefinition wid2 = defs.get(1);
        assertEquals("IncidentPriorityService", wid2.getName());
        assertEquals("Incident Priority Service", wid2.getDisplayName());
        assertEquals("incidentpriorityicon.png", wid2.getIconDefinition().getUri());
        assertEquals(BPMNCategories.CUSTOM_TASKS, wid2.getCategory());
        assertEquals(INCIDENT_WID_EXTRACTED_RETURN_PARAMETERS, wid2.getResults());
        assertEquals(INCIDENT_WID_EXTRACTED_PARAMETERS, wid2.getParameters());

        WorkItemDefinition wid3 = defs.get(2);
        assertEquals("Rest", wid3.getName());
        assertEquals("REST", wid3.getDisplayName());
        assertEquals("defaultservicenodeicon.png", wid3.getIconDefinition().getUri());
        assertEquals(REST_WID_RETURN_EXTRACTED_PARAMETERS, wid3.getResults());

        assertEquals(REST_WID_EXTRACTED_PARAMETERS, wid3.getParameters());
        assertEquals("|Result:java.lang.Object|", wid3.getResults());

        WorkItemDefinition wid4 = defs.get(3);

        assertEquals("Milestone", wid4.getName());
        assertEquals("Milestone", wid4.getDisplayName());
        assertEquals(ICON_64, wid4.getIconDefinition().getUri());
        assertEquals(ICON_64, wid4.getIconDefinition().getIconData());
        assertEquals("|Condition:String|", wid4.getParameters());
        assertEquals("Milestone", wid4.getCategory());
    }
}
