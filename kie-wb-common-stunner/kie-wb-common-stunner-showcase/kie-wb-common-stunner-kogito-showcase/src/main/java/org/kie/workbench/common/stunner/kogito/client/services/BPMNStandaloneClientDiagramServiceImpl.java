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

package org.kie.workbench.common.stunner.kogito.client.services;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.submarine.api.diagram.SubmarineDiagram;
import org.kie.workbench.common.stunner.submarine.api.diagram.SubmarineMetadata;
import org.kie.workbench.common.stunner.submarine.api.diagram.impl.SubmarineMetadataImpl;
import org.kie.workbench.common.stunner.submarine.api.editor.impl.SubmarineDiagramResourceImpl;
import org.kie.workbench.common.stunner.submarine.api.service.SubmarineDiagramService;
import org.kie.workbench.common.stunner.submarine.client.service.SubmarineClientDiagramService;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.promise.Promises;
import org.uberfire.commons.uuid.UUID;

@ApplicationScoped
public class BPMNStandaloneClientDiagramServiceImpl implements SubmarineClientDiagramService {

    private ShapeManager shapeManager;
    private Caller<VFSService> vfsServiceCaller;
    private Caller<SubmarineDiagramService> submarineDiagramServiceCaller;
    private Promises promises;

    @Inject
    private FactoryManager factoryManager;

    @Inject
    private DefinitionManager definitionManager;

    public BPMNStandaloneClientDiagramServiceImpl() {
        //CDI proxy
    }

    @Inject
    public BPMNStandaloneClientDiagramServiceImpl(final ShapeManager shapeManager,
                                                  final Caller<VFSService> vfsServiceCaller,
                                                  final Caller<SubmarineDiagramService> submarineDiagramServiceCaller,
                                                  final Promises promises) {
        this.shapeManager = shapeManager;
        this.vfsServiceCaller = vfsServiceCaller;
        this.submarineDiagramServiceCaller = submarineDiagramServiceCaller;
        this.promises = promises;
    }

    //Submarine requirements

    @Override
    public void transform(final String xml,
                          final ServiceCallback<SubmarineDiagram> callback) {
        submarineDiagramServiceCaller.call((SubmarineDiagram d) -> {
            updateClientMetadata(d);
            callback.onSuccess(d);
        }).transform(xml);

        callback.onSuccess(doNewDiagram());
    }

    private SubmarineDiagram doNewDiagram() {
        final String title = UUID.uuid();
        final String defSetId = BindableAdapterUtils.getDefinitionSetId(BPMNDefinitionSet.class);
        final SubmarineMetadata metadata = new SubmarineMetadataImpl.SubmarineMetadataBuilder(defSetId,
                                                                                              definitionManager)
                .setRoot(PathFactory.newPath(".", ""))
                .build();
        metadata.setTitle(title);

        try {
            return factoryManager.newDiagram(title,
                                             defSetId,
                                             metadata);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Promise<String> transform(final SubmarineDiagramResourceImpl resource) {
//        if (resource.getType() == DiagramType.PROJECT_DIAGRAM) {
//            return promises.promisify(submarineDiagramServiceCaller,
//                                      s -> {
//                                          return s.transform(resource.projectDiagram().orElseThrow(() -> new IllegalStateException("DiagramType is PROJECT_DIAGRAM however no instance present")));
//                                      });
//        }
//        return promises.resolve(resource.xmlDiagram().orElse("DiagramType is XML_DIAGRAM however no instance present"));
        return promises.resolve();
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
}
