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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractLayoutServiceTest {

    @Mock
    private AbstractLayoutService layoutService;

    @Mock
    private Graph graph;

    @Mock
    Node n1;

    @Mock
    Node n2;

    @Test
    public void getLayoutInformationThreshold() {
        final GraphNodeStoreImpl store = new GraphNodeStoreImpl();
        store.add(n1);
        store.add(n2);

        doCallRealMethod().when(layoutService).getLayoutInformationThreshold(graph);
        when(graph.nodes()).thenReturn(store);

        final double threshold = layoutService.getLayoutInformationThreshold(graph);
        assertEquals(0.25, threshold, 0.01);
    }
}