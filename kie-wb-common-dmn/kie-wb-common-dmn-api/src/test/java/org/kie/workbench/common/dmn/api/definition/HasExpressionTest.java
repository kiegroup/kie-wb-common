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
package org.kie.workbench.common.dmn.api.definition;

import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class HasExpressionTest {

    @Test
    public void testNOP() {
        final HasExpression hasExpression = HasExpression.NOP;

        assertNull(hasExpression.getExpression());
        assertNull(hasExpression.asDMNModelInstrumentedBase());

        final Context context = new Context();
        hasExpression.setExpression(context);

        assertNull(hasExpression.getExpression());
        assertNull(hasExpression.asDMNModelInstrumentedBase());
    }

    @Test
    public void testWrapNull() {
        final HasExpression hasExpression = HasExpression.wrap(null);

        assertNull(hasExpression.getExpression());
        assertNull(hasExpression.asDMNModelInstrumentedBase());

        final Context context = new Context();
        hasExpression.setExpression(context);

        assertNotNull(hasExpression.getExpression());
        assertEquals(context, hasExpression.getExpression());
        assertEquals(context, hasExpression.asDMNModelInstrumentedBase());
    }

    @Test
    public void testWrapNonNull() {
        final LiteralExpression le = new LiteralExpression();
        final HasExpression hasExpression = HasExpression.wrap(le);

        assertNotNull(hasExpression.getExpression());
        assertEquals(le, hasExpression.getExpression());
        assertEquals(le, hasExpression.asDMNModelInstrumentedBase());

        final Context context = new Context();
        hasExpression.setExpression(context);

        assertNotNull(hasExpression.getExpression());
        assertEquals(context, hasExpression.getExpression());
        assertEquals(context, hasExpression.asDMNModelInstrumentedBase());
    }
}
