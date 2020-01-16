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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.artifacts;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ReflectionUtilsTest;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(LienzoMockitoTestRunner.class)
public class DataObjectEditorFieldRendererTest extends ReflectionUtilsTest {

    @Mock
    private DataObjectTypeWidget dataObjectTypeWidget;

    @Mock
    private DefaultFormGroup formGroup;

    @Mock
    private ManagedInstance<DefaultFormGroup> managedInstance;

    private DataObjectEditorFieldRenderer dataObjectEditorFieldRenderer;

    @Before
    public void setUp() throws Exception {
        dataObjectEditorFieldRenderer = spy(new DataObjectEditorFieldRenderer(dataObjectTypeWidget));

        doCallRealMethod().when(dataObjectEditorFieldRenderer).getName();
        doCallRealMethod().when(dataObjectEditorFieldRenderer).getField();
        setFieldValue(dataObjectEditorFieldRenderer, "formGroupsInstance", managedInstance);
        when(managedInstance.get()).thenReturn(formGroup);
    }

    @Test
    public void getNameTest() {
        Assert.assertEquals("DataObjectEditor", dataObjectEditorFieldRenderer.getName());
    }

    @Test
    public void testSetReadOnly() {
        dataObjectEditorFieldRenderer.setReadOnly(true);
        verify(dataObjectTypeWidget, times(1)).setReadOnly(true);
    }

    @Test
    public void getFormGroup() {
        FormGroup formGroup = dataObjectEditorFieldRenderer.getFormGroup(RenderMode.EDIT_MODE);
        assertThat(formGroup).isInstanceOf(DefaultFormGroup.class);
    }
}
