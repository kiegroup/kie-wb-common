/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.project.migration.cli.maven;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.Repository;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.guvnor.common.services.project.backend.server.utils.configuration.ConfigurationKey;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.java.nio.file.StandardOpenOption;

public class PomEditor {

    private static final String PROPERTIES_FILE = "PomMigration.properties";
    private static final String KIE_VERSION_KEY = "KIE_VERSION";
    public final String TRUE = "true";
    private final Logger logger = LoggerFactory.getLogger(PomEditor.class);
    protected String FILE_URI = "file://";
    private MavenXpp3Reader reader;
    private MavenXpp3Writer writer;
    private String kieVersion;
    private String SCOPE_PROVIDED = "provided";
    private String KIE_PKG = "org.kie";
    private String DROOLS_PKG = "org.drools";
    private String OPTAPLANNER_PKG = "org.optaplanner";
    private JSONDTO jsonConf;
    private PomJsonReader jsonReader;

    private Properties props;

    public PomEditor() {
        reader = new MavenXpp3Reader();
        writer = new MavenXpp3Writer();
        props = loadProperties(PROPERTIES_FILE);
        kieVersion = props.getProperty(KIE_VERSION_KEY);
        if (kieVersion == null) {
            throw new RuntimeException("Kie version missing in configuration files");
        }
    }

    public Model updatePom(Path pom) {
        try {
            Model model = getModel(pom);
            Build build = getBuild(model);
            updateBuildTag(build);
            updateDependenciesTag(model);
            updateRepositories(model);
            updatePluginRepositories(model);
            boolean written = write(model, pom.toAbsolutePath().toString());
            if (written) {
                return model;
            } else {
                return new Model();
            }
        } catch (Exception e) {
            System.out.println("Error occurred during POMs migration:" + e.getMessage());
            logger.error(e.getMessage());
            return new Model();
        }
    }

    public Model updatePom(Path pom, String pathJsonFile) {
        try {
            jsonReader = new PomJsonReader(pathJsonFile);
            jsonConf = jsonReader.readPom();
            Model model = getModel(pom);
            Build build = getBuild(model);
            updateBuildTag(build);
            updateDependenciesTag(model);
            updateRepositories(model);
            updatePluginRepositories(model);
            boolean writed = write(model, pom.toAbsolutePath().toString());
            if (writed) {
                return model;
            } else {
                return new Model();
            }
        } catch (Exception e) {
            System.out.println("Error occurred during POMs migration:" + e.getMessage());
            logger.error(e.getMessage());
            return new Model();
        }
    }

    private Properties loadProperties(String propName) {
        Properties prop = new Properties();
        InputStream in = getClass().getClassLoader().getResourceAsStream(propName);
        if (in == null) {
            logger.info("{} not available with the classloader, skip to the next ConfigurationStrategy. \n", propName);
        } else {
            try {
                prop.load(in);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }finally {
                try {
                    in.close();
                } catch(IOException e) {
                    //suppressed
                }
            }
        }
        return prop;
    }

    /***************************************** Build TAG *****************************************/

    private void updateBuildTag(Build build) {
        List<Plugin> buildPlugins = getBuildPlugins(build);

        //order of processing matters !!!!
        processDefaultMavenCompiler(build, buildPlugins);
        processKieMavenCompiler(buildPlugins);
        processAlternativeMavenCompiler(build, buildPlugins);
    }

    private void processAlternativeMavenCompiler(Build build, List<Plugin> buildPlugins) {
        PluginPresence alternativeMavenCompiler = getPluginPresence(buildPlugins,
                                                                    props.getProperty(ConfigurationKey.TAKARI_COMPILER_PLUGIN_GROUP.name()),
                                                                    props.getProperty(ConfigurationKey.TAKARI_COMPILER_PLUGIN_ARTIFACT.name()));

        if (!alternativeMavenCompiler.isPresent()) {
            Plugin alternativeMavenCompilerPlugin = getAlternativeCompilerPlugin();
            List<Plugin> newPlugins = new ArrayList<>(buildPlugins.size() + 1);
            newPlugins.add(alternativeMavenCompilerPlugin);
            newPlugins.addAll(buildPlugins);
            build.setPlugins(newPlugins);
        }
    }

    private void processDefaultMavenCompiler(Build build, List<Plugin> buildPlugins) {
        PluginPresence defaultMavenCompiler = getPluginPresence(buildPlugins,
                                                                props.getProperty(ConfigurationKey.MAVEN_COMPILER_PLUGIN_GROUP.name()),
                                                                props.getProperty(ConfigurationKey.MAVEN_COMPILER_PLUGIN_ARTIFACT.name()));
        if (defaultMavenCompiler.isPresent()) {
            turnOffDefaultMavenCompiler(buildPlugins, defaultMavenCompiler);
        } else {
            Plugin disabledDefaultCompiler = new Plugin();
            disabledDefaultCompiler.setArtifactId(props.getProperty(ConfigurationKey.MAVEN_COMPILER_PLUGIN_ARTIFACT.name()));
            disableDefaultMavenCompiler(disabledDefaultCompiler);
            build.addPlugin(disabledDefaultCompiler);
        }
    }

    private void processKieMavenCompiler(List<Plugin> buildPlugins) {
        PluginPresence kieMavenCompiler = getPluginPresence(buildPlugins,
                                                            props.getProperty(ConfigurationKey.KIE_MAVEN_PLUGINS.name()),
                                                            props.getProperty(ConfigurationKey.KIE_MAVEN_PLUGIN.name()));
        if (kieMavenCompiler.isPresent()) {
            buildPlugins.remove(kieMavenCompiler.getPosition());
        }
    }

    private void turnOffDefaultMavenCompiler(List<Plugin> buildPlugins, PluginPresence defautlMavenCompiler) {
        Plugin defaulMavenCompiler = buildPlugins.get(defautlMavenCompiler.getPosition());
        Xpp3Dom skipMain = new Xpp3Dom(MavenConfig.MAVEN_SKIP_MAIN);
        skipMain.setValue(TRUE);
        Xpp3Dom skip = new Xpp3Dom(MavenConfig.MAVEN_SKIP);
        skip.setValue(TRUE);

        Xpp3Dom configuration = new Xpp3Dom(MavenConfig.MAVEN_PLUGIN_CONFIGURATION);
        configuration.addChild(skipMain);
        configuration.addChild(skip);

        defaulMavenCompiler.setConfiguration(configuration);

        PluginExecution exec = new PluginExecution();
        exec.setId(MavenConfig.MAVEN_DEFAULT_COMPILE);
        exec.setPhase(MavenConfig.MAVEN_PHASE_NONE);

        defaulMavenCompiler.setExecutions(Collections.singletonList(exec));
    }

    public Model getModel(Path pom) throws Exception {
        return reader.read(new ByteArrayInputStream(Files.readAllBytes(pom)));
    }

    private Build getBuild(Model model) {
        Build build = model.getBuild();
        if (build == null) {  //pom without build tag
            model.setBuild(new Build());
            build = model.getBuild();
        }
        return build;
    }

    private PluginPresence getPluginPresence(List<Plugin> plugins, String groupID, String artifactID) {
        boolean result = false;
        int i = 0;
        for (Plugin plugin : plugins) {
            if (plugin.getGroupId().equals(groupID) && plugin.getArtifactId().equals(artifactID)) {
                result = true;
                break;
            }
            i++;
        }
        return new PluginPresence(result, i);
    }

    private List<Plugin> getBuildPlugins(Build build) {
        return build.getPlugins();
    }

    private Plugin getAlternativeCompilerPlugin() {

        Plugin newCompilerPlugin = new Plugin();
        newCompilerPlugin.setGroupId(props.getProperty(ConfigurationKey.TAKARI_COMPILER_PLUGIN_GROUP.name()));
        newCompilerPlugin.setArtifactId(props.getProperty(ConfigurationKey.TAKARI_COMPILER_PLUGIN_ARTIFACT.name()));
        newCompilerPlugin.setVersion(props.getProperty(ConfigurationKey.TAKARI_COMPILER_PLUGIN_VERSION.name()));

        Xpp3Dom compilerId = new Xpp3Dom(MavenConfig.MAVEN_COMPILER_ID);
        compilerId.setValue(props.getProperty(ConfigurationKey.COMPILER.name()));
        Xpp3Dom sourceVersion = new Xpp3Dom(MavenConfig.MAVEN_SOURCE);
        sourceVersion.setValue(props.getProperty(ConfigurationKey.SOURCE_VERSION.name()));
        Xpp3Dom targetVersion = new Xpp3Dom(MavenConfig.MAVEN_TARGET);
        targetVersion.setValue(props.getProperty(ConfigurationKey.TARGET_VERSION.name()));

        Xpp3Dom failOnError = new Xpp3Dom(MavenConfig.FAIL_ON_ERROR);
        failOnError.setValue(props.getProperty(ConfigurationKey.FAIL_ON_ERROR.name()));

        Xpp3Dom configuration = new Xpp3Dom(MavenConfig.MAVEN_PLUGIN_CONFIGURATION);
        configuration.addChild(compilerId);
        configuration.addChild(sourceVersion);
        configuration.addChild(targetVersion);
        configuration.addChild(failOnError);
        newCompilerPlugin.setConfiguration(configuration);

        PluginExecution execution = new PluginExecution();
        execution.setId(MavenCLIArgs.DEFAULT_COMPILE);
        execution.setGoals(Arrays.asList(MavenCLIArgs.COMPILE));
        execution.setPhase(MavenCLIArgs.COMPILE);

        newCompilerPlugin.setExecutions(Arrays.asList(execution));
        return newCompilerPlugin;
    }

    private void disableDefaultMavenCompiler(Plugin plugin) {
        Xpp3Dom skipMain = new Xpp3Dom(MavenConfig.MAVEN_SKIP_MAIN);
        skipMain.setValue(TRUE);
        Xpp3Dom skip = new Xpp3Dom(MavenConfig.MAVEN_SKIP);
        skip.setValue(TRUE);

        Xpp3Dom configuration = new Xpp3Dom(MavenConfig.MAVEN_PLUGIN_CONFIGURATION);
        configuration.addChild(skipMain);
        configuration.addChild(skip);

        plugin.setConfiguration(configuration);

        PluginExecution exec = new PluginExecution();
        exec.setId(MavenConfig.MAVEN_DEFAULT_COMPILE);
        exec.setPhase(MavenConfig.MAVEN_PHASE_NONE);
        plugin.setExecutions(Collections.singletonList(exec));
    }

    /***************************************** END Build TAG *****************************************/
    /***************************************** Start Dependencies TAG *****************************************/

    private void updateDependenciesTag(Model model) {
        model.setDependencies(applyMandatoryDeps(getChangedCurrentDependencies(model.getDependencies())));
    }

    private List<Dependency> applyMandatoryDeps(List<Dependency> dependencies) {
        Set<Dependency> uniques = new HashSet<>(dependencies);
        if (jsonConf != null) {
            uniques.addAll(jsonConf.getDependencies());
        }
        return new ArrayList<>(uniques);
    }

    private List<Dependency> getChangedCurrentDependencies(List<Dependency> dependencies) {
        List<Dependency> newDeps = new ArrayList<>();
        for (Dependency dep : dependencies) {
            Dependency newDep = new Dependency();
            newDep.setGroupId(dep.getGroupId());
            newDep.setArtifactId(dep.getArtifactId());
            if (dep.getClassifier() != null) {
                newDep.setClassifier(dep.getClassifier());
            }
            if (dep.getVersion() == null) {
                newDep.setVersion(kieVersion);
            }
            if (dep.getScope().equals(SCOPE_PROVIDED) && isCustomerDependency(dep.getGroupId())) {
                newDep.setScope(dep.getScope());
            }
            newDeps.add(newDep);
        }
        return newDeps;
    }

    private boolean isCustomerDependency(String groupID) {
        return (!groupID.equals(KIE_PKG)
                || !groupID.equals(OPTAPLANNER_PKG)
                || !groupID.equals(DROOLS_PKG));
    }

    /***************************************** End Dependencies TAG *****************************************/
    /***************************************** Start Repositories TAG *****************************************/

    private void updateRepositories(Model model) {
        List<Repository> repos = model.getRepositories();
        if (repos == null || repos.isEmpty()) {
            repos = new ArrayList<>();
        }
        applyMandatoryRepos(repos);
        model.setRepositories(repos);
    }

    private void applyMandatoryRepos(List<Repository> repos) {
        if (jsonConf != null) {
            for (Repository repoFromJson : jsonConf.getRepositories()) {
                repos.add(repoFromJson);
            }
        }
    }

    /***************************************** End Repositories TAG *****************************************/
    /***************************************** Start PluginRepositories TAG *****************************************/

    private void updatePluginRepositories(Model model) {
        List<Repository> repos = model.getPluginRepositories();
        if (repos == null || repos.isEmpty()) {
            repos = new ArrayList<>();
        }
        applyMandatoryPluginRepos(repos, jsonConf);
        model.setPluginRepositories(repos);
    }

    private void applyMandatoryPluginRepos(List<Repository> repos, JSONDTO jsonConf) {
        if (jsonConf != null) {
            for (Repository repoFromJson : jsonConf.getPluginRepositories()) {
                repos.add(repoFromJson);
            }
        }
    }

    /***************************************** Start PluginRepositories TAG *****************************************/

    private boolean write(Model model, String absolutePath) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            writer.write(baos, model);
            if (logger.isDebugEnabled()) {
                logger.info("Pom changed:{}",
                            new String(baos.toByteArray(),
                                       StandardCharsets.UTF_8));
            }

            Path pomParent = Paths.get(URI.create(
                    new StringBuffer().
                            append(FILE_URI).
                            append(absolutePath).toString()));
            Files.delete(pomParent);
            Files.write(pomParent, baos.toByteArray(), StandardOpenOption.CREATE_NEW);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }finally {
            try {
                baos.close();
            } catch(IOException e) {
                //suppressed
            }
        }
    }
}
