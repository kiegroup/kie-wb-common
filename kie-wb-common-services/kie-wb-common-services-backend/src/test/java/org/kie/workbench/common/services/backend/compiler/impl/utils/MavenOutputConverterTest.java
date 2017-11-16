package org.kie.workbench.common.services.backend.compiler.impl.utils;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.shared.message.Level;
import org.junit.Test;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.uberfire.backend.server.util.Paths.convert;

/**
 * TODO: update me
 */
public class MavenOutputConverterTest {

    @Test
    public void test1() {

        final FileSystem fs = mock(FileSystem.class);
        when(fs.supportedFileAttributeViews()).thenReturn(Collections.emptySet());

        final Path path = mock(Path.class);
        final Path resultPath = mock(Path.class);

        final Path fileName = mock(Path.class);
        when(fileName.toString()).thenReturn("Applicant.java");

        when(resultPath.getFileName()).thenReturn(fileName);
        when(resultPath.toUri()).thenReturn(URI.create("git://repo"));
        when(resultPath.getFileSystem()).thenReturn(fs);
        when(path.resolve((any(Path.class)))).thenReturn(resultPath);
        when(path.resolve((any(String.class)))).thenReturn(resultPath);

        final List<String> output = Arrays.asList("[ERROR] Error in the kieModuleMetaInfo from the kieMap :kieModuleMetaInfo not present in the map",
                                                  "[ERROR] Error in the kieModule :kieModule not present in the map",
                                                  "2017-11-14 22:30:04,490 [Thread-2584] ERROR Unable to index java class field for class: mortgages.mortgages.Applicant, fieldName: serialVersionUID fieldType: xxxx",
                                                  "2017-11-14 22:30:04,609 [Thread-2568] INFO  Scanning for projects...",
                                                  "2017-11-14 22:30:05,542 [Thread-2568] WARN  ",
                                                  "2017-11-14 22:30:05,542 [Thread-2568] WARN  Some problems were encountered while building the effective model for org.kie.workbench.playground:mortgages:kjar:1.0.0-SNAPSHOT",
                                                  "2017-11-14 22:30:05,542 [Thread-2568] WARN  'build.plugins.plugin.version' for org.apache.maven.plugins:maven-compiler-plugin is missing. @ line 117, column 15",
                                                  "2017-11-14 22:30:05,542 [Thread-2568] WARN  ",
                                                  "2017-11-14 22:30:05,542 [Thread-2568] WARN  It is highly recommended to fix these problems because they threaten the stability of your build.",
                                                  "2017-11-14 22:30:05,543 [Thread-2568] WARN  ",
                                                  "2017-11-14 22:30:05,543 [Thread-2568] WARN  For this reason, future Maven versions might no longer support building such malformed projects.",
                                                  "2017-11-14 22:30:05,543 [Thread-2568] WARN  ",
                                                  "2017-11-14 22:30:05,713 [Thread-2568] INFO                                                                          ",
                                                  "2017-11-14 22:30:05,713 [Thread-2568] INFO  ------------------------------------------------------------------------",
                                                  "2017-11-14 22:30:05,713 [Thread-2568] INFO  Building mortgages 1.0.0-SNAPSHOT",
                                                  "2017-11-14 22:30:05,713 [Thread-2568] INFO  ------------------------------------------------------------------------",
                                                  "2017-11-14 22:30:05,771 [Thread-2568] INFO  ",
                                                  "2017-11-14 22:30:05,772 [Thread-2568] INFO  --- maven-resources-plugin:3.0.1:resources (default-resources) @ mortgages ---",
                                                  "2017-11-14 22:30:06,651 [Thread-2568] WARN  Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!",
                                                  "2017-11-14 22:30:06,662 [Thread-2568] INFO  Copying 17 resources",
                                                  "2017-11-14 22:30:06,686 [Thread-2568] INFO  ",
                                                  "2017-11-14 22:30:06,687 [Thread-2568] INFO  --- takari-lifecycle-plugin:1.13.0:compile (default-compile) @ mortgages ---",
                                                  "2017-11-14 22:30:07,344 [Thread-2568] INFO  Previous incremental build state does not exist, performing full build",
                                                  "2017-11-14 22:30:07,398 [Thread-2568] INFO  Compiling 4 sources to /var/folders/j4/86jpk9rx5rzdrbtrxwsfv0pr0000gn/T/maven/6fd6b697-461c-407e-965b-f799d2f9dc7e/myrepo/mortgages/target/classes",
                                                  "2017-11-14 22:30:07,476 [Thread-2568] ERROR /private/var/folders/j4/86jpk9rx5rzdrbtrxwsfv0pr0000gn/T/maven/6fd6b697-461c-407e-965b-f799d2f9dc7e/myrepo/mortgages/src/main/java/mortgages/mortgages/Applicant.java:[21,22] cannot find symbol",
                                                  "  symbol:   class xxxx",
                                                  "  location: class mortgages.mortgages.Applicant",
                                                  "2017-11-14 22:30:07,477 [Thread-2568] INFO  Compiled 4 out of 4 sources (115 ms)",
                                                  "2017-11-14 22:30:07,490 [Thread-2568] INFO  ------------------------------------------------------------------------",
                                                  "2017-11-14 22:30:07,490 [Thread-2568] INFO  BUILD FAILURE",
                                                  "2017-11-14 22:30:07,490 [Thread-2568] INFO  ------------------------------------------------------------------------",
                                                  "2017-11-14 22:30:07,490 [Thread-2568] INFO  Total time: 2.883 s",
                                                  "2017-11-14 22:30:07,490 [Thread-2568] INFO  Finished at: 2017-11-14T22:30:07-05:00",
                                                  "2017-11-14 22:30:11,874 [Thread-2568] INFO  Final Memory: 1699M/3687M",
                                                  "2017-11-14 22:30:11,899 [Thread-2568] INFO  ------------------------------------------------------------------------",
                                                  "2017-11-14 22:30:12,637 [Thread-2568] ERROR Failed to execute goal io.takari.maven.plugins:takari-lifecycle-plugin:1.13.0:compile (default-compile) on project mortgages: 1 error(s) encountered:",
                                                  "2017-11-14 22:30:12,637 [Thread-2568] ERROR Failed to execute goal io.takari.maven.plugins:takari-lifecycle-plugin:1.13.0:compile (default-compile) on project mortgages: 1 error(s) encountered:",
                                                  "2017-11-14 22:30:12,638 [Thread-2568] ERROR /private/var/folders/j4/86jpk9rx5rzdrbtrxwsfv0pr0000gn/T/maven/6fd6b697-461c-407e-965b-f799d2f9dc7e/myrepo/mortgages/src/main/java/mortgages/mortgages/Applicant.java:[21:22] cannot find symbol",
                                                  "2017-11-14 22:30:12,638 [Thread-2568] ERROR /private/var/folders/j4/86jpk9rx5rzdrbtrxwsfv0pr0000gn/T/maven/6fd6b697-461c-407e-965b-f799d2f9dc7e/myrepo/mortgages/src/main/java/mortgages/mortgages/Applicant.java:[21:22] cannot find symbol",
                                                  "2017-11-14 22:30:12,638 [Thread-2568] ERROR symbol:   class xxxx",
                                                  "2017-11-14 22:30:12,638 [Thread-2568] ERROR symbol:   class xxxx",
                                                  "2017-11-14 22:30:12,638 [Thread-2568] ERROR location: class mortgages.mortgages.Applicant",
                                                  "2017-11-14 22:30:12,638 [Thread-2568] ERROR location: class mortgages.mortgages.Applicant",
                                                  "2017-11-14 22:30:12,638 [Thread-2568] ERROR -> [Help 1]",
                                                  "2017-11-14 22:30:12,638 [Thread-2568] ERROR -> [Help 1]",
                                                  "2017-11-14 22:30:12,638 [Thread-2568] ERROR ",
                                                  "2017-11-14 22:30:12,638 [Thread-2568] ERROR ",
                                                  "2017-11-14 22:30:12,638 [Thread-2568] ERROR To see the full stack trace of the errors, re-run Maven with the -e switch.",
                                                  "2017-11-14 22:30:12,638 [Thread-2568] ERROR To see the full stack trace of the errors, re-run Maven with the -e switch.",
                                                  "2017-11-14 22:30:12,638 [Thread-2568] ERROR Re-run Maven using the -X switch to enable full debug logging.",
                                                  "2017-11-14 22:30:12,638 [Thread-2568] ERROR Re-run Maven using the -X switch to enable full debug logging.",
                                                  "2017-11-14 22:30:12,638 [Thread-2568] ERROR ",
                                                  "2017-11-14 22:30:12,638 [Thread-2568] ERROR ",
                                                  "2017-11-14 22:30:12,638 [Thread-2568] ERROR For more information about the errors and possible solutions, please read the following articles:",
                                                  "2017-11-14 22:30:12,638 [Thread-2568] ERROR For more information about the errors and possible solutions, please read the following articles:",
                                                  "2017-11-14 22:30:12,638 [Thread-2568] ERROR [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoExecutionException",
                                                  "2017-11-14 22:30:12,638 [Thread-2568] ERROR [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoExecutionException");

        final BuildResults results = MavenOutputConverter.convertIntoBuildResults(output,
                                                                                  "ERROR",
                                                                                  path,
                                                                                  "/private/var/folders/j4/86jpk9rx5rzdrbtrxwsfv0pr0000gn/T/maven/6fd6b697-461c-407e-965b-f799d2f9dc7e/myrepo/mortgages/");

//        for (BuildMessage message : results.getErrorMessages()) {
//            System.out.println(message.getText());
//        }

        assertEquals(3, results.getErrorMessages().size());
        assertEquals("cannot find symbol", results.getErrorMessages().get(0).getText());
        assertEquals("symbol:   class xxxx", results.getErrorMessages().get(1).getText());
        assertEquals("location: class mortgages.mortgages.Applicant", results.getErrorMessages().get(2).getText());

        for (final BuildMessage buildMessage : results.getErrorMessages()) {
            assertEquals(convert(resultPath), buildMessage.getPath());
            assertEquals(22, buildMessage.getColumn());
            assertEquals(21, buildMessage.getLine());
            assertEquals(Level.ERROR, buildMessage.getLevel());
        }
    }



}
