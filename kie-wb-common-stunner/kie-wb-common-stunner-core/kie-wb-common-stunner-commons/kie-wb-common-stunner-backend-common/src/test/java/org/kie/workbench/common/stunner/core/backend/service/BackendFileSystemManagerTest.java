/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.backend.service;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.io.IOService;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class BackendFileSystemManagerTest {

    @Mock
    private IOService ioService;

    @Mock
    private CommentedOptionFactory optionFactory;

    private BackendFileSystemManager tested;

    @Before
    public void setup() {
        tested = new BackendFileSystemManager(ioService,
                                              optionFactory);
    }

    @Test
    public void testDeploy() {
        org.uberfire.java.nio.file.Path path = mock(org.uberfire.java.nio.file.Path.class);
        // TODO tested.deploy();
    }
}
