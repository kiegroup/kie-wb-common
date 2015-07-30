/*
 * Copyright 2015 JBoss Inc
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guvnor.common.services.project.model.GAV;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieContainerResourceList;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.KieScannerResource;
import org.kie.server.api.model.KieScannerStatus;
import org.kie.server.api.model.KieServerConfig;
import org.kie.server.api.model.KieServerInfo;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesClient;
import org.kie.workbench.common.screens.server.management.model.ConnectionType;
import org.kie.workbench.common.screens.server.management.model.Container;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.ScannerStatus;
import org.kie.workbench.common.screens.server.management.model.Server;
import org.kie.workbench.common.screens.server.management.model.ServerRef;
import org.kie.workbench.common.screens.server.management.model.impl.ScannerOperationResult;
import org.kie.workbench.common.screens.server.management.model.impl.ServerImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ServerRefImpl;
import org.kie.workbench.common.screens.server.management.service.RemoteOperationFailedException;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.commons.data.Pair;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RemoteAccessImplTest {

    private RemoteAccessImpl remoteAccess;
    @Mock
    private KieServicesClient kieServicesClientMock;
    @Mock
    private ServiceResponse<KieServerInfo> serviceResponseMock;
    @Mock
    private ServiceResponse<KieContainerResourceList> containerResourcesResponseMock;
    @Mock
    private ServiceResponse<KieContainerResource> containerResourceServiceResponseMock;
    @Mock
    private ServiceResponse<KieScannerResource> scannerResourceServiceResponseMock;
    @Mock
    private ServiceResponse<Void> serviceResponse;
    @Mock
    private ServiceResponse<ReleaseId> serviceResponseReleaseId;
    private String serverId;
    private String serverUrl;
    private String version;
    private String name;
    private String username;
    private String password;
    private String endpoint;
    private String controllerUrl;
    private ContainerStatus containerStatusStarted;
    private ConnectionType connectionTypeRemote;
    private String endPointCleaned;
    private ArrayList<Container> containersEmpty;
    private Map<String, String> propertiesWithVersion;
    private Map<String, String> propertiesWithNullVersion;
    private String controllerURLEncoded;
    private String endPointWithBaseURI;
    private KieServerInfo serverInfo;
    private Collection<ContainerRef> containerRefsEmpty;
    private KieContainerResourceList containersConfig;
    private GAV gav;
    private String containerId;
    private KieContainerResource firstContainerFromContainers;
    private ReleaseId releaseId;

    @Before
    public void setUp() throws Exception {
        remoteAccess = new RemoteAccessImpl();
        remoteAccess = new RemoteAccessImpl() {
            @Override
            KieServicesClient getKieServicesClient( String username,
                                                    String password,
                                                    String _endpoint ) {

                return kieServicesClientMock;
            }
        };
        when( kieServicesClientMock.register( any( String.class ), any( KieServerConfig.class ) ) ).thenReturn( serviceResponseMock );
        when( kieServicesClientMock.listContainers() ).thenReturn( containerResourcesResponseMock );

        serverId = "serverId";
        serverUrl = "serverUrl";
        version = "version";
        name = "name";
        username = "username";
        password = "password";
        endpoint = "http://uberfire.org/s/rest/";
        controllerUrl = "http://controller.com";
        controllerURLEncoded = remoteAccess.encodeController( controllerUrl );
        containerStatusStarted = ContainerStatus.STARTED;
        connectionTypeRemote = ConnectionType.REMOTE;
        endPointCleaned = remoteAccess.cleanup( endpoint );
        endPointWithBaseURI = remoteAccess.addBaseURIToEndpoint( endpoint );
        containersEmpty = new ArrayList<Container>();
        propertiesWithVersion = new HashMap<String, String>();
        propertiesWithVersion.put( "version", version );
        propertiesWithNullVersion = new HashMap<String, String>();
        propertiesWithNullVersion.put( "version", version );
        serverInfo = new KieServerInfo( serverId, version );
        containerRefsEmpty = new ArrayList<ContainerRef>();
        containersConfig = generateContainers();
        firstContainerFromContainers = generateContainers().getContainers().get( 0 );
        gav = new GAV( "groupId", "artifactId", "version" );
        containerId = "containerId";
        releaseId = new ReleaseId( gav.getGroupId(), gav.getArtifactId(), gav.getVersion() );
    }

    private Server createServer( String id,
                                 String endpoint,
                                 String name,
                                 String username,
                                 String password,
                                 ContainerStatus containerStatus,
                                 ConnectionType connectionType,
                                 Collection<Container> containers,
                                 Map<String, String> properties,
                                 KieContainerResourceList containersConfig ) {
        Collection<ContainerRef> containersList = new ArrayList<ContainerRef>();
        for ( KieContainerResource container : containersConfig.getContainers() ) {
            containersList.add( remoteAccess.toContainer( id, container ) );
        }
        return new ServerImpl( id, endpoint, name, username, password, containerStatus, connectionType, containers, properties, containersList );
    }

    private ServerRefImpl createServerRef( String id,
                                           String endpoint,
                                           String name,
                                           String username,
                                           String password,
                                           ContainerStatus containerStatus,
                                           ConnectionType connectionType,
                                           Collection<ContainerRef> containers,
                                           Map<String, String> properties ) {
        return new ServerRefImpl( id, endpoint, name, username, password, containerStatus, connectionType, properties, containers );
    }

    private Collection<Container> getContainers( String endPointCleaned,
                                                 KieContainerResourceList containersConfig ) {
        Collection<Container> containersList = new ArrayList<Container>();
        for ( KieContainerResource container : containersConfig.getContainers() ) {
            containersList.add( remoteAccess.toContainer( endPointCleaned, container ) );
        }
        return containersList;
    }

    private KieContainerResourceList generateContainers() {
        List<KieContainerResource> containers = new ArrayList<KieContainerResource>();
        containers.add( new KieContainerResource( "1", new ReleaseId( "groupId", "artifact", "version" ), KieContainerStatus.CREATING ) );
        containers.add( new KieContainerResource( "2", new ReleaseId( "groupId", "artifact", "version" ), KieContainerStatus.CREATING ) );

        KieContainerResourceList kieContainerResource = new KieContainerResourceList( containers );
        return kieContainerResource;
    }

    @Test
    public void testRegisterServerWithServiceResponseSuccess() throws Exception {
        when( serviceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.SUCCESS );
        when( serviceResponseMock.getResult() ).thenReturn( new KieServerInfo( serverId, version ) );
        when( containerResourcesResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.SUCCESS );
        when( containerResourcesResponseMock.getResult() ).thenReturn( generateContainers() );

        Server actual = remoteAccess.registerServer( endpoint, name, username, password, connectionTypeRemote, controllerUrl );
        Server expected = createServer( serverId, endPointCleaned, name, username, password, containerStatusStarted, connectionTypeRemote, containersEmpty, propertiesWithVersion, generateContainers() );

        assertEquals( expected, actual );
    }

    @Test
    public void testRegisterServerWithoutServiceResponseSuccess() throws Exception {
        when( serviceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.FAILURE );
        when( containerResourcesResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.FAILURE );

        Server actual = remoteAccess.registerServer( endpoint, name, username, password, connectionTypeRemote, controllerUrl );
        Server expected = createServer( endPointCleaned, endPointCleaned, name, username, password, containerStatusStarted, connectionTypeRemote, containersEmpty, propertiesWithNullVersion, new KieContainerResourceList() );

        assertEquals( expected, actual );
    }

    @Test
    public void testRegisterServerWithServiceResponseException() throws Exception {

        when( serviceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.SUCCESS );
        when( serviceResponseMock.getResult() ).thenReturn( new KieServerInfo( serverId, version ) );
        when( containerResourcesResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.FAILURE );
        when( kieServicesClientMock.register( eq( controllerURLEncoded ), any( KieServerConfig.class ) ) )
                .thenThrow( Exception.class ).thenReturn( serviceResponseMock );

        Server actual = remoteAccess.registerServer( endpoint, name, username, password, connectionTypeRemote, controllerUrl );
        Server expected = createServer( serverId, endPointWithBaseURI, name, username, password, containerStatusStarted, connectionTypeRemote, containersEmpty, propertiesWithVersion, new KieContainerResourceList() );

        assertEquals( expected, actual );
    }

    @Test
    public void testToServerRef() throws Exception {
        when( serviceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.SUCCESS );
        when( serviceResponseMock.getResult() ).thenReturn( serverInfo );
        when( kieServicesClientMock.getServerInfo() ).thenReturn( serviceResponseMock );

        ServerRef actual = remoteAccess.toServerRef( endpoint, name, username, password, connectionTypeRemote, containerRefsEmpty );
        ServerRef expected = createServerRef( serverId, endPointCleaned, name, username, password, ContainerStatus.LOADING, connectionTypeRemote, containerRefsEmpty, propertiesWithVersion );

        assertEquals( expected, actual );
    }

    @Test
    public void testToServerRefWithoutServiceResponseSuccess() throws Exception {
        when( serviceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.FAILURE );
        when( kieServicesClientMock.getServerInfo() ).thenReturn( serviceResponseMock );

        ServerRef actual = remoteAccess.toServerRef( endpoint, name, username, password, connectionTypeRemote, containerRefsEmpty );
        ServerRef expected = createServerRef( endPointCleaned, endPointCleaned, name, username, password, ContainerStatus.LOADING, connectionTypeRemote, containerRefsEmpty, propertiesWithNullVersion );

        assertEquals( expected, actual );
    }

    @Test
    public void testToServerRefWithoutServiceResponseException() throws Exception {
        when( serviceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.SUCCESS );
        when( serviceResponseMock.getResult() ).thenReturn( serverInfo );
        when( kieServicesClientMock.getServerInfo() ).thenThrow( Exception.class ).thenReturn( serviceResponseMock );

        ServerRef actual = remoteAccess.toServerRef( endpoint, name, username, password, connectionTypeRemote, containerRefsEmpty );
        ServerRef expected = createServerRef( serverId, endPointWithBaseURI, name, username, password, ContainerStatus.LOADING, connectionTypeRemote, containerRefsEmpty, propertiesWithVersion );

        assertEquals( expected, actual );
    }

    @Test
    public void testToServer() throws Exception {
        when( serviceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.FAILURE );
        when( kieServicesClientMock.getServerInfo() ).thenReturn( serviceResponseMock );
        when( containerResourcesResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.SUCCESS );
        when( containerResourcesResponseMock.getResult() ).thenReturn( containersConfig );

        ServerRef serverRef = createServerRef( serverId, endPointCleaned, name, username, password, ContainerStatus.LOADING, connectionTypeRemote, containerRefsEmpty, propertiesWithNullVersion );
        Collection<Container> containersList = getContainers( endPointCleaned, containersConfig );

        Server actual = remoteAccess.toServer( serverRef );
        Server expected = createServer( serverId, endPointCleaned, name, username, password, ContainerStatus.STARTED, connectionTypeRemote, containersList, propertiesWithNullVersion, containersConfig );

        assertEquals( expected, actual );
    }

    @Test
    public void testToServerViaServerRef() throws Exception {
        when( serviceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.FAILURE );
        when( kieServicesClientMock.getServerInfo() ).thenReturn( serviceResponseMock );
        when( containerResourcesResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.SUCCESS );
        when( containerResourcesResponseMock.getResult() ).thenReturn( containersConfig );

        Collection<Container> containersList = getContainers( endPointCleaned, containersConfig );

        Server actual = remoteAccess.toServer( endpoint, name, username, password, connectionTypeRemote, containerRefsEmpty );
        Server expected = createServer( endPointCleaned, endPointCleaned, name, username, password, ContainerStatus.STARTED, connectionTypeRemote, containersList, propertiesWithNullVersion, containersConfig );

        assertEquals( expected, actual );

    }

    @Test
    public void testInstallWithSuccess() throws Exception {
        when( kieServicesClientMock.createContainer( containerId,
                                                     new KieContainerResource( new ReleaseId( gav.getGroupId(), gav.getArtifactId(), gav.getVersion() ) ) ) ).
                thenReturn( containerResourceServiceResponseMock );
        when( containerResourceServiceResponseMock.getResult() ).thenReturn( firstContainerFromContainers );
        when( containerResourceServiceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.SUCCESS );

        Container expected = remoteAccess.toContainer( serverId, firstContainerFromContainers );
        Container actual = remoteAccess.install( serverId, serverUrl, containerId, username, password, gav );

        assertEquals( expected, actual );
    }

    @Test(expected = RuntimeException.class)
    public void testInstallAContainerThatAlreadyExists() throws Exception {

        when( kieServicesClientMock
                      .createContainer( containerId,
                                        new KieContainerResource(
                                                new ReleaseId( gav.getGroupId(), gav.getArtifactId(), gav.getVersion() ) ) ) ).
                thenReturn( containerResourceServiceResponseMock );
        when( containerResourceServiceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.SUCCESS );

        remoteAccess.install( serverId, serverUrl, containerId, username, password, gav );
    }

    @Test
    public void testStop() throws Exception {
        when( kieServicesClientMock.updateScanner( containerId, new KieScannerResource( KieScannerStatus.STOPPED ) ) )
                .thenReturn( scannerResourceServiceResponseMock );
        when( scannerResourceServiceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.SUCCESS );

        assertTrue( remoteAccess.stop( "", "", containerId, "", "" ) );
    }

    @Test
    public void testToStatus() throws Exception {
        KieScannerResource kieScannerResource = null;

        assertEquals( ScannerStatus.STOPPED, remoteAccess.toStatus( kieScannerResource ) );
        assertEquals( ScannerStatus.UNKNOWN, remoteAccess.toStatus( new KieScannerResource( KieScannerStatus.UNKNOWN ) ) );
        assertEquals( ScannerStatus.STOPPED, remoteAccess.toStatus( new KieScannerResource( KieScannerStatus.STOPPED ) ) );
        assertEquals( ScannerStatus.CREATED, remoteAccess.toStatus( new KieScannerResource( KieScannerStatus.CREATED ) ) );
        assertEquals( ScannerStatus.STARTED, remoteAccess.toStatus( new KieScannerResource( KieScannerStatus.STARTED ) ) );
        assertEquals( ScannerStatus.SCANNING, remoteAccess.toStatus( new KieScannerResource( KieScannerStatus.SCANNING ) ) );
        assertEquals( ScannerStatus.DISPOSED, remoteAccess.toStatus( new KieScannerResource( KieScannerStatus.DISPOSED ) ) );

        assertEquals( ContainerStatus.LOADING, remoteAccess.toStatus( KieContainerStatus.CREATING ) );
        assertEquals( ContainerStatus.STOPPED, remoteAccess.toStatus( KieContainerStatus.DISPOSING ) );
        assertEquals( ContainerStatus.STARTED, remoteAccess.toStatus( KieContainerStatus.STARTED ) );
        assertEquals( ContainerStatus.ERROR, remoteAccess.toStatus( KieContainerStatus.FAILED ) );
    }

    @Test
    public void testDeleteContainer() throws Exception {
        when( kieServicesClientMock.disposeContainer( containerId ) )
                .thenReturn( serviceResponse );
        when( serviceResponse.getType() ).thenReturn( ServiceResponse.ResponseType.SUCCESS );

        assertTrue( remoteAccess.deleteContainer( "", containerId, "", "" ) );
    }

    @Test
    public void testContainerExists() throws Exception {
        when( kieServicesClientMock.getContainerInfo( containerId ) )
                .thenReturn( containerResourceServiceResponseMock );
        when( containerResourceServiceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.SUCCESS );

        assertTrue( remoteAccess.containerExists( "", containerId, "", "" ) );
    }

    @Test
    public void testToServerWithoutContainerRef() throws Exception {

        when( serviceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.FAILURE );
        when( kieServicesClientMock.getServerInfo() ).thenReturn( serviceResponseMock );

        Server actual = remoteAccess.toServer( endpoint, name, username, password, connectionTypeRemote );
        Server expected = remoteAccess.toServer( endpoint, name, username, password, connectionTypeRemote, null );

        assertEquals( expected, actual );

    }

    @Test
    public void testGetContainerWithSuccess() throws Exception {
        when( kieServicesClientMock.getContainerInfo( containerId ) ).thenReturn( containerResourceServiceResponseMock );
        when( containerResourceServiceResponseMock.getResult() ).thenReturn( firstContainerFromContainers );
        when( containerResourceServiceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.SUCCESS );

        firstContainerFromContainers.setScanner( new KieScannerResource( KieScannerStatus.CREATED, 1l ) );
        Pair<Boolean, Container> actual = remoteAccess.getContainer( serverId, serverUrl, containerId, username, password );
        Container expected = remoteAccess.toContainer( serverId, firstContainerFromContainers );

        assertEquals( true, actual.getK1() );
        assertEquals( expected, actual.getK2() );
    }

    @Test
    public void testGetContainerWithNoSuccess() throws Exception {
        when( kieServicesClientMock.getContainerInfo( containerId ) ).thenReturn( containerResourceServiceResponseMock );
        when( containerResourceServiceResponseMock.getResult() ).thenReturn( firstContainerFromContainers );
        when( containerResourceServiceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.FAILURE );

        Pair<Boolean, Container> actual = remoteAccess.getContainer( serverId, serverUrl, containerId, username, password );

        assertEquals( true, actual.getK1() );
        assertEquals( null, actual.getK2() );
    }

    @Test
    public void testGetContainerWithException() throws Exception {
        when( kieServicesClientMock.getContainerInfo( containerId ) ).thenThrow( Exception.class );

        Pair<Boolean, Container> actual = remoteAccess.getContainer( serverId, serverUrl, containerId, username, password );

        assertEquals( false, actual.getK1() );
        assertEquals( null, actual.getK2() );
    }

    @Test
    public void testScanNowWithError() throws Exception {

        KieScannerResource resource = new KieScannerResource( KieScannerStatus.SCANNING );

        when( kieServicesClientMock.updateScanner( containerId, resource ) ).thenReturn( scannerResourceServiceResponseMock );
        when( scannerResourceServiceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.FAILURE );
        when( scannerResourceServiceResponseMock.getMsg() ).thenReturn( "FAIL" );

        ScannerOperationResult actual = remoteAccess.scanNow( serverId, containerId, username, password );

        assertEquals( ScannerStatus.ERROR, actual.getScannerStatus() );
        assertEquals( "FAIL", actual.getMessage() );
        assertEquals( null, actual.getPollInterval() );

    }

    @Test
    public void testScanNowWithSuccess() throws Exception {

        KieScannerResource resource = new KieScannerResource( KieScannerStatus.SCANNING );

        when( kieServicesClientMock.updateScanner( containerId, resource ) ).thenReturn( scannerResourceServiceResponseMock );
        when( scannerResourceServiceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.SUCCESS );
        when( scannerResourceServiceResponseMock.getMsg() ).thenReturn( "MSG" );
        when( scannerResourceServiceResponseMock.getResult() ).thenReturn( resource );

        ScannerOperationResult actual = remoteAccess.scanNow( serverId, containerId, username, password );

        assertEquals( ScannerStatus.SCANNING, actual.getScannerStatus() );
        assertEquals( "MSG", actual.getMessage() );
        assertEquals( null, actual.getPollInterval() );

    }

    @Test
    public void testStarScanner() throws Exception {

        KieScannerResource resource = new KieScannerResource( KieScannerStatus.STARTED, remoteAccess.toMillis( 10l ) );

        when( kieServicesClientMock.updateScanner( containerId, resource ) ).thenReturn( scannerResourceServiceResponseMock );
        when( scannerResourceServiceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.SUCCESS );
        when( scannerResourceServiceResponseMock.getMsg() ).thenReturn( "MSG" );
        when( scannerResourceServiceResponseMock.getResult() ).thenReturn( resource );

        ScannerOperationResult actual = remoteAccess.startScanner( serverId, containerId, username, password, 10l );

        assertEquals( ScannerStatus.STARTED, actual.getScannerStatus() );
        assertEquals( "MSG", actual.getMessage() );
        assertEquals( remoteAccess.toMillis( 10l ), actual.getPollInterval().longValue() );

    }

    @Test
    public void testStopScanner() throws Exception {

        KieScannerResource resource = new KieScannerResource( KieScannerStatus.STOPPED );

        when( kieServicesClientMock.updateScanner( containerId, resource ) ).thenReturn( scannerResourceServiceResponseMock );
        when( scannerResourceServiceResponseMock.getType() ).thenReturn( ServiceResponse.ResponseType.SUCCESS );
        when( scannerResourceServiceResponseMock.getMsg() ).thenReturn( "MSG" );
        when( scannerResourceServiceResponseMock.getResult() ).thenReturn( resource );

        ScannerOperationResult actual = remoteAccess.stopScanner( serverId, containerId, username, password );

        assertEquals( ScannerStatus.STOPPED, actual.getScannerStatus() );
        assertEquals( "MSG", actual.getMessage() );
        assertEquals( null, actual.getPollInterval() );

    }

    @Test(expected = RemoteOperationFailedException.class)
    public void testUpgradeContainerWithoutSuccess() throws Exception {
        when( kieServicesClientMock.updateReleaseId( containerId, releaseId ) ).thenReturn( serviceResponseReleaseId );
        when( serviceResponseReleaseId.getType() ).thenReturn( ServiceResponse.ResponseType.FAILURE );

        remoteAccess.upgradeContainer( serverId, containerId, username, password, gav );
    }

}