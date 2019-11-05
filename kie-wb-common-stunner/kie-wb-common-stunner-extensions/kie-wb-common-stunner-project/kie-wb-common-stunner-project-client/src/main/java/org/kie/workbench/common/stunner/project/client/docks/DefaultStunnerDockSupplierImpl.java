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
package org.kie.workbench.common.stunner.project.client.docks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;

import org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorExplorerScreen;
import org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorPropertiesScreen;
import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@Default
@Dependent
public class DefaultStunnerDockSupplierImpl implements StunnerDockSupplier {

    protected static final DefaultWorkbenchConstants CONSTANTS = DefaultWorkbenchConstants.INSTANCE;

    protected static final UberfireDockPosition DOCK_POSITION = UberfireDockPosition.EAST;
    protected static final Double SIZE = 450.0;

    protected static final String PROPERTIES_DOCK_SCREEN_ID = DiagramEditorPropertiesScreen.SCREEN_ID;
    protected static final String PROPERTIES_ICON_TYPE = "PENCIL_SQUARE_O";
    protected static final String PROPERTIES_LABEL = CONSTANTS.DocksStunnerPropertiesTitle();
    protected static final String PROPERTIES_TOOLTIP = CONSTANTS.DocksStunnerPropertiesTooltip();

    protected static final String EXPLORER_DOCK_SCREEN_ID = DiagramEditorExplorerScreen.SCREEN_ID;
    protected static final String EXPLORER_ICON_TYPE = "EYE";
    protected static final String EXPLORER_LABEL = CONSTANTS.DocksStunnerExplorerTitle();

    @Override
    public Collection<UberfireDock> getDocks(String perspectiveIdentifier) {
        List<UberfireDock> result = new ArrayList<>();

        final UberfireDock propertiesDock = new UberfireDock(DOCK_POSITION,
                                                             PROPERTIES_ICON_TYPE,
                                                             new DefaultPlaceRequest(PROPERTIES_DOCK_SCREEN_ID),
                                                             perspectiveIdentifier)
                .withSize(SIZE)
                .withLabel(PROPERTIES_LABEL)
                .withTooltip(PROPERTIES_TOOLTIP);

        final UberfireDock explorerDock = new UberfireDock(DOCK_POSITION,
                                                           EXPLORER_ICON_TYPE,
                                                           new DefaultPlaceRequest(EXPLORER_DOCK_SCREEN_ID),
                                                           perspectiveIdentifier)
                .withSize(SIZE)
                .withLabel(EXPLORER_LABEL);

        result.add(propertiesDock);
        result.add(explorerDock);

        return result;
    }
}
