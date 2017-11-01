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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.guvnor.common.services.backend.cache.ClassLoaderCache;
import org.guvnor.common.services.backend.cache.DependenciesCache;
import org.guvnor.common.services.backend.cache.KieModuleMetaDataCache;
import org.kie.api.builder.KieModule;
import org.kie.scanner.KieModuleMetaData;
import org.kie.scanner.KieModuleMetaDataImpl;
import org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.utils.BuilderUtils;
import org.kie.workbench.common.services.backend.project.MapClassLoader;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.java.nio.file.Path;

import static org.uberfire.backend.server.util.Paths.convert;

@ApplicationScoped
public class KieAfBuilderClassloaderUtil {

    @Inject
    private BuilderUtils builderUtils;

    /**
     * This method return the classloader with the .class founded in the target folder and the UrlClassloader with all .jsrs declared and transitives from poms
     */
    public Optional<MapClassLoader> getProjectClassloader(final KieProject project,
                                                          final KieModuleMetaDataCache kieModuleMetaDataCache,
                                                          final DependenciesCache dependenciesCache,
                                                          final ClassLoaderCache classLoaderCache) {

        final KieAFBuilder builder = builderUtils.getBuilder(project);

        if (builder == null) {
            return Optional.empty();
        }

        //todo: porcelli
        final KieCompilationResponse res = builder.build(Boolean.TRUE, Boolean.FALSE);//Here the log is not required during the indexing startup

        if (res.isSuccessful() && res.getKieModule().isPresent() && res.getWorkingDir().isPresent()) {

            /* absolute path on the fs */
            final Path projectRootPath = convert(project.getRootPath());
//            Path workingDir = res.getWorkingDir().get();
            /* we collects all the thing produced in the target/classes folders */
            List<String> artifactsFromTargets = getArtifactFromTargets(res, res.getWorkingDir().get());
            if (artifactsFromTargets.size() > 0) {
                classLoaderCache.addTargetProjectDependencies(projectRootPath, artifactsFromTargets);
            } else {
                Optional<List<String>> targetClassesOptional = CompilerClassloaderUtils.getStringsFromTargets(res.getWorkingDir().get());
                // check this add
                targetClassesOptional.ifPresent(strings -> classLoaderCache.addTargetProjectDependencies(projectRootPath, strings));
            }
            MapClassLoader projectClassLoader = null;
            final KieModule module = res.getKieModule().get();
            if (module instanceof InternalKieModule) {

                ClassLoader dependenciesClassLoader = addToHolderAndGetDependenciesClassloader(projectRootPath,
                                                                                               kieModuleMetaDataCache,
                                                                                               dependenciesCache,
                                                                                               classLoaderCache,
                                                                                               res);

                Map<String, byte[]> store = Collections.emptyMap();
                if (res.getProjectClassLoaderStore().isPresent()) {
                    store = res.getProjectClassLoaderStore().get();
                    classLoaderCache.addDeclaredTypes(projectRootPath, store);
                }
                Set<String> eventTypeClasses = Collections.emptySet();
                if (res.getEventTypeClasses().isPresent()) {
                    eventTypeClasses = res.getEventTypeClasses().get();
                    classLoaderCache.addEventTypes(projectRootPath, eventTypeClasses);
                }
                /** The integration works with CompilerClassloaderUtils.getMapClasses
                 * This MapClassloader needs the .class from the target folders in a prj produced by the build, as a Map
                 * with a key like this "curriculumcourse/curriculumcourse/Curriculum.class" and the byte[] as a value */
                projectClassLoader = new MapClassLoader(CompilerClassloaderUtils.getMapClasses(res.getWorkingDir().get().toString(), store), dependenciesClassLoader);
                classLoaderCache.addTargetMapClassLoader(projectRootPath, projectClassLoader);
            }
            return Optional.ofNullable(projectClassLoader);
        }
        return Optional.empty();
    }

    private List<String> getArtifactFromTargets(KieCompilationResponse res, Path workingDir) {
        List<String> artifactsFromTargets = Collections.emptyList();
        if (res.getProjectDependenciesRaw().isPresent()) {
            artifactsFromTargets = res.getProjectDependenciesRaw().get();
            CompilerClassloaderUtils.getStringFromTargets(workingDir);
        } else {
            Optional<List<String>> optional = CompilerClassloaderUtils.getStringFromTargets(workingDir);
            if (optional.isPresent()) {
                artifactsFromTargets = CompilerClassloaderUtils.getStringFromTargets(workingDir).get();
            }
        }
        return artifactsFromTargets;
    }

    private ClassLoader addToHolderAndGetDependenciesClassloader(final Path projectRootPath,
                                                                 final KieModuleMetaDataCache kieModuleMetaDataCache,
                                                                 final DependenciesCache dependenciesCache,
                                                                 final ClassLoaderCache classLoaderCache,
                                                                 final KieCompilationResponse res) {

        Optional<ClassLoader> opDependenciesClassLoader = Optional.empty();
        if (res.getWorkingDir().isPresent()) {
            opDependenciesClassLoader = classLoaderCache.getDependenciesClassLoader(projectRootPath);
        }

        ClassLoader dependenciesClassLoader;
        if (!opDependenciesClassLoader.isPresent()) {
            dependenciesClassLoader = new URLClassLoader(res.getProjectDependenciesAsURL().get().toArray(new URL[res.getProjectDependenciesAsURL().get().size()]));
        } else {
            dependenciesClassLoader = opDependenciesClassLoader.get();
        }
        classLoaderCache.addDependenciesClassLoader(projectRootPath, dependenciesClassLoader);

        if (res.getProjectDependenciesRaw().isPresent()) {
            dependenciesCache.addDependenciesRaw(projectRootPath, res.getProjectDependenciesRaw().get());
        }
        if (res.getProjectDependenciesAsURI().isPresent()) {
            KieModuleMetaData kieModuleMetaData = new KieModuleMetaDataImpl((InternalKieModule) res.getKieModule().get(),
                                                                            res.getProjectDependenciesAsURI().get());
            kieModuleMetaDataCache.addKieModuleMetaData(projectRootPath, kieModuleMetaData);
        }
        return dependenciesClassLoader;
    }
}
