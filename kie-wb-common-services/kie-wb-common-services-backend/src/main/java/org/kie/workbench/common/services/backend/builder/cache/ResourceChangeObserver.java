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
package org.kie.workbench.common.services.backend.builder.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.common.services.builder.ObservablePOMFile;
import org.guvnor.common.services.builder.ResourceChangeObservableFile;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceChange;
import org.uberfire.workbench.events.ResourceChangeType;
import org.uberfire.workbench.events.ResourceCopiedEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

/**
 * Server side component that observes for the different resource add/delete/update events related to
 * a given project and that causes the ProjectDataModelOracle to be invalidated. Typically .java, .class and pom.xml
 * files. When such a resource is modified an InvalidateDMOProjectCacheEvent event is fired.
 */
@ApplicationScoped
public class ResourceChangeObserver {

    private static final Logger logger = LoggerFactory.getLogger(ResourceChangeObserver.class);

    @Inject
    private ProjectService<? extends Project> projectService;

    @Inject
    @Any
    private Instance<ResourceChangeObservableFile> observableFiles;

    @Inject
    private ProjectCache projectCache;

    @Inject
    private ObservablePOMFile observablePomFile;

    public void processResourceAdd(@Observes final ResourceAddedEvent resourceAddedEvent) {
        processResourceChange(resourceAddedEvent.getSessionInfo(),
                              resourceAddedEvent.getPath(),
                              ResourceChangeType.ADD);
    }

    public void processResourceDelete(@Observes final ResourceDeletedEvent resourceDeletedEvent) {
        processResourceChange(resourceDeletedEvent.getSessionInfo(),
                              resourceDeletedEvent.getPath(),
                              ResourceChangeType.DELETE);
    }

    public void processResourceUpdate(@Observes final ResourceUpdatedEvent resourceUpdatedEvent) {
        processResourceChange(resourceUpdatedEvent.getSessionInfo(),
                              resourceUpdatedEvent.getPath(),
                              ResourceChangeType.UPDATE);
    }

    public void processResourceCopied(@Observes final ResourceCopiedEvent resourceCopiedEvent) {
        processResourceChange(resourceCopiedEvent.getSessionInfo(),
                              resourceCopiedEvent.getPath(),
                              ResourceChangeType.COPY);
    }

    public void processResourceRenamed(@Observes final ResourceRenamedEvent resourceRenamedEvent) {
        processResourceChange(resourceRenamedEvent.getSessionInfo(),
                              resourceRenamedEvent.getDestinationPath(),
                              ResourceChangeType.RENAME);
    }

    public void processBatchChanges(@Observes final ResourceBatchChangesEvent resourceBatchChangesEvent) {
        final Map<Path, Collection<ResourceChange>> batchChanges = resourceBatchChangesEvent.getBatch();
        if (batchChanges == null || batchChanges.isEmpty()) {
            //un expected case
            logger.warn("No batchChanges was present for the given resourceBatchChangesEvent: " + resourceBatchChangesEvent);
        } else {
            processBatchResourceChanges(resourceBatchChangesEvent.getSessionInfo(), batchChanges);
        }
    }

    private void processResourceChange(final SessionInfo sessionInfo,
                                       final Path path,
                                       final ResourceChangeType changeType) {
        //Only process Project resources
        final Project project = projectService.resolveProject(path);
        if (project == null) {
            return;
        }

        final ProjectBuildData buildData = projectCache.getOrCreateEntry(project);

        if (isObservableResource(path)) {

            if (isPomFile(path)) {
                buildData.invalidate(ProjectBuildData.TypeOfInvalidation.POM);
            } else {
                buildData.invalidate(ProjectBuildData.TypeOfInvalidation.OBSERVABLE);
            }

            buildData.build();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Processing resource change for sessionInfo: " + sessionInfo
                                 + ", project: " + project
                                 + ", path: " + path
                                 + ", changeType: " + changeType);
        }
    }

    private void processBatchResourceChanges(final SessionInfo sessionInfo,
                                             final Map<Path, Collection<ResourceChange>> resourceChanges) {

        Project project;
        final Map<Project, Path> pendingNotifications = new HashMap<>();
        final Set<Project> modifiedPOMProjects = new HashSet<>();
        for (final Map.Entry<Path, Collection<ResourceChange>> pathCollectionEntry : resourceChanges.entrySet()) {

            //Only process Project resources
            project = projectService.resolveProject(pathCollectionEntry.getKey());
            if (project == null) {
                continue;
            }

            if (isPomFile(pathCollectionEntry.getKey())) {
                modifiedPOMProjects.add(project);
            }

            if (!pendingNotifications.containsKey(project) && isObservableResource(pathCollectionEntry.getKey())) {
                pendingNotifications.put(project, pathCollectionEntry.getKey());
            } else if (isPomFile(pathCollectionEntry.getKey())) {
                //if the pom.xml comes in the batch events set then use the pom.xml path for the cache invalidation event
                pendingNotifications.put(project, pathCollectionEntry.getKey());
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Processing resource change for sessionInfo: " + sessionInfo
                                     + ", project: " + project
                                     + ", path: " + pathCollectionEntry.getKey()
                                     + ", changeTypes: [" + pathCollectionEntry.getValue().toString() + "]");
            }
        }

        for (final Map.Entry<Project, Path> pendingEntry : pendingNotifications.entrySet()) {

            final ProjectBuildData buildData = projectCache.getOrCreateEntry(pendingEntry.getKey());

            if (modifiedPOMProjects.contains(pendingEntry.getKey())) {
                buildData.invalidate(ProjectBuildData.TypeOfInvalidation.POM);
            } else {
                buildData.invalidate(ProjectBuildData.TypeOfInvalidation.OBSERVABLE);
            }

            buildData.build();
        }
    }

    //Check if the changed file should invalidate the DMO cache
    private boolean isObservableResource(final Path path) {
        if (path == null) {
            return false;
        }
        for (ResourceChangeObservableFile observableFile : observableFiles) {
            if (observableFile.accept(path)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPomFile(final Path path) {
        return path != null && observablePomFile.accept(path);
    }
}
