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
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.MainJs;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.callbacks.DMN12MarshallCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.callbacks.DMN12UnmarshallCallback;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.DMN12;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.DMNMarshallerKogito;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.utils.JsUtils;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.submarine.api.diagram.SubmarineDiagram;
import org.kie.workbench.common.stunner.submarine.api.editor.DiagramType;
import org.kie.workbench.common.stunner.submarine.api.editor.impl.SubmarineDiagramResourceImpl;
import org.kie.workbench.common.stunner.submarine.api.service.SubmarineDiagramService;
import org.kie.workbench.common.stunner.submarine.client.service.SubmarineClientDiagramService;
import org.uberfire.client.promise.Promises;

@ApplicationScoped
public class SubmarineClientDiagramServiceImpl implements SubmarineClientDiagramService {

    private ShapeManager shapeManager;
    private DMNMarshallerKogito dmnMarshaller;
    private Caller<SubmarineDiagramService> submarineDiagramServiceCaller;
    private Promises promises;
    private DMN12 dmn12;

    public SubmarineClientDiagramServiceImpl() {
        //CDI proxy
    }

    @Inject
    public SubmarineClientDiagramServiceImpl(final ShapeManager shapeManager,
                                             final DMNMarshallerKogito dmnMarshaller,
                                             final Caller<SubmarineDiagramService> submarineDiagramServiceCaller,
                                             final Promises promises) {
        this.shapeManager = shapeManager;
        this.dmnMarshaller = dmnMarshaller;
        this.submarineDiagramServiceCaller = submarineDiagramServiceCaller;
        this.promises = promises;
    }

    //Submarine requirements

    @Override
    public void transform(final String xml,
                          final ServiceCallback<SubmarineDiagram> callback) {

        //TODO {manstis} XML->model marshalling...
        //Stage 1 client-side marshalling
        // - Stage 1 is XML -> generic POJO model
        // - Stage 2 is generic POJO model to DMN editor UI model
        testClientSideUnmarshaller(xml);

        //Legacy server-side marshalling
        submarineDiagramServiceCaller.call((SubmarineDiagram d) -> {
            updateClientMetadata(d);
            callback.onSuccess(d);
        }).transform(xml);
    }

    @Override
    public Promise<String> transform(final SubmarineDiagramResourceImpl resource) {
        if (resource.getType() == DiagramType.PROJECT_DIAGRAM) {
            return promises.promisify(submarineDiagramServiceCaller,
                                      s -> {
                                          resource.projectDiagram().ifPresent(diagram -> testClientSideMarshaller(diagram.getGraph()));

                                          return s.transform(resource.projectDiagram().orElseThrow(() -> new IllegalStateException("DiagramType is PROJECT_DIAGRAM however no instance present")));
                                      });
        }
        return promises.resolve(resource.xmlDiagram().orElse("DiagramType is XML_DIAGRAM however no instance present"));
    }

    private void updateClientMetadata(final SubmarineDiagram diagram) {
        if (null != diagram) {
            final Metadata metadata = diagram.getMetadata();
            if (Objects.nonNull(metadata) && StringUtils.isEmpty(metadata.getShapeSetId())) {
                final String sId = shapeManager.getDefaultShapeSet(metadata.getDefinitionSetId()).getId();
                metadata.setShapeSetId(sId);
            }
        }
    }

    private void testClientSideUnmarshaller(final String xml) {
        if (StringUtils.isEmpty(xml)) {
            return;
        }

        final DMN12UnmarshallCallback callback = dmn12 -> {
            this.dmn12 = dmn12;
            final JSITDefinitions definitions = Js.uncheckedCast(JsUtils.getUnwrappedElement(dmn12));
            final Graph graph = dmnMarshaller.unmarshall(null, definitions);

            //Round-tripping to isolate issues
            testClientSideMarshaller(graph);
        };

        try {
            MainJs.unmarshall(xml, callback);
        } catch (Exception e) {
            GWT.log(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void testClientSideMarshaller(final Graph graph) {
        if (Objects.isNull(graph)) {
            return;
        }

        final DMN12MarshallCallback callback = xml -> {
            final String breakpoint = xml;
        };

        try {
            final JSITDefinitions jsitDefinitions = dmnMarshaller.marshall(graph);
            dmn12.setDefinitions(jsitDefinitions);
            MainJs.marshall(dmn12, callback);
        } catch (Exception e) {
            GWT.log(e.getMessage());
        }
    }
}
