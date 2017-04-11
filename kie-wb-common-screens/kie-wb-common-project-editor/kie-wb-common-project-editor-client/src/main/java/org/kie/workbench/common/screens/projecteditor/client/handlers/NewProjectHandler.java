/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.projecteditor.client.handlers;

import java.util.Collections;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.library.api.preferences.LibraryPreferences;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.client.wizard.NewProjectWizard;
import org.kie.workbench.common.screens.projecteditor.client.wizard.POMBuilder;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.type.AnyResourceTypeDefinition;
import org.uberfire.workbench.type.ResourceTypeDefinition;

/**
 * Handler for the creation of new Projects
 */
@Dependent
public class NewProjectHandler
        implements org.kie.workbench.common.widgets.client.handlers.NewProjectHandler {

    private Caller<OrganizationalUnitService> ouService;
    private ProjectContext context;
    private Event<ProjectContextChangeEvent> projectContextChangeEvent;
    private LibraryPreferences libraryPreferences;
    private NewProjectWizard wizard;
    private ProjectController projectController;
    //We don't really need this for Packages but it's required by DefaultNewResourceHandler
    private AnyResourceTypeDefinition resourceType;
    private boolean openEditorOnCreation = true;
    private org.uberfire.client.callbacks.Callback<WorkspaceProject> creationSuccessCallback;

    public NewProjectHandler() {
        //Zero argument constructor for CDI proxies
    }

    @Inject
    public NewProjectHandler(final ProjectContext context,
                             final Event<ProjectContextChangeEvent> projectContextChangeEvent,
                             final LibraryPreferences libraryPreferences,
                             final NewProjectWizard wizard,
                             final Caller<OrganizationalUnitService> ouService,
                             final ProjectController projectController,
                             final AnyResourceTypeDefinition resourceType) {
        this.context = context;
        this.projectContextChangeEvent = projectContextChangeEvent;
        this.libraryPreferences = libraryPreferences;
        this.wizard = wizard;
        this.ouService = ouService;
        this.projectController = projectController;
        this.resourceType = resourceType;
    }

    @Override
    public List<Pair<String, ? extends IsWidget>> getExtensions() {
        return Collections.emptyList();
    }

    @Override
    public String getDescription() {
        return ProjectEditorResources.CONSTANTS.newProjectDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image(ProjectEditorResources.INSTANCE.newProjectIcon());
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @Override
    public boolean canCreate() {
        return projectController.canCreateProjects();
    }

    @Override
    public void create(final Package pkg,
                       final String projectName,
                       final NewResourcePresenter presenter) {
        //This is not supported by the NewModuleHandler. It is invoked via NewResourceView that has bypassed for NewModuleHandler
        throw new UnsupportedOperationException();
    }

    @Override
    public void validate(final String projectName,
                         final ValidatorWithReasonCallback callback) {
        //This is not supported by the NewModuleHandler. It is invoked via NewResourceView that has bypassed for NewModuleHandler
        throw new UnsupportedOperationException();
    }

    @Override
    public void acceptContext(final Callback<Boolean, Void> response) {
        response.onSuccess(context.getActiveOrganizationalUnit() != null);
    }

    @Override
    public Command getCommand(final NewResourcePresenter newResourcePresenter) {
        return new Command() {
            @Override
            public void execute() {

                // TODO : Test this
                // TODO: Might not be actually needed.
                if (context.getActiveOrganizationalUnit() == null) {
                    ouService.call(new RemoteCallback<OrganizationalUnit>() {
                        @Override
                        public void callback(OrganizationalUnit organizationalUnit) {

                            projectContextChangeEvent.fire(new ProjectContextChangeEvent(organizationalUnit));

                            init();
                        }
                    }).getOrganizationalUnit(libraryPreferences.getOrganizationalUnitPreferences().getName());
                } else {
                    init();
                }
            }
        };
    }

    private void init() {
        wizard.initialise(new POMBuilder().setModuleName("")
                                  .setGroupId(context.getActiveOrganizationalUnit().getDefaultGroupId()).build());
        wizard.start(creationSuccessCallback,
                     openEditorOnCreation);
    }

    @Override
    public void setCreationSuccessCallback(final org.uberfire.client.callbacks.Callback<Project> creationSuccessCallback) {
        this.creationSuccessCallback = creationSuccessCallback;
    }

    @Override
    public void setOpenEditorOnCreation(final boolean openEditorOnCreation) {
        this.openEditorOnCreation = openEditorOnCreation;
    }
}
