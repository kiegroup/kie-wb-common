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

package org.kie.workbench.common.stunner.core.graph.processing.layout.step03;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Edge;
import org.kie.workbench.common.stunner.core.graph.processing.layout.Layer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.ReorderedGraph;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class VertexOrderingTest {

    @Test
    public void testSimpleReorder() {
        ReorderedGraph graph = new ReorderedGraph();
        graph.addEdge("A", "D");
        graph.addEdge("B", "C");

        ArrayList<Layer> layers = new ArrayList<>();
        Layer layer01 = new Layer(1);
        layer01.addNewVertex("A");
        layer01.addNewVertex("B");
        layers.add(layer01);

        Layer layer02 = new Layer(2);
        layer02.addNewVertex("C");
        layer02.addNewVertex("D");
        layers.add(layer02);

        VertexOrdering ordering = new VertexOrdering(graph, layers);
        VertexOrdering.Ordered orderedLayers = ordering.process();

        assertArrayEquals(new Object[]{"A", "B"}, orderedLayers.getLayers().get(0).getVertices().stream().map(v -> v.getId()).toArray());
        assertArrayEquals(new Object[]{"D", "C"}, orderedLayers.getLayers().get(1).getVertices().stream().map(v -> v.getId()).toArray());
    }

    @Test
    public void testReorder() {
        ReorderedGraph graph = new ReorderedGraph();
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("F", "B");
        graph.addEdge("C", "E");
        graph.addEdge("G", "C");
        graph.addEdge("C", "H");
        graph.addEdge("D", "F");

        ArrayList<Layer> layers = new ArrayList<>();
        Layer layer01 = new Layer(1);
        layer01.addNewVertex("A");
        layers.add(layer01);

        Layer layer02 = new Layer(2);
        layer02.addNewVertex("B");
        layer02.addNewVertex("C");
        layer02.addNewVertex("D");
        layers.add(layer02);

        Layer layer03 = new Layer(3);
        layer03.addNewVertex("E");
        layer03.addNewVertex("F");
        layer03.addNewVertex("G");
        layer03.addNewVertex("H");
        layers.add(layer03);

        VertexOrdering ordering = new VertexOrdering(graph, layers);
        ArrayList<Layer> orderedLayers = ordering.process().getLayers();

        assertArrayEquals(new Object[]{"A"}, orderedLayers.get(0).getVertices().stream().map(v -> v.getId()).toArray());
        assertArrayEquals(new Object[]{"D", "B", "C"}, orderedLayers.get(1).getVertices().stream().map(v -> v.getId()).toArray());
        assertArrayEquals(new Object[]{"F", "E", "G", "H"}, orderedLayers.get(2).getVertices().stream().map(v -> v.getId()).toArray());
    }


    @Test
    public void calculateMedianTest(){

        ArrayList<Layer> layers = new ArrayList<>();
        ArrayList<Edge> edges = new ArrayList<>();
        edges.add(new Edge("G", "A"));
        edges.add(new Edge("G", "D"));
        edges.add(new Edge("G", "E"));

        Layer layer00 = new Layer(0);
        layer00.addNewVertex("A");
        layer00.addNewVertex("B");
        layer00.addNewVertex("C");
        layer00.addNewVertex("D");
        layer00.addNewVertex("E");
        Layer layer01 = new Layer(1);
        layer01.addNewVertex("F");
        layer01.addNewVertex("G");
        layer01.addNewVertex("H");
        layer01.addNewVertex("I");
        layer01.addNewVertex("J");
        layers.add(layer00);
        layers.add(layer01);

        double median = VertexOrdering.calculateMedianOfVerticesConnectedTo("G", layer00, edges);
        assertEquals(3.0, median, 0.0001);
    }

   /* @Test
    public void createVirtual() {
        Graph graph = new Graph();
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("A", "D");
        graph.addEdge("B", "E");
        graph.addEdge("C", "F");
        graph.addEdge("D", "G");
        graph.addEdge("A", "H");

        ArrayList<VertexOrdering.kie.wb.common.graph.layout.Layer> layers = new ArrayList<>();
        VertexOrdering.kie.wb.common.graph.layout.Layer layer1 = new VertexOrdering.kie.wb.common.graph.layout.Layer(1);
        layer1.addNewVertex("A");
        layers.add(layer1);

        VertexOrdering.kie.wb.common.graph.layout.Layer layer2 = new VertexOrdering.kie.wb.common.graph.layout.Layer(2);
        layer2.addNewVertex("B");
        layer2.addNewVertex("C");
        layer2.addNewVertex("D");
        layers.add(layer2);

        VertexOrdering.kie.wb.common.graph.layout.Layer layer3 = new VertexOrdering.kie.wb.common.graph.layout.Layer(3);
        layer3.addNewVertex("E");
        layer3.addNewVertex("F");
        layer3.addNewVertex("G");
        layer3.addNewVertex("H");
        layers.add(layer3);

        VertexOrdering ordering = new VertexOrdering(graph, layers);

        ArrayList<Edge> edges = new ArrayList<Edge>(Arrays.asList(graph.getEdges()));
        ArrayList<VertexOrdering.kie.wb.common.graph.layout.Layer> virtual = ordering.createVirtual(edges);

        virtual.size();
    }



    @Test
    public void testFlat(){
        ArrayList<VertexOrdering.kie.wb.common.graph.layout.Layer> layers = new ArrayList<>();
        VertexOrdering.kie.wb.common.graph.layout.Layer top = new VertexOrdering.kie.wb.common.graph.layout.Layer(0);
        top.addNewVertex("A");
        top.addNewVertex("B");
        top.addNewVertex("C");
        layers.add(top);

        VertexOrdering.kie.wb.common.graph.layout.Layer bottom = new VertexOrdering.kie.wb.common.graph.layout.Layer(1);
        bottom.addNewVertex("D");
        bottom.addNewVertex("E");
        bottom.addNewVertex("F");
        layers.add(bottom);

        ArrayList<Edge> edges = new ArrayList<>();
        edges.add(new Edge("A", "D"));
        edges.add(new Edge("A","E"));
        edges.add(new Edge("A","F"));

        Object[] expected = VertexOrdering.flat(edges, top, bottom);

        Assert.assertTrue(true);
    }*/

    //crossing

    @Test
    public void testSimpleCrossing() {
        ArrayList<Layer> layers = new ArrayList<>();
        Layer top = new Layer(0);
        top.addNewVertex("A");
        top.addNewVertex("B");
        layers.add(top);

        Layer bottom = new Layer(1);
        bottom.addNewVertex("C");
        bottom.addNewVertex("D");
        layers.add(bottom);

        ArrayList<Edge> edges = new ArrayList<>();
        edges.add(new Edge("A", "D"));
        edges.add(new Edge("B", "C"));

        int result = VertexOrdering.crossing(edges, top, bottom);

        assertEquals(1, result);
    }

    @Test
    public void testSimpleNoCrossing() {
        ArrayList<Layer> layers = new ArrayList<>();
        Layer top = new Layer(0);
        top.addNewVertex("A");
        top.addNewVertex("B");
        layers.add(top);

        Layer bottom = new Layer(1);
        bottom.addNewVertex("C");
        bottom.addNewVertex("D");
        layers.add(bottom);

        ArrayList<Edge> edges = new ArrayList<>();
        edges.add(new Edge("A", "C"));
        edges.add(new Edge("B", "D"));

        int result = VertexOrdering.crossing(edges, top, bottom);

        assertEquals(0, result);
    }

    @Test
    public void test1Crossing() {

        /*
         * 1 crossing
         * A   B   C
         *   \   /
         *     X
         *   /   \
         * D   E   F
         */
        ArrayList<Layer> layers = new ArrayList<>();
        Layer top = new Layer(0);
        top.addNewVertex("A");
        top.addNewVertex("B");
        top.addNewVertex("C");
        layers.add(top);

        Layer bottom = new Layer(1);
        bottom.addNewVertex("D");
        bottom.addNewVertex("E");
        bottom.addNewVertex("F");
        layers.add(bottom);

        ArrayList<Edge> edges = new ArrayList<>();
        edges.add(new Edge("A", "F"));
        edges.add(new Edge("D", "C"));

        int result = VertexOrdering.crossing(edges, top, bottom);

        assertEquals(1, result);
    }

    @Test
    public void test2CrossingsUnevenLayers() {

        /*
         * 2 crossings
         *       A           B
         *      /\\         /
         *     /  \ -------+ --
         *    / /-- +-----/    \
         *   / /     \          \
         *  D         E          F
         * */
        ArrayList<Layer> layers = new ArrayList<>();
        Layer top = new Layer(0);
        top.addNewVertex("A");
        top.addNewVertex("B");
        //top.addNewVertex("C");
        layers.add(top);

        Layer bottom = new Layer(1);
        bottom.addNewVertex("D");
        bottom.addNewVertex("E");
        bottom.addNewVertex("F");
        layers.add(bottom);

        ArrayList<Edge> edges = new ArrayList<>();
        edges.add(new Edge("A", "D"));
        edges.add(new Edge("E", "A"));
        edges.add(new Edge("A", "F"));
        edges.add(new Edge("D", "B"));

        int result = VertexOrdering.crossing(edges, top, bottom);

        assertEquals(2, result);
    }

    @Test
    public void test2Crossings() {

        /*
         * 2 crossings
         * A   B   C
         *  \  | /
         *   x x
         *  / \|
         * D   E   F
         * */
        ArrayList<Layer> layers = new ArrayList<>();
        Layer top = new Layer(0);
        top.addNewVertex("A");
        top.addNewVertex("B");
        top.addNewVertex("C");
        layers.add(top);

        Layer bottom = new Layer(1);
        bottom.addNewVertex("D");
        bottom.addNewVertex("E");
        bottom.addNewVertex("F");
        layers.add(bottom);

        ArrayList<Edge> edges = new ArrayList<>();
        edges.add(new Edge("A", "E"));
        edges.add(new Edge("B", "E"));
        edges.add(new Edge("C", "D"));

        int result = VertexOrdering.crossing(edges, top, bottom);

        assertEquals(2, result);
    }

    @Test
    public void test2Crossing8Vertex() {

        /*
         * 2 crossing
         *  A       B       C        D
         *  |\--\   |        \---\   |
         *  | \  \--+-------\     \  |
         *  |  \----+-----\  \     \ |
         *  |       |      \  \---\ \|
         *  E       F       G        H
         */
        ArrayList<Layer> layers = new ArrayList<>();
        Layer top = new Layer(0);
        top.addNewVertex("A");
        top.addNewVertex("B");
        top.addNewVertex("C");
        top.addNewVertex("D");
        layers.add(top);

        Layer bottom = new Layer(1);
        bottom.addNewVertex("E");
        bottom.addNewVertex("F");
        bottom.addNewVertex("G");
        bottom.addNewVertex("H");
        layers.add(bottom);

        ArrayList<Edge> edges = new ArrayList<>();
        edges.add(new Edge("A", "E"));
        edges.add(new Edge("A", "G"));
        edges.add(new Edge("A", "H"));
        edges.add(new Edge("B", "F"));
        edges.add(new Edge("C", "H"));
        edges.add(new Edge("D", "H"));

        int result = VertexOrdering.crossing(edges, top, bottom);

        assertEquals(2, result);
    }

    @Test
    public void test3CrossingsInMiddle() {

        /*
         * 3 crossings
         * A   B   C
         *   \ |  /
         *     X
         *   / | \
         * D   E   F
         */
        ArrayList<Layer> layers = new ArrayList<>();
        Layer top = new Layer(0);
        top.addNewVertex("A");
        top.addNewVertex("B");
        top.addNewVertex("C");
        layers.add(top);

        Layer bottom = new Layer(1);
        bottom.addNewVertex("D");
        bottom.addNewVertex("E");
        bottom.addNewVertex("F");
        layers.add(bottom);

        ArrayList<Edge> edges = new ArrayList<>();
        edges.add(new Edge("A", "F"));
        edges.add(new Edge("B", "E"));
        edges.add(new Edge("C", "D"));

        int result = VertexOrdering.crossing(edges, top, bottom);

        assertEquals(3, result);
    }

    @Test
    public void testK33GraphCrossing() {

        /*
         * k33 - every vertex from layer 1 connected to every vertex in layer 2
         */
        ArrayList<Layer> layers = new ArrayList<>();
        Layer top = new Layer(0);
        top.addNewVertex("A");
        top.addNewVertex("B");
        top.addNewVertex("C");
        layers.add(top);

        Layer bottom = new Layer(1);
        bottom.addNewVertex("D");
        bottom.addNewVertex("E");
        bottom.addNewVertex("F");
        layers.add(bottom);

        ArrayList<Edge> edges = new ArrayList<>();
        edges.add(new Edge("A", "D"));
        edges.add(new Edge("A", "E"));
        edges.add(new Edge("A", "F"));

        edges.add(new Edge("B", "D"));
        edges.add(new Edge("B", "E"));
        edges.add(new Edge("B", "F"));

        edges.add(new Edge("C", "D"));
        edges.add(new Edge("C", "E"));
        edges.add(new Edge("C", "F"));

        int result = VertexOrdering.crossing(edges, top, bottom);

        assertEquals(9, result);
    }

    @Test
    public void test5VertexUnevenLayersNoCrossing() {

        /*
         * 3 crossings
         * A    B
         * |   /|\
         * |  / | \
         * | /  |  \
         * D    E   F
         */
        ArrayList<Layer> layers = new ArrayList<>();
        Layer top = new Layer(0);
        top.addNewVertex("A");
        top.addNewVertex("B");
        //top.addNewVertex("C");
        layers.add(top);

        Layer bottom = new Layer(1);
        bottom.addNewVertex("D");
        bottom.addNewVertex("E");
        bottom.addNewVertex("F");
        layers.add(bottom);

        ArrayList<Edge> edges = new ArrayList<>();
        edges.add(new Edge("A", "D"));
        edges.add(new Edge("B", "D"));
        edges.add(new Edge("E", "B"));
        edges.add(new Edge("F", "B"));

        int result = VertexOrdering.crossing(edges, top, bottom);

        assertEquals(0, result);
    }
}