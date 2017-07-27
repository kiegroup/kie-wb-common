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
package org.kie.workbench.common.services.backend.compiler.nio.impl.kie;

import java.util.Collections;
import java.util.List;

import org.codehaus.plexus.classworlds.ClassWorld;
import org.kie.workbench.common.services.backend.compiler.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.configuration.Compilers;
import org.kie.workbench.common.services.backend.compiler.external339.AFMavenCli;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultKieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.ProcessedPoms;
import org.kie.workbench.common.services.backend.compiler.nio.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.IncrementalCompilerEnabler;
import org.kie.workbench.common.services.backend.compiler.nio.KieMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.nio.impl.DefaultIncrementalCompilerEnabler;
import org.kie.workbench.common.services.backend.compiler.nio.impl.DefaultMavenCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Run maven on Kie projects with https://maven.apache.org/ref/3.3.9/maven-embedder/xref/index.html
 * to use Takari plugins like a black box
 */
public class KieDefaultMavenCompiler implements KieMavenCompiler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMavenCompiler.class);

    private AFMavenCli cli;
    private IncrementalCompilerEnabler enabler;

    public KieDefaultMavenCompiler() {
        cli = new AFMavenCli();
        enabler = new DefaultIncrementalCompilerEnabler(Compilers.JAVAC);
    }

    public KieCompilationResponse compileSync(CompilationRequest req) {
        if (logger.isDebugEnabled()) {
            logger.debug("KieCompilationRequest:{}",
                         req);
        }

        if (!req.getInfo().getEnhancedMainPomFile().isPresent()) {
            ProcessedPoms processedPoms = enabler.process(req);
            if (!processedPoms.getResult()) {
                return new DefaultKieCompilationResponse(Boolean.FALSE,
                                                         "Processing poms failed",
                                                         Collections.emptyList());
            }
        }
        req.getKieCliRequest().getRequest().setLocalRepositoryPath(req.getMavenRepo());
        /**
         The classworld is now Created in the NioMavenCompiler and in the DefaultMaven compielr for this reasons:
         problem: https://stackoverflow.com/questions/22410706/error-when-execute-mavencli-in-the-loop-maven-embedder
         problem:https://stackoverflow.com/questions/40587683/invocation-of-mavencli-fails-within-a-maven-plugin
         solution:https://dev.eclipse.org/mhonarc/lists/sisu-users/msg00063.html
         */
        ClassLoader original = Thread.currentThread().getContextClassLoader();
        ClassWorld kieClassWorld = new ClassWorld("plexus.core",
                                                  getClass().getClassLoader());
        int exitCode = cli.doMain(req.getKieCliRequest(),
                                  kieClassWorld);
        Thread.currentThread().setContextClassLoader(original);
        if (exitCode == 0) {
            return new DefaultKieCompilationResponse(Boolean.TRUE);
        } else {
            return new DefaultKieCompilationResponse(Boolean.FALSE);
        }
    }

    @Override
    public KieCompilationResponse buildDefaultCompilationResponse(final Boolean value) {
        return new DefaultKieCompilationResponse(value);
    }

    @Override
    public KieCompilationResponse buildDefaultCompilationResponse(final Boolean value,
                                                                  final List<String> output) {
        return new DefaultKieCompilationResponse(value,
                                                 output);
    }
}
