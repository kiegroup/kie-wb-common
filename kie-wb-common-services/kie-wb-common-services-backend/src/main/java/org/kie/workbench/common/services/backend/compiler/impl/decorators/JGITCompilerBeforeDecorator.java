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

package org.kie.workbench.common.services.backend.compiler.impl.decorators;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.guvnor.common.services.backend.cache.GitCache;
import org.kie.workbench.common.services.backend.compiler.impl.utils.JGitUtils;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;

/***
 * Before decorator to update a git repo before the build
 */
public class JGITCompilerBeforeDecorator<T extends CompilationResponse, C extends AFCompiler<T>> implements CompilerDecorator {

    private Map<JGitFileSystem, Git> gitMap;
    private GitCache gitCache;
    private boolean holder;
    private C compiler;

    public JGITCompilerBeforeDecorator(C compiler) {
        this.compiler = compiler;
        gitMap = new HashMap<>();
        holder = Boolean.FALSE;
    }

    public JGITCompilerBeforeDecorator(C compiler, GitCache gitCache) {
        this.compiler = compiler;
        this.gitCache = gitCache;
        holder = Boolean.TRUE;
    }

    @Override
    public void cleanInternalCache() {}

    @Override
    public T compileSync(CompilationRequest req) {

        final Path path = req.getInfo().getPrjPath();
        final CompilationRequest _req;

        if (path.getFileSystem() instanceof JGitFileSystem) {
            final JGitFileSystem fs = (JGitFileSystem) path.getFileSystem();
            Git repo = holder ? useHolder(fs,
                                          req) : useInternalMap(fs,
                                                                req);
            if (!req.skipAutoSourceUpdate()) {
                JGitUtils.pullAndRebase(repo);
            }

            _req = new DefaultCompilationRequest(req.getMavenRepo(),
                                                 new WorkspaceCompilationInfo(Paths.get(repo.getRepository().getDirectory().toPath().getParent().resolve(path.getFileName().toString()).normalize().toUri())),
                                                 req.getOriginalArgs(),
                                                 req.getLogRequested(),
                                                 req.skipPrjDependenciesCreationList());
        } else {
            _req = req;
        }

        return compiler.compileSync(_req);
    }

    private Git useInternalMap(JGitFileSystem fs,
                               CompilationRequest req) {
        Git repo;
        if (!gitMap.containsKey(fs)) {
            repo = JGitUtils.tempClone(fs,
                                       req.getRequestUUID());
            gitMap.put(fs,
                       repo);
        }
        repo = gitMap.get(fs);
        return repo;
    }

    private Git useHolder(JGitFileSystem fs,
                          CompilationRequest req) {
        Git repo;
        if (!gitCache.containsJGitFileSystem(fs)) {
            repo = JGitUtils.tempClone(fs, req.getRequestUUID());
            gitCache.addJGitFileSystem(fs, repo);
        }
        repo = (Git)gitCache.getGit(fs);
        return repo;
    }

    @Override
    public T buildDefaultCompilationResponse(final Boolean value) {
        return compiler.buildDefaultCompilationResponse(value);
    }

    @Override
    public T buildDefaultCompilationResponse(final Boolean successful, final List mavenOutput, final Path workingDir) {
        return (T) compiler.buildDefaultCompilationResponse(successful, mavenOutput, workingDir);
    }
}