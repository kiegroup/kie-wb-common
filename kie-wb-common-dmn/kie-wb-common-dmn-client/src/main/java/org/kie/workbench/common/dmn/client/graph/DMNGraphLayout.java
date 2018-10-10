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

package org.kie.workbench.common.dmn.client.graph;

import java.util.HashMap;

import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.HasBounds;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.processing.layout.AutomaticLayoutService;

public class DMNGraphLayout {

    public void applyLayout(
            final AutomaticLayoutService.Layout layout,
            final Graph graph) {

        if (layout.getNodePositions().size() == 0) {
            return;
        }

        final HashMap<String, Node> indexByUuid = new HashMap<>();
        for (Object n :
                graph.nodes()) {

            if (n instanceof Node) {
                Node node = (Node) n;
                indexByUuid.put(node.getUUID(), node);
            }
        }

        for (AutomaticLayoutService.NodePosition position : layout.getNodePositions()) {

            Node indexed = indexByUuid.get(position.getNodeId());
            if (indexed.getContent() instanceof HasBounds) {
                ((HasBounds) indexed.getContent()).setBounds(BoundsImpl.build(
                        position.getUpperLeft().getX(),
                        position.getUpperLeft().getY(),
                        position.getBottomRight().getX(),
                        position.getBottomRight().getY()
                ));
            }
        }
    }
}