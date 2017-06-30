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
import org.kie.workbench.common.services.backend.compiler.KieCompilationResponse;

public class DefaultKieCompilationResponse implements KieCompilationResponse {

    private Optional<KieModuleMetaInfo> kieModuleMetaInfo;
    private Optional<KieModule> kieModule;
    private Optional<List<URI>> projectDependencies;
    private DefaultCompilationResponse defaultResponse;

    public DefaultKieCompilationResponse(Boolean successful) {
        defaultResponse = new DefaultCompilationResponse(successful,
                                                         Optional.empty(),
                                                         Optional.empty());
        this.kieModuleMetaInfo = Optional.empty();
        this.kieModule = Optional.empty();
        this.projectDependencies = Optional.empty();
    }

    public DefaultKieCompilationResponse(Boolean successful,
                                         KieModuleMetaInfo kieModuleMetaInfo,
                                         KieModule kieModule,
                                         Optional<List<String>> mavenOutput,
                                         Optional<List<URI>> projectDependencies) {

        defaultResponse = new DefaultCompilationResponse(successful,
                                                         mavenOutput);
        this.kieModuleMetaInfo = Optional.of(kieModuleMetaInfo);
        this.kieModule = Optional.of(kieModule);
        this.projectDependencies = projectDependencies;
    }

    public DefaultKieCompilationResponse(Boolean successful,
                                         Optional<String> errorMessage,
                                         Optional<List<String>> mavenOutput) {
        defaultResponse = new DefaultCompilationResponse(successful,
                                                         errorMessage,
                                                         mavenOutput);
        this.kieModuleMetaInfo = Optional.empty();
        this.kieModule = Optional.empty();
        this.projectDependencies = Optional.empty();
    }

    public DefaultKieCompilationResponse(Boolean successful,
                                         Optional<List<String>> mavenOutput) {
        defaultResponse = new DefaultCompilationResponse(successful,
                                                         Optional.empty(),
                                                         mavenOutput);
        this.kieModuleMetaInfo = Optional.empty();
        this.kieModule = Optional.empty();
        this.projectDependencies = Optional.empty();
    }

    @Override
    public Optional<List<URI>> getProjectDependencies() {
        return projectDependencies;
    }

    @Override
    public Optional<KieModuleMetaInfo> getKieModuleMetaInfo() {
        return kieModuleMetaInfo;
    }

    @Override
    public Optional<KieModule> getKieModule() {
        return kieModule;
    }

    @Override
    public Boolean isSuccessful() {
        return defaultResponse.isSuccessful();
    }

    @Override
    public Optional<String> getErrorMessage() {
        return defaultResponse.getErrorMessage();
    }

    @Override
    public Optional<List<String>> getMavenOutput() {
        return defaultResponse.getMavenOutput();
    }
}
