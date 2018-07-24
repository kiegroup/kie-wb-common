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

package org.kie.workbench.common.forms.cms.components.client.ui.report.preview;

import javax.enterprise.context.Dependent;

import org.gwtbootstrap3.client.ui.ModalSize;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKButton;

@Dependent
public class ObjectPreviewViewImpl implements ObjectPreviewView {

    private BaseModal modal;

    private Presenter presenter;

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;

        modal = new BaseModal();
        modal.setSize(ModalSize.LARGE);

        modal.setTitle(presenter.getTitle());

        modal.setBody(presenter.getRenderer().asWidget());

        modal.add(new ModalFooterOKButton(() -> presenter.getAcceptCommand().execute()));
    }

    @Override
    public void show() {
        modal.show();
    }

    @Override
    public void hide() {
        modal.hide();
    }
}
