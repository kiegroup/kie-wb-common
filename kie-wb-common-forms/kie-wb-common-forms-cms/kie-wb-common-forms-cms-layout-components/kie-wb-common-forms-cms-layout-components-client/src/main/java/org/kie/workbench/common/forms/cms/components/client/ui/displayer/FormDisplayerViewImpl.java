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

package org.kie.workbench.common.forms.cms.components.client.ui.displayer;

import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class FormDisplayerViewImpl implements IsElement,
                                              FormDisplayerView {

    @Inject
    @DataField("container")
    private Div container;

    @Inject
    @DataField("submit-button")
    private Button submit;

    @Inject
    @DataField("cancel-button")
    private Button cancel;

    private Presenter presenter;

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;

        DOMUtil.appendWidgetToElement(container, presenter.getRenderer());
    }

    @EventHandler("submit-button")
    public void onSubmit(@ForEvent("click") Event event) {
        presenter.onSubmit();
    }

    @EventHandler("cancel-button")
    public void onCancel(@ForEvent("click") Event event) {
        presenter.onCancel();
    }
}
