package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.kie.workbench.common.dmn.api.definition.v1_1.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.UUID;

public class BusinessKnowledgeModelConverter implements NodeConverter<org.kie.dmn.model.v1_1.BusinessKnowledgeModel, org.kie.workbench.common.dmn.api.definition.v1_1.BusinessKnowledgeModel> {
    
    private FactoryManager factoryManager;
    
    public BusinessKnowledgeModelConverter(FactoryManager factoryManager) {
        super();
        this.factoryManager = factoryManager;
    }

    @Override
    public Node<View<BusinessKnowledgeModel>, ?> nodeFromDMN(org.kie.dmn.model.v1_1.BusinessKnowledgeModel dmn) {
        @SuppressWarnings("unchecked")
        Node<View<BusinessKnowledgeModel>, ?> node = (Node<View<BusinessKnowledgeModel>, ?>) factoryManager.newElement(UUID.uuid(), BusinessKnowledgeModel.class)
                                                     .asNode();
        Id id = new Id( dmn.getId() );
        Description description = new Description( dmn.getDescription() );
        Name name = new Name( dmn.getName() );
        InformationItem informationItem = InformationItemPropertyConverter.wbFromDMN( dmn.getVariable() );
        FunctionDefinition functionDefinition = FunctionDefinitionConverter.wbFromDMN( dmn.getEncapsulatedLogic() );
        BusinessKnowledgeModel bkm = new BusinessKnowledgeModel(
                id,
                description,
                name,
                informationItem,
                functionDefinition,
                new BackgroundSet(),
                new FontSet(),
                new RectangleDimensionsSet());
        node.getContent().setDefinition( bkm );
        return node;
    }

    @Override
    public org.kie.dmn.model.v1_1.BusinessKnowledgeModel dmnFromNode(Node<View<BusinessKnowledgeModel>, ?> node) {
        BusinessKnowledgeModel source = node.getContent().getDefinition();
        org.kie.dmn.model.v1_1.BusinessKnowledgeModel result = new org.kie.dmn.model.v1_1.BusinessKnowledgeModel();
        result.setId( source.getId().getValue() );
        result.setDescription( source.getDescription().getValue() );
        result.setName( source.getName().getValue() );
        result.setVariable( InformationItemPropertyConverter.dmnFromWB( source.getVariable() ));
        result.setEncapsulatedLogic( FunctionDefinitionConverter.dmnFromWB( source.getEncapsulatedLogic() ) );
        return result;
    }

}
