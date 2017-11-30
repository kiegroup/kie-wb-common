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

import java.util.Collection;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.eclipse.jgit.api.Git;
import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.kie.workbench.common.services.backend.builder.af.impl.DefaultKieAFBuilder;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.guvnor.common.services.backend.cache.BuilderCache;
import org.guvnor.common.services.backend.cache.GitCache;
import org.kie.workbench.common.services.backend.compiler.impl.utils.JGitUtils;
import org.kie.workbench.common.services.backend.compiler.impl.utils.KieAFBuilderUtil;
import org.kie.workbench.common.services.backend.compiler.impl.utils.MavenOutputConverter;
import org.kie.workbench.common.services.backend.compiler.impl.utils.PathConverter;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.workbench.events.ResourceChange;

@Service
@ApplicationScoped
public class BuildServiceImpl implements BuildService {

    private KieProjectService projectService;

    private GuvnorM2Repository guvnorM2Repository;

    private Instance<User> identity;

    private GitCache gitCache;

    private BuilderCache builderCache;

    private String ERROR_LEVEL = "ERROR";

    public BuildServiceImpl() {
        //Empty constructor for Weld
    }

    @Inject
    public BuildServiceImpl(final KieProjectService projectService,
                            final GuvnorM2Repository guvnorM2Repository,
                            final Instance<User> identity,
                            final GitCache gitCache,
                            final BuilderCache builderCache) {
        this.projectService = projectService;
        this.guvnorM2Repository = guvnorM2Repository;
        this.identity = identity;
        this.gitCache = gitCache;
        this.builderCache = builderCache;
    }

    @Override
    public BuildResults build(final Project project) {
        return buildInternal(project);
    }

    private BuildResults buildAndDeployInternal(final Project project) {
        KieAFBuilder kieAfBuilder = KieAFBuilderUtil.getKieAFBuilder(project.getRootPath().toURI().toString(), PathConverter.getNioPath(project),
                                                                     gitCache, builderCache,
                                                                     guvnorM2Repository, KieAFBuilderUtil.getIdentifier(identity));
        gitPullAndRebase(project);
        KieCompilationResponse res = kieAfBuilder.buildAndInstall(((DefaultKieAFBuilder) kieAfBuilder).getInfo().getPrjPath().toString(), guvnorM2Repository.getM2RepositoryRootDir(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME));
        return MavenOutputConverter.convertIntoBuildResults(res.getMavenOutput().get());
    }

    private BuildResults buildInternal(final Project project) {

        KieAFBuilder kieAfBuilder = KieAFBuilderUtil.getKieAFBuilder(project.getRootPath().toURI().toString(), PathConverter.getNioPath(project),
                                                                     gitCache, builderCache, guvnorM2Repository,
                                                                     KieAFBuilderUtil.getIdentifier(identity));
        if (kieAfBuilder != null) {
            gitPullAndRebase(project);
            KieCompilationResponse res = kieAfBuilder.build(Boolean.TRUE,
                                                            Boolean.FALSE);
            BuildResults br =
             MavenOutputConverter.convertIntoBuildResults(res.getMavenOutput().get(),
                    ERROR_LEVEL,
                    ((DefaultKieAFBuilder)kieAfBuilder).getGITURI(),
                    ((DefaultKieAFBuilder) kieAfBuilder).getInfo().getPrjPath().getParent().toString());

            return br;
        } else {
            BuildResults buildRs = new BuildResults();
            BuildMessage msg = new BuildMessage();
            msg.setText("[ERROR] Isn't possible build the project " + project.getRootPath().toURI().toString() + " because isn't a Git FS project");
            buildRs.addBuildMessage(msg);
            return buildRs;
        }
    }

    private IncrementalBuildResults buildIncrementallyInternal(final Project project) {

        KieAFBuilder kieAfBuilder = KieAFBuilderUtil.getKieAFBuilder(project.getRootPath().toURI().toString(), PathConverter.getNioPath(project),
                                                                     gitCache, builderCache,
                                                                     guvnorM2Repository, KieAFBuilderUtil.getIdentifier(identity));

        gitPullAndRebase(project);
        KieCompilationResponse res = kieAfBuilder.build(Boolean.TRUE, Boolean.FALSE);
        return MavenOutputConverter.convertIntoIncrementalBuildResults(res.getMavenOutput().get(),ERROR_LEVEL,((DefaultKieAFBuilder)kieAfBuilder).getGITURI(),((DefaultKieAFBuilder) kieAfBuilder).getInfo().getPrjPath().getParent().toString());
    }

    private void gitPullAndRebase(Project project) {
        org.uberfire.java.nio.file.Path nioPath = PathConverter.getNioPath(project);
        if(nioPath.getFileSystem() instanceof JGitFileSystem){
           Git git = (Git)gitCache.getGit(nioPath.getFileSystem());
                JGitUtils.pullAndRebase(git);
        }
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
        return builderCache.getKieAFBuilder(project.getRootPath().toURI().toString()) != null;//@TODO check if could be better the classloaderHolder
    }

    @Override
    public IncrementalBuildResults addPackageResource(final Path resource) {
        Project project = projectService.resolveProject(resource);
        return buildIncrementallyInternal(project);
    }

    @Override
    public IncrementalBuildResults deletePackageResource(final Path resource) {
        Project project = projectService.resolveProject(resource);
        return buildIncrementallyInternal(project);
    }

    @Override
    public IncrementalBuildResults updatePackageResource(final Path resource) {
        Project project = projectService.resolveProject(resource);
        return buildIncrementallyInternal(project);
    }

    @Override
    public IncrementalBuildResults applyBatchResourceChanges(final Project project,
                                                             final Map<Path, Collection<ResourceChange>> changes) {
        if (project == null) {
            return new IncrementalBuildResults();
        }
        return buildIncrementallyInternal(project);
    }
}