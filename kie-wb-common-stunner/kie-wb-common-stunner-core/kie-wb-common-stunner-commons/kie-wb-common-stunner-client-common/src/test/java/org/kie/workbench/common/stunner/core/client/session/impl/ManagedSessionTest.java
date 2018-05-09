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

package org.kie.workbench.common.stunner.core.client.session.impl;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.ManagedInstanceStub;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasElementListener;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasShapeListener;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistryLoader;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferences;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ManagedSessionTest {

    private static final String DEF_SET_ID = "ds1";

    @Mock
    private DefinitionUtils definitionUtils;
    @Mock
    private AbstractCanvas canvas;
    private ManagedInstance<AbstractCanvas> canvasInstances;
    @Mock
    private AbstractCanvasHandler canvasHandler;
    private ManagedInstance<AbstractCanvasHandler> canvasHandlerInstances;
    @Mock
    private CanvasControl<AbstractCanvas> canvasControl;
    private ManagedInstance<CanvasControl<AbstractCanvas>> canvasControlInstances;
    @Mock
    private CanvasControl<AbstractCanvasHandler> canvasHandlerControl;
    private ManagedInstance<CanvasControl<AbstractCanvasHandler>> canvasHandlerControlInstances;
    @Mock
    private StunnerPreferencesRegistryLoader preferencesRegistryLoader;
    @Mock
    private StunnerPreferences preferences;
    @Mock
    private Diagram diagram;
    @Mock
    private Metadata metadata;
    @Mock
    private Annotation qualifier;

    private ManagedSession tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        canvasInstances = spy(new ManagedInstanceStub<>(canvas));
        canvasHandlerInstances = spy(new ManagedInstanceStub<>(canvasHandler));
        canvasControlInstances = spy(new ManagedInstanceStub<>(canvasControl));
        canvasHandlerControlInstances = spy(new ManagedInstanceStub<>(canvasHandlerControl));
        when(metadata.getDefinitionSetId()).thenReturn(DEF_SET_ID);
        when(definitionUtils.getQualifier(eq(DEF_SET_ID))).thenReturn(qualifier);
        doAnswer(invocation -> {
            ((ParameterizedCommand) invocation.getArguments()[1]).execute(preferences);
            return null;
        }).when(preferencesRegistryLoader).load(anyString(),
                                                any(ParameterizedCommand.class),
                                                any(ParameterizedCommand.class));
        tested = new ManagedSession(definitionUtils,
                                    canvasInstances,
                                    canvasHandlerInstances,
                                    canvasControlInstances,
                                    canvasHandlerControlInstances,
                                    preferencesRegistryLoader);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInit() {
        Command callback = mock(Command.class);
        Consumer<CanvasControl<AbstractCanvas>> canvasControlConsumer = mock(Consumer.class);
        Consumer<CanvasControl<AbstractCanvasHandler>> canvasHandlerControlConsumer = mock(Consumer.class);
        tested.registerCanvasControl(SomeTestControl.class)
                .registerCanvasHandlerControl(SomeTestControl.class)
                .onCanvasControlRegistered(canvasControlConsumer)
                .onCanvasHandlerControlRegistered(canvasHandlerControlConsumer)
                .init(metadata,
                      callback);
        assertEquals(canvas, tested.getCanvas());
        assertEquals(canvasHandler, tested.getCanvasHandler());
        assertEquals(canvasControl, tested.getCanvasControls().get(0));
        assertEquals(canvasHandlerControl, tested.getCanvasHandlerControls().get(0));
        verify(canvasInstances, times(1)).select(eq(qualifier));
        verify(canvasHandlerInstances, times(1)).select(eq(qualifier));
        verify(canvasControlInstances, times(1)).select(eq(SomeTestControl.class),
                                                        eq(qualifier));
        verify(canvasHandlerControlInstances, times(1)).select(eq(SomeTestControl.class),
                                                               eq(qualifier));
        verify(canvasControlConsumer, times(1)).accept(eq(canvasControl));
        verify(canvasHandlerControlConsumer, times(1)).accept(eq(canvasHandlerControl));
        verify(preferencesRegistryLoader, times(1)).load(eq(DEF_SET_ID),
                                                         any(ParameterizedCommand.class),
                                                         any(ParameterizedCommand.class));
        verify(callback, times(1)).execute();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOpen() {
        tested.registerCanvasControl(SomeTestControl.class)
                .registerCanvasHandlerControl(SomeTestControl.class)
                .init(metadata,
                      mock(Command.class));
        tested.open();
        verify(canvas, times(1)).addRegistrationListener(any(CanvasShapeListener.class));
        verify(canvasHandler, times(1)).addRegistrationListener(any(CanvasElementListener.class));
        verify(canvasControl, times(1)).init(eq(canvas));
        verify(canvasHandlerControl, times(1)).init(eq(canvasHandler));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOpenTwice() {
        tested.registerCanvasControl(SomeTestControl.class)
                .registerCanvasHandlerControl(SomeTestControl.class)
                .init(metadata,
                      mock(Command.class));
        tested.open();
        tested.open();
        verify(canvas, times(1)).addRegistrationListener(any(CanvasShapeListener.class));
        verify(canvasHandler, times(1)).addRegistrationListener(any(CanvasElementListener.class));
        verify(canvasControl, times(1)).init(eq(canvas));
        verify(canvasHandlerControl, times(1)).init(eq(canvasHandler));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDestroy() {
        tested.registerCanvasControl(SomeTestControl.class)
                .registerCanvasHandlerControl(SomeTestControl.class)
                .init(metadata,
                      mock(Command.class));
        tested.open();
        tested.destroy();
        assertNull(tested.getCanvas());
        assertNull(tested.getCanvasHandler());
        assertTrue(tested.getCanvasControls().isEmpty());
        assertTrue(tested.getCanvasHandlerControls().isEmpty());
        verify(canvas, times(1)).removeRegistrationListener(any(CanvasShapeListener.class));
        verify(canvasHandler, times(1)).removeRegistrationListener(any(CanvasElementListener.class));
        verify(canvasHandler, times(1)).destroy();
        verify(canvasControl, times(1)).destroy();
        verify(canvasHandlerControl, times(1)).destroy();
        verify(canvasControlInstances, times(1)).destroyAll();
        verify(canvasHandlerControlInstances, times(1)).destroyAll();
        verify(canvasControlInstances, times(1)).destroyAll();
        verify(canvasHandlerControlInstances, times(1)).destroyAll();
    }

    private interface SomeTestControl extends CanvasControl {

    }
}
