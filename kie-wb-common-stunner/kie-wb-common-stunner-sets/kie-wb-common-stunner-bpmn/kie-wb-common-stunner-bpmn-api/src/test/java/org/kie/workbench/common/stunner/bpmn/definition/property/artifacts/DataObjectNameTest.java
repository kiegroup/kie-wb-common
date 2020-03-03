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

package org.kie.workbench.common.stunner.bpmn.definition.property.artifacts;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DataObjectNameTest {

    private DataObjectName dataObjectName = new DataObjectName();

    @Test
    public void getValue() {
        DataObjectName value = new DataObjectName(this.getClass().getSimpleName());
        assertEquals(getClass().getSimpleName(), value.getValue());
    }

    @Test
    public void setValue() {
        DataObjectName value = new DataObjectName();
        value.setValue(this.getClass().getSimpleName());
        assertEquals(getClass().getSimpleName(), value.getValue());
    }

    @Test
    public void getType() {
        DataObjectName value = new DataObjectName();
        assertEquals(DataObjectName.type, value.getType());
    }

    @Test
    public void testHashCode() {
        assertEquals(new DataObjectName().hashCode(), dataObjectName.hashCode());
    }

    @Test
    public void testEquals() {
        assertEquals(new DataObjectName(), dataObjectName);
        assertNotEquals(new DataObjectName(), new Object());
    }
}
