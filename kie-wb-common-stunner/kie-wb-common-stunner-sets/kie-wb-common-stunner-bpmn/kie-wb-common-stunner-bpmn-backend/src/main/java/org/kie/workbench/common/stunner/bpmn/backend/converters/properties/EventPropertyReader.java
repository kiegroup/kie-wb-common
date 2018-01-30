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
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.kie.workbench.common.stunner.bpmn.backend.converters.events.TimerEventDefinitionConverter;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettingsValue;

public abstract class EventPropertyReader extends AbstractPropertyReader {

    public static EventPropertyReader of(CatchEvent catchEvent) {
        return new EventPropertyReader(catchEvent) {
            @Override
            public String getAssignmentsInfo() {
                return Properties.getAssignmentsInfo(catchEvent);
            }
        };
    }

    public static EventPropertyReader of(ThrowEvent event) {
        return new EventPropertyReader(event) {
            @Override
            public String getAssignmentsInfo() {
                return Properties.getAssignmentsInfo(event);
            }
        };
    }

    public static EventPropertyReader of(Activity activity) {
        return new EventPropertyReader(activity) {
            @Override
            public String getAssignmentsInfo() {
                return Properties.getAssignmentsInfo(activity);
            }
        };
    }


    private EventPropertyReader(BaseElement element) {
        super(element);
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
