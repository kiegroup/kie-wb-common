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

package org.kie.workbench.common.services.datamodel.backend.server.cache;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.cache.LRUCache;
import org.guvnor.common.services.project.model.Project;
import org.kie.soup.project.datamodel.oracle.ProjectDataModelOracle;
import org.kie.workbench.common.services.backend.builder.core.LRUProjectDependenciesClassLoaderCache;
import org.kie.workbench.common.services.shared.project.KieProject;

/**
 * A simple LRU cache for Project DataModelOracles
 */
@ApplicationScoped
@Named("ProjectDataModelOracleCache")
public class LRUProjectDataModelOracleCache extends LRUCache<Project, ProjectDataModelOracle> {

    private ProjectDataModelOracleBuilderProvider builderProvider;
    private LRUProjectDependenciesClassLoaderCache projectDependenciesClassLoaderCache;
    private LRUPackageDataModelOracleCache packageDataModelOracleCache;

    public LRUProjectDataModelOracleCache() {
    }

    @Inject
    public LRUProjectDataModelOracleCache(final ProjectDataModelOracleBuilderProvider builderProvider,
                                          final LRUProjectDependenciesClassLoaderCache projectDependenciesClassLoaderCache,
                                          final LRUPackageDataModelOracleCache packageDataModelOracleCache) {
        this.builderProvider = builderProvider;
        this.projectDependenciesClassLoaderCache = projectDependenciesClassLoaderCache;
        this.packageDataModelOracleCache = packageDataModelOracleCache;
    }

    @Override
    public void invalidateCache(final Project project) {
        packageDataModelOracleCache.invalidateCache(project);
        super.invalidateCache(project);
    }

    //Check the ProjectOracle for the Project has been created, otherwise create one!
    public synchronized ProjectDataModelOracle assertProjectDataModelOracle(final KieProject project) {
        //this call is used to load the classloader and the correct KieMetaData
        projectDependenciesClassLoaderCache.assertDependenciesClassLoader(project);

        ProjectDataModelOracle projectOracle = getEntry(project);

        if (projectOracle == null) {
            projectOracle = buildAndSetEntry(project);
        }
        return projectOracle;
    }

    private ProjectDataModelOracle buildAndSetEntry(final KieProject project) {
        ProjectDataModelOracle projectOracle;
        projectOracle = makeProjectOracle(project);
        setEntry(project, projectOracle);
        return projectOracle;
    }

    private ProjectDataModelOracle makeProjectOracle(final KieProject project) {
        return builderProvider.newBuilder(project).build();
    }
}