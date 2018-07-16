/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties;

import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Property;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Ids;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.VariableScope;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public abstract class InitializedVariable {

    private final String parentId;
    private final String identifier;
    private final String type;
    private ItemDefinition itemDefinition;

    public InitializedVariable(String parentId, VariableDeclaration varDecl) {
        this.parentId = parentId;
        this.identifier = varDecl.getIdentifier();
        this.type = varDecl.getType();
        this.itemDefinition = bpmn2.createItemDefinition();
        itemDefinition.setId(parentId);
        itemDefinition.setStructureRef(getType());
    }

    public static InitializedInputVariable inputOf(String parentId, VariableScope variableScope, VariableDeclaration varDecl, AssociationDeclaration associationDeclaration) {
        if (associationDeclaration == null) {
            return (InitializedInputVariable) of(
                    parentId,
                    variableScope,
                    varDecl,
                    new AssociationDeclaration(
                            AssociationDeclaration.Direction.Input,
                            AssociationDeclaration.Type.FromTo,
                            varDecl.getIdentifier(),
                            null));
        }
        return (InitializedInputVariable) of(parentId, variableScope, varDecl, associationDeclaration);
    }

    public static InitializedOutputVariable outputOf(String parentId, VariableScope variableScope, VariableDeclaration varDecl, AssociationDeclaration associationDeclaration) {
        if (associationDeclaration == null) {
            return (InitializedOutputVariable) of(
                    parentId,
                    variableScope,
                    varDecl,
                    new AssociationDeclaration(
                            AssociationDeclaration.Direction.Output,
                            AssociationDeclaration.Type.FromTo,
                            varDecl.getIdentifier(),
                            null));
        }
        return (InitializedOutputVariable) of(parentId, variableScope, varDecl, associationDeclaration);
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getType() {
        return type;
    }

    static InitializedVariable of(String parentId, VariableScope variableScope, VariableDeclaration varDecl, AssociationDeclaration associationDeclaration) {
        if (associationDeclaration == null) {
            return new Empty(parentId, varDecl);
        } else {
            AssociationDeclaration.Type type = associationDeclaration.getType();
            AssociationDeclaration.Direction direction = associationDeclaration.getDirection();
            switch (type) {
                case FromTo:
                    switch (direction) {
                        case Input:
                            if (associationDeclaration.getTarget() == null) {
                                return new InputEmpty(parentId, varDecl);
                            } else {
                                return new InputConstant(parentId, varDecl, associationDeclaration.getTarget());
                            }
                        case Output:
                            if (associationDeclaration.getTarget() == null) {
                                return new OutputEmpty(parentId, varDecl);
                            } else {
                                throw new IllegalArgumentException("Cannot assign constant to output variable");
                            }
                        default:
                            throw new IllegalArgumentException("Unknown direction " + direction);
                    }

                case SourceTarget:
                    switch (direction) {
                        case Input:
                            return new InputVariableReference(parentId, variableScope, varDecl, associationDeclaration.getSource());
                        case Output:
                            return new OutputVariableReference(parentId, variableScope, varDecl, associationDeclaration.getTarget());
                        default:
                            throw new IllegalArgumentException("Unknown direction " + direction);
                    }
                default:
                    throw new IllegalArgumentException("Unknown type " + type);
            }
        }
    }

    public ItemDefinition getItemDefinition() {
        return itemDefinition;
    }

    public static class Empty extends InitializedVariable {

        public Empty(String parentId, VariableDeclaration varDecl) {
            super(parentId, varDecl);
        }
    }

    public static abstract class InitializedInputVariable extends InitializedVariable {

        private final DataInput dataInput;

        public InitializedInputVariable(String parentId, VariableDeclaration varDecl) {
            super(parentId, varDecl);
            this.dataInput = dataInputOf(
                    parentId, varDecl.getIdentifier(), getItemDefinition());
        }

        public DataInput getDataInput() {
            return dataInput;
        }

        public abstract DataInputAssociation getDataInputAssociation();
    }

    public static abstract class InitializedOutputVariable extends InitializedVariable {

        private final DataOutput dataOutput;

        public InitializedOutputVariable(String parentId, VariableDeclaration varDecl) {
            super(parentId, varDecl);
            this.dataOutput = dataOutputOf(
                    parentId,
                    varDecl.getIdentifier(),
                    getItemDefinition());
        }

        public DataOutput getDataOutput() {
            return dataOutput;
        }

        public abstract DataOutputAssociation getDataOutputAssociation();
    }

    public static class InputVariableReference extends InitializedInputVariable {

        private final String sourceVariable;
        private final VariableScope scope;

        public InputVariableReference(String parentId, VariableScope variableScope, VariableDeclaration varDecl, String sourceVariable) {
            super(parentId, varDecl);
            this.scope = variableScope;
            this.sourceVariable = sourceVariable;
        }

        public DataInputAssociation getDataInputAssociation() {
            DataInputAssociation dataInputAssociation =
                    bpmn2.createDataInputAssociation();

            dataInputAssociation
                    .getSourceRef()
                    .add(scope.lookup(sourceVariable).getTypedIdentifier());

            dataInputAssociation
                    .setTargetRef(getDataInput());
            return dataInputAssociation;
        }
    }

    public static class OutputVariableReference extends InitializedOutputVariable {

        private final DataOutput dataOutput;
        private final String targetVariable;
        private final VariableScope scope;

        public OutputVariableReference(String parentId, VariableScope scope, VariableDeclaration varDecl, String targetVariable) {
            super(parentId, varDecl);
            this.scope = scope;
            this.targetVariable = targetVariable;
            this.dataOutput = dataOutputOf(
                    parentId,
                    varDecl.getIdentifier(),
                    getItemDefinition());
        }

        public DataOutput getDataOutput() {
            return dataOutput;
        }

        public DataOutputAssociation getDataOutputAssociation() {
            VariableScope.Variable variable = scope.lookup(targetVariable);
            return associationOf(variable.getTypedIdentifier(), dataOutput);
        }
    }

    public static class InputEmpty extends InitializedInputVariable {

        public InputEmpty(String parentId, VariableDeclaration varDecl) {
            super(parentId, varDecl);
        }

        @Override
        public DataInputAssociation getDataInputAssociation() {
            return null;
        }
    }

    public static class InputConstant extends InitializedInputVariable {

        final String expression;

        InputConstant(String parentId, VariableDeclaration varDecl, String expression) {
            super(parentId, varDecl);
            this.expression = expression;
        }

        public DataInputAssociation getDataInputAssociation() {
            DataInputAssociation dataInputAssociation =
                    bpmn2.createDataInputAssociation();

            Assignment assignment = bpmn2.createAssignment();
            String id = getDataInput().getId();

            FormalExpression toExpr = bpmn2.createFormalExpression();
            toExpr.setBody(id);
            assignment.setTo(toExpr);

            FormalExpression fromExpr = bpmn2.createFormalExpression();
            fromExpr.setBody(expression);
            assignment.setFrom(fromExpr);

            dataInputAssociation
                    .getAssignment().add(assignment);

            dataInputAssociation
                    .setTargetRef(getDataInput());
            return dataInputAssociation;
        }
    }

    public static class OutputEmpty extends InitializedInputVariable {

        public OutputEmpty(String parentId, VariableDeclaration varDecl) {
            super(parentId, varDecl);
        }

        @Override
        public DataInputAssociation getDataInputAssociation() {
            return null;
        }
    }

    private static DataInput dataInputOf(String parentId, String identifier, ItemDefinition itemDefinition) {
        DataInput dataInput = bpmn2.createDataInput();
        dataInput.setId(Ids.dataInput(parentId, identifier));
        dataInput.setName(identifier);
        dataInput.setItemSubjectRef(itemDefinition);
        CustomAttribute.dtype.of(dataInput).set(itemDefinition.getStructureRef());
        return dataInput;
    }

    private static DataOutput dataOutputOf(String parentId, String identifier, ItemDefinition itemDefinition) {
        DataOutput dataOutput = bpmn2.createDataOutput();
        dataOutput.setId(Ids.dataOutput(parentId, identifier));
        dataOutput.setName(identifier);
        dataOutput.setItemSubjectRef(itemDefinition);
        CustomAttribute.dtype.of(dataOutput).set(itemDefinition.getStructureRef());
        return dataOutput;
    }

    private static DataOutputAssociation associationOf(Property source, DataOutput dataOutput) {
        DataOutputAssociation dataOutputAssociation =
                bpmn2.createDataOutputAssociation();

        dataOutputAssociation
                .getSourceRef()
                .add(dataOutput);

        dataOutputAssociation
                .setTargetRef(source);
        return dataOutputAssociation;
    }
}
