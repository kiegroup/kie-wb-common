/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.validation;

import org.guvnor.common.services.shared.builder.model.BuildMessage;
import org.guvnor.common.services.shared.message.Level;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.validation.PersistenceDescriptorValidationMessage;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PersistenceDescriptorValidationMessageTranslatorTest {

    private static final String ORIGINAL_MESSAGE = "original message";

    private static final String TRANSLATED_MESSAGE = "translated message";

    private static final String FORMATTED_MESSAGE = "formatted message";

    @Mock
    private TranslationService translationService;

    private PersistenceDescriptorValidationMessageTranslator translator;

    private PersistenceDescriptorValidationMessage originalMessage;

    @Before
    public void setUp() {
        translator = new PersistenceDescriptorValidationMessageTranslator(translationService);
        originalMessage = new PersistenceDescriptorValidationMessage(12345, Level.INFO, ORIGINAL_MESSAGE);
    }

    @Test
    public void testAcceptMessage() {
        assertTrue(translator.accept(originalMessage));
        assertFalse(translator.accept(new BuildMessage()));
    }

    @Test
    public void testSimpleTranslation() {
        String translationKey = PersistenceDescriptorValidationMessageTranslator.PREFIX + originalMessage.getId();
        when(translationService.getTranslation(translationKey)).thenReturn(TRANSLATED_MESSAGE);

        BuildMessage expectedTranslatedMessage = new BuildMessage();
        expectedTranslatedMessage.setId(originalMessage.getId());
        expectedTranslatedMessage.setLevel(originalMessage.getLevel());
        expectedTranslatedMessage.setText(TRANSLATED_MESSAGE);

        assertEquals(expectedTranslatedMessage, translator.translate(originalMessage));
    }

    @Test
    public void testParametrizedTranslation() {
        String translationKey = PersistenceDescriptorValidationMessageTranslator.PREFIX + originalMessage.getId();
        originalMessage.getParams().add("param1");
        originalMessage.getParams().add("param2");

        when(translationService.getTranslation(translationKey)).thenReturn(TRANSLATED_MESSAGE);
        when(translationService.format(translationKey, originalMessage.getParams().toArray())).thenReturn(FORMATTED_MESSAGE);

        BuildMessage expectedTranslatedMessage = new BuildMessage();
        expectedTranslatedMessage.setId(originalMessage.getId());
        expectedTranslatedMessage.setLevel(originalMessage.getLevel());
        expectedTranslatedMessage.setText(FORMATTED_MESSAGE);

        assertEquals(expectedTranslatedMessage, translator.translate(originalMessage));
    }
}