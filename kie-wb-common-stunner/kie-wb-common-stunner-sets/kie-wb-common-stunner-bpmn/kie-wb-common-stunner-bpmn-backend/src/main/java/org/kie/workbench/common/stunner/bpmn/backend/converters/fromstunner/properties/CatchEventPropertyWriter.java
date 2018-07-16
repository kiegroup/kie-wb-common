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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import bpsim.ElementParameters;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.EventDefinition;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.ParsedAssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.SimulationAttributeSets;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationAttributeSet;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class CatchEventPropertyWriter extends EventPropertyWriter {

    private final CatchEvent event;
    private ElementParameters simulationParameters;

    public CatchEventPropertyWriter(CatchEvent event, VariableScope variableScope) {
        super(event, variableScope);
        this.event = event;
        event.setOutputSet(bpmn2.createOutputSet());
    }

    public void setAssignmentsInfo(AssignmentsInfo info) {
        ParsedAssignmentsInfo assignmentsInfo = ParsedAssignmentsInfo.of(info);
        assignmentsInfo
                .getOutputAssociations()
                .map(initializedVariable -> new OutputAssignmentWriter(
                        flowElement.getId(),
                        initializedVariable.getVariableDeclaration(),
                        variableScope.lookup(initializedVariable.getInitializationValue())
                ))
                .forEach(doa -> {
                    event.getDataOutputs().add(doa.getDataOutput());
                    event.getOutputSet().getDataOutputRefs().add(doa.getDataOutput());
                    this.addItemDefinition(doa.getItemDefinition());
                    DataOutputAssociation association = doa.getAssociation();
                    if (association != null) {
                        event.getDataOutputAssociation().add(association);
                    }
                });
    }

    public void setSimulationSet(SimulationAttributeSet simulationSet) {
        this.simulationParameters =
                SimulationAttributeSets.toElementParameters(simulationSet);
        simulationParameters.setElementRef(getId());
    }

    public ElementParameters getSimulationParameters() {
        return simulationParameters;
    }

    @Override
    public void addEventDefinition(EventDefinition eventDefinition) {
        this.event.getEventDefinitions().add(eventDefinition);
    }

    public void setCancelActivity(Boolean value) {
        // this only makes sense for boundary events: ignore
    }
}
