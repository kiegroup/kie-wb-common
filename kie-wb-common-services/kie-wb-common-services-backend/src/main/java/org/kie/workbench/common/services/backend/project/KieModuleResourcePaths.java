/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.project;

public interface KieModuleResourcePaths {

    String KMODULE_PATH = "src/main/resources/META-INF/kmodule.xml";

    String PROJECT_IMPORTS_PATH = "project.imports";

    String PROJECT_REPOSITORIES_PATH = "project.repositories";

    String PACKAGE_NAMES_ALLOW_LIST = "package-names-allow-list";
}
