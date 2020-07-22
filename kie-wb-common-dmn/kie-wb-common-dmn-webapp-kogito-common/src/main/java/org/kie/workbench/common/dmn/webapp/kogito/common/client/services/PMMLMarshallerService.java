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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.services;

import elemental2.promise.Promise;
import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.stunner.core.util.FileUtils;
import org.uberfire.client.promise.Promises;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Collections;

/**
 * Scope of this bean is to provide an entry point to the PMML client marshaller available thought enveloper
 */
@Dependent
public class PMMLMarshallerService {

    @Inject
    private Promises promises;

    public Promise<Integer> retrieveModelsNumber(final String pmmlFileContent) {
        /* Here, a JSInterop call through enveloper should be used, passing pmmlFileContent */
        return promises.resolve(0);
    }

    public Promise<PMMLDocumentMetadata> retrieveDocumentMetadata(final String pmmlFile, final String pmmlFileContent) {
        /* Here, a JSInterop call through enveloper should be used passing pmmlFileContent */
        String pmmlFileName = FileUtils.getFileName(pmmlFile);
        PMMLDocumentMetadata documentMetadata = new PMMLDocumentMetadata(pmmlFile,
                                                                         pmmlFileName,
                                                                         DMNImportTypes.PMML.getDefaultNamespace(),
                                                                         Collections.emptyList());
        return promises.resolve(documentMetadata);
    }
}
