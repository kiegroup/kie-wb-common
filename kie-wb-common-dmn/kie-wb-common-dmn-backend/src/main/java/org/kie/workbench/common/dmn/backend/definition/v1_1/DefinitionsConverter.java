package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.Namespace;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

public class DefinitionsConverter {
    
    public static Definitions wbFromDMN( org.kie.dmn.model.v1_1.Definitions dmn ) {
        if ( dmn == null ) { return null; }
        Id id = new Id( dmn.getId() );
        Name name = new Name( dmn.getName() );
        String namespace = dmn.getNamespace();
        Description description = new Description( dmn.getDescription() );
        Definitions result = new Definitions();
        result.setId( id );
        result.setName( name );
        result.setNamespace( namespace );
        result.setDescription( description );
        result.getNsContext().putAll( dmn.getNsContext() );
        return result;
    }

    public static org.kie.dmn.model.v1_1.Definitions dmnFromWB(Definitions wb) {
        if ( wb == null ) { return null; }
        org.kie.dmn.model.v1_1.Definitions result = new org.kie.dmn.model.v1_1.Definitions();
        result.setId( wb.getId().getValue() );
        result.setName( wb.getName().getValue() );
        result.setNamespace( wb.getNamespace() );
        result.setDescription( wb.getDescription().getValue() );
        result.getNsContext().putAll( wb.getNsContext() );
        return result;
    }
    
}