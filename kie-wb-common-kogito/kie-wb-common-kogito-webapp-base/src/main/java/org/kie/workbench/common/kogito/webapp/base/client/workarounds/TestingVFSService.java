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
package org.kie.workbench.common.kogito.webapp.base.client.workarounds;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.kogito.webapp.base.client.callbacks.VFSServiceCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

/**
 * Class used to provide <i>virtual file system</i> access to <i>kogito testing editors</i>
 */
@ApplicationScoped
public class TestingVFSService {

    private PlaceManager placeManager;
    private Caller<VFSService> vfsServiceCaller;

    public static final String CONTENT_PARAMETER_NAME = "content";
    public static final String FILE_NAME_PARAMETER_NAME = "fileName";

    private TestingVFSService() {
        //CDI proxy
    }

    @Inject
    public TestingVFSService(final PlaceManager placeManager,
                         final Caller<VFSService> vfsServiceCaller) {
        this.placeManager = placeManager;
        this.vfsServiceCaller = vfsServiceCaller;
    }

    /**
     * Create a new file
     * @param editorId The <b>id</b> of the editor to open by the <code>PlaceRequest</code>
     * @param fileName
     */
    public void newFile(final String editorId, final String fileName) {
        final PlaceRequest placeRequest = new DefaultPlaceRequest(editorId);
        placeRequest.addParameter(FILE_NAME_PARAMETER_NAME, fileName);
        placeManager.goTo(placeRequest);
    }

    /**
     * Load a file
     * @param path the <code>Path</code> to the file
     * @param editorId The <b>id</b> of the editor to open by the <code>PlaceRequest</code>
     */
    public void openFile(final Path path, final String editorId) {
        vfsServiceCaller.call((String xml) -> {
            final PlaceRequest placeRequest = new DefaultPlaceRequest(editorId);
            placeRequest.addParameter(FILE_NAME_PARAMETER_NAME, path.getFileName());
            placeRequest.addParameter(CONTENT_PARAMETER_NAME, xml);
            placeManager.goTo(placeRequest);
        }).readAllString(path);
    }

    @SuppressWarnings("unchecked")
    public <E> void saveFile(final Path path,
                         final String xml,
                         final VFSServiceCallback<String, E> callback) {
        vfsServiceCaller.call((Path p) -> callback.onSuccess(xml)).write(path, xml);
    }

}
