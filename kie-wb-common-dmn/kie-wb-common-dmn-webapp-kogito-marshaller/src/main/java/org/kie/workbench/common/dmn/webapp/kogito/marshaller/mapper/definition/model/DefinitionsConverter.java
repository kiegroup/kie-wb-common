/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITImport;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITItemDefinition;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.utils.NameSpaceUtils;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.core.util.UUID;

public class DefinitionsConverter {

    public static Definitions wbFromDMN(final JSITDefinitions dmn,
                                        final Map<JSITImport, JSITDefinitions> importDefinitions,
                                        final Map<JSITImport, PMMLDocumentMetadata> pmmlDocuments) {
        if (dmn == null) {
            return null;
        }
        final Id id = new Id(dmn.getId());
        final Name name = new Name(dmn.getName());
        final String namespace = dmn.getNamespace();
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final Definitions result = new Definitions();
        result.setId(id);
        result.setName(name);
        result.setNamespace(new Text(namespace));
        result.getNsContext().putIfAbsent(DMNModelInstrumentedBase.Namespace.DEFAULT.getPrefix(),
                                          namespace);
        result.setDescription(description);

        final Map<String, String> namespaces = NameSpaceUtils.extractNamespacesKeyedByPrefix(dmn);
        for (Entry<String, String> kv : namespaces.entrySet()) {
            String mappedURI = kv.getValue();
            switch (mappedURI) {
                case org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase.URI_DMN:
                    mappedURI = org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase.URI_DMN;
                    break;
                case org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase.URI_FEEL:
                    mappedURI = org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase.URI_FEEL;
                    break;
                case org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase.URI_KIE:
                    mappedURI = org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase.URI_KIE;
                    break;
            }
            if (kv.getKey().equalsIgnoreCase(DMNModelInstrumentedBase.Namespace.DEFAULT.getPrefix())) {
                result.getNsContext().putIfAbsent(kv.getKey(), mappedURI);
            } else {
                result.getNsContext().put(kv.getKey(), mappedURI);
            }
        }

        final JsArrayLike<JSITItemDefinition> wrappedItemDefinitions = dmn.getItemDefinition();
        if (Objects.nonNull(wrappedItemDefinitions)) {
            final JsArrayLike<JSITItemDefinition> jsiItemDefinitions = JsUtils.getUnwrappedElementsArray(wrappedItemDefinitions);
            for (int i = 0; i < jsiItemDefinitions.getLength(); i++) {
                final JSITItemDefinition jsiItemDefinition = Js.uncheckedCast(jsiItemDefinitions.getAt(i));
                final ItemDefinition itemDefConverted = ItemDefinitionPropertyConverter.wbFromDMN(jsiItemDefinition);
                if (Objects.nonNull(itemDefConverted)) {
                    itemDefConverted.setParent(result);
                    result.getItemDefinition().add(itemDefConverted);
                }
            }
        }

        final JsArrayLike<JSITImport> wrappedImports = dmn.getImport();
        if (Objects.nonNull(wrappedImports)) {
            final JsArrayLike<JSITImport> jsiImports = JsUtils.getUnwrappedElementsArray(wrappedImports);
            for (int i = 0; i < jsiImports.getLength(); i++) {
                final JSITImport jsiImport = Js.uncheckedCast(jsiImports.getAt(i));
                final JSITDefinitions definitions = importDefinitions.get(jsiImport);
                final PMMLDocumentMetadata pmmlDocument = pmmlDocuments.get(jsiImport);
                final Import importConverted = ImportConverter.wbFromDMN(jsiImport, definitions, pmmlDocument);
                if (Objects.nonNull(importConverted)) {
                    importConverted.setParent(result);
                    result.getImport().add(importConverted);
                }
            }
        }

        return result;
    }

    public static JSITDefinitions dmnFromWB(final Definitions wb) {
        if (wb == null) {
            return null;
        }
        final JSITDefinitions result = new JSITDefinitions();

        // TODO currently DMN wb UI does not offer feature to set these required DMN properties, setting some hardcoded defaults for now.
        final String defaultId = (wb.getId() != null) ? wb.getId().getValue() : UUID.uuid();
        final String defaultName = (wb.getName() != null) ? wb.getName().getValue() : UUID.uuid(8);
        final String defaultNamespace = !StringUtils.isEmpty(wb.getNamespace().getValue())
                ? wb.getNamespace().getValue()
                : DMNModelInstrumentedBase.Namespace.DEFAULT.getUri() + UUID.uuid();

        result.setId(defaultId);
        result.setName(defaultName);
        result.setNamespace(defaultNamespace);
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        result.getNsContext().putAll(wb.getNsContext());
        result.getNsContext().putIfAbsent(DMNModelInstrumentedBase.Namespace.DEFAULT.getPrefix(),
                                          defaultNamespace);

        for (ItemDefinition itemDef : wb.getItemDefinition()) {
            final JSITItemDefinition itemDefConverted = ItemDefinitionPropertyConverter.dmnFromWB(itemDef);
            if (itemDefConverted != null) {
                itemDefConverted.setParent(result);
            }
            JsUtils.add(result.getItemDefinition(), itemDefConverted);
        }

        for (Import i : wb.getImport()) {
            final JSITImport importConverted = ImportConverter.dmnFromWb(i);
            if (importConverted != null) {
                importConverted.setParent(result);
            }
            JsUtils.add(result.getImport(), importConverted);
        }

        return result;
    }
}