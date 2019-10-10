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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.docks;

import org.kie.workbench.common.stunner.kogito.api.docks.DiagramEditorDock;
import org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorPropertiesScreen;
import org.uberfire.client.workbench.docks.UberfireDockPosition;

public class KogitoPropertiesDockTest extends BaseKogitoDockTest {

    @Override
    protected DiagramEditorDock makeDock() {
        return new KogitoPropertiesDock(uberfireDocks,
                                        translationService);
    }

    @Override
    protected UberfireDockPosition position() {
        return UberfireDockPosition.EAST;
    }

    @Override
    protected String screen() {
        return DiagramEditorPropertiesScreen.SCREEN_ID;
    }
}
