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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.field;

import com.google.gwtmockito.GwtMockito;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.stunner.bpmn.forms.model.ImportsFieldDefinition;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ImportsFieldRendererTest {

    @Mock
    private ImportsFieldDefinition field1;

    @Mock
    private DefaultFormGroup formGroup;

    @Mock
    protected ManagedInstance<DefaultFormGroup> formGroupsInstance;

    private ImportsField view;

    @InjectMocks
    @Spy
    private ImportsFieldRenderer tested;

    @Before
    public void setUp() {
        GwtMockito.initMocks(this);

        view = mock(ImportsField.class);
        when(tested.getWidget()).thenReturn(view);
        when(formGroupsInstance.get()).thenReturn(formGroup);

        doCallRealMethod().when(tested).setReadOnly(anyBoolean());
    }

    @Test
    public void constructor() {
        ImportsField expected = new ImportsField();
        ImportsFieldRenderer importsFieldRenderer = new ImportsFieldRenderer(expected);
        ImportsField result = importsFieldRenderer.widget;
        assertEquals(expected, result);
    }

    @Test
    public void getFormGroup() {
        FormGroup expected = formGroup;
        FormGroup result = tested.getFormGroup(RenderMode.EDIT_MODE);
        verify(formGroupsInstance).get();
        verify(formGroup).render(eq(view), eq(field1));
        assertEquals(expected, result);
    }

    @Test
    public void getName() {
        String expected = ImportsFieldDefinition.FIELD_TYPE.getTypeName();
        String result = tested.getName();
        assertEquals(expected, result);
    }

    @Test
    public void setReadOnly() {
        tested.setReadOnly(true);
        verify(view, times(1)).setReadOnly(true);
    }
}