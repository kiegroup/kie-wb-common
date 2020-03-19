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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import java.util.Optional;
import java.util.function.Consumer;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.KeyboardEvent;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.EventListener;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.ValuePopoverViewImpl.ENTER_KEY;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.ValuePopoverViewImpl.ESC_KEY;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ValuePopoverViewImplTest {

    @Mock
    private Div popoverElement;

    @Mock
    private Div popoverContentElement;

    @Mock
    private JQueryProducer.JQuery<Popover> jQueryPopover;

    @Mock
    private HTMLInputElement valueEditor;

    @Mock
    private HTMLElement label;

    @Mock
    private TranslationService translationService;

    private ValuePopoverViewImpl popover;

    @Before
    public void setup() {
        popover = spy(new ValuePopoverViewImpl(popoverElement,
                                               popoverContentElement,
                                               jQueryPopover,
                                               valueEditor,
                                               label,
                                               translationService));
    }

    @Test
    public void testSetup() {

        final String value = "value";
        when(translationService.getTranslation(DMNEditorConstants.ValuePopover_ValueLabel)).thenReturn(value);

        popover.setup();

        assertEquals(value, label.textContent);
    }

    @Test
    public void testSetAndGetOnCloserByKeyboardCallback() {

        final Consumer callback = mock(Consumer.class);

        popover.setOnClosedByKeyboardCallback(callback);
        final Optional<Consumer> actual = popover.getClosedByKeyboardCallback();

        assertEquals(callback, actual.get());
    }

    @Test
    public void testOnClosedByKeyboard() {

        final Consumer theCallback = mock(Consumer.class);
        final Optional<Consumer> callback = Optional.of(theCallback);

        doReturn(callback).when(popover).getClosedByKeyboardCallback();

        popover.onClosedByKeyboard();

        verify(theCallback).accept(popover);
    }

    @Test
    public void testInit() {

        final ValuePopoverView.Presenter presenter = mock(ValuePopoverView.Presenter.class);
        doNothing().when(popover).setKeyDownListeners();

        popover.init(presenter);

        verify(popover).setKeyDownListeners();
    }

    @Test
    public void testSetKeyDownListeners() {

        final EventListener listener = mock(EventListener.class);
        doReturn(listener).when(popover).getKeyDownEventListener();

        popover.setKeyDownListeners();

        verify(popoverElement).addEventListener(BrowserEvents.KEYDOWN,
                                                listener,
                                                false);
    }

    @Test
    public void testKeyDownEventListenerWhenIsEnterKeyPressed() {

        final KeyboardEvent keyboardEvent = mock(KeyboardEvent.class);

        doReturn(true).when(popover).isEnterKeyPressed(keyboardEvent);

        popover.keyDownEventListener(keyboardEvent);

        verify(popover).hide(true);
        verify(keyboardEvent).stopPropagation();
        verify(popover).onClosedByKeyboard();
        verify(popover, never()).reset();
    }

    @Test
    public void testKeyDownEventListenerWhenIsEscKeyPressed() {

        final KeyboardEvent keyboardEvent = mock(KeyboardEvent.class);

        doReturn(false).when(popover).isEnterKeyPressed(keyboardEvent);
        doReturn(true).when(popover).isEscapeKeyPressed(keyboardEvent);

        popover.keyDownEventListener(keyboardEvent);

        verify(popover, never()).hide(true);
        verify(popover).hide(false);
        verify(popover).onClosedByKeyboard();
        verify(popover).reset();
    }

    @Test
    public void testKeyDownEventListenerWhenIsUnknownKeyPressed() {

        final KeyboardEvent keyboardEvent = mock(KeyboardEvent.class);

        doReturn(false).when(popover).isEnterKeyPressed(keyboardEvent);
        doReturn(false).when(popover).isEscapeKeyPressed(keyboardEvent);

        popover.keyDownEventListener(keyboardEvent);

        verify(popover, never()).hide(true);
        verify(popover, never()).hide(false);
        verify(keyboardEvent, never()).stopPropagation();
        verify(popover, never()).onClosedByKeyboard();
        verify(popover, never()).reset();
    }

    @Test
    public void testKeyDownEventListenerWhenIsNotKeyboardEvent() {

        final Object keyboardEvent = mock(Object.class);

        popover.keyDownEventListener(keyboardEvent);

        verify(popover, never()).isEnterKeyPressed(any());
        verify(popover, never()).isEscapeKeyPressed(any());
    }

    @Test
    public void testDefaultHide() {

        doNothing().when(popover).hide(true);
        popover.hide();

        verify(popover).hide(true);
    }

    @Test
    public void testHideSaveChanges() {

        final String value = "value";
        final ValuePopoverView.Presenter presenter = mock(ValuePopoverView.Presenter.class);

        doNothing().when(popover).superHide();
        doReturn(presenter).when(popover).getPresenter();
        doReturn(true).when(popover).isVisible();

        valueEditor.value = value;

        popover.hide(true);

        verify(presenter).setValue(value);
        assertEquals(value, popover.getValue());
        verify(popover).superHide();
    }

    @Test
    public void testHideDiscardChanges() {

        final String value = "value";
        final String previousValue = "previous";
        final ValuePopoverView.Presenter presenter = mock(ValuePopoverView.Presenter.class);

        doNothing().when(popover).superHide();
        doReturn(presenter).when(popover).getPresenter();
        doReturn(true).when(popover).isVisible();

        doReturn(previousValue).when(popover).getPreviousValue();

        valueEditor.value = value;

        popover.hide(false);

        verify(presenter, never()).setValue(value);
        assertNotEquals(value, popover.getValue());
        assertEquals(previousValue, popover.getValue());
        verify(popover).superHide();
    }

    @Test
    public void testHideWhenIsNotVisible() {

        doReturn(false).when(popover).isVisible();

        verify(popover, never()).superHide();
    }

    @Test
    public void testSetValue() {

        final String value = "value";
        popover.setValue(value);

        assertEquals(value, valueEditor.value);
        assertEquals(value, popover.getValue());
        assertEquals(value, popover.getPreviousValue());
    }

    @Test
    public void testIsEsc() {

        final KeyboardEvent keyDownEvent = mock(KeyboardEvent.class);
        keyDownEvent.key = ESC_KEY;

        final boolean actual = popover.isEscapeKeyPressed(keyDownEvent);

        assertTrue(actual);
    }

    @Test
    public void testIsEnter() {

        final KeyboardEvent keyDownEvent = mock(KeyboardEvent.class);
        keyDownEvent.key = ENTER_KEY;

        final boolean actual = popover.isEnterKeyPressed(keyDownEvent);

        assertTrue(actual);
    }

    @Test
    public void testIsNotEsc() {

        final KeyboardEvent keyDownEvent = mock(KeyboardEvent.class);
        keyDownEvent.key = "A";

        final boolean actual = popover.isEscapeKeyPressed(keyDownEvent);

        assertFalse(actual);
    }

    @Test
    public void testIsNotEnter() {

        final KeyboardEvent keyDownEvent = mock(KeyboardEvent.class);
        keyDownEvent.key = "A";

        final boolean actual = popover.isEnterKeyPressed(keyDownEvent);

        assertFalse(actual);
    }
}