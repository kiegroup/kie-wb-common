package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

public class FunctionDefinitionConverter {
    
    public static FunctionDefinition wbFromDMN( org.kie.dmn.model.v1_1.FunctionDefinition dmn ) {
        if ( dmn == null ) { return null; }
        Id id = new Id( dmn.getId() );
        Description description = new Description( dmn.getDescription() );
        QName typeRef = QNamePropertyConverter.wbFromDMN( dmn.getTypeRef() ) ;
        Expression expression = ExpressionPropertyConverter.wbFromDMN( dmn.getExpression() );
        FunctionDefinition result = new FunctionDefinition(id, description, typeRef, expression );
        return result;
    }

    public static org.kie.dmn.model.v1_1.FunctionDefinition dmnFromWB(FunctionDefinition wb) {
        if ( wb == null ) { return null; }
        org.kie.dmn.model.v1_1.FunctionDefinition result = new org.kie.dmn.model.v1_1.FunctionDefinition();
        result.setId( wb.getId().getValue() );
        result.setDescription( wb.getDescription().getValue() );
        QNamePropertyConverter.setDMNfromWB( wb.getTypeRef() , result::setTypeRef );
        result.setExpression( ExpressionPropertyConverter.dmnFromWB( wb.getExpression() ) );
        return result;
    }
    
}