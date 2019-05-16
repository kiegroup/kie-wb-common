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

package org.kie.workbench.common.dmn.api.editors.included;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DMNImportTypesTest {

    @Test
    public void testDMN() {
        assertEquals("dmn", DMNImportTypes.DMN.getFileExtension());

        assertEquals(DMNImportTypes.DMN, DMNImportTypes.determineImportTypeType("http://www.omg.org/spec/DMN/20180521/MODEL/"));
    }

    @Test
    public void testPMML() {
        assertEquals("pmml", DMNImportTypes.PMML.getFileExtension());

        assertEquals(DMNImportTypes.PMML, DMNImportTypes.determineImportTypeType("http://www.dmg.org/PMML-3_0"));
        assertEquals(DMNImportTypes.PMML, DMNImportTypes.determineImportTypeType("http://www.dmg.org/PMML-3_1"));
        assertEquals(DMNImportTypes.PMML, DMNImportTypes.determineImportTypeType("http://www.dmg.org/PMML-3_2"));
        assertEquals(DMNImportTypes.PMML, DMNImportTypes.determineImportTypeType("http://www.dmg.org/PMML-4_0"));
        assertEquals(DMNImportTypes.PMML, DMNImportTypes.determineImportTypeType("http://www.dmg.org/PMML-4_1"));
        assertEquals(DMNImportTypes.PMML, DMNImportTypes.determineImportTypeType("http://www.dmg.org/PMML-4_2"));
        assertEquals(DMNImportTypes.PMML, DMNImportTypes.determineImportTypeType("http://www.dmg.org/PMML-4_3"));
    }

    @Test
    public void testUnknown() {
        assertNull(DMNImportTypes.determineImportTypeType("cheese"));
    }
}
