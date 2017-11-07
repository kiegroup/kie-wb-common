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
 *
 */

package org.kie.workbench.common.services.backend.builder.cache;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.kie.api.builder.KieModule;
import org.kie.api.definition.type.Role;
import org.kie.scanner.KieModuleMetaDataImpl;
import org.kie.soup.project.datamodel.commons.util.RawMVELEvaluator;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.soup.project.datamodel.oracle.ProjectDataModelOracle;
import org.kie.soup.project.datamodel.oracle.TypeSource;
import org.kie.workbench.common.services.backend.builder.af.impl.DefaultKieAFBuilder;
import org.kie.workbench.common.services.backend.builder.core.TypeSourceResolver;
import org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.project.MapClassLoader;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ProjectDataModelOracleBuilder;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;

import static org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils.filterClassesByPackage;
import static org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils.filterPathClasses;
import static org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils.getStringsFromTargets;
import static org.kie.workbench.common.services.backend.compiler.impl.utils.MavenOutputConverter.convertIntoBuildResults;
import static org.uberfire.backend.server.util.Paths.convert;

public class ProjectBuildData {

    private static final Logger log = LoggerFactory.getLogger(ProjectBuildData.class);

    public enum TypeOfInvalidation {
        POM,
        OBSERVABLE,
        ALL
    }

    private static final String ERROR_LEVEL = "ERROR";

    private final IOService ioService;
    private final ProjectImportsService importsService;
    private final PackageNameWhiteListService packageNameWhiteListService;

    private final KieProject project;
    private final String mavenRepo;

    private DefaultKieAFBuilder builder;

    private final Map<String, byte[]> declaredTypes = new HashMap<>();
    private final List<String> targetProjectDependencies = new ArrayList<>();
    private final Set<String> eventTypes = new HashSet<>();
    private MapClassLoader targetClassloader;
    private ClassLoader dependenciesClassloader;

    private ProjectDataModelOracle projectDataModelOracle;
    private KieModuleMetaDataImpl kieModuleMetaData;
    private Set<String> dependenciesRaw = new HashSet<>();

    public ProjectBuildData(final IOService ioService,
                            final ProjectImportsService importsService,
                            final PackageNameWhiteListService packageNameWhiteListService,
                            final KieProject project,
                            final String mavenRepo) {
        this.ioService = ioService;
        this.importsService = importsService;
        this.packageNameWhiteListService = packageNameWhiteListService;
        this.project = project;
        this.mavenRepo = mavenRepo;
    }

    public List<ValidationMessage> validate(final Path resourcePath,
                                            final InputStream inputStream) {
        return null;
    }

    public BuildResults build() {
        final DefaultKieAFBuilder builder = getBuilder();

        final KieCompilationResponse res = builder.build();
        final BuildResults br = convertIntoBuildResults(res.getMavenOutput().get(),
                                                        ERROR_LEVEL,
                                                        builder.getGITURI(),
                                                        builder.getInfo().getPrjPath().getParent().toString());

        return br;
    }

    public BuildResults buildAndInstall() {
        final DefaultKieAFBuilder builder = getBuilder();

        KieCompilationResponse res = builder.buildAndInstall(builder.getInfo().getPrjPath().toString(),
                                                             mavenRepo);
        return convertIntoBuildResults(res.getMavenOutput().get());
    }

    public boolean isBuilt() {
        return builder != null;
    }

    public ClassLoader getClassLoader() {
        if (targetClassloader == null) {
            buildClassLoader();
        }
        return targetClassloader;
    }

    private void buildClassLoader() {
        final DefaultKieAFBuilder builder = getBuilder();
        final KieCompilationResponse res = builder.build();

        if (res.isSuccessful() && res.getKieModule().isPresent() && res.getWorkingDir().isPresent()) {
            /* absolute path on the fs */
            final Path workingDir = res.getWorkingDir().get();
            /* we collects all the thing produced in the target/classes folders */
            final List<String> artifactsFromTargets = getArtifactFromTargets(res, workingDir);
            if (artifactsFromTargets.size() > 0) {
                targetProjectDependencies.addAll(artifactsFromTargets);
            } else {
                Optional<List<String>> targetClassesOptional = getStringsFromTargets(workingDir);
                // check this add
                targetClassesOptional.ifPresent(targetProjectDependencies::addAll);
            }
            MapClassLoader projectClassLoader = null;
            final KieModule module = res.getKieModule().get();
            if (module instanceof InternalKieModule) {
                final ClassLoader dependenciesClassLoader = addToHolderAndGetDependenciesClassloader(res);

                Map<String, byte[]> store = Collections.emptyMap();
                if (res.getProjectClassLoaderStore().isPresent()) {
                    store = res.getProjectClassLoaderStore().get();
                    declaredTypes.putAll(store);
                }
                Set<String> eventTypeClasses = Collections.emptySet();
                if (res.getEventTypeClasses().isPresent()) {
                    eventTypeClasses = res.getEventTypeClasses().get();
                    eventTypes.addAll(eventTypeClasses);
                }
                /** The integration works with CompilerClassloaderUtils.getMapClasses
                 * This MapClassloader needs the .class from the target folders in a prj produced by the build, as a Map
                 * with a key like this "curriculumcourse/curriculumcourse/Curriculum.class" and the byte[] as a value */
                targetClassloader = new MapClassLoader(CompilerClassloaderUtils.getMapClasses(workingDir.toString(), store), dependenciesClassLoader);
            }
        }
    }

    private List<String> getArtifactFromTargets(final KieCompilationResponse res,
                                                final Path workingDir) {
        final List<String> artifactsFromTargets;
        if (res.getProjectDependenciesRaw().isPresent()) {
            artifactsFromTargets = res.getProjectDependenciesRaw().get();
            CompilerClassloaderUtils.getStringFromTargets(workingDir);
        } else {
            final Optional<List<String>> optional = CompilerClassloaderUtils.getStringFromTargets(workingDir);
            if (optional.isPresent()) {
                artifactsFromTargets = CompilerClassloaderUtils.getStringFromTargets(workingDir).get();
            } else {
                return Collections.emptyList();
            }
        }
        return artifactsFromTargets;
    }

    public ProjectDataModelOracle getProjectDataModelOracle() {
        if (projectDataModelOracle == null) {
            projectDataModelOracle = buildProjectDataModelOracle();
        }
        return projectDataModelOracle;
    }

    public PackageDataModelOracle getPackageDataModelOracle() {
        return null;
    }

    public void invalidate(final TypeOfInvalidation invalidation) {
        invalidate(EnumSet.of(invalidation));
    }

    private void invalidate(EnumSet<TypeOfInvalidation> enumSet) {
        if (enumSet.contains(TypeOfInvalidation.POM) || enumSet.contains(TypeOfInvalidation.ALL)) {
            //clear dependencies
        }
        if (enumSet.contains(TypeOfInvalidation.OBSERVABLE) || enumSet.contains(TypeOfInvalidation.ALL)) {
            //clear internal classloader
        }
    }

    private final DefaultKieAFBuilder getBuilder() {
        if (builder == null) {
            builder = new DefaultKieAFBuilder(convert(project.getRootPath()), mavenRepo);
        }
        return builder;
    }

    //from KieAfBuilderClassloaderUtil
    private ClassLoader addToHolderAndGetDependenciesClassloader(final KieCompilationResponse res) {
        if (dependenciesClassloader == null) {
            dependenciesClassloader = new URLClassLoader(res.getProjectDependenciesAsURL().get().toArray(new URL[res.getProjectDependenciesAsURL().get().size()]));
        }

        return dependenciesClassloader;
    }

    private ProjectDataModelOracle buildProjectDataModelOracle() {
        return new InnerBuilder().build();
    }

    class InnerBuilder {

        private final ProjectDataModelOracleBuilder pdBuilder = ProjectDataModelOracleBuilder.newProjectOracleBuilder(new RawMVELEvaluator());

        private final TypeSourceResolver typeSourceResolver;

        private InnerBuilder() {
            this.typeSourceResolver = new TypeSourceResolver(kieModuleMetaData, dependenciesRaw);
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
            if (Files.exists(Paths.convert(project.getImportsPath()))) {
                for (final Import item : getImports()) {
                    addClass(item);
                }
            }
        }

        private void addFromKieModuleMetadata() {
            //@TODO the eventtypes are only on "regular" classes ?  Not on classes generated by drools from a drl
            final WhiteList whiteList = getFilteredPackageNames();
            for (final String packageName : whiteList) {
                pdBuilder.addPackage(packageName);
                addClasses(packageName,
                           kieModuleMetaData.getClasses(packageName));
                addClasses(packageName,
                           filterClassesByPackage(targetProjectDependencies, packageName),
                           TypeSource.JAVA_PROJECT,
                           eventTypes);
            }

            if (!declaredTypes.isEmpty()) {
                for (final String packageName : whiteList) {
                    List<Class<?>> clazzes = getClazz(packageName, declaredTypes.keySet());
                    if (!clazzes.isEmpty()) {
                        addClass(clazzes, TypeSource.DECLARED);
                    }
                }
            }
        }

        private boolean isEvent(final String className,
                                final Set<String> eventTypes,
                                final Class<?> clazz) {
            if (!eventTypes.isEmpty()) {
                return eventTypes.contains(className);
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
            pkgs.addAll(filterPathClasses(targetProjectDependencies, "global/"));
            return packageNameWhiteListService.filterPackageNames(project, pkgs);
        }

        private void addClasses(final String packageName,
                                final Collection<String> classes) {
            for (final String className : classes) {
                addClass(packageName, className);
            }
        }

        private void addClasses(final String packageName, final Collection<String> classes, TypeSource typeSource, Set<String> eventTypes) {
            for (final String className : classes) {
                addClass(packageName, className, typeSource, eventTypes);
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

        private void addClass(final String packageName, final String className) {
            try {
                if (targetClassloader != null) {
                    final Class clazz = CompilerClassloaderUtils.getClass(packageName,
                                                                          className,
                                                                          targetClassloader);
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

        private void addClass(final String packageName, final String className, TypeSource typeSource, Set<String> eventTypes) {
            try {
                if (targetClassloader != null) {

                    final Class clazz = CompilerClassloaderUtils.getClass(packageName,
                                                                          className,
                                                                          targetClassloader);

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

    private List<Class<?>> getClazz(final String packageName, final Set<String> declaredTypes) {
        if (targetClassloader != null) {
            if (!targetClassloader.getKeys().isEmpty()) {
                final List<Class<?>> clazzes = new ArrayList<>();
                for (String key : targetClassloader.getKeys()) {
                    if (key.contains(packageName) && declaredTypes.contains(key)) {
                        try {
                            Class clazz = targetClassloader.loadClass(key.substring(0, key.lastIndexOf(".")).replace("/", "."));
                            if (clazz != null) {
                                clazzes.add(clazz);
                            }
                        } catch (Exception e) {
                            //nothing to do
                        }
                    }
                }
                return clazzes;
            }
        }

        return Collections.emptyList();
    }
}
