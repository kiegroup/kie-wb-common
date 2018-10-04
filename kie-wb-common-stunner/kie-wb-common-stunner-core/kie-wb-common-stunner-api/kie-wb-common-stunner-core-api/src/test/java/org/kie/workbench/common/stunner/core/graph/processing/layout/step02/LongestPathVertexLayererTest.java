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

package org.kie.workbench.common.stunner.core.graph.processing.layout.step02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Graphs;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Layer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.ReorderedGraph;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Vertex;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class LongestPathVertexLayererTest {

    @Test
    public void realCase1() {

        ReorderedGraph graph = new ReorderedGraph((Graphs.RealCase1));
        LongestPathVertexLayerer layerer = new LongestPathVertexLayerer(graph);
        ArrayList<Layer> result = layerer.execute();
    }

    @Test
    public void simple2LayersTest() {

        ReorderedGraph graph = new ReorderedGraph();
        graph.addEdge("A", "E");
        graph.addEdge("A", "G");
        graph.addEdge("A", "H");
        graph.addEdge("B", "F");
        graph.addEdge("C", "H");
        graph.addEdge("D", "H");

        LongestPathVertexLayerer layerer = new LongestPathVertexLayerer(graph);
        ArrayList<Layer> result = layerer.execute();

        assertEquals(2, result.size());

        Layer layer01 = result.get(0);
        match(new String[]{"A", "B", "C", "D"}, layer01);

        Layer layer02 = result.get(1);
        match(new String[]{"E", "F", "G", "H"}, layer02);
    }

    @Test
    public void simple3Layers() {

        ReorderedGraph graph = new ReorderedGraph();
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "F");
        graph.addEdge("C", "E");
        graph.addEdge("C", "G");
        graph.addEdge("C", "H");
        graph.addEdge("D", "F");

        LongestPathVertexLayerer layerer = new LongestPathVertexLayerer(graph);
        ArrayList<Layer> result = layerer.execute();

        assertEquals(3, result.size());

        Layer layer01 = result.get(0);
        match(new String[]{"A"}, layer01);

        Layer layer02 = result.get(1);
        match(new String[]{"D", "B", "C"}, layer02);

        Layer layer03 = result.get(2);
        match(new String[]{"E", "F", "G", "H"}, layer03);
    }

    @Test
    public void simple4Layers() {

        ReorderedGraph graph = new ReorderedGraph();
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "F");
        graph.addEdge("C", "E");
        graph.addEdge("C", "G");
        graph.addEdge("C", "H");
        graph.addEdge("B", "I");
        graph.addEdge("H", "I");
        graph.addEdge("G", "I");

        LongestPathVertexLayerer layerer = new LongestPathVertexLayerer(graph);
        ArrayList<Layer> result = layerer.execute();

        assertEquals(4, result.size());

        /* We're ensuring that the default algorithm behaviour is "good enough" and is not break by some change,
         * but If we changed it to a better one we'll have to modify this test to the new better expected result.
         */
        Layer layer01 = result.get(0);
        match(new String[]{"A"}, layer01);

        Layer layer02 = result.get(1);
        match(new String[]{"C"}, layer02);

        Layer layer03 = result.get(2);
        match(new String[]{"B", "G", "H"}, layer03);

        Layer layer04 = result.get(3);
        match(new String[]{"F", "I", "E"}, layer04);
    }

    @Test
    public void singleLineLayered() {
        ReorderedGraph graph = new ReorderedGraph();
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("C", "D");

        LongestPathVertexLayerer layerer = new LongestPathVertexLayerer(graph);
        ArrayList<Layer> result = layerer.execute();

        // TODO: should we decide if by design a single line should be vertical or horizontal
        assertEquals(4, result.size()); // 4 = layered = vertical line
    }

    private static void match(String[] expected, Layer layer) {
        assertEquals("kie.wb.common.graph.layout.Layer " + layer.getLevel() + " contains " + expected.length + " vertices",
                     expected.length,
                     layer.getVertices().size());

        String[] fromLayer = layer.getVertices().stream().map(Vertex::getId)
                .collect(Collectors.toSet())
                .toArray(new String[0]);

        boolean containsAllElements = Arrays.asList(expected).containsAll(Arrays.asList(fromLayer));

        assertTrue("kie.wb.common.graph.layout.Layer " + layer.getLevel() + " contains all expected vertices",
                   containsAllElements);
    }
}