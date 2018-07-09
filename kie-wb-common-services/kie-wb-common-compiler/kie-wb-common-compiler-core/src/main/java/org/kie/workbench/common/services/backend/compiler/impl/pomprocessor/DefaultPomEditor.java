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

package org.kie.workbench.common.services.backend.compiler.impl.pomprocessor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.guvnor.common.services.project.backend.server.utils.configuration.ConfigurationKey;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.configuration.ConfigurationProvider;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.java.nio.file.StandardOpenOption;

/***
 * IS the main actor in the changes to the build tag in the poms
 */
public class DefaultPomEditor implements PomEditor {

    public final String POM = "pom";
    public final String TRUE = "true";
    public final String POM_NAME = "pom.xml";
    public final String KJAR_EXT = "kjar";
    protected final Logger logger = LoggerFactory.getLogger(DefaultPomEditor.class);
    protected String FILE_URI = "file://";
    protected Map<ConfigurationKey, String> conf;
    protected MavenXpp3Reader reader;
    protected MavenXpp3Writer writer;
    protected Set<PomPlaceHolder> history;

    public DefaultPomEditor(Set<PomPlaceHolder> history,
                            ConfigurationProvider config) {
        conf = config.loadConfiguration();
        reader = new MavenXpp3Reader();
        writer = new MavenXpp3Writer();
        this.history = history;
    }

    public Set<PomPlaceHolder> getHistory() {
        return Collections.unmodifiableSet(history);
    }

    @Override
    public Boolean cleanHistory() {
        history.clear();
        return Boolean.TRUE;
    }

    private PluginPresents updatePom(Model model) {

        Build build = model.getBuild();
        if (build == null) {  //pom without build tag
            model.setBuild(new Build());
            build = model.getBuild();
        }

        Boolean defaultCompilerPluginPresent = Boolean.FALSE;
        Boolean alternativeCompilerPluginPresent = Boolean.FALSE;
        Boolean kiePluginPresent = Boolean.FALSE;
        Boolean kieTakariPresent = Boolean.FALSE;
        int alternativeCompilerPosition = 0;
        int defaultMavenCompilerPosition = 0;
        int kieMavenPluginPosition = 0;

        if (model.getPackaging().equals(KJAR_EXT)) {
            kiePluginPresent = Boolean.TRUE;
        }

        int i = 0;
        for (Plugin plugin : build.getPlugins()) {
            // check if is present the default maven compiler
            if (plugin.getGroupId().equals(conf.get(ConfigurationKey.MAVEN_COMPILER_PLUGIN_GROUP)) &&
                    plugin.getArtifactId().equals(conf.get(ConfigurationKey.MAVEN_COMPILER_PLUGIN_ARTIFACT))) {

                defaultCompilerPluginPresent = Boolean.TRUE;
                defaultMavenCompilerPosition = i;
            }

            //check if is present the alternative maven compiler
            if (plugin.getGroupId().equals(conf.get(ConfigurationKey.TAKARI_COMPILER_PLUGIN_GROUP)) &&
                    plugin.getArtifactId().equals(conf.get(ConfigurationKey.TAKARI_COMPILER_PLUGIN_ARTIFACT))) {
                alternativeCompilerPluginPresent = Boolean.TRUE;
                alternativeCompilerPosition = i;
            }

            //check if is present the kie maven plugin
            if (plugin.getGroupId().equals(conf.get(ConfigurationKey.KIE_MAVEN_PLUGINS)) &&
                    plugin.getArtifactId().equals(conf.get(ConfigurationKey.KIE_MAVEN_PLUGIN))) {
                kiePluginPresent = Boolean.TRUE;
                kieMavenPluginPosition = i;
            }

            if (plugin.getGroupId().equals(conf.get(ConfigurationKey.KIE_MAVEN_PLUGINS)) &&
                    plugin.getArtifactId().equals(conf.get(ConfigurationKey.KIE_TAKARI_PLUGIN))) {
                kieTakariPresent = Boolean.TRUE;
            }
            i++;
        }

        Boolean overwritePOM = updatePOMModel(build,
                                              defaultCompilerPluginPresent,
                                              alternativeCompilerPluginPresent,
                                              kiePluginPresent,
                                              kieTakariPresent,
                                              defaultMavenCompilerPosition,
                                              alternativeCompilerPosition,
                                              kieMavenPluginPosition);

        return new DefaultPluginPresents(defaultCompilerPluginPresent,
                                         alternativeCompilerPluginPresent,
                                         kiePluginPresent,
                                         overwritePOM);
    }

    private Boolean updatePOMModel(Build build,
                                   Boolean defaultCompilerPluginPresent,
                                   Boolean alternativeCompilerPluginPresent,
                                   Boolean kiePluginPresent,
                                   Boolean kieTakariPresent,
                                   int defaultMavenCompilerPosition,
                                   int alternativeCompilerPosition,
                                   int kieMavenPluginPosition) {

        Boolean overwritePOM = Boolean.FALSE;

        if (!alternativeCompilerPluginPresent) {
            build.addPlugin(getNewCompilerPlugin());
            alternativeCompilerPluginPresent = Boolean.TRUE;
            overwritePOM = Boolean.TRUE;
        }

        if (!defaultCompilerPluginPresent) {
            //if default maven compiler is not present we add the skip and phase none  to avoid its use
            Plugin disabledDefaultCompiler = new Plugin();
            disabledDefaultCompiler.setArtifactId(conf.get(ConfigurationKey.MAVEN_COMPILER_PLUGIN_ARTIFACT));
            disableMavenCompilerAlreadyPresent(disabledDefaultCompiler);
            build.addPlugin(disabledDefaultCompiler);
            defaultCompilerPluginPresent = Boolean.TRUE;
            overwritePOM = Boolean.TRUE;
        }

        if (defaultCompilerPluginPresent && alternativeCompilerPluginPresent) {
            if (defaultMavenCompilerPosition <= alternativeCompilerPosition) {
                //swap the positions
                Plugin defaultMavenCompiler = build.getPlugins().get(defaultMavenCompilerPosition);
                Plugin alternativeCompiler = build.getPlugins().get(alternativeCompilerPosition);
                build.getPlugins().set(defaultMavenCompilerPosition,
                                       alternativeCompiler);
                build.getPlugins().set(alternativeCompilerPosition,
                                       defaultMavenCompiler);
                overwritePOM = Boolean.TRUE;
            }
        }

        // Change the kie-maven-plugin into kie-takari-plugin
        if (kiePluginPresent && !kieTakariPresent) {
            List<Plugin> plugins = build.getPlugins();
            Plugin kieMavenPlugin = build.getPlugins().get(kieMavenPluginPosition);

            if (kieMavenPlugin.getArtifactId().equals(conf.get(ConfigurationKey.KIE_MAVEN_PLUGIN))) {
                Plugin kieTakariPlugin = new Plugin();
                kieTakariPlugin.setGroupId(kieMavenPlugin.getGroupId());
                kieTakariPlugin.setArtifactId(conf.get(ConfigurationKey.KIE_TAKARI_PLUGIN));
                kieTakariPlugin.setVersion(kieMavenPlugin.getVersion());
                kieTakariPlugin.setExtensions(Boolean.parseBoolean(kieMavenPlugin.getExtensions()));
                plugins.set(kieMavenPluginPosition,
                            kieTakariPlugin);
                build.setPlugins(plugins);
                overwritePOM = Boolean.TRUE;
            }
        }
        return overwritePOM;
    }

    private Plugin getNewCompilerPlugin() {

        Plugin newCompilerPlugin = new Plugin();
        newCompilerPlugin.setGroupId(conf.get(ConfigurationKey.TAKARI_COMPILER_PLUGIN_GROUP));
        newCompilerPlugin.setArtifactId(conf.get(ConfigurationKey.TAKARI_COMPILER_PLUGIN_ARTIFACT));
        newCompilerPlugin.setVersion(conf.get(ConfigurationKey.TAKARI_COMPILER_PLUGIN_VERSION));

        Xpp3Dom compilerId = new Xpp3Dom(MavenConfig.MAVEN_COMPILER_ID);
        compilerId.setValue(conf.get(ConfigurationKey.COMPILER));
        Xpp3Dom sourceVersion = new Xpp3Dom(MavenConfig.MAVEN_SOURCE);
        sourceVersion.setValue(conf.get(ConfigurationKey.SOURCE_VERSION));
        Xpp3Dom targetVersion = new Xpp3Dom(MavenConfig.MAVEN_TARGET);
        targetVersion.setValue(conf.get(ConfigurationKey.TARGET_VERSION));

        Xpp3Dom failOnError = new Xpp3Dom(MavenConfig.FAIL_ON_ERROR);
        failOnError.setValue(conf.get(ConfigurationKey.FAIL_ON_ERROR));

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

    private void disableMavenCompilerAlreadyPresent(Plugin plugin) {
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
        List<PluginExecution> executions = new ArrayList<>();
        executions.add(exec);
        plugin.setExecutions(executions);
    }


    private String[] addCreateClasspathMavenArgs(String[] args, CompilationRequest req) {
        String[] newArgs = Arrays.copyOf(args, args.length + 2);
        newArgs[args.length] = MavenConfig.DEPS_IN_MEMORY_BUILD_CLASSPATH;
        newArgs[args.length + 1] = MavenConfig.MAVEN_DEP_PLUGING_LOCAL_REPOSITORY + req.getMavenRepo();
        return newArgs;
    }

    public PomPlaceHolder readSingle(Path pom) {
        PomPlaceHolder holder = new PomPlaceHolder();
        try {
            Model model = reader.read(new ByteArrayInputStream(Files.readAllBytes(pom)));
            holder = new PomPlaceHolder(pom.toAbsolutePath().toString(),
                                        model.getArtifactId(),
                                        model.getGroupId(),
                                        model.getVersion(),
                                        model.getPackaging());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return holder;
    }

    public boolean write(Path pom,
                      CompilationRequest request) {

        try {
            Model model = reader.read(new ByteArrayInputStream(Files.readAllBytes(pom)));
            if (model == null) {
                logger.error("Model null from pom file:",
                             pom.toString());
                return false;
            }

            PomPlaceHolder pomPH = new PomPlaceHolder(pom.toAbsolutePath().toString(),
                                                      model.getArtifactId(),
                                                      model.getGroupId(),
                                                      model.getVersion(),
                                                      model.getPackaging(),
                                                      Files.readAllBytes(pom));

            if (!history.contains(pomPH)) {

                PluginPresents plugs = updatePom(model);
                request.getInfo().lateAdditionKiePluginPresent(plugs.isKiePluginPresent());
                if (!request.skipPrjDependenciesCreationList()) {
                    // we add the mvn cli args to run the dependency:build-classpath
                    String args[] = addCreateClasspathMavenArgs(request.getKieCliRequest().getArgs(), request);
                    request.getKieCliRequest().setArgs(args);
                }
                if (plugs.pomOverwriteRequired()) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    writer.write(baos,
                                 model);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Pom changed:{}",
                                     new String(baos.toByteArray(),
                                                StandardCharsets.UTF_8));
                    }

                    Path pomParent = Paths.get(URI.create(
                            new StringBuffer().
                                    append(FILE_URI).
                                    append(pom.getParent().toAbsolutePath().toString()).
                                    append("/").
                                    append(POM_NAME).toString()));
                    Files.delete(pomParent);
                    Files.write(pomParent,
                                baos.toByteArray(),
                                StandardOpenOption.CREATE_NEW);//enhanced pom
                }
                history.add(pomPH);
            }
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }
}
