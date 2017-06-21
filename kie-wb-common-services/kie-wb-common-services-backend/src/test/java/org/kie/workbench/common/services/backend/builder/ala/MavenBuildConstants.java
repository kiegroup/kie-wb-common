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

package org.kie.workbench.common.services.backend.builder.ala;

import org.guvnor.common.services.project.model.GAV;

public interface MavenBuildConstants {

    String BUILD_SUCCESS = "BUILD SUCCESS";

    String TEST_PROJECT_GROUP_ID = "org.kie.workbench.common.services.builder.tests";

    String TEST_PROJECT_ARTIFACT_ID = "maven-build-test";

    String TEST_PROJECT_VERSION = "1.0.0";

    GAV TEST_PROJECT_GAV = new GAV( TEST_PROJECT_GROUP_ID, TEST_PROJECT_ARTIFACT_ID, TEST_PROJECT_VERSION );

    String TEST_PROJECT_JAR = "maven-build-test-1.0.0.jar";

}