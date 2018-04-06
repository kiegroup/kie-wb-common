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

package org.kie.workbench.common.forms.editor.client.editor.errorMessage;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.editor.client.resources.i18n.FormEditorConstants;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ErrorMessageDisplayerTest {

    private static final String MESSAGE = "message";
    private static final String FULL_MESSAGE = "full message";

    @Mock
    private TranslationService translationService;

    @Mock
    private ErrorMessageDisplayerView view;

    private ErrorMessageDisplayer displayer;

    @Before
    public void init() {
        displayer = new ErrorMessageDisplayer(translationService, view);
        displayer.init();
    }

    @Test
    public void simpleMessageTest() {
        verify(view).init(displayer);
        verify(view, times(1)).hide();

        displayer.getElement();

        verify(view).getElement();

        displayer.show(MESSAGE);
        verify(view).displayShowMoreAnchor(false);
        verify(view, never()).setShowMoreLabel(any());

        verify(view).setMessage(MESSAGE);

        displayer.notifyShowMorePressed();
        verify(view, times(1)).setMessage(any());
        verify(translationService, never()).getTranslation(FormEditorConstants.ShowLessLabel);
        verify(view, never()).setShowMoreLabel(any());

        displayer.notifyShowMorePressed();
        verify(view, times(1)).setMessage(any());
        verify(translationService, never()).getTranslation(FormEditorConstants.ShowMoreLabel);
        verify(view, never()).setShowMoreLabel(any());

        displayer.hide();
        verify(view, times(2)).hide();
    }

    @Test
    public void fullMessageTest() {
        verify(view).init(displayer);
        verify(view, times(1)).hide();

        displayer.getElement();

        verify(view).getElement();

        displayer.show(MESSAGE, FULL_MESSAGE);
        verify(view).displayShowMoreAnchor(true);

        verify(translationService, times(1)).getTranslation(FormEditorConstants.ShowMoreLabel);
        verify(view, times(1)).setShowMoreLabel(any());

        verify(view).setMessage(MESSAGE);

        displayer.notifyShowMorePressed();
        verify(view, times(1)).setMessage(FULL_MESSAGE);
        verify(translationService, times(1)).getTranslation(FormEditorConstants.ShowLessLabel);
        verify(view, times(2)).setShowMoreLabel(any());

        displayer.notifyShowMorePressed();
        verify(view, times(2)).setMessage(MESSAGE);
        verify(translationService, times(2)).getTranslation(FormEditorConstants.ShowMoreLabel);
        verify(view, times(3)).setShowMoreLabel(any());

        displayer.notifyShowMorePressed();
        verify(view, times(2)).setMessage(FULL_MESSAGE);
        verify(translationService, times(2)).getTranslation(FormEditorConstants.ShowLessLabel);
        verify(view, times(4)).setShowMoreLabel(any());

        displayer.hide();
        verify(view, times(2)).hide();
    }
}
