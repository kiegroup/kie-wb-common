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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "itemDefinition", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
public class ItemDefinition {

    @XmlAttribute
    private String id;

    @XmlAttribute
    private String structureRef = "Object";

    public ItemDefinition() {

    }

    public ItemDefinition(String id) {
        this.id = id;
    }

    public ItemDefinition(String id, String structureRef) {
        this.id = id;
        this.structureRef = structureRef;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getStructureRef() != null ? getStructureRef().hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemDefinition)) {
            return false;
        }

        ItemDefinition that = (ItemDefinition) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) {
            return false;
        }
        return getStructureRef() != null ? getStructureRef().equals(that.getStructureRef()) : that.getStructureRef() == null;
    }

    public String getId() {
        return id;
    }

    public ItemDefinition setId(String id) {
        this.id = id;
        return this;
    }

    public String getStructureRef() {
        return structureRef;
    }

    public ItemDefinition setStructureRef(String structureRef) {
        this.structureRef = structureRef;
        return this;
    }

    @Override
    public String toString() {
        return "ItemDefinition{" +
                "id='" + id + '\'' +
                ", structureRef='" + structureRef + '\'' +
                '}';
    }
}
