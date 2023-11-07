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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.controller.api.model.events.ServerInstanceDeleted;
import org.kie.server.controller.api.model.events.ServerInstanceDisconnected;
import org.kie.server.controller.api.model.events.ServerInstanceUpdated;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.workbench.common.screens.server.management.client.container.status.card.ContainerCardPresenter;
import org.slf4j.Logger;

import static java.util.Collections.emptyList;

@Dependent
public class ContainerRemoteStatusPresenter {

    public interface View extends IsWidget {

        void addCard( final IsWidget content );

        void removeCard(final IsWidget content);

        void clear();


    }

    private final Logger logger;
    private final View view;
    private final ManagedInstance<ContainerCardPresenter> cardPresenterProvider;

    private final Map<String, ContainerCardPresenter> index = new HashMap<String, ContainerCardPresenter>();

    private ContainerSpecKey containerSpec;

    @Inject
    public ContainerRemoteStatusPresenter( final Logger logger,
                                           final View view,
                                           final ManagedInstance<ContainerCardPresenter> cardPresenterProvider ) {
        this.logger = logger;
        this.view = view;
        this.cardPresenterProvider = cardPresenterProvider;
    }

    @PostConstruct
    public void init() {
        this.view.clear();
    }

    public View getView() {
        return view;
    }

    public void onServerInstanceUpdated( @Observes final ServerInstanceUpdated serverInstanceUpdated ) {
        if ( serverInstanceUpdated != null && serverInstanceUpdated.getServerInstance() != null ) {
            updateView(serverInstanceUpdated.getServerInstance().getContainers());
        } else {
            logger.warn( "Illegal event argument ServerInstanceUpdated {}",  serverInstanceUpdated);
        }
    }

    public void onServerInstanceDisconnect( @Observes final ServerInstanceDisconnected serverInstanceDisconnected ) {
        if ( serverInstanceDisconnected != null &&
                serverInstanceDisconnected.getServerInstanceId() != null ) {
            updateServerInstanceView(serverInstanceDisconnected.getServerInstanceId(), emptyList());
        } else {
            logger.warn( "Illegal event argument ServerInstanceDisconnected {}",  serverInstanceDisconnected);
        }
    }

    public void onServerInstanceDelete( @Observes final ServerInstanceDeleted serverInstanceDeleted ) {
        if ( serverInstanceDeleted != null &&
                serverInstanceDeleted.getServerInstanceId() != null ) {
            updateServerInstanceView(serverInstanceDeleted.getServerInstanceId(), emptyList());
        } else {
            logger.warn( "Illegal event argument ServerInstanceDeleted {}", serverInstanceDeleted);
        }
    }

    public void setup( final ContainerSpecKey containerSpec,
                       final Collection<Container> containers ) {
        // change view we reset the view
        if(containerSpec == null || !containerSpec.equals(this.containerSpec)) {
            logger.debug("Clear ContainerRemoteStatusPresenter {} to {} ", this.containerSpec, containerSpec);
            index.clear();
            view.clear();
        }
        this.containerSpec = containerSpec;

        if(containerSpec == null) {
            return;
        }

        updateView(containers);

    }

    private void updateView(final Collection<Container> containers) {
        Set<String> serverInstanceIds = containers.stream().map(c -> c.getServerInstanceId()).collect(Collectors.toSet());
        for(String serverInstanceId : serverInstanceIds) {
            Collection<Container> containersByServerInstanceId = new ArrayList<>();
            for(Container container : containers) {
                if(container.getServerInstanceId().equals(serverInstanceId)) {
                    containersByServerInstanceId.add(container);
                }
            }
            updateServerInstanceView(serverInstanceId, containersByServerInstanceId);
        }
    }

    
    private boolean isContainerSpecCompliant(ContainerSpecKey containerSpec, Container container) {
        return containerSpec != null && 
                container.getServerTemplateId().equals( containerSpec.getServerTemplateKey().getId() ) &&
                container.getContainerSpecId().equals( containerSpec.getId() );
    }


    private void updateServerInstanceView(String serverInstanceId, Collection<Container> containers) {
        // ensure all compliant containers are shown
        boolean containerFound = false;
        for(Container container : containers) {
            if(isContainerSpecCompliant(this.containerSpec, container) && !KieContainerStatus.STOPPED.equals(container.getStatus()) ) {
                containerFound = true;
                if(hasIndexEntry(container)) {
                    logger.debug("Update entry serverInstanceId {} and container {}", serverInstanceId, container);
                    updateIndexEntry(container);
                } else {
                    logger.debug("Add entry serverInstanceId {} and container {}", serverInstanceId, container);
                    addIndexEntry(container);
                }
            }
        }

        if(!containerFound && index.containsKey( serverInstanceId ))  {
            logger.debug("Remove entry serverInstanceId {}", serverInstanceId);
            view.removeCard(index.remove( serverInstanceId ).getView().asWidget());
        }
    }

    private boolean hasIndexEntry(Container container) {
        return index.containsKey(container.getServerInstanceId());
    }

    private ContainerCardPresenter addIndexEntry(final Container container) {
        ContainerCardPresenter presenter = buildContainer(container);
        view.addCard(presenter.getView().asWidget());
        return index.put( container.getServerInstanceId(), presenter);
    }

    private ContainerCardPresenter updateIndexEntry(final Container container) {
        index.get(container.getServerInstanceId()).updateContent(container.getServerInstanceKey(), container );
        return index.get(container.getServerInstanceId());
    }

    private ContainerCardPresenter buildContainer( final Container container) {
        final ContainerCardPresenter cardPresenter = cardPresenterProvider.get();
        cardPresenter.setup( container.getServerInstanceKey(), container );
        return cardPresenter;
    }

}