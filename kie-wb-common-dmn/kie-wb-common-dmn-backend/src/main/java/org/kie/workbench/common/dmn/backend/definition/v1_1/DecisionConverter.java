package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;

import java.util.List;

import org.kie.dmn.model.v1_1.DMNElementReference;
import org.kie.dmn.model.v1_1.InformationRequirement;
import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.Question;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.UUID;

public class DecisionConverter implements Converter<org.kie.dmn.model.v1_1.Decision, org.kie.workbench.common.dmn.api.definition.v1_1.Decision> {
    
    private FactoryManager factoryManager;
    
    public DecisionConverter(FactoryManager factoryManager) {
        super();
        this.factoryManager = factoryManager;
    }

    @Override
    public Node<View<Decision>, ?> nodeFromDMN(org.kie.dmn.model.v1_1.Decision dmn) {
        @SuppressWarnings("unchecked")
        Node<View<Decision>, ?> node = (Node<View<Decision>, ?>) factoryManager.newElement(UUID.uuid(), Decision.class)
                                       .asNode();
        Id id = new Id( dmn.getId() );
        Description description = new Description( dmn.getDescription() );
        Name name = new Name( dmn.getName() );
        InformationItem informationItem = InformationItemPropertyConverter.from( dmn.getVariable() );
        Decision decision = new Decision(id,
                description,
                name,
                new Question(),
                new AllowedAnswers(),
                informationItem,
                new LiteralExpression(),
                new BackgroundSet(),
                new FontSet(),
                new RectangleDimensionsSet());
        node.getContent().setDefinition( decision );
        return node;
    }

    @Override
    public org.kie.dmn.model.v1_1.Decision dmnFromNode(Node<View<Decision>, ?> node) {
        Decision source = node.getContent().getDefinition();
        org.kie.dmn.model.v1_1.Decision d = new org.kie.dmn.model.v1_1.Decision();
        d.setId( source.getId().getValue() );
        d.setName( source.getName().getValue() );
        List<Edge<?, ?>> inEdges = (List<Edge<?, ?>>) node.getInEdges();
        for ( Edge<?, ?> e : inEdges ) {
            Node<?,?> sourceNode = e.getSourceNode();
            if ( sourceNode.getContent() instanceof View<?> ) {
                View<?> view = (View<?>) sourceNode.getContent();
                if ( view.getDefinition() instanceof DRGElement ) {
                    DRGElement drgElement = (DRGElement) view.getDefinition();
                    InformationRequirement iReq = new InformationRequirement();
                    DMNElementReference ri = new DMNElementReference();
                    ri.setHref( new StringBuilder("#").append( drgElement.getId().getValue() ).toString() );
                    iReq.setRequiredInput( ri );
                    d.getInformationRequirement().add(iReq);
                }
            }
        }
        return d;
    }

}
