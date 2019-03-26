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

package org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext;

import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.wires.layout.direction.DirectionLayout;
import com.ait.lienzo.client.core.shape.wires.layout.label.LabelLayout;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

public abstract class AbstractWiresShapeViewText {

    protected Text text;

    protected LabelLayout layout;

    @Mock
    protected WiresTextDecorator textDecorator;

    public void setUp() {
        this.text = new Text("test");
        this.layout = new LabelLayout.Builder()
                .horizontalAlignment(DirectionLayout.HorizontalAlignment.CENTER)
                .verticalAlignment(DirectionLayout.VerticalAlignment.MIDDLE)
                .referencePosition(DirectionLayout.ReferencePosition.INSIDE)
                .build();
        when(textDecorator.getView()).thenReturn(text);
        when(textDecorator.getView()).thenReturn(text);
        when(textDecorator.getLabelLayout()).thenReturn(layout);
    }
}
