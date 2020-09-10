/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import org.kie.workbench.common.stunner.bpmn.definition.dto.DataInput;
import org.kie.workbench.common.stunner.bpmn.definition.dto.DataInputAssociation;
import org.kie.workbench.common.stunner.bpmn.definition.dto.Definitions;
import org.kie.workbench.common.stunner.bpmn.definition.dto.Property;
import org.kie.workbench.common.stunner.bpmn.definition.dto.SourceRef;

import static org.kie.workbench.common.stunner.kogito.client.marshalling.converters.associations.AssignmentsInfos.isReservedIdentifier;

public class InputAssignmentReader {

    private static Logger logger = Logger.getLogger(InputAssignmentReader.class.getName());
    private final AssociationDeclaration associationDeclaration;

    InputAssignmentReader(Assignment assignment, String targetName) {
        String body = assignment.getFrom().getValue();
        this.associationDeclaration = new AssociationDeclaration(
                AssociationDeclaration.Direction.Input,
                AssociationDeclaration.Type.FromTo,
                body,
                targetName);
    }

    InputAssignmentReader(Property source, String targetName) {
        this.associationDeclaration = new AssociationDeclaration(
                AssociationDeclaration.Direction.Input,
                AssociationDeclaration.Type.SourceTarget,
                source.getName(),
                targetName);
    }

    public static Optional<InputAssignmentReader> fromAssociation(Definitions definitions, List<DataInput> datainput, DataInputAssociation in) {
        SourceRef source = in.getSourceRef();
        Assignment assignment = in.getAssignment();

        String targetName = in.getTargetRef().getValue();
        if (isReservedIdentifier(targetName)) {
            return Optional.empty();
        }

        Optional<DataInput> dataInput = datainput
                .stream()
                .filter(elm -> elm.getId().equals(targetName))
                .findFirst();

        if (dataInput.isPresent()) {

            if (source != null) {
                List<Property> properties = definitions.getProcess().getProperties();
                Optional<Property> property = properties.stream()
                        .filter(elm -> elm.getId().equals(source.getValue()))
                        .findFirst();
                if (property.isPresent()) {
                    return Optional.of(new InputAssignmentReader(property.get(), dataInput.get().getName()));
                }
            } else if (assignment != null) {
                return Optional.of(new InputAssignmentReader(assignment, dataInput.get().getName()));
            }
        }
        logger.log(Level.SEVERE, "Cannot find SourceRef or Assignment for Target " + targetName);
        return Optional.empty();
    }

    public AssociationDeclaration getAssociationDeclaration() {
        return associationDeclaration;
    }
}
