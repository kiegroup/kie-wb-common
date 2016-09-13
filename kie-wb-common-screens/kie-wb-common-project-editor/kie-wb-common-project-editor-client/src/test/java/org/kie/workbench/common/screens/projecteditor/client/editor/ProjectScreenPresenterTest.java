/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.projecteditor.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.asset.management.service.AssetManagementService;
import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.security.KieWorkbenchACL;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projecteditor.client.editor.extension.BuildOptionExtension;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.LockManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.file.SaveOperationService;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;

@RunWith(GwtMockitoTestRunner.class)
public class ProjectScreenPresenterTest
        extends ProjectScreenPresenterTestBase {

    @GwtMock
    @SuppressWarnings("unused")
    private com.google.gwt.user.client.ui.Widget dependenciesPart;

    private ProjectScreenModel model;

    @Before
    public void setup() {
        ApplicationPreferences.setUp( new HashMap<String, String>() );

        //The BuildOptions widget is manipulated in the Presenter so we need some nasty mocking
        mockBuildOptions();

        constructProjectScreenPresenter( new CallerMock<BuildService>( buildService ),
                                         new CallerMock<AssetManagementService>( assetManagementServiceMock ) );

        //Mock ProjectScreenService
        model = new ProjectScreenModel();
        final POM pom = mockProjectScreenService( model );

        //Mock BuildService
        mockBuildService( buildService );

        //Mock LockManager initialisation
        mockLockManager( model );

        //Mock ProjectContext
        mockProjectContext( pom,
                            repository,
                            project,
                            pomPath );

        //Trigger initialisation of view. Unfortunately this is the only way to initialise a Project in the Presenter
        context.onProjectContextChanged( new ProjectContextChangeEvent( mock( OrganizationalUnit.class ),
                                                                        repository,
                                                                        "master",
                                                                        project ) );

        verify( view,
                times( 1 ) ).setDeployToRuntimeSetting( eq( false ) );
        verify( view,
                times( 1 ) ).setGAVCheckDisabledSetting( eq( false ) );
        verify( view,
                times( 1 ) ).showBusyIndicator( eq( CommonConstants.INSTANCE.Loading() ) );
        verify( view,
                times( 1 ) ).hideBusyIndicator();
    }

    @Test
    public void testBuildCommand() {
        presenter.triggerBuild();

        verify( notificationEvent ).fire( argThat( new ArgumentMatcher<NotificationEvent>() {
            @Override
            public boolean matches( final Object argument ) {
                final NotificationEvent event = (NotificationEvent) argument;
                final String notification = event.getNotification();
                final NotificationEvent.NotificationType type = event.getType();

                return notification.equals( ProjectEditorResources.CONSTANTS.BuildSuccessful() ) &&
                        type.equals( NotificationEvent.NotificationType.SUCCESS );
            }
        } ) );

        verify( view,
                times( 1 ) ).showBusyIndicator( eq( ProjectEditorResources.CONSTANTS.Building() ) );
        //There are two calls to "hide" by this stage; one from the view initialisation one for the build
        verify( view,
                times( 2 ) ).hideBusyIndicator();
    }

    @Test
    public void testBuildCommandFail() {
        BuildMessage message = mock( BuildMessage.class );
        List<BuildMessage> messages = new ArrayList<BuildMessage>();
        messages.add( message );

        BuildResults results = mock( BuildResults.class );
        when( results.getErrorMessages() ).thenReturn( messages );

        when( buildService.buildAndDeploy( any( KieProject.class ),
                                           any( DeploymentMode.class ) ) ).thenReturn( results );

        presenter.triggerBuild();

        verify( notificationEvent ).fire( argThat( new ArgumentMatcher<NotificationEvent>() {
            @Override
            public boolean matches( final Object argument ) {
                final NotificationEvent event = (NotificationEvent) argument;
                final String notification = event.getNotification();
                final NotificationEvent.NotificationType type = event.getType();

                return notification.equals( ProjectEditorResources.CONSTANTS.BuildFailed() ) &&
                        type.equals( NotificationEvent.NotificationType.ERROR );
            }
        } ) );

        verify( view,
                times( 1 ) ).showBusyIndicator( eq( ProjectEditorResources.CONSTANTS.Building() ) );
        //There are two calls to "hide" by this stage; one from the view initialisation one for the build
        verify( view,
                times( 2 ) ).hideBusyIndicator();
    }

    @Test
    public void testBuildAndInstallCommand() {
        presenter.triggerBuildAndInstall();

        verify( notificationEvent ).fire( argThat( new ArgumentMatcher<NotificationEvent>() {
            @Override
            public boolean matches( final Object argument ) {
                final NotificationEvent event = (NotificationEvent) argument;
                final String notification = event.getNotification();
                final NotificationEvent.NotificationType type = event.getType();

                return notification.equals( ProjectEditorResources.CONSTANTS.BuildProcessStarted() ) &&
                        type.equals( NotificationEvent.NotificationType.SUCCESS );
            }
        } ) );

        verify( notificationEvent, times( 1 ) ).fire( any( NotificationEvent.class ) );
        verifyBusyShowHideAnyString( 1, 1 );
    }

    @Test
    public void testBuildAndInstallCommandFail() {

        doThrow( new RuntimeException() ).when( assetManagementServiceMock ).buildProject(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean()
                                                                                         );

        presenter.triggerBuildAndInstall();

        verify( notificationEvent, never() ).fire( any( NotificationEvent.class ) );

        verify( view, times( 1 ) ).showUnexpectedErrorPopup( anyString() );

        verifyBusyShowHideAnyString( 1, 1 );
    }

    @Test
    public void testBuildAndDeployCommand() {
        presenter.triggerBuildAndDeploy( "user",
                                         "password",
                                         "url" );

        verify( notificationEvent ).fire( argThat( new ArgumentMatcher<NotificationEvent>() {
            @Override
            public boolean matches( final Object argument ) {
                final NotificationEvent event = (NotificationEvent) argument;
                final String notification = event.getNotification();
                final NotificationEvent.NotificationType type = event.getType();

                return notification.equals( ProjectEditorResources.CONSTANTS.BuildProcessStarted() ) &&
                        type.equals( NotificationEvent.NotificationType.SUCCESS );
            }
        } ) );

        verify( notificationEvent, times( 1 ) ).fire( any( NotificationEvent.class ) );
        verifyBusyShowHideAnyString( 1, 1 );
    }

    @Test
    public void testBuildAndDeployCommandFail() {
        doThrow( new RuntimeException() ).when( assetManagementServiceMock ).buildProject(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean()
                                                                                         );

        presenter.triggerBuildAndDeploy( "user",
                                         "password",
                                         "url" );

        verify( notificationEvent, never() ).fire( any( NotificationEvent.class ) );
        verify( view, times( 1 ) ).showUnexpectedErrorPopup( anyString() );

        verifyBusyShowHideAnyString( 1, 1 );
    }

    @Test
    public void testAlreadyRunningBuild() {
        constructProjectScreenPresenter( buildServiceCaller(), new CallerMock<AssetManagementService>( assetManagementServiceMock ) );

        presenter.triggerBuild();
        presenter.triggerBuild();

        verify( view, times( 1 ) ).showABuildIsAlreadyRunning();
        verify( notificationEvent, never() ).fire( any( NotificationEvent.class ) );
        verifyBusyShowHideAnyString( 2, 1 );
    }

    @Test
    public void testAlreadyRunningBuildAndInstall() {
        constructProjectScreenPresenter( new CallerMock<BuildService>( buildService ),
                                         assetManagementCaller() );
        presenter.onStartup( mock( PlaceRequest.class ) );

        presenter.triggerBuildAndInstall();
        presenter.triggerBuildAndInstall();

        verify( view, times( 1 ) ).showABuildIsAlreadyRunning();
        verify( notificationEvent, never() ).fire( any( NotificationEvent.class ) );
        verifyBusyShowHideAnyString( 2, 2 );
    }

    @Test
    public void testAlreadyRunningBuildAndDeploy() {
        constructProjectScreenPresenter( new CallerMock<BuildService>( buildService ),
                                         assetManagementCaller() );

        presenter.onStartup( mock( PlaceRequest.class ) );

        presenter.triggerBuildAndDeploy( "usr", "psw", "url" );
        presenter.triggerBuildAndDeploy( "usr", "psw", "url" );

        verify( view, times( 1 ) ).showABuildIsAlreadyRunning();
        verify( notificationEvent, never() ).fire( any( NotificationEvent.class ) );
        verifyBusyShowHideAnyString( 2, 2 );
    }

    @Test
    public void testIsDirtyBuild() {
        model.setPOM( mock( POM.class ) ); // causes isDirty evaluates as true
        presenter.triggerBuild();

        verify( view, times( 1 ) ).showSaveBeforeContinue( any( Command.class ), any( Command.class ), any( Command.class ) );
        verify( notificationEvent, never() ).fire( any( NotificationEvent.class ) );
        verifyBusyShowHideAnyString( 1, 1 );
    }

    @Test
    public void testIsDirtyBuildAndInstall() {
        model.setPOM( mock( POM.class ) ); // causes isDirty evaluates as true
        presenter.triggerBuildAndInstall();

        verify( view, times( 1 ) ).showSaveBeforeContinue( any( Command.class ), any( Command.class ), any( Command.class ) );
        verify( notificationEvent, never() ).fire( any( NotificationEvent.class ) );
        verifyBusyShowHideAnyString( 1, 1 );
    }

    @Test
    public void testIsDirtyBuildAndDeploy() {
        model.setPOM( mock( POM.class ) ); // causes isDirty evaluates as true
        presenter.triggerBuildAndDeploy( "usr", "psw", "url" );

        verify( view, times( 1 ) ).showSaveBeforeContinue( any( Command.class ), any( Command.class ), any( Command.class ) );
        verify( notificationEvent, never() ).fire( any( NotificationEvent.class ) );
        verifyBusyShowHideAnyString( 1, 1 );
    }

    @Test
    public void testOnDependenciesSelected() throws Exception {

        when( lockManagerInstanceProvider.get() ).thenReturn( mock( LockManager.class ) );

        Path pathToPOM = mock( Path.class );
        model.setPathToPOM( pathToPOM );

        when( view.getDependenciesPart() ).thenReturn( dependenciesPart );

        presenter.onStartup( mock( PlaceRequest.class ) );

        presenter.onDependenciesSelected();

        verify( view ).showDependenciesPanel();
    }

    @Test
    public void testSaveNonClashingGAV() throws Exception {
        verify( view,
                times( 1 ) ).showBusyIndicator( eq( CommonConstants.INSTANCE.Loading() ) );
        verify( view,
                times( 1 ) ).hideBusyIndicator();

        final Command command = presenter.getSaveCommand( DeploymentMode.VALIDATED );
        command.execute();

        verify( projectScreenService,
                times( 1 ) ).save( eq( presenter.pathToPomXML ),
                                   eq( model ),
                                   eq( "" ),
                                   eq( DeploymentMode.VALIDATED ) );
        verify( view,
                times( 1 ) ).showBusyIndicator( eq( CommonConstants.INSTANCE.Saving() ) );
        verify( view,
                times( 2 ) ).hideBusyIndicator();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSaveClashingGAV() throws Exception {
        verify( view,
                times( 1 ) ).showBusyIndicator( eq( CommonConstants.INSTANCE.Loading() ) );
        verify( view,
                times( 1 ) ).hideBusyIndicator();

        doThrow( GAVAlreadyExistsException.class ).when( projectScreenService ).save( presenter.pathToPomXML,
                                                                                      model,
                                                                                      "",
                                                                                      DeploymentMode.VALIDATED );

        final GAV gav = model.getPOM().getGav();
        final ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass( Command.class );
        final Command command = presenter.getSaveCommand( DeploymentMode.VALIDATED );
        command.execute();

        verify( projectScreenService,
                times( 1 ) ).save( eq( presenter.pathToPomXML ),
                                   eq( model ),
                                   eq( "" ),
                                   eq( DeploymentMode.VALIDATED ) );

        verify( conflictingRepositoriesPopup,
                times( 1 ) ).setContent( eq( gav ),
                                         any( Set.class ),
                                         commandArgumentCaptor.capture() );
        verify( conflictingRepositoriesPopup,
                times( 1 ) ).show();

        assertNotNull( commandArgumentCaptor.getValue() );

        //Emulate User electing to force save
        commandArgumentCaptor.getValue().execute();

        verify( projectScreenService,
                times( 1 ) ).save( eq( presenter.pathToPomXML ),
                                   eq( model ),
                                   eq( "" ),
                                   eq( DeploymentMode.FORCED ) );
        //We attempted to save the Project twice
        verify( view,
                times( 2 ) ).showBusyIndicator( eq( CommonConstants.INSTANCE.Saving() ) );
        //We hid the BusyPopup 1 x loading, 1 x per save attempt
        verify( view,
                times( 3 ) ).hideBusyIndicator();
    }

    @Test
    public void testBuildManagedRepository() throws Exception {
        verify( view,
                times( 1 ) ).showBusyIndicator( eq( CommonConstants.INSTANCE.Loading() ) );
        verify( view,
                times( 1 ) ).hideBusyIndicator();

        final Map<String, Object> env = new HashMap<String, Object>() {
            {
                put( "managed",
                     true );
            }
        };
        when( repository.getEnvironment() ).thenReturn( env );

        presenter.triggerBuild();

        verify( buildService,
                times( 1 ) ).build( eq( project ) );
        verify( view,
                times( 1 ) ).showBusyIndicator( eq( ProjectEditorResources.CONSTANTS.Building() ) );
        verify( view,
                times( 2 ) ).hideBusyIndicator();
    }

    @Test
    public void testBuildNotManagedRepositoryNonClashingGAV() throws Exception {
        verify( view,
                times( 1 ) ).showBusyIndicator( eq( CommonConstants.INSTANCE.Loading() ) );
        verify( view,
                times( 1 ) ).hideBusyIndicator();

        final Map<String, Object> env = new HashMap<String, Object>() {
            {
                put( "managed",
                     false );
            }
        };
        when( repository.getEnvironment() ).thenReturn( env );

        presenter.triggerBuild();

        verify( buildService,
                times( 1 ) ).buildAndDeploy( eq( project ),
                                             eq( DeploymentMode.VALIDATED ) );
        verify( view,
                times( 1 ) ).showBusyIndicator( eq( ProjectEditorResources.CONSTANTS.Building() ) );
        verify( view,
                times( 2 ) ).hideBusyIndicator();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuildNotManagedRepositoryClashingGAV() throws Exception {
        verify( view,
                times( 1 ) ).showBusyIndicator( eq( CommonConstants.INSTANCE.Loading() ) );
        verify( view,
                times( 1 ) ).hideBusyIndicator();

        final Map<String, Object> env = new HashMap<String, Object>() {
            {
                put( "managed",
                     false );
            }
        };
        when( repository.getEnvironment() ).thenReturn( env );

        doThrow( GAVAlreadyExistsException.class ).when( buildService ).buildAndDeploy( eq( project ),
                                                                                        eq( DeploymentMode.VALIDATED ) );

        final GAV gav = model.getPOM().getGav();
        final ArgumentCaptor<Command> commandArgumentCaptor = ArgumentCaptor.forClass( Command.class );

        presenter.triggerBuild();

        verify( buildService,
                times( 1 ) ).buildAndDeploy( eq( project ),
                                             eq( DeploymentMode.VALIDATED ) );

        verify( conflictingRepositoriesPopup,
                times( 1 ) ).setContent( eq( gav ),
                                         any( Set.class ),
                                         commandArgumentCaptor.capture() );
        verify( conflictingRepositoriesPopup,
                times( 1 ) ).show();

        assertNotNull( commandArgumentCaptor.getValue() );

        //Emulate User electing to force save
        commandArgumentCaptor.getValue().execute();

        verify( conflictingRepositoriesPopup,
                times( 1 ) ).hide();

        verify( buildService,
                times( 1 ) ).buildAndDeploy( eq( project ),
                                             eq( DeploymentMode.FORCED ) );
        //We attempted to build the Project twice
        verify( view,
                times( 2 ) ).showBusyIndicator( eq( ProjectEditorResources.CONSTANTS.Building() ) );
        //We hid the BusyPopup 1 x loading, 1 x per build attempt
        verify( view,
                times( 3 ) ).hideBusyIndicator();
    }

    private void verifyBusyShowHideAnyString( int show,
                                              int hide ) {
        //Check the "Busy" popup has not been shown again
        verify( view,
                times( show ) ).showBusyIndicator( any( String.class ) );
        verify( view,
                times( hide ) ).hideBusyIndicator();
    }

    private Caller assetManagementCaller() {
        Caller<AssetManagementService> caller = mock( Caller.class );
        when( caller.call( any( RemoteCallback.class ), any( ErrorCallback.class ) ) ).thenAnswer( new Answer<AssetManagementService>() {
            @Override
            public AssetManagementService answer( InvocationOnMock invocationOnMock ) throws Throwable {
                //not calling callback causes building is still set to true
                return assetManagementServiceMock;
            }
        } );

        return caller;
    }

    private Caller buildServiceCaller() {
        Caller<BuildService> caller = mock( Caller.class );
        when( caller.call( any( RemoteCallback.class ), any( ErrorCallback.class ) ) ).thenAnswer( new Answer<BuildService>() {
            @Override
            public BuildService answer( InvocationOnMock invocationOnMock ) throws Throwable {
                //not calling callback causes building is still set to true
                return buildService;
            }
        } );

        return caller;
    }


}
