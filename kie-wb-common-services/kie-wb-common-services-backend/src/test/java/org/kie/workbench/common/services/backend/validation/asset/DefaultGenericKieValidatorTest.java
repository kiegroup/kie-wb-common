/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.validation.asset;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

import com.google.common.io.Resources;
import org.guvnor.common.services.shared.builder.model.BuildMessage;
import org.guvnor.test.TestFileSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.mocks.FileSystemTestingUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.uberfire.backend.server.util.Paths.convert;

@RunWith(MockitoJUnitRunner.class)
public class DefaultGenericKieValidatorTest {

    private TestFileSystem testFileSystem;
    private DefaultGenericKieValidator validator;
    private IOService ioService;
    private FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();

    @Before
    public void setUp() throws Exception {
        testFileSystem = new TestFileSystem();
        validator = testFileSystem.getReference(DefaultGenericKieValidator.class);
        fileSystemTestingUtils.setup();
        ioService = fileSystemTestingUtils.getIoService();
    }

    @After
    public void tearDown() throws Exception {
        testFileSystem.tearDown();
        fileSystemTestingUtils.cleanup();
    }

    @Test
    public void testWorks() throws Exception {
        final URI originRepo = URI.create("git://repo");
        final JGitFileSystem fs = (JGitFileSystem) ioService.newFileSystem(originRepo,
                                                                           new HashMap<String, Object>() {{
                                                                               put("init",
                                                                                   Boolean.TRUE);
                                                                               put("internal",
                                                                                   Boolean.TRUE);
                                                                               put("listMode",
                                                                                   "ALL");
                                                                           }});

        ioService.startBatch(fs);

        ioService.write(fs.getPath("/GuvnorM2RepoDependencyExample1/pom.xml"),
                        new String(java.nio.file.Files.readAllBytes(new File("target/test-classes/GuvnorM2RepoDependencyExample1/pom.xml").toPath())));
        ioService.write(fs.getPath("/GuvnorM2RepoDependencyExample1/src/main/java/org/kie/workbench/common/services/builder/tests/test1/Bean.java"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/resources/GuvnorM2RepoDependencyExample1/src/main/java/org/kie/workbench/common/services/builder/tests/test1/Bean.java").toPath())));
        ioService.write(fs.getPath("/GuvnorM2RepoDependencyExample1/src/main/resources/META-INF/kmodule.xml"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/resources/GuvnorM2RepoDependencyExample1/src/main/resources/META-INF/kmodule.xml").toPath())));
        ioService.write(fs.getPath("/GuvnorM2RepoDependencyExample1/src/main/resources/rule1.drl"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/resources/GuvnorM2RepoDependencyExample1/src/main/resources/rule1.drl").toPath())));
        ioService.write(fs.getPath("/GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl"),
                        new String(java.nio.file.Files.readAllBytes(new File("src/test/resources/GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl").toPath())));
        ioService.endBatch();

        final URL urlToValidate = this.getClass().getResource("/GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl");
        final List<BuildMessage> errors = validator.validate(convert(fs.getPath("/GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl")),
                                                             Resources.toString(urlToValidate,
                                                                                Charset.forName("UTF-8")));

        assertTrue(errors.isEmpty());
    }

    @Test
    public void validatingAnAlreadyInvalidAssetShouldReportErrors() throws Exception {
        final Path path = resourcePath("/BuilderExampleBrokenSyntax/src/main/resources/rule1.drl");
        final URL urlToValidate = this.getClass().getResource("/BuilderExampleBrokenSyntax/src/main/resources/rule1.drl");

        final List<BuildMessage> errors1 = validator.validate(path,
                                                              Resources.toString(urlToValidate,
                                                                                 Charset.forName("UTF-8")));

        final List<BuildMessage> errors2 = validator.validate(path,
                                                              Resources.toString(urlToValidate,
                                                                                 Charset.forName("UTF-8")));

        assertFalse(errors1.isEmpty());
        assertFalse(errors2.isEmpty());
        assertEquals(errors1.size(),
                     errors2.size());
    }

    private Path resourcePath(final String resourceName) throws URISyntaxException, MalformedURLException {
        //final URL url = this.getClass().getResource( resourceName );
        final URL url = new URL("file://" + resourceName);
        return convert(testFileSystem.fileSystemProvider.getPath(url.toURI()));
    }
}