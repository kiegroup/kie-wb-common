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

package org.kie.workbench.common.stunner.cm.client.shape.view;

import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.wires.ILayoutHandler;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitiveShape;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementShapeViewTest {

    @Mock
    private ILayoutHandler layoutHandler;

    private CaseManagementShapeView shape;

    private CaseManagementShapeView createShapeView(String name) {
        return new CaseManagementShapeView(name,
                                           new SVGPrimitiveShape(new Rectangle(0d, 0d)),
                                           0d,
                                           0d,
                                           false);
    }

    @Before
    public void setup() {
        this.shape = createShapeView("shape");
        this.shape.setLayoutHandler(layoutHandler);
    }

    @Test
    public void checkLogicalReplacementWithOneChild() {
        final CaseManagementShapeView child = createShapeView("child");
        final CaseManagementShapeView replacement = createShapeView("replacement");

        shape.add(child);

        verify(layoutHandler,
               times(1)).requestLayout(shape);

        assertEquals(1,
                     shape.getChildShapes().size());
        assertEquals(child,
                     shape.getChildShapes().get(0));
        assertEquals(shape,
                     child.getParent());

        shape.logicallyReplace(child,
                               replacement);

        assertEquals(1,
                     shape.getChildShapes().size());
        assertEquals(replacement,
                     shape.getChildShapes().get(0));
        assertEquals(shape,
                     replacement.getParent());
        assertNull(child.getParent());

        verify(layoutHandler,
               times(2)).requestLayout(shape);
    }

    @Test
    public void checkLogicalReplacementWithMultipleChildren() {
        final CaseManagementShapeView child1 = createShapeView("child1");
        final CaseManagementShapeView child2 = createShapeView("child2");
        final CaseManagementShapeView replacement = createShapeView("replacement");

        shape.add(child1);
        shape.add(child2);

        verify(layoutHandler,
               times(2)).requestLayout(shape);

        assertEquals(2,
                     shape.getChildShapes().size());
        assertEquals(child1,
                     shape.getChildShapes().get(0));
        assertEquals(child2,
                     shape.getChildShapes().get(1));
        assertEquals(shape,
                     child1.getParent());
        assertEquals(shape,
                     child2.getParent());

        shape.logicallyReplace(child1,
                               replacement);

        assertEquals(2,
                     shape.getChildShapes().size());
        assertEquals(replacement,
                     shape.getChildShapes().get(0));
        assertEquals(child2,
                     shape.getChildShapes().get(1));
        assertEquals(shape,
                     replacement.getParent());
        assertNull(child1.getParent());

        verify(layoutHandler,
               times(3)).requestLayout(shape);
    }

    @Test
    public void checkAddShapeAtIndex0WithNoExistingChildren() {
        final CaseManagementShapeView child = createShapeView("child");

        shape.addShape(child,
                       0);

        verify(layoutHandler,
               times(1)).requestLayout(shape);

        assertEquals(1,
                     shape.getChildShapes().size());
        assertEquals(child,
                     shape.getChildShapes().get(0));
    }

    @Test
    public void checkAddShapeAtIndex1WithNoExistingChildren() {
        final CaseManagementShapeView child = createShapeView("child");

        shape.addShape(child,
                       1);

        verify(layoutHandler,
               never()).requestLayout(shape);

        assertEquals(0,
                     shape.getChildShapes().size());
    }

    @Test
    public void checkAddShapeAtNegativeIndexWithNoExistingChildren() {
        final CaseManagementShapeView child = createShapeView("child");

        shape.addShape(child,
                       -1);

        verify(layoutHandler,
               never()).requestLayout(shape);

        assertEquals(0,
                     shape.getChildShapes().size());
    }
}
