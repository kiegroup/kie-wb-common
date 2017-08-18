/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.backend;

import java.util.Arrays;

import org.junit.Test;
import org.kie.dmn.api.marshalling.v1_1.DMNMarshaller;
import org.kie.dmn.backend.marshalling.v1_1.DMNMarshallerFactory;
import org.kie.dmn.model.v1_1.DMNElement.ExtensionElements;
import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.dmn.backend.definition.v1_1.DefinitionsConverter;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.DDExtensionsRegister;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.DMNShape;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.omg.spec.CMMN_20151109_DC.Bounds;

public class WIPDDDIMarshallerTest {

    @Test
    public void test1() {
        DMNMarshaller marshaller = DMNMarshallerFactory.newMarshallerWithExtensions(Arrays.asList( new DDExtensionsRegister() ));
        
        org.kie.dmn.model.v1_1.Definitions d = DefinitionsConverter.dmnFromWB( new Definitions() );
        d.setExtensionElements(new ExtensionElements());
        DMNShape x = new DMNShape();
        x.setId(UUID.uuid());
        x.setBounds(new Bounds());
        d.getExtensionElements().getAny().add(x);
        d.getExtensionElements().getAny().add(new DMNShape());
        String xmlString = marshaller.marshal(d);
        
        System.out.println(xmlString);
    }
    
}
