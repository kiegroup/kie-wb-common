/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.event.screen;

import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.event.BaseSessionEvent;

public class ScreenDiagramModelUnSelectedEvent extends BaseSessionEvent {

    private final boolean isExplorerScreen;

    public ScreenDiagramModelUnSelectedEvent(final ClientSession session, final boolean isExplorerScreen) {
        super(session);
        this.isExplorerScreen = isExplorerScreen;
    }

    public boolean isExplorerScreen() {
        return isExplorerScreen;
    }

    @Override
    public String toString() {
        return "ScreenDiagramModelUnSelectedEvent [session=" + session + "]";
    }
}
