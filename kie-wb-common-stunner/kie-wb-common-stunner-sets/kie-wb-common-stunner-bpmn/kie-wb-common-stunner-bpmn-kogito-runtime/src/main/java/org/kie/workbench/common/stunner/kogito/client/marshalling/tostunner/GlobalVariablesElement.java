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
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.bpmn.definition.dto.Definitions;
import org.kie.workbench.common.stunner.bpmn.definition.dto.drools.Global;

public class GlobalVariablesElement {

    private final Definitions definitions;

    public GlobalVariablesElement(Definitions definitions) {
        this.definitions = definitions;
    }

    public Optional<String> getStringValue() {
        if (definitions.getProcess() != null
                && definitions.getProcess().getExtensionElements() != null) {
            return Optional.of(definitions.getProcess()
                                       .getExtensionElements()
                                       .stream()
                                       .filter(extensionElement -> extensionElement instanceof Global)
                                       .map(elm -> (Global) elm)
                                       .map(globalType -> globalType.getIdentifier() + ":" + globalType.getType())
                                       .collect(Collectors.joining(",")));
        }
        return Optional.empty();
    }
}