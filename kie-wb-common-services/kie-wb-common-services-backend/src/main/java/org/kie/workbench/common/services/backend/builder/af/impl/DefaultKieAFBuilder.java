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

import java.net.URI;
import java.util.Optional;

import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieMavenCompilerFactory;
import org.kie.workbench.common.services.backend.compiler.impl.utils.PathConverter;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

public class DefaultKieAFBuilder implements KieAFBuilder {

    private AFCompiler compiler;
    private WorkspaceCompilationInfo info;
    private CompilationRequest req;
    private String mavenRepo;
    private String FILE_URI = "file://";
    private Path GIT_URI;

    @Override
    public void cleanInternalCache() {
        compiler.cleanInternalCache();
    }

    public DefaultKieAFBuilder(Path projectRepo,
                               String mavenRepo) {
        /**In the construct we create the objects ready for a call to the build() without params to reuse all the internal objects,
         * only in the internal maven compilation new objects will be created in the compileSync */
        this.mavenRepo = mavenRepo;
        compiler = KieMavenCompilerFactory.getCompiler(KieDecorator.LOG_OUTPUT_AFTER);
        info = new WorkspaceCompilationInfo(projectRepo);
        req = new DefaultCompilationRequest(mavenRepo,
                                            info,
                                            new String[]{MavenCLIArgs.COMPILE},
                                            Boolean.TRUE, Boolean.FALSE);
    }

    public DefaultKieAFBuilder(Path projectRepo,
                               String mavenRepo, AFCompiler compiler, Path gitUri) {
        /**In the construct we create the objects ready for a call to the build() without params to reuse all the internal objects,
         * only in the internal maven compilation new objects will be created in the compileSync */
        this.mavenRepo = mavenRepo;
        this.compiler = compiler;
        this.GIT_URI = gitUri;
        info = new WorkspaceCompilationInfo(projectRepo);
        req = new DefaultCompilationRequest(mavenRepo,
                                            info,
                                            new String[]{MavenCLIArgs.COMPILE},
                                            Boolean.TRUE, Boolean.FALSE);
    }

    public DefaultKieAFBuilder(String projectRepo,
                               String mavenRepo) {
        /**In the construct we create the objects ready for a call to the build() without params to reuse all the internal objects,
         * only in the internal maven compilation new objects will be created in the compileSync */
        this.mavenRepo = mavenRepo;
        compiler = KieMavenCompilerFactory.getCompiler(KieDecorator.LOG_OUTPUT_AFTER);
        info = new WorkspaceCompilationInfo(Paths.get(URI.create(FILE_URI + projectRepo)));
        req = new DefaultCompilationRequest(mavenRepo,
                                            info,
                                            new String[]{MavenCLIArgs.COMPILE},
                                            Boolean.TRUE, Boolean.FALSE);
    }

    public DefaultKieAFBuilder(String projectRepo,
                               String mavenRepo, Boolean skipPrjDependenciesCreationList) {
        /**In the construct we create the objects ready for a call to the build() without params to reuse all the internal objects,
         * only in the internal maven compilation new objects will be created in the compileSync */
        this.mavenRepo = mavenRepo;
        compiler = KieMavenCompilerFactory.getCompiler(KieDecorator.LOG_OUTPUT_AFTER);
        info = new WorkspaceCompilationInfo(Paths.get(URI.create(FILE_URI + projectRepo)));
        req = new DefaultCompilationRequest(mavenRepo,
                                            info,
                                            new String[]{MavenCLIArgs.COMPILE},
                                            Boolean.TRUE, skipPrjDependenciesCreationList);
    }

    public DefaultKieAFBuilder(String projectRepo,
                               String mavenRepo,
                               String[] args) {
        /**In the  construct we create the objects ready for a call to the build() without params to reuse all the internal objects,
         * only in the internal maven compilation new objects will be created in the compileSync */
        this.mavenRepo = mavenRepo;
        compiler = KieMavenCompilerFactory.getCompiler(KieDecorator.LOG_OUTPUT_AFTER);
        info = new WorkspaceCompilationInfo(Paths.get(projectRepo));
        req = new DefaultCompilationRequest(mavenRepo,
                                            info,
                                            args,
                                            Boolean.TRUE, Boolean.FALSE);
    }

    public DefaultKieAFBuilder(String projectRepo,
                               String mavenRepo,
                               String[] args, Boolean skipPrjDependenciesCreationList) {
        /**In the  construct we create the objects ready for a call to the build() without params to reuse all the internal objects,
         * only in the internal maven compilation new objects will be created in the compileSync */
        this.mavenRepo = mavenRepo;
        compiler = KieMavenCompilerFactory.getCompiler(KieDecorator.LOG_OUTPUT_AFTER);
        info = new WorkspaceCompilationInfo(Paths.get(projectRepo));
        req = new DefaultCompilationRequest(mavenRepo,
                                            info,
                                            args,
                                            Boolean.TRUE, skipPrjDependenciesCreationList);
    }

    public DefaultKieAFBuilder(String projectRepo,
                               String mavenRepo,
                               AFCompiler compiler) {
        /**In the construct we create the objects ready for a call to the build() without params to reuse all the internal objects,
         * only in the internal maven compilation new objects will be created in the compileSync */
        this.mavenRepo = mavenRepo;
        this.compiler = compiler;
        info = new WorkspaceCompilationInfo(Paths.get(URI.create(FILE_URI + projectRepo)));
        req = new DefaultCompilationRequest(mavenRepo,
                                            info,
                                            new String[]{MavenCLIArgs.COMPILE},
                                            Boolean.TRUE, Boolean.FALSE);
    }

    public DefaultKieAFBuilder(String projectRepo,
                               String mavenRepo,
                               AFCompiler compiler, Boolean skipPrjDependenciesCreationList) {
        /**In the construct we create the objects ready for a call to the build() without params to reuse all the internal objects,
         * only in the internal maven compilation new objects will be created in the compileSync */
        this.mavenRepo = mavenRepo;
        this.compiler = compiler;
        info = new WorkspaceCompilationInfo(Paths.get(URI.create(FILE_URI + projectRepo)));
        req = new DefaultCompilationRequest(mavenRepo,
                                            info,
                                            new String[]{MavenCLIArgs.COMPILE},
                                            Boolean.TRUE, skipPrjDependenciesCreationList);
    }

    public DefaultKieAFBuilder(String projectRepo,
                               String mavenRepo,
                               String[] mavenArgs,
                               AFCompiler compiler, Boolean skipPrjDependenciesCreationList) {
        /**In the construct we create the objects ready for a call to the build() without params to reuse all the internal objects,
         * only in the internal maven compilation new objects will be created in the compileSync */
        this.mavenRepo = mavenRepo;
        this.compiler = compiler;
        info = new WorkspaceCompilationInfo(Paths.get(URI.create(FILE_URI + projectRepo)));
        req = new DefaultCompilationRequest(mavenRepo,
                                            info,
                                            mavenArgs,
                                            Boolean.TRUE, skipPrjDependenciesCreationList);
    }

    @Override
    public KieCompilationResponse build() {
        req.getKieCliRequest().getMap().clear();
        return (KieCompilationResponse) compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse build(Boolean logRequested, Boolean skipPrjDependenciesCreationList) {
        req.getKieCliRequest().getMap().clear();
        req = new DefaultCompilationRequest(mavenRepo,
                                            info,
                                            new String[]{MavenCLIArgs.COMPILE},
                                            logRequested, skipPrjDependenciesCreationList);
        return (KieCompilationResponse) compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse buildAndPackage() {
        req = new DefaultCompilationRequest(mavenRepo,
                                            info,
                                            new String[]{MavenCLIArgs.PACKAGE},
                                            Boolean.TRUE, Boolean.FALSE);
        return (KieCompilationResponse) compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse buildAndPackage(Boolean skipPrjDependenciesCreationList) {
        req = new DefaultCompilationRequest(mavenRepo,
                                            info,
                                            new String[]{MavenCLIArgs.PACKAGE},
                                            Boolean.TRUE, skipPrjDependenciesCreationList);
        return (KieCompilationResponse) compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse buildAndInstall() {
        req = new DefaultCompilationRequest(mavenRepo,
                                            info,
                                            new String[]{MavenCLIArgs.INSTALL},
                                            Boolean.TRUE, Boolean.FALSE);
        return (KieCompilationResponse) compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse buildAndInstall(Boolean skipPrjDependenciesCreationList) {
        req = new DefaultCompilationRequest(mavenRepo,
                                            info,
                                            new String[]{MavenCLIArgs.INSTALL},
                                            Boolean.TRUE, skipPrjDependenciesCreationList);
        return (KieCompilationResponse) compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse build(String mavenRepo) {
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE},
                                                               Boolean.TRUE, Boolean.FALSE);
        return (KieCompilationResponse) compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse build(String mavenRepo, Boolean skipPrjDependenciesCreationList) {
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE},
                                                               Boolean.TRUE, skipPrjDependenciesCreationList);
        return (KieCompilationResponse) compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse build(String projectPath,
                                        String mavenRepo) {
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(projectPath));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE},
                                                               Boolean.TRUE, Boolean.FALSE);
        return (KieCompilationResponse) compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse build(String projectPath,
                                        String mavenRepo, Boolean skipPrjDependenciesCreationList) {
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(projectPath));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE},
                                                               Boolean.TRUE, skipPrjDependenciesCreationList);
        return (KieCompilationResponse) compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse buildAndPackage(String projectPath,
                                                  String mavenRepo) {
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(projectPath));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.PACKAGE},
                                                               Boolean.TRUE, Boolean.FALSE);
        return (KieCompilationResponse) compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse buildAndPackage(String projectPath,
                                                  String mavenRepo, Boolean skipPrjDependenciesCreationList) {
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(projectPath));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.PACKAGE},
                                                               Boolean.TRUE, skipPrjDependenciesCreationList);
        return (KieCompilationResponse) compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse buildAndInstall(String projectPath,
                                                  String mavenRepo) {
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(PathConverter.createPathFromString(projectPath));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.INSTALL},
                                                               Boolean.TRUE, Boolean.FALSE);
        return (KieCompilationResponse) compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse buildAndInstall(String projectPath,
                                                  String mavenRepo, Boolean skipPrjDependenciesCreationList) {
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(PathConverter.createPathFromString(projectPath));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.INSTALL},
                                                               Boolean.TRUE, skipPrjDependenciesCreationList);
        return (KieCompilationResponse) compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse buildSpecialized(String projectPath,
                                                   String mavenRepo,
                                                   String[] args) {
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(PathConverter.createPathFromString(projectPath));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               args,
                                                               Boolean.TRUE, Boolean.FALSE);
        return (KieCompilationResponse) compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse buildSpecialized(String projectPath,
                                                   String mavenRepo,
                                                   String[] args, Boolean skipPrjDependenciesCreationList) {
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(PathConverter.createPathFromString(projectPath));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               args,
                                                               Boolean.TRUE, skipPrjDependenciesCreationList);
        return (KieCompilationResponse) compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse buildSpecialized(String projectPath,
                                                   String mavenRepo,
                                                   String[] args,
                                                   KieDecorator decorator) {
        AFCompiler compiler = KieMavenCompilerFactory.getCompiler(decorator);
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(PathConverter.createPathFromString(projectPath));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               args,
                                                               Boolean.TRUE, Boolean.FALSE);
        return (KieCompilationResponse) compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse buildSpecialized(String projectPath,
                                                   String mavenRepo,
                                                   String[] args,
                                                   KieDecorator decorator, Boolean skipPrjDependenciesCreationList) {
        AFCompiler compiler = KieMavenCompilerFactory.getCompiler(decorator);
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(PathConverter.createPathFromString(projectPath));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               args,
                                                               Boolean.TRUE, skipPrjDependenciesCreationList);
        return (KieCompilationResponse) compiler.compileSync(req);
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

    public Path getGITURI(){
        return GIT_URI;
    }
}
