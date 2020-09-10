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

package org.kie.workbench.common.stunner.kogito.client.marshalling.converters.associations;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kie.workbench.common.stunner.bpmn.definition.dto.Assignment;
import org.kie.workbench.common.stunner.bpmn.definition.dto.DataOutput;
import org.kie.workbench.common.stunner.bpmn.definition.dto.DataOutputAssociation;
import org.kie.workbench.common.stunner.bpmn.definition.dto.Definitions;
import org.kie.workbench.common.stunner.bpmn.definition.dto.Property;
import org.kie.workbench.common.stunner.bpmn.definition.dto.SourceRef;
import org.kie.workbench.common.stunner.bpmn.definition.dto.TargetRef;

import static org.kie.workbench.common.stunner.kogito.client.marshalling.converters.associations.AssignmentsInfos.isReservedIdentifier;

public class OutputAssignmentReader {

    private static Logger logger = Logger.getLogger(OutputAssignmentReader.class.getName());
    private final AssociationDeclaration associationDeclaration;

    OutputAssignmentReader(String sourceName, Property target) {
        String propertyName = getPropertyName(target);
        this.associationDeclaration = new AssociationDeclaration(
                AssociationDeclaration.Direction.Output,
                AssociationDeclaration.Type.SourceTarget,
                sourceName,
                propertyName);
    }

    // TODO DataObject
    private static String getPropertyName(Property prop) {
        if (prop instanceof Property) {
            return prop.getName() == null ? prop.getId() : prop.getName();
        }
        return null;
    }

    OutputAssignmentReader(Assignment assignment, String targetName) {
        String body = assignment.getTo().getValue();
        this.associationDeclaration = new AssociationDeclaration(
                AssociationDeclaration.Direction.Output,
                AssociationDeclaration.Type.FromTo,
                targetName,
                body);
    }

    public static Optional<OutputAssignmentReader> fromAssociation(Definitions definitions, List<DataOutput> dataoutput, DataOutputAssociation out) {
        TargetRef target = out.getTargetRef();
        SourceRef source = out.getSourceRef();
        Assignment assignment = out.getAssignment();

        if (isReservedIdentifier(out.getSourceRef().getValue())) {
            return Optional.empty();
        }

        Optional<DataOutput> dataOutput = dataoutput.stream()
                .filter(elm -> elm.getId().equals(source.getValue()))
                .findFirst();

        if (dataOutput.isPresent()) {
            if (target != null) {
                List<Property> properties = definitions.getProcess().getProperties();
                Optional<Property> property = properties.stream()
                        .filter(elm -> elm.getId().equals(target.getValue()))
                        .findFirst();
                if (property.isPresent()) {
                    return Optional.of(new OutputAssignmentReader(dataOutput.get().getName(), property.get()));
                }
            }
            if (assignment != null && out.getSourceRef() != null) {
                return Optional.of(new OutputAssignmentReader(assignment, dataOutput.get().getName()));
            }
        }

        logger.log(Level.SEVERE, "Cannot find SourceRef or Assignment for Source " + out.getSourceRef().getValue());
        return Optional.empty();
    }

    public AssociationDeclaration getAssociationDeclaration() {
        return associationDeclaration;
    }
}
