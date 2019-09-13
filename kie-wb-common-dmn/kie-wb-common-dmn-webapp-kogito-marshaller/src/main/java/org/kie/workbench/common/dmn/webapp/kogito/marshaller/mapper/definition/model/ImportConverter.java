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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.api.definition.model.ImportDMN;
import org.kie.workbench.common.dmn.api.definition.model.ImportPMML;
import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITImport;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.utils.NameSpaceUtils;

import static org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes.determineImportType;

public final class ImportConverter {

    public static Import wbFromDMN(final JSITImport dmn,
                                   final JSITDefinitions definitions,
                                   final PMMLDocumentMetadata pmmlDocument) {
        final Import result = createWBImport(dmn, definitions, pmmlDocument);
        final Map<QName, String> additionalAttributes = new HashMap<>();
        for (Map.Entry<javax.xml.namespace.QName, String> entry : dmn.getOtherAttributes().entrySet()) {
            additionalAttributes.put(QNamePropertyConverter.wbFromDMN(entry.getKey().toString()), entry.getValue());
        }
        result.setAdditionalAttributes(additionalAttributes);
        final String name = dmn.getName();
        final String description = dmn.getDescription();
        result.setId(IdPropertyConverter.wbFromDMN(dmn.getId()));
        result.setName(new Name(name));
        result.setDescription(DescriptionPropertyConverter.wbFromDMN(description));

        NameSpaceUtils.extractNamespacesKeyedByPrefix(dmn).forEach((key, value) -> result.getNsContext().put(key, value));

        return result;
    }

    private static Import createWBImport(final JSITImport dmn,
                                         final JSITDefinitions definitions,
                                         final PMMLDocumentMetadata pmmlDocument) {
        final LocationURI locationURI = new LocationURI(dmn.getLocationURI());
        if (Objects.equals(DMNImportTypes.DMN, determineImportType(dmn.getImportType()))) {
            final ImportDMN result = new ImportDMN(dmn.getNamespace(), locationURI, dmn.getImportType());
            result.setDrgElementsCount(countDefinitionElement(definitions, () -> d -> d.getDrgElement().size()));
            result.setItemDefinitionsCount(countDefinitionElement(definitions, () -> d -> d.getItemDefinition().size()));
            return result;
        } else if (Objects.equals(DMNImportTypes.PMML, determineImportType(dmn.getImportType()))) {
            final ImportPMML result = new ImportPMML(dmn.getNamespace(), locationURI, dmn.getImportType());
            result.setModelCount(pmmlDocument.getModels().size());
            return result;
        } else {
            return new Import(dmn.getNamespace(), locationURI, dmn.getImportType());
        }
    }

    static JSITImport dmnFromWb(final Import wb) {
        final JSITImport result = new JSITImport();
        result.setImportType(wb.getImportType());
        result.setLocationURI(wb.getLocationURI().getValue());
        result.setNamespace(wb.getNamespace());
        final Map<javax.xml.namespace.QName, String> additionalAttributes = new HashMap<>();
        for (Map.Entry<QName, String> entry : wb.getAdditionalAttributes().entrySet()) {
            QNamePropertyConverter.dmnFromWB(entry.getKey())
                    .ifPresent(qName -> additionalAttributes.put(qName, entry.getValue()));
        }
        result.setId(wb.getId().getValue());
        result.setName(wb.getName().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        result.getOtherAttributes().putAll(additionalAttributes);

        //TODO {manstis} Do we need to copy wb.getNsContext() into dmn.otherAttributes()?
        //wb.getNsContext().forEach((key, value) -> result.getNsContext().put(key, value));

        return result;
    }

    private static Integer countDefinitionElement(final JSITDefinitions definitions,
                                                  final Supplier<Function<JSITDefinitions, Integer>> supplier) {
        final Integer none = 0;
        return Optional
                .ofNullable(definitions)
                .map(supplier.get())
                .orElse(none);
    }
}
