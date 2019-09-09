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
import java.util.function.Consumer;

import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.stunner.core.util.StringUtils;

public class QNamePropertyConverter {

    /**
     * @return maybe null
     */
    public static QName wbFromDMN(final String qNameAsString) {
        if (StringUtils.isEmpty(qNameAsString)) {
            return BuiltInType.UNDEFINED.asQName();
        }
        final javax.xml.namespace.QName qName = javax.xml.namespace.QName.valueOf(qNameAsString);

        //TODO {manstis} QNames are preserved with their original DMN version information...
//        //Convert DMN1.1 QName typeRefs to DMN1.2 (the editor only supports DMN1.2)
//        if (DMNModelInstrumentedBase.URI_FEEL.equals(hasTypeRef.getNamespaceURI(qName.getPrefix()))) {
//            return new QName(QName.NULL_NS_URI, qName.getLocalPart());
//        }
//        final String defaultNs = jsiDefinitions.getNamespace();
//        final String localNs = hasTypeRef.getNamespaceURI(qName.getPrefix());
//        if (defaultNs.equalsIgnoreCase(localNs)) {
//            return new QName(QName.NULL_NS_URI, qName.getLocalPart());
//        }

        return new QName(qName.getNamespaceURI(),
                         qName.getLocalPart(),
                         qName.getPrefix());
    }

    /*
     * Handles setting QName as appropriate back on DMN node
     */
    public static void setDMNfromWB(final QName qname,
                                    final Consumer<String> setter) {
        if (qname != null) {
            setter.accept(dmnFromWB(qname).map(javax.xml.namespace.QName::toString).orElse(null));
        }
    }

    public static Optional<javax.xml.namespace.QName> dmnFromWB(final QName wb) {
        if (wb != null) {
            if (Objects.equals(wb, BuiltInType.UNDEFINED.asQName())) {
                return Optional.empty();
            }
            return Optional.of(new javax.xml.namespace.QName(wb.getNamespaceURI(),
                                                             wb.getLocalPart(),
                                                             wb.getPrefix()));
        } else {
            return Optional.empty();
        }
    }
}
