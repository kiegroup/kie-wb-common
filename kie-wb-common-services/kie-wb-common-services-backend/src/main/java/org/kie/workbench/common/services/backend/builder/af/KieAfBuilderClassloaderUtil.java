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
package org.kie.workbench.common.services.backend.builder.af;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.kie.api.builder.KieModule;
import org.kie.scanner.KieModuleMetaData;
import org.kie.scanner.KieModuleMetaDataImpl;
import org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.guvnor.common.services.backend.cache.BuilderCache;
import org.guvnor.common.services.backend.cache.ClassLoaderCache;
import org.guvnor.common.services.backend.cache.DependenciesCache;
import org.guvnor.common.services.backend.cache.GitCache;
import org.guvnor.common.services.backend.cache.KieModuleMetaDataCache;
import org.kie.workbench.common.services.backend.compiler.impl.utils.KieAFBuilderUtil;
import org.kie.workbench.common.services.backend.project.MapClassLoader;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;

public class KieAfBuilderClassloaderUtil {

    /**
     * This method return the classloader with the .class founded in the target folder and the UrlClassloader with all .jsrs declared and transitives from poms
     */
    public static Optional<MapClassLoader> getProjectClassloader(KieProject project,
                                                                 GitCache gitCache, BuilderCache builderCache,
                                                                 KieModuleMetaDataCache kieModuleMetaDataCache,
                                                                 DependenciesCache dependenciesCache,
                                                                 GuvnorM2Repository guvnorM2Repository,
                                                                 ClassLoaderCache classLoaderCache,
                                                                 String indentity) {

        Path nioPath = Paths.convert(project.getRootPath());
        KieAFBuilder builder = KieAFBuilderUtil.getKieAFBuilder(project.getRootPath().toURI().toString(), nioPath, gitCache, builderCache, guvnorM2Repository, indentity);

        KieCompilationResponse res = builder.build(!indentity.equals("system"), Boolean.FALSE);//Here the log is not required during the indexing startup

        if (res.isSuccessful() && res.getKieModule().isPresent() && res.getWorkingDir().isPresent()) {

            /* absolute path on the fs */
            Path workingDir = res.getWorkingDir().get();
            /* we collects all the thing produced in the target/classes folders */
            List<String> artifactsFromTargets = getArtifactFromTargets(res, workingDir);
            if (artifactsFromTargets.size() > 0) {
                classLoaderCache.addTargetProjectDependencies(workingDir, artifactsFromTargets);
            } else {
                Optional<List<String>> targetClassesOptional = CompilerClassloaderUtils.getStringsFromTargets(workingDir);
                if (targetClassesOptional.isPresent()) {
                    classLoaderCache.addTargetProjectDependencies(workingDir, targetClassesOptional.get());// check this add
                }
            }
            MapClassLoader projectClassLoader = null;
            final KieModule module = res.getKieModule().get();
            if (module instanceof InternalKieModule) {


                ClassLoader dependenciesClassLoader = addToHolderAndGetDependenciesClassloader(workingDir,
                                                                                               kieModuleMetaDataCache,
                                                                                               dependenciesCache,
                                                                                               classLoaderCache,
                                                                                               res);

                Map<String,byte[]> store = Collections.EMPTY_MAP;
                if(res.getProjectClassLoaderStore().isPresent()){
                    store = res.getProjectClassLoaderStore().get();
                    classLoaderCache.addDeclaredTypes(workingDir, store);
                }
                Set<String> eventTypeClasses = Collections.EMPTY_SET;
                if(res.getEventTypeClasses().isPresent()){
                    eventTypeClasses = res.getEventTypeClasses().get();
                    classLoaderCache.addEventTypes(workingDir, eventTypeClasses);
                }
                /** The integration works with CompilerClassloaderUtils.getMapClasses
                 * This MapClassloader needs the .class from the target folders in a prj produced by the build, as a Map
                 * with a key like this "curriculumcourse/curriculumcourse/Curriculum.class" and the byte[] as a value */
                projectClassLoader = new MapClassLoader(CompilerClassloaderUtils.getMapClasses(workingDir.toString(), store), dependenciesClassLoader);
                classLoaderCache.addTargetMapClassLoader(workingDir, projectClassLoader);
            }
            return Optional.ofNullable(projectClassLoader);
        }
        return Optional.empty();
    }

    private static List<String> getArtifactFromTargets(KieCompilationResponse res, Path workingDir) {
        List<String> artifactsFromTargets = Collections.emptyList();
        if(res.getProjectDependenciesRaw().isPresent()) {
            artifactsFromTargets = res.getProjectDependenciesRaw().get();
            CompilerClassloaderUtils.getStringFromTargets(workingDir);
        }else{
            Optional<List<String>> optional = CompilerClassloaderUtils.getStringFromTargets(workingDir);
            if(optional.isPresent()) {
                artifactsFromTargets = CompilerClassloaderUtils.getStringFromTargets(workingDir).get();
            }
        }
        return artifactsFromTargets;
    }

    private static ClassLoader addToHolderAndGetDependenciesClassloader(Path workingDir,
                                                                        KieModuleMetaDataCache kieModuleMetaDataCache,
                                                                        DependenciesCache dependenciesCache,
                                                                        ClassLoaderCache classLoaderCache,
                                                                        KieCompilationResponse res) {

        Optional<ClassLoader> opDependenciesClassLoader = Optional.empty();
        if (res.getWorkingDir().isPresent()) {
            opDependenciesClassLoader = classLoaderCache.getDependenciesClassLoader(workingDir);
        }

        ClassLoader dependenciesClassLoader;
        if (!opDependenciesClassLoader.isPresent()) {
            dependenciesClassLoader = new URLClassLoader(res.getProjectDependenciesAsURL().get().toArray(new URL[res.getProjectDependenciesAsURL().get().size()]));
        } else {
            dependenciesClassLoader = opDependenciesClassLoader.get();
        }
        classLoaderCache.addDependenciesClassLoader(workingDir, dependenciesClassLoader);

        if (res.getProjectDependenciesRaw().isPresent()) {
            dependenciesCache.addDependenciesRaw(workingDir, res.getProjectDependenciesRaw().get());
        }
        if (res.getProjectDependenciesAsURI().isPresent()) {
            KieModuleMetaData kieModuleMetaData = new KieModuleMetaDataImpl((InternalKieModule) res.getKieModule().get(),
                                                                            res.getProjectDependenciesAsURI().get());
            kieModuleMetaDataCache.addKieModuleMetaData(workingDir, kieModuleMetaData);
        }
        return dependenciesClassLoader;
    }
}
