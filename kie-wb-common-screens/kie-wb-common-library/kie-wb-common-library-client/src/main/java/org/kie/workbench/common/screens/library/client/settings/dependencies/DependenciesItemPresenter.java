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

import java.util.Set;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Dependency;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.projecteditor.client.forms.dependencies.EnhancedDependenciesManager;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependency;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.uberfire.client.mvp.UberElemental;

public class DependenciesItemPresenter {

    public interface View extends UberElemental<DependenciesItemPresenter> {

        void setGroupId(String groupId);

        void setArtifactId(String artifactId);

        void setVersion(String version);

        void setPackageWhiteList(String packageWhiteList);
    }

    private View view;

    private EnhancedDependenciesManager enhancedDependenciesManager;

    private EnhancedDependency enhancedDependency;

    private WhiteList whiteList;

    @Inject
    public DependenciesItemPresenter(final View view,
                                     final EnhancedDependenciesManager enhancedDependenciesManager) {
        this.view = view;
        this.enhancedDependenciesManager = enhancedDependenciesManager;
    }

    public void setup(final EnhancedDependency enhancedDependency,
                      final WhiteList whiteList) {
        view.init(this);
        this.enhancedDependency = enhancedDependency;
        this.whiteList = whiteList;

        final Dependency dependency = enhancedDependency.getDependency();

        view.setGroupId(dependency.getGroupId());
        view.setArtifactId(dependency.getArtifactId());
        view.setVersion(dependency.getVersion());
        setupPackageWhiteList(enhancedDependency,
                              whiteList);
    }

    public void whiteListAddAll() {
        whiteList.addAll(enhancedDependency.getPackages());
    }

    public void whiteListAddNone() {
        whiteList.removeAll(enhancedDependency.getPackages());
    }

    public void delete() {
        enhancedDependenciesManager.delete(enhancedDependency);
    }

    private void setupPackageWhiteList(final EnhancedDependency enhancedDependency,
                                       final WhiteList whiteList) {
        if (whiteList.isEmpty()) {
            view.setPackageWhiteList(LibraryConstants.AllPackagesIncluded);
        }

        final Set<String> packages = enhancedDependency.getPackages();
        if (packages.isEmpty()) {
            view.setPackageWhiteList(LibraryConstants.PackagesNotIncluded);
        } else if (whiteList.containsAll(packages)) {
            view.setPackageWhiteList(LibraryConstants.AllPackagesIncluded);
        } else if (whiteList.containsAny(packages)) {
            view.setPackageWhiteList(LibraryConstants.SomePackagesIncluded);
        } else {
            view.setPackageWhiteList(LibraryConstants.PackagesNotIncluded);
        }
    }

    public View getView() {
        return view;
    }
}
