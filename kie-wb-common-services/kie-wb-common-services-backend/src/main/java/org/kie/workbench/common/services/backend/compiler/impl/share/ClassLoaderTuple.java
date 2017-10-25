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

import org.kie.workbench.common.services.backend.project.MapClassLoader;
import org.uberfire.java.nio.file.Path;

class ClassLoaderTuple {

    private Map<String, byte[]> declaredTypes;

    private List<String> targetProjectDependencies;

    private MapClassLoader targetClassloader;

    private ClassLoader dependenciesClassloader;

    private Set<String> eventTypes;

    public ClassLoaderTuple() {
        targetProjectDependencies = new ArrayList<>();
    }

    /** Event types*/

    public void addEventTypes(Set<String> eventTypes){
        this.eventTypes = eventTypes;
    }

    public Set<String> getEventTypes(Path project) { return eventTypes;}

    public void removeEventTypes(Path projectPath){ this.eventTypes = null; }


    /** Declared types*/

    public void addDeclaredTypes(Map<String, byte[]> declaredTypes){
        this.declaredTypes = declaredTypes;
    }

    public Map<String, byte[]> getDeclaredTypes(){ return declaredTypes; }

    public void removeDeclaredTypes(Path projectPath){ this.declaredTypes = null; }


    /** Target classloader*/

    public Optional<ClassLoader> getTargetMapClassloader() {
        return Optional.ofNullable(targetClassloader);
    }

    public void addTargetMapClassloader(MapClassLoader targetClassloader) {
        this.targetClassloader = targetClassloader;
    }

    public void removeTargetMapClassloader(Path project) {
        this.targetClassloader = null;
    }


    /** Dependencies classloader*/

    public void addDependenciesClassloader(ClassLoader dependenciesClassloader) {
        this.dependenciesClassloader = dependenciesClassloader;
    }

    public Optional<ClassLoader> getDependenciesClassloader() {
        return Optional.ofNullable(dependenciesClassloader);
    }

    public void removeDependenciesClassloader() {
        this.dependenciesClassloader = null;
    }



    /** Target Project dependencies*/


    public List<String> getTargetProjectDependencies() { return targetProjectDependencies; }

    public void addTargetProjectDependencies(List<String> resources) {
        targetProjectDependencies.addAll(resources);
    }

    public void removeTargetProjectDependencies(){
        this.targetProjectDependencies = null;
    }
}
