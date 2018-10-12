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
package org.kie.workbench.common.services.backend.maven.plugins.dependency.analyze;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.artifact.filter.StrictPatternExcludesArtifactFilter;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalysis;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzer;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzerException;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;

/**
 * Copy with changes of maven-dependency-plugin-3.1.1
 * Analyzes the dependencies of this project and determines which are: used and declared; used and undeclared; unused
 * and declared.
 */
public abstract class AbstractAnalyzeMojo
    extends AbstractMojo
    implements Contextualizable
{
    // fields -----------------------------------------------------------------

    /**
     * Key used to share the string classpath in the kieMap
     */
    private final String STRING_UNUSED_DEPS_KEY = "stringUnusedDepsKey";

    /**
     * This container is the same accessed in the KieMavenCli in the kie-wb-common
     */
    @Inject
    private PlexusContainer container;

    /**
     * The plexus context to look-up the right {@link ProjectDependencyAnalyzer} implementation depending on the mojo
     * configuration.
     */
    private Context context;

    /**
     * The Maven project to analyze.
     */
    @Parameter( defaultValue = "${project}", readonly = true, required = true )
    private MavenProject project;

    /**
     * Specify the project dependency analyzer to use (plexus component role-hint). By default,
     * <a href="/shared/maven-dependency-analyzer/">maven-dependency-analyzer</a> is used. To use this, you must declare
     * a dependency for this plugin that contains the code for the analyzer. The analyzer must have a declared Plexus
     * role name, and you specify the role name here.
     *
     * @since 2.2
     */
    @Parameter( property = "analyzer", defaultValue = "default" )
    private String analyzer;

    /**
     * Whether to fail the build if a dependency warning is found.
     */
    @Parameter( property = "failOnWarning", defaultValue = "false" )
    private boolean failOnWarning;

    /**
     * Output used dependencies.
     */
    @Parameter( property = "verbose", defaultValue = "false" )
    private boolean verbose;

    /**
     * Ignore Runtime/Provided/Test/System scopes for unused dependency analysis.
     */
    @Parameter( property = "ignoreNonCompile", defaultValue = "false" )
    private boolean ignoreNonCompile;

    /**
     * Output the xml for the missing dependencies (used but not declared).
     *
     * @since 2.0-alpha-5
     */
    @Parameter( property = "outputXML", defaultValue = "false" )
    private boolean outputXML;

    /**
     * Output scriptable values for the missing dependencies (used but not declared).
     *
     * @since 2.0-alpha-5
     */
    @Parameter( property = "scriptableOutput", defaultValue = "false" )
    private boolean scriptableOutput;

    /**
     * Flag to use for scriptable output.
     *
     * @since 2.0-alpha-5
     */
    @Parameter( property = "scriptableFlag", defaultValue = "$$$%%%" )
    private String scriptableFlag;

    /**
     * Flag to use for scriptable output
     *
     * @since 2.0-alpha-5
     */
    @Parameter( defaultValue = "${basedir}", readonly = true )
    private File baseDir;

    /**
     * Target folder
     *
     * @since 2.0-alpha-5
     */
    @Parameter( defaultValue = "${project.build.directory}", readonly = true )
    private File outputDirectory;

    /**
     * Force dependencies as used, to override incomplete result caused by bytecode-level analysis. Dependency format is
     * <code>groupId:artifactId</code>.
     *
     * @since 2.6
     */
    @Parameter
    private String[] usedDependencies;

    /**
     * Skip plugin execution completely.
     *
     * @since 2.7
     */
    @Parameter( property = "mdep.analyze.skip", defaultValue = "false" )
    private boolean skip;

    /**
     * List of dependencies that will be ignored. Any dependency on this list will be excluded from the "declared but
     * unused" and the "used but undeclared" list. The filter syntax is:
     *
     * <pre>
     * [groupId]:[artifactId]:[type]:[version]
     * </pre>
     *
     * where each pattern segment is optional and supports full and partial <code>*</code> wildcards. An empty pattern
     * segment is treated as an implicit wildcard. *
     * <p>
     * For example, <code>org.apache.*</code> will match all artifacts whose group id starts with
     * <code>org.apache.</code>, and <code>:::*-SNAPSHOT</code> will match all snapshot artifacts.
     * </p>
     *
     * @since 2.10
     * @see StrictPatternIncludesArtifactFilter
     */
    @Parameter
    private String[] ignoredDependencies = new String[0];

    /**
     * List of dependencies that will be ignored if they are used but undeclared. The filter syntax is:
     *
     * <pre>
     * [groupId]:[artifactId]:[type]:[version]
     * </pre>
     *
     * where each pattern segment is optional and supports full and partial <code>*</code> wildcards. An empty pattern
     * segment is treated as an implicit wildcard. *
     * <p>
     * For example, <code>org.apache.*</code> will match all artifacts whose group id starts with
     * <code>org.apache.</code>, and <code>:::*-SNAPSHOT</code> will match all snapshot artifacts.
     * </p>
     *
     * @since 2.10
     * @see StrictPatternIncludesArtifactFilter
     */
    @Parameter
    private String[] ignoredUsedUndeclaredDependencies = new String[0];

    /**
     * List of dependencies that will be ignored if they are declared but unused. The filter syntax is:
     *
     * <pre>
     * [groupId]:[artifactId]:[type]:[version]
     * </pre>
     *
     * where each pattern segment is optional and supports full and partial <code>*</code> wildcards. An empty pattern
     * segment is treated as an implicit wildcard. *
     * <p>
     * For example, <code>org.apache.*</code> will match all artifacts whose group id starts with
     * <code>org.apache.</code>, and <code>:::*-SNAPSHOT</code> will match all snapshot artifacts.
     * </p>
     *
     * @since 2.10
     * @see StrictPatternIncludesArtifactFilter
     */
    @Parameter
    private String[] ignoredUnusedDeclaredDependencies = new String[0];


    // Mojo methods -----------------------------------------------------------

    /*
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( isSkip() )
        {
            getLog().info( "Skipping plugin execution" );
            return;
        }

        if ( "pom".equals( project.getPackaging() ) )
        {
            getLog().info( "Skipping pom project" );
            return;
        }

        if ( outputDirectory == null || !outputDirectory.exists() )
        {
            getLog().info( "Skipping project with no build directory" );
            return;
        }

        boolean warning = checkDependencies();

        if ( warning && failOnWarning )
        {
            throw new MojoExecutionException( "Dependency problems found" );
        }
    }

    /**
     * @return {@link ProjectDependencyAnalyzer}
     * @throws MojoExecutionException in case of an error.
     */
    protected ProjectDependencyAnalyzer createProjectDependencyAnalyzer()
        throws MojoExecutionException
    {

        final String role = ProjectDependencyAnalyzer.ROLE;
        final String roleHint = analyzer;

        try
        {
            final PlexusContainer container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );

            return (ProjectDependencyAnalyzer) container.lookup( role, roleHint );
        }
        catch ( Exception exception )
        {
            throw new MojoExecutionException( "Failed to instantiate ProjectDependencyAnalyser with role " + role
                + " / role-hint " + roleHint, exception );
        }
    }

    @Override
    public void contextualize( Context theContext )
        throws ContextException
    {
        this.context = theContext;
    }

    /**
     * @return {@link #skip}
     */
    protected final boolean isSkip()
    {
        return skip;
    }

    // private methods --------------------------------------------------------

    private boolean checkDependencies()
        throws MojoExecutionException
    {
        ProjectDependencyAnalysis analysis;
        try
        {
            analysis = createProjectDependencyAnalyzer().analyze( project );

            if ( usedDependencies != null )
            {
                analysis = analysis.forceDeclaredDependenciesUsage( usedDependencies );
            }
        }
        catch ( ProjectDependencyAnalyzerException exception )
        {
            throw new MojoExecutionException( "Cannot analyze dependencies", exception );
        }

        if ( ignoreNonCompile )
        {
            analysis = analysis.ignoreNonCompile();
        }

        Set<Artifact> usedDeclared = new LinkedHashSet<Artifact>( analysis.getUsedDeclaredArtifacts() );
        Set<Artifact> usedUndeclared = new LinkedHashSet<Artifact>( analysis.getUsedUndeclaredArtifacts() );
        Set<Artifact> unusedDeclared = new LinkedHashSet<Artifact>( analysis.getUnusedDeclaredArtifacts() );

        Set<Artifact> ignoredUsedUndeclared = new LinkedHashSet<Artifact>();
        Set<Artifact> ignoredUnusedDeclared = new LinkedHashSet<Artifact>();

        ignoredUsedUndeclared.addAll( filterDependencies( usedUndeclared, ignoredDependencies ) );
        ignoredUsedUndeclared.addAll( filterDependencies( usedUndeclared, ignoredUsedUndeclaredDependencies ) );

        ignoredUnusedDeclared.addAll( filterDependencies( unusedDeclared, ignoredDependencies ) );
        ignoredUnusedDeclared.addAll( filterDependencies( unusedDeclared, ignoredUnusedDeclaredDependencies ) );

        boolean reported = false;
        boolean warning = false;
        Set<String> unusedDeps = new HashSet<>();

        if ( verbose && !usedDeclared.isEmpty() )
        {
            getLog().info( "Used declared dependencies found:" );

            logArtifacts( analysis.getUsedDeclaredArtifacts(), false );
            reported = true;
        }

        if ( !usedUndeclared.isEmpty() )
        {
            getLog().warn( "Used undeclared dependencies found:" );

            logArtifacts( usedUndeclared, true );
            reported = true;
            warning = true;
        }

        if ( !unusedDeclared.isEmpty() )
        {
            getLog().warn( "Unused declared dependencies found:" );
            for(Artifact artifact: unusedDeclared){
                StringBuffer sb = new StringBuffer();
                sb.append(artifact.getGroupId()).
                        append(":").
                        append(artifact.getArtifactId()).
                        append(":").
                        append(artifact.getVersion()).
                        append(":").
                        append(artifact.getScope());
                unusedDeps.add(sb.toString());
            }
            logArtifacts( unusedDeclared, true );
            reported = true;
            warning = true;
        }

        if ( verbose && !ignoredUsedUndeclared.isEmpty() )
        {
            getLog().info( "Ignored used undeclared dependencies:" );

            logArtifacts( ignoredUsedUndeclared, false );
            reported = true;
        }

        if ( verbose && !ignoredUnusedDeclared.isEmpty() )
        {
            getLog().info( "Ignored unused declared dependencies:" );

            logArtifacts( ignoredUnusedDeclared, false );
            reported = true;
        }

        if ( outputXML )
        {
            writeDependencyXML( usedUndeclared );
        }

        if ( scriptableOutput )
        {
            writeScriptableOutput( usedUndeclared );
        }

        if ( !reported )
        {
            getLog().info( "No dependency problems found" );
        }

        storeUnusedDeps(unusedDeps);
        return warning;
    }

    private void logArtifacts( Set<Artifact> artifacts, boolean warn )
    {
        if ( artifacts.isEmpty() )
        {
            getLog().info( "   None" );
        }
        else
        {
            for ( Artifact artifact : artifacts )
            {
                // called because artifact will set the version to -SNAPSHOT only if I do this. MNG-2961
                artifact.isSnapshot();

                if ( warn )
                {
                    getLog().warn( "   " + artifact );
                }
                else
                {
                    getLog().info( "   " + artifact );
                }

            }
        }
    }

    private void writeDependencyXML( Set<Artifact> artifacts )
    {
        if ( !artifacts.isEmpty() )
        {
            getLog().info( "Add the following to your pom to correct the missing dependencies: " );

            StringWriter out = new StringWriter();
            PrettyPrintXMLWriter writer = new PrettyPrintXMLWriter( out );

            for ( Artifact artifact : artifacts )
            {
                // called because artifact will set the version to -SNAPSHOT only if I do this. MNG-2961
                artifact.isSnapshot();

                writer.startElement( "dependency" );
                writer.startElement( "groupId" );
                writer.writeText( artifact.getGroupId() );
                writer.endElement();
                writer.startElement( "artifactId" );
                writer.writeText( artifact.getArtifactId() );
                writer.endElement();
                writer.startElement( "version" );
                writer.writeText( artifact.getBaseVersion() );
                if ( !isBlank( artifact.getClassifier() ) )
                {
                    writer.startElement( "classifier" );
                    writer.writeText( artifact.getClassifier() );
                    writer.endElement();
                }
                writer.endElement();

                if ( !Artifact.SCOPE_COMPILE.equals( artifact.getScope() ) )
                {
                    writer.startElement( "scope" );
                    writer.writeText( artifact.getScope() );
                    writer.endElement();
                }
                writer.endElement();
            }

            getLog().info( "\n" + out.getBuffer() );
        }
    }

    private void writeScriptableOutput( Set<Artifact> artifacts )
    {
        if ( !artifacts.isEmpty() )
        {
            getLog().info( "Missing dependencies: " );
            String pomFile = baseDir.getAbsolutePath() + File.separatorChar + "pom.xml";
            StringBuilder buf = new StringBuilder();

            for ( Artifact artifact : artifacts )
            {
                // called because artifact will set the version to -SNAPSHOT only if I do this. MNG-2961
                artifact.isSnapshot();

                //CHECKSTYLE_OFF: LineLength
                buf.append( scriptableFlag )
                   .append( ":" )
                   .append( pomFile )
                   .append( ":" )
                   .append( artifact.getDependencyConflictId() )
                   .append( ":" )
                   .append( artifact.getClassifier() )
                   .append( ":" )
                   .append( artifact.getBaseVersion() )
                   .append( ":" )
                   .append( artifact.getScope() )
                   .append( "\n" );
                //CHECKSTYLE_ON: LineLength
            }
            getLog().info( "\n" + buf );
        }
    }

    private List<Artifact> filterDependencies( Set<Artifact> artifacts, String[] excludes )
        throws MojoExecutionException
    {
        ArtifactFilter filter = new StrictPatternExcludesArtifactFilter( Arrays.asList( excludes ) );
        List<Artifact> result = new ArrayList<Artifact>();

        for ( Iterator<Artifact> it = artifacts.iterator(); it.hasNext(); )
        {
            Artifact artifact = it.next();
            if ( !filter.include( artifact ) )
            {
                it.remove();
                result.add( artifact );
            }
        }

        return result;
    }


    /**
     * It stores the specified string into the kieMap.
     * @param unusedDeps the string to be written into the map.
     */
    private void storeUnusedDeps(Set<String> unusedDeps) {
        if (container != null && unusedDeps != null && !unusedDeps.isEmpty()) {
            Optional<Map<String, Object>> optionalKieMap = getKieMap();
            if (optionalKieMap.isPresent()) {
                String compilationID = getCompilationID(optionalKieMap);
                shareStringClasspathWithMap(compilationID, unusedDeps);
            } else {
                getLog().error("Kie Map not present");
            }
        }
    }

    private Optional<Map<String, Object>> getKieMap() {
        try {
            /**
             * Retrieve the map passed into the Plexus container by the MavenEmbedder from the MavenIncrementalCompiler in the kie-wb-common
             */
            Map<String, Object> kieMap = (Map<String,Object>) container.lookup(Map.class,
                                                                               "java.util.HashMap",
                                                                               "kieMap");
            return Optional.of(kieMap);
        } catch (ComponentLookupException cle) {
            getLog().info("kieMap not present with compilationID and container present");
            return Optional.empty();
        }
    }

    private String getCompilationID(Optional<Map<String, Object>> optionalKieMap) {
        Object compilationIDObj = optionalKieMap.get().get("compilation.ID");
        if (compilationIDObj != null) {
            return compilationIDObj.toString();
        } else {
            getLog().error("compilation.ID key not present in the shared map using thread name:"
                                   + Thread.currentThread().getName());
            return Thread.currentThread().getName();
        }
    }

    private void shareStringClasspathWithMap(String compilationID, Set<String> unusedDeps) {
        Optional<Map<String, Object>> optionalKieMap = getKieMap();
        if (optionalKieMap.isPresent() && unusedDeps != null) {
            /*Standard for the kieMap keys -> compilationID + dot + class name or name of the variable if is a String */
            StringBuilder stringUnusedDepsKey = new StringBuilder(compilationID).append(".").append(STRING_UNUSED_DEPS_KEY);
            Object o = optionalKieMap.get().get(stringUnusedDepsKey.toString());
            if (o != null){
                Set<String> depsModules = (Set<String>) o;
                depsModules.addAll(unusedDeps);
            }else{
                optionalKieMap.get().put(stringUnusedDepsKey.toString(), unusedDeps);
            }
            getLog().info("Unused deps available in the map shared with the Maven Embedder with key:" + stringUnusedDepsKey.toString());
        }else{
            getLog().info("No Unused deps to share in the map with the Maven Embedder");
        }
    }

    public boolean isBlank(String str) {
        int strLen;
        if (str != null && (strLen = str.length()) != 0) {
            for(int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }
}
