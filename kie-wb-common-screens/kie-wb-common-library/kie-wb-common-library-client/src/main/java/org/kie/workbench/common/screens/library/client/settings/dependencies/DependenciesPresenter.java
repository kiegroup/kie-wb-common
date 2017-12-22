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

import java.util.List;
import java.util.stream.StreamSupport;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.guvnor.common.services.project.model.Dependency;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.client.settings.Promises;
import org.kie.workbench.common.screens.library.client.settings.SettingsPresenter;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.projecteditor.client.forms.dependencies.DependencySelectorPopup;
import org.kie.workbench.common.screens.projecteditor.client.forms.dependencies.EnhancedDependenciesManager;
import org.kie.workbench.common.screens.projecteditor.client.forms.dependencies.NewDependencyPopup;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependencies;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependency;

import static java.util.stream.Collectors.toList;

public class DependenciesPresenter implements SettingsPresenter.Section {

    public interface View extends SettingsPresenter.View.Section<DependenciesPresenter> {

        void add(DependenciesItemPresenter.View itemView);

        void setItems(List<DependenciesItemPresenter.View> itemViews);
    }

    private final View view;
    private final DependencySelectorPopup dependencySelectorPopup;
    private final NewDependencyPopup newDependencyPopup;
    private final EnhancedDependenciesManager enhancedDependenciesManager;
    private final ManagedInstance<DependenciesItemPresenter> presenters;

    @Inject
    public DependenciesPresenter(final View view,
                                 final DependencySelectorPopup dependencySelectorPopup,
                                 final NewDependencyPopup newDependencyPopup,
                                 final EnhancedDependenciesManager enhancedDependenciesManager,
                                 final ManagedInstance<DependenciesItemPresenter> presenters) {
        this.view = view;
        this.dependencySelectorPopup = dependencySelectorPopup;
        this.newDependencyPopup = newDependencyPopup;
        this.enhancedDependenciesManager = enhancedDependenciesManager;
        this.presenters = presenters;
    }

    @Override
    public Promise<Void> setup(final ProjectScreenModel model) {

        view.init(this);

        dependencySelectorPopup.addSelectionHandler(gav -> {
            final Dependency dependency = new Dependency(gav);
            dependency.setScope("compile");
            add(dependency);
        });

        return new Promise<>((resolve, reject) -> {
            enhancedDependenciesManager.init(model.getPOM(), dependencies -> {
                view.setItems(buildDependencyViews(model, dependencies));
                resolve.onInvoke(Promises.resolve());
            });

            enhancedDependenciesManager.update();
        });
    }

    private List<DependenciesItemPresenter.View> buildDependencyViews(ProjectScreenModel model, EnhancedDependencies dependencies) {
        return StreamSupport
                .stream(dependencies.spliterator(), false)
                .map(dependency -> presenters.get().setup(dependency, model.getWhiteList(), this).getView())
                .collect(toList());
    }

    private void add(final Dependency dependency) {
        enhancedDependenciesManager.addNew(dependency);
    }

    public void add() {
        newDependencyPopup.show(this::add);
    }

    public void addFromRepository() {
        dependencySelectorPopup.show();
    }

    public void remove(final EnhancedDependency enhancedDependency) {
        enhancedDependenciesManager.delete(enhancedDependency);
    }

    @Override
    public SettingsPresenter.View.Section getView() {
        return view;
    }
}
