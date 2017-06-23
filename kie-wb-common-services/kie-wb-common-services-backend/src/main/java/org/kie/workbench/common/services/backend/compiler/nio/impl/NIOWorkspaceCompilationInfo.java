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

package org.kie.workbench.common.services.backend.compiler.nio.impl;

import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;

import org.eclipse.jgit.api.Git;
import org.kie.workbench.common.services.backend.compiler.nio.NIOMavenCompiler;

public class NIOWorkspaceCompilationInfo {

    private Path prjPath;
    private Path enhancedMainPomFile;
    private URI remoteRepo;
    private NIOMavenCompiler compiler;
    private Git gitRepo;
    private Boolean kiePluginPresent = Boolean.FALSE;

    public NIOWorkspaceCompilationInfo(Path prjPath,
                                       URI remoteRepo,
                                       NIOMavenCompiler compiler,
                                       Git gitRepo) {
        this.prjPath = prjPath;
        this.remoteRepo = remoteRepo;
        this.compiler = compiler;
        this.gitRepo = gitRepo;
    }

    public NIOWorkspaceCompilationInfo(Path prjPath,
                                       NIOMavenCompiler compiler) {
        this.prjPath = prjPath;
        this.compiler = compiler;
    }

    public Boolean lateAdditionEnhancedMainPomFile(Path enhancedPom) {
        if (enhancedMainPomFile == null && enhancedPom != null) {
            this.enhancedMainPomFile = enhancedPom;
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean lateAdditionGitRepo(Git git) {
        if (gitRepo == null && git != null) {
            this.gitRepo = git;
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean lateAdditionRemoteGitRepo(URI uri) {
        if (remoteRepo == null && uri != null) {
            this.remoteRepo = uri;
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean lateAdditionKiePluginPresent(Boolean present) {
        if ((kiePluginPresent == null && present != null)) {
            this.kiePluginPresent = present;
            return Boolean.TRUE;
        }
        if (present != null) {
            kiePluginPresent = kiePluginPresent | present;
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean isKiePluginPresent() {
        return kiePluginPresent;
    }

    public Optional<Git> getGitRepo() {
        if (gitRepo != null) {
            return Optional.of(gitRepo);
        } else {
            return Optional.empty();
        }
    }

    public Path getPrjPath() {
        return prjPath;
    }

    public Optional<Path> getEnhancedMainPomFile() {
        if (enhancedMainPomFile != null) {
            return Optional.of(enhancedMainPomFile);
        } else {
            return Optional.empty();
        }
    }

    public Path getMavenRepo() {
        return compiler.getMavenRepo();
    }

    public Optional<URI> getRemoteRepo() {
        if (remoteRepo != null) {
            return Optional.of(remoteRepo);
        } else {
            return Optional.empty();
        }
    }

    public NIOMavenCompiler getCompiler() {
        return compiler;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WorkspaceCompilationInfo{");
        sb.append("prjPath=").append(prjPath);
        sb.append(", enhancedMainPomFile=").append(enhancedMainPomFile);
        sb.append(", mavenRepo=").append(compiler.getMavenRepo());
        sb.append(", remoteRepo=").append(remoteRepo);
        sb.append(", compiler=").append(compiler);
        sb.append('}');
        return sb.toString();
    }
}
