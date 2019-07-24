/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package javax.xml.namespace;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class QName {

    @JsProperty(name = "namespaceURI")
    public final native String getNamespaceURI();

    @JsProperty(name = "namespaceURI")
    public final native void setNamespaceURI(final String namespaceURI);

    @JsProperty(name = "localPart")
    public final native String getLocalPart();

    @JsProperty(name = "localPart")
    public final native void setLocalPart(final String localPart);

    @JsProperty(name = "prefix")
    public final native String getPrefix();

    @JsProperty(name = "prefix")
    public final native void setPrefix(final String prefix);
}
