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

package org.kie.workbench.common.stunner.bpmn.definition.dto.bpsim;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;

import org.treblereel.gwt.xml.mapper.api.annotation.XmlUnwrappedCollection;

public class ElementParameters {

    @XmlAttribute
    private String elementRef;
    @XmlElementRefs({
            @XmlElementRef(name = "TimeParameters", type = TimeParameters.class),
            @XmlElementRef(name = "ResourceParameters", type = ResourceParameters.class),
            @XmlElementRef(name = "CostParameters", type = CostParameters.class)
    })
    @XmlUnwrappedCollection
    private List<Parameters> parameters;

    public ElementParameters() {

    }

    public ElementParameters(String elementRef) {
        this.elementRef = elementRef;
    }

    @Override
    public int hashCode() {
        int result = getElementRef() != null ? getElementRef().hashCode() : 0;
        result = 31 * result + (getParameters() != null ? getParameters().hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ElementParameters)) {
            return false;
        }

        ElementParameters that = (ElementParameters) o;

        if (getElementRef() != null ? !getElementRef().equals(that.getElementRef()) : that.getElementRef() != null) {
            return false;
        }
        return getParameters() != null ? getParameters().equals(that.getParameters()) : that.getParameters() == null;
    }

    public String getElementRef() {
        return elementRef;
    }

    public void setElementRef(String elementRef) {
        this.elementRef = elementRef;
    }

    public List<Parameters> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameters> parameters) {
        this.parameters = parameters;
    }
}
