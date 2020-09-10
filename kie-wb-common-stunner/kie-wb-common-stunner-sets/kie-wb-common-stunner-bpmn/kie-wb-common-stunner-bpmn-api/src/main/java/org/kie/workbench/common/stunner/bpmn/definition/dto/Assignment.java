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

import org.kie.workbench.common.stunner.bpmn.definition.dto.handler.AssignmentDemarshaller;
import org.kie.workbench.common.stunner.bpmn.definition.dto.handler.AssignmentMarshaller;
import org.treblereel.gwt.jackson.api.annotation.XmlTypeAdapter;

@XmlTypeAdapter(
        serializer = AssignmentMarshaller.class,
        deserializer = AssignmentDemarshaller.class)
public class Assignment {

    private transient AssignmentValue from;
    private transient AssignmentValue to;

    public Assignment() {

    }

    public Assignment(String value, String id, String postfix) {
        this.from = new AssignmentValue(value);
        this.to = new AssignmentValue(id + "_" + postfix);
    }

    public AssignmentValue getFrom() {
        return from;
    }

    public Assignment setFrom(AssignmentValue from) {
        this.from = from;
        return this;
    }

    public Assignment from(String from) {
        this.from = new AssignmentValue(from);
        return this;
    }
    public Assignment from(String from, boolean isCDATA) {
        this.from = new AssignmentValue(from);
        this.from.setAsCDATA(isCDATA);
        return this;
    }

    public AssignmentValue getTo() {
        return to;
    }

    public Assignment to(String to) {
        this.to = new AssignmentValue(to);
        return this;
    }

    public Assignment to(String to, boolean isCDATA) {
        this.to = new AssignmentValue(to);
        this.to.setAsCDATA(isCDATA);
        return this;
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }

    public static class AssignmentValue {

        private String value;
        private transient boolean asCDATA = true;

        public AssignmentValue() {

        }

        public AssignmentValue(String value) {
            this.value = value;
        }

        AssignmentValue(String value, boolean asCDATA) {
            this.value = value;
            this.asCDATA = asCDATA;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isAsCDATA() {
            return asCDATA;
        }

        public void setAsCDATA(boolean asCDATA) {
            this.asCDATA = asCDATA;
        }
    }
}
