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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import java.util.Objects;
import java.util.Optional;

import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.api.property.dmn.Name;

public class HrefBuilder {

    public static String getHref(final DRGElement drgElement) {

        final String drgElementId = drgElement.getId().getValue();

        return getNamespace(drgElement)
                .map(namespace -> namespace + "#" + drgElementId)
                .orElse("#" + drgElementId);
    }

    private static Optional<String> getNamespace(final DRGElement drgElement) {
        final String drgElementPrefix = extractNamespaceFromName(drgElement.getName());
        return getDefinitions(drgElement)
                .map(definitions -> definitions
                        .getImport()
                        .stream()
                        .filter(anImport -> {
                            final String importName = anImport.getName().getValue();
                            return Objects.equals(importName, drgElementPrefix);
                        })
                        .findFirst()
                        .map(Import::getNamespace)
                        .orElse(null));
    }

    private static String extractNamespaceFromName(final Name name) {
        final String value = name.getValue();
        final boolean hasNamespace = value.contains(".");
        return hasNamespace ? value.split("\\.")[0] : "";
    }

    private static Optional<Definitions> getDefinitions(final DRGElement drgElement) {

        final DMNModelInstrumentedBase parent = drgElement.getParent();

        if (parent instanceof DMNDiagram) {
            final DMNDiagram diagram = (DMNDiagram) parent;
            return Optional.ofNullable(diagram.getDefinitions());
        }

        if (parent instanceof Definitions) {
            return Optional.of((Definitions) parent);
        }

        return Optional.empty();
    }
}
