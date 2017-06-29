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
import org.kie.workbench.common.services.backend.compiler.internalNioImpl.decorators.InternalNioImplOutputLogAfterDecorator;
import org.kie.workbench.common.services.backend.compiler.internalNioImpl.decorators.InternalNioKieAfterDecorator;

public class InternalNioImplMavenCompilerFactory {

    private static Map<String, InternalNioImplMavenCompiler> compilers = new ConcurrentHashMap<>();

    private InternalNioImplMavenCompilerFactory() {
    }

    /**
     * Provides a Maven compiler decorated with a Decorator Behaviour
     */
    public static InternalNioImplMavenCompiler getCompiler(Decorator decorator) {
        InternalNioImplMavenCompiler compiler = compilers.get(decorator.name());
        if (compiler == null) {
            compiler = createAndAddNewCompiler(decorator);
        }
        return compiler;
    }

    private static InternalNioImplMavenCompiler createAndAddNewCompiler(Decorator decorator) {
        InternalNioImplMavenCompiler compiler;
        switch (decorator) {
            case NONE:
                compiler = new InternalNioImplDefaultMavenCompiler();
                break;

            case JGIT_BEFORE:
                compiler = new InternalNioImplJGITCompilerBeforeDecorator(new InternalNioImplDefaultMavenCompiler());
                break;

            case LOG_OUTPUT_AFTER:
                compiler = new InternalNioImplOutputLogAfterDecorator(new InternalNioImplDefaultMavenCompiler());
                break;

            case KIE_AFTER:
                compiler = new InternalNioKieAfterDecorator(new InternalNioImplDefaultMavenCompiler());
                break;

            default:
                compiler = new InternalNioImplDefaultMavenCompiler();

        }
        compilers.put(Decorator.NONE.name(),
                      compiler);
        return compiler;
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
}
