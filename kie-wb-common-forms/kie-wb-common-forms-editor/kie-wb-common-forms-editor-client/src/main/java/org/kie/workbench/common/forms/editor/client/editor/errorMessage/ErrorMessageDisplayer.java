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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.editor.client.resources.i18n.FormEditorConstants;

@Dependent
public class ErrorMessageDisplayer implements ErrorMessageDisplayerView.Presenter,
                                              IsElement {

    private TranslationService translationService;

    private ErrorMessageDisplayerView view;

    private boolean showMoreEnabled = false;
    private boolean showMore = false;

    private String shortMessage;

    private String fullMessage;

    @Inject
    public ErrorMessageDisplayer(TranslationService translationService, ErrorMessageDisplayerView view) {
        this.translationService = translationService;
        this.view = view;
    }

    @PostConstruct
    void init() {
        view.init(this);
        view.hide();
    }

    public void show(String message) {
        show(message, null);
    }

    public void show(String shortMessage, String fullMessage) {
        this.shortMessage = shortMessage;
        this.fullMessage = fullMessage;

        showMoreEnabled = fullMessage != null;

        view.displayShowMoreAnchor(showMoreEnabled);

        if(showMoreEnabled) {
            showMore = false;
            view.setShowMoreLabel(translationService.getTranslation(FormEditorConstants.ShowMoreLabel));
        }

        view.setMessage(shortMessage);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @Override
    public void hide() {
        view.hide();
    }

    @Override
    public void notifyShowMorePressed() {
        if(showMoreEnabled) {
            if(showMore) {
                view.setMessage(shortMessage);
                view.setShowMoreLabel(translationService.getTranslation(FormEditorConstants.ShowMoreLabel));
            } else {
                view.setMessage(fullMessage);
                view.setShowMoreLabel(translationService.getTranslation(FormEditorConstants.ShowLessLabel));
            }
            showMore = !showMore;
        }
    }
}
