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

package org.kie.workbench.common.dmn.client.editors.common.cards.frame;

import com.google.gwt.dom.client.Style;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.common.cards.CardComponent;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class CardFrameComponentTest {

    @Mock
    private CardFrameComponent.View view;

    private CardFrameComponent cardFrame;

    @Before
    public void setup() {
        cardFrame = Mockito.spy(new CardFrameComponent(view));
    }

    @Test
    public void testSetup() {
        cardFrame.setup();
        verify(view).init(cardFrame);
    }

    @Test
    public void testInitialize() {

        final CardComponent card = mock(CardComponent.class);
        doNothing().when(cardFrame).refreshView();

        cardFrame.initialize(card);

        assertEquals(card, cardFrame.getCard());
        verify(cardFrame).refreshView();
    }

    @Test
    public void testGetElement() {

        final HTMLElement expectedElement = mock(HTMLElement.class);
        when(view.getElement()).thenReturn(expectedElement);

        final HTMLElement actualElement = cardFrame.getElement();

        assertEquals(expectedElement, actualElement);
    }

    @Test
    public void testRefreshView() {

        final CardComponent card = mock(CardComponent.class);
        final Style.HasCssName icon = mock(Style.HasCssName.class);
        final HTMLElement content = mock(HTMLElement.class);
        final String uuid = "uuid";
        final String title = "title";
        final String iconCSSClass = "icon-css-class";

        doReturn(card).when(cardFrame).getCard();
        when(icon.getCssName()).thenReturn(iconCSSClass);
        when(card.getUUID()).thenReturn(uuid);
        when(card.getIcon()).thenReturn(icon);
        when(card.getTitle()).thenReturn(title);
        when(card.getContent()).thenReturn(content);

        cardFrame.refreshView();

        verify(view).setUUID(uuid);
        verify(view).setIcon(iconCSSClass);
        verify(view).setTitle(title);
        verify(view).setContent(content);
        verify(view).enableReadOnlyMode();
    }

    @Test
    public void testChangeTitleWhenTitleIsSuccessfullyChanged() {

        final CardComponent card = mock(CardComponent.class);
        final String title = "title";

        doReturn(card).when(cardFrame).getCard();
        doNothing().when(cardFrame).refreshView();
        when(view.getTitle()).thenReturn(title);
        when(card.onTitleChanged(title)).thenReturn(true);

        cardFrame.changeTitle();

        verify(cardFrame).refreshView();
    }

    @Test
    public void testChangeTitleWhenTitleIsNotSuccessfullyChanged() {

        final CardComponent card = mock(CardComponent.class);
        final String title = "title";

        doReturn(card).when(cardFrame).getCard();
        when(view.getTitle()).thenReturn(title);
        when(card.onTitleChanged(title)).thenReturn(false);

        cardFrame.changeTitle();

        verify(cardFrame, never()).refreshView();
    }
}
