/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.builder.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.backend.builder.cache.ProjectCache;

@Service
@ApplicationScoped
public class BuildServiceImpl implements BuildService {

//    private GuvnorM2Repository guvnorM2Repository;

    private ProjectCache projectCache;

    private String ERROR_LEVEL = "ERROR";

    public BuildServiceImpl() {
        //Empty constructor for Weld
    }

    @Inject
    public BuildServiceImpl(final ProjectCache projectCache) {
        this.projectCache = projectCache;
    }

    @Override
    public BuildResults build(final Project project) {
        return buildInternal(project);
    }

    private BuildResults buildAndDeployInternal(final Project project) {
        return projectCache.getOrCreateEntry(project).buildAndInstall();
    }

    private BuildResults buildInternal(final Project project) {
        return projectCache.getOrCreateEntry(project).build();
    }

    @Override
    public BuildResults buildAndDeploy(final Project project) {
        return buildAndDeployInternal(project);
    }

    @Override
    public BuildResults buildAndDeploy(final Project project,
                                       final DeploymentMode mode) {
        return buildAndDeployInternal(project);
    }

    @Override
    public BuildResults buildAndDeploy(final Project project,
                                       final boolean suppressHandlers) {
        return buildAndDeployInternal(project);
    }

    @Override
    public BuildResults buildAndDeploy(final Project project,
                                       final boolean suppressHandlers,
                                       final DeploymentMode mode) {
        return buildAndDeployInternal(project);
    }

    @Override
    public boolean isBuilt(final Project project) {
        return projectCache.getOrCreateEntry(project).isBuilt();
    }
}