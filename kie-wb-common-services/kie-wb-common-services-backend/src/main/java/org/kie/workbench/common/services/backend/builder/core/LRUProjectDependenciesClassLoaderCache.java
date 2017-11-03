/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.builder.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.cache.LRUCache;
import org.guvnor.common.services.backend.cache.ProjectDependenciesClassLoaderCache;
import org.guvnor.common.services.project.model.Project;
import org.kie.workbench.common.services.backend.builder.af.KieAfBuilderClassloaderUtil;
import org.kie.workbench.common.services.backend.project.MapClassLoader;
import org.kie.workbench.common.services.shared.project.KieProject;

@ApplicationScoped
@Named("LRUProjectDependenciesClassLoaderCache")
public class LRUProjectDependenciesClassLoaderCache
        extends LRUCache<Project, ClassLoader>
        implements ProjectDependenciesClassLoaderCache<Project, ClassLoader> {

    private KieAfBuilderClassloaderUtil kieAfBuilderClassloaderUtil;

    public LRUProjectDependenciesClassLoaderCache() {
    }

    @Inject
    public LRUProjectDependenciesClassLoaderCache(final KieAfBuilderClassloaderUtil kieAfBuilderClassloaderUtil) {
        this.kieAfBuilderClassloaderUtil = kieAfBuilderClassloaderUtil;
    }

    public synchronized ClassLoader assertDependenciesClassLoader(final KieProject project) {
        ClassLoader classLoader = getEntry(project);
        if (classLoader == null) {
            Optional<MapClassLoader> opClassloader = buildClassLoader(project);
            if (opClassloader.isPresent()) {
                setEntry(project, opClassloader.get());
                classLoader = opClassloader.get();
            }
        }
        return classLoader;
    }

    protected Optional<MapClassLoader> buildClassLoader(final KieProject project) {
        return kieAfBuilderClassloaderUtil.getProjectClassloader(project);
    }

    public List<Class<?>> getClazz(final Project project,
                                   final String packageName,
                                   final Set<String> declaredTypes) {
        final List<Class<?>> clazzes = new ArrayList<>();
        ClassLoader classLoader = getEntry(project);
        if (classLoader != null && classLoader instanceof MapClassLoader) {
            final MapClassLoader mapClassLoader = (MapClassLoader) classLoader;
            if (!mapClassLoader.getKeys().isEmpty()) {
                for (String key : mapClassLoader.getKeys()) {
                    if (key.contains(packageName) && declaredTypes.contains(key)) {
                        try {
                            Class clazz = mapClassLoader.loadClass(key.substring(0, key.lastIndexOf(".")).replace("/", "."));
                            if (clazz != null) {
                                clazzes.add(clazz);
                            }
                        } catch (Exception e) {
                            //nothing to do
                        }
                    }
                }
            }
        }
        return clazzes;
    }
}