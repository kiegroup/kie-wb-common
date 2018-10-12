/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.Map;
import java.util.Set;

import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationResponse;

public class UnusedDeclaredDepsDecorator <T extends CompilationResponse, C extends AFCompiler<T>> implements CompilerDecorator{

    /**
     * Key used to share the string classpath in the kieMap
     */
    private final String STRING_UNUSED_DEPS_KEY = "stringUnusedDepsKey";

    private C compiler;

    public UnusedDeclaredDepsDecorator(C compiler) {
        this.compiler = compiler;
    }

    public C getCompiler() {
        return compiler;
    }

    @Override
    public Boolean cleanInternalCache() {
        return compiler.cleanInternalCache();
    }

    @Override
    public CompilationResponse compile(CompilationRequest req) {
        T res = compiler.compile(req);
        return handleUnusedDeps(req, res);
    }

    @Override
    public CompilationResponse compile(CompilationRequest req, Map override) {
        T res = (T) compiler.compile(req, override);
        return handleUnusedDeps(req, res);
    }

    private T handleUnusedDeps(CompilationRequest req, T res) {
        T t;
        Map<String, Object> kieMap = req.getMap();
        String classpathKey = req.getRequestUUID() + "." + STRING_UNUSED_DEPS_KEY;
        Object o = kieMap.get(classpathKey);
        if (o != null) {
            Set<String> unusedDeps = (Set<String>) o;
            t = (T) new DefaultCompilationResponse(res.isSuccessful(),
                                                   res.getMavenOutput(),
                                                   req.getInfo().getPrjPath(),
                                                   res.getDependencies(),
                                                   req.getRequestUUID(),
                                                   unusedDeps);
        } else {
            t = (T) new DefaultCompilationResponse(res.isSuccessful(),
                                                   res.getMavenOutput(),
                                                   req.getInfo().getPrjPath(),
                                                   res.getDependencies(),
                                                   req.getRequestUUID());
        }
        return t;
    }
}
