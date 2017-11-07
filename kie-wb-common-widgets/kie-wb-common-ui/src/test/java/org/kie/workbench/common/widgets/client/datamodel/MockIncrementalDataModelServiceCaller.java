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

package org.kie.workbench.common.widgets.client.datamodel;

import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.soup.project.datamodel.imports.Imports;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.backend.builder.cache.ProjectBuildData;
import org.kie.workbench.common.services.backend.builder.cache.ProjectCache;
import org.kie.workbench.common.services.datamodel.backend.server.IncrementalDataModelServiceImpl;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleIncrementalPayload;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.vfs.Path;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockIncrementalDataModelServiceCaller implements Caller<IncrementalDataModelService> {

    private IncrementalDataModelServiceImplWrapper service;

    public MockIncrementalDataModelServiceCaller() {
        this(mock(PackageDataModelOracle.class));
    }

    public MockIncrementalDataModelServiceCaller(final PackageDataModelOracle packageLoader) {
        final KieProject project = mock(KieProject.class);
        final Package pkg = new Package(mock(Path.class),
                                        mock(Path.class),
                                        mock(Path.class),
                                        mock(Path.class),
                                        mock(Path.class),
                                        packageLoader.getPackageName(),
                                        packageLoader.getPackageName(),
                                        packageLoader.getPackageName());
        final ProjectCache projectCache = mock(ProjectCache.class);

        final ProjectBuildData projectBuildData = mock(ProjectBuildData.class);
        when(projectBuildData.getPackageDataModelOracle()).thenReturn(packageLoader);
        when(projectCache.getOrCreateEntry(project)).thenReturn(projectBuildData);

        final KieProjectService projectService = mock(KieProjectService.class);
        when(projectService.resolveProject(any(Path.class))).thenReturn(project);
        when(projectService.resolvePackage(any(Path.class))).thenReturn(pkg);

        this.service = new IncrementalDataModelServiceImplWrapper(projectCache,
                                                                  projectService);
    }

    @Override
    public IncrementalDataModelService call() {
        return service;
    }

    @Override
    public IncrementalDataModelService call(final RemoteCallback<?> remoteCallback) {
        service.setCallback(remoteCallback);
        return service;
    }

    @Override
    public IncrementalDataModelService call(final RemoteCallback<?> remoteCallback,
                                            final ErrorCallback<?> errorCallback) {
        service.setCallback(remoteCallback);
        return service;
    }

    private static class IncrementalDataModelServiceImplWrapper extends IncrementalDataModelServiceImpl {

        private RemoteCallback<?> remoteCallback;

        public IncrementalDataModelServiceImplWrapper(final ProjectCache projectCache,
                                                      final KieProjectService projectService) {
            super(projectCache,
                  projectService);
        }

        public void setCallback(final RemoteCallback<?> remoteCallback) {
            this.remoteCallback = remoteCallback;
        }

        @Override
        public PackageDataModelOracleIncrementalPayload getUpdates(final Path resourcePath,
                                                                   final Imports imports,
                                                                   final String factType) {
            final PackageDataModelOracleIncrementalPayload payload = super.getUpdates(resourcePath,
                                                                                      imports,
                                                                                      factType);
            final RemoteCallback r = remoteCallback;
            r.callback(payload);
            return payload;
        }
    }
}
