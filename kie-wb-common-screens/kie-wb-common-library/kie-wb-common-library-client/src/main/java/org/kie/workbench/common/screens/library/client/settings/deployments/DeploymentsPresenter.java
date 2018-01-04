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

import java.util.ArrayList;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.dom.Element;
import elemental2.promise.Promise;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.client.settings.SettingsPresenter;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.deployments.eventlisteners.EventListenerItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.deployments.global.GlobalItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.deployments.marshallingstrategies.MarshallingStrategyItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.deployments.requiredroles.RequiredRolesItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.ListPresenter;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;

import static org.kie.workbench.common.screens.library.client.settings.Promises.resolve;

public class DeploymentsPresenter extends SettingsPresenter.Section {

    private final View view;

    private final MarshallingStrategiesListPresenter marshallingStrategyPresenters;
    private final EventListenersListPresenter eventListenerPresenters;
    private final GlobalsListPresenter globalPresenters;
    private final RequiredRolesListPresenter requiredRolePresenters;

    public interface View extends SettingsPresenter.View.Section<DeploymentsPresenter> {

        Element getMarshallingStrategiesTable();

        Element getEventListenersTable();

        Element getGlobalsTable();

        Element getRequiredRolesTable();
    }

    @Inject
    public DeploymentsPresenter(final View view,
                                final Event<SettingsSectionChange> settingsSectionChangeEvent,
                                final MarshallingStrategiesListPresenter marshallingStrategyPresenters,
                                final EventListenersListPresenter eventListenerPresenters,
                                final GlobalsListPresenter globalPresenters,
                                final RequiredRolesListPresenter requiredRolePresenters) {

        super(settingsSectionChangeEvent);
        this.view = view;
        this.marshallingStrategyPresenters = marshallingStrategyPresenters;
        this.eventListenerPresenters = eventListenerPresenters;
        this.globalPresenters = globalPresenters;
        this.requiredRolePresenters = requiredRolePresenters;
    }

    @Override
    public Promise<Void> setup(final ProjectScreenModel model) {
        this.marshallingStrategyPresenters.setup(
                view.getMarshallingStrategiesTable(),
                new ArrayList<>(),
                (marshallingStrategy, presenter) -> presenter.setup(marshallingStrategy, this));

        this.eventListenerPresenters.setup(
                view.getEventListenersTable(),
                new ArrayList<>(),
                (eventListener, presenter) -> presenter.setup(eventListener, this));

        this.globalPresenters.setup(
                view.getGlobalsTable(),
                new ArrayList<>(),
                (global, presenter) -> presenter.setup(global, this));

        this.requiredRolePresenters.setup(
                view.getRequiredRolesTable(),
                new ArrayList<>(),
                (requiredRole, presenter) -> presenter.setup(requiredRole, this));

        return resolve();
    }

    void addMarshallingStrategy(final Object marshallingStrategy) {
        marshallingStrategyPresenters.add(marshallingStrategy);
        fireChangeEvent();
    }

    void addEventStrategy(final Object eventStrategy) {
        eventListenerPresenters.add(eventStrategy);
        fireChangeEvent();
    }

    void addGlobal(final Object global) {
        globalPresenters.add(global);
        fireChangeEvent();
    }

    void addRequiredRole(final Object requiredRole) {
        requiredRolePresenters.add(requiredRole);
        fireChangeEvent();
    }

    public void openNewMarshallingStrategyPopup() {
    }

    public void openNewEventListenerPopup() {
    }

    public void openNewGlobalPopup() {
    }

    public void openNewRequiredRolePopup() {
    }

    @Override
    public int currentHashCode() {
        return 0;
    }

    @Override
    public SettingsPresenter.View.Section getView() {
        return view;
    }

    @Dependent
    public static class MarshallingStrategiesListPresenter extends ListPresenter<Object, MarshallingStrategyItemPresenter> {

        @Inject
        public MarshallingStrategiesListPresenter(ManagedInstance<MarshallingStrategyItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }

    @Dependent
    public static class EventListenersListPresenter extends ListPresenter<Object, EventListenerItemPresenter> {

        @Inject
        public EventListenersListPresenter(ManagedInstance<EventListenerItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }

    @Dependent
    public static class GlobalsListPresenter extends ListPresenter<Object, GlobalItemPresenter> {

        @Inject
        public GlobalsListPresenter(ManagedInstance<GlobalItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }

    @Dependent
    public static class RequiredRolesListPresenter extends ListPresenter<Object, RequiredRolesItemPresenter> {

        @Inject
        public RequiredRolesListPresenter(ManagedInstance<RequiredRolesItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }
}
