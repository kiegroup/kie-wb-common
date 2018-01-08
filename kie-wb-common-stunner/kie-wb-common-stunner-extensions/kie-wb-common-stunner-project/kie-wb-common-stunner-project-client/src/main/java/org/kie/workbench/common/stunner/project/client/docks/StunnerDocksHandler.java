/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.project.client.docks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import org.kie.workbench.common.stunner.core.client.event.screen.ScreenMaximizedEvent;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramLoseFocusEvent;
import org.kie.workbench.common.workbench.client.docks.WorkbenchDockEntry;
import org.kie.workbench.common.workbench.client.docks.impl.AbstractWorkbenchDocksHandler;
import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@Dependent
public class StunnerDocksHandler extends AbstractWorkbenchDocksHandler {

    protected DefaultWorkbenchConstants constants = DefaultWorkbenchConstants.INSTANCE;

    @Override
    public Collection<WorkbenchDockEntry> provideDocks(String perspectiveIdentifier) {
        List<WorkbenchDockEntry> result = new ArrayList<>();

        UberfireDock propertiesDock = new UberfireDock(UberfireDockPosition.EAST,
                                                       "PENCIL_SQUARE_O",
                                                       new DefaultPlaceRequest("ProjectDiagramPropertiesScreen"),
                                                       perspectiveIdentifier);
        propertiesDock.withSize(450);
        propertiesDock.withLabel(constants.DocksStunnerPropertiesTitle());

        WorkbenchDockEntry propertiesEntry = new WorkbenchDockEntry(propertiesDock, true);
        result.add(propertiesEntry);

        UberfireDock explorerDock = new UberfireDock(UberfireDockPosition.WEST,
                                                     "EYE",
                                                     new DefaultPlaceRequest("ProjectDiagramExplorerScreen"),
                                                     perspectiveIdentifier);
        explorerDock.withSize(450);
        explorerDock.withLabel(constants.DocksStunnerExplorerTitle());

        WorkbenchDockEntry explorerEntry = new WorkbenchDockEntry(explorerDock, true);
        result.add(explorerEntry);

        return result;
    }

    public void onDiagramFocusEvent(@Observes OnDiagramFocusEvent event) {
        refreshDocks(true,
                     false);
    }

    public void onDiagramLoseFocusEvent(@Observes OnDiagramLoseFocusEvent event) {
        refreshDocks(true,
                     true);
    }

    private void onDiagramEditorMaximized(@Observes ScreenMaximizedEvent event) {
        refreshDocks(true,
                     false);
    }
}
