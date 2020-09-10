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

import javax.xml.bind.annotation.XmlRootElement;

import org.kie.workbench.common.stunner.bpmn.definition.dto.di.Point;
import org.treblereel.gwt.jackson.api.annotation.XmlUnwrappedCollection;

@XmlRootElement
public class BPMNEdge extends BPMNPlaneElement {

    private static final String TYPE = "edge_shape_";

    @XmlUnwrappedCollection
    private List<Point> waypoint;

    public BPMNEdge() {

    }

    public BPMNEdge(String from, String to, String flow) {
        super(TYPE + from + "ZZZ" + to, flow);
    }

    public List<Point> getWaypoint() {
        return waypoint;
    }

    public void setWaypoint(List<Point> waypoint) {
        this.waypoint = waypoint;
    }
}
