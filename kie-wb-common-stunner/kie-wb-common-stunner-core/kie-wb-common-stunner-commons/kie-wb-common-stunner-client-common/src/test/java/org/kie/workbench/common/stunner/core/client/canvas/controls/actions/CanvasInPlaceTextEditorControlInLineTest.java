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

package org.kie.workbench.common.stunner.core.client.canvas.controls.actions;

import javax.enterprise.event.Event;

import com.google.gwt.user.client.ui.IsWidget;
import elemental2.dom.HTMLDivElement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.client.components.views.FloatingView;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextDoubleClickHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextEnterHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.event.TextExitHandler;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class CanvasInPlaceTextEditorControlInLineTest {

    private static final String UUID = "uuid";

    private static final double OFFSET_X = 00.0;

    private static final double OFFSET_Y = -85.0;

    private static final double X = 10.0;

    private static final double Y = 20.0;

    @Mock
    protected FloatingView<IsWidget> floatingView;

    @Mock
    protected TextEditorBox<AbstractCanvasHandler, Element> textEditorBox;
    @Mock
    protected TextEditorBox textEditBoxWidget;
    @Mock
    protected EditorSession session;
    @Mock
    protected KeyboardControl<AbstractCanvas, ClientSession> keyboardControl;
    @Mock
    protected AbstractCanvasHandler canvasHandler;
    @Mock
    protected Canvas canvas;
    @Mock
    protected AbstractCanvas abstractCanvas;
    @Mock
    protected AbstractCanvas.CanvasView abstractCanvasView;
    @Mock
    protected Element element;
    @Mock
    protected Shape shape;
    @Mock
    protected ShapeView shapeView;
    @Captor
    protected ArgumentCaptor<TextExitHandler> textExitHandlerCaptor;

    protected Bounds shapeViewBounds = Bounds.create();
    @Mock
    protected AbstractCanvasInPlaceTextEditorControlTest.TestShapeView testShapeView;
    @Captor
    protected ArgumentCaptor<KeyboardControl.KeyShortcutCallback> keyShortcutCallbackCaptor;
    @Captor
    protected ArgumentCaptor<CanvasSelectionEvent> canvasSelectionEventCaptor;
    @Captor
    protected ArgumentCaptor<Command> commandCaptor;
    @Captor
    protected ArgumentCaptor<TextDoubleClickHandler> textDoubleClickHandlerCaptor;
    @Captor
    protected ArgumentCaptor<TextEnterHandler> textEnterHandlerCaptor;
    @Mock
    protected RequiresCommandManager.CommandManagerProvider<AbstractCanvasHandler> commandManagerProvider;

    @Mock
    Event<CanvasSelectionEvent> canvasSelectionEvent;
    @Mock
    private HTMLDivElement nameField;
    private CanvasInPlaceTextEditorControlInLine control;

    @Before
    public void setUp() {
        initMocks(this);

        when(session.getKeyboardControl()).thenReturn(keyboardControl);
        when(floatingView.hide()).thenReturn(floatingView);
        when(floatingView.setHideCallback(any(Command.class))).thenReturn(floatingView);
        when(floatingView.setTimeOut(anyInt())).thenReturn(floatingView);
        when(floatingView.setX(anyDouble())).thenReturn(floatingView);
        when(floatingView.setY(anyDouble())).thenReturn(floatingView);
        when(floatingView.setOffsetX(anyDouble())).thenReturn(floatingView);
        when(floatingView.setOffsetY(anyDouble())).thenReturn(floatingView);
        when(textEditorBox.getDisplayOffsetX()).thenReturn(OFFSET_X);
        when(textEditorBox.getDisplayOffsetY()).thenReturn(OFFSET_Y);
        when(element.getUUID()).thenReturn(UUID);
        when(element.getContent()).thenReturn(shapeView);
        //when(shapeView.getBounds()).thenReturn(shapeViewBounds);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getAbstractCanvas()).thenReturn(abstractCanvas);
        when(abstractCanvas.getView()).thenReturn(abstractCanvasView);
        when(canvas.getShape(eq(UUID))).thenReturn(shape);
        when(shape.getUUID()).thenReturn(UUID);
        when(shape.getShapeView()).thenReturn(testShapeView);

        control = spy(new CanvasInPlaceTextEditorControlInLine(floatingView, textEditBoxWidget, canvasSelectionEvent));
    }

    @Test
    public void testBind() {
        control.bind(session);
        verify(keyboardControl).addKeyShortcutCallback(any(KeyboardControl.KeyShortcutCallback.class));
    }

    @Test
    public void testBindKeyControlHandledKey() {
        control.bind(session);

        verify(keyboardControl).addKeyShortcutCallback(keyShortcutCallbackCaptor.capture());

        final KeyboardControl.KeyShortcutCallback keyShortcutCallback = keyShortcutCallbackCaptor.getValue();
        keyShortcutCallback.onKeyShortcut(KeyboardEvent.Key.ESC);

        verify(control).onKeyDownEvent(eq(KeyboardEvent.Key.ESC));

        verify(control).hide();
    }

    @Test
    public void testBindKeyControlUnhandledKey() {
        control.bind(session);

        verify(keyboardControl).addKeyShortcutCallback(keyShortcutCallbackCaptor.capture());

        final KeyboardControl.KeyShortcutCallback keyShortcutCallback = keyShortcutCallbackCaptor.getValue();
        keyShortcutCallback.onKeyShortcut(KeyboardEvent.Key.ARROW_DOWN);

        verify(control).onKeyDownEvent(eq(KeyboardEvent.Key.ARROW_DOWN));

        verify(control, never()).hide();
    }

    @Test
    public void testOnCanvasClearSelectionEvent() {
        control.onCanvasClearSelectionEvent(new CanvasClearSelectionEvent(canvasHandler));

        verify(control).flush();
    }

    @Test
    public void testOnCanvasShapeRemovedEvent() {
        control.onCanvasShapeRemovedEvent(new CanvasShapeRemovedEvent(canvas, shape));

        verify(control).flush();
    }

    @Test
    public void testOnCanvasFocusedEvent() {
        control.onCanvasFocusedEvent(new CanvasFocusedEvent(canvas));

        verify(control).flush();
    }

    @Test
    public void testOnCanvasSelectionEvent() {
        control.onCanvasSelectionEvent(new CanvasSelectionEvent(canvasHandler, UUID));

        verify(control).flush();
    }

    @Test
    public void testGetCanvasSelectionEvent() {
        Assert.assertEquals(canvasSelectionEvent , control.getCanvasSelectionEvent());
    }

    @Test
    public void testGetFloatingView() {
        Assert.assertEquals(floatingView , control.getFloatingView());
    }

    private void assertShow() {
        verify(testShapeView).setFillAlpha(eq(AbstractCanvasInPlaceTextEditorControl.SHAPE_EDIT_ALPHA));
        verify(testShapeView).setTitleAlpha(eq(AbstractCanvasInPlaceTextEditorControl.SHAPE_EDIT_ALPHA));

        verify(textEditorBox).show(eq(element));

        verify(floatingView).setX(eq(X));
        verify(floatingView).setY(eq(Y));
        verify(floatingView).setOffsetX(eq(-OFFSET_X));
        verify(floatingView).setOffsetY(eq(-OFFSET_Y));
        verify(floatingView).show();
    }

    private void assertHide(final int t) {
        verify(testShapeView, times(t)).setFillAlpha(eq(AbstractCanvasInPlaceTextEditorControl.SHAPE_NOT_EDIT_ALPHA));
        verify(testShapeView, times(t)).setTitleAlpha(eq(AbstractCanvasInPlaceTextEditorControl.SHAPE_NOT_EDIT_ALPHA));

        verify(textEditorBox, times(t)).hide();
        verify(floatingView, times(t)).hide();
    }
}
