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

import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils;
import org.uberfire.java.nio.file.Path;

/***
 * Default implementation of a basic (Non Kie) Compilation response,
 * it contains a boolean flag as a result of the build, an optional String error message,
 * and an optional List of String with the maven output
 */
public class DefaultCompilationResponse implements CompilationResponse,
                                                   Serializable {

    private Boolean successful;
    private List<String> mavenOutput;
    private Path workingDir;

    private List<String> projectDependencies;
    private List<URI> projectDependenciesAsURI;
    private List<URL> projectDependenciesAsURL;

    private List<String> targetContent;
    private List<URI> targetContentAsURI;
    private List<URL> targetContentAsURL;

    public DefaultCompilationResponse(Boolean successful) {
        this.successful = successful;
    }

    public DefaultCompilationResponse(final Boolean successful,
                                      final List<String> mavenOutput) {
        this.successful = successful;
        this.mavenOutput = mavenOutput;
    }

    public DefaultCompilationResponse(final Boolean successful,
                                      final List<String> mavenOutput,
                                      final Path workingDir) {
        this.successful = successful;
        this.mavenOutput = mavenOutput;
        this.workingDir = workingDir;
    }

    public DefaultCompilationResponse(final Boolean successful,
                                      final List<String> mavenOutput,
                                      final Path workingDir,
                                      final List<String> targetContent,
                                      final List<String> projectDependencies) {
        this.successful = successful;
        this.mavenOutput = mavenOutput;
        this.workingDir = workingDir;
        this.targetContent = targetContent;
        this.projectDependencies = projectDependencies;
    }

    public Boolean isSuccessful() {
        return successful;
    }

    public List<String> getMavenOutput() {
        return mavenOutput;
    }

    public Optional<Path> getWorkingDir() {
        return Optional.ofNullable(workingDir);
    }

    @Override
    public List<String> getTargetContent() {
        return targetContent;
    }

    @Override
    public List<URI> getTargetContentAsURI() {
        if (targetContentAsURI == null) {
            targetContentAsURI = getRawAsURIs(targetContent);
        }
        return targetContentAsURI;
    }

    @Override
    public List<URL> getTargetContentAsURL() {
        if (targetContentAsURL == null) {
            targetContentAsURL = getRawAsURLs(targetContent);
        }
        return targetContentAsURL;
    }

    @Override
    public List<String> getDependencies() {
        return projectDependencies;
    }

    @Override
    public List<URI> getDependenciesAsURI() {
        if (projectDependenciesAsURI == null) {
            projectDependenciesAsURI = getRawAsURIs(projectDependencies);
        }
        return projectDependenciesAsURI;
    }

    @Override
    public List<URL> getDependenciesAsURL() {
        if (projectDependenciesAsURL == null) {
            projectDependenciesAsURL = getRawAsURLs(projectDependencies);
        }
        return projectDependenciesAsURL;
    }

    private List<URL> getRawAsURLs(final List<String> content) {
        if (content != null && !content.isEmpty()) {
            return CompilerClassloaderUtils.processScannedFilesAsURLs(content);
        }
        return Collections.emptyList();
    }

    private List<URI> getRawAsURIs(final List<String> content) {
        if (content != null && !content.isEmpty()) {
            return CompilerClassloaderUtils.processScannedFilesAsURIs(content);
        }
        return Collections.emptyList();
    }
}
