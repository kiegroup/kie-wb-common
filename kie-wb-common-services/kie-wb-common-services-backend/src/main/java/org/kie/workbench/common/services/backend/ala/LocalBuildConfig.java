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

package org.kie.workbench.common.services.backend.ala;

import java.util.Map;

import org.guvnor.ala.config.BuildConfig;

public interface LocalBuildConfig extends BuildConfig {

    String BUILD_TYPE = "build-type";

    String RESOURCE = "resource";

    String BATCH_CHANGE = "batch-change:";

    String DEPLOYMENT_TYPE = "deployment-type";

    String SUPPRESS_HANDLERS = "suppress-handlers";

    enum BuildType {
        FULL_BUILD,
        INCREMENTAL_ADD_RESOURCE,
        INCREMENTAL_DELETE_RESOURCE,
        INCREMENTAL_UPDATE_RESOURCE,
        INCREMENTAL_BATCH_CHANGES,
        FULL_BUILD_AND_DEPLOY
    }

    enum DeploymentType {
        VALIDATED, FORCED
    }

    default String getBuildType( ) {
        return "${input." + BUILD_TYPE + "}";
    }

    default String getResource( ) {
        return "${input." + RESOURCE + "}";
    }

    default String getDeploymentType( ) {
        return "${input." + DEPLOYMENT_TYPE + "}";
    }

    default String getSuppressHandlers( ) {
        return "${input." + SUPPRESS_HANDLERS + "}";
    }

    Map< String, String > getResourceChanges( );
}