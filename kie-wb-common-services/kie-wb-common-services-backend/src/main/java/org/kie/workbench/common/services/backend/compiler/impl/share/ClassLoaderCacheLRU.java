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
package org.kie.workbench.common.services.backend.compiler.impl.share;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.guvnor.common.services.backend.cache.ClassLoaderCache;
import org.guvnor.common.services.backend.cache.LRUCache;
import org.guvnor.common.services.project.model.Project;
import org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils;
import org.kie.workbench.common.services.backend.project.MapClassLoader;

@ApplicationScoped
@Named("LRUClassLoaderCache")
public class ClassLoaderCacheLRU extends LRUCache<Project, ClassLoaderTuple> implements ClassLoaderCache<Project> {

    @Override
    public synchronized boolean containsPomDependencies(Project project) {
        return getEntry(project) != null;
    }

    @Override
    public synchronized void clearClassloaderResourcesMap() {
        invalidateCache();
    }

    @Override
    public synchronized void removeProjectDeps(Project project) {
        invalidateCache(project);
    }

    /**
     * Event types
     */

    @Override
    public void addEventTypes(Project project, Set<String> eventTypes) {
        ClassLoaderTuple tuple = getEntry(project);
        if (tuple != null) {
            tuple.addEventTypes(eventTypes);
        } else {
            tuple = new ClassLoaderTuple();
            tuple.addEventTypes(eventTypes);
            setEntry(project, tuple);
        }
    }

    @Override
    public Optional<Set<String>> getEventTypes(Project project) {
        ClassLoaderTuple tuple = getEntry(project);
        if (tuple != null) {
            return Optional.ofNullable(tuple.getEventTypes());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void removeEventTypes(Project project) {
        ClassLoaderTuple tuple = getEntry(project);
        if (tuple != null) {
            tuple.removeEventTypes();
        }
    }

    /**
     * Declared type
     **/

    @Override
    public synchronized void addDeclaredTypes(Project project, Map<String, byte[]> store) {
        ClassLoaderTuple tuple = getEntry(project);
        if (tuple != null) {
            tuple.addDeclaredTypes(store);
        } else {
            tuple = new ClassLoaderTuple();
            tuple.addDeclaredTypes(store);
            setEntry(project, tuple);
        }
    }

    @Override
    public synchronized Optional<Map<String, byte[]>> getDeclaredTypes(Project project) {
        ClassLoaderTuple tuple = getEntry(project);
        if (tuple != null) {
            return Optional.ofNullable(tuple.getDeclaredTypes());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void removeDeclaredTypes(Project project) {
        ClassLoaderTuple tuple = getEntry(project);
        if (tuple != null) {
            tuple.removeDeclaredTypes();
        }
    }

    /**
     * Target classloader
     **/

    @Override
    public synchronized void addTargetMapClassLoader(Project project, ClassLoader classLoader) {
        ClassLoaderTuple tuple = getEntry(project);
        if (tuple != null) {
            tuple.addTargetMapClassloader((MapClassLoader) classLoader);
        } else {
            tuple = new ClassLoaderTuple();
            tuple.addTargetMapClassloader((MapClassLoader) classLoader);
            setEntry(project, tuple);
        }
    }

    @Override
    public synchronized Optional<ClassLoader> getTargetMapClassLoader(Project project) {
        ClassLoaderTuple tuple = getEntry(project);
        if (tuple != null) {
            return tuple.getTargetMapClassloader();
        } else {
            return Optional.empty();
        }
    }

    @Override
    public synchronized void removeTargetMapClassloader(Project project) {
        ClassLoaderTuple tuple = getEntry(project);
        if (tuple != null) {
            tuple.removeTargetMapClassloader();
        }
    }

    /**
     * Dependencies Classloader
     **/

    @Override
    public synchronized void addDependenciesClassLoader(Project project, ClassLoader classLoader) {
        ClassLoaderTuple tuple = getEntry(project);
        if (tuple != null) {
            tuple.addDependenciesClassloader(classLoader);
        } else {
            tuple = new ClassLoaderTuple();
            tuple.addDependenciesClassloader(classLoader);
            setEntry(project, tuple);
        }
    }

    @Override
    public synchronized Optional<ClassLoader> getDependenciesClassLoader(Project project) {
        ClassLoaderTuple tuple = getEntry(project);
        if (tuple != null) {
            return tuple.getDependenciesClassloader();
        } else {
            return Optional.empty();
        }
    }

    @Override
    public synchronized void removeDependenciesClassloader(Project project) {
        ClassLoaderTuple tuple = getEntry(project);
        if (tuple != null) {
            tuple.removeDependenciesClassloader();
        }
    }

    /**
     * Target Prj dependencies
     **/

    @Override
    public synchronized void addTargetProjectDependencies(Project project,
                                                          List<String> uris) {
        ClassLoaderTuple tuple = getEntry(project);
        if (tuple != null) {
            tuple.addTargetProjectDependencies(uris);
        } else {
            tuple = new ClassLoaderTuple();
            tuple.addTargetProjectDependencies(uris);
            setEntry(project, tuple);
        }
    }

    @Override
    public void removeTargetProjectDependencies(Project project) {
        ClassLoaderTuple tuple = getEntry(project);
        if (tuple != null) {
            tuple.removeTargetProjectDependencies();
        }
    }

    @Override
    public synchronized List<String> getTargetsProjectDependencies(Project project) {
        ClassLoaderTuple tuple = getEntry(project);
        if (tuple != null) {
            return tuple.getTargetProjectDependencies();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public synchronized List<String> getTargetsProjectDependenciesFiltered(Project project, String packageName) {
        ClassLoaderTuple tuple = getEntry(project);
        if (tuple != null) {
            List<String> allTargetDeps = tuple.getTargetProjectDependencies();
            return CompilerClassloaderUtils.filterClassesByPackage(allTargetDeps, packageName);
        } else {
            return Collections.emptyList();
        }
    }
}
