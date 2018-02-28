package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner;

import java.util.List;

import org.eclipse.bpmn2.di.BPMNEdge;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.BasePropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.LanePropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriter;

public interface ElementContainer {

    BasePropertyWriter getChildElement(String uuid);

    void addChildElement(PropertyWriter p);

    void addChildEdge(BPMNEdge edge);

    void addLaneSet(List<LanePropertyWriter> lanes);
}