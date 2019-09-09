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

package org.eclipse.jbpm;

import java.util.List;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Import;
import org.eclipse.bpmn2.impl.ImportHelper;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.xmi.resource.xml.XMLHelper;

public class BpmnXmlHelper extends XMLHelper {

    public BpmnXmlHelper(Bpmn2Resource resource) {
        super(resource);
    }

    private Definitions getDefinitions() {
        return ImportHelper.getDefinitions(this.getResource());
    }

    public boolean isTargetNamespace(String prefix) {
        if (prefix == null) {
            prefix = "";
        }

        String prefixNs = this.getNamespaceURI(prefix);
        if (prefixNs == null) {
            if ("".equals(prefix)) {
                return true;
            } else {
                throw new IllegalArgumentException("The prefix '%s' is not valid." + prefix);
            }
        } else if (prefixNs.equals(this.getDefinitions().getTargetNamespace())) {
            return true;
        } else {
            return "".equals(prefix) && ImportHelper.findImportForNamespace(this.getDefinitions(), prefixNs) == null;
        }
    }

    public URI getPathForPrefix(String prefix) {
        String ns = this.getNamespaceURI(prefix == null ? "" : prefix);
        if (ns != null) {
            Import imp = ImportHelper.findImportForNamespace(this.getDefinitions(), ns);
            return imp != null ? URI.createURI(imp.getLocation()).resolve(ImportHelper.makeURICanonical(this.getResource().getURI())) : URI.createURI(ns);
        } else {
            return URI.createURI("");
        }
    }

    private String getPrefixDuringSave(String namespace) {
        if (this.urisToPrefixes.containsKey(namespace)) {
            return (String) ((List) this.urisToPrefixes.get(namespace)).get(0);
        } else {
            EPackage ePackage = this.extendedMetaData.getPackage(namespace);
            if (ePackage == null) {
                ePackage = this.extendedMetaData.demandPackage(namespace);
            }

            String prefix;
            if (namespace.equals(this.getDefinitions().getTargetNamespace())) {
                prefix = "";
            } else {
                prefix = ePackage.getNsPrefix();
            }

            String originalPrefix = prefix + "_";

            for (int var5 = 0; this.prefixesToURIs.containsKey(prefix) && !((String) this.prefixesToURIs.get(prefix)).equals(namespace); prefix = originalPrefix + var5++) {
                ;
            }

            if (!this.packages.containsKey(ePackage)) {
                this.packages.put(ePackage, prefix);
            }

            this.prefixesToURIs.put(prefix, namespace);
            return prefix;
        }
    }

    public String getNsPrefix(URI referenced) {
        String ns = null;
        String prefix = "";
        URI referencedAbs = ImportHelper.makeURICanonical(referenced);
        URI thisAbs = ImportHelper.makeURICanonical(this.getResource().getURI());
        URI relativeToThis = referencedAbs.deresolve(thisAbs);
        if (relativeToThis.isEmpty()) {
            ns = this.getDefinitions().getTargetNamespace();
        } else {
            Import impForRef = ImportHelper.findImportForLocation(this.getDefinitions(), referenced);
            if (impForRef != null) {
                ns = impForRef.getNamespace();
            }
        }

        if (ns != null) {
            prefix = this.getPrefixDuringSave(ns);
        }

        return prefix;
    }
}
