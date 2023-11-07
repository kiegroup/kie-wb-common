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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.Message;
import org.kie.server.api.model.Severity;
import org.kie.server.controller.api.model.events.ServerInstanceDeleted;
import org.kie.server.controller.api.model.events.ServerInstanceDisconnected;
import org.kie.server.controller.api.model.events.ServerInstanceUpdated;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerConfig;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.ProcessConfig;
import org.kie.server.controller.api.model.spec.RuleConfig;
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
import org.slf4j.Logger;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import com.google.gwt.user.client.ui.IsWidget;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.kie.workbench.common.screens.server.management.model.ContainerRuntimeOperation.STOP_CONTAINER;

@Dependent
public class ContainerPresenter {

    private final Logger logger;
    private final View view;
    private final ContainerRemoteStatusPresenter containerRemoteStatusPresenter;
    private final ContainerStatusEmptyPresenter containerStatusEmptyPresenter;
    private final ContainerProcessConfigPresenter containerProcessConfigPresenter;
    private final ContainerRulesConfigPresenter containerRulesConfigPresenter;
    private final Caller<RuntimeManagementService> runtimeManagementService;
    private final Caller<SpecManagementService> specManagementService;
    private final Event<ServerTemplateSelected> serverTemplateSelectedEvent;
    private final Event<NotificationEvent> notification;
    private ContainerSpec containerSpec;
    private Boolean isEmpty = null;
    private Set<String> serverInstances = new HashSet<>();
    

    @Inject
    public ContainerPresenter(final Logger logger,
                              final View view,
                              final ContainerRemoteStatusPresenter containerRemoteStatusPresenter,
                              final ContainerStatusEmptyPresenter containerStatusEmptyPresenter,
                              final ContainerProcessConfigPresenter containerProcessConfigPresenter,
                              final ContainerRulesConfigPresenter containerRulesConfigPresenter,
                              final Caller<RuntimeManagementService> runtimeManagementService,
                              final Caller<SpecManagementService> specManagementService,
                              final Event<ServerTemplateSelected> serverTemplateSelectedEvent,
                              final Event<NotificationEvent> notification) {
        this.logger = logger;
        this.view = view;
        this.containerRemoteStatusPresenter = containerRemoteStatusPresenter;
        this.containerStatusEmptyPresenter = containerStatusEmptyPresenter;
        this.containerProcessConfigPresenter = containerProcessConfigPresenter;
        this.containerRulesConfigPresenter = containerRulesConfigPresenter;
        this.runtimeManagementService = runtimeManagementService;
        this.specManagementService = specManagementService;
        this.serverTemplateSelectedEvent = serverTemplateSelectedEvent;
        this.notification = notification;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.setStatus(containerStatusEmptyPresenter.getView());
        view.setRulesConfig(containerRulesConfigPresenter.getView());
        view.setProcessConfig(containerProcessConfigPresenter.getView());
    }

    public View getView() {
        return view;
    }

    protected void setContainerSpec(ContainerSpec containerSpec){
        this.containerSpec = containerSpec;
    }

    public void onRefresh(@Observes final RefreshRemoteServers refresh) {
        if (refresh != null && refresh.getContainerSpecKey() != null) {
            runtimeManagementService.call((RemoteCallback<ContainerSpecData>) content -> {
                logger.info("onRefresh {}", content);
                checkNotNull("content", content);
                updateContainerStatusView(content.getContainerSpec(), content.getContainers());
            }).getContainersByContainerSpec(refresh.getContainerSpecKey().getServerTemplateKey().getId(),
                                            refresh.getContainerSpecKey().getId());
        } else {
            logger.warn("Illegal event argument RefreshRemoteServers {}", refresh);
        }
    }

    public void onSelectContainerSpec(@Observes final ContainerSpecSelected containerSpecSelected) {
        if (containerSpecSelected != null &&
                containerSpecSelected.getContainerSpecKey() != null) {
            logger.info("onSelectContainerSpec {}", containerSpecSelected);
            load(containerSpecSelected.getContainerSpecKey());
        } else {
            logger.warn("Illegal event argument ContainerSpecSelected {}", containerSpecSelected);
        }
    }

    public void onChangeContainerSpecData(@Observes final ContainerSpecData content) {
        if (content != null &&
                content.getContainerSpec() != null &&
                content.getContainers() != null &&
                containerSpec != null &&
                containerSpec.getId() != null &&
                containerSpec.getId().equals(content.getContainerSpec().getId())) {

            resetReleaseIdForFailedContainers(content.getContainers(), content.getContainerSpec());
            if (isFailedContainerSpec(content.getContainers(), content.getContainerSpec())) {
                content.getContainerSpec().setStatus(KieContainerStatus.FAILED);
            }

            setup(content.getContainerSpec(),
                  content.getContainers());
        } else {
            logger.warn("Illegal event argument.");
        }
    }


    private void resetReleaseIdForFailedContainers(Collection<Container> containers, ContainerSpec containerSpec) {
        containers.forEach(container -> {
            if (KieContainerStatus.FAILED == container.getStatus() || container.getResolvedReleasedId() == null) {
                container.setResolvedReleasedId(containerSpec.getReleasedId());
                container.addMessage(new Message(Severity.ERROR, Collections.emptyList()));
            }
        });
    }

    private boolean isFailedContainerSpec(Collection<Container> containers, ContainerSpec containerSpec) {
        Optional<Container> allContainersAreFailed = containers.stream().filter(container -> KieContainerStatus.FAILED != container.getStatus()).findFirst();
        if (!allContainersAreFailed.isPresent() && containers.size() > 0) {
            return true;
        }
        return false;
    }

    public void onUpdateContainerSpec(@Observes final ContainerUpdateEvent updateEvent) {

        final ContainerRuntimeOperation runtimeOperation = updateEvent.getContainerRuntimeOperation();

        if (updateEvent.getContainerSpec().equals(containerSpec) && runtimeOperation != STOP_CONTAINER) {
            refresh();
        }
    }

    public void refresh() {
        load(containerSpec);
    }

    public void load(final ContainerSpecKey containerSpecKey) {
        checkNotNull("containerSpecKey", containerSpecKey);
        runtimeManagementService.call((RemoteCallback<ContainerSpecData>) content -> {
            logger.debug("load {}", content);
            checkNotNull("content", content);
            if(content.getContainerSpec() != null && content.getContainers() != null) {
                setup(content.getContainerSpec(), content.getContainers());
            }
        }).getContainersByContainerSpec(containerSpecKey.getServerTemplateKey().getId(),
                                        containerSpecKey.getId());

    }

    private void setup(final ContainerSpec containerSpec,
                       final Collection<Container> containers) {
        this.containerSpec = checkNotNull("containerSpec", containerSpec);
        updateView(containerSpec, containers);
    }

    private void updateView(final ContainerSpec containerSpecKey, final Collection<Container> containers) {
        containerStatusEmptyPresenter.setup(containerSpecKey);
        containerRemoteStatusPresenter.setup(containerSpecKey, containers);

        updateContainerView(containerSpecKey, containers);
        updateContainerStatusView(containerSpecKey, containers);

        updateStatus(containerSpec.getStatus() != null ? containerSpec.getStatus() : KieContainerStatus.STOPPED, containers);
    }

    private void updateContainerView(ContainerSpec updatedContainerSpec, Collection<Container> containers) {
        view.setContainerName(updatedContainerSpec.getContainerName());
        view.setGroupIp(updatedContainerSpec.getReleasedId().getGroupId());
        view.setArtifactId(updatedContainerSpec.getReleasedId().getArtifactId());
        containerRulesConfigPresenter.setVersion(updatedContainerSpec.getReleasedId().getVersion());
        containerProcessConfigPresenter.disable();

        for (Map.Entry<Capability, ContainerConfig> entry : updatedContainerSpec.getConfigs().entrySet()) {
            switch (entry.getKey()) {
                case RULE:
                    setupRuleConfig((RuleConfig) entry.getValue());
                    break;
                case PROCESS:
                    setupProcessConfig((ProcessConfig) entry.getValue());
                    break;
                case PLANNING:
                    // do nothing
                    break;
            }
        }
    }

    private boolean isEmpty(final Collection<Container> containers) {
        for (final Container container : containers) {
            if (!container.getStatus().equals(KieContainerStatus.STOPPED)) {
                return false;
            }
        }
        return true;
    }

    public void onServerInstanceUpdated( @Observes final ServerInstanceUpdated serverInstanceUpdated ) {
        if(this.containerSpec == null) {
            return;
        }
        if ( serverInstanceUpdated != null && serverInstanceUpdated.getServerInstance() != null ) {
            serverInstanceUpdated.getServerInstance().getContainers().stream()
                                                                     .filter(c -> !KieContainerStatus.STOPPED.equals(c.getStatus()))
                                                                     .filter(c -> this.containerSpec.getId().equals(c.getContainerSpecId()))
                                                                     .filter(c -> this.containerSpec.getServerTemplateKey().getId().equals(c.getServerTemplateId()))
                                                                     .forEach(c -> serverInstances.add(c.getServerInstanceId()));
            updateContainerStatusView();
        } else {
            logger.warn( "Illegal event argument ServerInstanceUpdated {}",  serverInstanceUpdated);
        }
    }

    public void onServerInstanceDisconnect( @Observes final ServerInstanceDisconnected serverInstanceDisconnected ) {
        if ( serverInstanceDisconnected != null &&
                serverInstanceDisconnected.getServerInstanceId() != null ) {
            if(serverInstances.remove(serverInstanceDisconnected.getServerInstanceId())) {
                updateContainerStatusView();
            }
        } else {
            logger.warn( "Illegal event argument ServerInstanceDisconnected {}",  serverInstanceDisconnected);
        }
    }

    public void onServerInstanceDelete( @Observes final ServerInstanceDeleted serverInstanceDeleted ) {
        if ( serverInstanceDeleted != null &&
                serverInstanceDeleted.getServerInstanceId() != null ) {

            if(serverInstances.remove(serverInstanceDeleted.getServerInstanceId())) {
                updateContainerStatusView();
            }
        } else {
            logger.warn( "Illegal event argument ServerInstanceDeleted {}", serverInstanceDeleted);
        }
    }

    private void updateContainerStatusView(ContainerSpecKey containerSpecUpdated, Collection<Container> containers) {
        logger.debug( "updateContainerStatusView {} with number of containers {}", containerSpecUpdated, containers.size());
        serverInstances.clear();
        containers.stream().filter(c -> !KieContainerStatus.STOPPED.equals(c.getStatus())).forEach(c -> serverInstances.add(c.getServerInstanceId()));
        updateContainerStatusView();
    }
    
    private void updateContainerStatusView() {
        logger.debug( "updateContainerStatusView with number of containers {}", serverInstances);
        // switch from empty -> not empty. Avoid the blink
        if ((isEmpty== null && serverInstances.isEmpty()) || (serverInstances.isEmpty() && !isEmpty)) {
            view.setStatus(containerStatusEmptyPresenter.getView());
            isEmpty = true;
        } else if ((isEmpty == null && !serverInstances.isEmpty()) || (!serverInstances.isEmpty() && isEmpty)) {
            view.setStatus(containerRemoteStatusPresenter.getView());
            isEmpty = false;
        }
    }

    protected void updateStatus(final KieContainerStatus status, final Collection<Container> containers) {
        switch (status) {
            case CREATING:
            case DISPOSING:
                view.disableRemoveButton();
                view.setContainerStartState(State.DISABLED);
                view.setContainerStopState(State.DISABLED);
                view.updateToggleActivationButton(false);
                view.disableToggleActivationButton();
                break;
            case STARTED:
                view.disableRemoveButton();
                view.setContainerStartState(State.ENABLED);
                view.setContainerStopState(State.DISABLED);
                view.updateToggleActivationButton(false);
                view.enableToggleActivationButton();
                break;
            case DEACTIVATED:
                view.disableRemoveButton();
                view.setContainerStartState(State.ENABLED);
                view.setContainerStopState(State.DISABLED);
                view.updateToggleActivationButton(true);
                view.enableToggleActivationButton();
                break;                
            case STOPPED:
                view.updateToggleActivationButton(false);
                view.setContainerStartState(State.DISABLED);
                view.setContainerStopState(State.ENABLED);
                if (isEmpty(containers)) {
                    view.enableRemoveButton();
                } else {
                    view.disableRemoveButton();
                }
                view.disableToggleActivationButton();
                break;
            case FAILED:
                view.enableRemoveButton();
                view.updateToggleActivationButton(false);
                view.setContainerStartState(State.DISABLED);
                view.setContainerStopState(State.DISABLED);
                view.disableToggleActivationButton();
                break;
        }
    }

    private void setupProcessConfig(final ProcessConfig value) {
        containerProcessConfigPresenter.setup(containerSpec,
                                              value);
    }

    private void setupRuleConfig(final RuleConfig value) {
        containerRulesConfigPresenter.setup(containerSpec,
                                            value);
    }

    public void removeContainer() {
        view.confirmRemove(new Command() {
            @Override
            public void execute() {
                specManagementService.call(new RemoteCallback<Void>() {
                                               @Override
                                               public void callback(final Void response) {
                                                   notification.fire(new NotificationEvent(view.getRemoveContainerSuccessMessage(),
                                                                                           NotificationEvent.NotificationType.SUCCESS));
                                                   serverTemplateSelectedEvent.fire(new ServerTemplateSelected(containerSpec.getServerTemplateKey()));
                                               }
                                           },
                                           new ErrorCallback<Object>() {
                                               @Override
                                               public boolean error(final Object o,
                                                                    final Throwable throwable) {
                                                   notification.fire(new NotificationEvent(view.getRemoveContainerErrorMessage(),
                                                                                           NotificationEvent.NotificationType.ERROR));
                                                   serverTemplateSelectedEvent.fire(new ServerTemplateSelected(containerSpec.getServerTemplateKey()));
                                                   return false;
                                               }
                                           }).deleteContainerSpec(containerSpec.getServerTemplateKey().getId(),
                                                                  containerSpec.getId());
            }
        });
    }

    public void stopContainer() {
        specManagementService.call(new RemoteCallback<Void>() {
                                       @Override
                                       public void callback(final Void response) {
                                           refresh();
                                       }
                                   },
                                   new ErrorCallback<Object>() {
                                       @Override
                                       public boolean error(final Object o,
                                                            final Throwable throwable) {
                                           notification.fire(new NotificationEvent(view.getStopContainerErrorMessage(),
                                                                                   NotificationEvent.NotificationType.ERROR));
                                           refresh();
                                           return false;
                                       }
                                   }).stopContainer(containerSpec);
    }

    public void startContainer() {
        specManagementService.call(new RemoteCallback<Void>() {
                                       @Override
                                       public void callback(final Void response) {
                                           refresh();
                                       }
                                   },
                                   new ErrorCallback<Object>() {
                                       @Override
                                       public boolean error(final Object o,
                                                            final Throwable throwable) {
                                           notification.fire(new NotificationEvent(view.getStartContainerErrorMessage(),
                                                                                   NotificationEvent.NotificationType.ERROR));
                                           refresh();
                                           return false;
                                       }
                                   }).startContainer(containerSpec);
    }
    
    public void toggleActivationContainer() {
        if (containerSpec.getStatus().equals(KieContainerStatus.DEACTIVATED)) {
            specManagementService.call(new RemoteCallback<Void>() {
                                           @Override
                                           public void callback(final Void response) {
                                               refresh();
                                           }
                                       },
                                       new ErrorCallback<Object>() {
                                           @Override
                                           public boolean error(final Object o,
                                                                final Throwable throwable) {
                                               notification.fire(new NotificationEvent(view.getStartContainerErrorMessage(),
                                                                                       NotificationEvent.NotificationType.ERROR));
                                               refresh();
                                               return false;
                                           }
                                       }).activateContainer(containerSpec);
        } else if (containerSpec.getStatus().equals(KieContainerStatus.STARTED)) {
         
            specManagementService.call(new RemoteCallback<Void>() {
                                           @Override
                                           public void callback(final Void response) {
                                               refresh();
                                           }
                                       },
                                       new ErrorCallback<Object>() {
                                           @Override
                                           public boolean error(final Object o,
                                                                final Throwable throwable) {
                                               notification.fire(new NotificationEvent(view.getStartContainerErrorMessage(),
                                                                                       NotificationEvent.NotificationType.ERROR));
                                               refresh();
                                               return false;
                                           }
                                       }).deactivateContainer(containerSpec);
        }
    }

    public interface View extends UberView<ContainerPresenter> {

        void clear();

        void disableRemoveButton();

        void enableRemoveButton();

        void updateToggleActivationButton(boolean activate);
        
        void disableToggleActivationButton();
        
        void enableToggleActivationButton();
        
        void setContainerName(final String containerName);

        void setGroupIp(final String groupIp);

        void setArtifactId(final String artifactId);

        void setStatus(final IsWidget view);

        void setProcessConfig(final ContainerProcessConfigPresenter.View view);

        void setRulesConfig(final ContainerRulesConfigPresenter.View view);

        void setContainerStopState(final State state);

        void setContainerStartState(final State state);

        void confirmRemove(final Command command);

        String getRemoveContainerSuccessMessage();

        String getRemoveContainerErrorMessage();

        String getStopContainerErrorMessage();

        String getStartContainerErrorMessage();

    }
}
