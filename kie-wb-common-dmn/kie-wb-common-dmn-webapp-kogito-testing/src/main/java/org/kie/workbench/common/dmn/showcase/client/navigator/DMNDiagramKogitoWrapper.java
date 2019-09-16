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

package org.kie.workbench.common.dmn.showcase.client.navigator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.dmn.showcase.client.editor.DMNDiagramEditor;
import org.kie.workbench.common.kogito.client.editor.BaseKogitoEditor;
import org.kie.workbench.common.stunner.core.client.annotation.DiagramEditor;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.promise.Promises;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@ApplicationScoped
public class DMNDiagramKogitoWrapper {

    private static final PlaceRequest DIAGRAM_EDITOR = new DefaultPlaceRequest(DMNDiagramEditor.EDITOR_ID);

    private PlaceManager placeManager;
    private Caller<VFSService> vfsServiceCaller;
    private BaseKogitoEditor baseKogitoEditor;
    private Promises promises;

    public DMNDiagramKogitoWrapper() {
        //CDI proxy
    }

    @Inject
    public DMNDiagramKogitoWrapper(final PlaceManager placeManager,
                                   final Caller<VFSService> vfsServiceCaller,
                                   final @DiagramEditor BaseKogitoEditor baseKogitoEditor,
                                   final Promises promises) {
        this.placeManager = placeManager;
        this.vfsServiceCaller = vfsServiceCaller;
        this.baseKogitoEditor = baseKogitoEditor;
        this.promises = promises;
    }

    public void newFile() {
        placeManager.registerOnOpenCallback(DIAGRAM_EDITOR,
                                            () -> {
                                                baseKogitoEditor.setContent("");
                                                placeManager.unregisterOnOpenCallbacks(DIAGRAM_EDITOR);
                                            });

        placeManager.goTo(DIAGRAM_EDITOR);
    }

    public void openFile(final Path path) {
        placeManager.registerOnOpenCallback(DIAGRAM_EDITOR,
                                            () -> {
                                                vfsServiceCaller.call((String xml) -> {
                                                    baseKogitoEditor.setContent(xml);
                                                    placeManager.unregisterOnOpenCallbacks(DIAGRAM_EDITOR);
                                                }, (m, t) -> {
                                                    placeManager.unregisterOnOpenCallbacks(DIAGRAM_EDITOR);
                                                    return false;
                                                }).readAllString(path);
                                            });

        placeManager.goTo(DIAGRAM_EDITOR);
    }

    @SuppressWarnings("unchecked")
    public void saveFile(final Path path,
                         final ServiceCallback<String> callback) {
        baseKogitoEditor.getContent().then(xml -> {
            vfsServiceCaller.call((Path p) -> callback.onSuccess((String) xml)).write(path, (String) xml);
            return promises.resolve();
        });
    }
}
