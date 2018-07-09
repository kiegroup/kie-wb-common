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
 *
 */

package org.kie.workbench.common.services.backend.compiler;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.maven.cli.CLIManager;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MavenParameterTest {
    //@TODO Alex
    @Test
    public void cliParameters() throws ParseException {
        for (int i = 0; i < 600; i++) {
            new Thread(() -> {
                final CLIManager manager = new CLIManager();

                final String[] values = new String[]{"compile",
                        "-s/Users/porcelli/Sources/kiegroup/kie-wb-common/kie-wb-common-services/kie-wb-common-compiler/src/test/settings.xml",
                        "-Dcompilation.ID=eb678741-0b34-409f-903e-addc083ab2aa",
                        "dependency:build-classpath",
                        "-Dmdep.outputFile=module.cpath"};

                try {
                    final CommandLine commandLine = manager.parse(values);
                    System.out.println(commandLine.getArgList());
                    assertThat(commandLine.getArgs().length).isEqualTo(2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
