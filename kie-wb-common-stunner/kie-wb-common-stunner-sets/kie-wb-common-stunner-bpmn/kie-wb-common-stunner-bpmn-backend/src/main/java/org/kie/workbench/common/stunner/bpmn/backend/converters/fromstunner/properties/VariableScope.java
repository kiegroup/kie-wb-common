package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import java.util.Collection;

import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Property;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Ids;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.VariableDeclaration;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public interface VariableScope {

    Variable declare(String scopeId, String identifier, String type);

    Variable lookup(String identifier);

    Collection<Variable> getVariables(String scopeId);

    class Variable {

        String parentScopeId;
        VariableDeclaration declaration;
        ItemDefinition typeDeclaration;
        Property typedIdentifier;

        Variable(String parentScopeId, String identifier, String type) {
            this.parentScopeId = parentScopeId;
            this.declaration = new VariableDeclaration(identifier, type);

            this.typeDeclaration = bpmn2.createItemDefinition();
            this.typeDeclaration.setId(Ids.item(identifier));
            this.typeDeclaration.setStructureRef(type);

            this.typedIdentifier = bpmn2.createProperty();
            this.typedIdentifier.setId(Ids.typedIdentifier(parentScopeId, identifier));
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
