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

package org.kie.workbench.common.stunner.core.graph.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(JUnit4.class)
public class AbstractElementHashCodeAndEqualityTest {

    private static class ConcreteTestElement extends AbstractElement<String> {

        public ConcreteTestElement(String uuid) {
            super(uuid);
        }

        @Override
        public Node<String, Edge> asNode() {
            return null;
        }

        @Override
        public Edge<String, Node> asEdge() {
            return null;
        }
    }

    @Test
    public void testAbstractElementEquals() {
        AbstractElement<String> a = new ConcreteTestElement("a");
        AbstractElement<String> b = new ConcreteTestElement("a");
        assertEquals(a,
                     b);

        a.setContent("x");
        assertNotEquals(a,
                        b);

        b.setContent("x");
        assertEquals(a, b);
    }

    @Test
    public void testViewHashCode() {
        AbstractElement<String> a = new ConcreteTestElement("a");
        AbstractElement<String> b = new ConcreteTestElement("a");
        assertEquals(a.hashCode(),
                     b.hashCode());

        a.setContent("x");
        assertNotEquals(a.hashCode(),
                        b.hashCode());

        b.setContent("x");
        assertEquals(a.hashCode(), b.hashCode());

    }
}
