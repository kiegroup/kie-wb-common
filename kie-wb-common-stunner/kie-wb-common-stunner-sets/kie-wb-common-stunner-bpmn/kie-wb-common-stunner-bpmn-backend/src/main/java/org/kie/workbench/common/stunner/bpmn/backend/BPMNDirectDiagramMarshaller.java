/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Process;
import org.kie.workbench.common.stunner.bpmn.backend.converters.ProcessConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMarshaller;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/*
 * Direct as in "skipping json encoding"
 */
public class BPMNDirectDiagramMarshaller<D> implements DiagramMarshaller<Graph, Metadata, Diagram<Graph, Metadata>> {

    private static final Logger LOG = LoggerFactory.getLogger(BPMNDirectDiagramMarshaller.class);

    private final FactoryManager factoryManager;

    public BPMNDirectDiagramMarshaller(final FactoryManager factoryManager) {
        this.factoryManager = factoryManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String marshall(final Diagram diagram) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Graph<DefinitionSet, Node> unmarshall(final Metadata metadata,
                            final InputStream inputStream) throws IOException {
        LOG.debug("Starting diagram unmarshalling...");

        TypedFactoryManager typedFactoryManager = new TypedFactoryManager(this.factoryManager);
        ProcessConverter processConverter = new ProcessConverter(typedFactoryManager);

        final Definitions definitions = BPMN2Definitions.parse(inputStream);
        Process processDiagram = (Process) definitions.getRootElements().get(0);
        Graph<DefinitionSet, Node> graph = processConverter.convert(processDiagram);

        // Update diagram's settings.
        Node<Definition<BPMNDiagramImpl>, ?> firstDiagramNode = GraphUtils.getFirstNode(graph, BPMNDiagramImpl.class);

        String uuid = firstDiagramNode.getUUID();
        metadata.setCanvasRootUUID(uuid);

        String title = firstDiagramNode.getContent().getDefinition().getDiagramSet().getName().getValue();
        metadata.setTitle(title);


        LOG.debug("Diagram unmarshalling finished successfully.");
        return graph;
    }

    @Override
    public DiagramMetadataMarshaller<Metadata> getMetadataMarshaller() {
        return null;
    }
}
