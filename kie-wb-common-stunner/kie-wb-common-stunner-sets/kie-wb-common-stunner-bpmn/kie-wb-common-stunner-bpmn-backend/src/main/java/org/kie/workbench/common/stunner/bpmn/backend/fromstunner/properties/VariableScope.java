package org.kie.workbench.common.stunner.bpmn.backend.fromstunner.properties;

import java.util.Collection;

import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Property;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.VariableDeclaration;

import static org.kie.workbench.common.stunner.bpmn.backend.fromstunner.Factories.bpmn2;

public interface VariableScope {

    public Variable declare(String scopeId, String identifier, String type);

    public Variable lookup(String identifier);

    public Collection<Variable> getVariables(String scopeId);

    public static class Variable {

        String parentScopeId;
        VariableDeclaration declaration;
        ItemDefinition typeDeclaration;
        Property typedIdentifier;

        Variable(String parentScopeId, String identifier, String type) {
            this.parentScopeId = parentScopeId;
            this.declaration = new VariableDeclaration(identifier, type);

            this.typeDeclaration = bpmn2.createItemDefinition();
            this.typeDeclaration.setId("_" + identifier + "Item");
            this.typeDeclaration.setStructureRef(type);

            this.typedIdentifier = bpmn2.createProperty();
            this.typedIdentifier.setId("var@" + parentScopeId + "::" + identifier);
            this.typedIdentifier.setName(identifier);
            this.typedIdentifier.setItemSubjectRef(typeDeclaration);
        }

        public ItemDefinition getTypeDeclaration() {
            return typeDeclaration;
        }

        public Property getTypedIdentifier() {
            return typedIdentifier;
        }

        public String getParentScopeId() {
            return parentScopeId;
        }
    }
}
