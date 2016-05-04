/*
 * Copyright 2016 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.server.management.backend;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class ServerImplMigrationTest {

    @Mock
    private IOService ioService;
    @Mock
    private FileSystem fileSystem;

    @Test
    public void testMigration() {

        String migrateServerXml = "<org.kie.server.controller.api.model.KieServerInstance>\n" +
                "  <identifier>test</identifier>\n" +
                "  <name>test</name>\n" +
                "  <version>6.2.0.Final</version>\n" +
                "  <managedInstances>\n" +
                "    <org.kie.server.controller.api.model.KieServerInstanceInfo>\n" +
                "      <location>http://localhost:8080/kie-server-6.2.0.Final-ee7/services/rest/server</location>\n" +
                "      <status>DOWN</status>\n" +
                "    </org.kie.server.controller.api.model.KieServerInstanceInfo>\n" +
                "  </managedInstances>\n" +
                "  <status>DOWN</status>\n" +
                "  <kieServerSetup>\n" +
                "    <containers>\n" +
                "      <org.kie.server.api.model.KieContainerResource>\n" +
                "        <containerId>mortgages</containerId>\n" +
                "        <releaseId>\n" +
                "          <groupId>mortgages</groupId>\n" +
                "          <artifactId>mortgages</artifactId>\n" +
                "          <version>0.0.1</version>\n" +
                "        </releaseId>\n" +
                "        <status>STOPPED</status>\n" +
                "      </org.kie.server.api.model.KieContainerResource>\n" +
                "    </containers>\n" +
                "  </kieServerSetup>\n" +
                "</org.kie.server.controller.api.model.KieServerInstance>";

        String serverImplXml = "<org.kie.workbench.common.screens.server.management.model.impl.ServerRefImpl>\n" +
                "  <id>http://localhost:8080/kie-server-6.2.0.Final-ee7/services/rest/server</id>\n" +
                "  <name>test</name>\n" +
                "  <username>user</username>\n" +
                "  <password>password</password>\n" +
                "  <status>LOADING</status>\n" +
                "  <connectionType>REMOTE</connectionType>\n" +
                "  <properties>\n" +
                "    <entry>\n" +
                "      <string>version</string>\n" +
                "      <string>6.2.0.Final</string>\n" +
                "    </entry>\n" +
                "  </properties>\n" +
                "  <containersRef>\n" +
                "    <entry>\n" +
                "      <string>mortgages</string>\n" +
                "      <org.kie.workbench.common.screens.server.management.model.impl.ContainerRefImpl>\n" +
                "        <serverId>http://localhost:8080/kie-server-6.2.0.Final-ee7/services/rest/server</serverId>\n" +
                "        <id>mortgages</id>\n" +
                "        <status>STOPPED</status>\n" +
                "        <releaseId>\n" +
                "          <groupId>mortgages</groupId>\n" +
                "          <artifactId>mortgages</artifactId>\n" +
                "          <version>0.0.1</version>\n" +
                "        </releaseId>\n" +
                "      </org.kie.workbench.common.screens.server.management.model.impl.ContainerRefImpl>\n" +
                "    </entry>\n" +
                "  </containersRef>\n" +
                "</org.kie.workbench.common.screens.server.management.model.impl.ServerRefImpl>";

        when(ioService.readAllString(any(Path.class))).thenReturn(serverImplXml);
        when(fileSystem.getPath(anyString(), anyString(), anyString())).thenReturn(Mockito.mock(Path.class));

        final List<String> migrated = new ArrayList<String>();

        when(ioService.write(any(Path.class), anyString())).thenAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                migrated.add((String)args[1]);
                return null;
            }
        });

        VFSKieServerControllerStorage storage = new VFSKieServerControllerStorage(ioService, fileSystem);

        Path path = Mockito.mock(Path.class);
        when(path.toString()).thenReturn("test-server");

        Path migratedPath = storage.migrate(path);
        assertNotNull(migratedPath);

        verify(ioService, times( 1 )).delete(any(Path.class));
        verify(ioService, times( 1 )).write(any(Path.class), anyString());

        assertEquals(1, migrated.size());
        assertEquals(migrateServerXml, migrated.get(0));
    }

    @Test
    public void testMigrationServerNoName() {

        String migrateServerXml = "<org.kie.server.controller.api.model.KieServerInstance>\n" +
                "  <identifier>localhost</identifier>\n" +
                "  <name></name>\n" +
                "  <version>6.2.0.Final</version>\n" +
                "  <managedInstances>\n" +
                "    <org.kie.server.controller.api.model.KieServerInstanceInfo>\n" +
                "      <location>http://localhost:8080/kie-server-6.2.0.Final-ee7/services/rest/server</location>\n" +
                "      <status>DOWN</status>\n" +
                "    </org.kie.server.controller.api.model.KieServerInstanceInfo>\n" +
                "  </managedInstances>\n" +
                "  <status>DOWN</status>\n" +
                "  <kieServerSetup>\n" +
                "    <containers>\n" +
                "      <org.kie.server.api.model.KieContainerResource>\n" +
                "        <containerId>mortgages</containerId>\n" +
                "        <releaseId>\n" +
                "          <groupId>mortgages</groupId>\n" +
                "          <artifactId>mortgages</artifactId>\n" +
                "          <version>0.0.1</version>\n" +
                "        </releaseId>\n" +
                "        <status>STOPPED</status>\n" +
                "      </org.kie.server.api.model.KieContainerResource>\n" +
                "    </containers>\n" +
                "  </kieServerSetup>\n" +
                "</org.kie.server.controller.api.model.KieServerInstance>";

        String serverImplXml = "<org.kie.workbench.common.screens.server.management.model.impl.ServerRefImpl>\n" +
                "  <id>http://localhost:8080/kie-server-6.2.0.Final-ee7/services/rest/server</id>\n" +
                "  <name></name>\n" +
                "  <username>user</username>\n" +
                "  <password>password</password>\n" +
                "  <status>LOADING</status>\n" +
                "  <connectionType>REMOTE</connectionType>\n" +
                "  <properties>\n" +
                "    <entry>\n" +
                "      <string>version</string>\n" +
                "      <string>6.2.0.Final</string>\n" +
                "    </entry>\n" +
                "  </properties>\n" +
                "  <containersRef>\n" +
                "    <entry>\n" +
                "      <string>mortgages</string>\n" +
                "      <org.kie.workbench.common.screens.server.management.model.impl.ContainerRefImpl>\n" +
                "        <serverId>http://localhost:8080/kie-server-6.2.0.Final-ee7/services/rest/server</serverId>\n" +
                "        <id>mortgages</id>\n" +
                "        <status>STOPPED</status>\n" +
                "        <releaseId>\n" +
                "          <groupId>mortgages</groupId>\n" +
                "          <artifactId>mortgages</artifactId>\n" +
                "          <version>0.0.1</version>\n" +
                "        </releaseId>\n" +
                "      </org.kie.workbench.common.screens.server.management.model.impl.ContainerRefImpl>\n" +
                "    </entry>\n" +
                "  </containersRef>\n" +
                "</org.kie.workbench.common.screens.server.management.model.impl.ServerRefImpl>";

        when(ioService.readAllString(any(Path.class))).thenReturn(serverImplXml);
        when(fileSystem.getPath(anyString(), anyString(), anyString())).thenReturn(Mockito.mock(Path.class));

        final List<String> migrated = new ArrayList<String>();

        when(ioService.write(any(Path.class), anyString())).thenAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                migrated.add((String)args[1]);
                return null;
            }
        });

        VFSKieServerControllerStorage storage = new VFSKieServerControllerStorage(ioService, fileSystem);

        Path path = Mockito.mock(Path.class);
        when(path.toString()).thenReturn("test-server");

        Path migratedPath = storage.migrate(path);
        assertNotNull(migratedPath);

        verify(ioService, times(1)).delete(any(Path.class));
        verify(ioService, times( 1 )).write(any(Path.class), anyString());

        assertEquals(1, migrated.size());
        assertEquals(migrateServerXml, migrated.get(0));
    }

    @Test
    public void testMigrationServerNameWithSpace() {

        String migrateServerXml = "<org.kie.server.controller.api.model.KieServerInstance>\n" +
                "  <identifier>my-custom-name</identifier>\n" +
                "  <name>my custom name</name>\n" +
                "  <version>6.2.0.Final</version>\n" +
                "  <managedInstances>\n" +
                "    <org.kie.server.controller.api.model.KieServerInstanceInfo>\n" +
                "      <location>http://localhost:8080/kie-server-6.2.0.Final-ee7/services/rest/server</location>\n" +
                "      <status>DOWN</status>\n" +
                "    </org.kie.server.controller.api.model.KieServerInstanceInfo>\n" +
                "  </managedInstances>\n" +
                "  <status>DOWN</status>\n" +
                "  <kieServerSetup>\n" +
                "    <containers>\n" +
                "      <org.kie.server.api.model.KieContainerResource>\n" +
                "        <containerId>mortgages</containerId>\n" +
                "        <releaseId>\n" +
                "          <groupId>mortgages</groupId>\n" +
                "          <artifactId>mortgages</artifactId>\n" +
                "          <version>0.0.1</version>\n" +
                "        </releaseId>\n" +
                "        <status>STOPPED</status>\n" +
                "      </org.kie.server.api.model.KieContainerResource>\n" +
                "    </containers>\n" +
                "  </kieServerSetup>\n" +
                "</org.kie.server.controller.api.model.KieServerInstance>";

        String serverImplXml = "<org.kie.workbench.common.screens.server.management.model.impl.ServerRefImpl>\n" +
                "  <id>http://localhost:8080/kie-server-6.2.0.Final-ee7/services/rest/server</id>\n" +
                "  <name>my custom name</name>\n" +
                "  <username>user</username>\n" +
                "  <password>password</password>\n" +
                "  <status>LOADING</status>\n" +
                "  <connectionType>REMOTE</connectionType>\n" +
                "  <properties>\n" +
                "    <entry>\n" +
                "      <string>version</string>\n" +
                "      <string>6.2.0.Final</string>\n" +
                "    </entry>\n" +
                "  </properties>\n" +
                "  <containersRef>\n" +
                "    <entry>\n" +
                "      <string>mortgages</string>\n" +
                "      <org.kie.workbench.common.screens.server.management.model.impl.ContainerRefImpl>\n" +
                "        <serverId>http://localhost:8080/kie-server-6.2.0.Final-ee7/services/rest/server</serverId>\n" +
                "        <id>mortgages</id>\n" +
                "        <status>STOPPED</status>\n" +
                "        <releaseId>\n" +
                "          <groupId>mortgages</groupId>\n" +
                "          <artifactId>mortgages</artifactId>\n" +
                "          <version>0.0.1</version>\n" +
                "        </releaseId>\n" +
                "      </org.kie.workbench.common.screens.server.management.model.impl.ContainerRefImpl>\n" +
                "    </entry>\n" +
                "  </containersRef>\n" +
                "</org.kie.workbench.common.screens.server.management.model.impl.ServerRefImpl>";

        when(ioService.readAllString(any(Path.class))).thenReturn(serverImplXml);
        when(fileSystem.getPath(anyString(), anyString(), anyString())).thenReturn(Mockito.mock(Path.class));

        final List<String> migrated = new ArrayList<String>();

        when(ioService.write(any(Path.class), anyString())).thenAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                migrated.add((String)args[1]);
                return null;
            }
        });

        VFSKieServerControllerStorage storage = new VFSKieServerControllerStorage(ioService, fileSystem);

        Path path = Mockito.mock(Path.class);
        when(path.toString()).thenReturn("test-server");

        Path migratedPath = storage.migrate(path);
        assertNotNull(migratedPath);

        verify(ioService, times(1)).delete(any(Path.class));
        verify(ioService, times( 1 )).write(any(Path.class), anyString());

        assertEquals(1, migrated.size());
        assertEquals(migrateServerXml, migrated.get(0));
    }
}
