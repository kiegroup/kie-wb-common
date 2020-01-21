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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.editors;

import java.util.List;

import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public abstract class ImportsEditorTest<T> {

    ImportsEditorView<T> view;

    protected ImportsEditor<T> tested;

    @Before
    public void setUp() {
        GwtMockito.initMocks(this);

        List<T> imports = mock(List.class);

        view = mockView();
        when(view.getImports()).thenReturn(imports);

        tested = spyEditor();
        tested.view = view;
    }

    @Test
    public void setView() {
        tested.setView(view);
        assertEquals(view, tested.view);
    }

    @Test
    public void addImport() {
        tested.addImport();
        verify(view.getImports()).add(any());
    }

    @Test
    public void removeImport() {
        T imp = mockImport();
        tested.removeImport(imp);
        verify(view.getImports()).remove(imp);
    }

    protected abstract T mockImport();

    protected abstract ImportsEditorView<T> mockView();

    protected abstract ImportsEditor<T> spyEditor();
}