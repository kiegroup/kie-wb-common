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

package org.kie.workbench.common.stunner.lienzo.toolbox.items.impl;

import java.util.function.BiConsumer;

import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.BoundingPoints;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.shared.HandlerRegistration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.lienzo.toolbox.GroupItem;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.AbstractDecoratorItem;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.TooltipItem;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class ItemImplTest {

    private final BoundingBox boundingBox = new BoundingBox(0d,
                                                            0d,
                                                            100d,
                                                            200d);

    @Mock
    private GroupItem groupItem;

    @Mock
    private AbstractFocusableGroupItem.FocusGroupExecutor focusGroupExecutor;

    @Mock
    private BiConsumer<Group, Command> hideExecutor;

    @Mock
    private Shape shape;

    @Mock
    private BoundingPoints boundingPoints;

    @Mock
    private HandlerRegistration mouseEnterHandlerRegistration;

    @Mock
    private HandlerRegistration mouseExitHandlerRegistration;

    private ItemImpl tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        when(shape.setListening(anyBoolean())).thenReturn(shape);
        when(shape.addNodeMouseEnterHandler(any(NodeMouseEnterHandler.class))).thenReturn(mouseEnterHandlerRegistration);
        when(shape.addNodeMouseExitHandler(any(NodeMouseExitHandler.class))).thenReturn(mouseExitHandlerRegistration);
        when(shape.getComputedBoundingPoints()).thenReturn(boundingPoints);
        when(shape.getBoundingBox()).thenReturn(boundingBox);
        when(boundingPoints.getBoundingBox()).thenReturn(boundingBox);
        doAnswer(invocationOnMock -> {
            ((Command) invocationOnMock.getArguments()[0]).execute();
            ((Command) invocationOnMock.getArguments()[1]).execute();
            return groupItem;
        }).when(groupItem).show(any(Command.class),
                                any(Command.class));
        doAnswer(invocationOnMock -> {
            ((Command) invocationOnMock.getArguments()[0]).execute();
            ((Command) invocationOnMock.getArguments()[1]).execute();
            return groupItem;
        }).when(groupItem).hide(any(Command.class),
                                any(Command.class));
        tested = new ItemImpl(groupItem,
                              shape)
                .setFocusDelay(0)
                .setUnFocusDelay(0)
                .useHideExecutor(hideExecutor)
                .useFocusGroupExecutor(focusGroupExecutor);
    }

    @Test
    public void testInit() {
        assertEquals(shape,
                     tested.getPrimitive());
        assertEquals(groupItem,
                     tested.getGroupItem());
        assertEquals(boundingBox,
                     tested.getBoundingBox().get());
        assertFalse(tested.isDecorated());
        assertFalse(tested.isVisible());
        assertFalse(tested.hasTooltip());
        verify(groupItem,
               times(1)).add(eq(shape));
        verify(shape,
               times(2)).setListening(eq(true));
        verify(shape,
               times(1)).addNodeMouseEnterHandler(any(NodeMouseEnterHandler.class));
        verify(shape,
               times(1)).addNodeMouseExitHandler(any(NodeMouseExitHandler.class));
    }

    @Test
    public void testShow() {
        final Command before = mock(Command.class);
        final Command after = mock(Command.class);
        tested.show(before,
                    after);
        verify(groupItem,
               times(1)).show(eq(before),
                              eq(after));
        verify(groupItem,
               never()).hide(any(Command.class),
                             any(Command.class));
        verify(before,
               times(1)).execute();
        verify(after,
               times(1)).execute();
    }

    @Test
    public void Command() {
        final Command before = mock(Command.class);
        final Command after = mock(Command.class);
        tested.hide(before,
                    after);
        verify(groupItem,
               times(1)).hide(any(Command.class),
                              eq(after));
        verify(groupItem,
               never()).show(any(Command.class),
                             any(Command.class));
        verify(before,
               times(1)).execute();
        verify(after,
               times(1)).execute();
        verify(focusGroupExecutor,
               times(1)).unFocus();
        verify(focusGroupExecutor,
               never()).focus();
        verify(focusGroupExecutor,
               never()).accept(any(Group.class),
                               any(Command.class));
    }

    @Test
    public void testFocus() {
        tested.focus();
        verify(focusGroupExecutor,
               times(1)).focus();
        verify(focusGroupExecutor,
               never()).unFocus();
        verify(focusGroupExecutor,
               never()).accept(any(Group.class),
                               any(Command.class));
    }

    @Test
    public void testUnFocus() {
        tested.unFocus();
        verify(focusGroupExecutor,
               times(1)).unFocus();
        verify(focusGroupExecutor,
               never()).focus();
        verify(focusGroupExecutor,
               never()).accept(any(Group.class),
                               any(Command.class));
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(groupItem,
               times(1)).destroy();
        verify(shape,
               times(1)).removeFromParent();
        verify(mouseEnterHandlerRegistration,
               times(1)).removeHandler();
        verify(mouseExitHandlerRegistration,
               times(1)).removeHandler();
    }

    @Test
    public void testFocusExecutorDoingFocus() {
        final AbstractDecoratorItem decorator = mock(AbstractDecoratorItem.class);
        final Group decPrimitive = mock(Group.class);
        when(decorator.asPrimitive()).thenReturn(decPrimitive);
        final TooltipItem<?> tooltip = mock(TooltipItem.class);
        tested =
                new ItemImpl(groupItem,
                             shape)
                        .setFocusDelay(0)
                        .setUnFocusDelay(0)
                        .decorate(decorator)
                        .tooltip(tooltip);
        final AbstractFocusableGroupItem<ItemImpl>.FocusGroupExecutor focusExecutor =
                spy(tested.getFocusGroupExecutor());
        doAnswer(invocationOnMock -> {
            ((Command) invocationOnMock.getArguments()[1]).execute();
            return null;
        }).when(focusExecutor).accept(any(Group.class),
                                      any(Command.class));
        focusExecutor.focus();
        verify(focusExecutor,
               times(1)).setAlpha(AbstractFocusableGroupItem.ALPHA_FOCUSED);
        verify(decorator,
               times(1)).show();
        verify(decPrimitive,
               times(1)).moveToBottom();
        verify(tooltip,
               times(1)).show();
    }

    @Test
    public void testFocusExecutorDoingUnFocus() {
        final AbstractDecoratorItem decorator = mock(AbstractDecoratorItem.class);
        final Group decPrimitive = mock(Group.class);
        when(decorator.asPrimitive()).thenReturn(decPrimitive);
        final TooltipItem<?> tooltip = mock(TooltipItem.class);
        tested =
                new ItemImpl(groupItem,
                             shape)
                        .setFocusDelay(0)
                        .setUnFocusDelay(0)
                        .decorate(decorator)
                        .tooltip(tooltip);
        final AbstractFocusableGroupItem<ItemImpl>.FocusGroupExecutor focusExecutor =
                spy(tested.getFocusGroupExecutor());
        doAnswer(invocationOnMock -> {
            ((Command) invocationOnMock.getArguments()[1]).execute();
            return null;
        }).when(focusExecutor).accept(any(Group.class),
                                      any(Command.class));
        focusExecutor.unFocus();
        verify(focusExecutor,
               times(1)).setAlpha(AbstractFocusableGroupItem.ALPHA_UNFOCUSED);
        verify(decorator,
               times(3)).hide();
        verify(tooltip,
               times(2)).hide();
    }
}
