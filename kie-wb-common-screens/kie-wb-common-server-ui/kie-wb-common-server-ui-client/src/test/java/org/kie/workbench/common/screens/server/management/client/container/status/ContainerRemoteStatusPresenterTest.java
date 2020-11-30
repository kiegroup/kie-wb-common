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

package org.kie.workbench.common.screens.server.management.client.container.status;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.screens.server.management.client.util.Convert.toKey;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.Message;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.model.events.ServerInstanceDeleted;
import org.kie.server.controller.api.model.events.ServerInstanceUpdated;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.ServerInstance;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerConfig;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;
import org.kie.workbench.common.screens.server.management.client.container.status.card.ContainerCardPresenter;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;


@RunWith(MockitoJUnitRunner.Silent.class)
public class ContainerRemoteStatusPresenterTest {

    @Mock
    Logger logger;

    @Mock
    ManagedInstance<ContainerCardPresenter> presenterProvider;

    @Mock
    ContainerRemoteStatusPresenter.View view;


    ContainerRemoteStatusPresenter presenter;

    @Before
    public void init() {
        presenter = spy( new ContainerRemoteStatusPresenter( logger, view, presenterProvider ) );
    }

    @Test
    public void testInit() {
        presenter.init();

        assertEquals( view, presenter.getView() );
    }

    @Test
    public void testOnDelete() {
        final ContainerCardPresenter cardPresenter = mock( ContainerCardPresenter.class );
        when( cardPresenter.getView() ).thenReturn( mock( ContainerCardPresenter.View.class ) );
        when( presenterProvider.get() ).thenReturn( cardPresenter );

        final ServerInstanceKey serverInstanceKey = new ServerInstanceKey( "templateId", "serverName", "serverInstanceId", "url" );
        final Container container = new Container( "containerSpecId", "containerName", serverInstanceKey, Collections.<Message>emptyList(), null, null );
        container.setStatus( KieContainerStatus.STARTED );

        ContainerSpec spec = new ContainerSpec();
        spec.setId("containerSpecId");
        spec.setServerTemplateKey(new ServerTemplateKey("templateId", "aName"));

        presenter.setup( spec, Collections.singletonList( container ) );

        verify( view ).clear();
        verify( cardPresenter ).setup( container.getServerInstanceKey(), container );
        verify( view ).addCard(Mockito.<IsWidget>any() );

        presenter.onServerInstanceDelete(new ServerInstanceDeleted( serverInstanceKey.getServerInstanceId() ) );

        verify( view ).removeCard(anyObject());

        presenter.onServerInstanceDelete( new ServerInstanceDeleted( "randomKey" ) );

        verify( view ).removeCard(anyObject());
    }

    @Test
    public void testOnServerInstanceUpdated() {
        final ContainerCardPresenter cardPresenter = mock( ContainerCardPresenter.class );
        when( cardPresenter.getView() ).thenReturn( mock( ContainerCardPresenter.View.class ) );
        when( presenterProvider.get() ).thenReturn( cardPresenter );

        final ServerInstance serverInstance = new ServerInstance( "templateId", "serverName", "serverInstanceId", "url", "1.0", Collections.<Message>emptyList(), Collections.<Container>emptyList() );
        final Container container = new Container( "containerSpecId", "containerName", serverInstance, Collections.<Message>emptyList(), null, "url" );
        container.setStatus( KieContainerStatus.STARTED );
        final Container containerToBeRemoved = new Container( "containerToBeRemovedSpecId", "containerToBeRemovedName", serverInstance, Collections.<Message>emptyList(), null, "url" );
        containerToBeRemoved.setStatus( KieContainerStatus.STARTED );
        serverInstance.addContainer( container );
        ContainerSpec spec = new ContainerSpec();
        spec.setId("containerSpecId");
        spec.setServerTemplateKey(new ServerTemplateKey("templateId", "aname"));
        presenter.setup(spec , Arrays.asList( container, containerToBeRemoved ) );

        presenter.onServerInstanceUpdated( new ServerInstanceUpdated( serverInstance ) );

        //One container updated,  one removed
        verify( cardPresenter ).updateContent( container.getServerInstanceKey(), container );
        verify( view ).addCard(anyObject());
        final ArgumentCaptor<Container> containerCaptor = ArgumentCaptor.forClass( Container.class );
        verify( cardPresenter, times( 1 ) ).setup( eq( container.getServerInstanceKey() ), containerCaptor.capture() );
        final List<Container> containers = containerCaptor.getAllValues();
        assertEquals( 1, containers.size() );
        assertEquals( container, containers.get( 0 ) );
    }

    @Test
    public void testOnServerInstanceUpdatedNewInstance() {
        presenter = spy( new ContainerRemoteStatusPresenter( logger, view, presenterProvider ) );

        final ContainerCardPresenter cardPresenter = mock( ContainerCardPresenter.class );
        when( cardPresenter.getView() ).thenReturn( mock( ContainerCardPresenter.View.class ) );
        final ContainerCardPresenter cardPresenter2 = mock( ContainerCardPresenter.class );
        when( cardPresenter2.getView() ).thenReturn( mock( ContainerCardPresenter.View.class ) );
        final ContainerCardPresenter cardPresenter3 = mock( ContainerCardPresenter.class );
        when( cardPresenter3.getView() ).thenReturn( mock( ContainerCardPresenter.View.class ) );
        doReturn( cardPresenter )
                .doReturn( cardPresenter2 )
                .doReturn( cardPresenter3 )
                .when( presenterProvider ).get();

        final ServerInstance serverInstance = new ServerInstance( "templateId", "serverInstanceId", "serverInstanceId", "url", "1.0", Collections.<Message>emptyList(), Collections.<Container>emptyList() );

        final Container container = new Container( "containerSpecId", "containerNameX", serverInstance, Collections.<Message>emptyList(), null, "url" );
        container.setStatus( KieContainerStatus.STARTED );
        serverInstance.addContainer( container );

        // To check when ContainerSpec not available
        presenter.onServerInstanceUpdated( new ServerInstanceUpdated( serverInstance ) );
        verify( presenterProvider , times(0)).get();

        
        // check selection
        presenter.setup( new ContainerSpec( "containerSpecId", "containerName", new ServerTemplateKey( "templateId", "templateId" ), new ReleaseId(), KieContainerStatus.STARTED, Collections.<Capability, ContainerConfig>emptyMap() ), Arrays.asList( container ) );
        verify( presenterProvider , times(1)).get();
        verify( cardPresenter, times(1) ).setup( eq(toKey( serverInstance )) , eq(container) );

        // update selection
        presenter.onServerInstanceUpdated( new ServerInstanceUpdated( serverInstance ) );
        verify( cardPresenter ).setup( toKey( serverInstance ), container );

        //One new container added to existing server
        final ServerInstance newServerInstance = new ServerInstance( "templateId", "newserverInstanceId", "newserverInstanceId", "url", "1.0", Collections.<Message>emptyList(), Collections.<Container>emptyList() );
        final Container newContainer = new Container( "containerSpecId", "containerName", newServerInstance, Collections.<Message>emptyList(), null, "url" );
        newContainer.setStatus( KieContainerStatus.STARTED );
        newServerInstance.addContainer( newContainer );

        presenter.onServerInstanceUpdated( new ServerInstanceUpdated( newServerInstance ) );
        verify( presenterProvider , times(2)).get();
        verify( cardPresenter2 ).setup( newContainer.getServerInstanceKey(), newContainer );

        // update container not belonging to current template
        final ServerInstance newServerInstanceNotTemplate = new ServerInstance( "notTemplateId", "newserverInstanceIdNotTemplate", "newserverInstanceIdNotTemplate", "url", "1.0", Collections.<Message>emptyList(), Collections.<Container>emptyList() );
        final Container newContainerNotTeamplate = new Container( "containerSpecId", "containerName", newServerInstance, Collections.<Message>emptyList(), null, "url" );
        newContainerNotTeamplate.setStatus( KieContainerStatus.STARTED );
        newServerInstance.addContainer( newContainerNotTeamplate );

        presenter.onServerInstanceUpdated( new ServerInstanceUpdated( newServerInstanceNotTemplate ) );
        // no new invocations
        verify( presenterProvider , times(2)).get();
    }

    @Test
    public void testOnServerInstanceUpdatedDuplicatedContainerName() {
        final ContainerCardPresenter cardPresenter = mock( ContainerCardPresenter.class );
        when( cardPresenter.getView() ).thenReturn( mock( ContainerCardPresenter.View.class ) );
        when( presenterProvider.get() ).thenReturn( cardPresenter );

        final ServerInstance serverInstance = new ServerInstance( "templateId", "serverName", "serverInstanceId", "url", "1.0", Collections.<Message>emptyList(), Collections.<Container>emptyList() );
        final Container container = new Container( "containerSpecId", "containerName", serverInstance, Collections.<Message>emptyList(), null, "url" );
        container.setStatus( KieContainerStatus.STARTED );
        final Container container2 = new Container( "containerSpecId2", "containerName", serverInstance, Collections.<Message>emptyList(), null, "url" );
        container2.setStatus( KieContainerStatus.STARTED );

        final Container containerToBeRemoved = new Container( "containerToBeRemovedSpecId", "containerToBeRemovedName", serverInstance, Collections.<Message>emptyList(), null, null );
        containerToBeRemoved.setStatus( KieContainerStatus.STARTED );
        serverInstance.addContainer( container );
        serverInstance.addContainer( container2 );

        ContainerSpec spec = new ContainerSpec();
        spec.setId("containerSpecId");
        spec.setServerTemplateKey(new ServerTemplateKey("templateId", "aName"));
        presenter.setup( spec, Arrays.asList( container, container2, containerToBeRemoved ) );

        presenter.onServerInstanceUpdated( new ServerInstanceUpdated( serverInstance ) );

        //One container updated
        verify( cardPresenter, times(1) ).updateContent(Mockito.anyObject(), Mockito.anyObject());
        verify( cardPresenter ).updateContent( container.getServerInstanceKey(), container );
 
        final ArgumentCaptor<Container> containerCaptor = ArgumentCaptor.forClass( Container.class );
        verify( cardPresenter, times( 1 ) ).setup( eq( container.getServerInstanceKey() ), containerCaptor.capture() );
        final List<Container> containers = containerCaptor.getAllValues();
        assertEquals( 1, containers.size() );
        assertEquals( container, containers.get( 0 ) );
    }

}