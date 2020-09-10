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

import javax.xml.bind.annotation.XmlCData;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.workbench.common.stunner.bpmn.definition.dto.handler.FormalExpressionDemarshaller;
import org.kie.workbench.common.stunner.bpmn.definition.dto.handler.FormalExpressionMarshaller;
import org.treblereel.gwt.jackson.api.annotation.XmlTypeAdapter;

@XmlRootElement(name = "formalExpression", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
public class FormalExpression {

    @XmlCData(value = false)
    private FormalExpressionValue value;

    public FormalExpression() {

    }

    public FormalExpression(String value) {
        this.value = new FormalExpressionValue(value);
    }

    public FormalExpressionValue getValue() {
        return value;
    }

    public void setValue(FormalExpressionValue value) {
        this.value = value;
    }

    @XmlTypeAdapter(
            serializer = FormalExpressionMarshaller.class,
            deserializer = FormalExpressionDemarshaller.class)
    public static class FormalExpressionValue {
        private String value;

        public FormalExpressionValue() {

        }

        public FormalExpressionValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
