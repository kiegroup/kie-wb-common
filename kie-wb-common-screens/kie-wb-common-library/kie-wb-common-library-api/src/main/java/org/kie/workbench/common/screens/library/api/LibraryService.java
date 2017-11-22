/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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

package org.kie.workbench.common.screens.library.api;

import java.util.List;
import java.util.Set;

import org.ext.uberfire.social.activities.model.SocialUser;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.screens.examples.model.ExampleProject;

@Remote
public interface LibraryService {

    String LAST_MODIFIED_TIME = "lastModifiedTime";
    String CREATED_TIME = "creationTime";

    OrganizationalUnitRepositoryInfo getDefaultOrganizationalUnitRepositoryInfo();

    OrganizationalUnitRepositoryInfo getOrganizationalUnitRepositoryInfo(final OrganizationalUnit selectedOrganizationalUnit);

    LibraryInfo getLibraryInfo(final OrganizationalUnit organizationalUnit);

    WorkspaceProject createProject(final String projectName,
                                   final OrganizationalUnit selectedOrganizationalUnit,
                                   final String description,
                                   final DeploymentMode deploymentMode);

    WorkspaceProject createProject(final OrganizationalUnit activeOrganizationalUnit,
                                   final POM pom,
                                   final DeploymentMode mode);

    Boolean thereIsAProjectInTheWorkbench();

    List<AssetInfo> getProjectAssets(final ProjectAssetsQuery query);

    Boolean hasProjects(final OrganizationalUnit organizationalUnit);

    Boolean hasAssets(final WorkspaceProject project);

    Set<ExampleProject> getExampleProjects();

    Set<ExampleProject> getProjects(final String repositoryUrl);

    WorkspaceProject importProject(final OrganizationalUnit organizationalUnit,
                                   final ExampleProject exampleProject);

    List<OrganizationalUnit> getOrganizationalUnits();

    OrganizationalUnit getDefaultOrganizationalUnit();

    GAV createGAV(final String projectName,
                  final OrganizationalUnit selectedOrganizationalUnit);

    List<SocialUser> getAllUsers();

    void migrate(final WorkspaceProject activeProject);
}
