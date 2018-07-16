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

public class InitializedVariable {

    private final VariableDeclaration variableDeclaration;
    private final AssociationDeclaration associationDeclaration;

    public InitializedVariable(VariableDeclaration varDecl, AssociationDeclaration associationDeclaration) {
        this.variableDeclaration = varDecl;
        this.associationDeclaration = associationDeclaration;
    }

    public VariableDeclaration getVariableDeclaration() {
        return variableDeclaration;
    }

    public AssociationDeclaration getAssociationDeclaration() {
        return associationDeclaration;
    }

    public String getInitializationValue() {
        return associationDeclaration == null ? null :
                associationDeclaration.getDirection() != AssociationDeclaration.Direction.Input ?
                    associationDeclaration.getTarget() : associationDeclaration.getSource();
    }
}
