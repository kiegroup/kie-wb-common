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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.workbench.common.stunner.bpmn.definition.dto.dc.Bounds;

@XmlRootElement(name = "BPMNShape", namespace = "http://www.omg.org/spec/BPMN/20100524/DI")
public class BPMNShape extends BPMNPlaneElement {

    private static final String TYPE = "shape_";

    @XmlElement(name = "Bounds")
    private Bounds bounds;

    public BPMNShape() {

    }

    public BPMNShape(String id) {
        super(TYPE + id, id);
    }

    public Bounds getBounds() {
        return bounds;
    }

    public BPMNShape setBounds(Bounds bounds) {
        this.bounds = bounds;
        return this;
    }
}
