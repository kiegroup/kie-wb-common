package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.kie.workbench.common.dmn.api.definition.v1_1.ImportedValues;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.ExpressionLanguage;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.Text;

public class LiteralExpressionPropertyConverter {

    public static LiteralExpression wbFromDMN( org.kie.dmn.model.v1_1.LiteralExpression dmn ) {
        Id id = new Id( dmn.getId() );
        Description description = new Description( dmn.getDescription() );
        QName typeRef = QNamePropertyConverter.wbFromDMN( dmn.getTypeRef() ) ;
        Text text = new Text( dmn.getText() );
        ExpressionLanguage expressionLanguage = new ExpressionLanguage( dmn.getExpressionLanguage() );
        // TODO missing importedValues
        LiteralExpression result = new LiteralExpression(id, description, typeRef, text, new ImportedValues(), expressionLanguage);
        return result;
    }
    
    public static org.kie.dmn.model.v1_1.LiteralExpression dmnFromWB( LiteralExpression wb ) {
        org.kie.dmn.model.v1_1.LiteralExpression result = new org.kie.dmn.model.v1_1.LiteralExpression();
        result.setId( wb.getId().getValue() );
        result.setDescription( wb.getDescription().getValue() );
        QNamePropertyConverter.setDMNfromWB( wb.getTypeRef() , result::setTypeRef );
        result.setText( wb.getText().getValue() );
        result.setExpressionLanguage( wb.getExpressionLanguage().getValue() );
        // TODO missing importedValues
        return result;
    }
}
