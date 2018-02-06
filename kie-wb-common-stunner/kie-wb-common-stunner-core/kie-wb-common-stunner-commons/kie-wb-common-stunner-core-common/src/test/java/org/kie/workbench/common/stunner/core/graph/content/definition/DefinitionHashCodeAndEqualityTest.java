/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.graph.content.definition;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(JUnit4.class)
public class DefinitionHashCodeAndEqualityTest {

    @Test
    public void testDefinitionEquals() {
        DefinitionImpl<String> a = new DefinitionImpl<>("a");
        DefinitionImpl<String> b = new DefinitionImpl<>("a");
        assertEquals(a,
                     b);

        b.setDefinition(null);
        assertNotEquals(a,
                        b);

        b.setDefinition("b");
        assertNotEquals(a,
                        b);

        b.setDefinition("a");
        assertEquals(a, b);
    }

    @Test
    public void testDefinitionHashCode() {
        DefinitionImpl<String> a = new DefinitionImpl<>("a");
        DefinitionImpl<String> b = new DefinitionImpl<>("a");
        assertEquals(a.hashCode(),
                     b.hashCode());

        b.setDefinition("b");
        assertNotEquals(a.hashCode(),
                        b.hashCode());

        b.setDefinition(null);
        assertNotEquals(a,
                        b);

        b.setDefinition("a");
        assertEquals(a.hashCode(), b.hashCode());

    }
}
