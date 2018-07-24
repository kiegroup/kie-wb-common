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

package org.kie.workbench.common.forms.cms.components.client.ui.settings;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Modal;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.cms.components.client.resources.i18n.CMSComponentsConstants;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

@Dependent
public class SettingsDisplayerViewImpl implements SettingsDisplayerView {

    private DynamicFormRenderer formRenderer;

    private TranslationService translationService;

    private SettingsDisplayer presenter;

    private BaseModal modal;

    @Inject
    public SettingsDisplayerViewImpl(DynamicFormRenderer formRenderer,
                                     TranslationService translationService) {
        this.formRenderer = formRenderer;
        this.translationService = translationService;
    }

    @PostConstruct
    public void init() {
        modal = new BaseModal();
        modal.setTitle(translationService.getTranslation(CMSComponentsConstants.SettingsDisplayerViewImplTitle));
        modal.setClosable(false);
        modal.setBody(formRenderer.asWidget());
        modal.add(new ModalFooterOKCancelButtons(
                this::accept,
                this::cancel));
    }

    private void accept() {
        if (formRenderer.isValid()) {
            modal.hide();
            presenter.onAccept();
        }
    }

    private void cancel() {
        modal.hide();
        presenter.onCancel();
    }

    @Override
    public void init(SettingsDisplayer displayer) {
        this.presenter = displayer;
    }

    @Override
    public Modal getPropertiesModal() {

        formRenderer.renderDefaultForm(presenter.getSettings());

        return modal;
    }
}
