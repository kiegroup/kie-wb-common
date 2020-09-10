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

public abstract class Data<T extends Data> {

    @XmlAttribute
    protected String id;

    @XmlAttribute(name = "drools:dtype")
    protected String dtype;

    @XmlAttribute
    protected String itemSubjectRef;

    @XmlAttribute
    protected String name;

    public String getDtype() {
        return dtype;
    }

    public T setDtype(String dtype) {
        this.dtype = dtype;
        return (T) this;
    }

    public String getId() {
        return id;
    }

    public T setId(String id) {
        this.id = id;
        return (T) this;
    }

    public String getItemSubjectRef() {
        return itemSubjectRef;
    }

    public T setItemSubjectRef(String itemSubjectRef) {
        this.itemSubjectRef = itemSubjectRef;
        return (T) this;
    }

    public String getName() {
        return name;
    }

    public T setName(String name) {
        this.name = name;
        return (T) this;
    }
}
