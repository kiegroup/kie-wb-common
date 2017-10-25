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

import java.util.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.guvnor.common.services.backend.cache.ClassLoaderCache;
import org.guvnor.common.services.backend.cache.LRUCache;
import org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils;
import org.kie.workbench.common.services.backend.project.MapClassLoader;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
@Named("LRUClassLoaderCache")
public class ClassLoaderCacheLRU extends LRUCache<Path, ClassLoaderTuple> implements ClassLoaderCache {

    @Override
    public synchronized boolean containsPomDependencies(Path projectRootPath) {
        return getEntry(projectRootPath) != null;
    }

    @Override
    public synchronized void clearClassloaderResourcesMap() {
        invalidateCache();
    }


    @Override
    public synchronized void removeProjectDeps(Path projectRootPath) {
        invalidateCache(projectRootPath);
    }



    /** Event types*/

    @Override
    public void addEventTypes(Path projectRootPath, Set<String> eventTypes) {
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            tuple.addEventTypes(eventTypes);
        } else {
            tuple = new ClassLoaderTuple();
            tuple.addEventTypes(eventTypes);
            setEntry(projectRootPath, tuple);
        }
    }

    @Override
    public Optional<Set<String>> getEventTypes(Path projectRootPath) {
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            return Optional.ofNullable(tuple.getEventTypes(projectRootPath));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void removeEventTypes(Path projectRootPath) {
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            tuple.removeEventTypes(projectRootPath);
        }
    }



    /** Declared type **/

    @Override
    public synchronized void addDeclaredTypes(Path projectRootPath, Map<String, byte[]> store) {
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            tuple.addDeclaredTypes(store);
        } else {
            tuple = new ClassLoaderTuple();
            tuple.addDeclaredTypes(store);
            setEntry(projectRootPath, tuple);
        }
    }

    @Override
    public synchronized Optional<Map<String, byte[]>> getDeclaredTypes(Path projectRootPath) {
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            return Optional.ofNullable(tuple.getDeclaredTypes());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void removeDeclaredTypes(Path projectRootPath) {
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            tuple.removeDeclaredTypes(projectRootPath);
        }
    }



    /** Target classloader **/

    @Override
    public synchronized void addTargetMapClassLoader(Path projectRootPath, ClassLoader classLoader) {
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            tuple.addTargetMapClassloader((MapClassLoader)classLoader);
        } else {
            tuple = new ClassLoaderTuple();
            tuple.addTargetMapClassloader((MapClassLoader)classLoader);
            setEntry(projectRootPath, tuple);
        }
    }


    @Override
    public synchronized Optional<ClassLoader> getTargetMapClassLoader(Path projectRootPath) {
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            return tuple.getTargetMapClassloader();
        } else {
            return Optional.empty();
        }
    }


    @Override
    public synchronized void removeTargetMapClassloader(Path projectRootPath) {
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            tuple.removeTargetMapClassloader(projectRootPath);
        }
    }



    /** Dependencies Classloader **/

    @Override
    public synchronized void addDependenciesClassLoader(Path projectRootPath, ClassLoader classLoader) {
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            tuple.addDependenciesClassloader(classLoader);
        } else {
            tuple = new ClassLoaderTuple();
            tuple.addDependenciesClassloader(classLoader);
            setEntry(projectRootPath, tuple);
        }
    }


    @Override
    public synchronized Optional<ClassLoader> getDependenciesClassLoader(Path projectRootPath) {
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            return tuple.getDependenciesClassloader();
        } else {
            return Optional.empty();
        }
    }

    @Override
    public synchronized void removeDependenciesClassloader(Path projectRootPath) {
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            tuple.removeDependenciesClassloader();
        }
    }





    /** Target Prj dependencies **/


    @Override
    public synchronized void addTargetProjectDependencies(Path projectRootPath,
                                                          List<String> uris) {
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            tuple.addTargetProjectDependencies(uris);
        } else {
            tuple = new ClassLoaderTuple();
            tuple.addTargetProjectDependencies(uris);
            setEntry(projectRootPath, tuple);
        }
    }

    @Override
    public void removeTargetProjectDependencies(Path projectRootPath) {
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            tuple.removeTargetProjectDependencies();
        }
    }

    @Override
    public synchronized List<String> getTargetsProjectDependencies(Path projectRootPath) {
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            return tuple.getTargetProjectDependencies();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public synchronized List<String> getTargetsProjectDependenciesFiltered(Path projectRootPath, String packageName) {
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            List<String> allTargetDeps = tuple.getTargetProjectDependencies();
            return CompilerClassloaderUtils.filterClassesByPackage(allTargetDeps, packageName);
        } else {
            return Collections.emptyList();
        }
    }
}
