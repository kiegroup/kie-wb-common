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

package org.kie.workbench.common.stunner.kogito.client.marshalling.tostunner;

import java.util.Optional;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.bpmn.definition.dto.Definitions;
import org.kie.workbench.common.stunner.bpmn.definition.dto.drools.MetaData;

public class MetaDataAttributesElement {

    public static final String DELIMITER = "Ø";
    public static final String SEPARATOR = "ß";
    private final Definitions definitions;

    public MetaDataAttributesElement(Definitions definitions) {
        this.definitions = definitions;
    }

    public Optional<String> getStringValue() {
        if (definitions.getProcess() != null
                && definitions.getProcess().getExtensionElements() != null) {
            return Optional.of(definitions.getProcess()
                                       .getExtensionElements()
                                       .stream()
                                       .filter(extensionElement -> extensionElement instanceof MetaData)
                                       .map(elm -> (MetaData) elm)
                                       .filter(metaDataType -> metaDataType.getName() != null)
                                       .filter(metaDataType -> metaDataType.getName().length() > 0)
                                       .map(metaDataType -> metaDataType.getName()
                                               + SEPARATOR
                                               + ((null != metaDataType.getMetaValue()
                                               && !metaDataType.getMetaValue().getValue().isEmpty())
                                               ? metaDataType.getMetaValue().getValue() : ""))
                                       .collect(Collectors.joining(DELIMITER)));
        }
        return Optional.empty();
    }
}