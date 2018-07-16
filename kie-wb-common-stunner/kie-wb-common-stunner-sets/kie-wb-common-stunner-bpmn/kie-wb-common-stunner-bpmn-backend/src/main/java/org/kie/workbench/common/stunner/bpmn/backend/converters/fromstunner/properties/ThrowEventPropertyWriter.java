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

import java.util.List;

import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.ThrowEvent;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.InitializedVariable;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.InitializedVariable.InputVariableReference;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.ParsedAssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.AssignmentsInfos.isReservedIdentifier;

public class ThrowEventPropertyWriter extends EventPropertyWriter {

    private final ThrowEvent throwEvent;

    public ThrowEventPropertyWriter(ThrowEvent flowElement, VariableScope variableScope) {
        super(flowElement, variableScope);
        this.throwEvent = flowElement;
        throwEvent.setInputSet(bpmn2.createInputSet());
    }

    @Override
    public void setAssignmentsInfo(AssignmentsInfo info) {
        ParsedAssignmentsInfo assignmentsInfo = ParsedAssignmentsInfo.of(info);
        List<InitializedVariable> inputAssociations =
                assignmentsInfo.createInitializedInputVariables(getId(), variableScope);

        if (inputAssociations.isEmpty()) {
            return;
        }
        if (inputAssociations.size() > 1) {
            throw new IllegalArgumentException("Input Associations should be at most 1 in Throw Events");
        }

        InputVariableReference initializedVariable = (InputVariableReference) inputAssociations.get(0);
        String identifier = initializedVariable.getIdentifier();

        if (isReservedIdentifier(identifier)) {
            return;
        }

        this.addItemDefinition(initializedVariable.getItemDefinition());
        DataInput dataInput = initializedVariable.getDataInput();
        throwEvent.getDataInputs().add(dataInput);
        throwEvent.getInputSet().getDataInputRefs().add(dataInput);

        DataInputAssociation dia = initializedVariable.getDataInputAssociation();
        if (dia != null) {
            throwEvent.getDataInputAssociation().add(dia);
        }
    }

    @Override
    protected void addEventDefinition(EventDefinition eventDefinition) {
        this.throwEvent.getEventDefinitions().add(eventDefinition);
    }
}
