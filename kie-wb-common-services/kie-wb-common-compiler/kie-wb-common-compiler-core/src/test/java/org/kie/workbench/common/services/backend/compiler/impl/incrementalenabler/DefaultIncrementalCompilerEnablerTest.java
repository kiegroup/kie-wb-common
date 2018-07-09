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
package org.kie.workbench.common.services.backend.compiler.impl.incrementalenabler;

import java.nio.charset.StandardCharsets;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.BaseCompilerTest;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.constants.ResourcesConstants;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.pomprocessor.ProcessedPoms;
import org.kie.workbench.common.services.backend.constants.TestConstants;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Paths;

public class DefaultIncrementalCompilerEnablerTest extends BaseCompilerTest {

    public DefaultIncrementalCompilerEnablerTest() {
        super(ResourcesConstants.DUMMYUNTOUCHED);
    }

    @Test
    public void processTest() {
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo.toAbsolutePath().toString(),
                                                               info,
                                                               new String[]{MavenCLIArgs.INSTALL, MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath},
                                                               Boolean.FALSE);

        byte[] encoded = Files.readAllBytes(Paths.get(tmpRoot + "/dummy/pom.xml"));
        String pomAsAstring = new String(encoded,
                                         StandardCharsets.UTF_8);
        assertThat(pomAsAstring).doesNotContain(TestConstants.KIE_TAKARI_LIFECYCLE_ARTIFACT);

        IncrementalCompilerEnabler enabler = new DefaultIncrementalCompilerEnabler();
        ProcessedPoms poms = enabler.process(req);
        assertThat(poms).isNotNull();
        assertThat(poms.getResult()).isTrue();
        assertThat(poms.getProjectPoms()).hasSize(1);
        String pom = poms.getProjectPoms().get(0);
        assertThat(pom).isEqualTo(tmpRoot.toString() + "/dummy/pom.xml");
        encoded = Files.readAllBytes(Paths.get(tmpRoot + "/dummy/pom.xml"));
        pomAsAstring = new String(encoded,
                                  StandardCharsets.UTF_8);
        assertThat(pomAsAstring).contains(TestConstants.KIE_TAKARI_LIFECYCLE_ARTIFACT);
    }
}
