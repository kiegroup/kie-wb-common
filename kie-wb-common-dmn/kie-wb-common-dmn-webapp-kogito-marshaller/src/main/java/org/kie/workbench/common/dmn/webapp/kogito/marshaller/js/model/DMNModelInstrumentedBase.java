/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(namespace = JsPackage.GLOBAL)
public class DMNModelInstrumentedBase {

    @JsIgnore
    public static final String URI_DMN = "http://www.omg.org/spec/DMN/20180521/MODEL/";

    @JsIgnore
    public static final String URI_FEEL = "http://www.omg.org/spec/DMN/20180521/FEEL/";

    @JsIgnore
    public static final String URI_KIE = "http://www.drools.org/kie/dmn/1.2";

    @JsIgnore
    public static final String URI_DMNDI = "http://www.omg.org/spec/DMN/20180521/DMNDI/";

    @JsIgnore
    public static final String URI_DI = "http://www.omg.org/spec/DMN/20180521/DI/";

    @JsIgnore
    public static final String URI_DC = "http://www.omg.org/spec/DMN/20180521/DC/";

    private Map<String, String> nsContext;
    private DMNModelInstrumentedBase parent;

    @JsIgnore
    //TODO {manstis} Remove. This does not exist in the XSD. User otherAttributes() and NamespaceUtils.
    public Map<String, String> getNsContext() {
        if (nsContext == null) {
            nsContext = new HashMap<>();
        }
        return nsContext;
    }

    @JsIgnore
    //TODO {manstis} Remove. This does not exist in the XSD. User otherAttributes() and NamespaceUtils.
    public String getNamespaceURI(final String prefix) {
        if (getNsContext().containsKey(prefix)) {
            return getNsContext().get(prefix);
        }
        if (this.parent != null) {
            return parent.getNamespaceURI(prefix);
        }
        return null;
    }

    @JsIgnore
    //TODO {manstis} Remove. This does not exist in the XSD. User otherAttributes() and NamespaceUtils.
    public Optional<String> getPrefixForNamespaceURI(final String namespaceURI) {
        if (getNsContext().containsValue(namespaceURI)) {
            return getNsContext().entrySet().stream().filter(kv -> kv.getValue().equals(namespaceURI)).findFirst().map(Map.Entry::getKey);
        }
        if (this.parent != null) {
            return parent.getPrefixForNamespaceURI(namespaceURI);
        }
        return Optional.empty();
    }

    @JsIgnore
    public DMNModelInstrumentedBase getParent() {
        return parent;
    }

    @JsIgnore
    public void setParent(final DMNModelInstrumentedBase parent) {
        this.parent = parent;
    }
}
