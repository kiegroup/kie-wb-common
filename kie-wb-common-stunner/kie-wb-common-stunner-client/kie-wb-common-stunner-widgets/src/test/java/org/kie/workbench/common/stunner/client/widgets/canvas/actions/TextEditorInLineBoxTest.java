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

import com.google.gwt.event.dom.client.KeyCodes;
import org.junit.Assert;
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
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TextEditorInLineBoxTest {

    public static final String NAME = "name";
    public static final String MODIFIED_NAME = "modified_name";
    public static final String ID = "id";

    protected TextEditorInLineBox presenter;

    @Mock
    private DefinitionUtils definitionUtils;
    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    @Mock
    private TextEditorInLineBoxView view;
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

        doCallRealMethod().when(view).setOrientation(any(String.class));
        doCallRealMethod().when(view).setFontAlignment(any(String.class));
        doCallRealMethod().when(view).setFontPosition(any(String.class));
        doCallRealMethod().when(view).setFontSize(any(double.class));
        doCallRealMethod().when(view).onInputChange();
        doCallRealMethod().when(view).setHeight(any(double.class));
        doCallRealMethod().when(view).getDisplayOffsetY();
        doCallRealMethod().when(view).getDisplayOffsetX();
        doCallRealMethod().when(view).setFontX(any(double.class));
        doCallRealMethod().when(view).setFontY(any(double.class));
        doCallRealMethod().when(view).setFontSize(any(double.class));

        presenter = new TextEditorInLineBox(view);

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
        presenter.onKeyPress(12,
                             false,
                             MODIFIED_NAME);

        verifyNameNotSaved();

        presenter.onKeyPress(KeyCodes.KEY_ENTER,
                             false,
                             MODIFIED_NAME);

        verifyNameSaved();
    }

    @Test
    public void testSaveByPressingTab() {
        presenter.onKeyDown(KeyCodes.KEY_T,
                            MODIFIED_NAME);

        verifyNameNotSaved();

        presenter.onKeyDown(KeyCodes.KEY_TAB,
                            MODIFIED_NAME);

        verifyNameSaved();
    }

    @Test
    public void testFlush() {
        presenter.onChangeName(MODIFIED_NAME);

        presenter.flush();

        verifyNameFlushed();
    }

    @Test
    public void testPrepareNodeNameToShow() {
        Assert.assertEquals("AAAA<br>B<br>BB", presenter.prepareNodeNameToShow("AAAA\nB\nBB"));
        Assert.assertEquals("AAAA<br>B<br>BB&nbsp;&nbsp;ololo", presenter.prepareNodeNameToShow("AAAA\nB\nBB  ololo"));
    }

    @Test
    public void testSetOrientation() {
        presenter.setOrientation("VERTICAL");
        Assert.assertEquals("VERTICAL", view.orientation);
    }

    @Test
    public void testSetFontAlignment() {
        final String position = "position";
        presenter.setFontAlignment(position);
        Assert.assertEquals(position, view.fontAlignment);
    }

    @Test
    public void testSetFontPosition() {
        final String position = "position";
        presenter.setFontPosition(position);
        Assert.assertEquals(position, view.fontPosition);
    }

    @Test
    public void testSetFontX() {
        presenter.setFontX(11);
        Assert.assertEquals(11, view.fontX, 0.001);
    }

    @Test
    public void testSetFontY() {
        presenter.setFontY(11);
        Assert.assertEquals(11, view.fontY, 0.001);
    }

    @SuppressWarnings("unchecked")
    protected void verifyNameNotSaved() {
        assertEquals(MODIFIED_NAME,
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

        verify(view,
               never()).hide();
        verify(closeCallback,
               never()).execute();
    }

    protected void verifyNameSaved() {
        verifyNameFlushed();
        verify(view).hide();
        verify(closeCallback).execute();
    }

    @SuppressWarnings("unchecked")
    protected void verifyNameFlushed() {
        assertEquals(MODIFIED_NAME,
                     presenter.getNameValue());

        verify(definitionUtils).getNameIdentifier(objectDefinition);
        verify(canvasCommandFactory).updatePropertyValue(element,
                                                         ID,
                                                         MODIFIED_NAME);

        verify(commandProvider).getCommandManager();
        verify(canvasCommandManager).execute(any(),
                                             any());
    }
}
