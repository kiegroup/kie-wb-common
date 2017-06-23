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

package org.kie.workbench.common.services.backend.compiler.internalNioImpl.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.workbench.common.services.backend.compiler.configuration.Decorator;
import org.kie.workbench.common.services.backend.compiler.internalNioImpl.InternalNioImplMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.internalNioImpl.decorators.InternalNioImplJGITCompilerBeforeDecorator;
import org.uberfire.java.nio.file.Path;

public class InternalNioImplMavenCompilerFactory {

    private static Map<Path, InternalNioImplMavenCompiler> compilers = new ConcurrentHashMap<>();

    private InternalNioImplMavenCompilerFactory() {
    }

    /**
     * Provides a Maven compiler decorated with a Decorator Behaviour
     */
    public static InternalNioImplMavenCompiler getCompiler(Path mavenRepo,
                                                           Decorator decorator) {
        InternalNioImplMavenCompiler compiler = compilers.get(mavenRepo);
        if (compiler == null) {
            compiler = createAndAddNewCompiler(mavenRepo,
                                               decorator);
        }
        return compiler;
    }

    private static InternalNioImplMavenCompiler createAndAddNewCompiler(Path mavenRepo,
                                                                        Decorator decorator) {
        switch (decorator) {
            case NONE:
                compilers.put(mavenRepo,
                              new InternalNioImplDefaultMavenCompiler(mavenRepo));
                break;

            case JGIT_BEFORE:
                compilers.put(mavenRepo,
                              new InternalNioImplJGITCompilerBeforeDecorator(new InternalNioImplDefaultMavenCompiler(mavenRepo)));
                break;

            default:
                compilers.put(mavenRepo,
                              new InternalNioImplDefaultMavenCompiler(mavenRepo));
        }
        return compilers.get(mavenRepo);
    }

    /**
     * Delete the compilers creating a new data structure
     */
    public static void deleteCompilers() {
        compilers = new ConcurrentHashMap<>();
    }

    /**
     * Clear the internal data structure
     */
    public static void clearCompilers() {
        compilers.clear();
    }

    public static void removeCompiler(Path mavenRepo) {
        compilers.remove(mavenRepo);
    }

    public static void replaceCompiler(Path mavenRepo,
                                       InternalNioImplMavenCompiler compiler) {
        compilers.replace(mavenRepo,
                          compiler);
    }
}
