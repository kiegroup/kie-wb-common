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

package org.kie.workbench.common.stunner.bpmn.client.marshall.service;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.ConverterUtils;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.factory.BPMNDiagramFactory;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.kie.workbench.common.stunner.submarine.api.diagram.SubmarineDiagram;
import org.kie.workbench.common.stunner.submarine.api.diagram.SubmarineMetadata;
import org.kie.workbench.common.stunner.submarine.api.diagram.impl.SubmarineMetadataImpl;
import org.kie.workbench.common.stunner.submarine.api.editor.DiagramType;
import org.kie.workbench.common.stunner.submarine.api.editor.impl.SubmarineDiagramResourceImpl;
import org.kie.workbench.common.stunner.submarine.client.service.SubmarineClientDiagramService;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.promise.Promises;

@ApplicationScoped
public class BPMNClientDiagramService implements SubmarineClientDiagramService {

    private final DefinitionManager definitionManager;
    private final BPMNClientMarshalling marshalling;
    private final FactoryManager factoryManager;
    private final BPMNDiagramFactory diagramFactory;
    private final ShapeManager shapeManager;
    private final Promises promises;

    //CDI proxy
    protected BPMNClientDiagramService() {
        this(null, null, null, null, null, null);
    }

    @Inject
    public BPMNClientDiagramService(final DefinitionManager definitionManager,
                                    final BPMNClientMarshalling marshalling,
                                    final FactoryManager factoryManager,
                                    final BPMNDiagramFactory diagramFactory,
                                    final ShapeManager shapeManager,
                                    final Promises promises) {
        this.definitionManager = definitionManager;
        this.marshalling = marshalling;
        this.factoryManager = factoryManager;
        this.diagramFactory = diagramFactory;
        this.shapeManager = shapeManager;
        this.promises = promises;
    }

    @Override
    public void transform(final String xml,
                          final ServiceCallback<SubmarineDiagram> callback) {
        // TODO: handle errors?
        SubmarineDiagram diagram = transform(xml);
        callback.onSuccess(diagram);
    }

    @Override
    public Promise<String> transform(final SubmarineDiagramResourceImpl resource) {
        if (resource.getType() == DiagramType.PROJECT_DIAGRAM) {
            return promises.resolve(transform(resource.projectDiagram().orElseThrow(() -> new IllegalStateException("DiagramType is PROJECT_DIAGRAM however no instance present"))));
        }
        return promises.resolve(resource.xmlDiagram().orElse("DiagramType is XML_DIAGRAM however no instance present"));
    }

    public SubmarineDiagram transform(final String xml) {
        if (Objects.isNull(xml) || xml.isEmpty()) {
            return doNewDiagram();
        }
        return doTransformation(xml);
    }

    public String transform(final SubmarineDiagram diagram) {
        return marshalling.marshall(convert(diagram));
    }

    private SubmarineDiagram doNewDiagram() {
        final String title = UUID.uuid();
        final String defSetId = BPMNClientMarshalling.getDefinitionSetId();
        final SubmarineMetadata metadata = createMetadata();
        metadata.setTitle(title);
        final SubmarineDiagram diagram = factoryManager.newDiagram(title,
                                                                   defSetId,
                                                                   metadata);
        updateClientMetadata(diagram);
        return diagram;
    }

    @SuppressWarnings("unchecked")
    private SubmarineDiagram doTransformation(final String raw) {
        final SubmarineMetadata metadata = createMetadata();
        final Graph<DefinitionSet, ?> graph = marshalling.unmarshall(metadata, raw);
        final Node<Definition<BPMNDiagram>, ?> diagramNode = GraphUtils.getFirstNode((Graph<?, Node>) graph, BPMNDiagramImpl.class);
        if (null == diagramNode) {
            throw new RuntimeException("No BPMN Diagram can be found.");
        }
        final String title = diagramNode.getContent().getDefinition().getDiagramSet().getName().getValue();
        metadata.setTitle(title);
        final SubmarineDiagram diagram = diagramFactory.build(title,
                                                              metadata,
                                                              graph);
        updateClientMetadata(diagram);
        return diagram;
    }

    // TODO: Necessary?
    private static final String ROOT_PATH = "default://master@system/stunner/diagrams";

    private SubmarineMetadata createMetadata() {
        return new SubmarineMetadataImpl.SubmarineMetadataBuilder(BPMNClientMarshalling.getDefinitionSetId(),
                                                                  definitionManager)
                .setRoot(PathFactory.newPath(".", ROOT_PATH))
                .build();
    }

    private void updateClientMetadata(final Diagram diagram) {
        if (null != diagram) {
            final Metadata metadata = diagram.getMetadata();
            if (Objects.nonNull(metadata) && ConverterUtils.isEmpty(metadata.getShapeSetId())) {
                final String sId = shapeManager.getDefaultShapeSet(metadata.getDefinitionSetId()).getId();
                metadata.setShapeSetId(sId);
            }
        }
    }

    private DiagramImpl convert(final SubmarineDiagram diagram) {
        return new DiagramImpl(diagram.getName(),
                               diagram.getGraph(),
                               diagram.getMetadata());
    }
}
