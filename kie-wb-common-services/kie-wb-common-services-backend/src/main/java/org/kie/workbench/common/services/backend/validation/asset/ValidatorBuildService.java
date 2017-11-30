/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.validation.asset;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Charsets;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.kie.workbench.common.services.backend.builder.af.impl.DefaultKieAFBuilder;
import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.decorators.JGITCompilerBeforeDecorator;
import org.kie.workbench.common.services.backend.compiler.impl.decorators.KieAfterDecorator;
import org.kie.workbench.common.services.backend.compiler.impl.decorators.OutputLogAfterDecorator;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieDefaultMavenCompiler;
import org.guvnor.common.services.backend.cache.BuilderCache;
import org.guvnor.common.services.backend.cache.GitCache;
import org.kie.workbench.common.services.backend.compiler.impl.utils.KieAFBuilderUtil;
import org.kie.workbench.common.services.backend.compiler.impl.utils.MavenOutputConverter;
import org.kie.workbench.common.services.backend.compiler.impl.utils.MavenUtils;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;

import static org.uberfire.backend.server.util.Paths.convert;

@ApplicationScoped
public class ValidatorBuildService {

    private final static String ERROR_CLASS_NOT_FOUND = "Definition of class \"{0}\" was not found. Consequentially validation cannot be performed.\nPlease check the necessary external dependencies for this project are configured correctly.";

    private IOService ioService;
    private KieProjectService projectService;
    private GuvnorM2Repository guvnorM2Repository;
    private GitCache gitCache;
    private BuilderCache builderCache;
    private Logger logger = LoggerFactory.getLogger(ValidatorBuildService.class);
    private String ERROR_LEVEL = "ERROR";
    private String SYSTEM_IDENTITY = "system";

    public ValidatorBuildService() {
        //CDI proxies
    }

    @Inject
    public ValidatorBuildService(final @Named("ioStrategy") IOService ioService,
                                 final KieProjectService projectService,
                                 final GuvnorM2Repository guvnorM2Repository,
                                 final GitCache gitCache,
                                 final BuilderCache builderCache) {
        this.ioService = ioService;
        this.projectService = projectService;
        this.guvnorM2Repository = guvnorM2Repository;
        this.gitCache = gitCache;
        this.builderCache = builderCache;
    }

    private AFCompiler getCompiler() {
        // we create the compiler in this weird mode to use the gitMap used internally
        AFCompiler innerDecorator = new KieAfterDecorator(new OutputLogAfterDecorator(new KieDefaultMavenCompiler()));
        AFCompiler outerDecorator = new JGITCompilerBeforeDecorator(innerDecorator);
        return outerDecorator;
    }

    private KieAFBuilder getBuilder(final Project project) {
        final org.uberfire.java.nio.file.Path projectRootPath = convert(project.getRootPath());
        final KieAFBuilder builder = (KieAFBuilder) builderCache.getKieAFBuilder(project.getRootPath().toURI().toString());
        if (builder == null) {
            final KieAFBuilder newBuilder = new DefaultKieAFBuilder(projectRootPath.toUri().toString(),
                                                                    MavenUtils.getMavenRepoDir(guvnorM2Repository.getM2RepositoryDir(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME)),
                                                                    getCompiler());
            builderCache.addKieAFBuilder(project.getRootPath().toURI().toString(), newBuilder);
            return newBuilder;
        }
        return builder;
    }

    public List<ValidationMessage> validate(final Path resourcePath,
                                            final String content) {
        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(content.getBytes(Charsets.UTF_8));
            final List<ValidationMessage> results = doValidation(resourcePath, inputStream);
            return results;
        } catch (NoClassDefFoundError e) {
            return error(MessageFormat.format(ERROR_CLASS_NOT_FOUND,
                                              e.getLocalizedMessage()));
        } catch (Throwable e) {
            return error(e.getLocalizedMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public List<ValidationMessage> validate(final Path resourcePath) {
        InputStream inputStream = null;
        try {

            inputStream = ioService.newInputStream(convert(resourcePath));
            final List<ValidationMessage> results = doValidation(resourcePath, inputStream);
            return results;
        } catch (NoClassDefFoundError e) {
            return error(MessageFormat.format(ERROR_CLASS_NOT_FOUND,
                                              e.getLocalizedMessage()));
        } catch (Throwable e) {
            return error(e.getLocalizedMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private List<ValidationMessage> doValidation(final Path _resourcePath,
                                                 final InputStream inputStream) throws NoProjectException {

        final Optional<KieProject> project = project(_resourcePath);
        if (!project.isPresent()) {
            return getExceptionMsgs("[ERROR] no project found");
        }

        final KieProject kieProject = project.get();
        final org.uberfire.java.nio.file.Path resourcePath = convert(_resourcePath);
        if(resourcePath.getFileSystem() instanceof JGitFileSystem) {
            final JGitFileSystem fs = (JGitFileSystem) resourcePath.getFileSystem();
            Git git = (Git)gitCache.getGit(fs);
            if (git == null) {
                //one build discarded to create the git in compiler map
                org.uberfire.java.nio.file.Path nioPath = Paths.convert(kieProject.getRootPath());
                final KieAFBuilder builder = KieAFBuilderUtil.getKieAFBuilder(kieProject.getRootPath().toURI(),nioPath,gitCache,builderCache,guvnorM2Repository, SYSTEM_IDENTITY);
                builder.build(Boolean.TRUE, Boolean.FALSE);
                git = (Git)gitCache.getGit(fs);
                if (git == null) {
                    logger.error("Git not constructed in the JGitDecorator");
                    throw new RuntimeException("Git repo not found");
                }
            }

            final java.nio.file.Path rootRepoPath = git.getRepository().getDirectory().toPath().getParent();
            final org.uberfire.java.nio.file.Path projectRootPath = convert(kieProject.getRootPath());
            final java.nio.file.Path tempResourcePath = rootRepoPath.resolve(kieProject.getRootPath().getFileName()).resolve(projectRootPath.relativize(resourcePath).toString());

            try {
                return writeFileChangeAndBuild(tempResourcePath, inputStream, kieProject);
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
                return getExceptionMsgs("[ERROR] no project found");
            } finally {
                try {

                    git.revert().call();
                } catch (GitAPIException e) {
                    logger.error(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            }
        }else{
            return getExceptionMsgs("[ERROR] no GIT FS found");
        }
    }

    private List<ValidationMessage> getExceptionMsgs(String msg) {
        List<ValidationMessage> msgs = new ArrayList<>();
        ValidationMessage msgInternal = new ValidationMessage();
        msgInternal.setText(msg);
        msgs.add(msgInternal);
        return msgs;
    }

    private List<ValidationMessage> writeFileChangeAndBuild(final java.nio.file.Path tempResourcePath,
                                                            final InputStream inputStream,
                                                            final KieProject project) throws IOException {

        Files.copy(inputStream, tempResourcePath, StandardCopyOption.REPLACE_EXISTING);
        final KieAFBuilder builder = getBuilder(project);
        final CompilationResponse res = builder.build(Boolean.TRUE, Boolean.FALSE);
        List<ValidationMessage> valMsgs =  MavenOutputConverter.convertIntoValidationMessage(res.getMavenOutput().get(), ERROR_LEVEL, ((DefaultKieAFBuilder)builder).getGITURI(), ((DefaultKieAFBuilder) builder).getInfo().getPrjPath().getParent().toString());
        return valMsgs;

    }

    private Optional<KieProject> project(final Path resourcePath) throws NoProjectException {
        final KieProject project = projectService.resolveProject(resourcePath);
        return Optional.ofNullable(project);
    }

    private ArrayList<ValidationMessage> error(final String errorMessage) {
        return new ArrayList<ValidationMessage>() {{
            add(new ValidationMessage(Level.ERROR, errorMessage));
        }};
    }
}