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

package org.kie.workbench.common.stunner.bpmn.definition.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.kie.workbench.common.stunner.bpmn.definition.dto.handler.ResourceAssignmentExpressionDemarshaller;
import org.kie.workbench.common.stunner.bpmn.definition.dto.handler.ResourceAssignmentExpressionMarshaller;
import org.treblereel.gwt.jackson.api.annotation.XmlTypeAdapter;

@XmlRootElement(name = "resourceAssignmentExpression", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")

@XmlTypeAdapter(
        serializer = ResourceAssignmentExpressionMarshaller.class,
        deserializer = ResourceAssignmentExpressionDemarshaller.class)
public class ResourceAssignmentExpression {

    private String id;

    private String formalExpression;

    public ResourceAssignmentExpression() {

    }

    public ResourceAssignmentExpression(String id, String value) {
        this.id = id;
        this.formalExpression = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormalExpression() {
        return formalExpression;
    }

    public void setFormalExpression(String formalExpression) {
        this.formalExpression = formalExpression;
    }
}
