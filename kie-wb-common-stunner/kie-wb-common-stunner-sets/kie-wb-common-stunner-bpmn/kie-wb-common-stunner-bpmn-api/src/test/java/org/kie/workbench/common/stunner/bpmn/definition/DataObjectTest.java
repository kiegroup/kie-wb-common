/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.definition;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.artifacts.DataObjectName;
import org.kie.workbench.common.stunner.bpmn.definition.property.artifacts.DataObjectType;
import org.kie.workbench.common.stunner.bpmn.definition.property.artifacts.DataObjectTypeValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class DataObjectTest {

    private DataObject dataObject = new DataObject();

    @Test
    public void getLabels() {
        assertEquals(1, dataObject.getLabels().size());
        assertEquals("all", dataObject.getLabels().iterator().next());
    }

    @Test
    public void getGeneral() {
        assertNotNull(dataObject.getGeneral());
    }

    @Test
    public void setGeneral() {
        BPMNGeneralSet general = new BPMNGeneralSet();
        dataObject.setGeneral(general);
        assertEquals(general, dataObject.getGeneral());
    }

    @Test
    public void setName() {
        DataObjectName name = new DataObjectName(this.getClass().getSimpleName());
        dataObject.setDataObjectName(name);
        assertEquals(name, dataObject.getDataObjectName());
    }

    @Test
    public void setType() {
        DataObjectType type = new DataObjectType(new DataObjectTypeValue(this.getClass().getSimpleName()));
        dataObject.setType(type);
        assertEquals(type, dataObject.getType());
    }

    @Test
    public void testHashCode() {
        assertEquals(new DataObject().hashCode(), dataObject.hashCode());
    }

    @Test
    public void testEquals() {
        assertEquals(new DataObject(), dataObject);
        assertNotEquals(new DataObject(), new Object());
    }
}