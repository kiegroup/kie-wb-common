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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.workbench.common.stunner.bpmn.definition.dto.drools.MetaData;

@XmlRootElement(name = "property", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
public class Property {

    @XmlAttribute
    private String id;
    @XmlAttribute
    private String itemSubjectRef;
    @XmlAttribute
    private String name;

    @XmlElementRefs({
            @XmlElementRef(name = "metaData", type = MetaData.class)
    })
    @XmlElement(name = "extensionElements", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
    private List<MetaData> extensionElements;

    public Property() {

    }

    public Property(String name, String customTags) {
        this.setId(name);
        this.setName(name);
        this.setItemSubjectRef("_" + name + "Item");
        if (customTags != null && !customTags.equals("[]")) {
            extensionElements = new ArrayList<>();
            extensionElements.add(new MetaData("customTags",
                                               customTags.replaceAll(";",",")));
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemSubjectRef() {
        return itemSubjectRef;
    }

    public void setItemSubjectRef(String itemSubjectRef) {
        this.itemSubjectRef = itemSubjectRef;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MetaData> getExtensionElements() {
        return extensionElements;
    }

    public void setExtensionElements(List<MetaData> extensionElements) {
        this.extensionElements = extensionElements;
    }
}
