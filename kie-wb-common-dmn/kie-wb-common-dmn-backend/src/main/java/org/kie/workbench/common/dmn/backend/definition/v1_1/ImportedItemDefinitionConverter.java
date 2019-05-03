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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.xml.namespace.QName;

import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;

public class ImportedItemDefinitionConverter {

    public static ItemDefinition wbFromDMN(final org.kie.dmn.model.api.ItemDefinition dmnItemDefinition,
                                           final String prefix) {

        final org.kie.dmn.model.api.ItemDefinition dmnItemDefinitionWithNamespace = withNamespace(dmnItemDefinition, prefix);
        final ItemDefinition wbItemDefinition = ItemDefinitionPropertyConverter.wbFromDMN(dmnItemDefinitionWithNamespace);

        allowOnlyVisualChange(wbItemDefinition);

        return wbItemDefinition;
    }

    public static org.kie.dmn.model.api.ItemDefinition withNamespace(final org.kie.dmn.model.api.ItemDefinition itemDefinition,
                                                                     final String prefix) {

        final String nameWithPrefix = prefix + "." + itemDefinition.getName();
        final List<org.kie.dmn.model.api.ItemDefinition> itemComponents = itemDefinition.getItemComponent();

        if (itemDefinition.getTypeRef() != null && !isBuiltInType(itemDefinition.getTypeRef())) {
            itemDefinition.setTypeRef(makeQNameWithPrefix(itemDefinition.getTypeRef(), prefix));
        }

        itemDefinition.setName(nameWithPrefix);
        setItemDefinitionsNamespace(itemComponents, prefix);

        return itemDefinition;
    }

    private static void allowOnlyVisualChange(final ItemDefinition itemDefinition) {
        itemDefinition.setAllowOnlyVisualChange(true);
        itemDefinition.getItemComponent().forEach(ImportedItemDefinitionConverter::allowOnlyVisualChange);
    }

    private static void setItemDefinitionsNamespace(final List<org.kie.dmn.model.api.ItemDefinition> itemDefinitions,
                                                    final String prefix) {
        itemDefinitions.forEach(itemDefinition -> withNamespace(itemDefinition, prefix));
    }

    private static boolean isBuiltInType(final QName typeRef) {
        return Arrays
                .stream(BuiltInType.values())
                .anyMatch(builtInType -> {

                    final String builtInTypeName = builtInType.getName();
                    final String typeRefName = typeRef.getLocalPart();

                    return Objects.equals(builtInTypeName, typeRefName);
                });
    }

    private static QName makeQNameWithPrefix(final QName qName,
                                             final String prefix) {

        final String namespaceURI = qName.getNamespaceURI();
        final String localPart = prefix + "." + qName.getLocalPart();

        return new QName(namespaceURI, localPart, qName.getPrefix());
    }
}
