/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.cms.components.client.ui.wizard.ui;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.cms.components.client.resources.i18n.CMSComponentsConstants;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;

@Templated
public class WizardFormViewImpl implements IsElement,
                                           WizardFormView {

    @Inject
    @DataField("header-step")
    @Named("strong")
    private HTMLElement step;

    @Inject
    @DataField("header-title")
    private Span title;

    @Inject
    @DataField("formContainer")
    private Div formContainer;

    @Inject
    @DataField("previous-button")
    private Button previous;

    @Inject
    @DataField("next-button")
    private Button next;

    @Inject
    @DataField("cancel-button")
    private Button cancel;

    @Inject
    @DataField("submit-button")
    private Button submit;

    private Presenter presenter;

    private TranslationService translationService;

    @Inject
    public WizardFormViewImpl(TranslationService translationService) {
        this.translationService = translationService;
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void renderStep(int index,
                           String title,
                           DynamicFormRenderer renderer) {

        this.step.setTextContent(translationService.format(CMSComponentsConstants.WizardFormViewImplStep,
                                                           index));
        this.title.setTextContent(title);
        DOMUtil.removeAllChildren(formContainer);
        DOMUtil.appendWidgetToElement(formContainer,
                                      renderer);

        previous.setDisabled(presenter.isFirst());
        next.setDisabled(presenter.isLast());
        submit.setDisabled(!presenter.isLast());
    }

    @Override
    public void clear() {
        this.step.setTextContent("");
        this.title.setTextContent("");
        DOMUtil.removeAllChildren(formContainer);
    }

    @EventHandler("previous-button")
    public void onPrevious(@ForEvent("click") Event event) {
        presenter.previousStep();
    }

    @EventHandler("next-button")
    public void onNext(@ForEvent("click") Event event) {
        presenter.nextStep();
    }

    @EventHandler("submit-button")
    public void onSubmit(@ForEvent("click") Event event) {
        presenter.finish();
    }

    @EventHandler("cancel-button")
    public void onCancel(@ForEvent("click") Event event) {
        presenter.cancel();
    }
}
