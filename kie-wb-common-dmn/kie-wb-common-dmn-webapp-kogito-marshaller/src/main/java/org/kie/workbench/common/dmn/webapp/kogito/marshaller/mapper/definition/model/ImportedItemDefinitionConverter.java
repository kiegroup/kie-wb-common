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

package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model;

import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.editors.types.BuiltInTypeUtils;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITItemDefinition;

public class ImportedItemDefinitionConverter {

    public static ItemDefinition wbFromDMN(final JSITItemDefinition dmnItemDefinition,
                                           final String prefix) {

        final JSITItemDefinition dmnItemDefinitionWithNamespace = withNamespace(dmnItemDefinition, prefix);
        final ItemDefinition wbItemDefinition = ItemDefinitionPropertyConverter.wbFromDMN(dmnItemDefinitionWithNamespace);

        allowOnlyVisualChange(wbItemDefinition);

        return wbItemDefinition;
    }

    public static JSITItemDefinition withNamespace(final JSITItemDefinition itemDefinition,
                                                   final String prefix) {

        final String nameWithPrefix = prefix + "." + itemDefinition.getName();
        final List<JSITItemDefinition> itemComponents = Arrays.asList(itemDefinition.getItemComponent().asArray());

        if (itemDefinition.getTypeRef() != null && !isBuiltInType(itemDefinition.getTypeRef())) {
            itemDefinition.setTypeRef(makeQNameWithPrefix(itemDefinition.getTypeRef(), prefix).toString());
        }

        itemDefinition.setName(nameWithPrefix);
        setItemDefinitionsNamespace(itemComponents, prefix);

        return itemDefinition;
    }

    private static void allowOnlyVisualChange(final ItemDefinition itemDefinition) {
        itemDefinition.setAllowOnlyVisualChange(true);
        itemDefinition.getItemComponent().forEach(ImportedItemDefinitionConverter::allowOnlyVisualChange);
    }

    private static void setItemDefinitionsNamespace(final List<JSITItemDefinition> itemDefinitions,
                                                    final String prefix) {
        itemDefinitions.forEach(itemDefinition -> withNamespace(itemDefinition, prefix));
    }

    private static boolean isBuiltInType(final String qNameAsString) {
        final QName typeRef = QName.valueOf(qNameAsString);
        return BuiltInTypeUtils.isBuiltInType(typeRef.getLocalPart());
    }

    private static QName makeQNameWithPrefix(final String qNameAsString,
                                             final String prefix) {
        final QName qName = QName.valueOf(qNameAsString);
        final String namespaceURI = qName.getNamespaceURI();
        final String localPart = prefix + "." + qName.getLocalPart();

        return new QName(namespaceURI, localPart, qName.getPrefix());
    }
}
