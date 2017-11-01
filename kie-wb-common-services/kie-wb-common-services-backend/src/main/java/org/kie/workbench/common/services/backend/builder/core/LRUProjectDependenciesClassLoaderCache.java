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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.cache.ClassLoaderCache;
import org.guvnor.common.services.backend.cache.DependenciesCache;
import org.guvnor.common.services.backend.cache.KieModuleMetaDataCache;
import org.guvnor.common.services.backend.cache.LRUCache;
import org.kie.workbench.common.services.backend.builder.af.KieAfBuilderClassloaderUtil;
import org.kie.workbench.common.services.backend.project.MapClassLoader;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.java.nio.file.Path;

import static org.uberfire.backend.server.util.Paths.convert;

@ApplicationScoped
@Named("LRUProjectDependenciesClassLoaderCache")
public class LRUProjectDependenciesClassLoaderCache extends LRUCache<Path, ClassLoader> {

    private KieAfBuilderClassloaderUtil kieAfBuilderClassloaderUtil;
    private DependenciesCache dependenciesCache;
    private KieModuleMetaDataCache kieModuleMetaDataCache;
    private ClassLoaderCache classLoaderCache;

    public LRUProjectDependenciesClassLoaderCache() {
    }

    @Inject
    public LRUProjectDependenciesClassLoaderCache(final KieAfBuilderClassloaderUtil kieAfBuilderClassloaderUtil,
                                                  final KieModuleMetaDataCache kieModuleMetaDataCache,
                                                  final DependenciesCache dependenciesCache,
                                                  final ClassLoaderCache classLoaderCache) {
        this.kieAfBuilderClassloaderUtil = kieAfBuilderClassloaderUtil;
        this.kieModuleMetaDataCache = kieModuleMetaDataCache;
        this.dependenciesCache = dependenciesCache;
        this.classLoaderCache = classLoaderCache;
    }

    public synchronized ClassLoader assertDependenciesClassLoader(final KieProject project) {
        final Path nioFsPath = convert(project.getRootPath());
        ClassLoader classLoader = getEntry(nioFsPath);
        if (classLoader == null) {
            Optional<MapClassLoader> opClassloader = buildClassLoader(project);
            if (opClassloader.isPresent()) {
                setEntry(nioFsPath, opClassloader.get());
                classLoader = opClassloader.get();
            }
        }
        return classLoader;
    }

    public synchronized void setDependenciesClassLoader(final KieProject project,
                                                        final ClassLoader classLoader) {
        final Path nioFsPath = convert(project.getRootPath());
        setEntry(nioFsPath, classLoader);
    }

    protected Optional<MapClassLoader> buildClassLoader(final KieProject project) {
        return kieAfBuilderClassloaderUtil.getProjectClassloader(project,
                                                                 kieModuleMetaDataCache,
                                                                 dependenciesCache,
                                                                 classLoaderCache);
    }

    @Override
    public void invalidateCache(Path path) {
        dependenciesCache.removeDependenciesRaw(path);
        kieModuleMetaDataCache.removeKieModuleMetaData(path);
        classLoaderCache.removeTargetMapClassloader(path);
        if (path.endsWith("pom.xml")) {
            classLoaderCache.removeDependenciesClassloader(path);
        }
    }

    public List<Class<?>> getClazz(Path projectRootPath, String packageName, Set<String> declaredTypes) {
        List<Class<?>> clazzes = Collections.emptyList();
        ClassLoader classLoader = getEntry(projectRootPath);
        if (classLoader != null && classLoader instanceof MapClassLoader) {
            MapClassLoader mapClassLoader = (MapClassLoader) classLoader;
            if (!mapClassLoader.getKeys().isEmpty()) {
                clazzes = new ArrayList<>();
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