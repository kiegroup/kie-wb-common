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
package org.kie.workbench.common.services.datamodel.backend.server.cache;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.guvnor.common.services.backend.cache.ClassLoaderCache;
import org.guvnor.common.services.backend.cache.DependenciesCache;
import org.guvnor.common.services.backend.cache.KieModuleMetaDataCache;
import org.guvnor.common.services.project.model.Project;
import org.kie.api.definition.type.Role;
import org.kie.scanner.KieModuleMetaData;
import org.kie.scanner.KieModuleMetaDataImpl;
import org.kie.soup.project.datamodel.commons.util.RawMVELEvaluator;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.oracle.ProjectDataModelOracle;
import org.kie.soup.project.datamodel.oracle.TypeSource;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.kie.workbench.common.services.backend.builder.core.LRUProjectDependenciesClassLoaderCache;
import org.kie.workbench.common.services.backend.builder.core.TypeSourceResolver;
import org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.utils.BuilderUtils;
import org.kie.workbench.common.services.backend.project.MapClassLoader;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ProjectDataModelOracleBuilder;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;

import static org.uberfire.backend.server.util.Paths.convert;

public class ProjectDataModelOracleBuilderProvider {

    private static final Logger log = LoggerFactory.getLogger(ProjectDataModelOracleBuilderProvider.class);

    private ProjectImportsService importsService;
    private PackageNameWhiteListService packageNameWhiteListService;
    private BuilderUtils builderUtils;
    private KieModuleMetaDataCache<Project, KieModuleMetaData> kieModuleMetaDataCache;
    private DependenciesCache<Project> dependenciesCache;
    private ClassLoaderCache<Project> classLoaderCache;
    private LRUProjectDependenciesClassLoaderCache lruProjectDependenciesClassLoaderCache;

    public ProjectDataModelOracleBuilderProvider() {
        //CDI proxy
    }

    @Inject
    public ProjectDataModelOracleBuilderProvider(final PackageNameWhiteListService packageNameWhiteListService,
                                                 final ProjectImportsService importsService,
                                                 final BuilderUtils builderUtils,
                                                 final KieModuleMetaDataCache<Project, KieModuleMetaData> kieModuleMetaDataCache,
                                                 final DependenciesCache<Project> dependenciesCache,
                                                 final ClassLoaderCache<Project> classLoaderCache,
                                                 final LRUProjectDependenciesClassLoaderCache lruProjectDependenciesClassLoaderCache) {
        this.packageNameWhiteListService = packageNameWhiteListService;
        this.importsService = importsService;
        this.builderUtils = builderUtils;
        this.kieModuleMetaDataCache = kieModuleMetaDataCache;
        this.dependenciesCache = dependenciesCache;
        this.classLoaderCache = classLoaderCache;
        this.lruProjectDependenciesClassLoaderCache = lruProjectDependenciesClassLoaderCache;
    }

    public InnerBuilder newBuilder(final KieProject project) {
        KieAFBuilder builder = builderUtils.getBuilder(project);

        if (builder == null) {
            throw new RuntimeException("Isn't possible create a Builder :" + project.getRootPath().toURI() + " because the project isn't a Git FS project");
        }
        KieModuleMetaData kieModuleMetaData = kieModuleMetaDataCache.getEntry(project);

        if (kieModuleMetaData != null) {
            return getInnerBuilderWithAlreadyPresentData(project, kieModuleMetaData);
        } else {
            return runNewBuild(project, builder);
        }
    }

    private InnerBuilder getInnerBuilderWithAlreadyPresentData(KieProject project, KieModuleMetaData kieModuleMetaData) {
        final Set<String> javaResources = new HashSet<>(dependenciesCache.getEntry(project));
        final TypeSourceResolver typeSourceResolver = new TypeSourceResolver(kieModuleMetaData, javaResources);
        return new InnerBuilder(project, kieModuleMetaData, typeSourceResolver);
    }

    private InnerBuilder runNewBuild(KieProject project, KieAFBuilder builder) {
        KieModuleMetaData kieModuleMetaData;
        KieCompilationResponse res = builder.build(Boolean.TRUE, Boolean.FALSE);// this could be readed from the ui
        if (res.isSuccessful() && res.getKieModule().isPresent() && res.getWorkingDir().isPresent()) {
            kieModuleMetaData = new KieModuleMetaDataImpl((InternalKieModule) res.getKieModule().get(),
                                                          res.getProjectDependenciesAsURI().get());
            kieModuleMetaDataCache.setEntry(project, kieModuleMetaData);
            if (res.getProjectDependenciesRaw().isPresent()) {
                final Set<String> javaResources = new HashSet<String>(res.getProjectDependenciesRaw().get());
                final TypeSourceResolver typeSourceResolver = new TypeSourceResolver(kieModuleMetaData,
                                                                                     javaResources);

                return new InnerBuilder(project, kieModuleMetaData, typeSourceResolver);
            } else {
                throw new RuntimeException("Failed to build correctly the project:" + project.toString());
            }
        } else {
            throw new RuntimeException("Failed to build correctly the project:" + project.toString());
        }
    }

    class InnerBuilder {

        private final ProjectDataModelOracleBuilder pdBuilder = ProjectDataModelOracleBuilder.newProjectOracleBuilder(new RawMVELEvaluator());

        private final KieProject project;
        private final KieModuleMetaData kieModuleMetaData;
        private final TypeSourceResolver typeSourceResolver;

        private InnerBuilder(final KieProject project,
                             final KieModuleMetaData kieModuleMetaData,
                             final TypeSourceResolver typeSourceResolver) {
            this.project = project;
            this.kieModuleMetaData = kieModuleMetaData;
            this.typeSourceResolver = typeSourceResolver;
        }

        public ProjectDataModelOracle build() {

            addFromKieModuleMetadata();

            addExternalImports();

            return pdBuilder.build();
        }

        /**
         * The availability of these classes is checked in Builder and failed fast. Here we load them into the DMO
         */
        private void addExternalImports() {
            if (Files.exists(convert(project.getImportsPath()))) {
                for (final Import item : getImports()) {
                    addClass(item);
                }
            }
        }

        private void addFromKieModuleMetadata() {
            final Path prjRoot = convert(project.getRootPath());
            Optional<Set<String>> eventTypes = classLoaderCache.getEventTypes(project);
            //@TODO the eventtypes are only on "regular" classes ?  Not on classes generated by drools from a drl
            WhiteList whiteList = getFilteredPackageNames();
            for (final String packageName : whiteList) {
                pdBuilder.addPackage(packageName);
                addClasses(packageName, kieModuleMetaData.getClasses(packageName), project);
                addClasses(packageName, classLoaderCache.getTargetsProjectDependenciesFiltered(project, packageName), project, TypeSource.JAVA_PROJECT, eventTypes);
            }

            Optional<Map<String, byte[]>> declaredTypes = classLoaderCache.getDeclaredTypes(project);
            if (declaredTypes.isPresent() && !declaredTypes.get().isEmpty()) {
                for (final String packageName : whiteList) {
                    List<Class<?>> clazzes = lruProjectDependenciesClassLoaderCache.getClazz(project, packageName, declaredTypes.get().keySet());
                    if (!clazzes.isEmpty()) {
                        addClass(clazzes, TypeSource.DECLARED);
                    }
                }
            }
        }

        private boolean isEvent(String className, Optional<Set<String>> eventTypes, Class<?> clazz) {
            if (eventTypes.isPresent() && eventTypes.get().size() > 0) {
                return eventTypes.get().contains(className);
            } else {
                if (clazz.isAnnotationPresent(org.kie.api.definition.type.Role.class)) {
                    Role.Type value = clazz.getAnnotation(org.kie.api.definition.type.Role.class).value();
                    return value.equals(Role.Type.EVENT);
                } else {
                    return false;
                }
            }
        }

        /**
         * @return A "white list" of package names that are available for authoring
         */
        private WhiteList getFilteredPackageNames() {
            final Collection<String> pkgs = kieModuleMetaData.getPackages();
            //@TODO change /global with guvnor repo
            final Set<String> filtered = CompilerClassloaderUtils.filterPathClasses(classLoaderCache.getTargetsProjectDependencies(project), "global/");
            pkgs.addAll(filtered);
            return packageNameWhiteListService.filterPackageNames(project, pkgs);
        }

        private void addClasses(final String packageName, final Collection<String> classes, Project projectPath) {
            for (final String className : classes) {
                addClass(packageName, className, projectPath);
            }
        }

        private void addClasses(final String packageName, final Collection<String> classes, Project projectPath, TypeSource typeSource, Optional<Set<String>> eventTypes) {
            for (final String className : classes) {
                addClass(packageName, className, projectPath, typeSource, eventTypes);
            }
        }

        private void addClass(final List<Class<?>> clazzes, TypeSource typeSource) {
            try {
                for (Class clazz : clazzes) {
                    pdBuilder.addClass(clazz,
                                       false,
                                       typeSource);
                }
            } catch (IOException ioe) {
                log.debug(ioe.getMessage());
            }
        }

        private void addClass(final Import item) {
            try {
                Class clazz = this.getClass().getClassLoader().loadClass(item.getType());
                pdBuilder.addClass(clazz,
                                   false,
                                   TypeSource.JAVA_DEPENDENCY);
            } catch (ClassNotFoundException cnfe) {
                //Class resolution would have happened in Builder and reported as warnings so log error here at debug level to avoid flooding logs
                log.debug(cnfe.getMessage());
            } catch (IOException ioe) {
                log.debug(ioe.getMessage());
            }
        }

        private void addClass(final String packageName, final String className, Project project) {
            try {
                Optional<ClassLoader> prjClassloaderOpt = classLoaderCache.getTargetMapClassLoader(project);
                if (prjClassloaderOpt.isPresent()) {

                    final Class clazz = CompilerClassloaderUtils.getClass(packageName,
                                                                          className,
                                                                          (MapClassLoader) prjClassloaderOpt.get());

                    if (clazz != null) {
                        pdBuilder.addClass(clazz,
                                           kieModuleMetaData.getTypeMetaInfo(clazz).isEvent(),
                                           typeSourceResolver.getTypeSource(clazz));
                    }
                }
            } catch (Throwable e) {
                //Class resolution would have happened in Builder and reported as warnings so log error here at debug level to avoid flooding logs
                log.debug(e.getMessage());
            }
        }

        private void addClass(final String packageName, final String className, Project project, TypeSource typeSource, Optional<Set<String>> eventTypes) {
            try {
                Optional<ClassLoader> prjClassloaderOpt = classLoaderCache.getTargetMapClassLoader(project);
                if (prjClassloaderOpt.isPresent()) {

                    final Class clazz = CompilerClassloaderUtils.getClass(packageName,
                                                                          className,
                                                                          (MapClassLoader) prjClassloaderOpt.get());

                    if (clazz != null) {
                        pdBuilder.addClass(clazz, isEvent(className, eventTypes, clazz), typeSource);
                    }
                }
            } catch (Throwable e) {
                //Class resolution would have happened in Builder and reported as warnings so log error here at debug level to avoid flooding logs
                log.debug(e.getMessage());
            }
        }

        private List<Import> getImports() {
            return importsService.load(project.getImportsPath()).getImports().getImports();
        }
    }
}
