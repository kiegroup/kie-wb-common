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

    private final String identifier;
    private final String type;
    private ItemDefinition itemDefinition;

    public InitializedVariable(VariableDeclaration varDecl) {
        this.identifier = varDecl.getIdentifier();
        this.type = varDecl.getType();
        this.itemDefinition = bpmn2.createItemDefinition();
        itemDefinition.setId(getIdentifier());
        itemDefinition.setStructureRef(getType());

    }

    public String getIdentifier() {
        return identifier;
    }

    public String getType() {
        return type;
    }

    static InitializedVariable of(String parentId, VariableScope variableScope, VariableDeclaration varDecl, AssociationDeclaration associationDeclaration) {
        if (associationDeclaration == null) {
            return new Empty(varDecl);
        } else {
            AssociationDeclaration.Type type = associationDeclaration.getType();
            switch (type) {
                case FromTo:
                    return new Constant(varDecl, associationDeclaration.getTarget());
                case SourceTarget:
                    AssociationDeclaration.Direction direction = associationDeclaration.getDirection();
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

        public Empty(VariableDeclaration varDecl) {
            super(varDecl);
        }
    }

    public static class InputVariableReference extends InitializedVariable {

        private final DataInput dataInput;
        private final String sourceVariable;
        private final String parentId;
        private final VariableScope scope;

        public InputVariableReference(String parentId, VariableScope variableScope, VariableDeclaration varDecl, String sourceVariable) {
            super(varDecl);
            this.parentId = parentId;
            this.scope = variableScope;
            String identifier = varDecl.getIdentifier();
            this.sourceVariable = sourceVariable;
            this.dataInput = bpmn2.createDataInput();
            dataInput.setId(Ids.dataInput(parentId, identifier));
            dataInput.setName(identifier);

            ItemDefinition itemDefinition = getItemDefinition();
            itemDefinition.setId(Ids.dataInputItem(parentId, identifier));
            dataInput.setItemSubjectRef(itemDefinition);
            CustomAttribute.dtype.of(dataInput).set(itemDefinition.getStructureRef());
        }

        public DataInput getDataInput() {
            return dataInput;
        }

        public DataInputAssociation getDataInputAssociation() {
            return associationOf(scope.lookup(sourceVariable).getTypedIdentifier(), dataInput);
        }

        private DataInputAssociation associationOf(String expression, DataInput dataInput) {
            DataInputAssociation dataInputAssociation =
                    bpmn2.createDataInputAssociation();

            Assignment assignment = bpmn2.createAssignment();
            String id = dataInput.getId();

            FormalExpression toExpr = bpmn2.createFormalExpression();
            toExpr.setBody(id);
            assignment.setTo(toExpr);

            FormalExpression fromExpr = bpmn2.createFormalExpression();
            fromExpr.setBody(expression);
            assignment.setFrom(fromExpr);

            dataInputAssociation
                    .getAssignment().add(assignment);

            dataInputAssociation
                    .setTargetRef(dataInput);
            return dataInputAssociation;
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


    }

    public static class OutputVariableReference extends InitializedVariable {

        private final DataOutput dataOutput;
        private final String targetVariable;
        private final String parentId;
        private final VariableScope scope;

        public OutputVariableReference(String parentId, VariableScope scope, VariableDeclaration varDecl, String targetVariable) {
            super(varDecl);
            this.parentId = parentId;
            this.scope = scope;
            this.targetVariable = targetVariable;
            String identifier = varDecl.getIdentifier();

            ItemDefinition itemDefinition = getItemDefinition();
            itemDefinition.setId(Ids.dataOutputItem(parentId, identifier));

            this.dataOutput = bpmn2.createDataOutput();
            dataOutput.setId(Ids.dataOutput(parentId, identifier));
            dataOutput.setName(identifier);
            dataOutput.setItemSubjectRef(itemDefinition);
            CustomAttribute.dtype.of(dataOutput).set(getType());
        }

        public DataOutput getDataOutput() {
            return dataOutput;
        }

//        public void setScope(String parentId, VariableScope scope) {
//            this.parentId = parentId;
//            this.scope = scope;
//            dataOutput.setId(Ids.dataOutput(parentId, getIdentifier()));
//            ItemDefinition itemSubjectRef = getItemDefinition();
//            itemSubjectRef.setId(Ids.dataOutputItem(parentId, getIdentifier()));
//        }

        public DataOutputAssociation getDataOutputAssociation() {
            VariableScope.Variable variable = scope.lookup(targetVariable);
            return associationOf(variable.getTypedIdentifier(), dataOutput);
        }

        private DataOutputAssociation associationOf(Property source, DataOutput dataOutput) {
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

    public static class Constant extends InitializedVariable {

        final String expression;

        Constant(VariableDeclaration varDecl, String expression) {
            super(varDecl);
            this.expression = expression;
        }
    }
}
