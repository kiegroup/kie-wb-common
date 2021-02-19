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

package org.kie.workbench.common.stunner.bpmn.definition.dto.bpmndi;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.treblereel.gwt.xml.mapper.api.annotation.XmlUnwrappedCollection;

@Portable
@XmlRootElement(name = "BPMNPlane", namespace = "http://www.omg.org/spec/BPMN/20100524/DI")
public class BPMNPlane {

    @XmlAttribute
    private String bpmnElement;

    @XmlElementRefs({
            @XmlElementRef(name = "BPMNShape", type = BPMNShape.class),
            @XmlElementRef(name = "BPMNEdge", type = BPMNEdge.class)
    })
    @XmlUnwrappedCollection
    private List<BPMNPlaneElement> elements;


    public String getBpmnElement() {
        return bpmnElement;
    }

    public void setBpmnElement(String bpmnElement) {
        this.bpmnElement = bpmnElement;
    }

    public List<BPMNPlaneElement> getElements() {
        return elements;
    }

    public void setElements(List<BPMNPlaneElement> elements) {
        this.elements = elements;
    }
}
