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
package org.kie.workbench.common.services.backend.compiler.impl.classloader;

import java.net.URI;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.BaseCompilerTest;
import org.kie.workbench.common.services.backend.compiler.TestUtilMaven;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.impl.utils.MavenUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class CompilerClassloaderUtilsTest extends BaseCompilerTest {

    public CompilerClassloaderUtilsTest() {
        super("target/test-classes/kjar-2-single-resources", EnumSet.of(KieDecorator.STORE_KIE_OBJECTS , KieDecorator.STORE_BUILD_CLASSPATH, KieDecorator.ENABLE_INCREMENTAL_BUILD));
    }

    @Test
    public void getStringFromTargets() {
        List<String> resources = CompilerClassloaderUtils.getStringFromTargets(tmpRoot);
        assertThat(resources).hasSize(3);
    }

    @Test
    public void loadDependenciesClassloaderFromProject() {
        Optional<ClassLoader> classloader = CompilerClassloaderUtils.loadDependenciesClassloaderFromProject(tmpRoot.toString(), mavenRepoPath);
        assertThat(classloader).isPresent();
    }

    @Test
    public void loadDependenciesClassloaderFromProjectWithPomList() {
        List<String> pomList = MavenUtils.searchPoms(tmpRoot);
        assertThat(pomList).hasSize(1);
        Optional<ClassLoader> classloader = CompilerClassloaderUtils.loadDependenciesClassloaderFromProject(pomList, mavenRepoPath);
        assertThat(classloader).isPresent();
    }

    @Test
    public void getClassloaderFromProjectTargets() {
        List<String> pomList = MavenUtils.searchPoms(tmpRoot);
        Optional<ClassLoader> classLoader = CompilerClassloaderUtils.getClassloaderFromProjectTargets(pomList);
        assertThat(classLoader).isPresent();
    }

    @Test
    public void getClassloaderFromAllDependencies() {
        Optional<ClassLoader> classLoader = CompilerClassloaderUtils.getClassloaderFromAllDependencies(tmpRoot.toString() + "/dummy", mavenRepoPath,
                                                                                                       TestUtilMaven.getSettingsFile());
        assertThat(classLoader).isPresent();
    }

    @Test
    public void createClassloaderFromCpFiles() {
        assertThat(res.getDependencies()).hasSize(4);
        Optional<ClassLoader> classLoader = CompilerClassloaderUtils.createClassloaderFromStringDeps(res.getDependencies());
        assertThat(classLoader).isPresent();
        assertThat(classLoader.get()).isNotNull();
    }

    @Test
    public void readFileAsURI() {
        assertThat(res.getDependencies()).isNotEmpty();
        List<String> projectDeps = res.getDependencies();
        List<URI> uris = CompilerClassloaderUtils.readAllDepsAsUris(projectDeps);
        assertThat(uris).hasSize(4);
    }
}