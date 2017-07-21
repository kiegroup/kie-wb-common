package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.kie.dmn.backend.marshalling.v1_1.xstream.MarshallingUtils;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.UUID;

public class InformationItemPropertyConverter {
    
    public static InformationItem wbFromDMN( org.kie.dmn.model.v1_1.InformationItem dmn ) {
        Id id = new Id( dmn.getId() );
        Description description = new Description( dmn.getDescription() );
        Name name = new Name( dmn.getName() );
        QName qname = new QName( MarshallingUtils.formatQName( dmn.getTypeRef() ) );
        InformationItem result = new InformationItem(id, description, name, qname);
        return result;
    }

    public static org.kie.dmn.model.v1_1.InformationItem dmnFromWB(InformationItem wb) {
        org.kie.dmn.model.v1_1.InformationItem result = new org.kie.dmn.model.v1_1.InformationItem();
        result.setId( wb.getId().getValue() );
        result.setDescription( wb.getDescription().getValue() );
        result.setName( wb.getName().getValue() );
        result.setTypeRef( MarshallingUtils.parseQNameString( wb.getTypeRef().getValue() ) );
        return result;
    }
    
}