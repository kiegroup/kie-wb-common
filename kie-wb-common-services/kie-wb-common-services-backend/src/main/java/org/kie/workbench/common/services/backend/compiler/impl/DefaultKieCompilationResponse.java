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

import java.net.URI;
import java.net.URL;
import java.util.*;

import org.drools.core.rule.KieModuleMetaInfo;
import org.kie.api.builder.KieModule;
import org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.uberfire.java.nio.file.Path;

/***
 * Default implementation of a Kie Compilation response,
 * it contains a boolean flag as a result of the build, an optional String error message,
 * and a  List of String with the maven output
 */
public class DefaultKieCompilationResponse implements KieCompilationResponse {

    private KieModuleMetaInfo kieModuleMetaInfo;
    private KieModule kieModule;
    private Map<String, byte[]> projectClassLoaderStore;
    private Set<String> eventsTypeClasses;
    private List<String> projectDependenciesRaw;
    private List<URI> projectDependenciesAsURI;
    private List<URL> projectDependenciesAsURL;
    private DefaultCompilationResponse defaultResponse;
    private Path workingDir;

    public DefaultKieCompilationResponse(Boolean successful) {
        this(successful,
             null,
             null,
             null,
             null);
    }

    public DefaultKieCompilationResponse(Boolean successful, List<String> mavenOutput) {
        this(successful,
             null,
             null,
                null,
             mavenOutput,
             null,
             null,
             null);
    }

    public DefaultKieCompilationResponse(Boolean successful,
                                         List<String> mavenOutput,
                                         Path workingDir) {
        defaultResponse = new DefaultCompilationResponse(successful,
                                                         mavenOutput);
        this.kieModuleMetaInfo = null;
        this.workingDir = workingDir;
    }

    public DefaultKieCompilationResponse(Boolean successful,
                                         KieModuleMetaInfo kieModuleMetaInfo,
                                         KieModule kieModule,
                                         Map<String,byte[]> projectClassLoaderStore,
                                         List<String> mavenOutput,
                                         List<String> projectDependenciesRaw,
                                         Path workingDir,
                                         Set<String> eventTypesClasses) {

        defaultResponse = new DefaultCompilationResponse(successful,
                                                         mavenOutput);
        this.kieModuleMetaInfo = kieModuleMetaInfo;
        this.kieModule = kieModule;
        this.projectClassLoaderStore = projectClassLoaderStore;
        this.projectDependenciesRaw = projectDependenciesRaw;
        this.workingDir = workingDir;
        this.eventsTypeClasses = eventTypesClasses;
    }

    public DefaultKieCompilationResponse(Boolean successful,
                                         KieModuleMetaInfo kieModuleMetaInfo,
                                         KieModule kieModule,
                                         Map<String, byte[]> projectClassloaderStore,
                                         List<String> projectDependenciesRaw,
                                         Path workingDir) {

        defaultResponse = new DefaultCompilationResponse(successful);
        this.kieModuleMetaInfo = kieModuleMetaInfo;
        this.projectClassLoaderStore = projectClassloaderStore;
        this.kieModule = kieModule;
        this.projectDependenciesRaw = projectDependenciesRaw;
        this.workingDir = workingDir;
    }

    public DefaultKieCompilationResponse(Boolean successful,
                                         KieModuleMetaInfo kieModuleMetaInfo,
                                         KieModule kieModule,
                                         Map<String, byte[]> projectClassloaderStore,
                                         Path workingDir) {

        defaultResponse = new DefaultCompilationResponse(successful);
        this.kieModuleMetaInfo = kieModuleMetaInfo;
        this.kieModule = kieModule;
        this.projectClassLoaderStore = projectClassloaderStore;
        this.workingDir = workingDir;
    }

    @Override
    public Optional<List<String>> getProjectDependenciesRaw() {
        return Optional.ofNullable(projectDependenciesRaw);
    }

    @Override
    public Optional<List<URI>> getProjectDependenciesAsURI() {
        return Optional.ofNullable(getRawAsURIs());
    }

    public Optional<List<URL>> getProjectDependenciesAsURL() {
        return Optional.ofNullable(getRawAsURLs());
    }

    @Override
    public Optional<KieModuleMetaInfo> getKieModuleMetaInfo() {
        return Optional.ofNullable(kieModuleMetaInfo);
    }

    @Override
    public Optional<KieModule> getKieModule() {
        return Optional.ofNullable(kieModule);
    }

    @Override
    public Boolean isSuccessful() {
        return defaultResponse.isSuccessful();
    }

    @Override
    public Optional<List<String>> getMavenOutput() {
        return defaultResponse.getMavenOutput();
    }

    @Override
    public Optional<Path> getWorkingDir() {
        return Optional.ofNullable(workingDir);
    }

    @Override
    public Optional<Map<String, byte[]>> getProjectClassLoaderStore() {
        return Optional.ofNullable(projectClassLoaderStore);
    }

    @Override
    public Optional<Set<String>> getEventTypeClasses() {
        return Optional.ofNullable(eventsTypeClasses);
    }

    private List<URL> getRawAsURLs() {
        if (projectDependenciesAsURL != null) {
            return projectDependenciesAsURL;
        }
        if (projectDependenciesAsURL == null && projectDependenciesRaw != null) {
            projectDependenciesAsURL = CompilerClassloaderUtils.processScannedFilesAsURLs(projectDependenciesRaw);
            return projectDependenciesAsURL;
        }
        return Collections.EMPTY_LIST;
    }

    private List<URI> getRawAsURIs() {
        if (projectDependenciesAsURI != null) {
            return projectDependenciesAsURI;
        }
        if (projectDependenciesAsURI == null && projectDependenciesRaw != null) {
            projectDependenciesAsURI = CompilerClassloaderUtils.processScannedFilesAsURIs(projectDependenciesRaw);
            return projectDependenciesAsURI;
        }
        return Collections.EMPTY_LIST;
    }
}
