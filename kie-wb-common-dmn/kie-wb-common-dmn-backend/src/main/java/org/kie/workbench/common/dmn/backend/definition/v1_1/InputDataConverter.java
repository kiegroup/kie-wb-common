package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
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

public class InputDataConverter implements NodeConverter<org.kie.dmn.model.v1_1.InputData, org.kie.workbench.common.dmn.api.definition.v1_1.InputData> {
    
    private FactoryManager factoryManager;
    
    public InputDataConverter(FactoryManager factoryManager) {
        super();
        this.factoryManager = factoryManager;
    }

    @Override
    public Node<View<InputData>, ?> nodeFromDMN(org.kie.dmn.model.v1_1.InputData dmn) {
        @SuppressWarnings("unchecked")
        Node<View<InputData>, ?> node = (Node<View<InputData>, ?>) factoryManager.newElement( dmn.getId(), InputData.class)
                                        .asNode();
        Id id = new Id( dmn.getId() );
        Description description = new Description( dmn.getDescription() );
        Name name = new Name( dmn.getName() );
        InformationItem informationItem = InformationItemPropertyConverter.wbFromDMN( dmn.getVariable() );
        InputData inputData = new InputData(id,
                description,
                name,
                informationItem,
                new BackgroundSet(),
                new FontSet(),
                new RectangleDimensionsSet());
        node.getContent().setDefinition( inputData );
        return node;
    }

    @Override
    public org.kie.dmn.model.v1_1.InputData dmnFromNode(Node<View<InputData>, ?> node) {
        InputData source = node.getContent().getDefinition();
        org.kie.dmn.model.v1_1.InputData result = new org.kie.dmn.model.v1_1.InputData();
        result.setId( source.getId().getValue() );
        result.setDescription( source.getDescription().getValue() );
        result.setName( source.getName().getValue() );
        result.setVariable( InformationItemPropertyConverter.dmnFromWB( source.getVariable() ));
        return result;
    }

}
