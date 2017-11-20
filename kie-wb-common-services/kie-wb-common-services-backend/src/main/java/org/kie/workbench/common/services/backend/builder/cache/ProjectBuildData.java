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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import javax.enterprise.inject.Instance;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.guvnor.common.services.backend.file.FileDiscoveryService;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.definition.type.Role;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.scanner.KieModuleMetaData;
import org.kie.scanner.KieModuleMetaDataImpl;
import org.kie.soup.project.datamodel.commons.util.MVELEvaluator;
import org.kie.soup.project.datamodel.commons.util.RawMVELEvaluator;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.soup.project.datamodel.oracle.ProjectDataModelOracle;
import org.kie.soup.project.datamodel.oracle.TypeSource;
import org.kie.workbench.common.services.backend.builder.af.impl.DefaultKieAFBuilder;
import org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.file.EnumerationsFileFilter;
import org.kie.workbench.common.services.backend.file.GlobalsFileFilter;
import org.kie.workbench.common.services.backend.project.MapClassLoader;
import org.kie.workbench.common.services.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ProjectDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.spi.DataModelExtension;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.StreamSupport.stream;
import static org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils.filterClassesByPackage;
import static org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils.filterPathClasses;
import static org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils.getMapClasses;
import static org.kie.workbench.common.services.backend.compiler.impl.utils.MavenOutputConverter.convertIntoBuildResults;
import static org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ProjectDataModelOracleBuilder.newProjectOracleBuilder;
import static org.uberfire.backend.server.util.Paths.convert;

public class ProjectBuildData {

    private final ReentrantLock lock = new ReentrantLock();

    private static final String ERROR_LEVEL = "ERROR";

    private static final MapClassLoader EMPTY_CLASSLOADER = new MapClassLoader(Collections.emptyMap(), new URLClassLoader(new URL[0], ClassLoader.getSystemClassLoader().getParent()));
    private static final ProjectDataModelOracle EMPTY_PROJECT_ORACLE = newProjectOracleBuilder(new RawMVELEvaluator()).build();

    private static final Logger log = LoggerFactory.getLogger(ProjectBuildData.class);

    private static final DirectoryStream.Filter<org.uberfire.java.nio.file.Path> FILTER_ENUMERATIONS = new EnumerationsFileFilter();

    private static final DirectoryStream.Filter<org.uberfire.java.nio.file.Path> FILTER_GLOBALS = new GlobalsFileFilter();

    private final IOService ioService;
    private final ProjectImportsService importsService;
    private final PackageNameWhiteListService packageNameWhiteListService;
    private final FileDiscoveryService fileDiscoveryService;
    private final Instance<DataModelExtension> dataModelExtensionsProvider;
    private final MVELEvaluator evaluator;

    private final KieProject project;
    private final String mavenRepo;

    private DefaultKieAFBuilder builder;
    private KieCompilationResponse response;
    private ReleaseId releaseId;

    private KieModuleMetaData kieModuleMetaData;

    private final Map<String, byte[]> declaredTypes = new HashMap<>();
    private final Set<String> eventTypes = new HashSet<>();
    private Path artifact;
    private Path workingDir;

    private Set<String> targetArtifacts = new HashSet<>();

    private MapClassLoader classLoader;

    private ClassLoader dependenciesClassLoader;
    private Set<String> dependencies = new HashSet<>();

    private ProjectDataModelOracle projectDataModelOracle;
    private Map<String, PackageDataModelOracle> packageDataModelOracle = new HashMap<>();
    private AtomicBoolean isReBuild = new AtomicBoolean(false);

    public ProjectBuildData(final IOService ioService,
                            final ProjectImportsService importsService,
                            final PackageNameWhiteListService packageNameWhiteListService,
                            final FileDiscoveryService fileDiscoveryService,
                            final Instance<DataModelExtension> dataModelExtensionsProvider,
                            final MVELEvaluator evaluator,
                            final KieProject project,
                            final String mavenRepo) {
        this.ioService = ioService;
        this.importsService = importsService;
        this.packageNameWhiteListService = packageNameWhiteListService;
        this.fileDiscoveryService = fileDiscoveryService;
        this.dataModelExtensionsProvider = dataModelExtensionsProvider;
        this.evaluator = evaluator;
        this.project = project;
        this.mavenRepo = mavenRepo;
    }

    public List<ValidationMessage> validate(final Path resourcePath,
                                            final InputStream inputStream) {
        lock.lock();
        try {
            return Collections.emptyList();
        } finally {
            lock.unlock();
        }
    }

    public BuildResults build() {
        lock.lock();
        try {
            final KieCompilationResponse res = getCompilationResponse();
            final BuildResults br = convertIntoBuildResults(res.getMavenOutput(),
                                                            convert(project.getRootPath()),
                                                            res.getWorkingDir().get().getParent().toString());

            return br;
        } finally {
            lock.unlock();
        }
    }

    public BuildResults buildAndInstall() {
        lock.lock();
        try {
            final DefaultKieAFBuilder builder = getBuilder();

            final KieCompilationResponse res = builder.buildAndInstall(builder.getInfo().getPrjPath().toString(),
                                                                       mavenRepo);
            return convertIntoBuildResults(res.getMavenOutput());
        } finally {
            lock.unlock();
        }
    }

    public boolean isBuilt() {
        return builder != null;
    }

    public ClassLoader getClassLoader() {
        if (classLoader == null) {
            return doGetClassLoader();
        }
        return classLoader;
    }

    private ClassLoader doGetClassLoader() {
        lock.lock();
        try {
            if (classLoader == null || isReBuild.get()) {
                classLoader = buildClassLoader();
            }
            return classLoader;
        } finally {
            lock.unlock();
        }
    }

    private MapClassLoader buildClassLoader() {
        lock.lock();
        try {
            final KieCompilationResponse res = getCompilationResponse();

            if (res.isSuccessful()) {

                res.getKieModule().ifPresent(km -> {
                    kieModuleMetaData = new KieModuleMetaDataImpl((InternalKieModule) km,
                                                                  res.getDependenciesAsURI());
                    releaseId = km.getReleaseId();
                });

                /* absolute path on the fs */
                workingDir = res.getWorkingDir().get();
                artifact = res.getWorkingDir().map(w -> {
                    final Path result = w.resolve("target").resolve(project.getPom().getGav().getArtifactId() + "-" + project.getPom().getGav().getVersion() + ".jar");
                    if (result.toFile().exists()) {
                        return result;
                    }
                    return null;
                }).orElse(null);

                /* we collects all the thing produced in the target/classes folders */
                targetArtifacts = new HashSet<>(res.getTargetContent().stream().filter(s -> !s.endsWith(".jar")).collect(toSet()));
                final ClassLoader dependenciesClassLoader = getDependenciesClassLoader(res);
                final Map<String, byte[]> store = new HashMap<>(res.getProjectClassLoaderStore());
                declaredTypes.putAll(store);
                eventTypes.addAll(res.getEventTypeClasses());
                /** The integration works with CompilerClassloaderUtils.getMapClasses
                 * This MapClassloader needs the .class from the target folders in a prj produced by the build, as a Map
                 * with a key like this "curriculumcourse/curriculumcourse/Curriculum.class" and the byte[] as a value */
                return new MapClassLoader(getMapClasses(workingDir.toString(), store), dependenciesClassLoader);
            }
            return EMPTY_CLASSLOADER;
        } finally {
            lock.unlock();
        }
    }

    public ProjectDataModelOracle getProjectDataModelOracle() {
        if (projectDataModelOracle == null) {
            return doGetProjectDataModelOracle();
        }
        return projectDataModelOracle;
    }

    private ProjectDataModelOracle doGetProjectDataModelOracle() {
        lock.lock();
        try {
            if (projectDataModelOracle == null || isReBuild.get()) {
                projectDataModelOracle = buildProjectDataModelOracle();
            }
            return projectDataModelOracle;
        } finally {
            lock.unlock();
        }
    }

    public PackageDataModelOracle getPackageDataModelOracle(final Package pkg) {
        if (!packageDataModelOracle.containsKey(pkg.getPackageName())) {
            return doGetPackageDataModelOracle(pkg);
        }
        return packageDataModelOracle.get(pkg.getPackageName());
    }

    private PackageDataModelOracle doGetPackageDataModelOracle(final Package pkg) {
        lock.lock();
        try {
            if (!packageDataModelOracle.containsKey(pkg.getPackageName())) {
                packageDataModelOracle.put(pkg.getPackageName(), buildPackageDataModelOracle(pkg));
            }
            return packageDataModelOracle.get(pkg.getPackageName());
        } finally {
            lock.unlock();
        }
    }

    private PackageDataModelOracle buildPackageDataModelOracle(final Package pkg) {
        final String packageName = pkg.getPackageName();
        final PackageDataModelOracleBuilder dmoBuilder = PackageDataModelOracleBuilder.newPackageOracleBuilder(evaluator,
                                                                                                               packageName);
        final ProjectDataModelOracle projectOracle = doGetProjectDataModelOracle();
        dmoBuilder.setProjectOracle(projectOracle);

        if (projectOracle.equals(EMPTY_PROJECT_ORACLE)) {
            return dmoBuilder.build();
        }

        //Add Guvnor enumerations
        loadEnumsForPackage(dmoBuilder, pkg);

        //Add DSLs
        loadExtensionsForPackage(dmoBuilder, pkg);

        //Add Globals
        loadGlobalsForPackage(dmoBuilder, pkg);

        return dmoBuilder.build();
    }

    private void loadEnumsForPackage(final PackageDataModelOracleBuilder dmoBuilder,
                                     final Package pkg) {
        final org.uberfire.java.nio.file.Path nioPackagePath = Paths.convert(pkg.getPackageMainResourcesPath());
        final Collection<org.uberfire.java.nio.file.Path> enumFiles = fileDiscoveryService.discoverFiles(nioPackagePath,
                                                                                                         FILTER_ENUMERATIONS);
        for (final org.uberfire.java.nio.file.Path path : enumFiles) {
            final String enumDefinition = ioService.readAllString(path);
            dmoBuilder.addEnum(enumDefinition, classLoader);
        }
    }

    private void loadExtensionsForPackage(final PackageDataModelOracleBuilder dmoBuilder,
                                          final Package pkg) {
        final org.uberfire.java.nio.file.Path nioPackagePath = Paths.convert(pkg.getPackageMainResourcesPath());
        final List<DataModelExtension> extensions = stream(dataModelExtensionsProvider.spliterator(), false).collect(toList());

        for (final DataModelExtension extension : extensions) {
            DirectoryStream.Filter<Path> filter = extension.getFilter();
            final Collection<org.uberfire.java.nio.file.Path> extensionFiles = fileDiscoveryService.discoverFiles(nioPackagePath,
                                                                                                                  filter);
            extensionFiles
                    .stream()
                    .map(file -> extension.getExtensions(file,
                                                         ioService.readAllString(file)))
                    .forEach(mappings -> mappings.forEach(mapping -> dmoBuilder.addExtension(mapping.getKind(),
                                                                                             mapping.getValues())));
        }
    }

    private void loadGlobalsForPackage(final PackageDataModelOracleBuilder dmoBuilder,
                                       final Package pkg) {
        final org.uberfire.java.nio.file.Path nioPackagePath = Paths.convert(pkg.getPackageMainResourcesPath());
        final Collection<org.uberfire.java.nio.file.Path> globalFiles = fileDiscoveryService.discoverFiles(nioPackagePath,
                                                                                                           FILTER_GLOBALS);
        for (final org.uberfire.java.nio.file.Path path : globalFiles) {
            final String definition = ioService.readAllString(path);
            dmoBuilder.addGlobals(definition);
        }
    }

    public void reBuild(final Path changedPath) {
        reBuild(singletonList(changedPath));
    }

    public void reBuild(final Collection<Path> changedPaths) {
        lock.lock();
        try {
            isReBuild.set(true);
            final List<String> packages = clearTargetAndListPackages(changedPaths);
            projectDataModelOracle = buildProjectDataModelOracle();
            packages.parallelStream().forEach(p -> packageDataModelOracle.remove(p));
            isReBuild.set(false);
        } finally {
            lock.unlock();
        }
    }

    private List<String> clearTargetAndListPackages(final Collection<Path> changedPaths) {
        final List<String> packages = new ArrayList<>();
        if (workingDir != null && workingDir.resolve("target/classes").toFile().exists()) {
            final Path baseTargetPath = workingDir.resolve("target/classes");
            for (final Path path : changedPaths) {
                final Path basePath = path.subpath(convert(project.getRootPath()).getNameCount(), path.getNameCount());
                if (basePath.startsWith("src/main/java") || basePath.startsWith("src/main/resources")) {
                    final Path relativeFilePath = path.subpath(convert(project.getRootPath()).getNameCount() + 3, path.getNameCount());
                    final Path filePackage = relativeFilePath.subpath(0, relativeFilePath.getNameCount() - 1);
                    packages.add(filePackage.toString().replace('/', '.'));
                    final String fullFileName = path.getFileName().toString();
                    final String fileName = fullFileName.substring(0, fullFileName.lastIndexOf('.'));
                    Files.deleteIfExists(baseTargetPath.resolve(filePackage.resolve(fileName + ".class")));
                }
            }
        }
        return packages;
    }

    private DefaultKieAFBuilder getBuilder() {
        if (builder == null || isReBuild.get()) {
            builder = new DefaultKieAFBuilder(convert(project.getRootPath()), mavenRepo);
        }
        return builder;
    }

    private KieCompilationResponse getCompilationResponse() {
        if (response == null || isReBuild.get()) {
            response = getBuilder().build();
        }
        return response;
    }

    private ClassLoader getDependenciesClassLoader(final KieCompilationResponse res) {
        if ((dependenciesClassLoader == null || dependencies == null || dependencies.isEmpty() ||
                (res.getDependencies().size() == dependencies.size() && res.getDependencies().containsAll(dependencies)))) {
            classLoader = null;
            dependencies = new HashSet<>(res.getDependencies());
            dependenciesClassLoader = new URLClassLoader(res.getDependenciesAsURL().toArray(new URL[res.getDependenciesAsURL().size()]));
        }
        return dependenciesClassLoader;
    }

    private ProjectDataModelOracle buildProjectDataModelOracle() {
        doGetClassLoader();
        if (kieModuleMetaData == null) {
            return EMPTY_PROJECT_ORACLE;
        }
        return new InnerBuilder().build();
    }

    class InnerBuilder {

        private final ProjectDataModelOracleBuilder pdBuilder = newProjectOracleBuilder(evaluator);

        public ProjectDataModelOracle build() {
            lock.lock();
            try {
                final WhiteList whiteList = getFilteredPackageNames();
                for (final String packageName : whiteList) {
                    pdBuilder.addPackage(packageName);
                    addClasses(packageName,
                               kieModuleMetaData.getClasses(packageName));
                    addClasses(packageName,
                               filterClassesByPackage(targetArtifacts, packageName),
                               TypeSource.JAVA_PROJECT);
                }

                if (!declaredTypes.isEmpty()) {
                    for (final String packageName : whiteList) {
                        List<Class<?>> clazzes = getClazz(packageName);
                        if (!clazzes.isEmpty()) {
                            addClass(clazzes, TypeSource.DECLARED);
                        }
                    }
                }

                if (Files.exists(Paths.convert(project.getImportsPath()))) {
                    for (final Import item : getImports()) {
                        addClass(item);
                    }
                }

                return pdBuilder.build();
            } finally {
                lock.unlock();
            }
        }

        private boolean isEvent(final String className,
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
            pkgs.addAll(filterPathClasses(targetArtifacts, "global/"));
            return packageNameWhiteListService.filterPackageNames(project, pkgs);
        }

        private void addClasses(final String packageName,
                                final Collection<String> classes) {
            for (final String className : classes) {
                addClass(packageName, className);
            }
        }

        private void addClasses(final String packageName, final Collection<String> classes, TypeSource typeSource) {
            for (final String className : classes) {
                addClass(packageName, className, typeSource);
            }
        }

        private void addClass(final List<Class<?>> clazzes, TypeSource typeSource) {
            try {
                for (Class clazz : clazzes) {
                    pdBuilder.addClass(clazz, false, typeSource);
                }
            } catch (IOException ioe) {
                log.debug(ioe.getMessage());
            }
        }

        private void addClass(final Import item) {
            try {
                Class clazz = classLoader.loadClass(item.getType());
                pdBuilder.addClass(clazz, false, TypeSource.JAVA_DEPENDENCY);
            } catch (ClassNotFoundException | IOException cnfe) {
                //Class resolution would have happened in Builder and reported as warnings so log error here at debug level to avoid flooding logs
                log.debug(cnfe.getMessage());
            }
        }

        private void addClass(final String packageName, final String className) {
            try {
                if (classLoader != null) {
                    final Class clazz = CompilerClassloaderUtils.getClass(packageName,
                                                                          className,
                                                                          classLoader);
                    if (clazz != null) {
                        pdBuilder.addClass(clazz,
                                           kieModuleMetaData.getTypeMetaInfo(clazz).isEvent(),
                                           defineType(clazz));
                    }
                }
            } catch (Throwable e) {
                //Class resolution would have happened in Builder and reported as warnings so log error here at debug level to avoid flooding logs
                log.debug(e.getMessage());
            }
        }

        private void addClass(final String packageName, final String className, TypeSource typeSource) {
            try {
                if (classLoader != null) {

                    final Class clazz = CompilerClassloaderUtils.getClass(packageName,
                                                                          className,
                                                                          classLoader);

                    if (clazz != null) {
                        pdBuilder.addClass(clazz, isEvent(className, clazz), typeSource);
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

    private TypeSource defineType(Class clazz) {
        final String typeName = clazz.getCanonicalName();
        if (kieModuleMetaData.getTypeMetaInfo(clazz).isDeclaredType()) {
            return TypeSource.DECLARED;
        }
        if (targetArtifacts.contains(typeName.replace('.', '/') + ".class")) {
            return TypeSource.JAVA_PROJECT;
        }
        return TypeSource.JAVA_DEPENDENCY;
    }

    private List<Class<?>> getClazz(final String _packageName) {
        final String packageName = _packageName.replace('.', '/');
        if (classLoader != null) {
            if (!classLoader.getKeys().isEmpty()) {
                final List<Class<?>> clazzes = new ArrayList<>();
                for (String key : classLoader.getKeys()) {
                    if (key.startsWith(packageName) && declaredTypes.keySet().contains(key)) {
                        try {
                            Class clazz = classLoader.loadClass(key.substring(0, key.lastIndexOf(".")).replace("/", "."));
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

    public Optional<KieContainer> getKieContainer() {
        lock.lock();
        try {
            final Optional<ReleaseId> releaseId = getReleaseId();
            if (!releaseId.isPresent()) {
                return Optional.empty();
            }

            final Optional<Path> results = getArtifact();

            return results.map(r -> {
                final KieServices ks = KieServices.get();
                KieModule km = KieServices.get().getRepository().getKieModule(releaseId.get());
                if (km == null) {
                    final Resource jarRes = ks.getResources().newFileSystemResource(r.toFile());
                    km = ks.getRepository().addKieModule(jarRes);
                }
                return Optional.of(ks.newKieContainer(km.getReleaseId()));
            }).orElse(Optional.empty());
        } finally {
            lock.unlock();
        }
    }

    private Optional<ReleaseId> getReleaseId() {
        if (releaseId == null) {
            buildClassLoader();
        }
        return Optional.of(releaseId);
    }

    private Optional<Path> getArtifact() {
        if (artifact == null) {
            buildClassLoader();
        }
        return Optional.of(artifact);
    }
}
