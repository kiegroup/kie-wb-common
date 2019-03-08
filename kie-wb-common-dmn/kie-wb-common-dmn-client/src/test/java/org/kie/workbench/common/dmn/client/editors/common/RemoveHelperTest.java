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

package org.kie.workbench.common.dmn.client.editors.common;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Element;
import elemental2.dom.Node;
import elemental2.dom.NodeList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class RemoveHelperTest {

    @Mock
    private Element element;

    @Test
    public void testRemoveChildren() {

        final Element children1 = mock(Element.class);
        final Element children2 = mock(Element.class);
        final NodeList<Node> list = spy(new NodeList<>());
        list.length = 2;
        doReturn(children1).when(list).getAt(0);
        doReturn(children2).when(list).getAt(1);

        children1.parentNode = element;
        children2.parentNode = element;

        element.childNodes = list;

        RemoveHelper.removeChildren(element);

        verify(element).removeChild(children1);
        verify(element).removeChild(children2);
    }
}