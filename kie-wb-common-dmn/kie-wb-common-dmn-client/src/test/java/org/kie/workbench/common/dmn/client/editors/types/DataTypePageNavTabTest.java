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

package org.kie.workbench.common.dmn.client.editors.types;

import elemental2.dom.Element;
import elemental2.dom.NodeList;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.JQueryNavTab;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypesPage_Label;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest({JQueryNavTab.class})
@RunWith(PowerMockRunner.class)
public class DataTypePageNavTabTest {

    private static final String LABEL = "Data Types";

    @Mock
    private TranslationService translationService;

    private DataTypePageNavTab pageNavTab;

    @Before
    public void setup() {
        pageNavTab = spy(new DataTypePageNavTab(translationService));

        when(translationService.format(DataTypesPage_Label)).thenReturn(LABEL);
    }

    @Test
    public void testActiveWhenTabExists() {

        final NodeList<Element> navTabItems = spy(new NodeList<>());
        final Element tabItem1 = mock(Element.class);
        final Element tabItem2 = mock(Element.class);
        final JQueryNavTab jQuery = PowerMockito.mock(JQueryNavTab.class);

        doReturn(navTabItems).when(pageNavTab).navTabItems();
        doReturn(tabItem1).when(navTabItems).getAt(0);
        doReturn(tabItem2).when(navTabItems).getAt(1);
        navTabItems.length = 2;
        tabItem1.textContent = "Model";
        tabItem2.textContent = LABEL;

        mockStatic(JQueryNavTab.class);
        PowerMockito.when(JQueryNavTab.$(tabItem2)).thenReturn(jQuery);

        pageNavTab.active();

        verify(jQuery).tab("show");
    }

    @Test
    public void testActiveWhenTabDoesNotExist() {

        final NodeList<Element> navTabItems = spy(new NodeList<>());
        final Element tabItem1 = mock(Element.class);
        final Element tabItem2 = mock(Element.class);
        final JQueryNavTab jQuery = PowerMockito.mock(JQueryNavTab.class);

        doReturn(navTabItems).when(pageNavTab).navTabItems();
        doReturn(tabItem1).when(navTabItems).getAt(0);
        doReturn(tabItem2).when(navTabItems).getAt(1);
        navTabItems.length = 2;
        tabItem1.textContent = "Model";
        tabItem2.textContent = "Overview";

        mockStatic(JQueryNavTab.class);
        PowerMockito.when(JQueryNavTab.$(tabItem2)).thenReturn(jQuery);

        assertThatThrownBy(pageNavTab::active)
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("A nav tab with the label 'Data Types' could not be found.");
    }
}
