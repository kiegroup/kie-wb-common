/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.docks.navigator;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponents;
import org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreePresenter;
import org.kie.workbench.common.dmn.client.editors.contextmenu.ContextMenu;

import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.hide;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.show;

@Templated
public class DecisionNavigatorView implements DecisionNavigatorPresenter.View {

    @DataField("main-tree")
    private final HTMLDivElement mainTree;

    @DataField("decision-components-container")
    private final HTMLDivElement decisionComponentsContainer;

    @DataField("decision-components")
    private final HTMLDivElement decisionComponents;

    @DataField("trigger-context-menu")
    private final HTMLDivElement triggerContextMenu;

    private DecisionNavigatorPresenter presenter;

    private ContextMenu dRDContextMenu;

    @Inject
    public DecisionNavigatorView(final HTMLDivElement triggerContextMenu,
                                 final ContextMenu dRDContextMenu,
                                 final HTMLDivElement mainTree,
                                 final HTMLDivElement decisionComponentsContainer,
                                 final HTMLDivElement decisionComponents) {
        this.triggerContextMenu = triggerContextMenu;
        this.dRDContextMenu = dRDContextMenu;
        this.mainTree = mainTree;
        this.decisionComponentsContainer = decisionComponentsContainer;
        this.decisionComponents = decisionComponents;
    }

    @EventHandler("trigger-context-menu")
    public void triggerContextMenu(final ClickEvent event) {
        mainTree.appendChild(dRDContextMenu.getElement());
        dRDContextMenu.resetMenuItems();
        dRDContextMenu.setHeaderMenu("DRD ACTIONS", "fa fa-share-alt");
        dRDContextMenu.addTextMenuItem("ITEM NAME", true, () -> DomGlobal.console.log(">>>>> COMMAND: item selected!"));
        dRDContextMenu.show();
    }

    @Override
    public void init(final DecisionNavigatorPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setupMainTree(final DecisionNavigatorTreePresenter.View mainTreeComponent) {
        mainTree.appendChild(mainTreeComponent.getElement());
    }

    @Override
    public void setupDecisionComponents(final DecisionComponents.View decisionComponentsComponent) {
        decisionComponents.appendChild(decisionComponentsComponent.getElement());
    }

    @Override
    public void showDecisionComponentsContainer() {
        show(decisionComponentsContainer);
    }

    @Override
    public void hideDecisionComponentsContainer() {
        hide(decisionComponentsContainer);
    }
}
