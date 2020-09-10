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

import org.kie.workbench.common.stunner.bpmn.definition.dto.ResourceAssignmentExpression;
import org.treblereel.gwt.jackson.api.XMLDeserializationContext;
import org.treblereel.gwt.jackson.api.XMLDeserializerParameters;
import org.treblereel.gwt.jackson.api.custom.CustomXMLDeserializer;
import org.treblereel.gwt.jackson.api.exception.XMLDeserializationException;
import org.treblereel.gwt.jackson.api.stream.XMLReader;

public class ResourceAssignmentExpressionDemarshaller extends CustomXMLDeserializer<ResourceAssignmentExpression> {


    @Override
    public ResourceAssignmentExpression deserialize(String value, XMLDeserializationContext ctx, XMLDeserializerParameters params) throws XMLDeserializationException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected ResourceAssignmentExpression doDeserialize(XMLReader reader, XMLDeserializationContext ctx, XMLDeserializerParameters params) throws XMLStreamException {
        ResourceAssignmentExpression result = new ResourceAssignmentExpression();
        result.setId(reader.getAttributeValue(0));
        reader.next();
        reader.next();
        result.setFormalExpression(reader.nextString());
        reader.next();
        reader.next();
        return result;
    }
}
