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

package org.kie.workbench.common.dmn.client.editors.included.imports.messages;

import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModel;

import static org.kie.workbench.common.dmn.client.editors.common.cards.frame.CardFrameComponentView.CARD_UUID_ATTR;
import static org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage.Type.ERROR;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.IncludedModelNameIsNotUniqueErrorMessage_RegularMessage;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.IncludedModelNameIsNotUniqueErrorMessage_StrongMessage;

public class IncludedModelErrorMessageFactory {

    private final TranslationService translationService;

    @Inject
    public IncludedModelErrorMessageFactory(final TranslationService translationService) {
        this.translationService = translationService;
    }

    public FlashMessage getNameIsNotUniqueFlashMessage(final IncludedModel includedModel) {
        return new FlashMessage(ERROR, getStrongMessage(includedModel), getRegularMessage(), getErrorElementSelector(includedModel));
    }

    private String getErrorElementSelector(final IncludedModel includedModel) {
        return "[" + CARD_UUID_ATTR + "=\"" + includedModel.getUUID() + "\"] [data-field=\"title-input\"]";
    }

    private String getStrongMessage(final IncludedModel includedModel) {
        return translationService.format(IncludedModelNameIsNotUniqueErrorMessage_StrongMessage, includedModel.getName());
    }

    private String getRegularMessage() {
        return translationService.format(IncludedModelNameIsNotUniqueErrorMessage_RegularMessage);
    }
}
