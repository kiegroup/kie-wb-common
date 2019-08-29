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
package org.kie.workbench.common.stunner.kogito.api.diagram.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(MockitoJUnitRunner.class)
public class SubmarineDiagramImplTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testProjectDiagramEquals() {
        final DiagramImpl a = new DiagramImpl("Diagram",
                                              new GraphImpl("Graph",
                                                            new GraphNodeStoreImpl()),
                                              new MetadataImpl());
        final DiagramImpl b = new DiagramImpl("AnotherDiagram",
                                              new GraphImpl("Graph",
                                                            new GraphNodeStoreImpl()),
                                              new MetadataImpl());
        assertNotEquals(a,
                        b);
        final DiagramImpl c = new DiagramImpl("Diagram",
                                              new GraphImpl("Graph",
                                                            new GraphNodeStoreImpl()),
                                              new MetadataImpl());
        assertEquals(a,
                     c);

        a.setGraph(new GraphImpl("Graph",
                                 new GraphNodeStoreImpl()));
        c.setGraph(new GraphImpl("Graph",
                                 new GraphNodeStoreImpl()));
        assertEquals(a,
                     c);

        a.getGraph().addNode(new NodeImpl("Node1"));
        c.getGraph().addNode(new NodeImpl("Node2"));
        assertNotEquals(a,
                        c);
        c.getGraph().removeNode("Node2");
        c.getGraph().addNode(new NodeImpl("Node1"));
        assertEquals(a,
                     c);
        assertEquals(a,
                     a);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testProjectDiagramHashCode() {
        final DiagramImpl a = new DiagramImpl("Diagram",
                                              new GraphImpl("Graph",
                                                            new GraphNodeStoreImpl()),
                                              new MetadataImpl());
        final DiagramImpl b = new DiagramImpl("AnotherDiagram",
                                              new GraphImpl("Graph",
                                                            new GraphNodeStoreImpl()),
                                              new MetadataImpl());
        assertNotEquals(a.hashCode(),
                        b.hashCode());
        final DiagramImpl c = new DiagramImpl("Diagram",
                                              new GraphImpl("Graph",
                                                            new GraphNodeStoreImpl()),
                                              new MetadataImpl());
        assertEquals(a.hashCode(),
                     c.hashCode());

        a.setGraph(new GraphImpl("Graph",
                                 new GraphNodeStoreImpl()));
        c.setGraph(new GraphImpl("Graph",
                                 new GraphNodeStoreImpl()));
        assertEquals(a.hashCode(),
                     c.hashCode());

        a.getGraph().addNode(new NodeImpl("Node1"));
        c.getGraph().addNode(new NodeImpl("Node2"));
        assertNotEquals(a.hashCode(),
                        c.hashCode());
        c.getGraph().removeNode("Node2");
        c.getGraph().addNode(new NodeImpl("Node1"));
        assertEquals(a.hashCode(),
                     c.hashCode());
        assertEquals(a.hashCode(),
                     a.hashCode());
    }
}
