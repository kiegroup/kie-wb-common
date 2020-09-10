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

@XmlRootElement(name = "import", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
public class Import {

    @XmlAttribute
    private String importType = "http://schemas.xmlsoap.org/wsdl/";
    @XmlAttribute
    private String location;
    @XmlAttribute
    private String namespace;

    public Import() {

    }

    public Import(String location, String namespace) {
        this.location = location;
        this.namespace = namespace;
    }

    @Override
    public int hashCode() {
        int result = getImportType() != null ? getImportType().hashCode() : 0;
        result = 31 * result + (getLocation() != null ? getLocation().hashCode() : 0);
        result = 31 * result + (getNamespace() != null ? getNamespace().hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Import)) {
            return false;
        }

        Import anImport = (Import) o;

        if (getImportType() != null ? !getImportType().equals(anImport.getImportType()) : anImport.getImportType() != null) {
            return false;
        }
        if (getLocation() != null ? !getLocation().equals(anImport.getLocation()) : anImport.getLocation() != null) {
            return false;
        }
        return getNamespace() != null ? getNamespace().equals(anImport.getNamespace()) : anImport.getNamespace() == null;
    }

    public String getImportType() {
        return importType;
    }

    public void setImportType(String importType) {
        this.importType = importType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
