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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.enterprise.context.ApplicationScoped;

import org.guvnor.common.services.project.model.Dependencies;
import org.kie.workbench.common.stunner.bpmn.workitem.IconDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.uberfire.commons.data.Pair;

/**
 * This is just a POC for a parser. It parses based on line information, so it is easy to break. <br />
 * Suggestion is to implement a solution character based.
 *
 */
@ApplicationScoped
public class WorkItemDefinitionParser {

    public List<WorkItemDefinition> parse(String widStr) {

        if (widStr == null || "".equals(widStr.trim())) {
            return Collections.emptyList();
        }

        List<WorkItemDefinition> widList = new ArrayList<>();
        String[] lines = widStr.split("\n");
        Queue<String> linesQueue = new LinkedList<>(Arrays.asList(lines));
        while (!linesQueue.isEmpty()) {
            String line = linesQueue.peek().trim();
            if (!(empty(line) || isStartingObject(line) || isEndingObject(line))) {
                WorkItemDefinition wid = parseWorkItemDefinitionObject(linesQueue);
                widList.add(wid);
            }
            linesQueue.poll();
        }
        return widList;
    }

    private boolean empty(String line) {
        return line == null || line.equals("");
    }

    private static WorkItemDefinition parseWorkItemDefinitionObject(Queue<String> objectQueue) {
        WorkItemDefinition wid = new WorkItemDefinition();
        wid.setIconDefinition(new IconDefinition());
        wid.getIconDefinition().setIconData("");
        wid.getIconDefinition().setUri("");
        wid.setUri("");
        wid.setName("");
        wid.setCategory("");
        wid.setDescription("");
        wid.setDocumentation("");
        wid.setDisplayName("");
        wid.setResults("");
        wid.setDefaultHandler("");
        wid.setParameters("");
        wid.setDependencies(new Dependencies());
        String line = objectQueue.poll();
        while (!isEndingObject(line) && !objectQueue.isEmpty()) {
            Pair<String, String> attributes = getAttribute(line);
            switch (attributes.getK1()) {
                case "name":
                    wid.setName(attributes.getK2());
                    break;
                case "displayName":
                    wid.setDisplayName(attributes.getK2());
                    break;
                case "icon":
                    wid.getIconDefinition().setUri(attributes.getK2());
                    break;
                case "parameters":
                    String parameters = "";
                    if (!isEndingObject(attributes.getK2())) {
                        parameters = retrieveParameters(objectQueue);
                    }
                    wid.setParameters(parameters);
                    break;
                case "results":
                    String results = "";
                    if (!isEndingObject(attributes.getK2())) {
                        results = retrieveParameters(objectQueue);
                    }
                    wid.setResults(results);
                    break;
                default:
                    break;
            }
            line = objectQueue.poll();
        }
        return wid;
    }

    private static String retrieveParameters(Queue<String> objectQueue) {
        String param = objectQueue.poll();
        String params = "";
        while (!(isEndingObject(param) || objectQueue.isEmpty())) {
            params += param.trim();
            param = objectQueue.poll();
        }
        return params;
    }

    private static Pair<String, String> getAttribute(String value) {
        Pair<String, String> attrs = new Pair<>("", "");
        if (value.indexOf(':') != -1) {
            String[] values = value.split(":");
            attrs = new Pair<>(cleanProp(values[0]), cleanProp(values[1]));
        }
        return attrs;
    }

    private static String cleanProp(String prop) {
        return prop.trim().replaceAll("\"", "").replaceAll(",", "");
    }

    private static boolean isStartingObject(String line) {
        return line.startsWith("[");
    }

    private static boolean isEndingObject(String line) {
        return line == null || line.endsWith("]") || line.endsWith("],");
    }

}
