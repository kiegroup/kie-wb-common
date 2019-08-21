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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.slaEditor;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.FieldEditorPresenter;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.FieldEditorPresenterBaseTest;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SLASettingsFieldEditorPresenterTest
        extends FieldEditorPresenterBaseTest<String, SLASettingsFieldEditorPresenter, SLASettingsFieldEditorPresenter.View> {

    private static final String VALUE_1 = "VALUE_1";

    @Before
    public void setUp() {
        super.setUp();
    }

    @Override
    public ArgumentCaptor<String> newArgumentCaptor() {
        return ArgumentCaptor.forClass(String.class);
    }

    @Override
    public SLASettingsFieldEditorPresenter.View mockEditorView() {
        return mock(SLASettingsFieldEditorPresenter.View.class);
    }

    @Override
    public SLASettingsFieldEditorPresenter newEditorPresenter(SLASettingsFieldEditorPresenter.View view) {
        return new SLASettingsFieldEditorPresenter(view);
    }

    @SuppressWarnings("unchecked")
    @Override
    public FieldEditorPresenter.ValueChangeHandler<String> mockChangeHandler() {
        return mock(FieldEditorPresenter.ValueChangeHandler.class);
    }

    @Test
    public void testOnTimerDurationChange() {
        String value = "";
        editor.setValue(value);
        when(view.getTimeDuration()).thenReturn("P6D");
        when(view.isValid()).thenReturn(true);

        editor.onTimerDurationChange();
        verify(changeHandler,
               times(1)).onValueChange(oldValueCaptor.capture(),
                                       newValueCaptor.capture());
        assertEquals(value, oldValueCaptor.getValue());
        assertEquals("P6D", newValueCaptor.getValue());
    }

    @Test
    public void testSetReadonlyTrue() {
        editor.setReadOnly(true);
        verify(view, times(1)).setReadOnly(true);
    }

    @Test
    public void testSetReadonlyFalse() {
        editor.setReadOnly(false);
        verify(view, times(1)).setReadOnly(false);
    }
}
