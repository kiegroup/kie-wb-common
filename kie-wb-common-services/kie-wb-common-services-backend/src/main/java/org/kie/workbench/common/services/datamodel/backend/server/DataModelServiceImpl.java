/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodel.backend.server;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.project.model.Package;
import org.kie.soup.project.datamodel.commons.oracle.PackageDataModelOracleImpl;
import org.kie.soup.project.datamodel.commons.oracle.ProjectDataModelOracleImpl;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.soup.project.datamodel.oracle.ProjectDataModelOracle;
import org.kie.workbench.common.services.backend.builder.cache.ProjectCache;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.vfs.Path;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@ApplicationScoped
public class DataModelServiceImpl
        implements DataModelService {

    private static final ProjectDataModelOracle EMPTY_PROJECT_MODEL = new ProjectDataModelOracleImpl();
    private static final PackageDataModelOracle EMPTY_PKG_MODEL = new PackageDataModelOracleImpl();

    private ProjectCache projectCache;

    private KieProjectService projectService;

    public DataModelServiceImpl() {
    }

    @Inject
    public DataModelServiceImpl(final ProjectCache projectCache,
                                final KieProjectService projectService) {
        this.projectCache = projectCache;
        this.projectService = projectService;
    }

    @Override
    public PackageDataModelOracle getDataModel(final Path resourcePath) {
        try {
            final Optional<KieProject> project = resolveProject(checkNotNull("resourcePath", resourcePath));
            final Optional<Package> pkg = resolvePackage(resourcePath);

            //Resource was not within a Project structure
            if (!project.isPresent()) {
                return EMPTY_PKG_MODEL;
            }

            return projectCache.getOrCreateEntry(project.get()).getPackageDataModelOracle();
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public ProjectDataModelOracle getProjectDataModel(final Path resourcePath) {
        try {
            final Optional<KieProject> project = resolveProject(checkNotNull("resourcePath", resourcePath));

            //Resource was not within a Project structure
            if (!project.isPresent()) {
                return EMPTY_PROJECT_MODEL;
            }
            //Retrieve (or build) oracle
            return projectCache.getOrCreateEntry(project.get()).getProjectDataModelOracle();
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    private Optional<KieProject> resolveProject(final Path resourcePath) {
        return Optional.ofNullable(projectService.resolveProject(resourcePath));
    }

    private Optional<Package> resolvePackage(final Path resourcePath) {
        return Optional.ofNullable(projectService.resolvePackage(resourcePath));
    }
}
