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

package org.kie.workbench.common.dmn.client.widgets.grid.controls.container;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasControl;

@Dependent
public class CellEditorControlImpl extends AbstractCanvasControl<AbstractCanvas> implements CellEditorControl {

    private CellEditorControlsView view;
    private CellEditorControls editorControls;

    @Inject
    public CellEditorControlImpl(final CellEditorControlsView view) {
        this.view = view;
    }

    @Override
    public void bind(final DMNSession session) {
        this.editorControls = new CellEditorControls(session::getGridPanel,
                                                     view);
    }

    @Override
    protected void doInit() {
    }

    @Override
    protected void doDestroy() {
        view = null;
        editorControls = null;
    }

    @Override
    public CellEditorControls getCellEditorControls() {
        return editorControls;
    }
}
