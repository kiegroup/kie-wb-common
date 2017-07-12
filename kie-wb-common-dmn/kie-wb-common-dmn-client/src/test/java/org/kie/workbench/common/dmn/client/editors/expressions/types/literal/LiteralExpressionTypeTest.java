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

package org.kie.workbench.common.dmn.client.editors.expressions.types.literal;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class LiteralExpressionTypeTest {

    @Mock
    private LiteralExpressionEditor editor;

    private LiteralExpressionType type;

    @Before
    public void setup() {
        this.type = new LiteralExpressionType(editor);
    }

    @Test
    public void checkGetIndex() {
        assertEquals(1,
                     type.getIndex());
    }

    @Test
    public void checkGetName() {
        assertEquals(LiteralExpression.class.getSimpleName(),
                     type.getName());
    }

    @Test
    public void checkGetModelClass() {
        final Optional<LiteralExpression> oExpression = type.getModelClass();
        assertTrue(oExpression.isPresent());
        assertEquals(LiteralExpression.class,
                     oExpression.get().getClass());
    }

    @Test
    public void checkGetEditor() {
        assertEquals(editor,
                     type.getEditor());
    }
}
