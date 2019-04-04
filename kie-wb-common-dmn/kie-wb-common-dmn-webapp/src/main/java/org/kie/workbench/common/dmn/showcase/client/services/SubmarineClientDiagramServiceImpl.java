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

package org.kie.workbench.common.dmn.showcase.client.services;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.submarine.api.diagram.SubmarineDiagram;
import org.kie.workbench.common.stunner.submarine.api.editor.DiagramType;
import org.kie.workbench.common.stunner.submarine.api.editor.impl.SubmarineDiagramResourceImpl;
import org.kie.workbench.common.stunner.submarine.api.service.SubmarineDiagramService;
import org.kie.workbench.common.stunner.submarine.client.service.SubmarineClientDiagramService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.promise.Promises;

@ApplicationScoped
public class SubmarineClientDiagramServiceImpl implements SubmarineClientDiagramService {

    private ShapeManager shapeManager;
    private Caller<VFSService> vfsServiceCaller;
    private Caller<SubmarineDiagramService> submarineDiagramServiceCaller;
    private Promises promises;

    public SubmarineClientDiagramServiceImpl() {
        //CDI proxy
    }

    @Inject
    public SubmarineClientDiagramServiceImpl(final ShapeManager shapeManager,
                                             final Caller<VFSService> vfsServiceCaller,
                                             final Caller<SubmarineDiagramService> submarineDiagramServiceCaller,
                                             final Promises promises) {
        this.shapeManager = shapeManager;
        this.vfsServiceCaller = vfsServiceCaller;
        this.submarineDiagramServiceCaller = submarineDiagramServiceCaller;
        this.promises = promises;
    }

    //DMNDiagramSubmarineWrapper requirements

    public void saveAsXml(final Path path,
                          final String xml,
                          final ServiceCallback<String> callback) {
        vfsServiceCaller.call((Path p) -> {
            callback.onSuccess(xml);
        }).write(path, xml);
    }

    public void loadAsXml(final Path path,
                          final ServiceCallback<String> callback) {
        vfsServiceCaller.call((String x) -> {
            callback.onSuccess(x);
        }).readAllString(path);
    }

    //Submarine requirements

    @Override
    public void transform(final String xml,
                          final ServiceCallback<SubmarineDiagram> callback) {
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
}
