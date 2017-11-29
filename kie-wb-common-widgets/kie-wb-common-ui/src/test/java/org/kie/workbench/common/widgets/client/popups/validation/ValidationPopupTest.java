/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.client.popups.validation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.guvnor.common.services.shared.builder.model.BuildMessage;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ValidationPopupTest {

    @Mock
    private ValidationPopupView view;

    @Mock
    private ValidationMessageTranslatorUtils validationMessageTranslatorUtils;

    @Mock
    private TranslationService translationService;

    private ValidationPopup validationPopup;

    @Before
    public void setUp() {
        validationPopup = new ValidationPopup(view,
                                              validationMessageTranslatorUtils,
                                              translationService);

        view.init(validationPopup);
    }

    @Test
    public void showMessages() {
        BuildMessage validationMessage = new BuildMessage();

        validationPopup.showMessages(Arrays.asList(validationMessage));

        verify(view).showYesButton(false);
        verify(view).showCancelButton(true);

        verify(view).setValidationMessages(anyListOf(BuildMessage.class));
        verify(view).show();
    }

    @Test
    public void showCopyValidationMessages() {
        List<BuildMessage> validationMessages = Arrays.asList(new BuildMessage());

        validationPopup.showCopyValidationMessages(() -> {
                                                   },
                                                   () -> {
                                                   },
                                                   validationMessages);

        verify(view).showYesButton(true);
        verify(view).showCancelButton(true);

        List<BuildMessage> translatedMessages = Collections.emptyList();
        when(validationMessageTranslatorUtils.translate(validationMessages)).thenReturn(translatedMessages);

        verify(view).setValidationMessages(translatedMessages);
        verify(view).show();
    }

    @Test
    public void showSaveValidationMessages() {
        List<BuildMessage> validationMessages = Arrays.asList(new BuildMessage());

        validationPopup.showSaveValidationMessages(() -> {
                                                   },
                                                   () -> {
                                                   },
                                                   validationMessages);

        verify(view).showYesButton(true);
        verify(view).showCancelButton(true);

        List<BuildMessage> translatedMessages = Collections.emptyList();
        when(validationMessageTranslatorUtils.translate(validationMessages)).thenReturn(translatedMessages);

        verify(view).setValidationMessages(translatedMessages);
        verify(view).show();
    }

    @Test
    public void showTranslatedMessages() {
        List<BuildMessage> validationMessages = Arrays.asList(new BuildMessage());

        validationPopup.showTranslatedMessages(validationMessages);

        List<BuildMessage> translatedMessages = Collections.emptyList();
        when(validationMessageTranslatorUtils.translate(validationMessages)).thenReturn(translatedMessages);

        verify(view).setValidationMessages(translatedMessages);
        verify(view).show();
    }
}