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

package org.kie.workbench.common.dmn.client.canvas.controls.toolbox;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import org.kie.workbench.common.dmn.client.editors.contextmenu.ContextMenu;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxAction;
import org.kie.workbench.common.stunner.core.client.resources.StunnerCommonImageResources;
import org.kie.workbench.common.stunner.core.client.shape.ImageDataUriGlyph;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

@Dependent
public class DMNEditDRDToolboxAction implements ToolboxAction<AbstractCanvasHandler> {

    private final ContextMenu drdContextMenu;

    @Inject
    public DMNEditDRDToolboxAction(final ContextMenu drdContextMenu) {
        this.drdContextMenu = drdContextMenu;
    }

    @Override
    public Glyph getGlyph(final AbstractCanvasHandler canvasHandler, final String uuid) {
        return ImageDataUriGlyph.create(StunnerCommonImageResources.INSTANCE.drd().getSafeUri());
    }

    @Override
    public String getTitle(final AbstractCanvasHandler canvasHandler, final String uuid) {
        return "DRD Actions";
    }

    @Override
    public ToolboxAction<AbstractCanvasHandler> onMouseClick(final AbstractCanvasHandler canvasHandler, final String uuid, final MouseClickEvent event) {
        final Element<? extends Definition<?>> element = CanvasLayoutUtils.getElement(canvasHandler, uuid);

        final HTMLElement contextMenuElement = drdContextMenu.getElement();
        contextMenuElement.style.position = "absolute";
        contextMenuElement.style.left = event.getClientX() + "px";
        contextMenuElement.style.top = event.getClientY() + "px";
        DomGlobal.document.body.appendChild(contextMenuElement);

        drdContextMenu.show(self -> {
            self.setHeaderMenu("DRD ACTIONS", "fa fa-share-alt");
            self.addTextMenuItem("Create", true, () -> DomGlobal.console.log("A", element));
            self.addTextMenuItem("Add to", true, () -> DomGlobal.console.log("B", element));
            self.addTextMenuItem("Remove", true, () -> DomGlobal.console.log("C", element));
        });

        return this;
    }
}
