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

package org.kie.workbench.common.stunner.bpmn.definition.dto.drools;

import javax.xml.bind.annotation.XmlAttribute;

public class Global extends ExtensionElement {

    @XmlAttribute
    private String identifier;

    @XmlAttribute
    private String type;

    public Global() {

    }

    public Global(String identifier, String type) {
        this.identifier = identifier;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Global)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        Global global = (Global) o;

        if (getIdentifier() != null ? !getIdentifier().equals(global.getIdentifier()) : global.getIdentifier() != null) {
            return false;
        }
        return getType() != null ? getType().equals(global.getType()) : global.getType() == null;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getIdentifier() != null ? getIdentifier().hashCode() : 0);
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        return result;
    }
}
