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

package org.kie.workbench.common.dmn.backend.editors.types.common;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.v1_1.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.service.DiagramService;
import org.uberfire.backend.vfs.Path;

public class DMNDiagramHelper {

    private final DiagramService diagramService;

    @Inject
    public DMNDiagramHelper(final DiagramService diagramService) {
        this.diagramService = diagramService;
    }

    public List<DRGElement> getNodes(final Diagram diagram) {
        return getDefinitionStream(diagram)
                .filter(d -> d instanceof DRGElement)
                .map(d -> (DRGElement) d)
                .collect(Collectors.toList());
    }

    public String getNamespace(final Path path) {
        final Diagram<Graph, Metadata> diagram = getDiagramByPath(path);
        return getNamespace(diagram);
    }

    public String getNamespace(final Diagram diagram) {
        return getDefinitionStream(diagram)
                .filter(d -> d instanceof DMNDiagram)
                .map(d -> (DMNDiagram) d)
                .findFirst()
                .map(DMNDiagram::getDefinitions)
                .map(definitions -> definitions.getNamespace().getValue())
                .orElse("");
    }

    public Diagram<Graph, Metadata> getDiagramByPath(final Path path) {
        return diagramService.getDiagramByPath(path);
    }

    private Stream<Object> getDefinitionStream(final Diagram diagram) {
        return getNodeStream(diagram)
                .map(Node::getContent)
                .filter(c -> c instanceof Definition)
                .map(c -> (Definition) c)
                .map(Definition::getDefinition);
    }

    @SuppressWarnings("unchecked")
    private Stream<Node> getNodeStream(final Diagram diagram) {
        final Graph<?, Node> graph = diagram.getGraph();
        return StreamSupport.stream(graph.nodes().spliterator(), false);
    }
}
