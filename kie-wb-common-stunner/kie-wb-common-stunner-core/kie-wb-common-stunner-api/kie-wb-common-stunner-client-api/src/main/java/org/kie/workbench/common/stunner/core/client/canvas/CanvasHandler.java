/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.canvas;

import org.kie.workbench.common.stunner.core.diagram.Diagram;

public interface CanvasHandler<D extends Diagram, C extends Canvas> {

    /**
     * Initializes the handler for a given canvas.
     */
    CanvasHandler<D, C> initialize( final C canvas );

    /**
     * Draws the given diagram..
     */
    CanvasHandler<D, C> draw( final D diagram );

    /**
     * The managed diagram instance.
     */
    D getDiagram();

    /**
     * The managed canvas instance.
     */
    C getCanvas();

    /**
     * Clears the canvas and the the diagram's state, but does not destroy this handler instance.
     * It can further be re-initialized in order to handle other canvas/diagram instances.
     */
    CanvasHandler<D, C> clear();

    /**
     * Destroys whatever canvas handler state is present and all its members' states, it will be no longer used.
     */
    void destroy();
}
