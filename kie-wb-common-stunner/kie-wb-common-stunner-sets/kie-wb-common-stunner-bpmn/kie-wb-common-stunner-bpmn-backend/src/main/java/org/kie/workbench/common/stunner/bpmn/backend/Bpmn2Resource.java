/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend;

import java.util.Iterator;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.util.ImportHelper;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.resource.JBPMBpmn2ResourceImpl;

public class Bpmn2Resource extends JBPMBpmn2ResourceImpl {

    public Bpmn2Resource(URI uri) {
        super(uri);
    }

    /**
     * Prepares this resource for saving.
     * <p>
     * Sets all ID attributes of cross-referenced objects
     * that are not yet set, to a generated UUID. Do not set if only referenced
     * by their container.
     */
    @Override
    protected void prepareSave() {
        EObject cur;
        Definitions thisDefinitions = ImportHelper.getDefinitions(this);
        setIdEvenIfSet(thisDefinitions);
        for (Iterator<EObject> iter = getAllContents(); iter.hasNext(); ) {
            cur = iter.next();

            for (EObject referenced : cur.eCrossReferences()) {
                if (referenced.eContainer() != cur) {
                    setIdIfNotSet(referenced);
                }
                if (thisDefinitions != null) {
                    Resource refResource = referenced.eResource();
                    if (refResource != null && refResource != this) {
                        createImportIfNecessary(thisDefinitions, refResource);
                    }
                }
            }
        }
    }
}