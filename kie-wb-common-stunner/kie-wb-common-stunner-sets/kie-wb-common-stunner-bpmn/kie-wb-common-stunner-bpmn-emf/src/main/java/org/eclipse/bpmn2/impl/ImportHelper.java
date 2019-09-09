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

package org.eclipse.bpmn2.impl;

import java.util.Iterator;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.Import;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

public class ImportHelper {

    public static Definitions getDefinitions(Resource resource) {
        Iterator var2 = resource.getContents().iterator();

        while (var2.hasNext()) {
            EObject eobj = (EObject) var2.next();
            if (eobj instanceof Definitions) {
                return (Definitions) eobj;
            }

            if (eobj instanceof DocumentRoot) {
                return ((DocumentRoot) eobj).getDefinitions();
            }
        }

        return null;
    }

    public static Import findImportForNamespace(Definitions definitions, String namespace) {
        Iterator var3 = definitions.getImports().iterator();

        while (var3.hasNext()) {
            Import imp = (Import) var3.next();
            if (namespace.equals(imp.getNamespace())) {
                return imp;
            }
        }

        return null;
    }

    public static Import findImportForLocation(Definitions referencingModel, URI location) {
        URI referencingURI = makeURICanonical(referencingModel.eResource().getURI());
        URI referencedURI = makeURICanonical(location);
        Iterator var5 = referencingModel.getImports().iterator();

        while (var5.hasNext()) {
            Import imp = (Import) var5.next();
            if (imp.getLocation() != null) {
                URI importUri = URI.createURI(imp.getLocation()).resolve(referencingURI);
                if (importUri.equals(referencedURI)) {
                    return imp;
                }
            }
        }

        return null;
    }

    public static URI makeURICanonical(URI uri) {
        return uri;
    }

}
