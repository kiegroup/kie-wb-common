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

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.stunner.kogito.client.services.util.ResourceSearchPath;

@ApplicationScoped
public class ResourceSearchPathImpl implements ResourceSearchPath {

    private static final String SEPARATOR = "/";
    private static final String SRC_FOLDER = "src";
    private static final String MAIN_FOLDER = "main";
    private static final String TEST_FOLDER = "test";

    private String path;

    @Override
    public void init(final String diagramPath) {
        if(diagramPath != null) {
            Optional<String> optional = Optional.ofNullable(resolvePath(diagramPath, MAIN_FOLDER));
            this.path = optional.orElse(resolvePath(diagramPath, TEST_FOLDER));
        }
    }

    @Override
    public String getSearchExpression(final String searchPattern) {
        Optional<String> optional = getSearchPath();

        return optional.map(searchPath -> searchPath + SEPARATOR + searchPattern).orElse(searchPattern);
    }

    private String resolvePath(final String diagramPath, final String folder) {
        final String sourcesFolder = SRC_FOLDER + SEPARATOR + folder;

        if(diagramPath.contains(sourcesFolder)) {
            return diagramPath.substring(0, diagramPath.indexOf(sourcesFolder) + sourcesFolder.length());
        }

        return null;
    }

    private Optional<String> getSearchPath() {
        return Optional.ofNullable(path);
    }
}
