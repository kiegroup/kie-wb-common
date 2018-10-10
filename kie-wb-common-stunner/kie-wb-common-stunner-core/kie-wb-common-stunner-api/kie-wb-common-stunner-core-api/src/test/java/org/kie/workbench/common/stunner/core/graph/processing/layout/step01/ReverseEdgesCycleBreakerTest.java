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

package org.kie.workbench.common.stunner.core.graph.processing.layout.step01;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Graphs;
import org.kie.workbench.common.stunner.core.graph.processing.layout.ReorderedGraph;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ReverseEdgesCycleBreakerTest {

    @Test
    public void testAcyclicGraphs() {
        ReorderedGraph graph = new ReorderedGraph(Graphs.SimpleAcyclic);

        ReverseEdgesCycleBreaker breaker = new ReverseEdgesCycleBreaker();
        breaker.breakCycle(graph);

        Assert.assertTrue(graph.isAcyclic());
    }

    @Test
    public void testSimpleCyclicGraph() {
        ReorderedGraph graph = new ReorderedGraph(Graphs.SimpleCyclic);
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("C", "D");
        graph.addEdge("D", "A");

        Assert.assertFalse(graph.isAcyclic());

        ReverseEdgesCycleBreaker breaker = new ReverseEdgesCycleBreaker();
        breaker.breakCycle(graph);

        Assert.assertTrue(graph.isAcyclic());
    }

    @Test
    public void testCyclicGraph1() {
        ReorderedGraph graph = new ReorderedGraph(Graphs.CyclicGraph1);

        ReverseEdgesCycleBreaker breaker = new ReverseEdgesCycleBreaker();
        breaker.breakCycle(graph);

        Assert.assertTrue(graph.isAcyclic());
    }
}