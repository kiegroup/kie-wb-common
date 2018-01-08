/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.deployments;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.dom.Element;
import elemental2.promise.Promise;
import org.guvnor.common.services.project.context.ProjectContext;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.datamodeller.model.kiedeployment.KieDeploymentDescriptorContent;
import org.kie.workbench.common.screens.datamodeller.model.kiedeployment.KieDeploymentDescriptorContent.BlergsModel;
import org.kie.workbench.common.screens.datamodeller.service.KieDeploymentDescriptorService;
import org.kie.workbench.common.screens.library.client.settings.Promises;
import org.kie.workbench.common.screens.library.client.settings.SettingsPresenter;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.deployments.items.NewTableItemPopupPresenter;
import org.kie.workbench.common.screens.library.client.settings.deployments.items.TableItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.KieSelectElement;
import org.kie.workbench.common.screens.library.client.settings.util.ListPresenter;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.PathFactory;

import static org.kie.workbench.common.screens.library.client.settings.Promises.resolve;

public class DeploymentsPresenter extends SettingsPresenter.Section {

    private final View view;

    private final ProjectContext projectContext;
    private final Caller<KieDeploymentDescriptorService> kieDeploymentDescriptorService;
    private final ManagedInstance<ObservablePath> observablePaths;
    private final MarshallingStrategiesListPresenter marshallingStrategyPresenters;
    private final EventListenersListPresenter eventListenerPresenters;
    private final GlobalsListPresenter globalPresenters;
    private final RequiredRolesListPresenter requiredRolePresenters;
    private final KieSelectElement runtimeStrategiesSelect;
    private final KieSelectElement persistenceModesSelect;
    private final KieSelectElement auditModesSelect;

    private ObservablePath pathToDeploymentsXml;
    private ObservablePath.OnConcurrentUpdateEvent concurrentDeploymentsXmlUpdateInfo;
    private KieDeploymentDescriptorContent model;
    private NewTableItemPopupPresenter newTableItemPopup;

    public interface View extends SettingsPresenter.View.Section<DeploymentsPresenter> {

        Element getMarshallingStrategiesTable();

        Element getEventListenersTable();

        Element getGlobalsTable();

        Element getRequiredRolesTable();

        void setPersistenceUnitName(final String persistenceUnitName);

        void setAuditPersistenceUnitName(final String auditPersistenceUnitName);

        Element getRuntimeStrategiesContainer();

        Element getPersistenceModesContainer();

        Element getAuditModesContainer();
    }

    @Inject
    public DeploymentsPresenter(final View view,
                                final KieSelectElement runtimeStrategiesSelect,
                                final KieSelectElement persistenceModesSelect,
                                final KieSelectElement auditModesSelect,
                                final NewTableItemPopupPresenter newTableItemPopup,
                                final ProjectContext projectContext,
                                final Caller<KieDeploymentDescriptorService> kieDeploymentDescriptorService,
                                final ManagedInstance<ObservablePath> observablePaths,
                                final Event<SettingsSectionChange> settingsSectionChangeEvent,
                                final MarshallingStrategiesListPresenter marshallingStrategyPresenters,
                                final EventListenersListPresenter eventListenerPresenters,
                                final GlobalsListPresenter globalPresenters,
                                final RequiredRolesListPresenter requiredRolePresenters) {

        super(settingsSectionChangeEvent);
        this.view = view;
        this.runtimeStrategiesSelect = runtimeStrategiesSelect;
        this.persistenceModesSelect = persistenceModesSelect;
        this.auditModesSelect = auditModesSelect;
        this.newTableItemPopup = newTableItemPopup;
        this.projectContext = projectContext;
        this.kieDeploymentDescriptorService = kieDeploymentDescriptorService;
        this.observablePaths = observablePaths;
        this.marshallingStrategyPresenters = marshallingStrategyPresenters;
        this.eventListenerPresenters = eventListenerPresenters;
        this.globalPresenters = globalPresenters;
        this.requiredRolePresenters = requiredRolePresenters;
    }

    @Override
    public Promise<Void> setup(final ProjectScreenModel ignore) {

        view.init(this);

        final String deploymentsXmlUri = projectContext.getActiveProject()
                .getRootPath().toURI() + "/src/main/resources/META-INF/kie-deployment-descriptor.xml";

        pathToDeploymentsXml = observablePaths.get().wrap(PathFactory.newPath(
                "kie-deployment-descriptor.xml",
                deploymentsXmlUri));

        pathToDeploymentsXml.onConcurrentUpdate(info -> concurrentDeploymentsXmlUpdateInfo = info);

        return Promises.promisify(kieDeploymentDescriptorService, s -> s.load(pathToDeploymentsXml)).then(model -> {

            this.model = model;

            runtimeStrategiesSelect.setup(view.getRuntimeStrategiesContainer(), getOptions());
            runtimeStrategiesSelect.setValue(model.getRuntimeStrategy());
            runtimeStrategiesSelect.onChange(runtimeStrategy -> {
                model.setRuntimeStrategy(runtimeStrategy);
                fireChangeEvent();
            });

            persistenceModesSelect.setup(view.getPersistenceModesContainer(), getOptions());
            persistenceModesSelect.setValue(model.getPersistenceMode());
            persistenceModesSelect.onChange(persistenceMode -> {
                model.setPersistenceMode(persistenceMode);
                fireChangeEvent();
            });

            auditModesSelect.setup(view.getAuditModesContainer(), getOptions());
            auditModesSelect.setValue(model.getAuditMode());
            auditModesSelect.onChange(auditMode -> {
                model.setAuditMode(auditMode);
                fireChangeEvent();
            });

            view.setPersistenceUnitName(model.getPersistenceUnitName());
            view.setAuditPersistenceUnitName(model.getAuditPersistenceUnitName());

            marshallingStrategyPresenters.setup(
                    view.getMarshallingStrategiesTable(),
                    model.getMarshallingStrategies(),
                    (marshallingStrategy, presenter) -> presenter.setup(marshallingStrategy, this));

            eventListenerPresenters.setup(
                    view.getEventListenersTable(),
                    model.getEventListeners(),
                    (eventListener, presenter) -> presenter.setup(eventListener, this));

            globalPresenters.setup(
                    view.getGlobalsTable(),
                    model.getGlobals(),
                    (global, presenter) -> presenter.setup(global, this));

            requiredRolePresenters.setup(
                    view.getRequiredRolesTable(),
                    model.getRequiredRoles(),
                    (requiredRole, presenter) -> presenter.setup(requiredRole, this));

            return resolve();
        });
    }

    private List<KieSelectElement.Option> getOptions() {
        return Arrays.asList(new KieSelectElement.Option("Test 1", "test1"),
                             new KieSelectElement.Option("Test 2", "test2"));
    }

    public void openNewMarshallingStrategyPopup() {
        newTableItemPopup.show(m -> {
            marshallingStrategyPresenters.add(m);
            fireChangeEvent();
        });
    }

    public void openNewEventListenerPopup() {
        newTableItemPopup.show(m -> {
            eventListenerPresenters.add(m);
            fireChangeEvent();
        });
    }

    public void openNewGlobalPopup() {
        newTableItemPopup.show(m -> {
            globalPresenters.add(m);
            fireChangeEvent();
        });
    }

    public void openNewRequiredRolePopup() {
        newTableItemPopup.show(m -> {
            requiredRolePresenters.add(m);
            fireChangeEvent();
        });
    }

    public void setPersistenceUnitName(final String persistenceUnitName) {
        model.setPersistenceUnitName(persistenceUnitName);
        fireChangeEvent();
    }

    public void setAuditPersistenceUnitName(final String auditPersistenceUnitName) {
        model.setAuditPersistenceUnitName(auditPersistenceUnitName);
        fireChangeEvent();
    }

    @Override
    public int currentHashCode() {
        return model.hashCode();
    }

    @Override
    public SettingsPresenter.View.Section getView() {
        return view;
    }

    @Dependent
    public static class MarshallingStrategiesListPresenter extends ListPresenter<BlergsModel, TableItemPresenter> {

        @Inject
        public MarshallingStrategiesListPresenter(ManagedInstance<TableItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }

    @Dependent
    public static class EventListenersListPresenter extends ListPresenter<BlergsModel, TableItemPresenter> {

        @Inject
        public EventListenersListPresenter(ManagedInstance<TableItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }

    @Dependent
    public static class GlobalsListPresenter extends ListPresenter<BlergsModel, TableItemPresenter> {

        @Inject
        public GlobalsListPresenter(ManagedInstance<TableItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }

    @Dependent
    public static class RequiredRolesListPresenter extends ListPresenter<BlergsModel, TableItemPresenter> {

        @Inject
        public RequiredRolesListPresenter(ManagedInstance<TableItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }
}
