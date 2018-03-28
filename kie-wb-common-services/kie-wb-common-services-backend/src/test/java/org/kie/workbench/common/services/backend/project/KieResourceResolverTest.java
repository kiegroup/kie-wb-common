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

package org.kie.workbench.common.services.backend.project;

import javax.enterprise.inject.Instance;

import org.guvnor.common.services.project.backend.server.ProjectResourcePathResolver;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.POMService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class KieResourceResolverTest {

    @Mock
    private IOService ioService;

    @Mock
    private POMService pomService;

    @Mock
    private Instance<ProjectResourcePathResolver> resourcePathResolversInstance;

    @InjectMocks
    private KieResourceResolver kieResourceResolver;

    @Before
    public void setup() {
        doReturn(true).when(ioService).exists(any());
    }

    @Test
    public void simpleProjectInstanceWithNoPOM() {
        doReturn(false).when(ioService).exists(any());

        final KieProject project = kieResourceResolver.simpleProjectInstance(Paths.convert(PathFactory.newPath("myProject",
                                                                                                               "file:///myProject")));

        assertEquals("myProject",
                     project.getProjectName());
    }

    @Test
    public void simpleProjectInstanceWithNameSet() {
        final POM pom = new POM(new GAV("groupId",
                                        "artifactId",
                                        "version"));
        pom.setName("my project");
        doReturn(pom).when(pomService).load(any());

        final KieProject project = kieResourceResolver.simpleProjectInstance(Paths.convert(PathFactory.newPath("myProject",
                                                                                                               "file:///myProject")));

        assertEquals("my project",
                     project.getProjectName());
    }

    @Test
    public void simpleProjectInstanceWithNameNotSetUsesArtifactId() {
        final POM pom = new POM(new GAV("groupId",
                                        "artifactId",
                                        "version"));
        doReturn(pom).when(pomService).load(any());

        final KieProject project = kieResourceResolver.simpleProjectInstance(Paths.convert(PathFactory.newPath("myProject",
                                                                                                               "file:///myProject")));

        assertEquals("artifactId",
                     project.getProjectName());
    }

    @Test
    public void simpleProjectInstanceWithNameNotSetGAVNotSetUsesFileName() {
        final POM pom = new POM();
        doReturn(pom).when(pomService).load(any());

        final KieProject project = kieResourceResolver.simpleProjectInstance(Paths.convert(PathFactory.newPath("myProject",
                                                                                                               "file:///myProject")));

        assertEquals("myProject",
                     project.getProjectName());
    }
}
