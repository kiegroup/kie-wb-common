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
package org.kie.workbench.common.services.backend.builder.af.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.impl.decorators.KieAfterDecorator;
import org.kie.workbench.common.services.backend.compiler.impl.decorators.OutputLogAfterDecorator;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieDefaultMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieMavenCompilerFactory;
import org.kie.workbench.common.services.backend.compiler.impl.utils.JGitUtils;
import org.kie.workbench.common.services.backend.compiler.impl.utils.PathConverter;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;

public class DefaultKieAFBuilder implements KieAFBuilder {

    private Path originalProjectRootPath;
    private Git git;
    private AFCompiler<KieCompilationResponse> compiler;
    private WorkspaceCompilationInfo info;
    private CompilationRequest req;
    private String mavenRepo;
    private AtomicBoolean isBuilding = new AtomicBoolean(false);
    private KieCompilationResponse lastResponse = null;

    public DefaultKieAFBuilder(final Path projectRootPath,
                               final String mavenRepo) {
        final Path projectRepo;
        if (projectRootPath.getFileSystem() instanceof JGitFileSystem) {
            this.git = JGitUtils.tempClone((JGitFileSystem) projectRootPath.getFileSystem(), getFolderName());
            try {
                projectRepo = Paths.get(git.getRepository().getDirectory().getParentFile().toPath().resolve(projectRootPath.getFileName().toString()).toFile().getCanonicalFile().toPath().toUri());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            git = null;
            projectRepo = projectRootPath;
        }
        setup(projectRootPath, mavenRepo, git, projectRepo);
    }

    public DefaultKieAFBuilder(final Path projectRootPath,
                               final String mavenRepo,
                               final Git git,
                               final Path workingDir) {
        setup(projectRootPath, mavenRepo, git, workingDir);
    }

    private void setup(Path projectRootPath,
                       String mavenRepo,
                       Git git,
                       Path workingDir) {
        this.originalProjectRootPath = projectRootPath;
        this.git = git;
        this.mavenRepo = mavenRepo;
        this.compiler = new KieAfterDecorator(new OutputLogAfterDecorator(new KieDefaultMavenCompiler()));

        info = new WorkspaceCompilationInfo(workingDir);
        req = new DefaultCompilationRequest(mavenRepo,
                                            info,
                                            new String[]{MavenCLIArgs.PACKAGE},
                                            Boolean.TRUE, Boolean.FALSE);
    }

    private String getFolderName() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void cleanInternalCache() {
        compiler.cleanInternalCache();
        lastResponse = null;
    }

    @Override
    public KieCompilationResponse validate(final Path path,
                                           final InputStream inputStream) {
        if (path.getFileSystem() instanceof JGitFileSystem) {
            final java.nio.file.Path convertedToCheckedPath = git.getRepository().getDirectory().toPath().getParent().resolve(path.toString().substring(1));

            try {
                Files.copy(inputStream, convertedToCheckedPath, StandardCopyOption.REPLACE_EXISTING);
                return doBuild(Boolean.TRUE, Boolean.FALSE);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    git.reset().setMode(ResetCommand.ResetType.HARD).call();
                } catch (GitAPIException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        java.nio.file.Path _path = java.nio.file.Paths.get(path.toUri());
        final byte[] content;
        try {
            content = Files.readAllBytes(_path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Files.copy(inputStream, _path, StandardCopyOption.REPLACE_EXISTING);
            return doBuild(true, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                Files.write(_path, content);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public KieCompilationResponse build() {
        if (lastResponse != null) {
            return lastResponse;
        }
        if (isBuilding.compareAndSet(false, true)) {
            gitPullAndRebase();
            req.getKieCliRequest().getMap().clear();
            lastResponse = compiler.compileSync(req);
            return lastResponse;
        }
        while (isBuilding.get()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
        return lastResponse;
    }

    @Override
    public KieCompilationResponse build(final Boolean logRequested,
                                        final Boolean skipPrjDependenciesCreationList) {
        if (lastResponse != null) {
            return lastResponse;
        }
        if (isBuilding.compareAndSet(false, true)) {
            gitPullAndRebase();
            lastResponse = doBuild(logRequested, skipPrjDependenciesCreationList);
            isBuilding.set(false);
            return lastResponse;
        }
        while (isBuilding.get()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
        return lastResponse;
    }

    private void gitPullAndRebase() {
        if (git != null) {
            JGitUtils.pullAndRebase(git);
        }
    }

    private KieCompilationResponse doBuild(final Boolean logRequested,
                                           final Boolean skipPrjDependenciesCreationList) {
        req.getKieCliRequest().getMap().clear();
        req = new DefaultCompilationRequest(mavenRepo,
                                            info,
                                            new String[]{MavenCLIArgs.COMPILE},
                                            logRequested,
                                            skipPrjDependenciesCreationList);
        return compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse buildAndPackage() {
        gitPullAndRebase();
        req = new DefaultCompilationRequest(mavenRepo,
                                            info,
                                            new String[]{MavenCLIArgs.PACKAGE},
                                            Boolean.TRUE, Boolean.FALSE);
        lastResponse = compiler.compileSync(req);
        return lastResponse;
    }

    @Override
    public KieCompilationResponse buildAndPackage(Boolean skipPrjDependenciesCreationList) {
        gitPullAndRebase();
        req = new DefaultCompilationRequest(mavenRepo,
                                            info,
                                            new String[]{MavenCLIArgs.PACKAGE},
                                            Boolean.TRUE, skipPrjDependenciesCreationList);
        lastResponse = compiler.compileSync(req);
        return lastResponse;
    }

    @Override
    public KieCompilationResponse buildAndInstall() {
        gitPullAndRebase();
        req = new DefaultCompilationRequest(mavenRepo,
                                            info,
                                            new String[]{MavenCLIArgs.INSTALL},
                                            Boolean.TRUE, Boolean.FALSE);
        lastResponse = compiler.compileSync(req);
        return lastResponse;
    }

    @Override
    public KieCompilationResponse buildAndInstall(Boolean skipPrjDependenciesCreationList) {
        gitPullAndRebase();
        req = new DefaultCompilationRequest(mavenRepo,
                                            info,
                                            new String[]{MavenCLIArgs.INSTALL},
                                            Boolean.TRUE, skipPrjDependenciesCreationList);
        lastResponse = compiler.compileSync(req);
        return lastResponse;
    }

    @Override
    public KieCompilationResponse build(String mavenRepo) {
        gitPullAndRebase();
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE},
                                                               Boolean.TRUE, Boolean.FALSE);
        lastResponse = compiler.compileSync(req);
        return lastResponse;
    }

    @Override
    public KieCompilationResponse build(String mavenRepo, Boolean skipPrjDependenciesCreationList) {
        gitPullAndRebase();
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE},
                                                               Boolean.TRUE, skipPrjDependenciesCreationList);
        lastResponse = compiler.compileSync(req);
        return lastResponse;
    }

    @Override
    public KieCompilationResponse build(String projectPath,
                                        String mavenRepo) {
        gitPullAndRebase();
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(projectPath));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE},
                                                               Boolean.TRUE, Boolean.FALSE);
        lastResponse = compiler.compileSync(req);
        return lastResponse;
    }

    @Override
    public KieCompilationResponse build(String projectPath,
                                        String mavenRepo, Boolean skipPrjDependenciesCreationList) {
        gitPullAndRebase();
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(projectPath));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE},
                                                               Boolean.TRUE, skipPrjDependenciesCreationList);
        lastResponse = compiler.compileSync(req);
        return lastResponse;
    }

    @Override
    public KieCompilationResponse buildAndPackage(String projectPath,
                                                  String mavenRepo) {
        gitPullAndRebase();
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(projectPath));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.PACKAGE},
                                                               Boolean.TRUE, Boolean.FALSE);
        lastResponse = compiler.compileSync(req);
        return lastResponse;
    }

    @Override
    public KieCompilationResponse buildAndPackage(String projectPath,
                                                  String mavenRepo, Boolean skipPrjDependenciesCreationList) {
        gitPullAndRebase();
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(projectPath));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.PACKAGE},
                                                               Boolean.TRUE, skipPrjDependenciesCreationList);
        lastResponse = compiler.compileSync(req);
        return lastResponse;
    }

    @Override
    public KieCompilationResponse buildAndInstall(String projectPath,
                                                  String mavenRepo) {
        gitPullAndRebase();
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(PathConverter.createPathFromString(projectPath));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.INSTALL},
                                                               Boolean.TRUE, Boolean.FALSE);
        lastResponse = compiler.compileSync(req);
        return lastResponse;
    }

    @Override
    public KieCompilationResponse buildAndInstall(String projectPath,
                                                  String mavenRepo, Boolean skipPrjDependenciesCreationList) {
        gitPullAndRebase();
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(PathConverter.createPathFromString(projectPath));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.INSTALL},
                                                               Boolean.TRUE, skipPrjDependenciesCreationList);
        lastResponse = compiler.compileSync(req);
        return lastResponse;
    }

    @Override
    public KieCompilationResponse buildSpecialized(String projectPath,
                                                   String mavenRepo,
                                                   String[] args) {
        gitPullAndRebase();
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(PathConverter.createPathFromString(projectPath));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               args,
                                                               Boolean.TRUE, Boolean.FALSE);
        lastResponse = compiler.compileSync(req);
        return lastResponse;
    }

    @Override
    public KieCompilationResponse buildSpecialized(String projectPath,
                                                   String mavenRepo,
                                                   String[] args, Boolean skipPrjDependenciesCreationList) {
        gitPullAndRebase();
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(PathConverter.createPathFromString(projectPath));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               args,
                                                               Boolean.TRUE, skipPrjDependenciesCreationList);
        lastResponse = compiler.compileSync(req);
        return lastResponse;
    }

    @Override
    public KieCompilationResponse buildSpecialized(String projectPath,
                                                   String mavenRepo,
                                                   String[] args,
                                                   KieDecorator decorator) {
        gitPullAndRebase();
        AFCompiler<KieCompilationResponse> compiler = KieMavenCompilerFactory.getCompiler(decorator);
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(PathConverter.createPathFromString(projectPath));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               args,
                                                               Boolean.TRUE, Boolean.FALSE);
        lastResponse = compiler.compileSync(req);
        return lastResponse;
    }

    @Override
    public KieCompilationResponse buildSpecialized(String projectPath,
                                                   String mavenRepo,
                                                   String[] args,
                                                   KieDecorator decorator, Boolean skipPrjDependenciesCreationList) {
        gitPullAndRebase();
        AFCompiler<KieCompilationResponse> compiler = KieMavenCompilerFactory.getCompiler(decorator);
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(PathConverter.createPathFromString(projectPath));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               args,
                                                               Boolean.TRUE, skipPrjDependenciesCreationList);
        lastResponse = compiler.compileSync(req);
        return lastResponse;
    }

    public AFCompiler getCompiler() {
        return compiler;
    }

    public WorkspaceCompilationInfo getInfo() {
        return info;
    }

    public CompilationRequest getReq() {
        return req;
    }

    public String getMavenRepo() {
        return mavenRepo;
    }

    public Path getGITURI() {
        return originalProjectRootPath;
    }

    public Git getGit() {
        return git;
    }
}
