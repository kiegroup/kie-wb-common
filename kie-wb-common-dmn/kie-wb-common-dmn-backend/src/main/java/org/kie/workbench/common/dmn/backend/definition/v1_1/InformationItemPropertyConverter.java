package org.kie.workbench.common.dmn.backend.definition.v1_1;

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
    
    public static InformationItem from( org.kie.dmn.model.v1_1.InformationItem dmn ) {
        Id id = new Id( dmn.getId() );
        Description description = new Description( dmn.getDescription() );
        Name name = new Name( dmn.getName() );
        QName qname = new QName( dmn.getTypeRef().toString() );
        InformationItem result = new InformationItem(id, description, name, qname);
        return result;
    }
    
}