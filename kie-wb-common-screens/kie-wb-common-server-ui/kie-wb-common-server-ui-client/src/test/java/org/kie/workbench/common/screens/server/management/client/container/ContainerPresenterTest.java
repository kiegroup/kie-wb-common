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

package org.kie.workbench.common.screens.server.management.client.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import javax.enterprise.event.Event;

import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.Message;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.model.events.ServerInstanceUpdated;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.ServerInstance;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.*;
import org.kie.workbench.common.screens.server.management.client.container.config.process.ContainerProcessConfigPresenter;
import org.kie.workbench.common.screens.server.management.client.container.config.rules.ContainerRulesConfigPresenter;
import org.kie.workbench.common.screens.server.management.client.container.status.ContainerRemoteStatusPresenter;
import org.kie.workbench.common.screens.server.management.client.container.status.empty.ContainerStatusEmptyPresenter;
import org.kie.workbench.common.screens.server.management.client.events.ContainerSpecSelected;
import org.kie.workbench.common.screens.server.management.client.events.RefreshRemoteServers;
import org.kie.workbench.common.screens.server.management.client.events.ServerTemplateSelected;
import org.kie.workbench.common.screens.server.management.client.util.State;
import org.kie.workbench.common.screens.server.management.model.ContainerRuntimeOperation;
import org.kie.workbench.common.screens.server.management.model.ContainerSpecData;
import org.kie.workbench.common.screens.server.management.model.ContainerUpdateEvent;
import org.kie.workbench.common.screens.server.management.service.RuntimeManagementService;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;


import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ContainerPresenterTest {

    @Mock
    Logger logger;

    @Spy
    Event<ServerTemplateSelected> serverTemplateSelectedEvent = new EventSourceMock<ServerTemplateSelected>();

    @Spy
    Event<NotificationEvent> notification = new EventSourceMock<NotificationEvent>();

    Caller<RuntimeManagementService> runtimeManagementServiceCaller;

    @Mock
    RuntimeManagementService runtimeManagementService;

    Caller<SpecManagementService> specManagementServiceCaller;

    @Mock
    SpecManagementService specManagementService;

    @Mock
    ContainerPresenter.View view;

    @Mock
    ContainerStatusEmptyPresenter containerStatusEmptyPresenter;

    @Mock
    ContainerStatusEmptyPresenter.View containerStatusEmptyPresenterView;

    @Mock
    ContainerRemoteStatusPresenter containerRemoteStatusPresenter;

    @Mock
    ContainerRemoteStatusPresenter.View containerRemoteStatusPresenterView;

    @Mock
    ContainerRulesConfigPresenter containerRulesConfigPresenter;

    @Mock
    ContainerProcessConfigPresenter containerProcessConfigPresenter;

    ContainerPresenter presenter;

    ReleaseId releaseId;

    ServerTemplateKey serverTemplateKey;

    ContainerSpec containerSpec;

    Collection<Container> containers;

    ContainerSpecData containerSpecData;

    @Before
    public void init() {
        runtimeManagementServiceCaller = new CallerMock<RuntimeManagementService>(runtimeManagementService);
        specManagementServiceCaller = new CallerMock<SpecManagementService>(specManagementService);
        doNothing().when(serverTemplateSelectedEvent).fire(any(ServerTemplateSelected.class));
        doNothing().when(notification).fire(any(NotificationEvent.class));
        when(containerStatusEmptyPresenter.getView()).thenReturn(containerStatusEmptyPresenterView);
        when(containerRemoteStatusPresenter.getView()).thenReturn(containerRemoteStatusPresenterView);
        presenter = spy(new ContainerPresenter(
                logger,
                view,
                containerRemoteStatusPresenter,
                containerStatusEmptyPresenter,
                containerProcessConfigPresenter,
                containerRulesConfigPresenter,
                runtimeManagementServiceCaller,
                specManagementServiceCaller,
                serverTemplateSelectedEvent,
                notification));

        releaseId = new ReleaseId("org.kie",
                                  "container",
                                  "1.0.0");
        serverTemplateKey = new ServerTemplateKey("serverTemplateKeyId",
                                                  "serverTemplateKeyName");
        containerSpec = new ContainerSpec("containerId",
                                          "containerName",
                                          serverTemplateKey,
                                          releaseId,
                                          KieContainerStatus.STOPPED,
                                          new HashMap<Capability, ContainerConfig>());
        containerSpec.addConfig(Capability.PROCESS,
                                new ProcessConfig());
        containerSpec.addConfig(Capability.RULE,
                                new RuleConfig());
        containers = new ArrayList<Container>();
        containerSpecData = new ContainerSpecData(containerSpec,
                                                  containers);

        presenter.setContainerSpec(containerSpec);
    }

    @Test
    public void testOnInstanceUpdatedWhenContainerSpecIsNull() {
        ServerInstanceUpdated serverInstanceUpdated = mock(ServerInstanceUpdated.class);
        ContainerSpec containerSpec = null;
        presenter.setContainerSpec(containerSpec);
        presenter.onServerInstanceUpdated(serverInstanceUpdated);

        verify(runtimeManagementService, times(0)).getContainersByContainerSpec(any(), any());
    }

    @Test
    public void testOnInstanceUpdatedWhenContainerSpecServerTemplateNotEqualServerInstanceUpdatedServerTemplate() {
        ServerInstanceUpdated serverInstanceUpdated = mock(ServerInstanceUpdated.class);
        ServerInstance serverInstance = mock(ServerInstance.class);
        when(serverInstanceUpdated.getServerInstance()).thenReturn(serverInstance);
        when(serverInstance.getServerTemplateId()).thenReturn(serverTemplateKey + "1");
        presenter.onServerInstanceUpdated(serverInstanceUpdated);

        verify(runtimeManagementService, times(0)).getContainersByContainerSpec(any(), any());
    }

    @Test
    public void testInit() {
        presenter.init();

        verify(view).init(presenter);
        assertEquals(view,
                     presenter.getView());
        verify(view).setStatus(containerStatusEmptyPresenter.getView());
        verify(view).setRulesConfig(containerRulesConfigPresenter.getView());
        verify(view).setProcessConfig(containerProcessConfigPresenter.getView());
    }

    @Test
    public void testStartContainer() {
        when(runtimeManagementService.getContainersByContainerSpec(
                serverTemplateKey.getId(),
                containerSpec.getId())).thenReturn(containerSpecData);

        presenter.onChangeContainerSpecData(containerSpecData);

        presenter.startContainer();

        verify(view, times(2)).setContainerStartState(State.DISABLED);
        verify(view, times(2)).setContainerStopState(State.ENABLED);

        final String errorMessage = "ERROR";
        when(view.getStartContainerErrorMessage()).thenReturn(errorMessage);
        doThrow(new RuntimeException()).when(specManagementService).startContainer(containerSpecData.getContainerSpec());
        presenter.startContainer();
        verify(notification).fire(new NotificationEvent(errorMessage,
                                                        NotificationEvent.NotificationType.ERROR));

        verify(view,
               times(3)).setContainerStartState(State.DISABLED);
        verify(view,
               times(3)).setContainerStopState(State.ENABLED);
        verify(view,
               times(3)).enableRemoveButton();
    }

    @Test
    public void testStopContainer() {
        when(runtimeManagementService.getContainersByContainerSpec(
                serverTemplateKey.getId(),
                containerSpec.getId())).thenReturn(containerSpecData);

        presenter.onChangeContainerSpecData(containerSpecData);

        presenter.stopContainer();

        verify(view,
               times(2)).setContainerStartState(State.DISABLED);
        verify(view,
               times(2)).setContainerStopState(State.ENABLED);
        verify(view,
               times(2)).enableRemoveButton();

        final String errorMessage = "ERROR";
        when(view.getStopContainerErrorMessage()).thenReturn(errorMessage);
        doThrow(new RuntimeException()).when(specManagementService).stopContainer(containerSpecData.getContainerSpec());
        presenter.stopContainer();
        verify(notification).fire(new NotificationEvent(errorMessage,
                                                        NotificationEvent.NotificationType.ERROR));

        verify(view, times(3)).setContainerStartState(State.DISABLED);
        verify(view, times(3)).setContainerStopState(State.ENABLED);
    }

    @Test
    public void testDeactivateContainerFromStopedState() {
        presenter.onChangeContainerSpecData(containerSpecData);

        presenter.toggleActivationContainer();

        verify(view).setContainerStartState(State.DISABLED);
        verify(view).setContainerStopState(State.ENABLED);
        verify(view).enableRemoveButton();
        verify(view).disableToggleActivationButton();
    }
    
    @Test
    public void testDeactivateContainerFromStartedState() {
        when(runtimeManagementService.getContainersByContainerSpec(
                serverTemplateKey.getId(),
                containerSpec.getId())).thenReturn(containerSpecData);

        presenter.onChangeContainerSpecData(containerSpecData);

        presenter.startContainer();
        containerSpec.setStatus(KieContainerStatus.STARTED);
       
        verify(view, never()).enableToggleActivationButton();

        presenter.toggleActivationContainer();

        verify(view).enableToggleActivationButton();
        verify(view, times(3)).updateToggleActivationButton(eq(false));
    }
    
    @Test
    public void testDeactivateThenActivateContainerFromStartedState() {
        when(runtimeManagementService.getContainersByContainerSpec(
                serverTemplateKey.getId(),
                containerSpec.getId())).thenReturn(containerSpecData);

        presenter.onChangeContainerSpecData(containerSpecData);

        presenter.startContainer();
        containerSpec.setStatus(KieContainerStatus.STARTED);

        verify(view, never()).enableToggleActivationButton();

        presenter.toggleActivationContainer();

        verify(view).enableToggleActivationButton();
        verify(view, times(3)).updateToggleActivationButton(eq(false));
        
        presenter.toggleActivationContainer();

        verify(view,
                times(2)).enableToggleActivationButton();
        verify(view,times(4)).updateToggleActivationButton(eq(false));


    }

    @Test
    public void testLoadContainersEmpty() {
        presenter.onChangeContainerSpecData(containerSpecData);

        verifyLoad(true, 1, false);
    }

    @Test
    public void testLoadContainersOnlyOnSelectedContainerEvent() {

        ContainerSpec containerSpec1 = new ContainerSpec("containerId1",
                                                         "containerName",
                                                         serverTemplateKey,
                                                         releaseId,
                                                         KieContainerStatus.STOPPED,
                                                         new HashMap<Capability, ContainerConfig>());
        presenter.setContainerSpec(containerSpec1);
        presenter.onChangeContainerSpecData(containerSpecData);

        verifyLoad(true, 0, false);

        presenter.setContainerSpec(containerSpec);
        presenter.onChangeContainerSpecData(containerSpecData);

        verifyLoad(true, 1, false);

    }


    @Test
    public void testRefresh() {
        when(runtimeManagementService.getContainersByContainerSpec(
                serverTemplateKey.getId(),
                containerSpec.getId())).thenReturn(containerSpecData);

        presenter.onChangeContainerSpecData(containerSpecData);
        presenter.refresh();


        verify(containerStatusEmptyPresenter, times(2)).setup(containerSpec);
        verify(containerRemoteStatusPresenter, times(2)).setup(containerSpec, containers);
        verify(view, times(1)).setStatus(containerStatusEmptyPresenterView);
        verify(view, never()).setStatus(containerRemoteStatusPresenterView);


    }

    @Test
    public void testLoadContainers() {
        final Container container = new Container("containerSpecId",
                                                  "containerName",
                                                  new ServerInstanceKey(),
                                                  Collections.<Message>emptyList(),
                                                  null,
                                                  null);
        containerSpecData.getContainers().add(container);
        presenter.onChangeContainerSpecData(containerSpecData);

        verifyLoad(true, 1, false);
    }

    @Test
    public void testLoadContainersNonStoped() {
        final Container container = new Container("containerSpecId",
                                                  "containerName",
                                                  new ServerInstanceKey(),
                                                  Collections.<Message>emptyList(),
                                                  null,
                                                  null);
        container.setStatus(KieContainerStatus.STARTED);
        containerSpecData.getContainers().add(container);
        presenter.onChangeContainerSpecData(containerSpecData);

        verifyLoad(false, 1, false);
    }


    @Test
    public void testLoadContainersHasFailed() {
        final Container container = new Container("containerSpecId",
                                                  "containerName",
                                                  new ServerInstanceKey(),
                                                  Collections.<Message>emptyList(),
                                                  null,
                                                  null);
        container.setStatus(KieContainerStatus.FAILED);
        containerSpecData.getContainers().add(container);
        assertNull(container.getResolvedReleasedId());
        presenter.onChangeContainerSpecData(containerSpecData);

        assertEquals(KieContainerStatus.FAILED, containerSpecData.getContainerSpec().getStatus());
        assertNotNull(container.getResolvedReleasedId());

        verifyLoad(false, 1, true);
    }

    private void verifyLoad(boolean empty, int times, boolean hasFailed) {

        verify(containerStatusEmptyPresenter,
               times(times)).setup(containerSpec);
        verify(containerRemoteStatusPresenter,
               times(times)).setup(containerSpec,
                                   containers);

        if (empty) {
            verify(view,
                   times(times)).setStatus(containerStatusEmptyPresenterView);
            verify(view,
                   never()).setStatus(containerRemoteStatusPresenterView);
        } else {
            verify(view,
                   times(times)).setStatus(containerRemoteStatusPresenterView);
            verify(view,
                   never()).setStatus(containerStatusEmptyPresenterView);
        }

        verify(view,
               times(times)).setContainerName(containerSpec.getContainerName());
        verify(view,
               times(times)).setGroupIp(containerSpec.getReleasedId().getGroupId());
        verify(view,
               times(times)).setArtifactId(containerSpec.getReleasedId().getArtifactId());
        verify(containerRulesConfigPresenter,
               times(times)).setVersion(releaseId.getVersion());
        verify(containerProcessConfigPresenter,
               times(times)).disable();

        verify(view,
               times(times)).setContainerStartState(State.DISABLED);
        if (!hasFailed) {
            verify(view, times(times)).setContainerStopState(State.ENABLED);
        } else {
            verify(view, times(times)).setContainerStopState(State.DISABLED);
        }

        verify(containerProcessConfigPresenter,
               times(times)).setup(containerSpec,
                                   (ProcessConfig) containerSpec.getConfigs().get(Capability.PROCESS));
        verify(containerRulesConfigPresenter,
               times(times)).setup(containerSpec,
                                   (RuleConfig) containerSpec.getConfigs().get(Capability.RULE));
    }

    @Test
    public void testLoad() {
        when(runtimeManagementService.getContainersByContainerSpec(
                serverTemplateKey.getId(),
                containerSpec.getId())).thenReturn(containerSpecData);

        presenter.onSelectContainerSpec(new ContainerSpecSelected(containerSpec));


        verify(containerStatusEmptyPresenter, times(1)).setup(containerSpec);
        verify(containerRemoteStatusPresenter, times(1)).setup(containerSpec, containers);

        verify(view, never()).setStatus(containerRemoteStatusPresenterView);
        verify(view, times(1)).setStatus(containerStatusEmptyPresenterView);


        verify(view,times(1)).setContainerName(containerSpec.getContainerName());
        verify(view, times(1)).setGroupIp(containerSpec.getReleasedId().getGroupId());
        verify(view,times(1)).setArtifactId(containerSpec.getReleasedId().getArtifactId());
        verify(containerRulesConfigPresenter, times(1)).setVersion(releaseId.getVersion());
        verify(containerProcessConfigPresenter, times(1)).disable();

        verify(view,times(1)).setContainerStartState(State.DISABLED);
        verify(view, times(1)).setContainerStopState(State.ENABLED);


        verify(containerProcessConfigPresenter,
               times(1)).setup(containerSpec,
                                   (ProcessConfig) containerSpec.getConfigs().get(Capability.PROCESS));
        verify(containerRulesConfigPresenter,
               times(1)).setup(containerSpec,
                                   (RuleConfig) containerSpec.getConfigs().get(Capability.RULE));


    }

    @Test
    public void testRefreshOnContainerUpdateEventWhenRuntimeOperationIsNotStopContainer() {
        final ContainerUpdateEvent updateEvent = new ContainerUpdateEvent(null,
                                                                          containerSpec,
                                                                          null,
                                                                          null,
                                                                          ContainerRuntimeOperation.START_CONTAINER);
        doNothing().when(presenter).refresh();

        presenter.onUpdateContainerSpec(updateEvent);

        verify(presenter).refresh();
    }

    @Test
    public void testRefreshOnContainerUpdateEventWhenRuntimeOperationIsStopContainer() {
        final ContainerUpdateEvent updateEvent = new ContainerUpdateEvent(null,
                                                                          containerSpec,
                                                                          null,
                                                                          null,
                                                                          ContainerRuntimeOperation.STOP_CONTAINER);
        doNothing().when(presenter).refresh();

        presenter.onUpdateContainerSpec(updateEvent);

        verify(presenter,
               never()).refresh();
    }

    @Test
    public void testRefreshOnContainerUpdateEventWithSameContainerSpec() {
        final ContainerUpdateEvent updateEvent = new ContainerUpdateEvent(null,
                                                                          containerSpec,
                                                                          null,
                                                                          null,
                                                                          ContainerRuntimeOperation.START_CONTAINER);

        doNothing().when(presenter).refresh();

        presenter.onUpdateContainerSpec(updateEvent);

        verify(presenter).refresh();
    }

    @Test
    public void testRefreshOnContainerUpdateEventWithDifferentContainerSpec() {
        final ContainerSpec containerSpecEvent = new ContainerSpec();
        containerSpecEvent.setReleasedId(new ReleaseId("org",
                                                       "kie",
                                                       "1.0"));
        final ContainerUpdateEvent updateEvent = new ContainerUpdateEvent(null,
                                                                          containerSpecEvent,
                                                                          null,
                                                                          null,
                                                                          ContainerRuntimeOperation.START_CONTAINER);

        presenter.onUpdateContainerSpec(updateEvent);

        verify(presenter,
               never()).refresh();
    }

    @Test
    public void testOnRefresh() {
        when(runtimeManagementService.getContainersByContainerSpec(
                serverTemplateKey.getId(),
                containerSpec.getId())).thenReturn(containerSpecData);
        presenter.load(containerSpec);
        
        presenter.onRefresh(new RefreshRemoteServers(containerSpec));



        verify(containerStatusEmptyPresenter, times(1)).setup(containerSpec);
        verify(containerRemoteStatusPresenter, times(1)).setup(containerSpec, containers);

        verify(view, never()).setStatus(containerRemoteStatusPresenterView);
        verify(view, times(1)).setStatus(containerStatusEmptyPresenterView);


        verify(view,times(1)).setContainerName(containerSpec.getContainerName());
        verify(view, times(1)).setGroupIp(containerSpec.getReleasedId().getGroupId());
        verify(view,times(1)).setArtifactId(containerSpec.getReleasedId().getArtifactId());
        verify(containerRulesConfigPresenter, times(1)).setVersion(releaseId.getVersion());
        verify(containerProcessConfigPresenter, times(1)).disable();

        verify(view,times(1)).setContainerStartState(State.DISABLED);
        verify(view, times(1)).setContainerStopState(State.ENABLED);


        verify(containerProcessConfigPresenter,
               times(1)).setup(containerSpec,
                                   (ProcessConfig) containerSpec.getConfigs().get(Capability.PROCESS));
        verify(containerRulesConfigPresenter,
               times(1)).setup(containerSpec,
                                   (RuleConfig) containerSpec.getConfigs().get(Capability.RULE));

    }

    @Test
    public void testRemoveContainer() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final Command command = (Command) invocation.getArguments()[0];
                if (command != null) {
                    command.execute();
                }
                return null;
            }
        }).when(view).confirmRemove(any(Command.class));
        final String successMessage = "SUCCESS";
        when(view.getRemoveContainerSuccessMessage()).thenReturn(successMessage);

        presenter.onChangeContainerSpecData(containerSpecData);
        presenter.removeContainer();

        verify(specManagementService).deleteContainerSpec(serverTemplateKey.getId(),
                                                          containerSpec.getId());

        final ArgumentCaptor<NotificationEvent> notificationCaptor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(notification).fire(notificationCaptor.capture());
        final NotificationEvent event = notificationCaptor.getValue();
        assertEquals(NotificationEvent.NotificationType.SUCCESS,
                     event.getType());
        assertEquals(successMessage,
                     event.getNotification());

        final ArgumentCaptor<ServerTemplateSelected> serverTemplateSelectedCaptor = ArgumentCaptor.forClass(ServerTemplateSelected.class);
        verify(serverTemplateSelectedEvent).fire(serverTemplateSelectedCaptor.capture());
        assertEquals(serverTemplateKey.getId(),
                     serverTemplateSelectedCaptor.getValue().getServerTemplateKey().getId());

        final String errorMessage = "ERROR";
        when(view.getRemoveContainerErrorMessage()).thenReturn(errorMessage);
        doThrow(new RuntimeException()).when(specManagementService).deleteContainerSpec(serverTemplateKey.getId(),
                                                                                        containerSpec.getId());
        presenter.removeContainer();
        verify(notification).fire(new NotificationEvent(errorMessage,
                                                        NotificationEvent.NotificationType.ERROR));
        verify(serverTemplateSelectedEvent,
               times(2)).fire(new ServerTemplateSelected(containerSpec.getServerTemplateKey()));
    }

    @Test //Test fix for GUVNOR-3579
    public void testLoadWhenRuntimeManagementServiceReturnsInvalidData() {
        ContainerSpecData badData = new ContainerSpecData(null, null);
        when(runtimeManagementService.getContainersByContainerSpec(anyObject(), anyObject())).thenReturn(badData);

        ContainerSpecKey lookupKey = new ContainerSpecKey("dummyId", "dummyName", new ServerTemplateKey("keyId", "keyName"));

        presenter.load(lookupKey); // Doesn't throw NPE when ContainerSpecData contain nulls

        verify(view, never()).setContainerName(anyString());
    }

    @Test //Test fix for JBPM-8028
    public void testUpdateStatusForStopped() {
        presenter.updateStatus(KieContainerStatus.STOPPED, containers);

        verify(view).enableRemoveButton();
        verify(view).setContainerStartState(State.DISABLED);
        verify(view).setContainerStopState(State.ENABLED);
        verify(view).updateToggleActivationButton(false);
        verify(view).disableToggleActivationButton();
    }

    @Test //Test fix for JBPM-8028
    public void testUpdateStatusForStoppedWithActiveContainer() {
        Container c = new Container();
        c.setStatus(KieContainerStatus.STARTED);
        containers.add(c);
        presenter.updateStatus(KieContainerStatus.STOPPED, containers);

        verify(view).disableRemoveButton();
        verify(view).setContainerStartState(State.DISABLED);
        verify(view).setContainerStopState(State.ENABLED);
        verify(view).updateToggleActivationButton(false);
        verify(view).disableToggleActivationButton();
    }

    @Test //Test fix for JBPM-8028
    public void testUpdateStatusForStarted() {
        presenter.updateStatus(KieContainerStatus.STARTED, containers);

        verify(view).disableRemoveButton();
        verify(view).setContainerStartState(State.ENABLED);
        verify(view).setContainerStopState(State.DISABLED);
        verify(view).updateToggleActivationButton(false);
        verify(view).enableToggleActivationButton();
    }

    @Test
    public void testUpdateStatusForDisposing() {
        presenter.updateStatus(KieContainerStatus.DISPOSING, containers);

        verify(view).disableRemoveButton();
        verify(view).setContainerStartState(State.DISABLED);
        verify(view).setContainerStopState(State.DISABLED);
        verify(view).updateToggleActivationButton(false);
        verify(view).disableToggleActivationButton();
    }

    @Test
    public void testUpdateStatusForCreating() {
        presenter.updateStatus(KieContainerStatus.CREATING, containers);

        verify(view).disableRemoveButton();
        verify(view).setContainerStartState(State.DISABLED);
        verify(view).setContainerStopState(State.DISABLED);
        verify(view).updateToggleActivationButton(false);
        verify(view).disableToggleActivationButton();
    }

    @Test //Test fix for JBPM-8028 DEACTIVATED
    public void testUpdateStatusForFailed() {
        presenter.updateStatus(KieContainerStatus.FAILED, containers);

        verify(view).enableRemoveButton();
        verify(view).setContainerStartState(State.DISABLED);
        verify(view).setContainerStopState(State.DISABLED);
        verify(view).disableToggleActivationButton();
    }

    @Test //Test fix for JBPM-8028 DEACTIVATED
    public void testUpdateStatusForDeactiveated() {
        presenter.updateStatus(KieContainerStatus.DEACTIVATED, containers);

        verify(view).disableRemoveButton();
        verify(view).setContainerStartState(State.ENABLED);
        verify(view).setContainerStopState(State.DISABLED);
        verify(view).updateToggleActivationButton(true);
        verify(view).enableToggleActivationButton();
    }
}
