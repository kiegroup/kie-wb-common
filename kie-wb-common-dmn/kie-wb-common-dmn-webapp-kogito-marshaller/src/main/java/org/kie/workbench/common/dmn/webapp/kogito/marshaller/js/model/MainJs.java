
package org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.callbacks.DCMarshallCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.callbacks.DCUnmarshallCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.callbacks.DIMarshallCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.callbacks.DIUnmarshallCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.callbacks.DMN12MarshallCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.callbacks.DMN12UnmarshallCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.callbacks.DMNDI12MarshallCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.callbacks.DMNDI12UnmarshallCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.DC;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.di.DI;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.DMN12;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.DMNDI12;


/**
 * JSInterop adapter to use for marshalling/unmarshalling.
 * 
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class MainJs {


    @JsMethod
    public final static native void unmarshall(String xmlString, DCUnmarshallCallback dCUnmarshallCallback);

    @JsMethod
    public final static native void marshall(DC dC, DCMarshallCallback dCMarshallCallback);

    @JsMethod
    public final static native void unmarshall(String xmlString, DMN12UnmarshallCallback dMN12UnmarshallCallback);

    @JsMethod
    public final static native void marshall(DMN12 dMN12, DMN12MarshallCallback dMN12MarshallCallback);

    @JsMethod
    public final static native void unmarshall(String xmlString, DMNDI12UnmarshallCallback dMNDI12UnmarshallCallback);

    @JsMethod
    public final static native void marshall(DMNDI12 dMNDI12, DMNDI12MarshallCallback dMNDI12MarshallCallback);

    @JsMethod
    public final static native void unmarshall(String xmlString, DIUnmarshallCallback dIUnmarshallCallback);

    @JsMethod
    public final static native void marshall(DI dI, DIMarshallCallback dIMarshallCallback);

}
