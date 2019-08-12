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
package org.kie.workbench.common.dmn.webapp.kogito.common.backend.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.factory.DMNDiagramFactory;
import org.kie.workbench.common.dmn.backend.DMNBackendService;
import org.kie.workbench.common.dmn.backend.DMNMarshaller;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.service.DefinitionSetService;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.DiagramParsingException;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.submarine.api.diagram.SubmarineDiagram;
import org.kie.workbench.common.stunner.submarine.api.diagram.SubmarineMetadata;
import org.kie.workbench.common.stunner.submarine.api.diagram.impl.SubmarineMetadataImpl;
import org.kie.workbench.common.stunner.submarine.api.service.SubmarineDiagramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.commons.uuid.UUID;

@Service
@ApplicationScoped
public class SubmarineDiagramServiceImpl implements SubmarineDiagramService {

    private static final Logger LOG = LoggerFactory.getLogger(SubmarineDiagramServiceImpl.class);

    private static final String DIAGRAMS_PATH = "diagrams";

    //This path is needed by DiagramsNavigatorImpl's use of AbstractClientDiagramService.lookup(..) to retrieve a list of diagrams
    private static final String ROOT = "default://master@system/stunner/" + DIAGRAMS_PATH;

    private DefinitionManager definitionManager;
    private FactoryManager factoryManager;
    private DMNBackendService dmnBackendService;
    private DMNDiagramFactory dmnDiagramFactory;

    protected SubmarineDiagramServiceImpl() {
        // CDI proxy.
        this(null,
             null,
             null,
             null);
    }

    @Inject
    public SubmarineDiagramServiceImpl(final DefinitionManager definitionManager,
                                       final FactoryManager factoryManager,
                                       final DMNBackendService dmnBackendService,
                                       final DMNDiagramFactory dmnDiagramFactory) {
        this.definitionManager = definitionManager;
        this.factoryManager = factoryManager;
        this.dmnBackendService = dmnBackendService;
        this.dmnDiagramFactory = dmnDiagramFactory;
    }

    @Override
    public SubmarineDiagram transform(final String xml) {
        if (Objects.isNull(xml) || xml.isEmpty()) {
            return doNewDiagram();
        }
        return doTransformation(xml);
    }

    private SubmarineDiagram doNewDiagram() {
        final String title = UUID.uuid();
        final String defSetId = getDefinitionSetId(dmnBackendService);
        final SubmarineMetadata metadata = buildMetadataInstance(defSetId);
        metadata.setTitle(title);

        try {
            return factoryManager.newDiagram(title,
                                             defSetId,
                                             metadata);
        } catch (final Exception e) {
            LOG.error("Cannot create new diagram", e);
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private SubmarineDiagram doTransformation(final String xml) {
        final String defSetId = getDefinitionSetId(dmnBackendService);
        final SubmarineMetadata metadata = buildMetadataInstance(defSetId);
        final InputStream is = new ByteArrayInputStream(xml.getBytes());

        try {
            final DMNMarshaller dmnMarshaller = (DMNMarshaller) dmnBackendService.getDiagramMarshaller();
            final Graph<DefinitionSet, ?> graph = dmnMarshaller.unmarshall(metadata, is);
            final Node<Definition<DMNDiagram>, ?> diagramNode = GraphUtils.getFirstNode((Graph<?, Node>) graph, DMNDiagram.class);
            final String title = diagramNode.getContent().getDefinition().getDefinitions().getName().getValue();
            metadata.setTitle(title);

            return dmnDiagramFactory.build(title,
                                           metadata,
                                           graph);
        } catch (Exception e) {
            LOG.error("Error whilst converting XML to DMNDiagram.", e);
            throw new DiagramParsingException(metadata, xml);
        }
    }

    @Override
    public String transform(final SubmarineDiagram diagram) {
        try {
            final DMNMarshaller dmnMarshaller = (DMNMarshaller) dmnBackendService.getDiagramMarshaller();
            return dmnMarshaller.marshall(convert(diagram));
        } catch (Exception e) {
            LOG.error("Error whilst converting DMNDiagram to XML.", e);
            throw new RuntimeException(e);
        }
    }

    private String getDefinitionSetId(final DefinitionSetService services) {
        final Class<?> type = services.getResourceType().getDefinitionSetType();
        return BindableAdapterUtils.getDefinitionSetId(type);
    }

    private SubmarineMetadata buildMetadataInstance(final String defSetId) {
        return new SubmarineMetadataImpl.SubmarineMetadataBuilder(defSetId,
                                                                  definitionManager)
                .setRoot(PathFactory.newPath(".", ROOT))
                .build();
    }

    private DiagramImpl convert(final SubmarineDiagram diagram) {
        return new DiagramImpl(diagram.getName(),
                               diagram.getGraph(),
                               diagram.getMetadata());
    }
}
