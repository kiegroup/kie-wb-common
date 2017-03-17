/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.shared.builder.service;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.bus.server.annotations.Remote;

/**
 * Service for invoking project purely based on the guvnor ala maven based build.
 */
@Remote
public interface MavenBuildService {

    /**
     * Builds a project by invoking a maven build through a pipeline.
     * @param project the project to build.
     * @param deploy true if the resulting maven artifact should be deployed in current WB m2 repository.
     *
     * @return the results of the build operation.
     */
    BuildResults build( Project project, boolean deploy );

}