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

package org.kie.workbench.common.stunner.bpmn.definition.dto.handler;

import javax.xml.stream.XMLStreamException;

import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Package;

import org.treblereel.gwt.xml.mapper.api.XMLSerializationContext;
import org.treblereel.gwt.xml.mapper.api.XMLSerializerParameters;
import org.treblereel.gwt.xml.mapper.api.custom.CustomXMLSerializer;
import org.treblereel.gwt.xml.mapper.api.stream.XMLWriter;

public class PackageMarshaller extends CustomXMLSerializer<Package> {

    @Override
    protected void doSerialize(
            XMLWriter writer, Package value, XMLSerializationContext ctx, XMLSerializerParameters params)
            throws XMLStreamException {
        writer.writeAttribute(propertyName, value.getValue());
    }
}
