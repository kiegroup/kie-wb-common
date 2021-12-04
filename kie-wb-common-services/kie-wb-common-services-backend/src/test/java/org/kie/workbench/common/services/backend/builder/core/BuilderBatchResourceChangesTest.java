/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.builder.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.test.TestFileSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.internal.builder.InternalKieBuilder;
import org.kie.workbench.common.services.shared.allowlist.PackageNameAllowListService;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.workbench.events.ResourceAdded;
import org.uberfire.workbench.events.ResourceChange;
import org.uberfire.workbench.events.ResourceRenamed;
import org.uberfire.workbench.events.ResourceUpdated;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BuilderBatchResourceChangesTest {

    @Mock
    private KieFileSystem kieFileSystem;

    @Mock
    private InternalKieBuilder kieBuilder;

    @Captor
    private ArgumentCaptor<String> pathCaptor;

    @Captor
    private ArgumentCaptor<Object> arrayCaptor;

    private Module module;

    private Builder builder;
    private TestFileSystem testFileSystem;

    @Before
    public void setUp() throws Exception {
        testFileSystem = new TestFileSystem();
        final Path root = testFileSystem.createTempDirectory("root");
        final Path pomFile = testFileSystem.createTempFile("root/pom.xml");

        module = new Module(root,
                            pomFile,
                            new POM(new GAV()));

        builder = new Builder(module,
                              mock(IOService.class),
                              mock(KieModuleService.class),
                              mock(ProjectImportsService.class),
                              new ArrayList<>(),
                              mock(LRUModuleDependenciesClassLoaderCache.class),
                              mock(LRUPomModelCache.class),
                              mock(PackageNameAllowListService.class),
                              mock(Predicate.class),
                              kieBuilder,
                              kieFileSystem);
    }

    @After
    public void tearDown() {
        testFileSystem.tearDown();
    }

    @Test
    public void noChanges() {

        builder.applyBatchResourceChanges(new HashMap<>());

        verify(kieFileSystem, never()).delete(any());
    }

    @Test
    public void updateOnRename() throws IOException {

        final Path oldName = testFileSystem.createTempFile("project/hello.txt");
        final Path newName = testFileSystem.createTempFile("project/helloAgain.txt");

        final HashMap<Path, Collection<ResourceChange>> changes = new HashMap<>();

        changes.put(newName, Collections.singleton(new ResourceUpdated("message")));
        changes.put(oldName, Collections.singleton(new ResourceRenamed(oldName, "message")));

        builder.applyBatchResourceChanges(changes);

        verify(kieFileSystem).delete(pathCaptor.capture());
        final String pathToBeDeleted = pathCaptor.getValue();
        assertTrue(pathToBeDeleted.contains("hello"));
        assertFalse(pathToBeDeleted.contains("helloAgain"));
    }

    @Test
    public void testAddAndUpdate() throws IOException {

        final Path path = testFileSystem.createTempFile("project/hello.txt");

        final HashMap<Path, Collection<ResourceChange>> changes = new HashMap<>();

        final ArrayList<ResourceChange> resourceChanges = new ArrayList<>();
        resourceChanges.add(new ResourceAdded("message"));
        resourceChanges.add(new ResourceUpdated("message"));
        changes.put(path, resourceChanges);

        builder.applyBatchResourceChanges(changes);

        verify(kieBuilder, times(1)).createFileSet(eq(Message.Level.WARNING), (String[]) arrayCaptor.capture());
        assertEquals(1, arrayCaptor.getAllValues().size());
    }
}