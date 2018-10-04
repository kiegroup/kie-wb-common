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
package org.kie.workbench.common.stunner.core.graph.processing.layout;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.processing.layout.step01.CycleBreaker;
import org.kie.workbench.common.stunner.core.graph.processing.layout.step02.LongestPathVertexLayerer;
import org.kie.workbench.common.stunner.core.graph.processing.layout.step03.VertexOrdering;
import org.kie.workbench.common.stunner.core.graph.processing.layout.step04.VertexPositioning;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class IntegrationTests {

    @Test
    public void testRealCase1(){
        ReorderedGraph g1 = new ReorderedGraph(Graphs.RealCase1);

        CycleBreaker s01 = new CycleBreaker(g1);
        ReorderedGraph acyclic = s01.breakCycle();
        LongestPathVertexLayerer s02 = new LongestPathVertexLayerer(acyclic);
        ArrayList<Layer> layers = s02.execute();
        VertexOrdering s03 = new VertexOrdering(acyclic, layers);
        VertexOrdering.Ordered ordered = s03.process();

        Assert.assertEquals(6, ordered.getLayers().size());

        VertexPositioning drawing = new VertexPositioning();
        drawing.execute(ordered.getLayers(), ordered.getEdges(), VertexPositioning.LayerArrangement.TopDown);
    }

/*    @Test
    public void testRealCase0004(){
        Graph g1 = new Graph(Graphs.RealCase0004_Lending);

        CycleBreaker s01 = new CycleBreaker(g1);
        Graph acyclic = s01.breakCycle();
        LongestPathVertexLayerer s02 = new LongestPathVertexLayerer(acyclic);
        ArrayList<Layer> layers = s02.execute();
        VertexOrdering s03 = new VertexOrdering(acyclic, layers);
        VertexOrdering.Ordered ordered = s03.process();
        SimpleGraphDrawing drawing = new SimpleGraphDrawing();
        drawing.createDraw(ordered.getLayers(), ordered.getEdges());
    }*/
}