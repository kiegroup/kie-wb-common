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

import java.util.ArrayList;

import bpsim.impl.BpsimFactoryImpl;
import org.eclipse.bpmn2.util.OnlyContainmentTypeInfo;
import org.eclipse.bpmn2.util.XmlExtendedMetadata;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.ElementHandlerImpl;
import org.jboss.drools.impl.DroolsFactoryImpl;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.resource.JBPMBpmn2ResourceFactoryImpl;

public class Bpmn2ResourceFactory extends JBPMBpmn2ResourceFactoryImpl {

    @Override
    public Bpmn2Resource createResource(URI uri) {
        DroolsFactoryImpl.init();
        BpsimFactoryImpl.init();
        Bpmn2Resource result = new Bpmn2Resource(uri);
        ExtendedMetaData extendedMetadata = new XmlExtendedMetadata();
        result.getDefaultSaveOptions().put(XMLResource.OPTION_EXTENDED_META_DATA,
                                           extendedMetadata);
        result.getDefaultLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA,
                                           extendedMetadata);
        result.getDefaultSaveOptions().put(XMLResource.OPTION_SAVE_TYPE_INFORMATION,
                                           new OnlyContainmentTypeInfo());
        result.getDefaultLoadOptions().put(XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE,
                                           Boolean.TRUE);
        result.getDefaultSaveOptions().put(XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE,
                                           Boolean.TRUE);
        result.getDefaultLoadOptions().put(XMLResource.OPTION_USE_LEXICAL_HANDLER,
                                           Boolean.TRUE);
        result.getDefaultSaveOptions().put(XMLResource.OPTION_ELEMENT_HANDLER,
                                           new ElementHandlerImpl(true));
        result.getDefaultSaveOptions().put(XMLResource.OPTION_ENCODING,
                                           "UTF-8");
        result.getDefaultSaveOptions().put(XMLResource.OPTION_USE_CACHED_LOOKUP_TABLE,
                                           new ArrayList<Object>());
        result.getDefaultSaveOptions().put(XMLResource.OPTION_DEFER_IDREF_RESOLUTION,
                                           true);
        result.getDefaultSaveOptions().put(XMLResource.OPTION_PROCESS_DANGLING_HREF,
                                           XMLResource.OPTION_PROCESS_DANGLING_HREF_RECORD);
        return result;
    }
}

