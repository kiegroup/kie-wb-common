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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import bpsim.impl.BpsimPackageImpl;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.impl.DroolsPackageImpl;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.resource.JBPMBpmn2ResourceFactoryImpl;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.resource.JBPMBpmn2ResourceImpl;

public class BPMN2Definitions {
    public static Definitions parse(final InputStream inputStream) throws IOException {
        try {
            DroolsPackageImpl.init();
            BpsimPackageImpl.init();

            final ResourceSet resourceSet = new ResourceSetImpl();
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION,
                    new JBPMBpmn2ResourceFactoryImpl());
            resourceSet.getPackageRegistry().put("http://www.omg.org/spec/BPMN/20100524/MODEL",
                    Bpmn2Package.eINSTANCE);
            resourceSet.getPackageRegistry().put("http://www.jboss.org/drools",
                    DroolsPackage.eINSTANCE);

            final JBPMBpmn2ResourceImpl resource = (JBPMBpmn2ResourceImpl) resourceSet.createResource(URI.createURI("inputStream://dummyUriWithValidSuffix.xml"));
            resource.getDefaultLoadOptions().put(JBPMBpmn2ResourceImpl.OPTION_ENCODING,
                    "UTF-8");
            resource.setEncoding("UTF-8");

            final Map<String, Object> options = new HashMap<String, Object>();
            options.put(JBPMBpmn2ResourceImpl.OPTION_ENCODING,
                    "UTF-8");
            options.put(JBPMBpmn2ResourceImpl.OPTION_DEFER_IDREF_RESOLUTION,
                    true);
            options.put(JBPMBpmn2ResourceImpl.OPTION_DISABLE_NOTIFY,
                    true);
            options.put(JBPMBpmn2ResourceImpl.OPTION_PROCESS_DANGLING_HREF,
                    JBPMBpmn2ResourceImpl.OPTION_PROCESS_DANGLING_HREF_RECORD);

            resource.load(inputStream,
                    options);

            final DocumentRoot root = (DocumentRoot) resource.getContents().get(0);
            return root.getDefinitions();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return null;
    }}
