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

package org.kie.workbench.common.screens.library.client.settings.sections.dependencies;

import java.util.Collections;
import java.util.Set;

import javax.inject.Inject;

import org.guvnor.common.services.project.model.Dependency;
import org.kie.workbench.common.screens.projecteditor.client.forms.dependencies.validation.DependencyValidator;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependency;
import org.kie.workbench.common.services.shared.dependencies.TransitiveEnhancedDependency;
import org.kie.workbench.common.services.shared.allowlist.AllowList;
import org.uberfire.client.mvp.UberElemental;

public class DependenciesItemPresenter {

    public interface View extends UberElemental<DependenciesItemPresenter> {

        void setGroupId(String groupId);

        void setArtifactId(String artifactId);

        void setVersion(String version);

        void setGroupIdHelpBock(final String groupIdHelpBock);

        void setArtifactIdHelpBock(final String artifactIdHelpBock);

        void setVersionHelpBock(final String versionHelpBock);

        void setPackagesAllowListedState(final AllowListedPackagesState state);

        void setTransitiveDependency(final boolean disabled);
    }

    private final View view;

    private DependencyValidator validator;

    DependenciesPresenter parentPresenter;
    EnhancedDependency enhancedDependency;

    @Inject
    public DependenciesItemPresenter(final View view) {
        this.view = view;
    }

    public DependenciesItemPresenter setup(final EnhancedDependency enhancedDependency,
                                           final AllowList allowList,
                                           final DependenciesPresenter dependenciesPresenter) {

        this.enhancedDependency = enhancedDependency;
        this.parentPresenter = dependenciesPresenter;

        final Dependency dependency = enhancedDependency.getDependency();
        validator = new DependencyValidator(dependency);

        view.init(this);
        view.setGroupId(dependency.getGroupId());
        view.setArtifactId(dependency.getArtifactId());
        view.setVersion(dependency.getVersion());
        view.setPackagesAllowListedState(AllowListedPackagesState.from(allowList, enhancedDependency.getPackages()));
        view.setTransitiveDependency(enhancedDependency instanceof TransitiveEnhancedDependency);

        return this;
    }

    public void onGroupIdChange(final String groupId){
        this.enhancedDependency.getDependency().setGroupId(groupId);
        validateGroupId();
        parentPresenter.fireChangeEvent();
    }

    public void onArtifactIdChange(final String artifactId){
        this.enhancedDependency.getDependency().setArtifactId(artifactId);
        validateArtifactId();
        parentPresenter.fireChangeEvent();
    }

    public void onVersionChange(final String version){
        this.enhancedDependency.getDependency().setVersion(version);
        validateVersion();
        parentPresenter.fireChangeEvent();
    }

    public void addAllPackagesToAllowList() {
        parentPresenter.addAllToAllowList(enhancedDependency.getPackages());
    }

    public void removeAllPackagesFromAllowList() {
        parentPresenter.removeAllFromAllowList(enhancedDependency.getPackages());
    }

    public void remove() {
        parentPresenter.remove(enhancedDependency);
    }

    public View getView() {
        return view;
    }

    public enum AllowListedPackagesState {
        ALL,
        SOME,
        NONE;

        public static AllowListedPackagesState from(final Set<String> allowList,
                                                    final Set<String> packages) {

            if (allowList.containsAll(packages)) {
                return ALL;
            }

            if (!Collections.disjoint(allowList, packages)) {
                return SOME;
            }

            return NONE;
        }
    }

    private void validateGroupId() {
        if (validator.validateGroupId()) {
            view.setGroupIdHelpBock("");
        } else {
            view.setGroupIdHelpBock(validator.getMessage());
        }
    }

    private void validateArtifactId() {
        if (validator.validateArtifactId()) {
            view.setArtifactIdHelpBock("");
        } else {
            view.setArtifactIdHelpBock(validator.getMessage());
        }
    }

    private void validateVersion() {
        if (validator.validateVersion()) {
            view.setVersionHelpBock("");
        } else {
            view.setVersionHelpBock(validator.getMessage());
        }
    }
}
