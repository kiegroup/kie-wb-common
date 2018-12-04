/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens.project.actions;

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.kie.workbench.common.screens.library.client.util.LibraryPermissions;
import org.kie.workbench.common.screens.projecteditor.client.build.BuildExecutor;

@Dependent
public class ProjectMainActions implements ProjectMainActionsView.Presenter,
                                           IsElement {

    private final WorkspaceProjectContext workspaceProjectContext;
    private final BuildExecutor buildExecutor;
    private final LibraryPermissions libraryPermissions;
    private final ProjectMainActionsView view;

    @Inject
    public ProjectMainActions(WorkspaceProjectContext workspaceProjectContext, BuildExecutor buildExecutor, LibraryPermissions libraryPermissions, ProjectMainActionsView view) {
        this.workspaceProjectContext = workspaceProjectContext;
        this.buildExecutor = buildExecutor;
        this.libraryPermissions = libraryPermissions;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.setBuildDropDownEnabled(userCanBuildProject());
        view.setBuildAndDeployDropDownEnabled(userCanDeployProject());
        enableRedeploy(workspaceProjectContext.getActiveWorkspaceProject());
    }

    public void onWorkspaceProjectContextChangeEvent(@Observes WorkspaceProjectContextChangeEvent event) {
        enableRedeploy(Optional.of(event.getWorkspaceProject()));
    }

    private void enableRedeploy(Optional<WorkspaceProject> optional) {
        if(optional.isPresent()) {
            view.setRedeployEnabled(optional.get().getMainModule().getPom().getGav().isSnapshot());
        } else {
            view.setRedeployEnabled(false);
        }
    }

    @Override
    public void triggerBuild() {
        if (this.userCanBuildProject()) {
            this.buildExecutor.triggerBuild();
        }
    }

    @Override
    public void triggerBuildAndInstall() {
        if (this.userCanDeployProject()) {
            this.buildExecutor.triggerBuildAndInstall();
        }
    }

    @Override
    public void triggerBuildAndDeploy() {
        if (this.userCanDeployProject()) {
            this.buildExecutor.triggerBuildAndDeploy();
        }
    }

    @Override
    public void triggerRedeploy() {
        if (this.userCanDeployProject()) {
            if(workspaceProjectContext.getActiveModule().get().getPom().getGav().isSnapshot()) {
                this.buildExecutor.triggerRedeploy();
            }
        }
    }

    private boolean userCanDeployProject() {
        return libraryPermissions.userCanDeployProject(getWorkSpaceProject());
    }

    private boolean userCanBuildProject() {
        return libraryPermissions.userCanBuildProject(getWorkSpaceProject());
    }

    private WorkspaceProject getWorkSpaceProject() {
        return this.workspaceProjectContext.getActiveWorkspaceProject().get();
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }
}
