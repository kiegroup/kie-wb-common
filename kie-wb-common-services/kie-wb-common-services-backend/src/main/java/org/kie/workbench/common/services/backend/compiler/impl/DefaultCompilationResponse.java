/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.compiler.impl;

import java.util.List;
import java.util.Optional;

import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.uberfire.java.nio.file.Path;

/***
 * Default implementation of a basic (Non Kie) Compilation response,
 * it contains a boolean flag as a result of the build, an optional String error message,
 * and an optional List of String with the maven output
 */
public class DefaultCompilationResponse implements CompilationResponse {

    private Boolean successful;
    private List<String> mavenOutput;
    private Path workingDir;

    public DefaultCompilationResponse(Boolean successful) {
        this.successful = successful;
    }

    public DefaultCompilationResponse(Boolean successful,
                                      List<String> mavenOutput) {
        this.successful = successful;
        this.mavenOutput = mavenOutput;
    }

    public DefaultCompilationResponse(Boolean successful,
                                      List<String> mavenOutput,
                                      Path workingDir) {
        this.successful = successful;
        this.mavenOutput = mavenOutput;
        this.workingDir = workingDir;
    }

    public Boolean isSuccessful() {
        return successful;
    }

    public Optional<List<String>> getMavenOutput() {
        return Optional.ofNullable(mavenOutput);
    }

    public Optional<Path> getWorkingDir() {
        return Optional.ofNullable(workingDir);
    }
}
