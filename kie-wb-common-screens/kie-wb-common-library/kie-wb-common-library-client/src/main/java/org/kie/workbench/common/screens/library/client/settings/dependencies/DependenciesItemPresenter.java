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

import org.guvnor.common.services.project.model.Dependency;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependency;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.uberfire.client.mvp.UberElemental;

public class DependenciesItemPresenter {

    public interface View extends UberElemental<DependenciesItemPresenter> {

        void setGroupId(String groupId);

        void setArtifactId(String artifactId);

        void setVersion(String version);

        void setAllPackagesWhiteListed(final boolean packageWhiteList);
    }

    private final View view;

    private WhiteList whiteList;
    private DependenciesPresenter dependenciesPresenter;
    private EnhancedDependency enhancedDependency;

    @Inject
    public DependenciesItemPresenter(final View view) {
        this.view = view;
    }

    public DependenciesItemPresenter setup(final EnhancedDependency enhancedDependency,
                                           final WhiteList whiteList,
                                           final DependenciesPresenter dependenciesPresenter) {

        this.whiteList = whiteList;
        this.enhancedDependency = enhancedDependency;
        this.dependenciesPresenter = dependenciesPresenter;

        final Dependency dependency = enhancedDependency.getDependency();

        view.init(this);
        view.setGroupId(dependency.getGroupId());
        view.setArtifactId(dependency.getArtifactId());
        view.setVersion(dependency.getVersion());
        view.setAllPackagesWhiteListed(whiteList.isEmpty() || whiteList.containsAll(enhancedDependency.getPackages()));

        return this;
    }

    public void addAllPackagesToWhiteList() {
        whiteList.addAll(enhancedDependency.getPackages());
    }

    public void removeAllPackagesFromWhiteList() {
        whiteList.removeAll(enhancedDependency.getPackages());
    }

    public void remove() {
        dependenciesPresenter.remove(enhancedDependency);
    }

    public View getView() {
        return view;
    }
}
