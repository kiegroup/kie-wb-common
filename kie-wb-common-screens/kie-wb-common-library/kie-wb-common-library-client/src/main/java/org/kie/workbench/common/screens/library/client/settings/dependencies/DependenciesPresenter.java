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

import com.google.gwt.core.client.GWT;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.client.settings.SettingsPresenter;
import org.kie.workbench.common.screens.projecteditor.client.forms.dependencies.EnhancedDependenciesManager;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

public class DependenciesPresenter implements SettingsPresenter.Section {

    public interface View extends SettingsPresenter.View.Section<DependenciesPresenter> {

        void addItem(DependenciesItemPresenter.View dependenciesItemView);
    }

    private final View view;
    private final EnhancedDependenciesManager enhancedDependenciesManager;
    private final ManagedInstance<DependenciesItemPresenter> dependenciesItemPresenters;

    private WhiteList whiteList;

    @Inject
    public DependenciesPresenter(final View view,
                                 final EnhancedDependenciesManager enhancedDependenciesManager,
                                 final ManagedInstance<DependenciesItemPresenter> dependenciesItemPresenters) {
        this.view = view;
        this.enhancedDependenciesManager = enhancedDependenciesManager;
        this.dependenciesItemPresenters = dependenciesItemPresenters;
    }

    @Override
    public void setup(final HasBusyIndicator container,
                      final ProjectScreenModel model) {

        view.init(this);
        this.whiteList = null; //FIXME: tiago

        enhancedDependenciesManager.init(model.getPOM(), enhancedDependencies -> {
            container.hideBusyIndicator();
            GWT.log("Dependencies section Setup success");
//
//        for (final EnhancedDependency enhancedDependency : enhancedDependencies) {
//            final DependenciesItemPresenter dependenciesItemPresenter = dependenciesItemPresenters.get();
//            dependenciesItemPresenter.setup(enhancedDependency, whiteList);
//            view.addItem(dependenciesItemPresenter.getView());
//        }
        });
    }

    public void add() {
        //TODO
    }

    public void addFromRepository() {
        //TODO
    }

    @Override
    public SettingsPresenter.View.Section getView() {
        return view;
    }
}
