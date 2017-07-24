package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

public class InformationItemPropertyConverter {
    
    public static InformationItem wbFromDMN( org.kie.dmn.model.v1_1.InformationItem dmn ) {
        Id id = new Id( dmn.getId() );
        Description description = new Description( dmn.getDescription() );
        Name name = new Name( dmn.getName() );
        QName typeRef = QNamePropertyConverter.wbFromDMN( dmn.getTypeRef() ) ;
        InformationItem result = new InformationItem(id, description, name, typeRef);
        return result;
    }

    public static org.kie.dmn.model.v1_1.InformationItem dmnFromWB(InformationItem wb) {
        org.kie.dmn.model.v1_1.InformationItem result = new org.kie.dmn.model.v1_1.InformationItem();
        result.setId( wb.getId().getValue() );
        result.setDescription( wb.getDescription().getValue() );
        result.setName( wb.getName().getValue() );
        QNamePropertyConverter.setDMNfromWB( wb.getTypeRef() , result::setTypeRef );
        return result;
    }
    
}