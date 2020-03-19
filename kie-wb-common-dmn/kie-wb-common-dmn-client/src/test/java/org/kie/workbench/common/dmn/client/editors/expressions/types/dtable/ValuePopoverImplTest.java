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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DecisionTableEditor_SelectRuleAnnotationName;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ValuePopoverImplTest {

    @Mock
    private ValuePopoverView view;

    @Mock
    private TranslationService translationService;

    private ValuePopoverImpl popover;

    @Before
    public void setup() {
        popover = spy(new ValuePopoverImpl(view, translationService));
    }

    @Test
    public void testShow() {

        final ArgumentCaptor<Optional> captor = ArgumentCaptor.forClass(Optional.class);
        final HasValueSelectorControl hasValueSelectorControl = mock(HasValueSelectorControl.class);
        final Optional<HasValueSelectorControl> binding = Optional.of(hasValueSelectorControl);
        final String value = "value";
        final String title = "title";

        when(hasValueSelectorControl.getValue()).thenReturn(value);
        doReturn(binding).when(popover).getBinding();
        doReturn(title).when(popover).getPopoverTitle();

        popover.show();

        verify(view).setValue(value);
        verify(view).show(captor.capture());

        final Optional captured = captor.getValue();
        assertEquals(title, captured.get());
    }

    @Test
    public void testHide() {

        popover.hide();
        verify(popover).refresh();
        verify(view).hide();
    }

    @Test
    public void testRefresh() {

        final HasValueSelectorControl hasValueSelectorControl = mock(HasValueSelectorControl.class);
        final Optional<HasValueSelectorControl> binding = Optional.of(hasValueSelectorControl);
        final String value = "value";

        doReturn(binding).when(popover).getBinding();
        when(view.getValue()).thenReturn(value);

        popover.refresh();

        verify(hasValueSelectorControl).setValue(value);
    }

    @Test
    public void testSetValue() {

        final HasValueSelectorControl hasValueSelectorControl = mock(HasValueSelectorControl.class);
        final Optional<HasValueSelectorControl> binding = Optional.of(hasValueSelectorControl);
        final String value = "value";

        doReturn(binding).when(popover).getBinding();

        popover.setValue(value);

        verify(hasValueSelectorControl).setValue(value);
    }

    @Test
    public void testGetPopoverTitle() {

        final String title = "title";

        when(translationService.getTranslation(DecisionTableEditor_SelectRuleAnnotationName)).thenReturn(title);

        final String actualTitle = popover.getPopoverTitle();

        assertEquals(title, actualTitle);
    }

    @Test
    public void testBind() {

        final HasValueSelectorControl hasValueSelectorControl = mock(HasValueSelectorControl.class);
        popover.bind(hasValueSelectorControl, 0, 0);

        final Optional<HasValueSelectorControl> bind = popover.getBinding();

        assertEquals(hasValueSelectorControl, bind.get());
    }

    @Test
    public void testGetElement() {

        final HTMLElement element = mock(HTMLElement.class);
        when(view.getElement()).thenReturn(element);

        final HTMLElement actual = popover.getElement();

        assertEquals(element, actual);
    }

    @Test
    public void testSetOnClosedByKeyboardCallback() {

        final Consumer consumer = mock(Consumer.class);

        popover.setOnClosedByKeyboardCallback(consumer);

        verify(view).setOnClosedByKeyboardCallback(consumer);
    }
}