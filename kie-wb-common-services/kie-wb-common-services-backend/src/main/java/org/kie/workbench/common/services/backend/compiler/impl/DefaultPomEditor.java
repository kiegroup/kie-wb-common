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

package org.kie.workbench.common.services.backend.compiler.impl;

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
import org.kie.workbench.common.services.backend.compiler.PluginPresents;
import org.kie.workbench.common.services.backend.compiler.PomEditor;
import org.kie.workbench.common.services.backend.compiler.configuration.Compilers;
import org.kie.workbench.common.services.backend.compiler.configuration.ConfigurationKey;
import org.kie.workbench.common.services.backend.compiler.configuration.ConfigurationProvider;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPomEditor implements PomEditor {

    public final String POM = "pom";
    public final String TRUE = "true";
    public final String POM_NAME = "pom.xml";
    public final String MAVEN_COMPILER_EXECUTION_ID = "default-compile";
    public final String MAVEN_COMPILER_EXECUTION_PHASE = "none";
    protected final Logger logger = LoggerFactory.getLogger(DefaultPomEditor.class);
    protected Compilers compiler;
    protected Map<ConfigurationKey, String> conf;
    protected MavenXpp3Reader reader;
    protected MavenXpp3Writer writer;
    protected Set<PomPlaceHolder> history;

    public DefaultPomEditor(Set<PomPlaceHolder> history,
                            ConfigurationProvider config,
                            Compilers compiler) {
        conf = config.loadConfiguration();
        reader = new MavenXpp3Reader();
        writer = new MavenXpp3Writer();
        this.history = history;
        this.compiler = compiler;
    }

    public Set<PomPlaceHolder> getHistory() {
        return Collections.unmodifiableSet(history);
    }

    @Override
    public void cleanHistory() {
        history.clear();
    }

    protected PluginPresents updatePom(Model model) {

        Build build = model.getBuild();
        if (build == null) {  //pom without build tag
            model.setBuild(new Build());
            build = model.getBuild();
        }

        Boolean defaultCompilerPluginPresent = Boolean.FALSE;
        Boolean alternativeCompilerPluginPresent = Boolean.FALSE;
        Boolean kiePluginPresent = Boolean.FALSE;
        int alternativeCompilerPosition = 0;
        int defaultMavenCompilerPosition = 0;
        int kieMavenPluginPosition = 0;

        int i = 0;
        for (Plugin plugin : build.getPlugins()) {
            // check if is present the default maven compiler
            if (plugin.getGroupId().equals(conf.get(ConfigurationKey.MAVEN_PLUGINS)) &&
                    plugin.getArtifactId().equals(conf.get(ConfigurationKey.MAVEN_COMPILER_PLUGIN))) {

                defaultCompilerPluginPresent = Boolean.TRUE;
                defaultMavenCompilerPosition = i;
            }

            //check if is present the alternative maven compiler
            if (plugin.getGroupId().equals(conf.get(ConfigurationKey.ALTERNATIVE_COMPILER_PLUGINS)) &&
                    plugin.getArtifactId().equals(conf.get(ConfigurationKey.ALTERNATIVE_COMPILER_PLUGIN))) {
                alternativeCompilerPluginPresent = Boolean.TRUE;
                alternativeCompilerPosition = i;
            }

            //check if is present the kie maven plugin
            if (plugin.getGroupId().equals(conf.get(ConfigurationKey.KIE_MAVEN_PLUGINS)) &&
                    plugin.getArtifactId().equals(conf.get(ConfigurationKey.KIE_MAVEN_PLUGIN))) {
                kiePluginPresent = Boolean.TRUE;
                kieMavenPluginPosition = i;
            }
            i++;
        }

        Boolean overwritePOM = updatePOMModel(build,
                                              defaultCompilerPluginPresent,
                                              alternativeCompilerPluginPresent,
                                              kiePluginPresent,
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
                                   int defaultMavenCompilerPosition,
                                   int alternativeCompilerPosition,
                                   int kieMavenPluginPosition) {

        Boolean overwritePOM = Boolean.FALSE;

        if (!alternativeCompilerPluginPresent) {
            //add alternative compiler and disable the default compiler
            build.addPlugin(getNewCompilerPlugin());
            alternativeCompilerPluginPresent = Boolean.TRUE;
            overwritePOM = Boolean.TRUE;
        }

        //@TODO check about the precedence of kie maven during the compile
        if (!defaultCompilerPluginPresent) {
            //if default maven compiler is not present we add the skip and phase none  to avoid its use
            Plugin disabledDefaultCompiler = new Plugin();
            disabledDefaultCompiler.setArtifactId(conf.get(ConfigurationKey.MAVEN_COMPILER_PLUGIN));
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
        return overwritePOM;
    }

    protected int getPluginPosition(Build build,
                                    String artifactPlugin) {
        int i = 0;
        for (Plugin plugin : build.getPlugins()) {
            if (plugin.getGroupId().equals(artifactPlugin)) {
                break;
            }
            i++;
        }
        return i;
    }

    protected Plugin getNewCompilerPlugin() {

        Plugin newCompilerPlugin = new Plugin();
        newCompilerPlugin.setGroupId(conf.get(ConfigurationKey.ALTERNATIVE_COMPILER_PLUGINS));
        newCompilerPlugin.setArtifactId(conf.get(ConfigurationKey.ALTERNATIVE_COMPILER_PLUGIN));
        newCompilerPlugin.setVersion(conf.get(ConfigurationKey.ALTERNATIVE_COMPILER_PLUGIN_VERSION));

        PluginExecution execution = new PluginExecution();
        execution.setId(MavenArgs.COMPILE);
        execution.setGoals(Arrays.asList(MavenArgs.COMPILE));
        execution.setPhase(MavenArgs.COMPILE);

        Xpp3Dom compilerId = new Xpp3Dom(conf.get(ConfigurationKey.MAVEN_COMPILER_ID));
        compilerId.setValue(compiler.name().toLowerCase());
        Xpp3Dom configuration = new Xpp3Dom(conf.get(ConfigurationKey.MAVEN_PLUGIN_CONFIGURATION));
        configuration.addChild(compilerId);

        execution.setConfiguration(configuration);
        newCompilerPlugin.setExecutions(Arrays.asList(execution));

        return newCompilerPlugin;
    }

    protected void disableMavenCompilerAlreadyPresent(Plugin plugin) {
        Xpp3Dom skipMain = new Xpp3Dom(conf.get(ConfigurationKey.MAVEN_SKIP_MAIN));
        skipMain.setValue(TRUE);
        Xpp3Dom skip = new Xpp3Dom(conf.get(ConfigurationKey.MAVEN_SKIP));
        skip.setValue(TRUE);

        Xpp3Dom configuration = new Xpp3Dom(conf.get(ConfigurationKey.MAVEN_PLUGIN_CONFIGURATION));
        configuration.addChild(skipMain);
        configuration.addChild(skip);

        plugin.setConfiguration(configuration);

        PluginExecution exec = new PluginExecution();
        exec.setId(conf.get(ConfigurationKey.MAVEN_DEFAULT_COMPILE));
        exec.setPhase(conf.get(ConfigurationKey.MAVEN_PHASE_NONE));
        List<PluginExecution> executions = new ArrayList<>();
        executions.add(exec);
        plugin.setExecutions(executions);
    }
}
