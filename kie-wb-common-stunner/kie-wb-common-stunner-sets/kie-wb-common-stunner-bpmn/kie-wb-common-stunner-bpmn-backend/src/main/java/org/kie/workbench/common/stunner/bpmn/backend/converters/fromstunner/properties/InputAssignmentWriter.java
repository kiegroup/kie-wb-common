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

import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Property;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.VariableDeclaration;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class InputAssignmentWriter {

    private final DataInputAssociation association;
    private final DeclarationWriter declarationWriter;

    public InputAssignmentWriter(
            String parentId,
            VariableScope.Variable variable,
            VariableDeclaration decl) {

        this.declarationWriter = new DeclarationWriter(parentId, decl);

        // then we create the actual association between the two
        // e.g. mySource := myTarget (or, to put it differently, myTarget -> mySource)
        this.association = associationOf(variable.getTypedIdentifier(), declarationWriter.getDataInput());
    }

    private DataInputAssociation associationOf(Property source, DataInput dataInput) {
        DataInputAssociation dataInputAssociation =
                bpmn2.createDataInputAssociation();

        dataInputAssociation
                .getSourceRef()
                .add(source);

        dataInputAssociation
                .setTargetRef(dataInput);
        return dataInputAssociation;
    }

    public DataInput getDataInput() {
        return declarationWriter.getDataInput();
    }

    public ItemDefinition getItemDefinition() {
        return declarationWriter.getItemDefinition();
    }

    public InputSet getInputSet() {
        return declarationWriter.getInputSet();
    }

    public DataInputAssociation getAssociation() {
        return association;
    }
}
