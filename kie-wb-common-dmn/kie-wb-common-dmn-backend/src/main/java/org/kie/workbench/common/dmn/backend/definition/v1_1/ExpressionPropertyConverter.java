package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;

// TODO currently supporting only literal expressions..
public class ExpressionPropertyConverter {

    public static Expression wbFromDMN( org.kie.dmn.model.v1_1.Expression dmn ) {
        if ( dmn instanceof org.kie.dmn.model.v1_1.LiteralExpression ) {
            return LiteralExpressionPropertyConverter.wbFromDMN( (org.kie.dmn.model.v1_1.LiteralExpression) dmn );
        }
        return null;
    }
    
    public static org.kie.dmn.model.v1_1.Expression dmnFromWB( Expression wb ) {
        if ( wb instanceof LiteralExpression ) {
            return LiteralExpressionPropertyConverter.dmnFromWB( (LiteralExpression) wb );
        }
        return null;
    }
}
