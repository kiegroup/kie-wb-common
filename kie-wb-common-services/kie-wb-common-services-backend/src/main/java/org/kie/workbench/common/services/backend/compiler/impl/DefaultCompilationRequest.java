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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.external339.AFCliRequest;
import org.uberfire.java.nio.file.Path;

/***
 * Implementation of CompilationRequest, holds the information for the AFMavenCli
 */
public class DefaultCompilationRequest implements CompilationRequest {

    private AFCliRequest req;
    private WorkspaceCompilationInfo info;
    private String requestUUID;
    private String[] originalArgs;
    private String mavenRepo;
    private Boolean logRequested;
    private Boolean skipPrjDependenciesCreationList;

    /***
     * @param mavenRepo a string representation of the Path
     * @param info
     * @param args param for maven, can be used {@link org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs}
     * @param logRequested if is true the output of the build will be provided as a List<String>
     */
    public DefaultCompilationRequest(String mavenRepo,
                                     WorkspaceCompilationInfo info,
                                     String[] args,
                                     Boolean logRequested) {
        this.mavenRepo = mavenRepo;
        this.info = info;
        this.skipPrjDependenciesCreationList = Boolean.TRUE;
        this.requestUUID = UUID.randomUUID().toString();

        this.originalArgs = args;
        this.logRequested = logRequested;
        String[] internalArgs = getInternalArgs(args,
                                                logRequested);
        this.req = new AFCliRequest(this.info.getPrjPath().toAbsolutePath().toString(),
                                    internalArgs,
                                    new HashMap<>(),
                                    this.requestUUID,
                                    logRequested);
    }

    /***
     * @param mavenRepo a string representation of the Path
     * @param info
     * @param args param for maven, can be used {@link org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs}
     * @param logRequested if is true the output of the build will be provided as a List<String>
     * @param skipPrjDependenciesCreationList if false a List with all dependencies of the project will be available in the response
     */
    public DefaultCompilationRequest(String mavenRepo,
                                     WorkspaceCompilationInfo info,
                                     String[] args,
                                     Boolean logRequested,
                                     Boolean skipPrjDependenciesCreationList) {
        this.mavenRepo = mavenRepo;
        this.info = info;
        this.skipPrjDependenciesCreationList = skipPrjDependenciesCreationList;
        this.requestUUID = UUID.randomUUID().toString();

        this.originalArgs = args;
        this.logRequested = logRequested;
        String[] internalArgs = getInternalArgs(args,
                                                logRequested);
        this.req = new AFCliRequest(this.info.getPrjPath().toAbsolutePath().toString(),
                                    internalArgs,
                                    new HashMap<>(),
                                    this.requestUUID,
                                    logRequested);
    }

    private String[] getInternalArgs(String[] args,
                                     Boolean logRequested) {
        String[] internalArgs;
        StringBuilder sbCompilationID = new StringBuilder().append("-Dcompilation.ID=").append(requestUUID);

        if (logRequested) {
            StringBuilder sbLogID = new StringBuilder().append("-l ").append("log").append(".").append(requestUUID).append(".log");
            internalArgs = Arrays.copyOf(args,
                                         args.length + 2);
            internalArgs[args.length + 1] = sbLogID.toString();
        } else {
            internalArgs = Arrays.copyOf(args,
                                         args.length + 1);
        }

        internalArgs[args.length] = sbCompilationID.toString();
        return internalArgs;
    }

    @Override
    public String getRequestUUID() {
        return requestUUID;
    }

    @Override
    public Boolean skipAutoSourceUpdate() {
        return true;
    }

    @Override
    public WorkspaceCompilationInfo getInfo() {
        return info;
    }

    @Override
    public Optional<Path> getPomFile() {
        return info.getEnhancedMainPomFile();
    }

    public AFCliRequest getReq() {
        return req;
    }

    @Override
    public AFCliRequest getKieCliRequest() {
        return req;
    }

    @Override
    public String getMavenRepo() {
        return mavenRepo;
    }

    @Override
    public String[] getOriginalArgs() {
        return originalArgs;
    }

    @Override
    public Map<String, Object> getMap() {
        return req.getMap();
    }

    @Override
    public Boolean getLogRequested() {
        return logRequested;
    }

    @Override
    public Boolean skipPrjDependenciesCreationList() {
        return skipPrjDependenciesCreationList;
    }
}