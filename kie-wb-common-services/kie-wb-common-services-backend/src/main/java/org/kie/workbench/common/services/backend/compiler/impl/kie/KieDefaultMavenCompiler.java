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
package org.kie.workbench.common.services.backend.compiler.impl.kie;

import java.util.List;

import org.kie.workbench.common.services.backend.compiler.impl.BaseMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultKieCompilationResponse;
import org.uberfire.java.nio.file.Path;

/**
 * Run maven on Kie projects with https://maven.apache.org/ref/3.3.9/maven-embedder/xref/index.html
 * to use Takari plugins like a black box
 */
public class KieDefaultMavenCompiler extends BaseMavenCompiler<KieCompilationResponse> implements KieMavenCompiler {

    @Override
    public KieCompilationResponse buildDefaultCompilationResponse(final Boolean successful) {
        return new DefaultKieCompilationResponse(successful);
    }

    @Override
    public KieCompilationResponse buildDefaultCompilationResponse(final Boolean successful,
                                                                  final List mavenOutput) {
        return new DefaultKieCompilationResponse(successful, mavenOutput);
    }

    @Override
    public KieCompilationResponse buildDefaultCompilationResponse(final Boolean successful, final List mavenOutput, final Path workingDir) {
        return new DefaultKieCompilationResponse(successful, mavenOutput, workingDir);
    }

    @Override
    public void cleanInternalCache() {
       cleanInternalCache();
    }
}
