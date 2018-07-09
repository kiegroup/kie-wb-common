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
public class DefaultCompilationResponse implements CompilationResponse, Serializable {

    private Boolean successful;
    private List<String> mavenOutput;
    private Path workingDir;

    private List EMPTY_LIST = Collections.EMPTY_LIST;

    private List<String> projectDependencies = EMPTY_LIST;
    private List<URI> projectDependenciesAsURI = EMPTY_LIST;
    private List<URL> projectDependenciesAsURL = EMPTY_LIST;

    private List<String> targetContent = EMPTY_LIST;
    private List<URI> targetContentAsURI = EMPTY_LIST;
    private List<URL> targetContentAsURL = EMPTY_LIST;

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

    public DefaultCompilationResponse(final Boolean successful,
                                      final List<String> mavenOutput,
                                      final Path workingDir,
                                      final List<String> projectDependencies) {
        this.successful = successful;
        this.mavenOutput = mavenOutput;
        this.workingDir = workingDir;
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
        if (targetContentAsURL.equals(EMPTY_LIST)) {
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
        if (projectDependenciesAsURI.equals(EMPTY_LIST)) {
            projectDependenciesAsURI = getProjectDependenciesAsURIs();
        }
        return projectDependenciesAsURI;
    }

    private List<URI> getProjectDependenciesAsURIs() {
        if (!projectDependencies.equals(EMPTY_LIST) && !projectDependencies.isEmpty()) {
            return CompilerClassloaderUtils.readAllDepsAsUris(projectDependencies);
        }
        return EMPTY_LIST;
    }

    @Override
    public List<URL> getDependenciesAsURL() {
        if (projectDependenciesAsURL.equals(EMPTY_LIST)) {
            projectDependenciesAsURL = getProjectDependenciesAsURLs();
        }
        return projectDependenciesAsURL;
    }

    private List<URL> getProjectDependenciesAsURLs() {
        if(projectDependencies != null && !projectDependencies.isEmpty()){
            return CompilerClassloaderUtils.readAllDepsAsUrls(projectDependencies);
        }
        return EMPTY_LIST;
    }

    private List<URL> getRawAsURLs(final List<String> targetContent) {
        if(targetContent != null && !targetContent.isEmpty()){
            return CompilerClassloaderUtils.processScannedFilesAsURLs(targetContent);
        }
        return EMPTY_LIST;
    }



    private List<URI> getRawAsURIs(final List<String> targetContent) {
        if (targetContent != null && !targetContent.isEmpty()) {
            return CompilerClassloaderUtils.processScannedFilesAsURIs(targetContent);
        }
        return EMPTY_LIST;
    }
}