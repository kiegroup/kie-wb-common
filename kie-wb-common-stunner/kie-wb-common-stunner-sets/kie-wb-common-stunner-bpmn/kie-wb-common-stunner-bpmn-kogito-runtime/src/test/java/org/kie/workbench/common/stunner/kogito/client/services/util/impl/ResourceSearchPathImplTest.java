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

package org.kie.workbench.common.stunner.kogito.client.services.util.impl;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class ResourceSearchPathImplTest {

    private static final String MAIN_FOLDER = "onboarding-example/onboarding/src/main";
    private static final String MAIN_RESOURCES = MAIN_FOLDER + "/resources/org/kie/kogito/examples/onboarding/onboarding.bpmn";

    private static final String TEST_FOLDER = "onboarding-example/onboarding/src/test";
    private static final String TEST_RESOURCES = TEST_FOLDER + "/resources/org/kie/kogito/examples/onboarding/onboarding.bpmn";

    private static final String NON_MAVEN = "onboarding-example/onboarding/resources/org/kie/kogito/examples/onboarding/onboarding.bpmn";

    private static final String SEARCH_PATTERN = "**.wid";

    private ResourceSearchPathImpl resource;

    @Before
    public void init() {
        resource = new ResourceSearchPathImpl();
    }

    @Test
    public void testGetSearchExpressionForSrcMain() {
        resource.init(MAIN_RESOURCES);

        Assertions.assertThat(resource.getSearchExpression(SEARCH_PATTERN))
                .isNotNull()
                .contains(MAIN_FOLDER + "/" + SEARCH_PATTERN);
    }

    @Test
    public void testGetSearchExpressionForSrcTest() {
        resource.init(TEST_RESOURCES);

        Assertions.assertThat(resource.getSearchExpression(SEARCH_PATTERN))
                .isNotNull()
                .contains(TEST_FOLDER + "/" + SEARCH_PATTERN);
    }

    @Test
    public void testGetSearchExpressionForNonMaven() {
        resource.init(NON_MAVEN);

        Assertions.assertThat(resource.getSearchExpression(SEARCH_PATTERN))
                .isNotNull()
                .contains(SEARCH_PATTERN);
    }

    @Test
    public void testGetSearchExpressionForEmptyPath() {
        resource.init(null);

        Assertions.assertThat(resource.getSearchExpression(SEARCH_PATTERN))
                .isNotNull()
                .contains(SEARCH_PATTERN);
    }
}
