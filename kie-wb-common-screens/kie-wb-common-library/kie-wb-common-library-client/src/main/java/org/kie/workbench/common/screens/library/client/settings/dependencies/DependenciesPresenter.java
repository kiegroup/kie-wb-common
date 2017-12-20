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
import elemental2.promise.Promise;
import org.guvnor.common.services.project.context.ProjectContext;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.client.settings.Promises;
import org.kie.workbench.common.screens.library.client.settings.SettingsPresenter;
import org.kie.workbench.common.screens.projecteditor.client.forms.dependencies.EnhancedDependenciesManager;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependencies;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependency;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

public class DependenciesPresenter implements SettingsPresenter.Section {

    public interface View extends SettingsPresenter.View.Section<DependenciesPresenter> {

        void addItem(DependenciesItemPresenter.View dependenciesItemView);
    }

    private final View view;
    private final ProjectContext workbenchContext;
    private final EnhancedDependenciesManager enhancedDependenciesManager;
    private final ManagedInstance<DependenciesItemPresenter> dependenciesItemPresenters;
    private final ManagedInstance<ObservablePath> observablePaths;
    private final Caller<ProjectScreenService> projectScreenService;

    private HasBusyIndicator container;
    private ObservablePath pathToPomXml;
    private WhiteList whiteList;

    @Inject
    public DependenciesPresenter(final View view,
                                 final ProjectContext workbenchContext,
                                 final EnhancedDependenciesManager enhancedDependenciesManager,
                                 final ManagedInstance<DependenciesItemPresenter> dependenciesItemPresenters,
                                 final ManagedInstance<ObservablePath> observablePaths,
                                 final Caller<ProjectScreenService> projectScreenService) {
        this.view = view;
        this.workbenchContext = workbenchContext;
        this.enhancedDependenciesManager = enhancedDependenciesManager;
        this.dependenciesItemPresenters = dependenciesItemPresenters;
        this.observablePaths = observablePaths;
        this.projectScreenService = projectScreenService;
    }

    @Override
    public void setup(final HasBusyIndicator container) {
        view.init(this);
        this.container = container;
        this.whiteList = null; //FIXME: tiago

        if (pathToPomXml != null) {
            pathToPomXml.dispose();
        }

        pathToPomXml = observablePaths.get().wrap(workbenchContext.getActiveProject().getPomXMLPath());

        loadPom().then(this::loadDependencies);
    }

    private Promise<ProjectScreenModel> loadPom() {
        return Promises.promisify(projectScreenService, s -> s.load(pathToPomXml));
    }

    private Promise<Void> loadDependencies(final ProjectScreenModel model) {
        enhancedDependenciesManager.init(model.getPOM(), this::onSetupSuccess);
        return Promises.resolve();
    }

    private void onSetupSuccess(final EnhancedDependencies enhancedDependencies) {
        container.hideBusyIndicator();
        GWT.log("Dependencies section Setup success");
//
//        for (final EnhancedDependency enhancedDependency : enhancedDependencies) {
//            final DependenciesItemPresenter dependenciesItemPresenter = dependenciesItemPresenters.get();
//            dependenciesItemPresenter.setup(enhancedDependency, whiteList);
//            view.addItem(dependenciesItemPresenter.getView());
//        }
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
