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

package org.eclipse.bpmn2;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.resource.xml.URIHandler;
import org.eclipse.jbpm.BpmnXmlHelper;

public class QNameURIHandler extends URIHandler {

    private final BpmnXmlHelper xmlHelper;

    public QNameURIHandler(BpmnXmlHelper xmlHelper) {
        this.xmlHelper = xmlHelper;
    }

    public String convertQNameToUri(String qName) {
        if (!qName.contains("#") && !qName.contains("/")) {
            String[] parts = qName.split(":");
            String prefix;
            String fragment;
            if (parts.length == 1) {
                prefix = null;
                fragment = qName;
            } else {
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Illegal QName: " + qName);
                }

                prefix = parts[0];
                fragment = parts[1];
            }

            if (fragment.contains(".")) {
                return qName;
            } else {
                return !this.xmlHelper.isTargetNamespace(prefix) ?
                        this.xmlHelper.getPathForPrefix(prefix).appendFragment(fragment).toString() :
                        this.getBaseURI().appendFragment(fragment).toString();
            }
        } else {
            return qName;
        }
    }

    public URI resolve(URI uri) {
        return super.resolve(URI.createURI(this.convertQNameToUri(uri.toString())));
    }

    public URI deresolve(URI uri) {
        String fragment = uri.fragment();
        if (fragment != null && !fragment.startsWith("/")) {
            String prefix = "";
            if (uri.hasPath()) {
                prefix = this.xmlHelper.getNsPrefix(uri.trimFragment());
            }

            return prefix.length() > 0 ? URI.createURI(prefix + ":" + fragment) : URI.createURI(fragment);
        } else {
            return super.deresolve(uri);
        }
    }
}
