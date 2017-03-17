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

import java.util.Collection;
import java.util.Collections;
import javax.enterprise.event.Event;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.model.Project;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.services.shared.builder.service.MavenBuildService;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.workbench.events.NotificationEvent;

public abstract class MavenBuildOptionExtensionBase
        implements BuildOptionExtension {

    protected Caller< MavenBuildService > mavenBuildService;

    protected Event< NotificationEvent > notificationEvent;

    protected Event< BuildResults > buildResultsEvent;


    public MavenBuildOptionExtensionBase( Caller< MavenBuildService > mavenBuildService,
                                          Event< NotificationEvent > notificationEvent,
                                          Event< BuildResults > buildResultsEvent ) {
        this.mavenBuildService = mavenBuildService;
        this.notificationEvent = notificationEvent;
        this.buildResultsEvent = buildResultsEvent;
    }

    @Override
    public Collection< Widget > getBuildOptions( Project project ) {
        return Collections.singleton( createNavLink( project ) );
    }

    protected Widget createNavLink( final Project project ) {
        return new AnchorListItem( getLinkName( ) ) {{
            addClickHandler( createClickHandler( project ) );
        }};
    }

    protected ClickHandler createClickHandler( final Project project ) {
        return event -> {
            BusyPopup.showMessage( getBuildingMessage( ) );
            mavenBuildService.call( getBuildSuccessCallback( ), getErrorCallback( ) ).build( project, isDeployment( ) );
        };
    }

    protected RemoteCallback< BuildResults > getBuildSuccessCallback( ) {
        return result -> {
            if ( result.getErrorMessages( ).isEmpty( ) ) {
                notificationEvent.fire( new NotificationEvent( ProjectEditorResources.CONSTANTS.BuildSuccessful( ),
                        NotificationEvent.NotificationType.SUCCESS ) );
            } else {
                notificationEvent.fire( new NotificationEvent( ProjectEditorResources.CONSTANTS.BuildFailed( ),
                        NotificationEvent.NotificationType.ERROR ) );
            }
            buildResultsEvent.fire( result );
            BusyPopup.close( );
        };
    }

    protected ErrorCallback< Message > getErrorCallback( ) {
        return new DefaultErrorCallback( ) {
            @Override
            public boolean error( Message message, Throwable throwable ) {
                BusyPopup.close( );
                return super.error( message, throwable );
            }
        };
    }

    protected abstract String getLinkName( );

    protected abstract String getBuildingMessage( );

    protected abstract boolean isDeployment( );

}