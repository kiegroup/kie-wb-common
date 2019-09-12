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

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.definition.model.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITItemDefinition;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITUnaryTests;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils;

import static java.util.Optional.ofNullable;

public class ItemDefinitionPropertyConverter {

    public static ItemDefinition wbFromDMN(final JSITItemDefinition dmn) {

        if (dmn == null) {
            return null;
        }

        final Id id = IdPropertyConverter.wbFromDMN(dmn.getId());
        final Name name = new Name(dmn.getName());

        final Description description = wbDescriptionFromDMN(dmn);
        final QName typeRef = wbTypeRefFromDMN(dmn);

        final String typeLanguage = dmn.getTypeLanguage();
        final boolean isCollection = dmn.getIsCollection();

        final ItemDefinition wb = new ItemDefinition(id,
                                                     description,
                                                     name,
                                                     typeRef,
                                                     null,
                                                     null,
                                                     typeLanguage,
                                                     isCollection,
                                                     false);

        setUnaryTests(wb, dmn);
        setItemComponent(wb, dmn);

        return wb;
    }

    static void setUnaryTests(final ItemDefinition wb,
                              final JSITItemDefinition dmn) {

        final JSITUnaryTests dmnAllowedValues = dmn.getAllowedValues();
        final Optional<UnaryTests> wbUnaryTests = ofNullable(UnaryTestsPropertyConverter.wbFromDMN(dmnAllowedValues));

        wbUnaryTests.ifPresent(unaryTests -> {
            wb.setAllowedValues(unaryTests);
            unaryTests.setParent(wb);
        });
    }

    static void setItemComponent(final ItemDefinition wb,
                                 final JSITItemDefinition dmn) {
        Stream.of(dmn.getItemComponent().asArray()).forEach(dmnChild -> {
            wb.getItemComponent().add(wbChildFromDMN(wb,
                                                     dmnChild));
        });
    }

    static ItemDefinition wbChildFromDMN(final ItemDefinition wbParent,
                                         final JSITItemDefinition dmnChild) {

        final ItemDefinition wbChild = wbFromDMN(dmnChild);

        if (wbChild != null) {
            wbChild.setParent(wbParent);
        }

        return wbChild;
    }

    static Description wbDescriptionFromDMN(final JSITItemDefinition dmn) {
        return DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
    }

    static QName wbTypeRefFromDMN(final JSITItemDefinition dmn) {
        final QName wbQName = QNamePropertyConverter.wbFromDMN(dmn.getTypeRef());
        final QName undefinedQName = BuiltInType.UNDEFINED.asQName();

        return Objects.equals(wbQName, undefinedQName) ? null : wbQName;
    }

    public static JSITItemDefinition dmnFromWB(final ItemDefinition wb) {
        if (wb == null) {
            return null;
        }
        final JSITItemDefinition result = JSITItemDefinition.newInstance();
        result.setId(wb.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(wb.getDescription()));
        result.setName(wb.getName().getValue());
        QNamePropertyConverter.setDMNfromWB(wb.getTypeRef(),
                                            result::setTypeRef);

        result.setTypeLanguage(wb.getTypeLanguage());
        result.setIsCollection(wb.isIsCollection());

        final JSITUnaryTests utConverted = UnaryTestsPropertyConverter.dmnFromWB(wb.getAllowedValues());
        result.setAllowedValues(utConverted);

        for (ItemDefinition child : wb.getItemComponent()) {
            final JSITItemDefinition convertedChild = ItemDefinitionPropertyConverter.dmnFromWB(child);
            JSITItemDefinition.addItemComponent(result, convertedChild);
        }

        return result;
    }
}
