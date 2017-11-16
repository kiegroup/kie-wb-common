/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.Event;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.DefaultTextPropertyProviderImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TextEditorBoxImplTest {

    public static final String NAME = "name";
    public static final String MODIFIED_NAME = "modified_name";
    public static final String MODIFIED_NAME_WITH_NEWLINES = "modified\nname\nwith some\nnew lines";
    public static final String ID = "id";
    public static final int ENTER_KEY = 13;
    public static final int NOT_ENTER_KEY = 12;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    @Mock
    private TextEditorBoxView view;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Command closeCallback;

    @Mock
    private RequiresCommandManager.CommandManagerProvider<AbstractCanvasHandler> commandProvider;

    @Mock
    private TextPropertyProviderFactory textPropertyProviderFactory;

    @Mock
    private CanvasCommandManager canvasCommandManager;

    @Mock
    private Element element;

    @Mock
    private Definition definition;

    @Mock
    private TextPropertyProvider textPropertyProvider;

    private Object objectDefinition = new Object();

    private TextEditorBoxImpl presenter;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        this.textPropertyProvider = new DefaultTextPropertyProviderImpl(definitionUtils,
                                                                        canvasCommandFactory);

        when(element.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(objectDefinition);
        when(definitionUtils.getName(objectDefinition)).thenReturn(NAME);
        when(definitionUtils.getNameIdentifier(objectDefinition)).thenReturn(ID);
        when(commandProvider.getCommandManager()).thenReturn(canvasCommandManager);
        when(canvasHandler.getTextPropertyProviderFactory()).thenReturn(textPropertyProviderFactory);
        when(textPropertyProviderFactory.getProvider(any(Element.class))).thenReturn(textPropertyProvider);

        presenter = new TextEditorBoxImpl(view);

        presenter.setup();

        verify(view).init(presenter);

        presenter.initialize(canvasHandler,
                             closeCallback);

        presenter.setCommandManagerProvider(commandProvider);

        presenter.getElement();

        verify(view).getElement();

        presenter.show(element);

        verify(view).show(NAME);
    }

    @Test
    public void testSaveByPressingEnter() {
        
        presenter.onKeyPress(getEventFromKeyCode(NOT_ENTER_KEY),
                             MODIFIED_NAME);

        verifyNameNotSaved(MODIFIED_NAME);

        presenter.onKeyPress(getEventFromKeyCode(ENTER_KEY),
                             MODIFIED_NAME);

        verifyNameSaved(MODIFIED_NAME);
    }

    @Test
    public void testSaveByPressingButton() {
        presenter.onChangeName(MODIFIED_NAME);

        verifyNameNotSaved(MODIFIED_NAME);

        presenter.onSave();

        verifyNameSaved(MODIFIED_NAME);
    }

    @Test
    public void testSaveWithNewlinesByPressingEnter() {
        presenter.onKeyPress(getEventFromKeyCode(NOT_ENTER_KEY),
                MODIFIED_NAME_WITH_NEWLINES);

        verifyNameNotSaved(MODIFIED_NAME_WITH_NEWLINES);

        presenter.onKeyPress(getEventFromKeyCode(ENTER_KEY),
                MODIFIED_NAME_WITH_NEWLINES);

        verifyNameSaved(MODIFIED_NAME_WITH_NEWLINES);
    }
    
    @Test
    public void testNoSaveWithNewlinesByPressingEnterWithCtrl() {
        presenter.onKeyPress(getEventFromKeyCode(ENTER_KEY, ModifierKey.CTRL),
                MODIFIED_NAME_WITH_NEWLINES);

        verifyNameNotSaved(MODIFIED_NAME_WITH_NEWLINES);
        
        presenter.onKeyPress(getEventFromKeyCode(ENTER_KEY, ModifierKey.SHIFT),
                MODIFIED_NAME_WITH_NEWLINES);

        verifyNameNotSaved(MODIFIED_NAME_WITH_NEWLINES);

        presenter.onKeyPress(getEventFromKeyCode(ENTER_KEY),
                MODIFIED_NAME_WITH_NEWLINES);

        verifyNameSaved(MODIFIED_NAME_WITH_NEWLINES);
    }

    @Test
    public void testSaveWithNewlinesByPressingButton() {
        presenter.onChangeName(MODIFIED_NAME_WITH_NEWLINES);

        verifyNameNotSaved(MODIFIED_NAME_WITH_NEWLINES);

        presenter.onSave();

        verifyNameSaved(MODIFIED_NAME_WITH_NEWLINES);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCloseButton() {
        presenter.onChangeName(MODIFIED_NAME);

        assertEquals(MODIFIED_NAME,
                     presenter.getNameValue());

        presenter.onClose();

        assertEquals(null,
                     presenter.getNameValue());

        verify(definitionUtils,
               never()).getNameIdentifier(objectDefinition);
        verify(canvasCommandFactory,
               never()).updatePropertyValue(element,
                                            ID,
                                            MODIFIED_NAME);

        verify(commandProvider,
               never()).getCommandManager();
        verify(canvasCommandManager,
               never()).execute(any(),
                                any());

        verify(view).hide();
        verify(closeCallback).execute();
    }

    @SuppressWarnings("unchecked")
    protected void verifyNameNotSaved(String name) {
        assertEquals(name,
                     presenter.getNameValue());

        verify(definitionUtils,
               never()).getNameIdentifier(objectDefinition);
        verify(canvasCommandFactory,
               never()).updatePropertyValue(element,
                                            ID,
                name);

        verify(commandProvider,
               never()).getCommandManager();
        verify(canvasCommandManager,
               never()).execute(any(),
                                any());

        verify(view,
               never()).hide();
        verify(closeCallback,
               never()).execute();
    }

    @SuppressWarnings("unchecked")
    protected void verifyNameSaved(String name) {
        assertEquals(name,
                     presenter.getNameValue());

        verify(definitionUtils).getNameIdentifier(objectDefinition);
        verify(canvasCommandFactory).updatePropertyValue(element,
                                                         ID,
                name);

        verify(commandProvider).getCommandManager();
        verify(canvasCommandManager).execute(any(),
                                             any());
        verify(view).hide();
        verify(closeCallback).execute();
    }
    
    private Event getEventFromKeyCode(int keyCode, ModifierKey... modifiers) {
        List<ModifierKey> modifiersList = Arrays.asList(modifiers);
        Event e = mock(Event.class);
        when(e.getKeyCode()).thenReturn(keyCode);
        when(e.getCtrlKey()).thenReturn(modifiersList.contains(ModifierKey.CTRL));
        when(e.getShiftKey()).thenReturn(modifiersList.contains(ModifierKey.SHIFT));
        when(e.getAltKey()).thenReturn(modifiersList.contains(ModifierKey.ALT));
        when(e.getMetaKey()).thenReturn(modifiersList.contains(ModifierKey.META));
        return e;
    }
    
    private static enum ModifierKey {
        CTRL, SHIFT, ALT, META;
    }
}

