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
package org.kie.workbench.common.services.backend.compiler.impl.classloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.artifact.Artifact;
import org.drools.core.util.IoUtils;
import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.configuration.Decorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenConfig;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.MavenCompilerFactory;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.impl.utils.DotFileFilter;
import org.kie.workbench.common.services.backend.compiler.impl.utils.MavenUtils;
import org.kie.workbench.common.services.backend.project.MapClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

public class CompilerClassloaderUtils {

    protected static final Logger logger = LoggerFactory.getLogger(CompilerClassloaderUtils.class);
    protected static final DirectoryStream.Filter<Path> dotFileFilter = new DotFileFilter();
    protected static String DOT_FILE = ".";
    protected static String JAVA_ARCHIVE_RESOURCE_EXT = ".jar";
    protected static String JAVA_CLASS_EXT = ".class";
    protected static String XML_EXT = ".xml";
    protected static String DROOLS_EXT = ".drl";
    protected static String GDROOLS_EXT = ".gdrl";
    protected static String RDROOLS_EXT = ".rdrl";
    protected static String SCENARIO_EXT = ".scenario";
    protected static String FILE_URI = "file://";
    protected static String MAVEN_TARGET = "target/classes/";
    protected static String META_INF = "META-INF";
    protected static String UTF_8 = "UTF-8";

    /**
     * Execute a maven run to create the classloaders with the dependencies in the Poms, transitive included
     */
    public static Optional<ClassLoader> getClassloaderFromAllDependencies(String prjPath,
                                                                          String localRepo) {
        AFCompiler compiler = MavenCompilerFactory.getCompiler(Decorator.NONE);
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(URI.create(FILE_URI + prjPath)));
        StringBuilder sb = new StringBuilder(MavenConfig.MAVEN_DEP_PLUGING_OUTPUT_FILE).append(MavenConfig.CLASSPATH_FILENAME).append(MavenConfig.CLASSPATH_EXT);
        CompilationRequest req = new DefaultCompilationRequest(localRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.DEBUG, MavenConfig.DEPS_BUILD_CLASSPATH, sb.toString()},
                                                               Boolean.TRUE,
                                                               Boolean.FALSE);
        CompilationResponse res = compiler.compileSync(req);
        if (res.isSuccessful()) {
            /** Maven dependency plugin is not able to append the modules classpath using an absolute path in -Dmdep.outputFile,
             it override each time and at the end only the last writted is present in  the file,
             for this reason we use a relative path and then we read each file present in each module to build a unique classpath file
             * */
            Optional<ClassLoader> urlClassLoader = CompilerClassloaderUtils.createClassloaderFromCpFiles(prjPath);
            if (urlClassLoader != null) {
                return urlClassLoader;
            }
        }
        return Optional.empty();
    }

    /**
     * Used by the indexer
     **/
    public static Map<String, byte[]> getMapClasses(String path, Map<String, byte[]> store) {

        List<String> keys = IoUtils.recursiveListFile(new File(path), "", filterClasses());

        Map<String, byte[]> classes = new HashMap<String, byte[]>(keys.size());

        for (String item : keys) {
            byte[] bytez = getBytes(path + "/" + item);
            String fqn = item.substring(item.lastIndexOf(MAVEN_TARGET) + 15); // 15 chars are for "target/classes"
            classes.put(fqn, bytez);
        }
        if(store != Collections.EMPTY_MAP){
            for (Map.Entry<String, byte[]> entry : store.entrySet()) {
                classes.put(entry.getKey(), entry.getValue());
            }
        }
        return classes;
    }

    public static Predicate<File> filterClasses() {
        return f -> f.toString().contains(MAVEN_TARGET) && !f.toString().contains(META_INF) &&!FilenameUtils.getName(f.toString()).startsWith(DOT_FILE);
    }

    public static void searchCPFiles(Path file,
                                     List<String> classPathFiles,
                                     String... extensions) {
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(file.toAbsolutePath())) {
            for (Path p : ds) {
                if (Files.isDirectory(p)) {
                    searchCPFiles(p,
                                  classPathFiles,
                                  extensions);
                } else if (Stream.of(extensions).anyMatch(p.toString()::endsWith)) {
                    classPathFiles.add(p.toAbsolutePath().toString());
                }
            }
        }
    }

    public static void searchTargetFiles(Path file,
                                         List<String> classPathFiles,
                                         String... extensions) {
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(file.toAbsolutePath())) {
            for (Path p : ds) {
                if (Files.isDirectory(p)) {
                    searchTargetFiles(p,
                                      classPathFiles,
                                      extensions);
                } else if (Stream.of(extensions).anyMatch(p.toString()::endsWith) && p.toString().contains(MAVEN_TARGET)) {
                    if (FilenameUtils.getName(p.getFileName().toString()).startsWith(DOT_FILE)) {
                        continue;
                    }
                    classPathFiles.add(p.toAbsolutePath().toString());
                }
            }
        }
    }

    public static Optional<ClassLoader> loadDependenciesClassloaderFromProject(String prjPath,
                                                                               String localRepo) {
        List<String> poms =
                MavenUtils.searchPoms(Paths.get(URI.create(FILE_URI + prjPath)));
        List<URL> urls = getDependenciesURL(poms,
                                            localRepo);
        return buildResult(urls);
    }

    public static Optional<ClassLoader> loadDependenciesClassloaderFromProject(List<String> poms,
                                                                               String localRepo) {
        List<URL> urls = getDependenciesURL(poms,
                                            localRepo);
        return buildResult(urls);
    }

    public static Optional<ClassLoader> getClassloaderFromProjectTargets(List<String> pomsPaths,
                                                                         Boolean loadIntoClassloader) {
        List<URL> urls = loadIntoClassloader ? loadFiles(pomsPaths) : getTargetModulesURL(pomsPaths);
        return buildResult(urls);
    }

    public static List<URL> buildUrlsFromArtifacts(String localRepo,
                                                   List<Artifact> artifacts) throws MalformedURLException {
        List<URL> urls = new ArrayList<>(artifacts.size());
        for (Artifact artifact : artifacts) {
            StringBuilder sb = new StringBuilder(FILE_URI);
            sb.append(localRepo).append("/").append(artifact.getGroupId()).
                    append("/").append(artifact.getVersion()).append("/").append(artifact.getArtifactId()).
                    append("-").append(artifact.getVersion()).append(".").append(artifact.getType());
            URL url = new URL(sb.toString());
            urls.add(url);
        }
        return urls;
    }

    public static List<URL> getDependenciesURL(List<String> poms,
                                               String localRepo) {
        List<Artifact> artifacts = MavenUtils.resolveDependenciesFromMultimodulePrj(poms);
        List<URL> urls = Collections.emptyList();
        try {
            urls = buildUrlsFromArtifacts(localRepo,
                                          artifacts);
        } catch (MalformedURLException ex) {
            logger.error(ex.getMessage());
        }
        return urls;
    }

    public static List<URL> getTargetModulesURL(List<String> pomsPaths) {
        if (pomsPaths != null && pomsPaths.size() > 0) {
            List<URL> targetModulesUrls = new ArrayList(pomsPaths.size());
            try {
                for (String pomPath : pomsPaths) {
                    Path path = Paths.get(URI.create(FILE_URI + pomPath));
                    StringBuilder sb = new StringBuilder(FILE_URI)
                            .append(path.getParent().toAbsolutePath().toString())
                            .append("/").append(MAVEN_TARGET);
                    targetModulesUrls.add(new URL(sb.toString()));
                }
            } catch (MalformedURLException ex) {
                logger.error(ex.getMessage());
            }
            return targetModulesUrls;
        } else {
            return Collections.emptyList();
        }
    }

    public static Optional<ClassLoader> buildResult(List<URL> urls) {
        if (urls.isEmpty()) {
            return Optional.empty();
        } else {
            URLClassLoader urlClassLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]));
            return Optional.of(urlClassLoader);
        }
    }

    public static Optional<ClassLoader> createClassloaderFromCpFiles(String prjPath) {
        List<URL> deps = readAllCpFilesAsUrls(prjPath,
                                              MavenConfig.CLASSPATH_EXT);
        if (deps.isEmpty()) {
            return Optional.empty();
        } else {
            URLClassLoader urlClassLoader = new URLClassLoader(deps.toArray(new URL[deps.size()]));
            return Optional.of(urlClassLoader);
        }
    }

    public static List<URL> readAllCpFilesAsUrls(String prjPath,
                                                 String extension) {
        List<String> classPathFiles = new ArrayList<>();
        searchCPFiles(Paths.get(URI.create(FILE_URI + prjPath)),
                      classPathFiles,
                      extension);
        if (!classPathFiles.isEmpty()) {
            List<URL> deps = new ArrayList<>();
            for (String file : classPathFiles) {
                deps.addAll(readFileAsURL(file));
            }
            if (!deps.isEmpty()) {

                return deps;
            }
        }
        return Collections.emptyList();
    }

    public static List<URL> loadFiles(List<String> pomsPaths) {
        List<URL> targetModulesUrls = getTargetModulesURL(pomsPaths);
        if (!targetModulesUrls.isEmpty()) {
            List<URL> targetFiles = CompilerClassloaderUtils.addFilesURL(targetModulesUrls);
            return targetFiles;
        }
        return Collections.emptyList();
    }

    public static List<URI> readFileAsURI(String filePath) {

        BufferedReader br = null;
        List<URI> urls = new ArrayList<>();
        try {

            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), UTF_8));
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                StringTokenizer token = new StringTokenizer(sCurrentLine, ":");
                while (token.hasMoreTokens()) {
                    StringBuilder sb = new StringBuilder(FILE_URI).append(token.nextToken());
                    urls.add(new URI(sb.toString()));
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            close(filePath,
                  br);
        }
        return urls;
    }

    public static void close(String filePath,
                             BufferedReader br) {
        try {
            if (br != null) {
                br.close();
            }
            if (filePath.startsWith(FILE_URI)) {
                Files.delete(Paths.get(filePath));
            } else {
                Files.delete(Paths.get(URI.create(FILE_URI + filePath)));
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    public static List<URL> readFileAsURL(String filePath) {

        BufferedReader br = null;
        List<URL> urls = new ArrayList<>();
        try {

            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), UTF_8));
            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                StringTokenizer token = new StringTokenizer(sCurrentLine, ":");
                while (token.hasMoreTokens()) {
                    StringBuilder sb = new StringBuilder(FILE_URI).append(token.nextToken());
                    urls.add(new URL(sb.toString()));
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            close(filePath,
                  br);
        }
        return urls;
    }

    public static List<String> readFileAsString(String filePath) {

        BufferedReader br = null;
        List<String> items = new ArrayList<>();
        try {

            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), UTF_8));

            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                StringTokenizer token = new StringTokenizer(sCurrentLine, ":");
                while (token.hasMoreTokens()) {
                    StringBuilder sb = new StringBuilder(FILE_URI).append(token.nextToken());
                    items.add(sb.toString());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            close(filePath,
                  br);
        }
        return items;
    }

    public static Optional<List<String>> getStringFromTargets(Path prjPath) {
        List<String> classPathFiles = new ArrayList<>();
        searchTargetFiles(prjPath,
                          classPathFiles,
                          JAVA_CLASS_EXT,
                          DROOLS_EXT,
                          GDROOLS_EXT,
                          RDROOLS_EXT,
                          XML_EXT,
                          SCENARIO_EXT);
        if (!classPathFiles.isEmpty()) {
            return Optional.of(classPathFiles);
        }
        return Optional.empty();
    }

    public static Optional<List<String>> getStringsFromTargets(Path prjPath,
                                                               String... extensions) {
        List<String> classPathFiles = new ArrayList<>();
        searchCPFiles(prjPath,
                      classPathFiles,
                      extensions);
        if (!classPathFiles.isEmpty()) {
            return Optional.of(classPathFiles);
        }
        return Optional.empty();
    }

    public static Optional<List<String>> getStringsFromAllDependencies(Path prjPath) {
        List<String> classPathFiles = new ArrayList<>();
        searchCPFiles(prjPath,
                      classPathFiles,
                      MavenConfig.CLASSPATH_EXT,
                      JAVA_ARCHIVE_RESOURCE_EXT,
                      JAVA_CLASS_EXT,
                      DROOLS_EXT,
                      GDROOLS_EXT,
                      RDROOLS_EXT,
                      SCENARIO_EXT);
        if (!classPathFiles.isEmpty()) {
            List<String> deps = processScannedFilesAsString(classPathFiles);
            if (!deps.isEmpty()) {
                return Optional.of(deps);
            }
        }
        return Optional.empty();
    }

    public static List<URI> processScannedFilesAsURIs(List<String> classPathFiles) {
        List<URI> deps = new ArrayList<>();
        for (String file : classPathFiles) {
            if (FilenameUtils.getName(file).startsWith(".")) {
                continue;
            }
            if (file.endsWith(MavenConfig.CLASSPATH_EXT)) {
                //the .cpath will be processed to extract the deps of each module
                deps.addAll(readFileAsURI(file));
            } else if (file.endsWith(JAVA_ARCHIVE_RESOURCE_EXT)) {
                //the jar is added as is with file:// prefix
                if (file.startsWith(FILE_URI)) {
                    deps.add(URI.create(file));
                } else {
                    deps.add(URI.create(FILE_URI + file));
                }
            }
        }
        return deps;
    }

    public static List<String> processScannedFilesAsString(List<String> classPathFiles) {
        List<String> deps = new ArrayList<>();
        for (String file : classPathFiles) {
            if (FilenameUtils.getName(file).startsWith(".")) {
                continue;
            }
            if (file.endsWith(MavenConfig.CLASSPATH_EXT)) {
                //the .cpath will be processed to extract the deps of each module
                deps.addAll(readFileAsString(file));
            } else if (file.endsWith(JAVA_ARCHIVE_RESOURCE_EXT)) {
                //the jar is added as is with file:// prefix
                deps.add(file);
            }
        }
        return deps;
    }

    public static List<URL> processScannedFilesAsURLs(List<String> classPathFiles) {
        List<URL> deps = new ArrayList<>();
        try {
            for (String file : classPathFiles) {
                if (FilenameUtils.getName(file).startsWith(".")) {
                    continue;
                }
                if (file.endsWith(MavenConfig.CLASSPATH_EXT)) {
                    //the .cpath will be processed to extract the deps of each module
                    deps.addAll(readFileAsURL(file));
                } else if (file.endsWith(JAVA_ARCHIVE_RESOURCE_EXT)) {
                    //the jar/class is added as is with file:// prefix
                    if (file.startsWith(FILE_URI)) {
                        deps.add(new URL(file));
                    } else {
                        deps.add(new URL(FILE_URI + file));
                    }
                }
            }
        } catch (MalformedURLException ex) {
            logger.error(ex.getMessage());
        }
        return deps;
    }

    public static List<URL> addFilesURL(List<URL> targetModulesUrls) {
        List<URL> targetFiles = new ArrayList<>(targetModulesUrls.size());
        for (URL url : targetModulesUrls) {
            try {
                targetFiles.addAll(visitFolders(Files.newDirectoryStream(Paths.get(url.toURI()))));
            } catch (URISyntaxException ex) {
                logger.error(ex.getMessage());
            }
        }
        return targetFiles;
    }

    public static List<URL> visitFolders(final DirectoryStream<Path> directoryStream) {
        List<URL> urls = new ArrayList<>();
        for (final Path path : directoryStream) {
            if (Files.isDirectory(path)) {
                visitFolders(Files.newDirectoryStream(path));
            } else {
                //Don't process dotFiles
                if (!dotFileFilter.accept(path)) {
                    try {
                        urls.add(path.toUri().toURL());
                    } catch (MalformedURLException ex) {
                        logger.error(ex.getMessage());
                    }
                }
            }
        }
        return urls;
    }

    public static byte[] getBytes(String pResourceName) {
        try {
            File resource = new File(pResourceName);
            return resource.exists() ? IoUtils.readBytesFromInputStream(new FileInputStream(pResourceName)) : null;
        } catch (IOException e) {
            throw new RuntimeException("Unable to get bytes for: " + new File(pResourceName) + " " + e.getMessage());
        }
    }

    public static Set<String> filterPathClasses(List<String> paths,  String mavenRpo) {
        int mavenRepoLength = mavenRpo.length();
        Set<String> filtered = new HashSet<>(paths.size());
        for (String item : paths) {
            if (item.endsWith(JAVA_CLASS_EXT)) {
                String one = item.substring(item.lastIndexOf(MAVEN_TARGET) + 15); // 15 chars are for "target/classes/"
                if(one.contains("/")){  //there is a package
                   one =  one.substring(0, one.lastIndexOf("/")).replace("/", ".");
                    filtered.add(one);
                }
            } else if (item.endsWith(JAVA_ARCHIVE_RESOURCE_EXT)) {
                String one = item.substring(item.lastIndexOf(mavenRpo) + mavenRepoLength,
                                            item.lastIndexOf("/")).replace("/", ".");
                filtered.add(one);
            }
        }
        return filtered;
    }

    public static List<String> filterClassesByPackage(List<String> items, String packageName) {
        String packageNameWithSlash = packageName.replace(".", "/");//fix for the wildcard
        List<String> filtered = new ArrayList<>(items.size());
        for (String item : items) {
            if (!item.contains(META_INF)) {
                String one = item.substring(item.lastIndexOf(MAVEN_TARGET) + 15, item.lastIndexOf(".")); // 15 chars are for "target/classes/"
                if (one.contains(packageNameWithSlash)) {
                    if(one.contains("/")) { //there is a package
                        one = one.replace("/", ".");
                    }
                    filtered.add(one);
                }
            }
        }
        return filtered;
    }

    public static Class<?> getClass(String pkgName, String className, MapClassLoader classloader) {
        try {
            String input;
            if (pkgName != null && pkgName.trim().length() != 0) {
                input = className;
            } else {
                return null;
            }
            Class<?> clazz = classloader.loadClass(input);
            return clazz;
        } catch (ClassNotFoundException var4) {
            return null;
        }
    }
}
