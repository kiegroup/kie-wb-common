/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.projecteditor.client.editor.extension;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.services.shared.builder.service.MavenBuildService;
import org.uberfire.workbench.events.NotificationEvent;

@ApplicationScoped
public class MavenBuildOptionExtension
        extends MavenBuildOptionExtensionBase {

    @Inject
    public MavenBuildOptionExtension( Caller< MavenBuildService > mavenBuildService,
                                      Event< NotificationEvent > notificationEvent,
                                      Event< BuildResults > buildResultsEvent ) {
        super( mavenBuildService, notificationEvent, buildResultsEvent );
    }

    @Override
    protected String getLinkName( ) {
        return "Maven Build";
    }

    @Override
    protected String getBuildingMessage( ) {
        return "Executing Maven Build";
    }

    @Override
    protected boolean isDeployment( ) {
        return false;
    }
}