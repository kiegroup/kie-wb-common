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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.services;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.factory.DMNDiagramFactory;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.MainJs;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.callbacks.DMN12MarshallCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.callbacks.DMN12UnmarshallCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.DMN12;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.DMNMarshallerKogito;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.kogito.api.editor.DiagramType;
import org.kie.workbench.common.stunner.kogito.api.editor.impl.KogitoDiagramResourceImpl;
import org.kie.workbench.common.stunner.kogito.api.service.KogitoDiagramService;
import org.kie.workbench.common.stunner.kogito.client.service.KogitoClientDiagramService;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.promise.Promises;

@ApplicationScoped
public class KogitoClientDiagramServiceImpl implements KogitoClientDiagramService {

    private static final String DIAGRAMS_PATH = "diagrams";

    //This path is needed by DiagramsNavigatorImpl's use of AbstractClientDiagramService.lookup(..) to retrieve a list of diagrams
    private static final String ROOT = "default://master@system/stunner/" + DIAGRAMS_PATH;

    private ShapeManager shapeManager;
    private DMNMarshallerKogito dmnMarshaller;
    private Caller<KogitoDiagramService> submarineDiagramServiceCaller;
    private DefinitionManager definitionManager;
    private DMNDiagramFactory dmnDiagramFactory;
    private Promises promises;
    private DMN12 dmn12;

    public KogitoClientDiagramServiceImpl() {
        //CDI proxy
    }

    @Inject
    public KogitoClientDiagramServiceImpl(final ShapeManager shapeManager,
                                          final DMNMarshallerKogito dmnMarshaller,
                                          final Caller<KogitoDiagramService> submarineDiagramServiceCaller,
                                          final DefinitionManager definitionManager,
                                          final DMNDiagramFactory dmnDiagramFactory,
                                          final Promises promises) {
        this.shapeManager = shapeManager;
        this.dmnMarshaller = dmnMarshaller;
        this.submarineDiagramServiceCaller = submarineDiagramServiceCaller;
        this.definitionManager = definitionManager;
        this.dmnDiagramFactory = dmnDiagramFactory;
        this.promises = promises;
    }

    //Kogito requirements

    @Override
    public void transform(final String xml,
                          final ServiceCallback<Diagram> callback) {
        //TODO {manstis} XML->model marshalling... New diagrams too!
        if (!StringUtils.isEmpty(xml)) {
            testClientSideUnmarshaller(xml, callback);
        } else {
            //Legacy server-side marshalling for new diagrams
            submarineDiagramServiceCaller.call((Diagram d) -> {
                updateClientMetadata(d);
                callback.onSuccess(d);
            }).transform(xml);
        }
    }

    @Override
    public Promise<String> transform(final KogitoDiagramResourceImpl resource) {
        if (resource.getType() == DiagramType.PROJECT_DIAGRAM) {
            return promises.promisify(submarineDiagramServiceCaller,
                                      s -> {
                                          resource.projectDiagram().ifPresent(diagram -> testClientSideMarshaller(diagram.getGraph()));

                                          return s.transform(resource.projectDiagram().orElseThrow(() -> new IllegalStateException("DiagramType is PROJECT_DIAGRAM however no instance present")));
                                      });
        }
        return promises.resolve(resource.xmlDiagram().orElse("DiagramType is XML_DIAGRAM however no instance present"));
    }

    private void updateClientMetadata(final Diagram diagram) {
        if (null != diagram) {
            final Metadata metadata = diagram.getMetadata();
            if (Objects.nonNull(metadata) && StringUtils.isEmpty(metadata.getShapeSetId())) {
                final String sId = shapeManager.getDefaultShapeSet(metadata.getDefinitionSetId()).getId();
                metadata.setShapeSetId(sId);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void testClientSideUnmarshaller(final String xml,
                                            final ServiceCallback<Diagram> callback) {
        final DMN12UnmarshallCallback jsCallback = dmn12 -> {
            this.dmn12 = dmn12;
            final Metadata metadata = buildMetadataInstance();
            final JSITDefinitions definitions = Js.uncheckedCast(JsUtils.getUnwrappedElement(dmn12));
            final Graph graph = dmnMarshaller.unmarshall(metadata, definitions);
            final Node<Definition<DMNDiagram>, ?> diagramNode = GraphUtils.getFirstNode((Graph<?, Node>) graph, DMNDiagram.class);
            final String title = diagramNode.getContent().getDefinition().getDefinitions().getName().getValue();
            metadata.setTitle(title);

            final Diagram diagram = dmnDiagramFactory.build(title, metadata, graph);
            updateClientMetadata(diagram);
            callback.onSuccess(diagram);
        };

        try {
            MainJs.unmarshall(xml, jsCallback);
        } catch (Exception e) {
            GWT.log(e.getMessage());
        }
    }

    private Metadata buildMetadataInstance() {
        final String defSetId = BindableAdapterUtils.getDefinitionSetId(DMNDefinitionSet.class);
        return new MetadataImpl.MetadataImplBuilder(defSetId,
                                                    definitionManager)
                .setRoot(PathFactory.newPath(".", ROOT))
                .build();
    }

    @SuppressWarnings("unchecked")
    private void testClientSideMarshaller(final Graph graph) {
        if (Objects.isNull(graph)) {
            return;
        }

        final DMN12MarshallCallback jsCallback = xml -> {
            final String breakpoint = xml;
        };

        try {
            final JSITDefinitions jsitDefinitions = dmnMarshaller.marshall(graph);
            dmn12.setDefinitions(jsitDefinitions);
            MainJs.marshall(dmn12, jsCallback);
        } catch (Exception e) {
            GWT.log(e.getMessage());
        }
    }
}
