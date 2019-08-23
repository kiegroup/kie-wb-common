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

package org.kie.workbench.common.stunner.kogito.backend.services;

import java.io.InputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.backend.service.XMLEncoderDiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMarshaller;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSetImpl;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.kie.workbench.common.stunner.core.util.UUID;

@ApplicationScoped
public class KogitoDiagramRepresentationMarshaller implements DiagramMarshaller<Graph, Metadata, Diagram<Graph, Metadata>> {

    private final XMLEncoderDiagramMetadataMarshaller diagramMetadataMarshaller;

    // CDI proxy.
    protected KogitoDiagramRepresentationMarshaller() {
        this(null);
    }

    @Inject
    public KogitoDiagramRepresentationMarshaller(final XMLEncoderDiagramMetadataMarshaller diagramMetadataMarshaller) {
        this.diagramMetadataMarshaller = diagramMetadataMarshaller;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Graph unmarshall(final Metadata metadata,
                            final InputStream input) {
        final String definitionSetId = metadata.getDefinitionSetId();
        final GraphImpl graph = new GraphImpl<>(UUID.uuid(),
                                                new GraphNodeStoreImpl());
        final DefinitionSet content = new DefinitionSetImpl(definitionSetId);
        graph.setContent(content);
        graph.getLabels().add(definitionSetId);
        return graph;
    }

    @Override
    public String marshall(final Diagram<Graph, Metadata> diagram) {
        throw new UnsupportedOperationException("DiagramRepresentationMarshaller is not able to marshall.");
    }

    @Override
    public DiagramMetadataMarshaller<Metadata> getMetadataMarshaller() {
        return diagramMetadataMarshaller;
    }
}