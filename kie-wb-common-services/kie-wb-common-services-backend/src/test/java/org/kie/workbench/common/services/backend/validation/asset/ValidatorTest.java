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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.guvnor.test.TestFileSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.mocks.FileSystemTestingUtils;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.mocks.FileSystemTestingUtils;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.uberfire.backend.server.util.Paths.convert;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ValidatorTest {

    @Mock
    Path path;

    private TestFileSystem testFileSystem;

    private DefaultGenericKieValidator validator;
    private ValidatorBuildService validatorBuildService;
    private IOService ioService;
    private FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();


    @Before
    public void setUp() throws Exception {
        testFileSystem = new TestFileSystem();
        validatorBuildService = testFileSystem.getReference( ValidatorBuildService.class );
        validator = new DefaultGenericKieValidator( validatorBuildService );
        fileSystemTestingUtils.setup();
        ioService = fileSystemTestingUtils.getIoService();
    }

    @After
    public void tearDown() throws Exception {
        testFileSystem.tearDown();
        fileSystemTestingUtils.cleanup();
    }

    @Test
    public void testValidateWithAValidDRLFile() throws Throwable {
        final JGitFileSystem fs = prepareTheFS();
        final String content = "package org.kie.workbench.common.services.builder.tests.test1\n" +
                "\n" +
                "rule R2\n" +
                "when\n" +
                "Bean()\n" +
                "then\n" +
                "end";

        List<ValidationMessage> errors = validator.validate(
                convert(fs.getPath("/GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl")), content );
        //"No value present"
        assertTrue( errors.isEmpty() );
    }

    @Test
    public void testValidateWithAInvalidDRLFile() throws Throwable {
        final JGitFileSystem fs = prepareTheFS();
        final String content = "package org.kie.workbench.common.services.builder.tests.test1\n" +
                "\n" +
                "rule R2\n" +
                "when\n" +
                "Ban()\n" +
                "then\n" +
                "end";

        List<ValidationMessage> errors = validator.validate(
                convert(fs.getPath("/GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl")), content );

        assertFalse( errors.isEmpty() );
    }

    @Test
    public void testValidateWithAValidJavaFile() throws Throwable {
        final JGitFileSystem fs = prepareTheFS();
        final String content = "package org.kie.workbench.common.services.builder.tests.test1;\n" +
                "\n" +
                "public class Bean {\n" +
                "    private final int value;\n" +
                "\n" +
                "    public Bean(int value) {\n" +
                "        this.value = value;\n" +
                "    }\n" +
                "    public int getValue() {\n" +
                "        return value*7;\n" +
                "    }\n"+
                "    public String toString(){\n"+
                "        return String.valueOf(value); \n"+
                "    }\n" +
                "}";
        List<ValidationMessage> errors = validator.validate(
                convert(fs.getPath("/GuvnorM2RepoDependencyExample1/src/main/java/org/kie/workbench/common/services/builder/tests/test1/Bean.java")), content );

        assertTrue( errors.isEmpty() );
    }

    @Test
    public void testValidateWithAInvalidJavaFile() throws Throwable {
        final JGitFileSystem fs = prepareTheFS();
       // final Path path1 = path( "/GuvnorM2RepoDependencyExample1/src/main/java/org/kie/workbench/common/services/builder/tests/test1/Bean.java" );
        final String content = "package org.kie.workbench.common.services.builder.tests.test1;\n" +
                "\n" +
                "public class Bean {\n" +
                "    private fnal int value;\n" +
                "\n" +
                "}\n";


        List<ValidationMessage> errors = validator.validate(
                convert(fs.getPath("/GuvnorM2RepoDependencyExample1/src/main/java/org/kie/workbench/common/services/builder/tests/test1/Bean.java")), content );


        assertFalse( errors.isEmpty() );
    }

    @Test
    public void testValidateWhenTheresNoProject() throws Throwable {
        final JGitFileSystem fs = prepareTheFS();
        URL urlToValidate = this.getClass().getResource( "/META-INF/beans.xml" );

        List<ValidationMessage> errors = validator.validate(
                convert(fs.getPath("/META-INF/beans.xml")),  Resources.toString( urlToValidate,
                                                                                                                                                                           Charsets.UTF_8 ) );
        assertFalse( errors.isEmpty() );
        assertTrue(errors.get(0).getText().equals("[ERROR] no project found"));
    }

    @Test
    public void testFilterMessageWhenMessageIsInvalid() throws Throwable {
        Path path = path( "/GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl" );
        ValidationMessage errorMessage = errorMessage( path( "/GuvnorM2RepoDependencyExample1/src/main/resources/rule1.drl" ) );

        List<ValidationMessage> result = applyPredicate( errorMessage,
                                                         validator.fromValidatedPath( path ) );

        assertTrue( result.isEmpty() );
    }

    @Test
    public void testFilterMessageWhenMessageIsValid() throws Throwable {
        Path path = path( "/GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl" );
        ValidationMessage errorMessage = errorMessage( path );

        List<ValidationMessage> result = applyPredicate( errorMessage,
                                                         validator.fromValidatedPath( path ) );

        assertFalse( result.isEmpty() );
    }

    @Test
    public void testFilterMessageWhenMessageIsBlank() throws Throwable {
        Path path = path( "/GuvnorM2RepoDependencyExample1/src/main/resources/rule2.drl" );
        ValidationMessage errorMessage = errorMessage( null );

        List<ValidationMessage> result = applyPredicate( errorMessage,
                                                         validator.fromValidatedPath( path ) );

        assertFalse( result.isEmpty() );
    }

    private JGitFileSystem prepareTheFS() throws Throwable{
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

        return fs;
    }

    private List<ValidationMessage> applyPredicate( final ValidationMessage errorMessage,
                                                    final Predicate<ValidationMessage> predicate ) {
        return validationMessages( errorMessage )
                .stream()
                .filter( predicate )
                .collect( Collectors.toList() );
    }

    private ArrayList<ValidationMessage> validationMessages( final ValidationMessage errorMessage ) {
        return new ArrayList<ValidationMessage>() {{
            add( errorMessage );
        }};
    }

    private ValidationMessage errorMessage( Path path ) {
        return new ValidationMessage( 0,
                                      Level.ERROR,
                                      path,
                                      0,
                                      0,
                                      null );
    }

    private Path path( final String resourceName ) throws URISyntaxException {
        final URL urlToValidate = this.getClass().getResource( resourceName );
        return Paths.convert( testFileSystem.fileSystemProvider.getPath( urlToValidate.toURI() ) );
    }

}
