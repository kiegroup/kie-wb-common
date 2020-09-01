/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.included;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.appformer.kogito.bridge.client.resource.interop.ResourceListOptions;
import org.kie.workbench.common.dmn.client.marshaller.included.DMNMarshallerImportsContentService;
import org.kie.workbench.common.kogito.webapp.base.client.workarounds.KogitoResourceContentService;

@Alternative
public class DMNMarshallerImportsContentServiceKogitoImpl implements DMNMarshallerImportsContentService {

    private final KogitoResourceContentService contentService;

    static final String DMN_FILES_PATTERN = "*.dmn";

    static final String PMML_FILES_PATTERN = "*.pmml";

    static final String MODEL_FILES_PATTERN = "*.{dmn,pmml}";

    @Inject
    public DMNMarshallerImportsContentServiceKogitoImpl(final KogitoResourceContentService contentService) {
        this.contentService = contentService;
    }

    @Override
    public Promise<String> loadFile(final String file) {
        return contentService.loadFile(file);
    }

    @Override
    public Promise<String[]> getModelsURIs() {
        return contentService.getFilteredItems(MODEL_FILES_PATTERN, ResourceListOptions.assetFolder());
    }

    @Override
    public Promise<String[]> getModelsDMNFilesURIs() {
        return contentService.getFilteredItems(DMN_FILES_PATTERN, ResourceListOptions.assetFolder());
    }

    @Override
    public Promise<String[]> getModelsPMMLFilesURIs() {
        return contentService.getFilteredItems(PMML_FILES_PATTERN, ResourceListOptions.assetFolder());
    }
}
