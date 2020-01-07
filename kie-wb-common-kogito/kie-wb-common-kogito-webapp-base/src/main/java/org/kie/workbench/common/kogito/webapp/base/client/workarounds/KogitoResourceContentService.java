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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.promise.IThenable;
import org.appformer.kogito.bridge.client.resource.ResourceContentService;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

/**
 * Class used to provide <i>resources</i> access to <i>kogito editors</i>
 */
@ApplicationScoped
public class KogitoResourceContentService {

    public static final String CONTENT_PARAMETER_NAME = "content";
    public static final String FILE_NAME_PARAMETER_NAME = "fileName";
    private PlaceManager placeManager;
    private ResourceContentService resourceContentService;

    private KogitoResourceContentService() {
        //CDI proxy
    }

    @Inject
    public KogitoResourceContentService(final PlaceManager placeManager,
                                        final ResourceContentService resourceContentService) {
        this.placeManager = placeManager;
        this.resourceContentService = resourceContentService;
    }

    /**
     * Open a file at given <code>Path</code> inside an <b>editor</b>
     * @param fileUri the <b>uri</code> to the file
     * @param editorId The <b>id</b> of the editor to open by the <code>PlaceRequest</code>
     */
    public void openFile(final String fileUri,
                         final String editorId) {
        resourceContentService.get(fileUri).then((IThenable.ThenOnFulfilledCallbackFn<String, Void>) fileContent -> {
            final PlaceRequest placeRequest = new DefaultPlaceRequest(editorId);
            placeRequest.addParameter(FILE_NAME_PARAMETER_NAME, fileUri);
            placeRequest.addParameter(CONTENT_PARAMETER_NAME, fileContent);
            placeManager.goTo(placeRequest);
            return null;
        });
    }

    /**
     * Load the file at given <code>Path</code> and returns content inside a callback
     * @param fileUri
     * @param callback
     */
    public void loadFile(final String fileUri,
                         final RemoteCallback<String> callback,
                         final ErrorCallback<Object> errorCallback) {
        resourceContentService.get(fileUri).then((IThenable.ThenOnFulfilledCallbackFn<String, Void>) fileContent -> {
                                                     callback.callback(fileContent);
                                                     return null;
                                                 }/*,
                                                 (IThenable.ThenOnRejectedCallbackFn<Void>) errorObject -> {
                                                     errorCallback.error(errorObject, new Throwable("Failed to load file " + fileUri));
                                                     return null;
                                                 }*/);
    }

    /**
     * Get the <code>List&lt;Path&gt;</code> contained in the given <b>root</b>
     * @param rootUri
     * @param callback
     * @param errorCallback
     */
    public void getItemsByPath(final String rootUri,
                               final RemoteCallback<List<String>> callback,
                               final ErrorCallback<Object> errorCallback) {
        resourceContentService.list(rootUri).then(fileList -> {
                                                      callback.callback(Arrays.asList(fileList));
                                                      return null;
                                                  }/*,
                                                  (IThenable.ThenOnRejectedCallbackFn<Void>) errorObject -> {
                                                      errorCallback.error(errorObject, new Throwable("Failed to load files at " + rootUri));
                                                      return null;
                                                  }*/);
    }

    /**
     * Get <b>filtered</b> <code>List&lt;Path&gt;</code>  contained in the given <b>root</b>
     * @param rootUri
     * @param fileSuffix
     * @param callback
     * @param errorCallback
     * @param <T>
     */
    public void getItemsByPath(final String rootUri,
                               final String fileSuffix,
                               final RemoteCallback<List<String>> callback,
                               final ErrorCallback<Object> errorCallback) {
        String filteredSuffix = fileSuffix.startsWith(".") ? fileSuffix : "." + fileSuffix;
        resourceContentService.list(rootUri).then(fileList -> {
                                                      List<String> toReturn = Arrays.asList(fileList).stream().filter(fileName -> fileName.endsWith(filteredSuffix))
                                                              .collect(Collectors.toList());
                                                      callback.callback(toReturn);
                                                      return null;
                                                  }/*,
                                                  (IThenable.ThenOnRejectedCallbackFn<Void>) errorObject -> {
                                                      errorCallback.error(errorObject, new Throwable("Failed to load files at " + rootUri));
                                                      return null;
                                                  }*/);
    }
}
