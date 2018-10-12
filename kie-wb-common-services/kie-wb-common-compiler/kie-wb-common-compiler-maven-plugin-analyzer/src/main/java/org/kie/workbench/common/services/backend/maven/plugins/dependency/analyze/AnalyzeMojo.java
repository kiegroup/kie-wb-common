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
package org.kie.workbench.common.services.backend.maven.plugins.dependency.analyze;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Copy with changes of maven-dependency-plugin-3.1.1
 * Analyzes the dependencies of this project and determines which are: used and declared; used and undeclared; unused
 * and declared. This goal is intended to be used standalone, thus it always executes the <code>test-compile</code>
 * phase - use the <code>dependency:analyze-only</code> goal instead when participating in the build lifecycle.
 * <p>
 * By default, <a href="http://maven.apache.org/shared/maven-dependency-analyzer/">maven-dependency-analyzer</a> is used
 * to perform the analysis, with limitations due to the fact that it works at bytecode level, but any analyzer can be
 * plugged in through <code>analyzer</code> parameter.
 * </p>
 *
 * @author <a href="mailto:markhobson@gmail.com">Mark Hobson</a>
 * @see AnalyzeOnlyMojo
 * @since 2.0-alpha-3
 */
@Mojo( name = "analyze", requiresDependencyResolution = ResolutionScope.TEST, defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true )
public class AnalyzeMojo
    extends AbstractAnalyzeMojo
{
    // subclassed to provide annotations
}
