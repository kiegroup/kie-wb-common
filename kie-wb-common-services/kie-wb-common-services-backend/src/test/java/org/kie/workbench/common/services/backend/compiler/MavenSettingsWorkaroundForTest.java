/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.compiler;

import java.io.File;

import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;

/**
 * This is to workaround to resolve the dependencies needed for running the Maven related compiler tests.
 * The issue arises when using Jenkins instances that need to use different Maven repos. In general these ( main build and tests )
 * should pull all dependencies from the same repo.
 * In the past we tried MavenCLIArgs.OFFLINE which in theory should pull all dependencies before starting the tests that could then run in offline mode.
 * but it didn't work because it would fail to resolve all the necessary dependencies for running the kie-takari-plugin.
 * This issue forces us to have to specify the Maven repo in the src/test/settings.xml file.
 */
public final class MavenSettingsWorkaroundForTest {

    public static final String MAVEN_SETTINGS_OPTION = MavenCLIArgs.ALTERNATE_USER_SETTINGS + new File("src/test/settings.xml").getAbsolutePath();

}
