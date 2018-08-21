/*
 *
 *  * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.kie.workbench.common.screens.library.client.settings.util.modal.single;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class AddSingleValueModalTest {

    @Mock
    AddSingleValueModal.View view;

    @Mock
    TranslationService translationService;

    private AddSingleValueModal addSingleValueModal;

    @Before
    public void before() {
        addSingleValueModal = spy(new AddSingleValueModal(view,
                                                          translationService));
        doReturn(view).when(addSingleValueModal).getView();
        doNothing().when(addSingleValueModal).show();
        doNothing().when(addSingleValueModal).setup();
    }

    @Test
    public void setupTest() {
        addSingleValueModal.setup(any(), any());

        verify(view).setHeader(any());
        verify(view).setLabel(any());
    }

    @Test
    public void showTest() {
        addSingleValueModal.show(null);

        verify(view).clearForm();
        verify(view).focus();
    }

    @Test
    public void showTestWithParam() {
        addSingleValueModal.show(null, "value");

        verify(view).clearForm();
        verify(view).focus();
        verify(view).setValue("value");
    }
}
