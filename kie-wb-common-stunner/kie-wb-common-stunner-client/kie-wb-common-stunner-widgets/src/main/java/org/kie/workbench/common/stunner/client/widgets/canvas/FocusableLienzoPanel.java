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

package org.kie.workbench.common.stunner.client.widgets.canvas;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.Layer;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;
import org.kie.workbench.common.stunner.core.client.canvas.event.mouse.CanvasMouseDownEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.mouse.CanvasMouseUpEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyDownEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyPressEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyUpEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;

// TODO: Avoid code duplication with ScrollableLienzoPanel

@Dependent
@Typed(FocusableLienzoPanel.class)
public class FocusableLienzoPanel
        implements LienzoPanel {

    private static final int PADDING = 15;

    private final Event<KeyPressEvent> keyPressEvent;
    private final Event<KeyDownEvent> keyDownEvent;
    private final Event<KeyUpEvent> keyUpEvent;
    private final Event<CanvasMouseDownEvent> mouseDownEvent;
    private final Event<CanvasMouseUpEvent> mouseUpEvent;

    private FocusableLienzoPanelWidgetView view;

    @Inject
    public FocusableLienzoPanel(final Event<KeyPressEvent> keyPressEvent,
                                final Event<KeyDownEvent> keyDownEvent,
                                final Event<KeyUpEvent> keyUpEvent,
                                final Event<CanvasMouseDownEvent> mouseDownEvent,
                                final Event<CanvasMouseUpEvent> mouseUpEvent) {
        this.keyPressEvent = keyPressEvent;
        this.keyDownEvent = keyDownEvent;
        this.keyUpEvent = keyUpEvent;
        this.mouseDownEvent = mouseDownEvent;
        this.mouseUpEvent = mouseUpEvent;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public LienzoPanel show(final LienzoLayer layer) {
        return doShow(layer,
                      new FocusableLienzoPanelWidgetView());
    }

    @Override
    public LienzoPanel show(final LienzoLayer layer,
                            final int width,
                            final int height) {
        return doShow(layer,
                      new FocusableLienzoPanelWidgetView(width + PADDING,
                                                         height + PADDING));
    }

    private LienzoPanel doShow(final LienzoLayer layer,
                               final FocusableLienzoPanelWidgetView view) {
        view.setPresenter(this);
        view.add(layer.getLienzoLayer());
        return this;
    }

    @Override
    public LienzoPanel focus() {
        view.setFocus(true);
        return this;
    }

    @Override
    public int getWidth() {
        return view.getWidth();
    }

    @Override
    public int getHeight() {
        return view.getHeight();
    }

    @Override
    public LienzoPanel setPixelSize(final int wide,
                                    final int high) {
        view.setPixelSize(wide,
                          high);
        return this;
    }

    @Override
    public void setBackgroundLayer(final Layer layer) {
        view.setBackgroundLayer(layer);
    }

    public void destroy() {
        view.destroy();
        view = null;
    }

    void onMouseDown() {
        mouseDownEvent.fire(new CanvasMouseDownEvent());
    }

    void onMouseUp() {
        mouseUpEvent.fire(new CanvasMouseUpEvent());
    }

    void onKeyPress(final int unicodeChar) {
        final KeyboardEvent.Key key = getKey(unicodeChar);
        if (null != key) {
            keyPressEvent.fire(new KeyPressEvent(key));
        }
    }

    void onKeyDown(final int unicodeChar) {
        final KeyboardEvent.Key key = getKey(unicodeChar);
        if (null != key) {
            keyDownEvent.fire(new KeyDownEvent(key));
        }
    }

    void onKeyUp(final int unicodeChar) {
        final KeyboardEvent.Key key = getKey(unicodeChar);
        if (null != key) {
            keyUpEvent.fire(new KeyUpEvent(key));
        }
    }

    private KeyboardEvent.Key getKey(final int unicodeChar) {
        final KeyboardEvent.Key[] keys = KeyboardEvent.Key.values();
        for (final KeyboardEvent.Key key : keys) {
            final int c = key.getUnicharCode();
            if (c == unicodeChar) {
                return key;
            }
        }
        return null;
    }
}
