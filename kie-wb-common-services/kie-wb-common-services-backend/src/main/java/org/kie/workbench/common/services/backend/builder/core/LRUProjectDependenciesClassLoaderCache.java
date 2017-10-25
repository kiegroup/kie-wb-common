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

import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.cache.LRUCache;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.services.backend.builder.af.KieAfBuilderClassloaderUtil;
import org.guvnor.common.services.backend.cache.BuilderCache;
import org.guvnor.common.services.backend.cache.ClassLoaderCache;
import org.guvnor.common.services.backend.cache.DependenciesCache;
import org.guvnor.common.services.backend.cache.GitCache;
import org.guvnor.common.services.backend.cache.KieModuleMetaDataCache;
import org.kie.workbench.common.services.backend.compiler.impl.utils.KieAFBuilderUtil;
import org.kie.workbench.common.services.backend.project.MapClassLoader;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
@Named("LRUProjectDependenciesClassLoaderCache")
public class LRUProjectDependenciesClassLoaderCache extends LRUCache<Path, ClassLoader> {

    private GuvnorM2Repository guvnorM2Repository;
    private Instance< User > identity;
    private GitCache gitCache;
    private BuilderCache builderCache;
    private DependenciesCache dependenciesCache;
    private KieModuleMetaDataCache kieModuleMetaDataCache;
    private ClassLoaderCache classLoaderCache;

    public LRUProjectDependenciesClassLoaderCache() {
    }

    @Inject
    public LRUProjectDependenciesClassLoaderCache(GuvnorM2Repository guvnorM2Repository,
                                                  Instance< User > identity, GitCache gitCache, BuilderCache builderCache,
                                                  KieModuleMetaDataCache kieModuleMetaDataCache, DependenciesCache dependenciesCache, ClassLoaderCache classLoaderCache) {
        this.guvnorM2Repository = guvnorM2Repository;
        this.identity = identity;
        this.gitCache = gitCache;
        this.builderCache = builderCache;
        this.kieModuleMetaDataCache = kieModuleMetaDataCache;
        this.dependenciesCache = dependenciesCache;
        this.classLoaderCache = classLoaderCache;
    }


    public synchronized ClassLoader assertDependenciesClassLoader(final KieProject project, String identity) {
        Optional<Path> nioFsPath = KieAFBuilderUtil.getFSPath(project, gitCache, builderCache, guvnorM2Repository, identity);
        if(nioFsPath.isPresent()){
            ClassLoader classLoader = getEntry(nioFsPath.get());
            if (classLoader == null) {
                Optional<MapClassLoader> opClassloader = buildClassLoader(project, identity);
                if(opClassloader.isPresent()) {
                    setEntry(nioFsPath.get(), opClassloader.get());
                    classLoader = opClassloader.get();
                }
            }
            return classLoader;
        }else{
            List<URL> urls = new ArrayList<>(1);
            URLClassLoader urlClassLoader = new URLClassLoader(urls.toArray(new URL[1]));
            return urlClassLoader;
        }
    }

    public synchronized void setDependenciesClassLoader(final KieProject project,
                                                        ClassLoader classLoader, String identity) {
        Optional<Path> nioFsPAth = KieAFBuilderUtil.getFSPath(project,  gitCache, builderCache,  guvnorM2Repository, identity);
        if(nioFsPAth.isPresent()){
            setEntry(nioFsPAth.get(), classLoader);
        }
    }

    protected Optional<MapClassLoader> buildClassLoader(final KieProject project, String identity) {
        Optional<MapClassLoader> classLoader = KieAfBuilderClassloaderUtil.getProjectClassloader(project,
                                                                                                 gitCache, builderCache,
                                                                                                 kieModuleMetaDataCache,
                                                                                                 dependenciesCache,
                                                                                                 guvnorM2Repository,
                                                                                                 classLoaderCache,
                                                                                                 identity);
        return  classLoader;
    }

    @Override
    public void invalidateCache(Path path) {
        dependenciesCache.removeDependenciesRaw(path);
        kieModuleMetaDataCache.removeKieModuleMetaData(path);
        classLoaderCache.removeTargetMapClassloader(path);
        if(path.endsWith("pom.xml")){
            classLoaderCache.removeDependenciesClassloader(path);
        }
    }

    private String getKey(String projectRootPath){
        return  new StringBuilder().append(projectRootPath.toString()).append("-").append(KieAFBuilderUtil.getIdentifier(identity)).toString();
    }

    public List<Class<?>> getClazz(Path projectRootPath, String packageName, Set<String> declaredTypes) {
        List<Class<?>> clazzes = Collections.EMPTY_LIST;
        ClassLoader classLoader = getEntry(projectRootPath);
        if (classLoader!= null && classLoader instanceof MapClassLoader) {
            MapClassLoader mapClassLoader  = (MapClassLoader) classLoader;
                if(!mapClassLoader.getKeys().isEmpty()){
                    clazzes = new ArrayList<>();
                    for(String key: mapClassLoader.getKeys()){
                        if (key.contains(packageName) && declaredTypes.contains(key)){
                            try{
                                Class clazz = mapClassLoader.loadClass(key.substring(0,key.lastIndexOf(".")).replace("/","."));
                                if(clazz != null) {
                                    clazzes.add(clazz);
                                }
                            }catch (Exception e){
                            //nothing to do
                            }
                        }
                    }
                }

        }
        return clazzes;
    }
}