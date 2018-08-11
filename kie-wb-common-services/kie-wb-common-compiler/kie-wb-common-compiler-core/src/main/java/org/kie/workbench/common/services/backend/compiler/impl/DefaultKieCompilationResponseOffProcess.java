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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.drools.core.rule.KieModuleMetaInfo;
import org.kie.api.builder.KieModule;
import org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

public class DefaultKieCompilationResponseOffProcess implements KieCompilationResponse,
                                                                Serializable {

    private KieModuleMetaInfo kieModuleMetaInfo;
    private KieModule kieModule;
    private Map<String, byte[]> projectClassLoaderStore;
    private Set<String> eventsTypeClasses;
    private Boolean successful;
    private List<String> mavenOutput;
    private String workingDir;
    private String requestUUID;

    private List<String> projectDependencies;
    private List<URI> projectDependenciesAsURI = Collections.emptyList();
    private List<URL> projectDependenciesAsURL = Collections.emptyList();

    private List<String> targetContent;
    private List<URI> targetContentAsURI = Collections.emptyList();
    private List<URL> targetContentAsURL = Collections.emptyList();

    public DefaultKieCompilationResponseOffProcess(boolean successful, String requestUUID) {
        this.successful = successful;
        this.requestUUID = requestUUID;
    }

    public DefaultKieCompilationResponseOffProcess(KieCompilationResponse res) {
        this.kieModuleMetaInfo = res.getKieModuleMetaInfo().get();
        this.kieModule = res.getKieModule().get();
        this.projectClassLoaderStore = res.getProjectClassLoaderStore();
        this.eventsTypeClasses = res.getEventTypeClasses();
        this.successful = res.isSuccessful();
        this.mavenOutput = res.getMavenOutput();
        this.workingDir = res.getWorkingDir().get().toString();
        this.projectDependencies = res.getDependencies();
        this.targetContent = res.getTargetContent();
        this.requestUUID = ((DefaultKieCompilationResponse) res).getRequestUUID();
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
    public Map<String, byte[]> getProjectClassLoaderStore() {
        return new HashMap<>(projectClassLoaderStore);
    }

    @Override
    public Set<String> getEventTypeClasses() {
        return new HashSet<>(eventsTypeClasses);
    }

    @Override
    public Boolean isSuccessful() {
        return successful;
    }

    @Override
    public List<String> getMavenOutput() {
        return new ArrayList<>(mavenOutput);
    }

    @Override
    public Optional<Path> getWorkingDir() {
        return Optional.ofNullable(Paths.get(workingDir));
    }

    @Override
    public List<String> getDependencies() {
        return new ArrayList<>(projectDependencies);
    }

    public String getRequestUUID() {
        return requestUUID;
    }

    @Override
    public List<URI> getDependenciesAsURI() {
        if (projectDependenciesAsURI.isEmpty()) {
            projectDependenciesAsURI = getProjectDependenciesAsURIs();
        }
        return projectDependenciesAsURI;
    }

    private List<URI> getProjectDependenciesAsURIs() {
        if (projectDependencies != null && !projectDependencies.isEmpty()) {
            return CompilerClassloaderUtils.readAllDepsAsUris(projectDependencies);
        }
        return Collections.emptyList();
    }

    @Override
    public List<URL> getDependenciesAsURL() {
        if (projectDependenciesAsURL.isEmpty()) {
            projectDependenciesAsURL = getProjectDependenciesAsURLs();
        }
        return projectDependenciesAsURL;
    }

    private List<URL> getProjectDependenciesAsURLs() {
        if (projectDependencies != null && !projectDependencies.isEmpty()) {
            return CompilerClassloaderUtils.readAllDepsAsUrls(projectDependencies);
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> getTargetContent() {
        return targetContent;
    }

    @Override
    public List<URI> getTargetContentAsURI() {
        if (targetContentAsURI.isEmpty()) {
            targetContentAsURI = getRawAsURIs(targetContent);
        }
        return targetContentAsURI;
    }

    @Override
    public List<URL> getTargetContentAsURL() {
        if (targetContentAsURL.isEmpty()) {
            targetContentAsURL = getRawAsURLs(targetContent);
        }
        return targetContentAsURL;
    }

    private List<URL> getRawAsURLs(final List<String> targetContent) {
        if (targetContent != null && !targetContent.isEmpty()) {
            return CompilerClassloaderUtils.processScannedFilesAsURLs(targetContent);
        }
        return Collections.emptyList();
    }

    private List<URI> getRawAsURIs(final List<String> targetContent) {
        if (targetContent != null && !targetContent.isEmpty()) {
            return CompilerClassloaderUtils.processScannedFilesAsURIs(targetContent);
        }
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DefaultKieCompilationResponseOffProcess{");
        sb.append("requestUUID=").append(requestUUID);
        sb.append(", kieModuleMetaInfo=").append(kieModuleMetaInfo);
        sb.append(", kieModule=").append(kieModule);
        sb.append(", projectClassLoaderStore=").append(projectClassLoaderStore);
        sb.append(", eventsTypeClasses=").append(eventsTypeClasses);
        sb.append(", successful=").append(successful);
        sb.append(", mavenOutput=").append(mavenOutput);
        sb.append(", workingDir='").append(workingDir).append('\'');
        sb.append(", projectDependencies=").append(projectDependencies);
        sb.append(", projectDependenciesAsURI=").append(projectDependenciesAsURI);
        sb.append(", projectDependenciesAsURL=").append(projectDependenciesAsURL);
        sb.append(", targetContent=").append(targetContent);
        sb.append(", targetContentAsURI=").append(targetContentAsURI);
        sb.append(", targetContentAsURL=").append(targetContentAsURL);
        sb.append('}');
        return sb.toString();
    }
}
