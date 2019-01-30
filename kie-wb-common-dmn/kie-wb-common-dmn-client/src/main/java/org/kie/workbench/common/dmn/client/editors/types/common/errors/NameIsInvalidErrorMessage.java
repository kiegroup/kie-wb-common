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

package org.kie.workbench.common.dmn.client.editors.types.common.errors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;

import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.NameIsInvalidErrorMessage_RegularMessage;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.NameIsInvalidErrorMessage_StrongMessage;

@Dependent
public class NameIsInvalidErrorMessage extends ErrorMessage {

    @Inject
    public NameIsInvalidErrorMessage(final TranslationService translationService) {
        super(translationService);
    }

    @Override
    String getStrongMessage(final DataType dataType) {
        return translationService.format(NameIsInvalidErrorMessage_StrongMessage);
    }

    @Override
    String getRegularMessage() {
        return translationService.format(NameIsInvalidErrorMessage_RegularMessage);
    }
}
