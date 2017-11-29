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

import java.util.List;

import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.uberfire.java.nio.file.Path;

/**
 * Run maven with https://maven.apache.org/ref/3.3.9/maven-embedder/xref/index.html
 * to use Takari plugins like a black box
 * <p>
 * MavenCompiler compiler = new DefaultMavenCompiler();
 * or
 * MavenCompiler compiler = MavenCompilerFactory.getCompiler(Decorator.LOG_OUTPUT_AFTER);
 * <p>
 * WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(tmp);
 * CompilationRequest req = new DefaultCompilationRequest(mavenRepo, info,new String[]{MavenArgs.COMPILE},new HashMap<>(), Boolean.TRUE );
 * CompilationResponse res = compiler.compileSync(req);
 */
public class DefaultMavenCompiler extends BaseMavenCompiler<CompilationResponse> implements MavenCompiler {

    @Override
    public CompilationResponse buildDefaultCompilationResponse(final Boolean successful) {
        return new DefaultCompilationResponse(successful);
    }

    @Override
    public CompilationResponse buildDefaultCompilationResponse(final Boolean successful,
                                                               final List mavenOutput) {
        return new DefaultCompilationResponse(successful, mavenOutput);
    }

    @Override
    public CompilationResponse buildDefaultCompilationResponse(final Boolean successful,
                                                               final List mavenOutput,
                                                               final Path workingDir) {
        return new DefaultCompilationResponse(successful, mavenOutput, workingDir);
    }

    @Override
    public void cleanInternalCache() {
        cleanInternalCache();
    }
}