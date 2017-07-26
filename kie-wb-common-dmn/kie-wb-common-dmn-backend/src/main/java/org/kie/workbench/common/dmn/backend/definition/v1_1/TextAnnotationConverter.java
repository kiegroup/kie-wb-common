package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.kie.workbench.common.dmn.api.definition.v1_1.TextAnnotation;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.api.property.dmn.TextFormat;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.UUID;

public class TextAnnotationConverter implements NodeConverter<org.kie.dmn.model.v1_1.TextAnnotation, org.kie.workbench.common.dmn.api.definition.v1_1.TextAnnotation> {
    
    private FactoryManager factoryManager;
    
    public TextAnnotationConverter(FactoryManager factoryManager) {
        super();
        this.factoryManager = factoryManager;
    }

    @Override
    public Node<View<TextAnnotation>, ?> nodeFromDMN(org.kie.dmn.model.v1_1.TextAnnotation dmn) {
        @SuppressWarnings("unchecked")
        Node<View<TextAnnotation>, ?> node = (Node<View<TextAnnotation>, ?>) factoryManager.newElement( dmn.getId(), TextAnnotation.class)
                                             .asNode();
        Id id = new Id( dmn.getId() );
        Description description = new Description( dmn.getDescription() );
        Text text = new Text( dmn.getText() );
        TextFormat textFormat = new TextFormat( dmn.getTextFormat() );
        TextAnnotation textAnnotation = new TextAnnotation(
                id,
                description,
                text,
                textFormat,
                new BackgroundSet(),
                new FontSet(),
                new RectangleDimensionsSet());
        node.getContent().setDefinition( textAnnotation );
        return node;
    }

    @Override
    public org.kie.dmn.model.v1_1.TextAnnotation dmnFromNode(Node<View<TextAnnotation>, ?> node) {
        TextAnnotation source = node.getContent().getDefinition();
        org.kie.dmn.model.v1_1.TextAnnotation result = new org.kie.dmn.model.v1_1.TextAnnotation();
        result.setId( source.getId().getValue() );
        result.setDescription( source.getDescription().getValue() );
        result.setText( source.getText().getValue() );
        result.setTextFormat( source.getTextFormat().getValue() );
        return result;
    }

}
