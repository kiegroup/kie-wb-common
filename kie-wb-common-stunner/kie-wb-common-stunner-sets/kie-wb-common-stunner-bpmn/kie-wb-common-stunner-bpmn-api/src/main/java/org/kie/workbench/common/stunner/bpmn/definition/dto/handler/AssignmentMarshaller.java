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

import org.kie.workbench.common.stunner.bpmn.definition.dto.Assignment;
import org.treblereel.gwt.jackson.api.XMLSerializationContext;
import org.treblereel.gwt.jackson.api.XMLSerializerParameters;
import org.treblereel.gwt.jackson.api.custom.CustomXMLSerializer;
import org.treblereel.gwt.jackson.api.stream.XMLWriter;

public class AssignmentMarshaller extends CustomXMLSerializer<Assignment> {

    @Override
    protected void doSerialize(
            XMLWriter writer, Assignment value, XMLSerializationContext ctx, XMLSerializerParameters params)
            throws XMLStreamException {
        writer.beginObject("bpmn2:assignment");
        writer.beginObject("bpmn2:from");
        writer.writeAttribute("xsi:type", "bpmn2:tFormalExpression");
        if(value.getFrom().isAsCDATA()) {
            writer.writeCData(value.getFrom().getValue());
        } else {
            writer.writeCharacters(value.getFrom().getValue());
        }
        writer.endObject();
        writer.beginObject("bpmn2:to");
        writer.writeAttribute("xsi:type", "bpmn2:tFormalExpression");
        if(value.getTo().isAsCDATA()) {
            writer.writeCData(value.getTo().getValue());
        } else {
            writer.writeCharacters(value.getTo().getValue());
        }        writer.endObject();
        writer.endObject();
    }
}
