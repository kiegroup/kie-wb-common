package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeSource;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.KnowledgeSourceType;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.UUID;

public class KnowledgeSourceConverter implements NodeConverter<org.kie.dmn.model.v1_1.KnowledgeSource, org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeSource> {
    
    private FactoryManager factoryManager;
    
    public KnowledgeSourceConverter(FactoryManager factoryManager) {
        super();
        this.factoryManager = factoryManager;
    }

    @Override
    public Node<View<KnowledgeSource>, ?> nodeFromDMN(org.kie.dmn.model.v1_1.KnowledgeSource dmn) {
        @SuppressWarnings("unchecked")
        Node<View<KnowledgeSource>, ?> node = (Node<View<KnowledgeSource>, ?>) factoryManager.newElement(UUID.uuid(), KnowledgeSource.class)
                                              .asNode();
        Id id = new Id( dmn.getId() );
        Description description = new Description( dmn.getDescription() );
        Name name = new Name( dmn.getName() );
        KnowledgeSourceType ksType = new KnowledgeSourceType( dmn.getType() );
        LocationURI locationURI = new LocationURI( dmn.getLocationURI() );
        KnowledgeSource ks = new KnowledgeSource(
                id,
                description,
                name,
                ksType,
                locationURI,
                new BackgroundSet(),
                new FontSet(),
                new RectangleDimensionsSet());
        node.getContent().setDefinition( ks );
        return node;
    }

    @Override
    public org.kie.dmn.model.v1_1.KnowledgeSource dmnFromNode(Node<View<KnowledgeSource>, ?> node) {
        KnowledgeSource source = node.getContent().getDefinition();
        org.kie.dmn.model.v1_1.KnowledgeSource result = new org.kie.dmn.model.v1_1.KnowledgeSource();
        result.setId( source.getId().getValue() );
        result.setDescription( source.getDescription().getValue() );
        result.setName( source.getName().getValue() );
        result.setType( source.getType().getValue() );
        result.setLocationURI( source.getLocationURI().getValue() );
        return result;
    }

}
