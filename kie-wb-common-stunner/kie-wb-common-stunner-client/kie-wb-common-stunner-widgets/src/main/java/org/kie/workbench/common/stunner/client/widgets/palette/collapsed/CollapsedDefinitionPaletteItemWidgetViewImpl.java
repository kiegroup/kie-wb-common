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

package org.kie.workbench.common.stunner.client.widgets.palette.collapsed;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.MouseDownEvent;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.DOMGlyphRenderers;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

@Templated
@Dependent
public class CollapsedDefinitionPaletteItemWidgetViewImpl implements CollapsedDefinitionPaletteItemWidgetView,
                                                                     IsElement {

    @DataField
    private Anchor itemAnchor;

    @DataField
    private Span icon;

    private DOMGlyphRenderers domGlyphRenderers;

    private Presenter presenter;

    public CollapsedDefinitionPaletteItemWidgetViewImpl() {
        //CDI proxy
    }

    @Inject
    public CollapsedDefinitionPaletteItemWidgetViewImpl(final Anchor itemAnchor,
                                                        final Span icon,
                                                        final DOMGlyphRenderers domGlyphRenderers) {
        this.itemAnchor = itemAnchor;
        this.icon = icon;
        this.domGlyphRenderers = domGlyphRenderers;
    }

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void render(final Glyph glyph,
                       final double width,
                       final double height) {
        final org.jboss.errai.common.client.api.IsElement glyphElement = domGlyphRenderers.render(glyph, width, height);
        icon.appendChild(glyphElement.getElement());

        final String tooltip = presenter.getItem().getTooltip();
        if (!isEmpty(tooltip)) {
            itemAnchor.setTitle(tooltip);
        } else {
            itemAnchor.setTitle("");
        }
    }

    @EventHandler("itemAnchor")
    public void onMouseDown(final MouseDownEvent mouseDownEvent) {
        presenter.onMouseDown(mouseDownEvent.getClientX(),
                              mouseDownEvent.getClientY(),
                              mouseDownEvent.getX(),
                              mouseDownEvent.getY());
    }

    @PreDestroy
    public void destroy() {
        DOMUtil.removeAllChildren(itemAnchor);
        DOMUtil.removeAllChildren(icon);
        presenter = null;
    }

    private static boolean isEmpty(final String s) {
        return null == s || s.trim().length() == 0;
    }
}
