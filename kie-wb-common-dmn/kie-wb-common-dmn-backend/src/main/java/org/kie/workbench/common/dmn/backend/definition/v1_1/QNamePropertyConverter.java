package org.kie.workbench.common.dmn.backend.definition.v1_1;

import java.util.function.Consumer;

import org.kie.dmn.backend.marshalling.v1_1.xstream.MarshallingUtils;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

public class QNamePropertyConverter {

    /**
     * @return maybe null
     */
    public static QName wbFromDMN(javax.xml.namespace.QName qName) {
        return ( qName != null ) ? new QName( MarshallingUtils.formatQName( qName ) ) : null;
    }
    
    /*
     * Handles setting QName as appropriate back on DMN node
     */
    public static void setDMNfromWB(QName qname, Consumer<javax.xml.namespace.QName> setter) {
        if ( qname != null ) { 
            setter.accept( MarshallingUtils.parseQNameString( qname.getValue() ) );
        }
    }
}
