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
import java.util.List;
import java.util.Optional;

import org.drools.core.rule.KieModuleMetaInfo;
import org.kie.api.builder.KieModule;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;

public class DefaultCompilationResponse implements CompilationResponse {

    private Boolean successful;
    private Optional<String> errorMessage;
    private Optional<KieModuleMetaInfo> kieModuleMetaInfo;
    private Optional<KieModule> kieModule;
    private Optional<List<String>> mavenOutput;
    private Optional<List<URI>> projectDependencies;

    public DefaultCompilationResponse(Boolean successful) {
        this.successful = successful;
        this.errorMessage = Optional.empty();
        this.kieModuleMetaInfo = Optional.empty();
        this.mavenOutput = Optional.empty();
        this.projectDependencies = Optional.empty();
    }

    public DefaultCompilationResponse(Boolean successful,
                                      Optional<List<String>> mavenOutput) {
        this.successful = successful;
        this.errorMessage = Optional.empty();
        this.kieModuleMetaInfo = Optional.empty();
        this.mavenOutput = mavenOutput;
        this.projectDependencies = Optional.empty();
        this.projectDependencies = Optional.empty();
    }

    public DefaultCompilationResponse(Boolean successful,
                                      Optional<String> errorMessage,
                                      Optional<List<String>> mavenOutput) {
        this.successful = successful;
        this.errorMessage = errorMessage;
        this.kieModuleMetaInfo = Optional.empty();
        this.mavenOutput = mavenOutput;
        this.projectDependencies = Optional.empty();
    }

    public DefaultCompilationResponse(Boolean successful,
                                      KieModuleMetaInfo kieModuleMetaInfo,
                                      KieModule kieModule,
                                      Optional<List<String>> mavenOutput) {
        this.successful = successful;
        this.errorMessage = Optional.empty();
        this.kieModuleMetaInfo = Optional.of(kieModuleMetaInfo);
        this.kieModule = Optional.of(kieModule);
        this.mavenOutput = mavenOutput;
        this.projectDependencies = Optional.empty();
    }

    public DefaultCompilationResponse(Boolean successful,
                                      KieModuleMetaInfo kieModuleMetaInfo,
                                      KieModule kieModule,
                                      Optional<List<String>> mavenOutput,
                                      Optional<List<URI>> projectDependencies) {
        this.successful = successful;
        this.errorMessage = Optional.empty();
        this.kieModuleMetaInfo = Optional.of(kieModuleMetaInfo);
        this.kieModule = Optional.of(kieModule);
        this.mavenOutput = mavenOutput;
        this.projectDependencies = projectDependencies;
    }

    public Boolean isSuccessful() {
        return successful;
    }

    public Optional<String> getErrorMessage() {
        return errorMessage;
    }

    public Optional<KieModuleMetaInfo> getKieModuleMetaInfo() {
        return kieModuleMetaInfo;
    }

    public Optional<KieModule> getKieModule() {
        return kieModule;
    }

    public Optional<List<String>> getMavenOutput() {
        return mavenOutput;
    }

    public Optional<List<URI>> getProjectDependencies() {
        return projectDependencies;
    }
}
