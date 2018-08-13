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

public class DefaultKieCompilationResponseOffProcess implements Serializable {

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
        this.successful = res.isSuccessful();
        this.requestUUID = ((DefaultKieCompilationResponse) res).getRequestUUID();
        if(res.getProjectClassLoaderStore() != null) {
            this.projectClassLoaderStore = res.getProjectClassLoaderStore();
        }else{
            this.projectClassLoaderStore = Collections.emptyMap();
        }
        if(res.getEventTypeClasses() != null) {
            this.eventsTypeClasses = res.getEventTypeClasses();
        }else{
            this.eventsTypeClasses = Collections.emptySet();
        }
        if(res.getMavenOutput() != null) {
            this.mavenOutput = res.getMavenOutput();
        }else{
            this.mavenOutput = Collections.emptyList();
        }
        if(res.getWorkingDir().isPresent()) {
            this.workingDir = res.getWorkingDir().get().toString();
        }else {
            this.workingDir = "";
        }
        if(res.getDependencies()!= null) {
            this.projectDependencies = res.getDependencies();
        }else {
            this.projectDependencies = Collections.emptyList();
        }
        if(res.getTargetContent()!= null) {
            this.targetContent = res.getTargetContent();
        }else{
            this.targetContent = Collections.emptyList();
        }
    }

    public Optional<KieModuleMetaInfo> getKieModuleMetaInfo() {
        return Optional.ofNullable(kieModuleMetaInfo);
    }

    public Optional<KieModule> getKieModule() {
        return Optional.ofNullable(kieModule);
    }

    public Map<String, byte[]> getProjectClassLoaderStore() {
        return new HashMap<>(projectClassLoaderStore);
    }

    public Set<String> getEventTypeClasses() {
        return new HashSet<>(eventsTypeClasses);
    }

    public Boolean isSuccessful() {
        return successful;
    }

    public List<String> getMavenOutput() {
        if(mavenOutput == null){
            return Collections.emptyList();
        }
        return new ArrayList<>(mavenOutput);
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public List<String> getDependencies() {
        if(projectDependencies == null){
            return Collections.emptyList();
        }
        return new ArrayList<>(projectDependencies);
    }

    public String getRequestUUID() {
        return requestUUID;
    }

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

    public List<String> getTargetContent() {
        return targetContent;
    }

    public List<URI> getTargetContentAsURI() {
        if (targetContentAsURI.isEmpty()) {
            targetContentAsURI = getRawAsURIs(targetContent);
        }
        return targetContentAsURI;
    }

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
