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

package org.kie.workbench.common.stunner.bpmn.factory;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.factory.AbstractBPMNDiagramFactory.DEFAULT_NAME;
import static org.kie.workbench.common.stunner.bpmn.factory.AbstractBPMNDiagramFactory.createValidName;

public class AbstractBPMNDiagramFactoryTest {

    @Test
    public void testDefaultValidName() {
        assertEquals(DEFAULT_NAME, createValidName(""));
        assertEquals(DEFAULT_NAME, createValidName(null));
        assertEquals(DEFAULT_NAME, createValidName("&"));
        assertEquals(DEFAULT_NAME, createValidName("#"));
        assertEquals(DEFAULT_NAME, createValidName(" "));
        assertEquals(DEFAULT_NAME, createValidName("£"));
    }

    @Test
    public void testFullyValidName() {
        String name = "SomeValidNameForTheПроцесс";
        assertEquals(name, createValidName(name));
    }

    @Test
    public void testSomeSymbolsCleared() {
        String name = "Hello $& Name";
        assertEquals("HelloName", createValidName(name));
    }
}