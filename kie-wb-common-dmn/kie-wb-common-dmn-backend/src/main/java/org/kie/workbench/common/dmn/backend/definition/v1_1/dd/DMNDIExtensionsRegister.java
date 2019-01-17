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
package org.kie.workbench.common.dmn.backend.definition.v1_1.dd;

import javax.xml.namespace.QName;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.QNameMap;
import org.kie.dmn.api.marshalling.DMNExtensionRegister;

import static org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase.Namespace.KIE;

public class DMNDIExtensionsRegister implements DMNExtensionRegister {

    static final String COMPONENTS_WIDTHS_EXTENSION_ALIAS = "ComponentsWidthsExtension";

    static final String COMPONENT_WIDTHS_ALIAS = "ComponentWidths";

    static final String COMPONENT_WIDTH_ALIAS = "width";

    @Override
    public void registerExtensionConverters(final XStream xStream) {
        xStream.processAnnotations(ComponentsWidthsExtension.class);
        xStream.processAnnotations(ComponentWidths.class);
        xStream.alias(COMPONENT_WIDTH_ALIAS, Double.class);

        xStream.registerConverter(new ComponentWidthsConverter(xStream));
    }

    @Override
    public void beforeMarshal(final Object o,
                              final QNameMap qmap) {
        qmap.registerMapping(new QName(KIE.getUri(),
                                       COMPONENTS_WIDTHS_EXTENSION_ALIAS,
                                       KIE.getPrefix()),
                             COMPONENTS_WIDTHS_EXTENSION_ALIAS);
        qmap.registerMapping(new QName(KIE.getUri(),
                                       COMPONENT_WIDTHS_ALIAS,
                                       KIE.getPrefix()),
                             COMPONENT_WIDTHS_ALIAS);
        qmap.registerMapping(new QName(KIE.getUri(),
                                       COMPONENT_WIDTH_ALIAS,
                                       KIE.getPrefix()),
                             COMPONENT_WIDTH_ALIAS);
    }
}
