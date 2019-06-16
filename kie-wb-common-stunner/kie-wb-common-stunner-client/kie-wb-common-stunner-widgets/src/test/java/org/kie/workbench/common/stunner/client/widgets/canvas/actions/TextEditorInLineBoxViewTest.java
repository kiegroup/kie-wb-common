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

package org.kie.workbench.common.stunner.client.widgets.canvas.actions;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.CSSProperties;
import elemental2.dom.HTMLDivElement;
import org.jboss.errai.common.client.dom.CSSStyleDeclaration;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TextEditorInLineBoxViewTest {

    private static final String NAME = "UserTask";

    @Mock
    private TextEditorInLineBoxView.Presenter presenter;

    @Mock
    private Event event;

    @Mock
    private TranslationService translationService;

    @Mock
    private ClickEvent clickEvent;

    @Mock
    private Div editNameBox;

    @Mock
    private HTMLDivElement nameField;

    @Mock
    private HTMLElement htmlElement;

    @Mock
    private CSSStyleDeclaration style;

    @Mock
    private HTMLElement element;

    @Mock
    private Command showCommand;

    @Mock
    private Command hideCommand;

    @Mock
    private HTMLElement closeButton;

    @Mock
    private HTMLElement saveButton;

    private TextEditorInLineBoxView tested;

    @Mock
    private CSSProperties.WidthUnionType widthUnionType;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        this.tested = spy(new TextEditorInLineBoxView(translationService, editNameBox, nameField, showCommand, hideCommand, closeButton, saveButton));
        this.tested.init(presenter);
        nameField.innerHTML = NAME;

        doCallRealMethod().when(tested).getNameFieldValue();
        doCallRealMethod().when(tested).show(anyString());
        doCallRealMethod().when(tested).editNameBoxEsc(any(Event.class));
        doCallRealMethod().when(tested).onChangeName(any(Event.class));

        doCallRealMethod().when(tested).setWidth(any(double.class));
        doCallRealMethod().when(tested).setHeight(any(double.class));

        doCallRealMethod().when(tested).setOrientation(any(String.class));
        doCallRealMethod().when(tested).setFontAlignment(any(String.class));
        doCallRealMethod().when(tested).setFontPosition(any(String.class));
        doCallRealMethod().when(tested).setFontSize(any(double.class));
        doCallRealMethod().when(tested).onInputChange();
        doCallRealMethod().when(tested).setHeight(any(double.class));
        doCallRealMethod().when(tested).getDisplayOffsetY();
        doCallRealMethod().when(tested).getDisplayOffsetX();
        doCallRealMethod().when(tested).setFontX(any(double.class));
        doCallRealMethod().when(tested).setFontY(any(double.class));
        doCallRealMethod().when(tested).setWidthUnionType(any(elemental2.dom.CSSStyleDeclaration.class), any(double.class));

        when(nameField.getAttribute(anyString())).thenReturn("placeHolder");
        when(editNameBox.getStyle()).thenReturn(style);
        when(style.getPropertyValue("width")).thenReturn("300");
        doNothing().when(tested).setWidthUnionType(any(), any(double.class));
        doNothing().when(tested).setMaxWidthUnionType(any(), any(double.class));
        doNothing().when(tested).onResize();
        doNothing().when(tested).fireTitleChangeEvent();

        doAnswer(i -> {
            ((Scheduler.ScheduledCommand) i.getArguments()[0]).execute();
            return null;
        }).when(tested).scheduleDeferredCommand(any(Scheduler.ScheduledCommand.class));
    }

    @Test
    public void testInitialize() {
        tested.initialize();
        Assert.assertEquals(nameField.getAttribute("placeHolder"), "placeHolder");
    }

    @Test
    public void testOnKeyDownEscEvent() {
        when(event.getTypeInt()).thenReturn(Event.ONKEYDOWN);
        when(event.getKeyCode()).thenReturn(KeyCodes.KEY_ESCAPE);
        tested.editNameBoxEsc(event);
        verify(presenter,
               times(1)).onClose();
    }

    @Test
    public void testOnChangeNameChangeEvent() {
        when(event.getTypeInt()).thenReturn(Event.ONCHANGE);
        tested.onChangeName(event);
        verify(presenter, times(1)).onChangeName(eq(NAME));
    }

    @Test
    public void testOnChangeNameKeyPressEvent() {
        when(event.getTypeInt()).thenReturn(Event.ONKEYPRESS);
        when(event.getKeyCode()).thenReturn(KeyCodes.KEY_A);
        tested.onChangeName(event);
        verify(presenter, times(1)).onKeyPress(eq(KeyCodes.KEY_A), eq(false), eq(NAME));
    }

    @Test
    public void testOnChangeNameKeyDownEvent() {
        when(event.getTypeInt()).thenReturn(Event.ONKEYDOWN);
        when(event.getKeyCode()).thenReturn(KeyCodes.KEY_A);
        tested.onChangeName(event);
        verify(presenter, times(1)).onKeyDown(eq(KeyCodes.KEY_A), eq(NAME));
    }

    @Test
    public void testShow() {
        tested.show(NAME);
        verify(tested,
               times(1)).setVisible();
        verify(tested,
               times(1)).setVisible();
        verify(showCommand, times(1)).execute();
    }

    @Test
    public void testShowTags() {
        tested.show("AAA\nBBBB\nZZZZ");
        Assert.assertEquals("AAA\nBBBB\nZZZZ", nameField.innerHTML);
        tested.show("AAA\nBBBB\nZZZZ  OLOLO");
        Assert.assertEquals("AAA\nBBBB\nZZZZ  OLOLO", nameField.innerHTML);
    }

    @Test
    public void setWidth() {
        tested.setOrientation("HORIZONTAL");
        tested.setFontPosition("INSIDE");
        tested.setFontAlignment("TOP");
        tested.setWidth(300);
        verify(tested, times(1)).setWidthUnionType(any(), eq(300.0));
        verify(tested, times(1)).setMaxWidthUnionType(any(), eq(300.0));
    }

    @Test
    public void setHeight() {
        tested.setFontPosition("OUTSIDE");
        tested.setFontAlignment("TOP");
        tested.setOrientation("HORIZONTAL");
        tested.setHeight(300);

        Assert.assertEquals(20.0, tested.defaultHeight, 0.0001);

        verify(tested, times(1)).setStyleProperty(any(), eq("height"), eq(20.0));
        verify(tested, times(1)).setStyleProperty(any(), eq("min-height"), eq(20.0));

        tested.setFontPosition("INSIDE");
        tested.setFontAlignment("HORIZONTAL");
        tested.setHeight(300);
        verify(tested, times(1)).setStyleProperty(any(), eq("height"), eq(20.0));
        tested.setOrientation("VERTICAL");
        tested.setHeight(300);
        verify(tested, times(2)).setStyleProperty(any(), eq("min-height"), eq(300.0));
    }

    @Test
    public void testSetFontAlignment() {
        final String position = "position";
        tested.setFontAlignment(position);
        Assert.assertEquals(position, tested.fontAlignment);
    }

    @Test
    public void testSetFontPosition() {
        final String position = "position";
        tested.setFontPosition(position);
        Assert.assertEquals(position, tested.fontPosition);
    }

    @Test
    public void testOnInputChange() {
        tested.onInputChange();
        //tested.onNodeTitleChangeEvent = onNodeTitleChangeEvent;
        verify(tested, times(1)).onResize();
        verify(tested, times(1)).fireTitleChangeEvent();
    }

    @Test
    public void testGetDisplayOffsetY() {
        tested.setFontPosition("INSIDE");
        Assert.assertEquals(0, tested.getDisplayOffsetX(), 0.0001);
    }

    @Test
    public void testGetDisplayOffsetX() {
        tested.setFontPosition("INSIDE");
        Assert.assertEquals(0, tested.getDisplayOffsetX(), 0.0001);
        tested.setFontPosition("OUTSIDE");
        nameField.innerHTML = "";
        Assert.assertEquals(-30.0, tested.getDisplayOffsetX(), 0.0001);
    }

    @Test
    public void testSetFontX() {
        tested.setFontY(30.00);
        Assert.assertEquals(30, tested.fontY, 0.1);
    }

    @Test
    public void testSetFontY() {
        tested.setFontX(30.00);
        Assert.assertEquals(30, tested.fontX, 0.1);
    }
}
