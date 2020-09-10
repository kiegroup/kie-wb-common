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

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "metaData", namespace = "http://www.jboss.org/drools")
public class MetaData extends ExtensionElement {

    private MetaValue metaValue;

    public MetaData() {

    }

    public MetaData(String name, String metaValue) {
        super(name);
        this.metaValue = new MetaValue(metaValue);
    }

    public MetaValue getMetaValue() {
        return metaValue;
    }

    public void setMetaValue(MetaValue metaValue) {
        this.metaValue = metaValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){ return true; }
        if (!(o instanceof MetaData)){ return false;}
        if (!super.equals(o)){ return false;}

        MetaData metaData = (MetaData) o;

        return getMetaValue() != null ? getMetaValue().equals(metaData.getMetaValue()) : metaData.getMetaValue() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getMetaValue() != null ? getMetaValue().hashCode() : 0);
        return result;
    }
}
