/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard;

import java.util.function.Function;

import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;

/**
 * By implementing this interface you can append different nodes to canvas depending on pressed keyboard key
 * combination.
 */
public interface CanvasShortcutsControl<C extends CanvasHandler, S extends ClientSession> extends CanvasControl<C>,
                                                                                                  CanvasControl.SessionAware<S> {

    /**
     * Invoked each time some keys were pressed
     * @param keys
     */
    void onKeyDownEvent(final KeyboardEvent.Key... keys);

    /**
     * Appends node to an existing one with 'sourceNodeId'
     * @param sourceNodeId id of the source node
     * @param definitionCheck function that returns true if its argument is instance of demanded definition, false
     * otherwise.
     */
    void appendNode(final String sourceNodeId, final Function<Object, Boolean> definitionCheck);
}
