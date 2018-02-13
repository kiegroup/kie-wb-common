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

package org.kie.workbench.common.forms.processing.engine.handling.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.processing.engine.handling.FormHandler;
import org.kie.workbench.common.forms.processing.engine.handling.impl.test.TestDisabledFormHandlerRegistryImpl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DisabledFormHandlerRegistryImplTest {

    private TestDisabledFormHandlerRegistryImpl registry = new TestDisabledFormHandlerRegistryImpl();

    @Test
    public void testFunctionality() {

        FormHandler rootHandler = getHandler();

        registry.startBatch(rootHandler);

        assertTrue(registry.isBatchActive());

        FormHandler nestedHandler = getHandler();

        registry.addToActiveBatch(nestedHandler);

        FormHandler secondNestedHandler = getHandler();

        registry.addToActiveBatch(secondNestedHandler);

        assertTrue(registry.isBatchActive());

        Assertions.assertThat(registry.getActiveBatches())
                .isNotEmpty()
                .hasSize(1);

        registry.finishBatch();

        assertFalse(registry.isBatchActive());

        // Getting the batch and executing to avoid adding a timer
        DisabledFormHandlerRegistryImpl.FormHandlerBatch batch = registry.getActiveBatches().iterator().next();

        assertSame(rootHandler, batch.getRootHandler());
        Assertions.assertThat(batch.getNestedHandlers())
                .isNotEmpty()
                .hasSize(2)
                .containsExactly(nestedHandler, secondNestedHandler);

        batch.clear();

        verify(secondNestedHandler).clear();
        verify(nestedHandler).clear();
        verify(rootHandler).clear();

        Assertions.assertThat(batch.getNestedHandlers())
                .isEmpty();

    }

    @Test
    public void testDestroy() {

        FormHandler firstHandler = getHandler();
        registry.startBatch(firstHandler);
        registry.finishBatch();

        assertFalse(registry.isBatchActive());

        FormHandler secondHandler = getHandler();
        registry.startBatch(secondHandler);
        registry.finishBatch();

        assertFalse(registry.isBatchActive());

        List<DisabledFormHandlerRegistryImpl.FormHandlerBatch> batches = new ArrayList<>(registry.getActiveBatches());

        Assertions.assertThat(batches)
                .isNotEmpty()
                .hasSize(2);

        registry.destroy();

        verify(firstHandler, times(1)).clear();
        verify(secondHandler, times(1)).clear();

        Assertions.assertThat(registry.getActiveBatches())
                .isEmpty();


        // checking if after destroying the FormHandlers inside batches can be cleared (they shouldn't)
        batches.forEach(DisabledFormHandlerRegistryImpl.FormHandlerBatch::clear);

        verify(firstHandler, times(1)).clear();
        verify(secondHandler, times(1)).clear();
    }

    @Test
    public void testExceptions() {

        Assertions.assertThatThrownBy(() -> registry.addToActiveBatch(getHandler()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot add handler: there's no active batch!");

        Assertions.assertThatThrownBy(() -> registry.finishBatch())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot end batch: there's no active batch!");

    }

    protected FormHandler getHandler() {
        return mock(FormHandler.class);
    }
}
