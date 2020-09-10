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

package org.kie.workbench.common.stunner.kogito.client.marshalling.tostunner;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.bpmn.definition.dto.Definitions;
import org.kie.workbench.common.stunner.bpmn.definition.dto.ItemDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.dto.Property;

class ProcessVariableReader {

    static String getProcessVariables(Definitions definitions,
                                      Function<String, ItemDefinition> getItemDefinitionStructureRef) {
        return definitions.getProcess()
                .getProperties()
                .stream()
                .map(p -> toProcessVariableString(p, getItemDefinitionStructureRef))
                .collect(Collectors.joining(","));
    }

    private static String toProcessVariableString(Property p, Function<String, ItemDefinition> getItemDefinitionStructureRef) {
        String processVariableName = getProcessVariableName(p);
        ItemDefinition itemDefinition = getItemDefinitionStructureRef.apply(p.getItemSubjectRef());
        String tags = hasNoTags(p) ? "[]" : p.getExtensionElements()
                .get(0).getMetaValue()
                .getValue().replaceAll(",", ";");
        return Optional.ofNullable(itemDefinition)
                .map(type -> processVariableName + ":" + itemDefinition
                        .getStructureRef() + ":" + tags)
                .orElse(processVariableName + "::" + tags);
    }

    public static String getProcessVariableName(Property p) {
        String name = p.getName();
        // legacy uses ID instead of name
        return name == null || name.isEmpty() ? p.getId() : name;
    }

    private static boolean hasNoTags(Property p) {
        return p.getExtensionElements() == null
                || p.getExtensionElements().isEmpty();
    }
}
