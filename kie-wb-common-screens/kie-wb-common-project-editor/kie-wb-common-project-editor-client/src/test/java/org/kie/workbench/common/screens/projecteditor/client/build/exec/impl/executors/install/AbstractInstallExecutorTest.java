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

package org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.install;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.junit.Test;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.AbstractExecutorTest;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources.CONSTANTS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class AbstractInstallExecutorTest<RUNNER extends AbstractInstallExecutor> extends AbstractExecutorTest<RUNNER> {

    @Test
    public void testDefaultBuildAndInstall() {

        runner.run(context);

        verify(buildDialog).startBuild();

        verify(buildDialog).showBusyIndicator(CONSTANTS.Building());

        verifyNotification(ProjectEditorResources.CONSTANTS.BuildAndInstallSuccessful(), NotificationEvent.NotificationType.SUCCESS);

        verify(buildResultsEvent).fire(any());

        verify(buildDialog).stopBuild();
    }

    @Test
    public void testDefaultBuildAndInstallErrorResults() {

        BuildMessage message = mock(BuildMessage.class);
        List<BuildMessage> messages = new ArrayList<>();
        messages.add(message);

        BuildResults results = mock(BuildResults.class);
        when(results.getErrorMessages()).thenReturn(messages);

        when(buildServiceMock.buildAndDeploy(any(Module.class), any(DeploymentMode.class))).thenReturn(results);

        runner.run(context);

        verify(buildDialog).startBuild();

        verify(buildDialog).showBusyIndicator(CONSTANTS.Building());

        verifyNotification(ProjectEditorResources.CONSTANTS.BuildFailed(), NotificationEvent.NotificationType.ERROR);

        verify(buildResultsEvent).fire(any());

        verify(buildDialog).stopBuild();
    }

    @Test
    public void testBuildAndInstallException() {
        when(buildServiceMock.buildAndDeploy(any(Module.class), any(DeploymentMode.class))).thenAnswer(invocationOnMock -> {
            throw new Exception();
        });

        runner.run(context);

        verify(buildDialog).startBuild();

        verify(buildDialog).showBusyIndicator(CONSTANTS.Building());

        verifyNotification(CONSTANTS.BuildFailed(), NotificationEvent.NotificationType.ERROR);

        verify(buildResultsEvent, never()).fire(any());

        verify(buildDialog).stopBuild();
    }
}
