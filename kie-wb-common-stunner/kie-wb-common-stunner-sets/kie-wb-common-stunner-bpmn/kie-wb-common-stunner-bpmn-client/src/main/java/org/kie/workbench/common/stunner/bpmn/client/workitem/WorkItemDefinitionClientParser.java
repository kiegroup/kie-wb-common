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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.guvnor.common.services.project.model.Dependencies;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNCategories;
import org.kie.workbench.common.stunner.bpmn.workitem.IconDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.uberfire.commons.data.Pair;

/**
 * This is just a POC for a parser. It parses based on line information, so it is easy to break. <br />
 * Suggestion is to implement a solution character based.
 *
 */
@ApplicationScoped
public class WorkItemDefinitionClientParser {

    public List<WorkItemDefinition> parse(String widStr) {

        if (empty(widStr)) {
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

    private static boolean empty(String line) {
        return line == null || "".equals(line.trim());
    }

    private static WorkItemDefinition parseWorkItemDefinitionObject(Queue<String> objectQueue) {
        WorkItemDefinition wid = emptyWid();
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
                    if (!isEndingObject(attributes.getK2())) {
                        Collection<Pair<String, String>> parameters = retrieveParameters(objectQueue);
                        wid.setParameters(parseParameters(parameters));
                    }
                    break;
                case "results":
                    if (!isEndingObject(attributes.getK2())) {
                        Collection<Pair<String, String>> results = retrieveParameters(objectQueue);
                        wid.setResults(parseParameters(results));
                    }
                    break;
                case "category":
                    wid.setCategory(attributes.getK2());
                default:
                    break;
            }
            line = objectQueue.poll();
        }

        if (empty(wid.getCategory())) {
            wid.setCategory(BPMNCategories.SERVICE_TASKS);
        }
        return wid;
    }

    private static WorkItemDefinition emptyWid() {
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
        wid.setDependencies(new Dependencies(Collections.emptyList()));
        wid.setParameters("");
        return wid;
    }

    private static Collection<Pair<String, String>> retrieveParameters(Queue<String> objectQueue) {
        String param = objectQueue.poll();
        List<Pair<String, String>> params = new ArrayList<>();
        while (!(isEndingObject(param) || objectQueue.isEmpty())) {
            String[] paramsParts = param.trim().split(":");
            String paramName = cleanProp(paramsParts[0]);
            String paramType = paramsParts[1].replaceAll("new", "")
                                             .replaceAll(",", "")
                                             .replaceAll("\\(\\)", "").trim();
            params.add(new Pair<>(paramName, toJavaType(paramType)));
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

    private static String parseParameters(final Collection<Pair<String, String>> parameters) {
        return "|" + parameters.stream()
                               .map(param -> param.getK1() + ":" + param.getK2())
                               .sorted(String::compareTo)
                               .collect(Collectors.joining(",")) + "|";
    }

    /**
     * 
     * Converts a MVEL datatype to Java type. Could be extended for all MVEL possible types.
     * @param mvelType
     *  The MVEL type, e.g. StringDataType
     * @return
     * The Java corresponding type e.g. String
     */
    private static String toJavaType(String mvelType) {
        switch (mvelType) {
            case "StringDataType":
                return "String";
            case "ObjectDataType":
                return "java.lang.Object";
            default:
                return mvelType;
        }
    }

}
