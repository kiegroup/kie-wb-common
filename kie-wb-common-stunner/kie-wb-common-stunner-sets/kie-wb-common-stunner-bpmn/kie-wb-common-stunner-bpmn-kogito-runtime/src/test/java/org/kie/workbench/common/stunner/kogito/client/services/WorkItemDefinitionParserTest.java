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

package org.kie.workbench.common.stunner.kogito.client.services;

import java.util.List;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WorkItemDefinitionParserTest {
    
    String WID = "  [\n" + 
            "    [\n" + 
            "      \"name\" : \"Email\",\n" + 
            "      \"parameters\" : [\n" + 
            "        \"From\" : new StringDataType(),\n" + 
            "        \"To\" : new StringDataType(),\n" + 
            "        \"Subject\" : new StringDataType(),\n" + 
            "        \"Body\" : new StringDataType()\n" + 
            "      ],\n" + 
            "      \"displayName\" : \"Email\",\n" + 
            "      \"icon\" : \"defaultemailicon.gif\"\n" + 
            "    ],\n" + 
            "  \n" + 
            "    [\n" + 
            "      \"name\" : \"Log\",\n" + 
            "      \"parameters\" : [\n" + 
            "        \"Message\" : new StringDataType()\n" + 
            "      ],\n" + 
            "      \"displayName\" : \"Log\",\n" + 
            "      \"icon\" : \"defaultlogicon.gif\"\n" + 
            "    ],\n" + 
            "  \n" + 
            "    [\n" + 
            "      \"name\" : \"WebService\",\n" + 
            "      \"parameters\" : [\n" + 
            "          \"Url\" : new StringDataType(),\n" + 
            "           \"Namespace\" : new StringDataType(),\n" + 
            "           \"Interface\" : new StringDataType(),\n" + 
            "           \"Operation\" : new StringDataType(),\n" + 
            "           \"Parameter\" : new StringDataType(),\n" + 
            "           \"Endpoint\" : new StringDataType(),\n" + 
            "           \"Mode\" : new StringDataType()\n" + 
            "      ],\n" + 
            "      \"results\" : [\n" + 
            "          \"Result\" : new ObjectDataType(),\n" + 
            "      ],\n" + 
            "      \"displayName\" : \"WS\",\n" + 
            "      \"icon\" : \"defaultservicenodeicon.png\"\n" + 
            "    ],\n" + 
            "  \n" + 
            "    [\n" + 
            "      \"name\" : \"Rest\",\n" + 
            "      \"parameters\" : [\n" + 
            "          \"ContentData\" : new StringDataType(),\n" + 
            "          \"Url\" : new StringDataType(),\n" + 
            "          \"Method\" : new StringDataType(),\n" + 
            "          \"ConnectTimeout\" : new StringDataType(),\n" + 
            "          \"ReadTimeout\" : new StringDataType(),\n" + 
            "          \"Username\" : new StringDataType(),\n" + 
            "          \"Password\" : new StringDataType()\n" + 
            "      ],\n" + 
            "      \"results\" : [\n" + 
            "          \"Result\" : new ObjectDataType(),\n" + 
            "      ],\n" + 
            "      \"displayName\" : \"REST\",\n" + 
            "      \"icon\" : \"defaultservicenodeicon.png\"\n" + 
            "    ],\n" + 
            "  \n" + 
            "    [\n" + 
            "       \"name\" : \"BusinessRuleTask\",\n" + 
            "       \"parameters\" : [\n" + 
            "         \"Language\" : new StringDataType(),\n" + 
            "         \"KieSessionName\" : new StringDataType(),\n" + 
            "         \"KieSessionType\" : new StringDataType()\n" + 
            "       ],\n" + 
            "       \"displayName\" : \"Business Rule Task\",\n" + 
            "       \"icon\" : \"defaultbusinessrulesicon.png\",\n" + 
            "       \"category\" : \"Decision tasks\"\n" + 
            "     ],\n" + 
            "  \n" + 
            "     [\n" + 
            "       \"name\" : \"DecisionTask\",\n" + 
            "       \"parameters\" : [\n" + 
            "         \"Language\" : new StringDataType(),\n" + 
            "         \"Namespace\" : new StringDataType(),\n" + 
            "         \"Model\" : new StringDataType(),\n" + 
            "         \"Decision\" : new StringDataType()\n" + 
            "       ],\n" + 
            "       \"displayName\" : \"Decision Task\",\n" + 
            "       \"icon\" : \"defaultdecisionicon.png\",\n" + 
            "       \"category\" : \"Decision tasks\"\n" + 
            "     ],\n" + 
            "  \n" + 
            "     [\n" + 
            "      \"name\" : \"Milestone\",\n" + 
            "      \"parameters\" : [\n" + 
            "          \"Condition\" : new StringDataType()\n" + 
            "      ],\n" + 
            "      \"displayName\" : \"Milestone\",\n" + 
            "      \"icon\" : \"defaultmilestoneicon.png\",\n" + 
            "      \"category\" : \"Milestone\"\n" + 
            "      ]\n" + 
            "  ]";
    
    
    WorkItemDefinitionParser parser = new WorkItemDefinitionParser();
    
    @Test
    public void widTests() {
        List<WorkItemDefinition> defs; 
        defs = parser.parse("");
        assertTrue(defs.isEmpty());
        defs = parser.parse("[]");
        assertTrue(defs.isEmpty());
        defs = parser.parse("[\n]");
        assertTrue(defs.isEmpty());
        defs = parser.parse(WID);
        assertEquals(7, defs.size());
    }
}