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
package org.kie.workbench.common.services.backend.compiler.impl.utils;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.inject.Instance;

import org.eclipse.jgit.api.Git;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.kie.workbench.common.services.backend.builder.af.impl.DefaultKieAFBuilder;
import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.impl.decorators.JGITCompilerBeforeDecorator;
import org.kie.workbench.common.services.backend.compiler.impl.decorators.KieAfterDecorator;
import org.kie.workbench.common.services.backend.compiler.impl.decorators.OutputLogAfterDecorator;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieDefaultMavenCompiler;
import org.guvnor.common.services.backend.cache.BuilderCache;
import org.guvnor.common.services.backend.cache.GitCache;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;

public class KieAFBuilderUtil {

    //@TODO currently the only way to understand if is a imported prj
    private static final String IMPORTED_REPO_URI = "@myrepo";

    private static final String SYSTEM_IDENTITY = "system";

    public static KieAFBuilder getKieAFBuilder(String uri, org.uberfire.java.nio.file.Path nioPath,
                                               GitCache gitCache, BuilderCache builderCache,
                                               GuvnorM2Repository guvnorM2Repository, String user) {

        KieAFBuilder builder = (KieAFBuilder) builderCache.getKieAFBuilder(uri);
        if (builder == null) {
            if (nioPath.getFileSystem() instanceof JGitFileSystem) {
                String folderName = getFolderName(uri, user);
                Git repo = JGitUtils.tempClone((JGitFileSystem) nioPath.getFileSystem(), folderName);
                gitCache.addJGitFileSystem((JGitFileSystem) nioPath.getFileSystem(), repo);
                org.uberfire.java.nio.file.Path prj = org.uberfire.java.nio.file.Paths.get(URI.create(repo.getRepository().getDirectory().toPath().getParent().toAbsolutePath().toUri().toString() + nioPath.toString()));
                builder = new DefaultKieAFBuilder(prj,
                                                  MavenUtils.getMavenRepoDir(guvnorM2Repository.getM2RepositoryDir(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME)),
                                                  getCompiler(gitCache), nioPath);
                builderCache.addKieAFBuilder(uri, builder);
            } else {
                //uri is the working dir
                org.uberfire.java.nio.file.Path prj = org.uberfire.java.nio.file.Paths.get(uri);
                builder = new DefaultKieAFBuilder(prj,
                                                  MavenUtils.getMavenRepoDir(guvnorM2Repository.getM2RepositoryDir(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME)),
                                                  getCompilerWithoutGITsupport(), nioPath);
                builderCache.addKieAFBuilder(uri, builder);
            }
        }
        return builder;
    }

    public static String getFolderName(String uri, String user) {
        return uri.contains(IMPORTED_REPO_URI) ? UUID.randomUUID().toString() : user + "-" + UUID.randomUUID().toString();
    }

    public static AFCompiler getCompiler(GitCache gitCache) {
        // we create the compiler in this weird mode to use the gitMap used internally
        AFCompiler innerDecorator = new KieAfterDecorator(new OutputLogAfterDecorator(new KieDefaultMavenCompiler()));
        AFCompiler outerDecorator = new JGITCompilerBeforeDecorator(innerDecorator, gitCache);
        return outerDecorator;
    }

    public static AFCompiler getCompilerWithoutGITsupport() {
        AFCompiler decorator = new KieAfterDecorator(new OutputLogAfterDecorator(new KieDefaultMavenCompiler()));
        return decorator;
    }

    public static Optional<Path> getFSPath(KieProject project,
                                           GitCache gitCache, BuilderCache builderCache,
                                           GuvnorM2Repository guvnorM2Repository, String user) {
        Path nioPath = Paths.convert(project.getRootPath());
        KieAFBuilder builder = KieAFBuilderUtil.getKieAFBuilder(project.getRootPath().toURI(), nioPath,
                                                                gitCache, builderCache, guvnorM2Repository, user);
        if (builder != null) {
            Path prjPath = ((DefaultKieAFBuilder) builder).getInfo().getPrjPath();
            return Optional.ofNullable(prjPath);
        } else {
            return Optional.empty();
        }
    }

    public static String getIdentifier(Instance<User> identity) {
        if (identity.isUnsatisfied()) {
            return SYSTEM_IDENTITY;
        }
        try {
            return identity.get().getIdentifier();
        } catch (ContextNotActiveException e) {
            return SYSTEM_IDENTITY;
        }
    }
}
