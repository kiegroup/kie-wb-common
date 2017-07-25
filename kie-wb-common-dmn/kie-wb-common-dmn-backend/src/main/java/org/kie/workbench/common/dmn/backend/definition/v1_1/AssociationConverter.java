package org.kie.workbench.common.dmn.backend.definition.v1_1;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.dmn.api.definition.v1_1.Association;
import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.TextAnnotation;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class AssociationConverter {

    public static List<org.kie.dmn.model.v1_1.Association> dmnFromWB( Node<View<TextAnnotation>, ?> node ) {
        TextAnnotation ta = node.getContent().getDefinition();
        org.kie.dmn.model.v1_1.DMNElementReference ta_elementReference = new org.kie.dmn.model.v1_1.DMNElementReference();
        ta_elementReference.setHref( new StringBuilder("#").append( ta.getId().getValue() ).toString() );
        
        List<org.kie.dmn.model.v1_1.Association> result = new ArrayList<>();
        
        List<Edge<?, ?>> inEdges = (List<Edge<?, ?>>) node.getInEdges();
        for ( Edge<?, ?> e : inEdges ) {
            Node<?,?> sourceNode = e.getSourceNode();
            if ( sourceNode.getContent() instanceof View<?> ) {
                View<?> view = (View<?>) sourceNode.getContent();
                if ( view.getDefinition() instanceof DRGElement ) {
                    DRGElement drgElement = (DRGElement) view.getDefinition();
                    org.kie.dmn.model.v1_1.DMNElementReference sourceRef = new org.kie.dmn.model.v1_1.DMNElementReference();
                    sourceRef.setHref( new StringBuilder("#").append( drgElement.getId().getValue() ).toString() );
                    
                    org.kie.dmn.model.v1_1.Association adding = new org.kie.dmn.model.v1_1.Association();
                    adding.setId( ((View<Association>) e.getContent()).getDefinition().getId().getValue() );
                    adding.setDescription( ((View<Association>) e.getContent()).getDefinition().getDescription().getValue() );
                    adding.setSourceRef(sourceRef);
                    adding.setTargetRef(ta_elementReference);
                    result.add(adding);
                }
            }
        }
        List<Edge<?, ?>> outEdges = (List<Edge<?, ?>>) node.getOutEdges();
        for ( Edge<?, ?> e : outEdges ) {
            Node<?,?> targetNode = e.getTargetNode();
            if ( targetNode.getContent() instanceof View<?> ) {
                View<?> view = (View<?>) targetNode.getContent();
                if ( view.getDefinition() instanceof DRGElement ) {
                    DRGElement drgElement = (DRGElement) view.getDefinition();
                    org.kie.dmn.model.v1_1.DMNElementReference targetRef = new org.kie.dmn.model.v1_1.DMNElementReference();
                    targetRef.setHref( new StringBuilder("#").append( drgElement.getId().getValue() ).toString() );
                    
                    org.kie.dmn.model.v1_1.Association adding = new org.kie.dmn.model.v1_1.Association();
                    // TODO missing id, name
                    adding.setId( ((View<Association>) e.getContent()).getDefinition().getId().getValue() );
                    adding.setDescription( ((View<Association>) e.getContent()).getDefinition().getDescription().getValue() );
                    adding.setSourceRef(ta_elementReference);
                    adding.setTargetRef(targetRef);
                    result.add(adding);
                }
            }
        }
        
        return result;
    }
    
}