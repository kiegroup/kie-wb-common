/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.converters.properties;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.kie.workbench.common.stunner.bpmn.backend.converters.events.TimerEventDefinitionConverter;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettingsValue;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

public abstract class EventPropertyReader extends BasePropertyReader {

    public static EventPropertyReader of(Event el, BPMNPlane plane) {

        if (el instanceof BoundaryEvent) {
            return new EventPropertyReader(el, plane) {
                @Override
                public String getAssignmentsInfo() {
                    return Properties.getAssignmentsInfo((CatchEvent) el);
                }

                @Override
                public Bounds getBounds() {
                    org.eclipse.dd.dc.Bounds bounds = shape.getBounds();
                    Point2D docker = getDockerInfo();
                    return BoundsImpl.build(
                            docker.getX(),
                            docker.getY(),
                            docker.getX() + bounds.getWidth(),
                            docker.getY() + bounds.getHeight());
                }

                public Point2D getDockerInfo() {
                    String dockerInfoStr = attribute("dockerinfo");

                    dockerInfoStr = dockerInfoStr.substring(0, dockerInfoStr.length() - 1);
                    String[] dockerInfoParts = dockerInfoStr.split("\\|");
                    String infoPartsToUse = dockerInfoParts[0];
                    String[] infoPartsToUseParts = infoPartsToUse.split("\\^");

                    double x = Double.valueOf(infoPartsToUseParts[0]);
                    double y = Double.valueOf(infoPartsToUseParts[1]);

                    return Point2D.create(x, y);
                }
            };
        } else if (el instanceof CatchEvent) {
            return new EventPropertyReader(el, plane) {
                @Override
                public String getAssignmentsInfo() {
                    return Properties.getAssignmentsInfo((CatchEvent) el);
                }
            };
        } else if (el instanceof ThrowEvent) {
            return new EventPropertyReader(el, plane) {
                @Override
                public String getAssignmentsInfo() {
                    return Properties.getAssignmentsInfo((ThrowEvent) el);
                }
            };
        } else if (el instanceof Activity) {
            return new EventPropertyReader(el, plane) {
                @Override
                public String getAssignmentsInfo() {
                    return Properties.getAssignmentsInfo((Activity) el);
                }
            };
        } else {
            throw new IllegalArgumentException(el.toString());
        }
    }

    EventPropertyReader(BaseElement element, BPMNPlane plane) {
        super(element, plane);
    }

    public String getSignalScope() {
        return metaData("customScope");
    }

    public abstract String getAssignmentsInfo();

    public boolean isCancelActivity() {
        return Boolean.parseBoolean(attribute("boundaryca"));
    }

    public TimerSettingsValue getTimerSettings(TimerEventDefinition eventDefinition) {
        return TimerEventDefinitionConverter.convertTimerEventDefinition(eventDefinition);
    }
}
