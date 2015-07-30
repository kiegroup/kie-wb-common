package org.kie.workbench.common.screens.server.management.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.guvnor.common.services.project.model.GAV;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieContainerResourceList;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.KieServerInfo;
import org.kie.server.api.model.ReleaseId;
import org.kie.workbench.common.screens.server.management.events.ServerConnected;
import org.kie.workbench.common.screens.server.management.events.ServerOnError;
import org.kie.workbench.common.screens.server.management.model.ConnectionType;
import org.kie.workbench.common.screens.server.management.model.Container;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.impl.ServerRefImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class ServerControllerRestImplTest {

    @Mock
    private EventSourceMock<ServerConnected> serverConnectedEvent;

    @Mock
    private EventSourceMock<ServerOnError> serverOnErrorEvent;

    @Mock
    private ServerReferenceStorageImpl storage;

    @Mock
    private HttpHeaders httpHeaders;

    @Mock
    private MultivaluedMap requestHeaders;

    private ServerControllerRestImpl serverControllerRest;

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
    private Collection<ContainerRef> containerRefs;
    private KieContainerResourceList containersConfig;
    private GAV gav;
    private String containerId;
    private KieContainerResource firstContainerFromContainers;
    private ReleaseId releaseId;

    @Before
    public void setUp() throws Exception {
        serverControllerRest = new ServerControllerRestImpl(  );
        serverControllerRest = new ServerControllerRestImpl( storage, serverConnectedEvent, serverOnErrorEvent );
        when( httpHeaders.getRequestHeaders() ).thenReturn( requestHeaders );
        when( requestHeaders.get( "Accept" ) ).thenReturn( null );

        serverId = "serverId";
        serverUrl = "serverUrl";
        version = "version";
        name = "name";
        username = "username";
        password = "password";
        endpoint = "http://uberfire.org/s/rest/";
        controllerUrl = "http://controller.com";
        containerStatusStarted = ContainerStatus.STARTED;
        connectionTypeRemote = ConnectionType.REMOTE;
        containersEmpty = new ArrayList<Container>();
        propertiesWithVersion = new HashMap<String, String>();
        propertiesWithVersion.put( "version", version );
        propertiesWithNullVersion = new HashMap<String, String>();
        propertiesWithNullVersion.put( "version", version );
        serverInfo = new KieServerInfo( serverId, version );
        containerRefs = new ArrayList<ContainerRef>();
        containersConfig = generateContainers();
        firstContainerFromContainers = generateContainers().getContainers().get( 0 );
        gav = new GAV( "groupId", "artifactId", "version" );
        containerId = "containerId";
        releaseId = new ReleaseId( gav.getGroupId(), gav.getArtifactId(), gav.getVersion() );
    }

    private KieContainerResourceList generateContainers() {
        List<KieContainerResource> containers = new ArrayList<KieContainerResource>();
        containers.add( new KieContainerResource( "1", new ReleaseId( "groupId", "artifact", "version" ), KieContainerStatus.CREATING ) );
        containers.add( new KieContainerResource( "2", new ReleaseId( "groupId", "artifact", "version" ), KieContainerStatus.CREATING ) );

        KieContainerResourceList kieContainerResource = new KieContainerResourceList( containers );
        return kieContainerResource;
    }



    private ServerRefImpl createServerRef( String id,
                                           String endpoint,
                                           String name,
                                           String username,
                                           String password,
                                           ContainerStatus containerStatus,
                                           ConnectionType connectionType,
                                           Map<String, String> properties ) {
        Collection<ContainerRef> containersList = new ArrayList<ContainerRef>();
        RemoteAccessImpl remoteAccess = new RemoteAccessImpl();
        for ( KieContainerResource container : containersConfig.getContainers() ) {
            containersList.add( remoteAccess.toContainer( id, container ) );
        }

        return new ServerRefImpl( id, endpoint, name, username, password, containerStatus, connectionType, properties, containersList );
    }

    @Test
    public void testSyncOnConnectWithServerNotFound() throws Exception {
        when( storage.loadServerRef( serverId ) ).thenReturn( null );
        Response response = serverControllerRest.syncOnConnect( httpHeaders, serverId );

        assertEquals( Response.Status.NOT_FOUND.getStatusCode(), response.getStatus() );
    }

    @Test
    public void testSyncOnConnect() throws Exception {

        ServerRefImpl serverRef = createServerRef( serverId, endPointCleaned, name, username, password, ContainerStatus.LOADING, connectionTypeRemote, propertiesWithNullVersion );

        when( storage.loadServerRef( serverId ) ).thenReturn( serverRef );

        Response response = serverControllerRest.syncOnConnect( httpHeaders, serverId );

        final ArgumentCaptor<ServerConnected> serverConnectedCaptor = ArgumentCaptor.forClass( ServerConnected.class );
        verify( serverConnectedEvent, times( 1 ) ).fire( serverConnectedCaptor.capture() );
        assertEquals( serverControllerRest.getServer(serverRef ), serverConnectedCaptor.getValue().getServer() );

        assertEquals( Response.Status.OK.getStatusCode(), response.getStatus() );
    }

    @Test
    public void testSyncOnDisconnectWithServerNotFound() throws Exception {
        when( storage.loadServerRef( serverId ) ).thenReturn( null );
        Response response = serverControllerRest.disconnect( httpHeaders, serverId );

        assertEquals( Response.Status.NOT_FOUND.getStatusCode(), response.getStatus() );
    }

    @Test
    public void testSyncOnDisconnect() throws Exception {

        ServerRefImpl serverRef = createServerRef( serverId, endPointCleaned, name, username, password, ContainerStatus.LOADING, connectionTypeRemote, propertiesWithNullVersion );

        when( storage.loadServerRef( serverId ) ).thenReturn( serverRef );

        Response response = serverControllerRest.disconnect( httpHeaders, serverId );

        final ArgumentCaptor<ServerOnError> serverConnectedCaptor = ArgumentCaptor.forClass( ServerOnError.class );
        verify( serverOnErrorEvent, times( 1 ) ).fire( serverConnectedCaptor.capture() );
        assertEquals( serverControllerRest.getServer(serverRef ), serverConnectedCaptor.getValue().getServer() );

        assertEquals( null, response );
    }


}