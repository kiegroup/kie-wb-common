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

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.Element;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLSelectElement;
import elemental2.dom.HTMLTableSectionElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class DeploymentsView implements DeploymentsPresenter.View {

    @Inject
    @DataField("runtime-strategy")
    private HTMLSelectElement runtimeStrategy;

    @Inject
    @DataField("persistence-unit-name")
    private HTMLInputElement persistenceUnitName;

    @Inject
    @DataField("persistence-mode")
    private HTMLSelectElement persistenceMode;

    @Inject
    @DataField("audit-persistence-unit-name")
    private HTMLInputElement auditPersistenceUnitName;

    @Inject
    @DataField("audit-mode")
    private HTMLSelectElement auditMode;


    @Inject
    @Named("tbody")
    @DataField("marshalling-strategies")
    private HTMLTableSectionElement marshallingStrategiesTable;

    @Inject
    @DataField("add-marshalling-strategy-button")
    private HTMLButtonElement addMarshallingStrategyButton;


    @Inject
    @Named("tbody")
    @DataField("event-listeners")
    private HTMLTableSectionElement eventListenersTable;

    @Inject
    @DataField("add-event-listener-button")
    private HTMLButtonElement addEventListenerButton;


    @Inject
    @Named("tbody")
    @DataField("globals")
    private HTMLTableSectionElement globalsTable;

    @Inject
    @DataField("add-global-button")
    private HTMLButtonElement addGlobalButton;


    @Inject
    @Named("tbody")
    @DataField("required-roles")
    private HTMLTableSectionElement requiredRolesTable;

    @Inject
    @DataField("add-required-role-button")
    private HTMLButtonElement addRequiredRoleButton;

    private DeploymentsPresenter presenter;

    @Override
    public void init(final DeploymentsPresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("add-marshalling-strategy-button")
    public void onAddMarshallingStrategyButtonClicked(final ClickEvent ignore) {
        presenter.openNewMarshallingStrategyPopup();
    }

    @EventHandler("add-event-listener-button")
    public void onAddEventListenerButtonClicked(final ClickEvent ignore) {
        presenter.openNewEventListenerPopup();
    }

    @EventHandler("add-global-button")
    public void onAddGlobalButtonClicked(final ClickEvent ignore) {
        presenter.openNewGlobalPopup();
    }

    @EventHandler("add-required-role-button")
    public void onAddRequiredRoleButtonClicked(final ClickEvent ignore) {
        presenter.openNewRequiredRolePopup();
    }

    @Override
    public Element getEventListenersTable() {
        return eventListenersTable;
    }

    @Override
    public Element getMarshallingStrategiesTable() {
        return marshallingStrategiesTable;
    }

    @Override
    public Element getGlobalsTable() {
        return globalsTable;
    }

    @Override
    public Element getRequiredRolesTable() {
        return requiredRolesTable;
    }
}
