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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(JUnit4.class)
public class DefinitionSetHashCodeAndEqualityTest {

    @Test
    public void testDefinitionSetEquals() {
        DefinitionSet a = new DefinitionSetImpl("a");
        DefinitionSetImpl b = new DefinitionSetImpl("a");
        assertEquals(a,
                     b);

        b.setDefinition("b");
        assertNotEquals(a,
                        b);

        b.setDefinition("a");
        b.setBounds(new BoundsImpl(new BoundImpl(0.0,
                                                 0.0),
                                   new BoundImpl(5.0,
                                                 5.0)));
        assertNotEquals(a,
                        b);
        a.setBounds(new BoundsImpl(new BoundImpl(0.0,
                                                 0.0),
                                   new BoundImpl(5.0,
                                                 5.0)));

        assertEquals(a, b);
    }

    @Test
    public void testDefinitionSetHashCode() {
        DefinitionSetImpl a = new DefinitionSetImpl("a");
        DefinitionSetImpl b = new DefinitionSetImpl("a");
        assertEquals(a.hashCode(),
                     b.hashCode());

        b.setDefinition("b");
        assertNotEquals(a.hashCode(),
                        b.hashCode());

        b.setDefinition("a");
        b.setBounds(new BoundsImpl(new BoundImpl(0.0,
                                                 0.0),
                                   new BoundImpl(5.0,
                                                 5.0)));
        assertNotEquals(a.hashCode(),
                        b.hashCode());
        a.setBounds(new BoundsImpl(new BoundImpl(0.0,
                                                 0.0),
                                   new BoundImpl(5.0,
                                                 5.0)));

        assertEquals(a.hashCode(), b.hashCode());
    }
}
