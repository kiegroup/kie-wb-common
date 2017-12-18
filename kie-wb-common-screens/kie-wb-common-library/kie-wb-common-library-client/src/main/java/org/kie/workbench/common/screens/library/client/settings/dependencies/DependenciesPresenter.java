/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.dependencies;

import javax.inject.Inject;

import org.guvnor.common.services.project.model.POM;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.client.settings.SettingsBaseSectionView;
import org.kie.workbench.common.screens.projecteditor.client.forms.dependencies.EnhancedDependenciesManager;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependency;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

public class DependenciesPresenter {

    public interface View extends UberElemental<DependenciesPresenter>,
                                  HasBusyIndicator,
                                  SettingsBaseSectionView {

        void showBusyIndicator();

        void addItem(DependenciesItemPresenter.View dependenciesItemView);
    }

    private View view;

    private EnhancedDependenciesManager enhancedDependenciesManager;

    private ManagedInstance<DependenciesItemPresenter> dependenciesItemPresenters;

    private WhiteList whiteList;

    @Inject
    public DependenciesPresenter(final View view,
                                 final EnhancedDependenciesManager enhancedDependenciesManager,
                                 final ManagedInstance<DependenciesItemPresenter> dependenciesItemPresenters) {
        this.view = view;
        this.enhancedDependenciesManager = enhancedDependenciesManager;
        this.dependenciesItemPresenters = dependenciesItemPresenters;
    }

    public void setup(final POM pom,
                      final WhiteList whiteList) {
        view.init(this);
        this.whiteList = whiteList;

        view.showBusyIndicator();
        enhancedDependenciesManager.init(pom,
                                         enhancedDependencies -> {
                                             view.hideBusyIndicator();
                                             for (EnhancedDependency enhancedDependency : enhancedDependencies) {
                                                 final DependenciesItemPresenter dependenciesItemPresenter = dependenciesItemPresenters.get();
                                                 dependenciesItemPresenter.setup(enhancedDependency,
                                                                                 whiteList);
                                                 view.addItem(dependenciesItemPresenter.getView());
                                             }
                                         });
    }

    public void add() {
        //TODO
    }

    public void addFromRepository() {
        //TODO
    }

    public View getView() {
        return view;
    }
}
