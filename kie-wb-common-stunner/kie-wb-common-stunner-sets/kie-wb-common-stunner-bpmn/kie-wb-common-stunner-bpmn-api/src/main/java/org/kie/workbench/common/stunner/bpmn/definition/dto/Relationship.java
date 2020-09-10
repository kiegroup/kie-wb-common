/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.workbench.common.stunner.bpmn.definition.dto.bpsim.BPSimData;
import org.treblereel.gwt.jackson.api.annotation.XmlUnwrappedCollection;

@XmlRootElement(name = "relationship", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
public class Relationship {

    @XmlAttribute
    private String type = "BPSimData";

    @XmlElementWrapper(name = "extensionElements", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
    @XmlUnwrappedCollection
    @XmlElement(name = "BPSimData", namespace = "http://www.bpsim.org/schemas/1.0")
    private List<BPSimData> extensionElements;

    private StringValue source;

    private StringValue target;

    public Relationship() {

    }

    public Relationship(String id) {
        this.source = new StringValue(id);
        this.target = new StringValue(id);
    }

    @Override
    public int hashCode() {
        int result = getType() != null ? getType().hashCode() : 0;
        result = 31 * result + (getExtensionElements() != null ? getExtensionElements().hashCode() : 0);
        result = 31 * result + (getSource() != null ? getSource().hashCode() : 0);
        result = 31 * result + (getTarget() != null ? getTarget().hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Relationship)) {
            return false;
        }

        Relationship that = (Relationship) o;

        if (getType() != null ? !getType().equals(that.getType()) : that.getType() != null) {
            return false;
        }
        if (getExtensionElements() != null ? !getExtensionElements().equals(that.getExtensionElements()) : that.getExtensionElements() != null) {
            return false;
        }
        if (getSource() != null ? !getSource().equals(that.getSource()) : that.getSource() != null) {
            return false;
        }
        return getTarget() != null ? getTarget().equals(that.getTarget()) : that.getTarget() == null;
    }

    @Override
    public String toString() {
        return "Relationship{" +
                "type='" + type + '\'' +
                ", extensionElements=" + extensionElements +
                ", source='" + source + '\'' +
                ", target='" + target + '\'' +
                '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<BPSimData> getExtensionElements() {
        return extensionElements;
    }

    public void setExtensionElements(List<BPSimData> extensionElements) {
        this.extensionElements = extensionElements;
    }

    public StringValue getSource() {
        return source;
    }

    public void setSource(StringValue source) {
        this.source = source;
    }

    public StringValue getTarget() {
        return target;
    }

    public void setTarget(StringValue target) {
        this.target = target;
    }

    public void setSource(String id) {
        this.source = new StringValue(id);
    }

    public void setTarget(String id) {
        this.target = new StringValue(id);
    }

    @XmlRootElement(name = "target", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
    public static class Target {

        private StringValue value;

        public Target() {

        }

        public Target(String value) {
            this.value = new StringValue(value);
        }

        public StringValue getValue() {
            return value;
        }

        public void setValue(StringValue value) {
            this.value = value;
        }
    }

    @XmlRootElement(name = "source", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
    public static class Source {

        private StringValue value;

        public Source() {

        }

        public Source(String value) {
            this.value = new StringValue(value);
        }

        public StringValue getValue() {
            return value;
        }

        public void setValue(StringValue value) {
            this.value = value;
        }
    }
}
