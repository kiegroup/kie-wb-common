package org.kie.workbench.common.services.backend.compiler.impl.utils;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.shared.message.Level;
import org.junit.Before;
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

    private Path path;
    private Path resultPath;

    @Before
    public void setup() {
        final FileSystem fs = mock(FileSystem.class);
        when(fs.supportedFileAttributeViews()).thenReturn(Collections.emptySet());

        this.path = mock(Path.class);
        this.resultPath = mock(Path.class);

        final Path fileName = mock(Path.class);
        when(fileName.toString()).thenReturn("Applicant.java");

        when(resultPath.getFileName()).thenReturn(fileName);
        when(resultPath.toUri()).thenReturn(URI.create("git://repo"));
        when(resultPath.getFileSystem()).thenReturn(fs);
        when(path.toString()).thenReturn("git://repo/root/");
        when(path.resolve((any(Path.class)))).thenReturn(resultPath);
        when(path.resolve((any(String.class)))).thenReturn(resultPath);
    }

    @Test
    public void test1() {
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
                                                  "2017-11-14 22:30:07,476 [Thread-2568] ERROR /var/folders/j4/86jpk9rx5rzdrbtrxwsfv0pr0000gn/T/maven/6fd6b697-461c-407e-965b-f799d2f9dc7e/myrepo/mortgages/src/main/java/mortgages/mortgages/Applicant.java:[21,22] cannot find symbol",
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
                                                  "2017-11-14 22:30:12,638 [Thread-2568] ERROR /var/folders/j4/86jpk9rx5rzdrbtrxwsfv0pr0000gn/T/maven/6fd6b697-461c-407e-965b-f799d2f9dc7e/myrepo/mortgages/src/main/java/mortgages/mortgages/Applicant.java:[21:22] cannot find symbol",
                                                  "2017-11-14 22:30:12,638 [Thread-2568] ERROR /var/folders/j4/86jpk9rx5rzdrbtrxwsfv0pr0000gn/T/maven/6fd6b697-461c-407e-965b-f799d2f9dc7e/myrepo/mortgages/src/main/java/mortgages/mortgages/Applicant.java:[21:22] cannot find symbol",
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
                                                                                  path,
                                                                                  "/var/folders/j4/86jpk9rx5rzdrbtrxwsfv0pr0000gn/T/maven/6fd6b697-461c-407e-965b-f799d2f9dc7e/myrepo/mortgages/");

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

    @Test
    public void test2() {
        final List<String> output = Arrays.asList("2017-11-16 16:35:01,830 [Thread-2579] INFO  Scanning for projects...",
                                                  "2017-11-16 16:35:01,878 [Thread-2596] ERROR Unable to index java class field for class: mortgages.mortgages.Applicant, fieldName: serialVersionUID fieldType: xxxx",
                                                  "2017-11-16 16:35:03,067 [Thread-2579] INFO                                                                          ",
                                                  "2017-11-16 16:35:03,068 [Thread-2579] INFO  ------------------------------------------------------------------------",
                                                  "2017-11-16 16:35:03,068 [Thread-2579] INFO  Building mortgages 1.0.0-SNAPSHOT",
                                                  "2017-11-16 16:35:03,068 [Thread-2579] INFO  ------------------------------------------------------------------------",
                                                  "2017-11-16 16:35:03,126 [Thread-2579] INFO  ",
                                                  "2017-11-16 16:35:03,126 [Thread-2579] INFO  --- maven-resources-plugin:3.0.2:resources (default-resources) @ mortgages ---",
                                                  "2017-11-16 16:35:03,283 [Thread-2579] WARN  Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!",
                                                  "2017-11-16 16:35:03,292 [Thread-2579] INFO  Copying 16 resources",
                                                  "2017-11-16 16:35:03,314 [Thread-2579] INFO  ",
                                                  "2017-11-16 16:35:03,314 [Thread-2579] INFO  --- maven-compiler-plugin:3.1:compile (default-compile) @ mortgages ---",
                                                  "2017-11-16 16:35:03,918 [Thread-2579] INFO  Changes detected - recompiling the module!",
                                                  "2017-11-16 16:35:03,920 [Thread-2579] WARN  File encoding has not been set, using platform encoding UTF-8, i.e. build is platform dependent!",
                                                  "2017-11-16 16:35:03,930 [Thread-2579] INFO  Compiling 4 source files to /var/folders/j4/86jpk9rx5rzdrbtrxwsfv0pr0000gn/T/maven/41643253-9e34-4768-985d-74a2f3728b7d/myrepo/mortgages/target/classes",
                                                  "2017-11-16 16:35:05,023 [Thread-2579] WARN  /var/folders/j4/86jpk9rx5rzdrbtrxwsfv0pr0000gn/T/maven/41643253-9e34-4768-985d-74a2f3728b7d/myrepo/mortgages/src/main/java/mortgages/mortgages/Applicant.java:[21] ",
                                                  "\tstatic final xxxx serialVersionUID = 1L;",
                                                  "\t             ^^^^",
                                                  "xxxx cannot be resolved to a type",
                                                  "2. WARNING: CAL10NAnnotationProcessor 0.8.1 initialized",
                                                  "2 problems (1 error, 1 warning)",
                                                  "2017-11-16 16:35:05,023 [Thread-2579] INFO  ",
                                                  "2017-11-16 16:35:05,023 [Thread-2579] INFO  --- kie-maven-plugin:7.5.0-SNAPSHOT:build (default-build) @ mortgages ---",
                                                  "2017-11-16 16:35:05,045 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,046 [Thread-2579] INFO  Discovered kie.conf url=jar:file:/Users/porcelli/.m2/repository/org/drools/drools-core/7.5.0-SNAPSHOT/drools-core-7.5.0-SNAPSHOT.jar!/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,049 [Thread-2579] INFO  Adding Service org.drools.core.io.impl.ResourceFactoryServiceImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,052 [Thread-2579] INFO  Adding Service org.drools.core.marshalling.impl.MarshallerProviderImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,053 [Thread-2579] INFO  Adding Service org.drools.core.concurrent.ExecutorProviderImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,053 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,053 [Thread-2579] INFO  Discovered kie.conf url=jar:file:/Users/porcelli/.m2/repository/org/drools/drools-compiler/7.5.0-SNAPSHOT/drools-compiler-7.5.0-SNAPSHOT.jar!/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,060 [Thread-2579] INFO  Adding Service org.drools.compiler.kie.builder.impl.KieServicesImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,064 [Thread-2579] INFO  Adding Service org.drools.compiler.builder.impl.KnowledgeBuilderFactoryServiceImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,064 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,065 [Thread-2579] INFO  Discovered kie.conf url=jar:file:/Users/porcelli/.m2/repository/org/kie/kie-internal/7.5.0-SNAPSHOT/kie-internal-7.5.0-SNAPSHOT.jar!/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,066 [Thread-2579] INFO  Adding Service org.kie.internal.services.KieWeaversImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,067 [Thread-2579] INFO  Adding Service org.kie.internal.services.KieBeliefsImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,068 [Thread-2579] INFO  Adding Service org.kie.internal.services.KieAssemblersImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,069 [Thread-2579] INFO  Adding Service org.kie.internal.services.KieRuntimesImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,069 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,070 [Thread-2579] INFO  Discovered kie.conf url=jar:file:/Users/porcelli/.m2/repository/org/drools/drools-decisiontables/7.5.0-SNAPSHOT/drools-decisiontables-7.5.0-SNAPSHOT.jar!/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,071 [Thread-2579] INFO  Adding Service org.drools.decisiontable.DecisionTableProviderImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,071 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,072 [Thread-2579] INFO  Discovered kie.conf url=jar:file:/Users/porcelli/.m2/repository/org/drools/drools-scorecards/7.5.0-SNAPSHOT/drools-scorecards-7.5.0-SNAPSHOT.jar!/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,073 [Thread-2579] INFO  Adding Service org.drools.scorecards.ScoreCardProviderImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,073 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,073 [Thread-2579] INFO  Discovered kie.conf url=jar:file:/Users/porcelli/.m2/repository/org/drools/drools-pmml/7.5.0-SNAPSHOT/drools-pmml-7.5.0-SNAPSHOT.jar!/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,116 [Thread-2579] INFO  Adding Service org.drools.pmml.pmml_4_2.PMML4Compiler",
                                                  "",
                                                  "2017-11-16 16:35:05,117 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,117 [Thread-2579] INFO  Discovered kie.conf url=jar:file:/Users/porcelli/.m2/repository/org/jbpm/jbpm-bpmn2/7.5.0-SNAPSHOT/jbpm-bpmn2-7.5.0-SNAPSHOT.jar!/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,328 [Thread-2579] INFO  Adding Service org.jbpm.bpmn2.BPMN2ProcessProviderImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,329 [Thread-2579] INFO  Adding Service org.jbpm.bpmn2.xml.XmlProcessDumperFactoryServiceImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,329 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,329 [Thread-2579] INFO  Discovered kie.conf url=jar:file:/Users/porcelli/.m2/repository/org/jbpm/jbpm-flow-builder/7.5.0-SNAPSHOT/jbpm-flow-builder-7.5.0-SNAPSHOT.jar!/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,330 [Thread-2579] INFO  Adding Service org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,330 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,330 [Thread-2579] INFO  Discovered kie.conf url=jar:file:/Users/porcelli/.m2/repository/org/jbpm/jbpm-flow/7.5.0-SNAPSHOT/jbpm-flow-7.5.0-SNAPSHOT.jar!/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,331 [Thread-2579] INFO  Adding Service org.jbpm.marshalling.impl.ProcessMarshallerFactoryServiceImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,333 [Thread-2579] INFO  Adding Service org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,333 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,333 [Thread-2579] INFO  Discovered kie.conf url=jar:file:/Users/porcelli/.m2/repository/org/drools/drools-workbench-models-guided-dtable/7.5.0-SNAPSHOT/drools-workbench-models-guided-dtable-7.5.0-SNAPSHOT.jar!/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,335 [Thread-2579] INFO  Adding Service org.drools.workbench.models.guided.dtable.backend.GuidedDecisionTableProviderImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,335 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,335 [Thread-2579] INFO  Discovered kie.conf url=jar:file:/Users/porcelli/.m2/repository/org/drools/drools-workbench-models-guided-template/7.5.0-SNAPSHOT/drools-workbench-models-guided-template-7.5.0-SNAPSHOT.jar!/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,340 [Thread-2579] INFO  Adding Service org.drools.workbench.models.guided.template.backend.GuidedRuleTemplateProviderImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,340 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,340 [Thread-2579] INFO  Discovered kie.conf url=jar:file:/Users/porcelli/.m2/repository/org/drools/drools-workbench-models-guided-scorecard/7.5.0-SNAPSHOT/drools-workbench-models-guided-scorecard-7.5.0-SNAPSHOT.jar!/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,341 [Thread-2579] INFO  Adding Service org.drools.workbench.models.guided.scorecard.backend.GuidedScoreCardProviderImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,341 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,341 [Thread-2579] INFO  Discovered kie.conf url=jar:file:/Users/porcelli/.m2/repository/org/optaplanner/optaplanner-core/7.5.0-SNAPSHOT/optaplanner-core-7.5.0-SNAPSHOT.jar!/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,343 [Thread-2579] INFO  Adding Service +org.optaplanner.core.impl.solver.kie.KieSolverAssemblerService",
                                                  "",
                                                  "2017-11-16 16:35:05,343 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,344 [Thread-2579] INFO  Discovered kie.conf url=jar:file:/Users/porcelli/.m2/repository/org/kie/kie-dmn-core/7.5.0-SNAPSHOT/kie-dmn-core-7.5.0-SNAPSHOT.jar!/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,345 [Thread-2579] INFO  Adding Service +org.kie.dmn.core.weaver.DMNWeaverService",
                                                  "",
                                                  "2017-11-16 16:35:05,347 [Thread-2579] INFO  Adding Service +org.kie.dmn.core.assembler.DMNAssemblerService",
                                                  "",
                                                  "2017-11-16 16:35:05,348 [Thread-2579] INFO  Adding Service +org.kie.dmn.core.runtime.DMNRuntimeService",
                                                  "",
                                                  "2017-11-16 16:35:05,349 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,349 [Thread-2579] INFO  Discovered kie.conf url=vfs:/Users/porcelli/Library/Caches/IntelliJIdea15/gwt/drools-wb.2b242f68/drools-wb-webapp.8cb26b02/run/www/WEB-INF/lib/drools-compiler-7.5.0-20171115.192413-87.jar/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,349 [Thread-2579] INFO  Adding Service org.drools.compiler.kie.builder.impl.KieServicesImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,349 [Thread-2579] INFO  Adding Service org.drools.compiler.builder.impl.KnowledgeBuilderFactoryServiceImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,349 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,349 [Thread-2579] INFO  Discovered kie.conf url=vfs:/Users/porcelli/Library/Caches/IntelliJIdea15/gwt/drools-wb.2b242f68/drools-wb-webapp.8cb26b02/run/www/WEB-INF/lib/drools-core-7.5.0-20171115.191918-88.jar/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,349 [Thread-2579] INFO  Adding Service org.drools.core.io.impl.ResourceFactoryServiceImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,349 [Thread-2579] INFO  Adding Service org.drools.core.marshalling.impl.MarshallerProviderImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,349 [Thread-2579] INFO  Adding Service org.drools.core.concurrent.ExecutorProviderImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,349 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,349 [Thread-2579] INFO  Discovered kie.conf url=vfs:/Users/porcelli/Library/Caches/IntelliJIdea15/gwt/drools-wb.2b242f68/drools-wb-webapp.8cb26b02/run/www/WEB-INF/lib/drools-decisiontables-7.5.0-20171115.192945-86.jar/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,349 [Thread-2579] INFO  Adding Service org.drools.decisiontable.DecisionTableProviderImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,349 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,349 [Thread-2579] INFO  Discovered kie.conf url=vfs:/Users/porcelli/Library/Caches/IntelliJIdea15/gwt/drools-wb.2b242f68/drools-wb-webapp.8cb26b02/run/www/WEB-INF/lib/drools-pmml-7.5.0-20171115.192903-87.jar/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,364 [Thread-2579] INFO  Adding Service org.drools.pmml.pmml_4_2.PMML4Compiler",
                                                  "",
                                                  "2017-11-16 16:35:05,364 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,364 [Thread-2579] INFO  Discovered kie.conf url=vfs:/Users/porcelli/Library/Caches/IntelliJIdea15/gwt/drools-wb.2b242f68/drools-wb-webapp.8cb26b02/run/www/WEB-INF/lib/drools-scorecards-7.5.0-20171115.193248-86.jar/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,365 [Thread-2579] INFO  Adding Service org.drools.scorecards.ScoreCardProviderImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,365 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,365 [Thread-2579] INFO  Discovered kie.conf url=vfs:/Users/porcelli/Library/Caches/IntelliJIdea15/gwt/drools-wb.2b242f68/drools-wb-webapp.8cb26b02/run/www/WEB-INF/lib/drools-workbench-models-guided-dtable-7.5.0-20171115.193103-86-sources.jar/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,365 [Thread-2579] INFO  Adding Service org.drools.workbench.models.guided.dtable.backend.GuidedDecisionTableProviderImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,365 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,365 [Thread-2579] INFO  Discovered kie.conf url=vfs:/Users/porcelli/Library/Caches/IntelliJIdea15/gwt/drools-wb.2b242f68/drools-wb-webapp.8cb26b02/run/www/WEB-INF/lib/drools-workbench-models-guided-dtable-7.5.0-20171115.193103-86.jar/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,365 [Thread-2579] INFO  Adding Service org.drools.workbench.models.guided.dtable.backend.GuidedDecisionTableProviderImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,365 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,365 [Thread-2579] INFO  Discovered kie.conf url=vfs:/Users/porcelli/Library/Caches/IntelliJIdea15/gwt/drools-wb.2b242f68/drools-wb-webapp.8cb26b02/run/www/WEB-INF/lib/drools-workbench-models-guided-scorecard-7.5.0-20171115.193336-86-sources.jar/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,365 [Thread-2579] INFO  Adding Service org.drools.workbench.models.guided.scorecard.backend.GuidedScoreCardProviderImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,365 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,365 [Thread-2579] INFO  Discovered kie.conf url=vfs:/Users/porcelli/Library/Caches/IntelliJIdea15/gwt/drools-wb.2b242f68/drools-wb-webapp.8cb26b02/run/www/WEB-INF/lib/drools-workbench-models-guided-scorecard-7.5.0-20171115.193336-86.jar/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,365 [Thread-2579] INFO  Adding Service org.drools.workbench.models.guided.scorecard.backend.GuidedScoreCardProviderImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,365 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,365 [Thread-2579] INFO  Discovered kie.conf url=vfs:/Users/porcelli/Library/Caches/IntelliJIdea15/gwt/drools-wb.2b242f68/drools-wb-webapp.8cb26b02/run/www/WEB-INF/lib/drools-workbench-models-guided-template-7.5.0-20171115.193119-86-sources.jar/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,365 [Thread-2579] INFO  Adding Service org.drools.workbench.models.guided.template.backend.GuidedRuleTemplateProviderImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,365 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,365 [Thread-2579] INFO  Discovered kie.conf url=vfs:/Users/porcelli/Library/Caches/IntelliJIdea15/gwt/drools-wb.2b242f68/drools-wb-webapp.8cb26b02/run/www/WEB-INF/lib/drools-workbench-models-guided-template-7.5.0-20171115.193119-86.jar/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,365 [Thread-2579] INFO  Adding Service org.drools.workbench.models.guided.template.backend.GuidedRuleTemplateProviderImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,365 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,365 [Thread-2579] INFO  Discovered kie.conf url=vfs:/Users/porcelli/Library/Caches/IntelliJIdea15/gwt/drools-wb.2b242f68/drools-wb-webapp.8cb26b02/run/www/WEB-INF/lib/jbpm-bpmn2-7.5.0-20171115.195114-94.jar/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,365 [Thread-2579] INFO  Adding Service org.jbpm.bpmn2.BPMN2ProcessProviderImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,365 [Thread-2579] INFO  Adding Service org.jbpm.bpmn2.xml.XmlProcessDumperFactoryServiceImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,365 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,365 [Thread-2579] INFO  Discovered kie.conf url=vfs:/Users/porcelli/Library/Caches/IntelliJIdea15/gwt/drools-wb.2b242f68/drools-wb-webapp.8cb26b02/run/www/WEB-INF/lib/jbpm-flow-7.5.0-20171115.194115-101.jar/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,365 [Thread-2579] INFO  Adding Service org.jbpm.marshalling.impl.ProcessMarshallerFactoryServiceImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,365 [Thread-2579] INFO  Adding Service org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,366 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,366 [Thread-2579] INFO  Discovered kie.conf url=vfs:/Users/porcelli/Library/Caches/IntelliJIdea15/gwt/drools-wb.2b242f68/drools-wb-webapp.8cb26b02/run/www/WEB-INF/lib/jbpm-flow-builder-7.5.0-20171115.194217-101.jar/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,366 [Thread-2579] INFO  Adding Service org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,366 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,366 [Thread-2579] INFO  Discovered kie.conf url=vfs:/Users/porcelli/Library/Caches/IntelliJIdea15/gwt/drools-wb.2b242f68/drools-wb-webapp.8cb26b02/run/www/WEB-INF/lib/kie-ci-7.5.0-SNAPSHOT.jar/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,366 [Thread-2579] INFO  Adding Service org.kie.scanner.KieScannerFactoryServiceImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,367 [Thread-2579] INFO  Adding Service org.kie.scanner.MavenClassLoaderResolver",
                                                  "",
                                                  "2017-11-16 16:35:05,367 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,367 [Thread-2579] INFO  Discovered kie.conf url=vfs:/Users/porcelli/Library/Caches/IntelliJIdea15/gwt/drools-wb.2b242f68/drools-wb-webapp.8cb26b02/run/www/WEB-INF/lib/kie-internal-7.5.0-20171115.191232-67.jar/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,367 [Thread-2579] INFO  Adding Service org.kie.internal.services.KieWeaversImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,367 [Thread-2579] INFO  Adding Service org.kie.internal.services.KieBeliefsImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,367 [Thread-2579] INFO  Adding Service org.kie.internal.services.KieAssemblersImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,367 [Thread-2579] INFO  Adding Service org.kie.internal.services.KieRuntimesImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,367 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,367 [Thread-2579] INFO  Discovered kie.conf url=vfs:/Users/porcelli/Library/Caches/IntelliJIdea15/gwt/drools-wb.2b242f68/drools-wb-webapp.8cb26b02/run/www/WEB-INF/lib/optaplanner-core-7.5.0-20171115.194058-93.jar/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,367 [Thread-2579] INFO  Adding Service +org.optaplanner.core.impl.solver.kie.KieSolverAssemblerService",
                                                  "",
                                                  "2017-11-16 16:35:05,367 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,367 [Thread-2579] INFO  Discovered kie.conf url=jar:file:/Users/porcelli/.m2/repository/org/kie/kie-internal/7.5.0-SNAPSHOT/kie-internal-7.5.0-SNAPSHOT.jar!/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,367 [Thread-2579] INFO  Adding Service org.kie.internal.services.KieWeaversImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,367 [Thread-2579] INFO  Adding Service org.kie.internal.services.KieBeliefsImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,367 [Thread-2579] INFO  Adding Service org.kie.internal.services.KieAssemblersImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,367 [Thread-2579] INFO  Adding Service org.kie.internal.services.KieRuntimesImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,367 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,368 [Thread-2579] INFO  Discovered kie.conf url=jar:file:/Users/porcelli/.m2/repository/org/drools/drools-core/7.5.0-SNAPSHOT/drools-core-7.5.0-SNAPSHOT.jar!/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,368 [Thread-2579] INFO  Adding Service org.drools.core.io.impl.ResourceFactoryServiceImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,368 [Thread-2579] INFO  Adding Service org.drools.core.marshalling.impl.MarshallerProviderImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,368 [Thread-2579] INFO  Adding Service org.drools.core.concurrent.ExecutorProviderImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,368 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,368 [Thread-2579] INFO  Discovered kie.conf url=jar:file:/Users/porcelli/.m2/repository/org/optaplanner/optaplanner-core/7.5.0-SNAPSHOT/optaplanner-core-7.5.0-SNAPSHOT.jar!/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,368 [Thread-2579] INFO  Adding Service +org.optaplanner.core.impl.solver.kie.KieSolverAssemblerService",
                                                  "",
                                                  "2017-11-16 16:35:05,368 [Thread-2579] INFO  Loading kie.conf from  ",
                                                  "2017-11-16 16:35:05,368 [Thread-2579] INFO  Discovered kie.conf url=jar:file:/Users/porcelli/.m2/repository/org/drools/drools-compiler/7.5.0-SNAPSHOT/drools-compiler-7.5.0-SNAPSHOT.jar!/META-INF/kie.conf ",
                                                  "2017-11-16 16:35:05,368 [Thread-2579] INFO  Adding Service org.drools.compiler.kie.builder.impl.KieServicesImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,368 [Thread-2579] INFO  Adding Service org.drools.compiler.builder.impl.KnowledgeBuilderFactoryServiceImpl",
                                                  "",
                                                  "2017-11-16 16:35:05,387 [Thread-2579] INFO  Adding KieModule from resource: FileResource[file=/var/folders/j4/86jpk9rx5rzdrbtrxwsfv0pr0000gn/T/maven/41643253-9e34-4768-985d-74a2f3728b7d/myrepo/mortgages/src/main/resources]",
                                                  "2017-11-16 16:35:05,670 [Thread-2579] WARN  Unable to find pom.properties in /var/folders/j4/86jpk9rx5rzdrbtrxwsfv0pr0000gn/T/maven/41643253-9e34-4768-985d-74a2f3728b7d/myrepo/mortgages/src/main/resources",
                                                  "2017-11-16 16:35:05,681 [Thread-2579] INFO  Recursed up folders, found and used pom.xml /var/folders/j4/86jpk9rx5rzdrbtrxwsfv0pr0000gn/T/maven/41643253-9e34-4768-985d-74a2f3728b7d/myrepo/mortgages/pom.xml",
                                                  "2017-11-16 16:35:05,693 [Thread-2579] INFO  KieModule was added: FileKieModule[releaseId=org.kie.workbench.playground:mortgages:1.0.0-SNAPSHOT,file=/var/folders/j4/86jpk9rx5rzdrbtrxwsfv0pr0000gn/T/maven/41643253-9e34-4768-985d-74a2f3728b7d/myrepo/mortgages/src/main/resources]",
                                                  "2017-11-16 16:35:09,445 [Thread-2579] ERROR Unable to build KieBaseModel:defaultKieBase",
                                                  "Unable to resolve ObjectType 'Applicant' : [Rule name='No bad credit checks']",
                                                  "",
                                                  "Unable to resolve ObjectType 'Applicant' : [Rule name='No bad credit checks']",
                                                  "",
                                                  "Unable to resolve ObjectType 'Applicant' : [Rule name='Underage']",
                                                  "",
                                                  "Unable to resolve ObjectType 'Applicant' : [Rule name='CreditApproval']",
                                                  "",
                                                  "Unable to Analyse Expression applicant.setApproved(true);:",
                                                  "[Error: unable to resolve method using strict-mode: org.drools.core.spi.KnowledgeHelper.applicant()]",
                                                  "[Near : {... applicant.setApproved(true); ....}]",
                                                  "             ^",
                                                  "[Line: 5, Column: 0] : [Rule name='CreditApproval']",
                                                  "",
                                                  "Unable to resolve ObjectType 'Applicant' : [Rule name='RegexDslRule']",
                                                  "",
                                                  "Rule Compilation error : [Rule name='Dummy rule']",
                                                  "\tmortgages/mortgages/Rule_Dummy_rule994743581.java (2:146) : Only a type can be imported. mortgages.mortgages.Applicant resolves to a package",
                                                  "",
                                                  "Error importing : 'mortgages.mortgages.Applicant'",
                                                  "",
                                                  "2017-11-16 16:35:09,488 [Thread-2579] INFO  KieModelMetaInfo available in the map shared with the Maven Embedder",
                                                  "2017-11-16 16:35:09,489 [Thread-2579] INFO  KieModule available in the map shared with the Maven Embedder",
                                                  "2017-11-16 16:35:09,490 [Thread-2579] INFO  TypesMetaInfo keys available in the map shared with the Maven Embedder",
                                                  "2017-11-16 16:35:09,490 [Thread-2579] ERROR Message [id=1, kieBase=defaultKieBase, level=ERROR, path=mortgages/mortgages/No bad credit checks.rdrl, line=27, column=0",
                                                  "   text=Unable to resolve ObjectType 'Applicant']",
                                                  "2017-11-16 16:35:09,490 [Thread-2579] ERROR Message [id=2, kieBase=defaultKieBase, level=ERROR, path=mortgages/mortgages/No bad credit checks.rdrl, line=27, column=0",
                                                  "   text=Unable to resolve ObjectType 'Applicant']",
                                                  "2017-11-16 16:35:09,490 [Thread-2579] ERROR Message [id=3, kieBase=defaultKieBase, level=ERROR, path=mortgages/mortgages/Underage.rdrl, line=27, column=0",
                                                  "   text=Unable to resolve ObjectType 'Applicant']",
                                                  "2017-11-16 16:35:09,490 [Thread-2579] ERROR Message [id=4, kieBase=defaultKieBase, level=ERROR, path=mortgages/mortgages/CreditApproval.rdslr, line=8, column=0",
                                                  "   text=Unable to resolve ObjectType 'Applicant']",
                                                  "2017-11-16 16:35:09,490 [Thread-2579] ERROR Message [id=5, kieBase=defaultKieBase, level=ERROR, path=mortgages/mortgages/CreditApproval.rdslr, line=5, column=0",
                                                  "   text=Unable to Analyse Expression applicant.setApproved(true);:",
                                                  "[Error: unable to resolve method using strict-mode: org.drools.core.spi.KnowledgeHelper.applicant()]",
                                                  "[Near : {... applicant.setApproved(true); ....}]",
                                                  "             ^",
                                                  "[Line: 5, Column: 0]]",
                                                  "2017-11-16 16:35:09,490 [Thread-2579] ERROR Message [id=6, kieBase=defaultKieBase, level=ERROR, path=mortgages/mortgages/RegexDslRule.rdslr, line=8, column=0",
                                                  "   text=Unable to resolve ObjectType 'Applicant']",
                                                  "2017-11-16 16:35:09,490 [Thread-2579] ERROR Message [id=7, kieBase=defaultKieBase, level=ERROR, path=mortgages/mortgages/Dummy rule.drl, line=19, column=0",
                                                  "   text=Rule Compilation error Only a type can be imported. mortgages.mortgages.Applicant resolves to a package]",
                                                  "2017-11-16 16:35:09,490 [Thread-2579] ERROR Message [id=8, kieBase=defaultKieBase, level=ERROR, path=mortgages/mortgages/No bad credit checks.rdrl, line=1, column=0",
                                                  "   text=Error importing : 'mortgages.mortgages.Applicant']",
                                                  "2017-11-16 16:35:09,490 [Thread-2579] INFO  ------------------------------------------------------------------------",
                                                  "2017-11-16 16:35:09,490 [Thread-2579] INFO  BUILD FAILURE",
                                                  "2017-11-16 16:35:09,490 [Thread-2579] INFO  ------------------------------------------------------------------------",
                                                  "2017-11-16 16:35:09,490 [Thread-2579] INFO  Total time: 7.664 s",
                                                  "2017-11-16 16:35:09,491 [Thread-2579] INFO  Finished at: 2017-11-16T16:35:09-05:00",
                                                  "2017-11-16 16:35:15,768 [Thread-2579] INFO  Final Memory: 1685M/4049M",
                                                  "2017-11-16 16:35:15,769 [Thread-2579] INFO  ------------------------------------------------------------------------",
                                                  "2017-11-16 16:35:15,770 [Thread-2579] ERROR Failed to execute goal org.kie:kie-maven-plugin:7.5.0-SNAPSHOT:build (default-build) on project mortgages: Build failed! -> [Help 1]",
                                                  "2017-11-16 16:35:15,770 [Thread-2579] ERROR Failed to execute goal org.kie:kie-maven-plugin:7.5.0-SNAPSHOT:build (default-build) on project mortgages: Build failed! -> [Help 1]",
                                                  "2017-11-16 16:35:15,770 [Thread-2579] ERROR ",
                                                  "2017-11-16 16:35:15,770 [Thread-2579] ERROR ",
                                                  "2017-11-16 16:35:15,770 [Thread-2579] ERROR To see the full stack trace of the errors, re-run Maven with the -e switch.",
                                                  "2017-11-16 16:35:15,770 [Thread-2579] ERROR To see the full stack trace of the errors, re-run Maven with the -e switch.",
                                                  "2017-11-16 16:35:15,770 [Thread-2579] ERROR Re-run Maven using the -X switch to enable full debug logging.",
                                                  "2017-11-16 16:35:15,770 [Thread-2579] ERROR Re-run Maven using the -X switch to enable full debug logging.",
                                                  "2017-11-16 16:35:15,770 [Thread-2579] ERROR ",
                                                  "2017-11-16 16:35:15,770 [Thread-2579] ERROR ",
                                                  "2017-11-16 16:35:15,770 [Thread-2579] ERROR For more information about the errors and possible solutions, please read the following articles:",
                                                  "2017-11-16 16:35:15,770 [Thread-2579] ERROR For more information about the errors and possible solutions, please read the following articles:",
                                                  "2017-11-16 16:35:15,770 [Thread-2579] ERROR [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoFailureException",
                                                  "2017-11-16 16:35:15,770 [Thread-2579] ERROR [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoFailureException");

        final BuildResults results = MavenOutputConverter.convertIntoBuildResults(output,
                                                                                  path,
                                                                                  "/var/folders/j4/86jpk9rx5rzdrbtrxwsfv0pr0000gn/T/maven/41643253-9e34-4768-985d-74a2f3728b7d/myrepo/mortgages/");

        for (BuildMessage message : results.getErrorMessages()) {
            System.out.println(message.getText());
        }

        assertEquals(4, results.getErrorMessages().size());
        assertEquals("\tstatic final xxxx serialVersionUID = 1L;", results.getErrorMessages().get(0).getText());
        assertEquals("\t             ^^^^", results.getErrorMessages().get(1).getText());
        assertEquals("xxxx cannot be resolved to a type", results.getErrorMessages().get(2).getText());
        assertEquals("2. WARNING: CAL10NAnnotationProcessor 0.8.1 initialized", results.getErrorMessages().get(3).getText());

        for (final BuildMessage buildMessage : results.getErrorMessages()) {
            assertEquals(convert(resultPath), buildMessage.getPath());
            assertEquals(0, buildMessage.getColumn());
            assertEquals(21, buildMessage.getLine());
            assertEquals(Level.ERROR, buildMessage.getLevel());
        }
    }
}
